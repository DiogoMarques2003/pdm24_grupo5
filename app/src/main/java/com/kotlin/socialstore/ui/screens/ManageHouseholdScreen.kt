package com.kotlin.socialstore.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.google.firebase.storage.FirebaseStorage
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.firebase.FirebaseObj
import com.kotlin.socialstore.viewModels.ManageHouseholdViewModel
import kotlinx.coroutines.tasks.await
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource


@Composable
fun ManageHousehold(
    navController: NavController,
    viewModel: ManageHouseholdViewModel
) {
    val householdId by viewModel._householdId.collectAsState(null)
    val householdMembers by viewModel.householdMembers.collectAsState(emptyList())
    val currentUserId = FirebaseObj.getCurrentUser()?.uid
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var emailInput by remember { mutableStateOf("") }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            stringResource(R.string.Manage_Household),
            style = MaterialTheme.typography.headlineMedium
        )

        if (householdId == null) {
            CircularProgressIndicator()
            LaunchedEffect(Unit) {
                viewModel.initialize(currentUserId)
            }
        } else {
         //   Text(
         //       text = "Household ID: $householdId",
         //       style = MaterialTheme.typography.bodyLarge
          //  )

            val storage = FirebaseStorage.getInstance()
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
                                    viewModel.removeMemberFromHousehold(member["id"] as String)
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
                                viewModel.addUserToHousehold(emailInput, context)
                                emailInput = ""
                                showDialog = false
                            }
                        ) {
                            Text(stringResource(R.string.add_people_to_your_household))
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
            Button(onClick = { navController.popBackStack() }) {
                Text(text = "Back")
            }
        }
    }
}
