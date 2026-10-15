package online.codyuhi.gamedeals.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        StoreEntity::class,
        DealEntity::class,
        FavoriteGroupEntity::class,
        FavoriteItemEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class GameDealsDatabase : RoomDatabase() {
    abstract fun storeDao(): StoreDao
    abstract fun dealDao(): DealDao
    abstract fun favoriteDao(): FavoriteDao
}
