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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
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

@Composable
fun <T> TwoColumnGridList(
    items: List<T>,
    itemContent: @Composable (T) -> Unit,
    pictureProvider: (T) -> Any?,
    onItemClick: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items) { item ->
            ElevatedCard(
                modifier = Modifier
                    .size(height = 200.dp, width = 200.dp)
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> RowList(
    items: List<T>,
    itemContent: @Composable (T) -> Unit,
    itemEndContet: @Composable (T) -> Unit,
    pictureProvider: (T) -> Any?,
    onItemClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    setSelectMultipleBehaviour: Boolean = false,
    onItemsSelected: (List<T>) -> Unit = {},
    resetSelection: Boolean = false
) {
    var itemsSelected by remember { mutableStateOf<List<T>>(emptyList()) }

    LaunchedEffect(resetSelection) {
        if (resetSelection) {
            itemsSelected = emptyList()
        }
    }

    LazyColumn(
        modifier = modifier
    ) {
        items(items) { item ->
            val isSelected = itemsSelected.contains(item)

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = UiConstants.itemSpacing)
                    .combinedClickable(
                        onLongClick = {
                            if(!setSelectMultipleBehaviour) return@combinedClickable

                            itemsSelected = if (isSelected) {
                                itemsSelected.filter { it != item }
                            } else {
                                itemsSelected + item
                            }
                            onItemsSelected(itemsSelected)
                        },
                        onClick = { },
                    ),
                colors = if (isSelected) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) else
                    CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
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
                                    if(!setSelectMultipleBehaviour) return@clickable

                                    if (itemsSelected.isNotEmpty()) {
                                        itemsSelected = itemsSelected + item
                                        onItemsSelected(itemsSelected)
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
                                if(!setSelectMultipleBehaviour) return@IconButton

                                if(itemsSelected.isNotEmpty()) {
                                    itemsSelected = itemsSelected.filter{ it != item }
                                    onItemsSelected(itemsSelected)
                                }
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
