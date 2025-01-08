import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.kotlin.socialstore.ui.screens.Products.SelectionBehavior

@Composable
fun <T> TwoColumnGridList(
    items: List<T>,
    itemContent: @Composable (T) -> Unit,
    pictureProvider: (T) -> Any?,
    onItemClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    columns: Int = 2,
    showAddButton: Boolean = false,
    onAddButtonClick: (T) -> Unit = {},
    isItemSelected: (T) -> Boolean
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items) { item ->
            ElevatedCard(
                modifier = Modifier
                    .size(height = 250.dp, width = 200.dp)
                    .padding(UiConstants.itemSpacing),
                onClick = { onItemClick(item) }
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            SubcomposeAsyncImage(
                                model = pictureProvider(item),
                                contentDescription = null,
                                loading = { CircularProgressIndicator() },
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        if (showAddButton) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                            ) {
                                IconButton(
                                    onClick = { onAddButtonClick(item) },
                                    modifier = Modifier.size(100.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isItemSelected(item)) {
                                            Icons.Default.Delete
                                        } else {
                                            Icons.Default.Add
                                        },
                                        contentDescription = if (isItemSelected(item)) {
                                            "Remove item"
                                        } else {
                                            "Add item"
                                        },
                                        tint = Color.White,
                                        modifier = Modifier.size(25.dp)
                                    )
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(5.dp)) {
                        itemContent(item)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> RowList(
    items: List<T>,
    itemContent: @Composable (T) -> Unit,
    itemEndContet: @Composable (T) -> Unit = {},
    pictureProvider: (T) -> Any?,
    onItemClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    selectionBehavior: SelectionBehavior<T>? = null
) {
    var itemsSelected by remember { mutableStateOf<List<T>>(emptyList()) }

    LaunchedEffect(selectionBehavior?.selectedItems) {
        selectionBehavior?.selectedItems?.let {
            itemsSelected = it
        }
    }

    LazyColumn(modifier = modifier) {
        items(
            items = items,
            key = { it.hashCode() }
        ) { item ->
            val isSelected = itemsSelected.contains(item)

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .animateItemPlacement()
                    .combinedClickable(
                        onLongClick = {
                            if (selectionBehavior?.enabled != true) return@combinedClickable

                            itemsSelected = if (isSelected) {
                                itemsSelected - item
                            } else {
                                itemsSelected + item
                            }
                            selectionBehavior.onItemsSelected(itemsSelected)
                        },
                        onClick = { onItemClick(item) },
                    ),
                colors = if (isSelected)
                    CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                else
                    CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(13.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isSelected) {
                        SubcomposeAsyncImage(
                            model = pictureProvider(item),
                            contentDescription = null,
                            loading = { CircularProgressIndicator(strokeWidth = 2.dp) },
                            modifier = Modifier
                                .size(60.dp)
                                .clickable {
                                    if (selectionBehavior?.enabled != true) return@clickable

                                    if (itemsSelected.isNotEmpty()) {
                                        itemsSelected = itemsSelected + item
                                        selectionBehavior.onItemsSelected(itemsSelected)
                                    }
                                }
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                        )
                    } else {
                        IconButton(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape),
                            onClick = {
                                if (selectionBehavior?.enabled != true) return@IconButton

                                itemsSelected = itemsSelected - item
                                selectionBehavior.onItemsSelected(itemsSelected)
                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(40.dp),
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        itemContent(item)
                    }

                    itemEndContet(item)
                }
            }
        }
    }
}


@Composable
fun <T> ScrollableRowList(
    items: List<T>,
    itemContent: @Composable (T) -> Unit,
    pictureProvider: (T) -> Any?,
    onItemClick: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(modifier = Modifier.fillMaxWidth()) {
        items(items) { item ->
            ElevatedCard(
                modifier = Modifier
                    .size(height = 200.dp, width = 150.dp)
                    .padding(UiConstants.itemSpacing),
                onClick = { onItemClick(item) }
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    SubcomposeAsyncImage(
                        model = pictureProvider(item),
                        contentDescription = null,
                        loading = { CircularProgressIndicator() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Column(modifier = Modifier.padding(5.dp)) {
                    itemContent(item)
                }
            }
        }
    }
}
