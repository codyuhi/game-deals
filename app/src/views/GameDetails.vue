<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import TitleCard from '../components/TitleCard.vue'
import FavoriteModal from '../components/FavoriteModal.vue'
import type { GameDetailsData, Store } from '../types'

import ListOfStores from '../sampledata/ListOfStores.json'
import GameFallback from '../sampledata/Game.json'

const route = useRoute()
const isLoading = ref(false)
const game = ref<GameDetailsData | null>(null)
const storeData = ref<Store[]>([])
const favModalRef = ref<InstanceType<typeof FavoriteModal> | null>(null)

const gameId = computed(() => route.params.gameid as string)

const descriptions = computed(() => {
  return game.value ? [`More information about ${game.value.info.title}`] : []
})

const getStoreData = async () => {
  if (localStorage.storeData) {
    try {
      storeData.value = JSON.parse(localStorage.storeData)
      return
    } catch (e) {
      // ignore
    }
  }

  try {
    const res = await fetch('https://www.cheapshark.com/api/1.0/stores')
    if (!res.ok) throw new Error()
    const json = await res.json()
    storeData.value = json
    localStorage.storeData = JSON.stringify(storeData.value)
  } catch (err) {
    storeData.value = ListOfStores.listOfStores as unknown as Store[]
  }
}

const getGameData = async () => {
  if (!gameId.value) return
  isLoading.value = true

  try {
    const res = await fetch(`https://www.cheapshark.com/api/1.0/games?id=${gameId.value}`)
    if (!res.ok) throw new Error()
    const json = await res.json()
    game.value = json
  } catch (err) {
    console.error('Game data fetch failed, using fallback:', err)
    game.value = GameFallback.game as unknown as GameDetailsData
  } finally {
    isLoading.value = false
  }
}

const convertDate = (unixDate: number) => {
  if (!unixDate) return 'N/A'
  const format: Intl.DateTimeFormatOptions = {
    day: 'numeric',
    month: 'short',
    year: 'numeric'
  }
  return new Date(unixDate * 1000).toLocaleString('en-US', format)
}

const getStoreName = (storeId: string) => {
  const store = storeData.value.find(s => s.storeID === storeId)
  return store ? store.storeName : 'Anonymous store'
}

const getStoreLogoUrl = (storeId: string) => {
  const store = storeData.value.find(s => s.storeID === storeId)
  if (store && store.images.logo) {
    return `https://www.cheapshark.com${store.images.logo}`
  }
  return undefined
}

const openDealer = (dealId: string) => {
  window.open(`https://www.cheapshark.com/redirect?dealID=${dealId}`, '_blank')
}

const triggerFavorite = () => {
  if (!game.value || !gameId.value) return
  favModalRef.value?.open({
    name: game.value.info.title,
    id: gameId.value
  })
}

onMounted(async () => {
  await getStoreData()
  await getGameData()
})
</script>

<template>
  <div class="game-details-page">
    <div v-if="isLoading" class="loader">
      <div class="spinner"></div>
      <p>Loading game details...</p>
    </div>

    <div v-else-if="game">
      <TitleCard
        title="Game Details"
        :descriptions="descriptions"
        :showSearchButton="false"
        imgClass="games-img"
      />

      <main class="page-container">
        <!-- Hero Details Card -->
        <section class="game-hero-section glass-card">
          <div class="game-cover-wrapper">
            <img
              v-if="game.info.thumb"
              :src="game.info.thumb"
              :alt="'Cover art for ' + game.info.title"
              class="game-cover-img"
            />
          </div>
          
          <div class="game-hero-info">
            <h2 class="game-title">{{ game.info.title }}</h2>
            
            <div class="cheapest-ever-box">
              <span class="box-label">All-Time Lowest Price</span>
              <div class="price-row">
                <span class="price-val">${{ game.cheapestPriceEver.price }}</span>
                <span class="date-val" v-if="game.cheapestPriceEver.date">
                  on {{ convertDate(game.cheapestPriceEver.date) }}
                </span>
              </div>
            </div>

            <button @click="triggerFavorite" class="btn-secondary fav-add-btn">
              <i class="fa fa-heart"></i> Add to Favorites
            </button>
          </div>
        </section>

        <!-- Deals Listing Section -->
        <section class="deals-section">
          <h2 class="section-title">Active Deals for {{ game.info.title }}</h2>
          
          <div v-if="game.deals.length === 0" class="no-deals glass-card">
            <i class="fa fa-tag empty-icon"></i>
            <p>There are currently no deals available for this game.</p>
          </div>

          <div v-else class="deals-table-wrapper glass-card">
            <div class="deals-header">
              <div class="hdr-store">Store</div>
              <div class="hdr-price text-right">Sale Price</div>
              <div class="hdr-retail text-right">Retail Price</div>
              <div class="hdr-savings text-right">Savings</div>
              <div class="hdr-action"></div>
            </div>
            
            <div class="deals-list">
              <div v-for="deal in game.deals" :key="deal.dealID" class="deal-row">
                <div class="col-store">
                  <img
                    v-if="getStoreLogoUrl(deal.storeID)"
                    :src="getStoreLogoUrl(deal.storeID)"
                    :alt="getStoreName(deal.storeID) + ' logo'"
                    class="store-logo-img"
                    loading="lazy"
                  />
                  <span class="store-name">{{ getStoreName(deal.storeID) }}</span>
                </div>
                
                <div class="col-price text-right">
                  <span class="price-tag">${{ deal.price }}</span>
                </div>
                
                <div class="col-retail text-right">
                  <span class="retail-tag">${{ deal.retailPrice }}</span>
                </div>
                
                <div class="col-savings text-right">
                  <span class="badge badge-savings">
                    -{{ parseFloat(deal.savings).toFixed(0) }}%
                  </span>
                </div>
                
                <div class="col-action">
                  <button @click="openDealer(deal.dealID)" class="btn-primary buy-btn">
                    Buy <i class="fa fa-external-link-alt"></i>
                  </button>
                </div>
              </div>
            </div>
          </div>
        </section>
      </main>
    </div>

    <!-- Reusable Favorite Modal -->
    <FavoriteModal ref="favModalRef" />
  </div>
