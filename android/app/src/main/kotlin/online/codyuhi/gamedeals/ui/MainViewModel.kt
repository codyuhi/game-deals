package online.codyuhi.gamedeals.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import online.codyuhi.gamedeals.data.repository.GameDealsRepository
import online.codyuhi.gamedeals.data.repository.SettingsRepository
import online.codyuhi.gamedeals.domain.Deal
import online.codyuhi.gamedeals.domain.DealDetails
import online.codyuhi.gamedeals.domain.FavoriteGroup
import online.codyuhi.gamedeals.domain.GameDetails
import online.codyuhi.gamedeals.domain.SearchResult
import online.codyuhi.gamedeals.domain.Store
import online.codyuhi.gamedeals.ui.theme.ThemeMode

enum class NavTab { Deals, Search, Favorites }

enum class DealSort { DealRating, Savings, Price, SteamRating }

data class MainUiState(
    val themeMode: ThemeMode = ThemeMode.System,
    val selectedTab: NavTab = NavTab.Deals,
    val stores: List<Store> = emptyList(),
    val deals: List<Deal> = emptyList(),
    val selectedStoreID: String = "all",
    val sortBy: DealSort = DealSort.DealRating,
    val quickSearchQuery: String = "",
    val isRefreshing: Boolean = false,
    val isOffline: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<SearchResult> = emptyList(),
    val isSearching: Boolean = false,
    val searchError: String? = null,
    val favoriteGroups: List<FavoriteGroup> = emptyList(),
    val activeDealDetail: DealDetails? = null,
    val activeDealID: String? = null,
    val isLoadingDealDetail: Boolean = false,
    val activeGameDetail: GameDetails? = null,
    val activeGameID: String? = null,
    val isLoadingGameDetail: Boolean = false,
    val favoriteModalTarget: Pair<String, String>? = null, // (gameID, title)
) {
    val activeStores: List<Store>
        get() {
            val dealStoreIds = deals.map { it.storeID }.toSet()
            return stores.filter { dealStoreIds.contains(it.storeID) }
        }

    val filteredDeals: List<Deal>
        get() {
            var list = deals

            if (selectedStoreID != "all") {
                list = list.filter { it.storeID == selectedStoreID }
            }

            if (quickSearchQuery.isNotBlank()) {
                val q = quickSearchQuery.trim().lowercase()
                list = list.filter { it.title.lowercase().contains(q) }
            }

            return when (sortBy) {
                DealSort.DealRating -> list.sortedByDescending { it.dealRating }
                DealSort.Savings -> list.sortedByDescending { it.savings }
                DealSort.Price -> list.sortedBy { it.salePrice }
                DealSort.SteamRating -> list.sortedByDescending { it.steamRatingPercent }
            }
        }
}

class MainViewModel(
    private val settingsRepository: SettingsRepository,
    private val repository: GameDealsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        // Collect theme settings
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _uiState.update { it.copy(themeMode = settings.themeMode) }
            }
        }

        // Collect stores
        viewModelScope.launch {
            repository.stores.collect { stores ->
                _uiState.update { it.copy(stores = stores) }
            }
        }

        // Collect deals
        viewModelScope.launch {
            repository.deals.collect { deals ->
                _uiState.update { it.copy(deals = deals) }
            }
        }

        // Collect favorites
        viewModelScope.launch {
            repository.favoriteGroups.collect { groups ->
                _uiState.update { it.copy(favoriteGroups = groups) }
            }
        }

        // Initial refresh
        refresh()
        viewModelScope.launch {
            repository.initDefaultFavoritesIfEmpty()
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    fun setTab(tab: NavTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun setSelectedStore(storeID: String) {
        _uiState.update { it.copy(selectedStoreID = storeID) }
    }

    fun setSortBy(sort: DealSort) {
        _uiState.update { it.copy(sortBy = sort) }
    }

    fun setQuickSearchQuery(query: String) {
        _uiState.update { it.copy(quickSearchQuery = query) }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val storesResult = repository.refreshStores()
            val dealsResult = repository.refreshDeals()
            val isOffline = storesResult.isFailure || dealsResult.isFailure
            _uiState.update {
                it.copy(
                    isRefreshing = false,
                    isOffline = isOffline,
                )
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun executeSearch(query: String? = null) {
        val q = query ?: _uiState.value.searchQuery
        if (q.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, searchError = null, searchQuery = q) }
            val result = repository.searchGames(q)
            result.onSuccess { results ->
                _uiState.update { it.copy(searchResults = results, isSearching = false) }
            }.onFailure { error ->
                _uiState.update { it.copy(isSearching = false, searchError = error.message) }
            }
        }
    }

    fun openDealDetail(dealID: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(activeDealID = dealID, isLoadingDealDetail = true, activeDealDetail = null) }
            val result = repository.getDealDetails(dealID)
            result.onSuccess { details ->
                _uiState.update { it.copy(activeDealDetail = details, isLoadingDealDetail = false) }
            }.onFailure {
                _uiState.update { it.copy(isLoadingDealDetail = false) }
            }
        }
    }

    fun closeDealDetail() {
        _uiState.update { it.copy(activeDealDetail = null, activeDealID = null, isLoadingDealDetail = false) }
    }

    fun openGameDetail(gameID: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(activeGameID = gameID, isLoadingGameDetail = true, activeGameDetail = null) }
            val result = repository.getGameDetails(gameID)
            result.onSuccess { details ->
                _uiState.update { it.copy(activeGameDetail = details, isLoadingGameDetail = false) }
            }.onFailure {
                _uiState.update { it.copy(isLoadingGameDetail = false) }
            }
        }
    }

    fun closeGameDetail() {
        _uiState.update { it.copy(activeGameDetail = null, activeGameID = null, isLoadingGameDetail = false) }
    }

    fun openFavoriteModal(gameID: String, title: String) {
        _uiState.update { it.copy(favoriteModalTarget = gameID to title) }
    }

    fun closeFavoriteModal() {
        _uiState.update { it.copy(favoriteModalTarget = null) }
    }

    fun addFavorite(groupNickname: String, gameID: String, name: String) {
        viewModelScope.launch {
            repository.addFavoriteItem(groupNickname, gameID, name)
            closeFavoriteModal()
        }
    }

    fun removeFavorite(id: Long) {
        viewModelScope.launch {
            repository.removeFavoriteItem(id)
        }
    }

    fun addFavoriteGroup(nickname: String) {
        viewModelScope.launch {
            repository.addFavoriteGroup(nickname)
        }
    }

    fun renameFavoriteGroup(oldNickname: String, newNickname: String) {
        viewModelScope.launch {
            repository.renameFavoriteGroup(oldNickname, newNickname)
        }
    }

    fun deleteFavoriteGroup(nickname: String) {
        viewModelScope.launch {
            repository.deleteFavoriteGroup(nickname)
        }
    }

    companion object {
        fun factory(
            settingsRepository: SettingsRepository,
            repository: GameDealsRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainViewModel(settingsRepository, repository) as T
            }
        }
    }
}
