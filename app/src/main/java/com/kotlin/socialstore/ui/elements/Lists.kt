import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

@Composable
fun <T> RowList(
    items: List<T>,
    itemContent: @Composable (T) -> Unit,
    pictureProvider: (T) -> Any?,
    onItemClick: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(items) { item ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(item) },
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(13.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        SubcomposeAsyncImage(
                            model = pictureProvider(item),
                            contentDescription = null,
                            loading = { CircularProgressIndicator(strokeWidth = 2.dp) },
                            modifier = Modifier
                                .matchParentSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        itemContent(item)
                    }

                    // Options icon
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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