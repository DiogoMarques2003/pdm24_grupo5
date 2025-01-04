import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.kotlin.socialstore.R
import com.kotlin.socialstore.data.DataConstants
import com.kotlin.socialstore.data.entity.Users
import com.kotlin.socialstore.ui.elements.PopBackButton
import com.kotlin.socialstore.ui.theme.SocialStoreTheme
import androidx.compose.material3.MaterialTheme
import TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUsersPage(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ManageUsersViewModel
) {
    val userManagementState by viewModel.userManagementState.collectAsState()

    var state by remember { mutableStateOf(0) }
    val titles = listOf("Volunteers", "Beneficiaries")

    //Stop listeners when leaving screen
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopListeners()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(navController, "Manage Users", true)

//        PrimaryTabRow(selectedTabIndex = state) {
//            titles.forEachIndexed { index, title ->
//                Tab(
//                    selected = state == index,
//                    onClick = {
//                        state = index
//                        viewModel.setTabIndex(state) },
//                    text = { Text(text = title,
//                            maxLines = 1,
//                            overflow = TextOverflow.Ellipsis,
//                            style = MaterialTheme.typography.subtitle1,
//                            modifier = Modifier)
//                    }
//                )
//            }
//        }

        TabRow(selectedTabIndex = state, backgroundColor = MaterialTheme.colorScheme.surface ) {
            titles.forEachIndexed { index, title ->
//                Tab(text = { Text(title) },
//                    selected = state == index,
//                    onClick = { state = index }
//                )
                Tab(
                    selected = state == index,
                    onClick = {
                        state = index
                        viewModel.setTabIndex(state) },
                    text = { Text(title) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        TextField(
            value = userManagementState.searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search by email") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // User List
        if (userManagementState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(userManagementState.filteredUsers) { user ->
                    UserCard(user = user, onApprove = {
                        viewModel.updateUserStatus(user.id, true)
                    }, onDeny = {
                        viewModel.updateUserStatus(user.id, false)
                    }, onEdit = {
                        // composable para a outra tela
                    })
                }
            }
        }
    }
}

@Composable
fun UserCard(
    user: Users,
    onApprove: () -> Unit,
    onDeny: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = 4.dp
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberAsyncImagePainter(user.profilePic ?: R.drawable.profile_image_not_found ),
                contentDescription = null,
                modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.Gray)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.email, style = MaterialTheme.typography.bodyLarge, maxLines = 1)
                Text(
                    text = formatAccountType(user.accountType),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            when (user.active) {
                false -> {
                    Row {
                        IconButton(onClick = onApprove) {
                            Icon(Icons.Default.Check, contentDescription = "Approve")
                        }
                        IconButton(onClick = onDeny) {
                            Icon(Icons.Default.Close, contentDescription = "Deny")
                        }
                    }
                }
                true -> {
                    if (user.accountType == DataConstants.AccountType.benefiaryy) {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                }
            }
        }
    }
}