package online.codyuhi.gamedeals.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import online.codyuhi.gamedeals.domain.SearchResult
import online.codyuhi.gamedeals.domain.formatPrice
import online.codyuhi.gamedeals.ui.MainUiState
import online.codyuhi.gamedeals.ui.components.CedarButton
import online.codyuhi.gamedeals.ui.components.CedarCard
import online.codyuhi.gamedeals.ui.components.CedarTextField
import online.codyuhi.gamedeals.ui.components.Lucide
import online.codyuhi.gamedeals.ui.theme.Cedar
import online.codyuhi.gamedeals.ui.theme.Eyebrow

@Composable
fun SearchScreen(
    state: MainUiState,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onGameClick: (String) -> Unit,
    onFavoriteClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(8.dp))

        // Title and description
        Text(
            text = "Search PC Catalog",
            style = MaterialTheme.typography.headlineSmall,
            color = Cedar.colors.ink,
        )
        Text(
            text = "Find sales, price history, and current deals across 30+ digital stores.",
            style = MaterialTheme.typography.bodyMedium,
            color = Cedar.colors.ink4,
        )

        Spacer(Modifier.height(12.dp))

        // Search Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CedarTextField(
                value = state.searchQuery,
                onValueChange = onQueryChange,
                placeholder = "Search game title…",
                modifier = Modifier.weight(1f),
                leadingIcon = {
                    Icon(
                        imageVector = Lucide.Search,
                        contentDescription = null,
                        tint = Cedar.colors.ink4,
                        modifier = Modifier.size(18.dp),
                    )
                },
                trailingIcon = if (state.searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(
                                imageVector = Lucide.X,
                                contentDescription = "Clear",
                                tint = Cedar.colors.ink4,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                } else null,
            )

            CedarButton(
                onClick = onSearch,
                enabled = state.searchQuery.isNotBlank() && !state.isSearching,
                text = "Search",
                leadingIcon = Lucide.Search,
            )
        }

        Spacer(Modifier.height(12.dp))

        // Results state
        if (state.isSearching) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Cedar.colors.spruce)
                    Spacer(Modifier.height(12.dp))
                    Text("Searching catalog…", style = MaterialTheme.typography.bodyMedium, color = Cedar.colors.ink3)
                }
            }
        } else if (state.searchResults.isEmpty() && state.searchQuery.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Lucide.Search,
                        contentDescription = null,
                        tint = Cedar.colors.ink4,
                        modifier = Modifier.size(40.dp),
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "No games found for \"${state.searchQuery}\"",
                        style = MaterialTheme.typography.titleMedium,
                        color = Cedar.colors.ink2,
                    )
                }
            }
        } else if (state.searchResults.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Lucide.Gamepad2,
                        contentDescription = null,
                        tint = Cedar.colors.ink4,
                        modifier = Modifier.size(48.dp),
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Search over 100,000 PC games",
                        style = MaterialTheme.typography.titleMedium,
                        color = Cedar.colors.ink2,
                    )
                    Text(
                        "Type a title above to discover discounts and store prices.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Cedar.colors.ink4,
                    )
                }
            }
        } else {
            Text(
                text = "${state.searchResults.size} results found",
                style = Eyebrow,
                color = Cedar.colors.ink4,
            )
            Spacer(Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 320.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.searchResults, key = { it.gameID }) { item ->
                    SearchResultCard(
                        result = item,
                        onClick = { onGameClick(item.gameID) },
                        onFavoriteClick = { onFavoriteClick(item.gameID, item.external) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchResultCard(
    result: SearchResult,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CedarCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AsyncImage(
                model = result.thumb,
                contentDescription = result.external,
                modifier = Modifier
                    .size(width = 80.dp, height = 50.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(Cedar.colors.inset),
                contentScale = ContentScale.Crop,
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.external,
                    style = MaterialTheme.typography.titleMedium,
                    color = Cedar.colors.ink,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "From ",
                        style = MaterialTheme.typography.bodySmall,
                        color = Cedar.colors.ink4,
                    )
                    Text(
                        text = if (result.cheapest == 0.0) "FREE" else "$${formatPrice(result.cheapest)}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Cedar.colors.spruce,
                    )
                }
            }

            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    imageVector = Lucide.Heart,
                    contentDescription = "Favorite game",
                    tint = Cedar.colors.rust,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}
