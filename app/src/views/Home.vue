<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import TitleCard from '../components/TitleCard.vue'
import DealTile from '../components/DealTile.vue'
import FavoriteModal from '../components/FavoriteModal.vue'
import type { Deal, Store, FavoriteItem } from '../types'

import ListOfStores from '../sampledata/ListOfStores.json'
import ListOfDeals from '../sampledata/ListOfDeals.json'

const isLoading = ref(false)
const deals = ref<Deal[]>([])
const storeData = ref<Store[]>([])
const favModalRef = ref<InstanceType<typeof FavoriteModal> | null>(null)

// Filtering & Sorting State
const selectedStoreID = ref<string>('all')
const sortBy = ref<'dealRating' | 'savings' | 'price' | 'steam'>('dealRating')
const quickSearchText = ref<string>('')

const descriptions = [
  'Powered by the CheapShark API',
  'Find the best verified discounts on PC video games across multiple digital storefronts.',
  'Browse popular deals below or search for any game title in the catalog.'
]

const getStoreData = async () => {
  if (localStorage.storeData) {
    try {
      storeData.value = JSON.parse(localStorage.storeData)
      return
    } catch (e) {
      // Parse failed, fetch fresh
    }
  }

  try {
    const res = await fetch('https://www.cheapshark.com/api/1.0/stores')
    if (!res.ok) throw new Error('API returned non-200 status')
    const json = await res.json()
    storeData.value = json
    localStorage.storeData = JSON.stringify(storeData.value)
  } catch (err) {
    console.error('Store fetch failed, using fallback:', err)
    storeData.value = ListOfStores.listOfStores as unknown as Store[]
  }
}

const getListOfDeals = async () => {
  isLoading.value = true
  try {
    const res = await fetch('https://www.cheapshark.com/api/1.0/deals?onSale=1')
    if (!res.ok) throw new Error('API returned non-200 status')
    const json = await res.json()
    deals.value = json
  } catch (err) {
    console.error('Deals fetch failed, using fallback:', err)
    deals.value = ListOfDeals.listOfDeals as unknown as Deal[]
  } finally {
    isLoading.value = false
  }
}

const onFavorite = (item: FavoriteItem) => {
  favModalRef.value?.open(item)
}

// Extract stores that actually have deals currently
const activeStores = computed(() => {
  const storeIdSet = new Set(deals.value.map(d => d.storeID))
  return storeData.value.filter(s => storeIdSet.has(s.storeID))
})

// Filter and sort deals
const filteredDeals = computed(() => {
  let list = [...deals.value]

  // Filter by store
  if (selectedStoreID.value !== 'all') {
    list = list.filter(d => d.storeID === selectedStoreID.value)
  }

  // Filter by quick search text
  if (quickSearchText.value.trim()) {
    const q = quickSearchText.value.toLowerCase().trim()
    list = list.filter(d => d.title.toLowerCase().includes(q))
  }

  // Sort
  list.sort((a, b) => {
    if (sortBy.value === 'dealRating') {
      return parseFloat(b.dealRating) - parseFloat(a.dealRating)
    }
    if (sortBy.value === 'savings') {
      return parseFloat(b.savings) - parseFloat(a.savings)
    }
    if (sortBy.value === 'price') {
      return parseFloat(a.salePrice) - parseFloat(b.salePrice)
    }
    if (sortBy.value === 'steam') {
      const aScore = a.steamRatingPercent ? parseInt(a.steamRatingPercent) : 0
      const bScore = b.steamRatingPercent ? parseInt(b.steamRatingPercent) : 0
      return bScore - aScore
    }
    return 0
  })

  return list
})

const resetFilters = () => {
  selectedStoreID.value = 'all'
  quickSearchText.value = ''
  sortBy.value = 'dealRating'
}

onMounted(async () => {
  await getStoreData()
  await getListOfDeals()
})
</script>

