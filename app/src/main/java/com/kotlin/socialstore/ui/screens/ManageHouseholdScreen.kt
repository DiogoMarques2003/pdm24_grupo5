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
    val userInfo by profileViewModel.userData.collectAsState(null)

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
                    // Criar um novo FamilyHousehold com um ID único
                    val newHouseholdDocument = familyHouseholdCollection.document()
                    val newHouseholdData = mapOf(
                        "kidsStore" to false,
                        "createdBy" to currentUserId,
                        "createdAt" to FieldValue.serverTimestamp()
                    )
                    newHouseholdDocument.set(newHouseholdData).await()

                    // Gerar o caminho completo do novo ID
                    val newFamilyHouseholdID = FirebaseObj.getReferenceById(DataConstants.FirebaseCollections.familyHousehold, newHouseholdDocument.id)

                    // Atualizar FamilyHouseholdID no Firestore
                    firestore.collection("users").document(currentUserId)
                        .update("familyHouseholdID", newFamilyHouseholdID).await()

                    // Atualizar FamilyHouseholdID na base local
                    val updatedUser = user?.copy(familyHouseholdID = newFamilyHouseholdID.id)
                    if (updatedUser != null) {
                        usersDao.deleteById(user.id)
                        usersDao.insert(updatedUser) // Atualiza na base local
                    }

                    householdId = newFamilyHouseholdID.id
                    loadHouseholdMembers() // Carregar membros do household
                } catch (e: Exception) {
                    Log.e("ManageHouseholdScreen", "Erro ao criar/atualizar FamilyHousehold", e)
                }
            } else {
                householdId = user?.familyHouseholdID // Apenas exibir o ID existente
                loadHouseholdMembers() // Carregar membros do household
            }
        }
    }

    fun addUserToHousehold(email: String) {
        // Verificar se o email é válido e o usuário existe
        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Log.e("ManageHouseholdScreen", "Usuário não encontrado.")
                } else {
                    val userDocument = querySnapshot.documents[0]
                    val userId = userDocument.id
                    val userName = userDocument.getString("name") ?: "Sem nome"
                    val userPhotoUrl = userDocument.getString("photoUrl")

                    // Adicionar o usuário ao household
                    if (householdId != null) {
                        val newMember = mapOf(
                            "id" to userId,
                            "name" to userName,
                            "photoUrl" to userPhotoUrl
                        )

                        firestore.collection("familyHousehold").document(householdId!!)
                            .update("members", FieldValue.arrayUnion(newMember))
                            .addOnSuccessListener {
                                val householdPath = "familyHousehold/${householdId!!}"
                                firestore.collection("users").document(userId)
                                    .update("familyHouseholdID", householdPath)
                                    .addOnSuccessListener {
                                        loadHouseholdMembers()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("ManageHouseholdScreen", "Erro ao atualizar householdID do usuário", e)
                                    }
                            }
                            .addOnFailureListener { e ->
                                Log.e("ManageHouseholdScreen", "Erro ao adicionar membro ao household", e)
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("ManageHouseholdScreen", "Erro ao consultar usuário", e)
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

            // Exibição dos membros do household
            LazyColumn(modifier = Modifier.fillMaxHeight().weight(1f)) {
                items(householdMembers) { member ->
                    val memberName = member["name"] as? String ?: "Nome não disponível"

                    Row(modifier = Modifier.padding(vertical = 8.dp)) {

                        val photoUrl = member["photoUrl"] as? String ?: R.drawable.product_image_not_found.toString()

                        SubcomposeAsyncImage(
                            model = if (photoUrl == R.drawable.product_image_not_found.toString()) {
                                R.drawable.product_image_not_found
                            } else {
                                photoUrl
                            },
                            contentDescription = "Profile Picture",
                            loading = { CircularProgressIndicator() },
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(text = memberName, style = MaterialTheme.typography.bodyLarge)
                    }

                }
            }
        }

        // Botão flutuante de adicionar membro
        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.End) // Certifique-se que o botão está alinhado à direita
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add User")
        }

        // Dialog para inserir o email
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
