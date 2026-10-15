package online.codyuhi.gamedeals

import kotlinx.serialization.json.Json
import online.codyuhi.gamedeals.data.api.DealDto
import online.codyuhi.gamedeals.data.api.DealListWrapper
import online.codyuhi.gamedeals.data.api.StoreDto
import online.codyuhi.gamedeals.data.api.StoreListWrapper
import online.codyuhi.gamedeals.domain.Deal
import online.codyuhi.gamedeals.ui.DealSort
import online.codyuhi.gamedeals.ui.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameDealsUnitTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    @Test
    fun dealDto_mapsCorrectlyToDomain() {
        val dto = DealDto(
            dealID = "deal123",
            title = "Test Game",
            storeID = "1",
            gameID = "999",
            salePrice = "9.99",
            normalPrice = "39.99",
            savings = "75.018755",
            dealRating = "9.5",
            steamRatingText = "Very Positive",
            steamRatingPercent = "92",
            steamRatingCount = "1500",
            metacriticScore = "88",
            thumb = "https://example.com/thumb.jpg",
        )

        val deal = dto.toDomain()
        assertEquals("deal123", deal.dealID)
        assertEquals("Test Game", deal.title)
        assertEquals("1", deal.storeID)
        assertEquals("999", deal.gameID)
        assertEquals(9.99, deal.salePrice, 0.001)
        assertEquals(39.99, deal.normalPrice, 0.001)
        assertEquals(75.018755, deal.savings, 0.001)
        assertEquals(9.5, deal.dealRating, 0.001)
        assertEquals("Very Positive", deal.steamRatingText)
        assertEquals(92, deal.steamRatingPercent)
        assertEquals(1500L, deal.steamRatingCount)
        assertEquals(88, deal.metacriticScore)
    }

    @Test
    fun filteredDeals_filtersByStoreAndSortsBySavings() {
        val dealA = Deal(
            dealID = "1",
            title = "Alpha Game",
            storeID = "1",
            gameID = "10",
            salePrice = 5.0,
            normalPrice = 20.0,
            savings = 75.0,
            dealRating = 8.0,
        )
        val dealB = Deal(
            dealID = "2",
            title = "Beta Game",
            storeID = "2",
            gameID = "20",
            salePrice = 10.0,
            normalPrice = 20.0,
            savings = 50.0,
            dealRating = 9.0,
        )
        val dealC = Deal(
            dealID = "3",
            title = "Gamma Deal",
            storeID = "1",
            gameID = "30",
            salePrice = 2.0,
            normalPrice = 20.0,
            savings = 90.0,
            dealRating = 7.0,
        )

        val state = MainUiState(
            deals = listOf(dealA, dealB, dealC),
            selectedStoreID = "1",
            sortBy = DealSort.Savings,
        )

        val filtered = state.filteredDeals
        assertEquals(2, filtered.size)
        assertEquals("3", filtered[0].dealID) // 90% savings
        assertEquals("1", filtered[1].dealID) // 75% savings
    }

    @Test
    fun filteredDeals_quickSearchFiltersByTitle() {
        val dealA = Deal(
            dealID = "1",
            title = "The Witcher 3: Wild Hunt",
            storeID = "1",
            gameID = "10",
            salePrice = 9.99,
            normalPrice = 39.99,
            savings = 75.0,
            dealRating = 9.8,
        )
        val dealB = Deal(
            dealID = "2",
            title = "Cyberpunk 2077",
            storeID = "1",
            gameID = "20",
            salePrice = 29.99,
            normalPrice = 59.99,
            savings = 50.0,
            dealRating = 8.5,
        )

        val state = MainUiState(
            deals = listOf(dealA, dealB),
            selectedStoreID = "all",
            quickSearchQuery = "witcher",
        )

        val filtered = state.filteredDeals
        assertEquals(1, filtered.size)
        assertEquals("The Witcher 3: Wild Hunt", filtered.first().title)
    }

    @Test
    fun storeListWrapper_deserializesValidJson() {
        val sampleJson = """
            {
                "listOfStores": [
                    {
                        "storeID": "1",
                        "storeName": "Steam",
                        "isActive": 1,
                        "images": {
                            "banner": "/img/stores/banners/0.png",
                            "logo": "/img/stores/logos/0.png",
                            "icon": "/img/stores/icons/0.png"
                        }
                    }
                ]
            }
        """.trimIndent()

        val wrapper = json.decodeFromString<StoreListWrapper>(sampleJson)
        assertEquals(1, wrapper.listOfStores.size)
        val store = wrapper.listOfStores.first().toDomain()
        assertEquals("1", store.storeID)
        assertEquals("Steam", store.storeName)
        assertEquals(1, store.isActive)
        assertEquals("https://www.cheapshark.com/img/stores/logos/0.png", store.logoUrl)
    }
}
