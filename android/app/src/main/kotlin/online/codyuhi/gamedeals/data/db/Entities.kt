package online.codyuhi.gamedeals.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import online.codyuhi.gamedeals.domain.Deal
import online.codyuhi.gamedeals.domain.FavoriteItem
import online.codyuhi.gamedeals.domain.Store

@Entity(tableName = "stores")
data class StoreEntity(
    @PrimaryKey val storeID: String,
    val storeName: String,
    val isActive: Int,
    val imagesBanner: String,
    val imagesLogo: String,
    val imagesIcon: String,
) {
    fun toDomain(): Store = Store(
        storeID = storeID,
        storeName = storeName,
        isActive = isActive,
        imagesBanner = imagesBanner,
        imagesLogo = imagesLogo,
        imagesIcon = imagesIcon,
    )

    companion object {
        fun fromDomain(store: Store): StoreEntity = StoreEntity(
            storeID = store.storeID,
            storeName = store.storeName,
            isActive = store.isActive,
            imagesBanner = store.imagesBanner,
            imagesLogo = store.imagesLogo,
            imagesIcon = store.imagesIcon,
        )
    }
}

@Entity(tableName = "deals")
data class DealEntity(
    @PrimaryKey val dealID: String,
    val title: String,
    val storeID: String,
    val gameID: String,
    val salePrice: Double,
    val normalPrice: Double,
    val savings: Double,
    val dealRating: Double,
    val releaseDate: Long,
    val steamRatingText: String?,
    val steamRatingPercent: Int,
    val steamRatingCount: Long,
    val metacriticScore: Int,
    val metacriticLink: String?,
    val thumb: String,
    val fetchedAt: Long = System.currentTimeMillis(),
) {
    fun toDomain(): Deal = Deal(
        dealID = dealID,
        title = title,
        storeID = storeID,
        gameID = gameID,
        salePrice = salePrice,
        normalPrice = normalPrice,
        savings = savings,
        dealRating = dealRating,
        releaseDate = releaseDate,
        steamRatingText = steamRatingText,
        steamRatingPercent = steamRatingPercent,
        steamRatingCount = steamRatingCount,
        metacriticScore = metacriticScore,
        metacriticLink = metacriticLink,
        thumb = thumb,
    )

    companion object {
        fun fromDomain(deal: Deal): DealEntity = DealEntity(
            dealID = deal.dealID,
            title = deal.title,
            storeID = deal.storeID,
            gameID = deal.gameID,
            salePrice = deal.salePrice,
            normalPrice = deal.normalPrice,
            savings = deal.savings,
            dealRating = deal.dealRating,
            releaseDate = deal.releaseDate,
            steamRatingText = deal.steamRatingText,
            steamRatingPercent = deal.steamRatingPercent,
            steamRatingCount = deal.steamRatingCount,
            metacriticScore = deal.metacriticScore,
            metacriticLink = deal.metacriticLink,
            thumb = deal.thumb,
        )
    }
}

@Entity(tableName = "favorite_groups")
data class FavoriteGroupEntity(
    @PrimaryKey val nickname: String,
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(
    tableName = "favorite_items",
    indices = [
        Index("groupNickname"),
        Index("gameID"),
    ],
)
data class FavoriteItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val groupNickname: String,
    val gameID: String,
    val name: String,
    val addedAt: Long = System.currentTimeMillis(),
) {
    fun toDomain(): FavoriteItem = FavoriteItem(
        id = id,
        groupNickname = groupNickname,
        gameID = gameID,
        name = name,
        addedAt = addedAt,
    )
}