</template>

<style scoped>
.game-details-page {
  width: 100%;
}

.game-hero-section {
  display: flex;
  flex-direction: column;
  gap: 2rem;
  padding: 2rem;
  margin-block-end: 2.5rem;
}

.game-cover-wrapper {
  width: 100%;
  max-width: 320px;
  aspect-ratio: 16 / 9;
  border-radius: 8px;
  overflow: hidden;
  background: var(--bg-inset);
  align-self: center;
  border: 1px solid var(--border-color);
}

.game-cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.game-hero-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 1.5rem;
  flex-grow: 1;
}

.game-title {
  font-size: clamp(1.75rem, 3vw + 0.5rem, 2.25rem);
  font-weight: 700;
  letter-spacing: -0.02em;
}

.cheapest-ever-box {
  background: color-mix(in srgb, var(--text-primary) 2%, transparent);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 1rem 1.25rem;
  width: fit-content;
  min-width: 250px;
}

.box-label {
  font-size: 0.75rem;
  text-transform: uppercase;
  color: var(--text-muted);
  letter-spacing: 0.05em;
  font-weight: 600;
  margin-block-end: 0.25rem;
  display: block;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 0.75rem;
}

.price-val {
  font-size: 1.6rem;
  font-weight: 700;
  color: var(--accent);
}

.date-val {
  font-size: 0.85rem;
  color: var(--text-secondary);
}

.fav-add-btn {
  align-self: flex-start;
  font-size: 0.9rem;
  
  i {
    transition: transform 0.2s ease;
  }
  
  &:hover i {
    color: var(--danger-solid);
    transform: scale(1.1);
  }
}

.section-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin-block-end: 1.5rem;
  letter-spacing: -0.01em;
  border-left: 3px solid var(--accent);
  padding-left: 0.75rem;
}

.no-deals {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem 1rem;
  color: var(--text-secondary);
  gap: 0.75rem;

  .empty-icon {
    font-size: 2.5rem;
    color: var(--text-muted);
  }
}

/* Custom Table Layout */
.deals-table-wrapper {
  overflow-x: auto;
  border: 1px solid var(--border-color);
}

.deals-header {
  display: grid;
  grid-template-columns: 2.5fr 1fr 1fr 1fr 1fr;
  align-items: center;
  padding: 1rem 1.5rem;
  border-block-end: 1px solid var(--border-color);
  font-family: var(--font-heading);
  font-size: 0.8rem;
  font-weight: 600;
  text-transform: uppercase;
  color: var(--text-secondary);
  letter-spacing: 0.05em;
  min-width: 600px;
}

.deal-row {
  display: grid;
  grid-template-columns: 2.5fr 1fr 1fr 1fr 1fr;
  align-items: center;
  padding: 1rem 1.5rem;
  border-block-end: 1px solid var(--border-color);
  min-width: 600px;
  
  &:last-child {
    border-block-end: none;
  }

  &:hover {
    background: color-mix(in srgb, var(--text-primary) 1%, transparent);
  }
}

.col-store {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.store-logo-img {
  width: 120px;
  height: 32px;
  object-fit: contain;
  filter: brightness(0.9) contrast(1.1);
}

.store-name {
  font-weight: 500;
  font-size: 0.95rem;
}

.price-tag {
  color: var(--accent);
  font-weight: 700;
  font-size: 1.1rem;
}

.retail-tag {
  color: var(--text-muted);
  text-decoration: line-through;
  font-size: 0.95rem;
}

.buy-btn {
  padding: 0.35rem 0.85rem;
  font-size: 0.8rem;
  border-radius: 6px;
  width: fit-content;
}

.text-right {
  text-align: right;
  padding-right: 1.5rem;
}

@media (min-width: 768px) {
  .game-hero-section {
    flex-direction: row;
  }
  
  .game-cover-wrapper {
    max-width: 240px;
    align-self: auto;
  }
}
</style>
