package online.codyuhi.gamedeals.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import online.codyuhi.gamedeals.ui.components.CedarThemeToggle
import online.codyuhi.gamedeals.ui.components.Lucide
import online.codyuhi.gamedeals.ui.components.OfflineBanner
import online.codyuhi.gamedeals.ui.deals.DealsScreen
import online.codyuhi.gamedeals.ui.details.DealDetailSheet
import online.codyuhi.gamedeals.ui.details.GameDetailSheet
import online.codyuhi.gamedeals.ui.favorites.FavoriteModal
import online.codyuhi.gamedeals.ui.favorites.FavoritesScreen
import online.codyuhi.gamedeals.ui.search.SearchScreen
import online.codyuhi.gamedeals.ui.theme.Cedar
import online.codyuhi.gamedeals.ui.theme.CedarTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDealsApp(
    viewModel: MainViewModel,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isWideScreen = adaptiveInfo.windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT

    CedarTheme(mode = state.themeMode) {
        val navSuiteType = if (isWideScreen) {
            NavigationSuiteType.NavigationRail
        } else {
            NavigationSuiteType.NavigationBar
        }

        val itemColors = NavigationSuiteDefaults.itemColors(
            navigationBarItemColors = NavigationBarItemDefaults.colors(
                selectedIconColor = Cedar.colors.spruce,
                selectedTextColor = Cedar.colors.spruce,
                indicatorColor = Cedar.colors.inset,
                unselectedIconColor = Cedar.colors.ink4,
                unselectedTextColor = Cedar.colors.ink4,
            ),
            navigationRailItemColors = NavigationRailItemDefaults.colors(
                selectedIconColor = Cedar.colors.spruce,
                selectedTextColor = Cedar.colors.spruce,
                indicatorColor = Cedar.colors.inset,
                unselectedIconColor = Cedar.colors.ink4,
                unselectedTextColor = Cedar.colors.ink4,
            ),
        )

        val storeMap = state.stores.associateBy { it.storeID }

        NavigationSuiteScaffold(
            layoutType = navSuiteType,
            navigationSuiteColors = NavigationSuiteDefaults.colors(
                navigationBarContainerColor = Cedar.colors.surface,
                navigationRailContainerColor = Cedar.colors.surface,
            ),
            navigationSuiteItems = {
                item(
                    selected = state.selectedTab == NavTab.Deals,
                    onClick = { viewModel.setTab(NavTab.Deals) },
                    icon = { Icon(Lucide.Gamepad2, contentDescription = "Deals") },
                    label = { Text("Deals") },
                    colors = itemColors,
                )
                item(
                    selected = state.selectedTab == NavTab.Search,
                    onClick = { viewModel.setTab(NavTab.Search) },
                    icon = { Icon(Lucide.Search, contentDescription = "Search") },
                    label = { Text("Search") },
                    colors = itemColors,
                )
                item(
                    selected = state.selectedTab == NavTab.Favorites,
                    onClick = { viewModel.setTab(NavTab.Favorites) },
                    icon = { Icon(Lucide.Heart, contentDescription = "Favorites") },
                    label = { Text("Favorites") },
                    colors = itemColors,
                )
            },
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Lucide.Gamepad2,
                                    contentDescription = null,
                                    tint = Cedar.colors.spruce,
                                    modifier = Modifier.size(24.dp),
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "GameDeals",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = Cedar.colors.ink,
                                )
                            }
                        },
                        actions = {
                            CedarThemeToggle(
                                mode = state.themeMode,
                                onToggle = { viewModel.setThemeMode(it) },
                                modifier = Modifier.padding(end = 8.dp),
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Cedar.colors.canvas,
                            titleContentColor = Cedar.colors.ink,
                        ),
                    )
                },
                containerColor = Cedar.colors.canvas,
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    OfflineBanner(isOffline = state.isOffline)

                    Box(modifier = Modifier.fillMaxSize()) {
                        when (state.selectedTab) {
                            NavTab.Deals -> {
                                DealsScreen(
                                    state = state,
                                    onSelectStore = { viewModel.setSelectedStore(it) },
                                    onSelectSort = { viewModel.setSortBy(it) },
                                    onQuickSearch = { viewModel.setQuickSearchQuery(it) },
                                    onRefresh = { viewModel.refresh() },
                                    onDealClick = { viewModel.openDealDetail(it) },
                                    onFavoriteClick = { id, title -> viewModel.openFavoriteModal(id, title) },
                                )
                            }
                            NavTab.Search -> {
                                SearchScreen(
                                    state = state,
                                    onQueryChange = { viewModel.setSearchQuery(it) },
                                    onSearch = { viewModel.executeSearch() },
                                    onGameClick = { viewModel.openGameDetail(it) },
                                    onFavoriteClick = { id, title -> viewModel.openFavoriteModal(id, title) },
                                )
                            }
                            NavTab.Favorites -> {
                                FavoritesScreen(
                                    state = state,
                                    onAddGroup = { viewModel.addFavoriteGroup(it) },
                                    onRenameGroup = { old, new -> viewModel.renameFavoriteGroup(old, new) },
                                    onDeleteGroup = { viewModel.deleteFavoriteGroup(it) },
                                    onRemoveItem = { viewModel.removeFavorite(it) },
                                    onGameClick = { viewModel.openGameDetail(it) },
                                )
                            }
                        }
                    }
                }
            }

            // Deal Details Modal Sheet
            if (state.activeDealID != null) {
                DealDetailSheet(
                    dealID = state.activeDealID!!,
                    details = state.activeDealDetail,
                    isLoading = state.isLoadingDealDetail,
                    storeMap = storeMap,
                    onDismiss = { viewModel.closeDealDetail() },
                    onFavoriteClick = { id, title -> viewModel.openFavoriteModal(id, title) },
                )
            }

            // Game Details Modal Sheet
            if (state.activeGameID != null) {
                GameDetailSheet(
                    gameID = state.activeGameID!!,
                    details = state.activeGameDetail,
                    isLoading = state.isLoadingGameDetail,
                    storeMap = storeMap,
                    onDismiss = { viewModel.closeGameDetail() },
                    onFavoriteClick = { id, title -> viewModel.openFavoriteModal(id, title) },
                )
            }

            // Add to Favorites Modal Dialog
            if (state.favoriteModalTarget != null) {
                val (gameID, title) = state.favoriteModalTarget!!
                FavoriteModal(
                    gameID = gameID,
                    gameTitle = title,
                    groups = state.favoriteGroups,
                    onSave = { group, id, name -> viewModel.addFavorite(group, id, name) },
                    onDismiss = { viewModel.closeFavoriteModal() },
                )
            }
        }
    }
}
