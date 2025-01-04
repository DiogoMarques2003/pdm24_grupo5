import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    LazyColumn() {
        items(items) { item ->
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(10.dp)) {
                    Row {
                        SubcomposeAsyncImage(
                            model = pictureProvider(item),
                            contentDescription = null,
                            loading = { CircularProgressIndicator() },
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(80.dp)
                                .align(Alignment.CenterVertically),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(Modifier.size(UiConstants.itemSpacing))
                        Column {
                            itemContent(item)
                        }
                    }
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