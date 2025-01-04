package com.kotlin.socialstore.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.database.AppDatabase
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.viewModels.ProfileViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun ManageHouseholdScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val usersDao = database.usersDao()
    val currentUserId = FirebaseObj.getCurrentUser()?.uid
    val firestore = FirebaseFirestore.getInstance()
    val familyHouseholdCollection = firestore.collection("familyHousehold")
    val coroutineScope = rememberCoroutineScope()

    var householdId by remember { mutableStateOf<String?>(null) }
    var isInitialized by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var emailInput by remember { mutableStateOf("") }
    var householdMembers by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

    // Função para carregar membros do household
    fun loadHouseholdMembers() {
        if (householdId != null) {
            coroutineScope.launch {
                try {

                    val householdRef =
                        FirebaseObj.getReferenceById("familyHousehold", householdId!!)

                    val membersQuery = FirebaseObj.getData(
                        collection = "users",
                        whereField = "familyHouseholdID",
                        whereEqualTo = householdRef
                    )

                    Log.d("ManageHouseholdScreen", "Resultado da query: $membersQuery")

                    if (!membersQuery.isNullOrEmpty()) {
                        val members = membersQuery.mapNotNull { userDocument ->
                            val userId = userDocument["id"] as? String
                            val userName = userDocument["name"] as? String ?: "Sem nome"
                            val profilePic = userDocument["profilePic"] as? String

                            if (userId != null) {
                                mapOf(
                                    "id" to userId,
                                    "name" to userName,
                                    "profilePic" to profilePic
                                )
                            } else {
                                null
                            }
                        }

                        householdMembers = members.map { member ->
                            member.mapValues { it.value as Any }
                        }

                        Log.d("ManageHouseholdScreen", "Membros carregados: $householdMembers")
                    } else {
                        Log.e(
                            "ManageHouseholdScreen",
                            "Nenhum membro encontrado para householdId $householdId"
                        )
                    }
                } catch (e: Exception) {
                    Log.e("ManageHouseholdScreen", "Erro ao carregar membros do household", e)
                }
            }
        }
    }

    LaunchedEffect(currentUserId) {
        if (currentUserId != null && !isInitialized) {
            isInitialized = true

            val user = usersDao.getById(currentUserId).firstOrNull()

            if (user?.familyHouseholdID.isNullOrEmpty()) {
                try {

                    val newHouseholdDocument = familyHouseholdCollection.document()
                    val newHouseholdData = mapOf(
                        "kidsStore" to false
                    )
                    newHouseholdDocument.set(newHouseholdData).await()

                    val newFamilyHouseholdID = FirebaseObj.getReferenceById(
                        DataConstants.FirebaseCollections.familyHousehold,
                        newHouseholdDocument.id
                    )


                    firestore.collection("users").document(currentUserId)
                        .update("familyHouseholdID", newFamilyHouseholdID).await()

                    // Atualizar FamilyHouseholdID na base local
                    val updatedUser = user?.copy(familyHouseholdID = newFamilyHouseholdID.id)
                    if (updatedUser != null) {
                        usersDao.deleteById(user.id)
                        usersDao.insert(updatedUser)
                    }

                    householdId = newFamilyHouseholdID.id
                    loadHouseholdMembers()
                } catch (e: Exception) {
                    Log.e("ManageHouseholdScreen", "Erro ao criar/atualizar FamilyHousehold", e)
                }
            } else {
                householdId = user?.familyHouseholdID
                loadHouseholdMembers()
            }
        }
    }

    fun addUserToHousehold(email: String) {
        coroutineScope.launch {
            try {
                // Buscar usuário pelo email
                val userQuery =
                    FirebaseObj.getData("users", whereField = "email", whereEqualTo = email)

                if (!userQuery.isNullOrEmpty()) {
                    val userDocument = userQuery.first()
                    val userId = userDocument["id"] as String

                    // Criar o caminho como uma referência Firestore
                    val householdReference =
                        firestore.collection("familyHousehold").document(householdId!!)

                    // Atualizar o documento do usuário para usar a referência
                    FirebaseObj.updateData(
                        "users", userId,
                        mapOf("familyHouseholdID" to householdReference)
                    )

                    // Recarregar os membros do household após a adição
                    loadHouseholdMembers()
                } else {
                    Log.e("ManageHouseholdScreen", "Usuário não encontrado.")
                }
            } catch (e: Exception) {
                Log.e("ManageHouseholdScreen", "Erro ao adicionar membro ao household", e)
            }
        }
    }


    fun removeMemberFromHousehold(memberId: String) {
        coroutineScope.launch {
            if (householdId != null) {
                try {

                    FirebaseObj.updateData(
                        "users", memberId,
                        mapOf("familyHouseholdID" to "")
                    )

                    householdMembers = householdMembers.filterNot { it["id"] == memberId }

                } catch (e: Exception) {
                    Log.e("ManageHouseholdScreen", "Erro ao remover membro do household", e)
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Manage Household",
            style = MaterialTheme.typography.headlineMedium
        )

        if (householdId == null && isInitialized) {
            CircularProgressIndicator()
        } else if (householdId != null) {
            Text(
                text = "Household ID: $householdId",
                style = MaterialTheme.typography.bodyLarge
            )

            val storage = Firebase.storage
            val storageReference = storage.reference

            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                if (householdMembers.isEmpty()) {
                    item {
                        Text(
                            text = "Nenhum membro encontrado.",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(householdMembers) { member ->
                        val memberName = member["name"] as? String ?: "Nome não disponível"
                        val photoPath = member["profilePic"] as? String
                        var expanded by remember { mutableStateOf(false) }
                        var imageUrl by remember { mutableStateOf<String?>(null) }

                        // Buscar o URL da imagem do Firebase Storage
                        LaunchedEffect(photoPath) {
                            if (!photoPath.isNullOrEmpty()) {
                                try {
                                    val url = storageReference.child(photoPath).downloadUrl.await()
                                    imageUrl = url.toString()
                                } catch (e: Exception) {
                                    Log.e(
                                        "ManageHouseholdScreen",
                                        "Erro ao carregar imagem: $photoPath",
                                        e
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SubcomposeAsyncImage(
                                model = imageUrl ?: R.drawable.product_image_not_found,
                                contentDescription = "Profile Picture",
                                loading = { CircularProgressIndicator() },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Nome do membro
                            Text(
                                text = memberName,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )

                            // Botão de opções (menu suspenso)
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(Icons.Filled.MoreVert, contentDescription = "Mais opções")
                            }

                            // Menu suspenso
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(onClick = {
                                    expanded = false
                                    removeMemberFromHousehold(member["id"] as String)
                                }) {
                                    Text("Apagar do Household")
                                }
                            }
                        }
                    }
                }
            }



            FloatingActionButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(16.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add User")
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Adicionar Usuário ao Household") },
                    text = {
                        Column {
                            TextField(
                                value = emailInput,
                                onValueChange = { emailInput = it },
                                label = { Text("Email do Usuário") }
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                addUserToHousehold(emailInput)
                                emailInput = ""
                                showDialog = false
                            }
                        ) {
                            Text("Adicionar")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { navController.popBackStack() }) {
                Text(text = "Back")
            }
        }
    }
}


