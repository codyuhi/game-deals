package online.codyuhi.gamedeals.data.api

import kotlinx.serialization.Serializable
import online.codyuhi.gamedeals.domain.CheapestPrice
import online.codyuhi.gamedeals.domain.Deal
import online.codyuhi.gamedeals.domain.DealGameInfo
import online.codyuhi.gamedeals.domain.GameDeal
import online.codyuhi.gamedeals.domain.GameDetails
import online.codyuhi.gamedeals.domain.GameInfo
import online.codyuhi.gamedeals.domain.SearchResult
import online.codyuhi.gamedeals.domain.Store

@Serializable
data class StoreImagesDto(
    val banner: String = "",
    val logo: String = "",
    val icon: String = "",
)

@Serializable
data class StoreDto(
    val storeID: String,
    val storeName: String,
    val isActive: Int = 1,
    val images: StoreImagesDto = StoreImagesDto(),
) {
    fun toDomain(): Store = Store(
        storeID = storeID,
        storeName = storeName,
        isActive = isActive,
        imagesBanner = images.banner,
        imagesLogo = images.logo,
        imagesIcon = images.icon,
    )
}

@Serializable
data class StoreListWrapper(
    val listOfStores: List<StoreDto> = emptyList(),
)

@Serializable
data class DealDto(
    val dealID: String,
    val title: String = "",
    val storeID: String = "",
    val gameID: String = "",
    val salePrice: String = "0.0",
    val normalPrice: String = "0.0",
    val savings: String = "0.0",
    val dealRating: String = "0.0",
    val releaseDate: Long = 0L,
    val steamRatingText: String? = null,
    val steamRatingPercent: String = "0",
    val steamRatingCount: String = "0",
    val metacriticScore: String = "0",
    val metacriticLink: String? = null,
    val thumb: String = "",
) {
    fun toDomain(): Deal = Deal(
        dealID = dealID,
        title = title,
        storeID = storeID,
        gameID = gameID,
        salePrice = salePrice.toDoubleOrNull() ?: 0.0,
        normalPrice = normalPrice.toDoubleOrNull() ?: 0.0,
        savings = savings.toDoubleOrNull() ?: 0.0,
        dealRating = dealRating.toDoubleOrNull() ?: 0.0,
        releaseDate = releaseDate,
        steamRatingText = steamRatingText,
        steamRatingPercent = steamRatingPercent.toIntOrNull() ?: 0,
        steamRatingCount = steamRatingCount.toLongOrNull() ?: 0L,
        metacriticScore = metacriticScore.toIntOrNull() ?: 0,
        metacriticLink = metacriticLink,
        thumb = thumb,
    )
}

@Serializable
data class DealListWrapper(
    val listOfDeals: List<DealDto> = emptyList(),
)

@Serializable
data class DealGameInfoDto(
    val storeID: String = "",
    val gameID: String = "",
    val name: String = "",
    val steamAppID: String? = null,
    val salePrice: String = "0.0",
    val retailPrice: String = "0.0",
    val steamRatingText: String? = null,
    val steamRatingPercent: String = "0",
    val steamRatingCount: String = "0",
    val metacriticScore: String = "0",
    val metacriticLink: String = "",
    val releaseDate: Long = 0L,
    val publisher: String = "",
    val steamworks: String? = null,
    val thumb: String = "",
) {
    fun toDomain(): DealGameInfo = DealGameInfo(
        storeID = storeID,
        gameID = gameID,
        name = name,
        steamAppID = steamAppID,
        salePrice = salePrice.toDoubleOrNull() ?: 0.0,
        retailPrice = retailPrice.toDoubleOrNull() ?: 0.0,
        steamRatingText = steamRatingText,
        steamRatingPercent = steamRatingPercent.toIntOrNull() ?: 0,
        steamRatingCount = steamRatingCount.toLongOrNull() ?: 0L,
        metacriticScore = metacriticScore.toIntOrNull() ?: 0,
        metacriticLink = metacriticLink,
        releaseDate = releaseDate,
        publisher = publisher,
        steamWorks = steamworks,
        thumb = thumb,
    )
}

@Serializable
data class CheapestPriceDto(
    val price: String = "0.0",
    val date: Long = 0L,
) {
    fun toDomain(): CheapestPrice = CheapestPrice(
        price = price.toDoubleOrNull() ?: 0.0,
        date = date,
    )
}

@Serializable
data class DealDetailsDto(
    val gameInfo: DealGameInfoDto = DealGameInfoDto(),
    val cheapestPrice: CheapestPriceDto? = null,
)

@Serializable
data class DealDetailsWrapper(
    val deal: DealDetailsDto,
)

@Serializable
data class GameInfoDto(
    val title: String = "",
    val steamAppID: String? = null,
    val thumb: String = "",
)

@Serializable
data class GameDealDto(
    val storeID: String = "",
    val dealID: String = "",
    val price: String = "0.0",
    val retailPrice: String = "0.0",
    val savings: String = "0.0",
) {
    fun toDomain(): GameDeal = GameDeal(
        storeID = storeID,
        dealID = dealID,
        price = price.toDoubleOrNull() ?: 0.0,
        retailPrice = retailPrice.toDoubleOrNull() ?: 0.0,
        savings = savings.toDoubleOrNull() ?: 0.0,
    )
}

@Serializable
data class GameDetailsDto(
    val info: GameInfoDto = GameInfoDto(),
    val cheapestPriceEver: CheapestPriceDto? = null,
    val deals: List<GameDealDto> = emptyList(),
) {
    fun toDomain(): GameDetails = GameDetails(
        info = GameInfo(
            title = info.title,
            steamAppID = info.steamAppID,
            thumb = info.thumb,
        ),
        cheapestPriceEver = cheapestPriceEver?.toDomain(),
        deals = deals.map { it.toDomain() },
    )
}

@Serializable
data class GameDetailsWrapper(
    val game: GameDetailsDto,
)

@Serializable
data class SearchResultDto(
    val gameID: String,
    val steamAppID: String? = null,
    val cheapest: String = "0.0",
    val cheapestDealID: String = "",
    val external: String = "",
    val thumb: String = "",
) {
    fun toDomain(): SearchResult = SearchResult(
        gameID = gameID,
        steamAppID = steamAppID,
        cheapest = cheapest.toDoubleOrNull() ?: 0.0,
        cheapestDealID = cheapestDealID,
        external = external,
        thumb = thumb,
    )
}

@Serializable
data class SearchResultsWrapper(
    val results: List<SearchResultDto> = emptyList(),
)
