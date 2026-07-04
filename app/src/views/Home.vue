<script setup lang="ts">
import { ref, onMounted } from 'vue'
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

const descriptions = [
  'Powered by the CheapShark API',
  'This website allows you to find the best current deals on video games across many different websites',
  'Please use the search bar to find a game that you are interested in or take a look at the deals below'
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
        <h2 class="section-title">Latest PC Game Deals</h2>
        <div class="deals-grid">
          <DealTile
            v-for="deal in deals"
            :key="deal.dealID"
            :deal="deal"
            :storeData="storeData"
            @favorite="onFavorite"
          />
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

.section-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin-block-end: 1.5rem;
  letter-spacing: -0.01em;
  border-left: 3px solid var(--accent);
  padding-left: 0.75rem;
}

.deals-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
  justify-items: center;
  width: 100%;
}

@media (min-width: 768px) {
  .section-title {
    font-size: 1.75rem;
  }
  
  .deals-grid {
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  }
}
</style>