<template>
  <div class="home-page">
    <div v-if="isLoading" class="loader">
      <div class="spinner"></div>
      <p>Fetching deals...</p>
    </div>
    
    <div v-else>
      <TitleCard
        title="GameDeals"
        :descriptions="descriptions"
        :showSearchButton="true"
        imgClass="main-img"
      />
      
      <main class="page-container">
        <!-- Controls & Filter Section -->
        <div class="filter-controls-card glass-card">
          <div class="filter-header-row">
            <h2 class="section-title">
              PC Game Deals
              <span class="deal-count-badge">{{ filteredDeals.length }} available</span>
            </h2>

            <div class="quick-filter-wrapper">
              <i class="fa fa-filter filter-icon"></i>
              <input
                v-model="quickSearchText"
                type="text"
                placeholder="Quick filter on page..."
                class="form-input quick-filter-input"
                aria-label="Filter deals on page"
              />
              <button
                v-if="quickSearchText"
                @click="quickSearchText = ''"
                class="clear-filter-btn"
                aria-label="Clear filter"
              >
                <i class="fa fa-times"></i>
              </button>
            </div>
          </div>

          <!-- Store Selection Horizontal Pills -->
          <div class="pills-section">
            <span class="pills-label">Store:</span>
            <div class="pills-scroll-strip" role="tablist">
              <button
                type="button"
                class="filter-pill-btn"
                :class="{ active: selectedStoreID === 'all' }"
                @click="selectedStoreID = 'all'"
              >
                All Stores
              </button>
              <button
                v-for="store in activeStores"
                :key="store.storeID"
                type="button"
                class="filter-pill-btn"
                :class="{ active: selectedStoreID === store.storeID }"
                @click="selectedStoreID = store.storeID"
              >
                <img
                  v-if="store.images?.icon"
                  :src="`https://www.cheapshark.com${store.images.icon}`"
                  class="store-pill-icon"
                  alt=""
                />
                {{ store.storeName }}
              </button>
            </div>
          </div>

          <!-- Sort Selection Pills -->
          <div class="pills-section sort-section">
            <span class="pills-label">Sort:</span>
            <div class="pills-scroll-strip">
              <button
                type="button"
                class="sort-pill-btn"
                :class="{ active: sortBy === 'dealRating' }"
                @click="sortBy = 'dealRating'"
              >
                ⭐ Top Deal Score
              </button>
              <button
                type="button"
                class="sort-pill-btn"
                :class="{ active: sortBy === 'savings' }"
                @click="sortBy = 'savings'"
              >
                🔥 Biggest Discount
              </button>
              <button
                type="button"
                class="sort-pill-btn"
                :class="{ active: sortBy === 'price' }"
                @click="sortBy = 'price'"
              >
                💵 Lowest Price
              </button>
              <button
                type="button"
                class="sort-pill-btn"
                :class="{ active: sortBy === 'steam' }"
                @click="sortBy = 'steam'"
              >
                👍 Steam Rating
              </button>
            </div>
          </div>
        </div>

        <!-- Deals Grid -->
        <div v-if="filteredDeals.length > 0" class="deals-grid">
          <DealTile
            v-for="deal in filteredDeals"
            :key="deal.dealID"
            :deal="deal"
            :storeData="storeData"
            @favorite="onFavorite"
          />
        </div>

        <!-- Empty Filter Results -->
        <div v-else class="empty-filter glass-card">
          <i class="fa fa-tag empty-icon"></i>
          <h3>No deals match your filter</h3>
          <p>Try clearing your search term or switching stores.</p>
          <button @click="resetFilters" class="btn-primary reset-btn">
            Reset Filters
          </button>
        </div>
      </main>
    </div>

    <!-- Reusable Favorite Modal -->
    <FavoriteModal ref="favModalRef" />
  </div>
</template>

<style scoped>
.home-page {
  width: 100%;
}

