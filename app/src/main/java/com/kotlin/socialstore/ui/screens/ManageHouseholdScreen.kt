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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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
    modifier: Modifier = Modifier,
    profileViewModel: ProfileViewModel
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
            firestore.collection("familyHousehold").document(householdId!!)
                .get()
                .addOnSuccessListener { document ->
                    val members = document.get("members") as? List<Map<String, Any>> ?: emptyList()
                    householdMembers = members
                }
                .addOnFailureListener { e ->
                    Log.e("ManageHouseholdScreen", "Erro ao carregar membros do household", e)
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


                    val newFamilyHouseholdID = FirebaseObj.getReferenceById(DataConstants.FirebaseCollections.familyHousehold, newHouseholdDocument.id)


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
            val userQuery = FirebaseObj.getData("users", whereField = "email", whereEqualTo = email)
            if (!userQuery.isNullOrEmpty()) {
                val userDocument = userQuery.first()
                val userId = userDocument["id"] as String
                val userName = userDocument["name"] as? String ?: "Sem nome"
                val householdPath = "familyHousehold/${householdId!!}"
                val profilePic = userDocument["profilePic"] as? String

                    if (householdId != null) {
                    val newMember = mapOf(
                        "id" to userId,
                        "name" to userName,
                        "profilePic" to profilePic
                    )

                    try {
                        FirebaseObj.updateData(
                            "familyHousehold",
                            householdId!!,
                            mapOf("members" to FieldValue.arrayUnion(newMember))
                        )

                        FirebaseObj.updateData("users", userId, mapOf("familyHouseholdID" to householdPath))

                        loadHouseholdMembers()
                    } catch (e: Exception) {
                        Log.e("ManageHouseholdScreen", "Erro ao adicionar membro ao household", e)
                    }
                }
            } else {
                Log.e("ManageHouseholdScreen", "Usuário não encontrado.")
            }
        }
    }

    fun removeMemberFromHousehold(memberId: String) {
        coroutineScope.launch {
            if (householdId != null) {
                val memberToRemove = householdMembers.find { it["id"] == memberId }
                if (memberToRemove != null) {
                    try {
                        // Remover o membro do familyHousehold
                        FirebaseObj.updateData(
                            "familyHousehold", householdId!!,
                            mapOf("members" to FieldValue.arrayRemove(memberToRemove))
                        )
                        Log.d("ManageHouseholdScreen", "Membro removido com sucesso do household!")

                        // Atualizar o familyHouseholdID no documento do usuário
                        FirebaseObj.updateData(
                            "users", memberId,
                            mapOf("familyHouseholdID" to "")
                        )
                        Log.d("ManageHouseholdScreen", "familyHouseholdID removido do usuário $memberId")

                        // Atualizar lista local
                        householdMembers = householdMembers.filterNot { it["id"] == memberId }

                    } catch (e: Exception) {
                        Log.e("ManageHouseholdScreen", "Erro ao remover membro do household", e)
                    }
                } else {
                    Log.e("ManageHouseholdScreen", "Membro não encontrado para remoção.")
                }
            }
        }
    }



    Column(
        modifier = modifier
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

            LazyColumn(modifier = Modifier.fillMaxHeight().weight(1f)) {
                items(householdMembers) { member ->
                    val memberName = member["name"] as? String ?: "Nome não disponível"
                    val photoUrl = member["profilePic"] as? String ?: R.drawable.product_image_not_found.toString()
                    var expanded by remember { mutableStateOf(false) }

                    Row(modifier = Modifier.padding(vertical = 8.dp)) {
                        SubcomposeAsyncImage(
                            model = if (photoUrl == R.drawable.product_image_not_found.toString()) {
                                R.drawable.product_image_not_found
                            } else {
                                photoUrl
                            },
                            contentDescription = "Profile Picture",
                            loading = { CircularProgressIndicator() },
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(text = memberName, style = MaterialTheme.typography.bodyLarge)
                    }

                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Mais opções")
                    }
                    // Menu suspenso
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        // Opção de apagar membro do household
                        DropdownMenuItem(onClick = {
                            expanded = false // Fechar o menu antes da ação
                            removeMemberFromHousehold(member["id"] as String)
                        }) {
                            Text("Apagar do Household")
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
