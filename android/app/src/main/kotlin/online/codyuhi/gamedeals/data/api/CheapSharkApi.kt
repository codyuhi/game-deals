package online.codyuhi.gamedeals.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface CheapSharkApi {
    @GET("stores")
    suspend fun getStores(): List<StoreDto>

    @GET("deals?onSale=1")
    suspend fun getDeals(
        @Query("storeID") storeID: String? = null,
        @Query("sortBy") sortBy: String? = null,
        @Query("title") title: String? = null,
        @Query("pageNumber") pageNumber: Int? = null,
    ): List<DealDto>

    @GET("deals")
    suspend fun getDealDetails(
        @Query("id") dealID: String,
    ): DealDetailsDto

    @GET("games")
    suspend fun getGameDetails(
        @Query("id") gameID: String,
    ): GameDetailsDto

    @GET("games")
    suspend fun searchGames(
        @Query("title") title: String,
    ): List<SearchResultDto>
}
