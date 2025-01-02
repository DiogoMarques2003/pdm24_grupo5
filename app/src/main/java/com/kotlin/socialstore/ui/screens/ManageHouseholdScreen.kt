package com.kotlin.socialstore.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val usersDao = database.usersDao()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
    val firestore = FirebaseFirestore.getInstance()
    val familyHouseholdCollection = firestore.collection("familyHousehold")
    var householdId by remember { mutableStateOf<String?>(null) }
    var isInitialized by remember { mutableStateOf(false) }

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
                        usersDao.deleteById(user.id )
                        usersDao.insert(updatedUser) // Atualiza na base local
                    }

                    householdId = newFamilyHouseholdID.id
                } catch (e: Exception) {
                    Log.e("ManageHouseholdScreen", "Erro ao criar/atualizar FamilyHousehold", e)
                }
            } else {
                householdId = user?.familyHouseholdID // Apenas exibir o ID existente
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
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { navController.popBackStack() }) {
            Text(text = "Back")
        }
    }
}

