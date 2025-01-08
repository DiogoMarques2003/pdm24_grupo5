package com.kotlin.socialstore.ui.screens.Users

import UiConstants
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.SubcomposeAsyncImage
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.DataConstants.mapStatusColors
import com.kotlin.socialstore.data.DataConstants.mapStatusIcons
import com.kotlin.socialstore.data.entity.UsersNotes
import com.kotlin.socialstore.ui.elements.BackgroundImageElement
import com.kotlin.socialstore.ui.elements.ButtonElement
import com.kotlin.socialstore.ui.elements.LoadIndicator
import com.kotlin.socialstore.ui.elements.PopBackButton
import com.kotlin.socialstore.viewModels.CheckInViewModel
import kotlinx.coroutines.flow.first

@Composable
fun CheckInScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    checkInViewModel: CheckInViewModel
) {
    val user by checkInViewModel.user.collectAsState(null)
    val isLoading by checkInViewModel.isLoading.collectAsState(true)

    var visits by remember { mutableIntStateOf(0) }
    var takenItems by remember { mutableLongStateOf(0) }
    var householdNotes by remember { mutableStateOf<List<UsersNotes>>(emptyList()) }

    BackgroundImageElement()

    LaunchedEffect(Unit) {
        checkInViewModel.getData()
    }

    LaunchedEffect(user) {
        visits = checkInViewModel.getVisitsMonthlyById(user?.familyHouseholdID ?: "")
            .first().size

        takenItems =
            checkInViewModel.getTakenItensMonthlyById(user?.familyHouseholdID ?: "")
                .first()

        val notes = checkInViewModel.getHouseholdNotes(user?.familyHouseholdID ?: "").first()

        householdNotes = notes.filter { !it.notes.isNullOrEmpty() }
    }

    Column(modifier.fillMaxSize()) {
        PopBackButton(navController)

        if (isLoading) {
            LoadIndicator(modifier)
        } else {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier) {
                    SubcomposeAsyncImage(
                        model = user?.profilePic ?: R.drawable.product_image_not_found,
                        contentDescription = null,
                        loading = { CircularProgressIndicator() },
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(UiConstants.itemSpacing))

                // Nome
                Text(
                    text = user?.name?.split(" ")?.first() ?: "N/A",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(UiConstants.itemSpacing))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.this_month),
                    style = MaterialTheme.typography.titleSmall
                        .copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(UiConstants.itemSpacing))


                Text("${stringResource(R.string.visits)}: $visits")
                Text("${stringResource(R.string.taken_items_text)}: $takenItems")

                // Status
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.status))
                    Icon(
                        imageVector = mapStatusIcons[user?.warningsLevel]
                            ?: Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = mapStatusColors[user?.warningsLevel] ?: Color.Green
                    )
                }
            }

            // Espaçamento
            Spacer(modifier = Modifier.height(16.dp))

            Text(stringResource(R.string.household_notes),
                style = MaterialTheme.typography.titleSmall
                    .copy(fontWeight = FontWeight.Bold))

            LazyColumn(
                Modifier
                    .weight(1f)
                    .padding(top = 10.dp)
            ) {
                items(householdNotes) { note ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = note.name,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Divider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = note.notes.orEmpty(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                ButtonElement(
                    text = stringResource(R.string.edit_user),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate("edit_profile_as_admin_screen/${checkInViewModel.userId}")
                    }
                )
            }

        }
    }

}