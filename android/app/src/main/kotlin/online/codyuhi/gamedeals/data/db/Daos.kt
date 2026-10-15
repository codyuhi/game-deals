package online.codyuhi.gamedeals.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {
    @Query("SELECT * FROM stores ORDER BY storeName ASC")
    fun getAllStores(): Flow<List<StoreEntity>>

    @Query("SELECT * FROM stores WHERE isActive = 1 ORDER BY storeName ASC")
    fun getActiveStores(): Flow<List<StoreEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStores(stores: List<StoreEntity>)
}

@Dao
interface DealDao {
    @Query("SELECT * FROM deals ORDER BY dealRating DESC")
    fun getAllDeals(): Flow<List<DealEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDeals(deals: List<DealEntity>)

    @Query("DELETE FROM deals")
    suspend fun clearAllDeals()
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorite_groups ORDER BY createdAt ASC")
    fun getAllGroups(): Flow<List<FavoriteGroupEntity>>

    @Query("SELECT * FROM favorite_items ORDER BY addedAt DESC")
    fun getAllItems(): Flow<List<FavoriteItemEntity>>

    @Query("SELECT * FROM favorite_items WHERE groupNickname = :groupNickname ORDER BY addedAt DESC")
    fun getItemsForGroup(groupNickname: String): Flow<List<FavoriteItemEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGroup(group: FavoriteGroupEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: FavoriteItemEntity): Long

    @Query("UPDATE favorite_groups SET nickname = :newNickname WHERE nickname = :oldNickname")
    suspend fun updateGroupNickname(oldNickname: String, newNickname: String)

    @Query("UPDATE favorite_items SET groupNickname = :newNickname WHERE groupNickname = :oldNickname")
    suspend fun updateItemsGroupNickname(oldNickname: String, newNickname: String)

    @Transaction
    suspend fun renameGroup(oldNickname: String, newNickname: String) {
        insertGroup(FavoriteGroupEntity(newNickname))
        updateItemsGroupNickname(oldNickname, newNickname)
        deleteGroupOnly(oldNickname)
    }

    @Query("DELETE FROM favorite_groups WHERE nickname = :nickname")
    suspend fun deleteGroupOnly(nickname: String)

    @Query("DELETE FROM favorite_items WHERE groupNickname = :nickname")
    suspend fun deleteItemsForGroup(nickname: String)

    @Transaction
    suspend fun deleteGroup(nickname: String) {
        deleteItemsForGroup(nickname)
        deleteGroupOnly(nickname)
    }

    @Query("DELETE FROM favorite_items WHERE id = :id")
    suspend fun deleteItem(id: Long)

    @Query("DELETE FROM favorite_items WHERE gameID = :gameID AND groupNickname = :groupNickname")
    suspend fun deleteItemByGameAndGroup(gameID: String, groupNickname: String)

    @Query("SELECT COUNT(*) > 0 FROM favorite_items WHERE gameID = :gameID")
    fun isGameFavorited(gameID: String): Flow<Boolean>
}