/* Filter Controls Card */
.filter-controls-card {
  padding: 1.15rem 1.25rem;
  margin-block-end: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

@media (min-width: 640px) {
  .filter-controls-card {
    padding: 1.35rem 1.5rem;
  }
}

.filter-header-row {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

@media (min-width: 700px) {
  .filter-header-row {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
  }
}

.section-title {
  font-size: 1.35rem;
  font-weight: 700;
  letter-spacing: -0.01em;
  border-left: 3px solid var(--accent);
  padding-left: 0.65rem;
  display: flex;
  align-items: center;
  gap: 0.65rem;
  flex-wrap: wrap;
}

@media (min-width: 768px) {
  .section-title {
    font-size: 1.65rem;
  }
}

.deal-count-badge {
  font-family: var(--font-body);
  font-size: 0.75rem;
  font-weight: 600;
  color: var(--accent);
  background: var(--accent-glow);
  border: 1px solid color-mix(in srgb, var(--accent) 25%, transparent);
  padding: 0.2rem 0.6rem;
  border-radius: 9999px;
  vertical-align: middle;
}

.quick-filter-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  max-width: 320px;
  width: 100%;
}

.filter-icon {
  position: absolute;
  left: 0.85rem;
  color: var(--text-muted);
  font-size: 0.85rem;
  pointer-events: none;
}

.quick-filter-input {
  padding-left: 2.25rem;
  padding-right: 2rem;
  height: 40px;
  min-height: 40px;
  font-size: 16px;
  background: color-mix(in srgb, var(--bg-primary) 60%, transparent);
}

.clear-filter-btn {
  position: absolute;
  right: 0.75rem;
  background: transparent;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  padding: 0.25rem;
  font-size: 0.85rem;
}

.clear-filter-btn:hover {
  color: var(--text-primary);
}

/* Horizontal Scrollable Pills */
.pills-section {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.pills-label {
  font-size: 0.775rem;
  font-weight: 700;
  text-transform: uppercase;
  color: var(--text-muted);
  letter-spacing: 0.05em;
  flex-shrink: 0;
}

.pills-scroll-strip {
  display: flex;
  gap: 0.4rem;
  overflow-x: auto;
  padding-bottom: 0.25rem;
  scroll-snap-type: x proximity;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
}

.pills-scroll-strip::-webkit-scrollbar {
  display: none;
}

.filter-pill-btn,
.sort-pill-btn {
  background: color-mix(in srgb, var(--bg-secondary) 70%, transparent);
  border: 1px solid var(--border-color);
  color: var(--text-secondary);
  padding: 0.4rem 0.8rem;
  border-radius: 9999px;
  font-size: 0.8rem;
  font-weight: 600;
  cursor: pointer;
  white-space: nowrap;
  scroll-snap-align: start;
  transition: all 0.2s ease;
  min-height: 38px;
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  touch-action: manipulation;
}

.filter-pill-btn:hover,
.sort-pill-btn:hover {
  border-color: var(--accent);
  color: var(--text-primary);
}

.filter-pill-btn.active,
.sort-pill-btn.active {
  background: var(--accent-solid);
  color: var(--on-accent);
  border-color: var(--accent-solid);
}

.store-pill-icon {
  width: 14px;
  height: 14px;
  object-fit: contain;
}

/* Deals Grid */
.deals-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(min(100%, 280px), 1fr));
  gap: 1.25rem;
  width: 100%;
}

@media (min-width: 768px) {
  .deals-grid {
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 1.5rem;
  }
}

/* Empty Filter Card */
.empty-filter {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 3rem 1.5rem;
  text-align: center;
  gap: 0.75rem;
  margin-top: 1rem;
}

.empty-icon {
  font-size: 2.5rem;
  color: var(--text-muted);
}

.empty-filter h3 {
  font-size: 1.25rem;
}

.empty-filter p {
  color: var(--text-secondary);
  font-size: 0.9rem;
}

.reset-btn {
  margin-top: 0.5rem;
}
</style>
