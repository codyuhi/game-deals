package online.codyuhi.gamedeals.ui.deals

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import online.codyuhi.gamedeals.domain.Deal
import online.codyuhi.gamedeals.domain.Store
import online.codyuhi.gamedeals.ui.DealSort
import online.codyuhi.gamedeals.ui.MainUiState
import online.codyuhi.gamedeals.ui.components.CedarTextField
import online.codyuhi.gamedeals.ui.components.Lucide
import online.codyuhi.gamedeals.ui.theme.Cedar
import online.codyuhi.gamedeals.ui.theme.Eyebrow

@Composable
fun DealsScreen(
    state: MainUiState,
    onSelectStore: (String) -> Unit,
    onSelectSort: (DealSort) -> Unit,
    onQuickSearch: (String) -> Unit,
    onRefresh: () -> Unit,
    onDealClick: (String) -> Unit,
    onFavoriteClick: (String, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val storeMap = state.stores.associateBy { it.storeID }
    val filteredDeals = state.filteredDeals

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(8.dp))

        // Quick search bar + Refresh
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CedarTextField(
                value = state.quickSearchQuery,
                onValueChange = onQuickSearch,
                placeholder = "Filter deals by title…",
                modifier = Modifier.weight(1f),
                leadingIcon = {
                    Icon(
                        imageVector = Lucide.Search,
                        contentDescription = null,
                        tint = Cedar.colors.ink4,
                        modifier = Modifier.size(18.dp),
                    )
                },
                trailingIcon = if (state.quickSearchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { onQuickSearch("") }) {
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

            IconButton(
                onClick = onRefresh,
                enabled = !state.isRefreshing,
                modifier = Modifier.size(48.dp),
            ) {
                if (state.isRefreshing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Cedar.colors.spruce,
                    )
                } else {
                    Icon(
                        imageVector = Lucide.RefreshCw,
                        contentDescription = "Refresh deals",
                        tint = Cedar.colors.spruce,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Store filter chips (scrollable row)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilterChip(
                selected = state.selectedStoreID == "all",
                onClick = { onSelectStore("all") },
                label = { Text("All Stores (${state.deals.size})") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Cedar.colors.spruce,
                    selectedLabelColor = Cedar.colors.onSpruce,
                    containerColor = Cedar.colors.surface,
                    labelColor = Cedar.colors.ink2,
                ),
            )

            state.activeStores.forEach { store ->
                FilterChip(
                    selected = state.selectedStoreID == store.storeID,
                    onClick = { onSelectStore(store.storeID) },
                    label = { Text(store.storeName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Cedar.colors.spruce,
                        selectedLabelColor = Cedar.colors.onSpruce,
                        containerColor = Cedar.colors.surface,
                        labelColor = Cedar.colors.ink2,
                    ),
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        // Sort filter chips (scrollable row)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "Sort:",
                style = Eyebrow,
                color = Cedar.colors.ink4,
                modifier = Modifier.padding(end = 4.dp),
            )

            val sorts = listOf(
                DealSort.DealRating to "Deal Rating",
                DealSort.Savings to "Savings %",
                DealSort.Price to "Sale Price",
                DealSort.SteamRating to "Steam Reviews",
            )

            sorts.forEach { (sort, label) ->
                FilterChip(
                    selected = state.sortBy == sort,
                    onClick = { onSelectSort(sort) },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Cedar.colors.inset,
                        selectedLabelColor = Cedar.colors.spruce,
                        containerColor = Cedar.colors.surface,
                        labelColor = Cedar.colors.ink3,
                    ),
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Deals Count
        Text(
            text = "Showing ${filteredDeals.size} deals",
            style = Eyebrow,
            color = Cedar.colors.ink4,
        )

        Spacer(Modifier.height(6.dp))

        // Responsive grid (1 col on 360dp phone/cover, 2-3 cols on 752dp Fold 8 inner screen)
        if (filteredDeals.isEmpty() && !state.isRefreshing) {
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
                        "No deals found",
                        style = MaterialTheme.typography.titleMedium,
                        color = Cedar.colors.ink2,
                    )
                    Text(
                        "Try selecting another store or clearing the search filter.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Cedar.colors.ink4,
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 340.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(filteredDeals, key = { it.dealID }) { deal ->
                    DealTile(
                        deal = deal,
                        store = storeMap[deal.storeID],
                        onClick = { onDealClick(deal.dealID) },
                        onFavoriteClick = { onFavoriteClick(deal.gameID, deal.title) },
                    )
                }
            }
        }
    }
}
