package online.codyuhi.gamedeals.domain

import kotlinx.serialization.Serializable

@Serializable
data class Store(
    val storeID: String,
    val storeName: String,
    val isActive: Int = 1,
    val imagesBanner: String = "",
    val imagesLogo: String = "",
    val imagesIcon: String = "",
) {
    val logoUrl: String
        get() = if (imagesLogo.startsWith("http")) imagesLogo else "https://www.cheapshark.com$imagesLogo"
    val iconUrl: String
        get() = if (imagesIcon.startsWith("http")) imagesIcon else "https://www.cheapshark.com$imagesIcon"
}

@Serializable
data class Deal(
    val dealID: String,
    val title: String,
    val storeID: String,
    val gameID: String,
    val salePrice: Double,
    val normalPrice: Double,
    val savings: Double,
    val dealRating: Double,
    val releaseDate: Long = 0L,
    val steamRatingText: String? = null,
    val steamRatingPercent: Int = 0,
    val steamRatingCount: Long = 0L,
    val metacriticScore: Int = 0,
    val metacriticLink: String? = null,
    val thumb: String = "",
)

@Serializable
data class DealGameInfo(
    val storeID: String,
    val gameID: String,
    val name: String,
    val steamAppID: String? = null,
    val salePrice: Double,
    val retailPrice: Double,
    val steamRatingText: String? = null,
    val steamRatingPercent: Int = 0,
    val steamRatingCount: Long = 0L,
    val metacriticScore: Int = 0,
    val metacriticLink: String = "",
    val releaseDate: Long = 0L,
    val publisher: String = "",
    val steamWorks: String? = null,
    val thumb: String = "",
)

@Serializable
data class CheapestPrice(
    val price: Double,
    val date: Long,
)

@Serializable
data class CheaperStore(
    val dealID: String,
    val storeID: String,
    val salePrice: Double,
    val retailPrice: Double,
)

@Serializable
data class DealDetails(
    val gameInfo: DealGameInfo,
    val cheapestPrice: CheapestPrice? = null,
    val cheaperStores: List<CheaperStore> = emptyList(),
)

@Serializable
data class GameInfo(
    val title: String,
    val steamAppID: String? = null,
    val thumb: String = "",
)

@Serializable
data class GameDeal(
    val storeID: String,
    val dealID: String,
    val price: Double,
    val retailPrice: Double,
    val savings: Double,
)

@Serializable
data class GameDetails(
    val info: GameInfo,
    val cheapestPriceEver: CheapestPrice? = null,
    val deals: List<GameDeal> = emptyList(),
)

@Serializable
data class SearchResult(
    val gameID: String,
    val steamAppID: String? = null,
    val cheapest: Double,
    val cheapestDealID: String,
    val external: String,
    val thumb: String = "",
)

@Serializable
data class FavoriteItem(
    val id: Long = 0L,
    val groupNickname: String,
    val gameID: String,
    val name: String,
    val addedAt: Long = System.currentTimeMillis(),
)

@Serializable
data class FavoriteGroup(
    val nickname: String,
    val items: List<FavoriteItem> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
)

fun formatPrice(amount: Double): String = String.format(java.util.Locale.US, "%.2f", amount)
fun formatScore(score: Double): String = String.format(java.util.Locale.US, "%.1f", score)
fun formatDate(timestampSeconds: Long): String =
    java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.US).format(java.util.Date(timestampSeconds * 1000L))
