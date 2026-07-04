<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import TitleCard from '../components/TitleCard.vue'
import SearchTile from '../components/SearchTile.vue'
import type { SearchResult } from '../types'

import SearchResultsFallback from '../sampledata/SearchResults.json'

const route = useRoute()
const isLoading = ref(false)
const searchResults = ref<SearchResult[]>([])
const searchString = ref('')

const getSearchResults = async () => {
  const query = route.params.searchString as string
  if (!query) return

  searchString.value = decodeURIComponent(query)
  isLoading.value = true
  
  try {
    const res = await fetch(`https://www.cheapshark.com/api/1.0/games?title=${encodeURIComponent(query)}`)
    if (!res.ok) throw new Error('API returned non-200 status')
    const json = await res.json()
    searchResults.value = json
  } catch (err) {
    console.error('Search fetch failed, using fallback:', err)
    searchResults.value = SearchResultsFallback.results as unknown as SearchResult[]
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  getSearchResults()
})

// Watch route params to update search results when searching again from the nav bar
watch(
  () => route.params.searchString,
  () => {
    getSearchResults()
  }
)
</script>

<template>
  <div class="search-results-page">
    <div v-if="isLoading" class="loader">
      <div class="spinner"></div>
      <p>Searching deals...</p>
    </div>

    <div v-else>
      <TitleCard
        title="Search Results"
        :descriptions="['Search Results for &quot;' + searchString + '&quot;']"
        :showSearchButton="true"
        imgClass="search-img"
      />

      <main class="page-container">
        <div v-if="searchResults.length < 1" class="empty-results glass-card">
          <i class="fa fa-search-minus empty-icon"></i>
          <h2>No games found for "{{ searchString }}"</h2>
          <p>Please double check the spelling or try searching for another title.</p>
        </div>

        <div v-else class="results-list">
          <SearchTile
            v-for="result in searchResults"
            :key="result.gameID"
            :game="result"
          />
        </div>
      </main>
    </div>
  </div>
</template>

<style scoped>
.search-results-page {
  width: 100%;
}

.empty-results {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 4rem 2rem;
  max-width: 600px;
  margin-inline: auto;
  gap: 1rem;

  h2 {
    font-size: 1.5rem;
    font-weight: 600;
  }

  p {
    color: var(--text-secondary);
  }
}

.empty-icon {
  font-size: 3rem;
  color: var(--text-muted);
}

.results-list {
  max-width: 800px;
  margin-inline: auto;
}
</style>
