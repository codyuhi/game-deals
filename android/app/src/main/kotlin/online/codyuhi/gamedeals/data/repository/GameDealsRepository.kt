package online.codyuhi.gamedeals.data.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import online.codyuhi.gamedeals.data.api.CheapSharkApi
import online.codyuhi.gamedeals.data.api.DealDetailsWrapper
import online.codyuhi.gamedeals.data.api.DealListWrapper
import online.codyuhi.gamedeals.data.api.GameDetailsWrapper
import online.codyuhi.gamedeals.data.api.SearchResultsWrapper
import online.codyuhi.gamedeals.data.api.StoreListWrapper
import online.codyuhi.gamedeals.data.db.DealDao
import online.codyuhi.gamedeals.data.db.DealEntity
import online.codyuhi.gamedeals.data.db.FavoriteDao
import online.codyuhi.gamedeals.data.db.FavoriteGroupEntity
import online.codyuhi.gamedeals.data.db.FavoriteItemEntity
import online.codyuhi.gamedeals.data.db.StoreDao
import online.codyuhi.gamedeals.data.db.StoreEntity
import online.codyuhi.gamedeals.domain.Deal
import online.codyuhi.gamedeals.domain.DealDetails
import online.codyuhi.gamedeals.domain.FavoriteGroup
import online.codyuhi.gamedeals.domain.GameDetails
import online.codyuhi.gamedeals.domain.SearchResult
import online.codyuhi.gamedeals.domain.Store

class GameDealsRepository(
    private val context: Context,
    private val api: CheapSharkApi,
    private val storeDao: StoreDao,
    private val dealDao: DealDao,
    private val favoriteDao: FavoriteDao,
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    val stores: Flow<List<Store>> = storeDao.getActiveStores().map { list ->
        list.map { it.toDomain() }
    }

    val deals: Flow<List<Deal>> = dealDao.getAllDeals().map { list ->
        list.map { it.toDomain() }
    }

    val favoriteGroups: Flow<List<FavoriteGroup>> = combine(
        favoriteDao.getAllGroups(),
        favoriteDao.getAllItems(),
    ) { groups, items ->
        val itemsByGroup = items.groupBy { it.groupNickname }
        groups.map { group ->
            FavoriteGroup(
                nickname = group.nickname,
                createdAt = group.createdAt,
                items = itemsByGroup[group.nickname]?.map { it.toDomain() } ?: emptyList(),
            )
        }
    }

    suspend fun refreshStores(): Result<Unit> {
        return try {
            val storeDtos = api.getStores()
            val entities = storeDtos.map { StoreEntity.fromDomain(it.toDomain()) }
            storeDao.upsertStores(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            // Load bundled sample data fallback
            loadStoresFromAssets()
        }
    }

    private suspend fun loadStoresFromAssets(): Result<Unit> {
        return try {
            val assetJson = context.assets.open("sampledata/ListOfStores.json").bufferedReader().use { it.readText() }
            val wrapper = json.decodeFromString<StoreListWrapper>(assetJson)
            val entities = wrapper.listOfStores.map { StoreEntity.fromDomain(it.toDomain()) }
            storeDao.upsertStores(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshDeals(): Result<Unit> {
        return try {
            val dealDtos = api.getDeals()
            val entities = dealDtos.map { DealEntity.fromDomain(it.toDomain()) }
            dealDao.upsertDeals(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            // Load bundled sample data fallback
            loadDealsFromAssets()
        }
    }

    private suspend fun loadDealsFromAssets(): Result<Unit> {
        return try {
            val assetJson = context.assets.open("sampledata/ListOfDeals.json").bufferedReader().use { it.readText() }
            val wrapper = json.decodeFromString<DealListWrapper>(assetJson)
            val entities = wrapper.listOfDeals.map { DealEntity.fromDomain(it.toDomain()) }
            dealDao.upsertDeals(entities)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchGames(query: String): Result<List<SearchResult>> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return Result.success(emptyList())

        return try {
            val results = api.searchGames(trimmed).map { it.toDomain() }
            Result.success(results)
        } catch (e: Exception) {
            // Fallback to sample search data
            try {
                val assetJson = context.assets.open("sampledata/SearchResults.json").bufferedReader().use { it.readText() }
                val wrapper = json.decodeFromString<SearchResultsWrapper>(assetJson)
                val filtered = wrapper.results
                    .map { it.toDomain() }
                    .filter { it.external.contains(trimmed, ignoreCase = true) }
                Result.success(filtered)
            } catch (fallbackError: Exception) {
                Result.failure(fallbackError)
            }
        }
    }

    suspend fun getDealDetails(dealID: String): Result<DealDetails> {
        return try {
            val dto = api.getDealDetails(dealID)
            Result.success(
                DealDetails(
                    gameInfo = dto.gameInfo.toDomain(),
                    cheapestPrice = dto.cheapestPrice?.toDomain(),
                ),
            )
        } catch (e: Exception) {
            // Fallback to sample Deal.json
            try {
                val assetJson = context.assets.open("sampledata/Deal.json").bufferedReader().use { it.readText() }
                val wrapper = json.decodeFromString<DealDetailsWrapper>(assetJson)
                Result.success(
                    DealDetails(
                        gameInfo = wrapper.deal.gameInfo.toDomain(),
                        cheapestPrice = wrapper.deal.cheapestPrice?.toDomain(),
                    ),
                )
            } catch (fallbackError: Exception) {
                Result.failure(fallbackError)
            }
        }
    }

    suspend fun getGameDetails(gameID: String): Result<GameDetails> {
        return try {
            val dto = api.getGameDetails(gameID)
            Result.success(dto.toDomain())
        } catch (e: Exception) {
            // Fallback to sample Game.json
            try {
                val assetJson = context.assets.open("sampledata/Game.json").bufferedReader().use { it.readText() }
                val wrapper = json.decodeFromString<GameDetailsWrapper>(assetJson)
                Result.success(wrapper.game.toDomain())
            } catch (fallbackError: Exception) {
                Result.failure(fallbackError)
            }
        }
    }

    suspend fun addFavoriteGroup(nickname: String) {
        val trimmed = nickname.trim()
        if (trimmed.isNotEmpty()) {
            favoriteDao.insertGroup(FavoriteGroupEntity(nickname = trimmed))
        }
    }

    suspend fun renameFavoriteGroup(oldNickname: String, newNickname: String) {
        val trimmed = newNickname.trim()
        if (trimmed.isNotEmpty()) {
            favoriteDao.renameGroup(oldNickname, trimmed)
        }
    }

    suspend fun deleteFavoriteGroup(nickname: String) {
        favoriteDao.deleteGroup(nickname)
    }

    suspend fun addFavoriteItem(groupNickname: String, gameID: String, name: String) {
        favoriteDao.insertGroup(FavoriteGroupEntity(groupNickname))
        favoriteDao.insertItem(
            FavoriteItemEntity(
                groupNickname = groupNickname,
                gameID = gameID,
                name = name,
            ),
        )
    }

    suspend fun removeFavoriteItem(id: Long) {
        favoriteDao.deleteItem(id)
    }

    suspend fun removeFavoriteByGameAndGroup(gameID: String, groupNickname: String) {
        favoriteDao.deleteItemByGameAndGroup(gameID, groupNickname)
    }

    fun isGameFavorited(gameID: String): Flow<Boolean> = favoriteDao.isGameFavorited(gameID)

    suspend fun initDefaultFavoritesIfEmpty() {
        favoriteDao.insertGroup(FavoriteGroupEntity(nickname = "Favorites"))
    }
}
