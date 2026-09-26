<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import TitleCard from '../components/TitleCard.vue'
import type { DealDetailsData, Store } from '../types'

import ListOfStores from '../sampledata/ListOfStores.json'
import DealFallback from '../sampledata/Deal.json'

const route = useRoute()
const isLoading = ref(false)
const deal = ref<DealDetailsData | null>(null)
const storeData = ref<Store[]>([])

const dealId = computed(() => route.params.dealid as string)

const descriptions = computed(() => {
  return deal.value && deal.value.gameInfo
    ? [`More information about the deal for ${deal.value.gameInfo.name} on ${getStoreName(deal.value.gameInfo.storeID)}`]
    : []
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

const getDealData = async () => {
  if (!dealId.value) return
  isLoading.value = true

  try {
    const res = await fetch(`https://www.cheapshark.com/api/1.0/deals?id=${dealId.value}`)
    if (!res.ok) throw new Error()
    const json = await res.json()
    deal.value = json
  } catch (err) {
    console.error('Deal data fetch failed, using fallback:', err)
    deal.value = DealFallback.deal as unknown as DealDetailsData
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

const getStoreIconUrl = (storeId: string) => {
  const store = storeData.value.find(s => s.storeID === storeId)
  if (store && store.images.icon) {
    return `https://www.cheapshark.com${store.images.icon}`
  }
  return undefined
}

const openDealer = () => {
  if (!dealId.value) return
  window.open(`https://www.cheapshark.com/redirect?dealID=${dealId.value}`, '_blank')
}

onMounted(async () => {
  await getStoreData()
  await getDealData()
})
</script>

<template>
  <div class="deal-details-page">
    <div v-if="isLoading" class="loader">
      <div class="spinner"></div>
      <p>Loading deal details...</p>
    </div>

    <div v-else-if="deal && deal.gameInfo">
      <TitleCard
        title="Deal Details"
        :descriptions="descriptions"
        :showSearchButton="false"
        imgClass="deals-img"
      />

      <main class="page-container">
        <div class="deal-layout">
          <!-- Left Column: Cover & Main Actions -->
          <div class="left-col">
            <div class="cover-card glass-card">
              <img
                :src="deal.gameInfo.thumb"
                :alt="'Cover art for ' + deal.gameInfo.name"
                class="deal-cover-img"
              />
              
              <div class="cover-details">
                <h2 class="game-title">
                  <router-link :to="'/games/' + deal.gameInfo.gameID">
                    {{ deal.gameInfo.name }}
                  </router-link>
                </h2>
                <p class="pub-date" v-if="deal.gameInfo.publisher || deal.gameInfo.releaseDate">
                  <span v-if="deal.gameInfo.releaseDate">Released {{ convertDate(deal.gameInfo.releaseDate) }}</span>
                  <span v-if="deal.gameInfo.publisher"> by {{ deal.gameInfo.publisher }}</span>
                </p>
              </div>
            </div>

            <!-- Redirect CTA Card -->
            <div class="cta-card glass-card">
              <button @click="openDealer" class="btn-primary dealer-btn">
                Go to Dealer Site <i class="fa fa-external-link-alt"></i>
              </button>
              <p class="disclaimer">
                Redirects via a secure CheapShark referral link. This supports the API hosting provider.
              </p>
            </div>
          </div>

          <!-- Right Column: Deal Analysis & Ratings -->
          <div class="right-col">
            <!-- Pricing Analysis -->
            <div class="analysis-card glass-card">
              <h3>Pricing Analysis</h3>
              <div class="divider"></div>
              
              <div class="pricing-stats">
                <div class="stat-item">
                  <span class="stat-label">Current Sale Price</span>
                  <div class="price-store-val">
                    <span class="sale-val">${{ deal.gameInfo.salePrice }}</span>
                    <span class="store-tag">
                      on {{ getStoreName(deal.gameInfo.storeID) }}
                      <img
                        v-if="getStoreIconUrl(deal.gameInfo.storeID)"
                        :src="getStoreIconUrl(deal.gameInfo.storeID)"
                        class="store-icon"
                        alt=""
                      />
                    </span>
                  </div>
                </div>

                <div class="stat-item">
                  <span class="stat-label">Standard Retail Price</span>
                  <span class="retail-val">${{ deal.gameInfo.retailPrice }}</span>
                </div>

                <div class="stat-item" v-if="deal.cheapestPrice && deal.cheapestPrice.price">
                  <span class="stat-label">All-Time Best Deal</span>
                  <div class="cheapest-val">
                    <span class="accent-val">${{ deal.cheapestPrice.price }}</span>
                    <span class="date-val">on {{ convertDate(deal.cheapestPrice.date) }}</span>
                  </div>
                </div>
              </div>
            </div>

            <!-- Cheaper Store Checker -->
            <div class="checker-card glass-card">
              <h3>Store Comparisons</h3>
              <div class="divider"></div>

              <div class="checker-content">
                <div v-if="deal.cheaperStores.length > 0" class="better-deals-alert">
                  <i class="fa fa-info-circle info-icon"></i>
                  <div>
                    <p class="alert-title">Better deals found elsewhere:</p>
                    <ul class="better-stores-list">
                      <li v-for="store in deal.cheaperStores" :key="store">
                        {{ store }}
                      </li>
                    </ul>
                  </div>
                </div>
                <div v-else class="best-deal-badge">
                  <i class="fa fa-check-circle check-icon"></i>
                  <span>This is currently the best available deal on the web!</span>
                </div>
              </div>
            </div>

            <!-- Community Ratings -->
            <div class="ratings-card glass-card">
              <h3>Community Ratings</h3>
              <div class="divider"></div>

              <div class="ratings-row">
                <!-- Steam Rating -->
                <div v-if="deal.gameInfo.steamRatingText" class="rating-block border-right">
                  <span class="rating-label"><i class="fab fa-steam"></i> Steam Users</span>
                  <span class="rating-summary text-steam">{{ deal.gameInfo.steamRatingText }}</span>
                  <p class="rating-detail">
                    <strong>{{ deal.gameInfo.steamRatingPercent }}%</strong> positive reviews out of
                    {{ deal.gameInfo.steamRatingCount }} ratings.
                  </p>
                </div>
                <div v-else class="rating-block border-right empty-rating">
                  <span class="rating-label"><i class="fab fa-steam"></i> Steam Users</span>
                  <p class="rating-detail">No Steam reviews listed.</p>
                </div>

                <!-- Metacritic Rating -->
                <div v-if="deal.gameInfo.metacriticScore && deal.gameInfo.metacriticScore !== '0'" class="rating-block">
                  <span class="rating-label"><i class="fa fa-star"></i> Metacritic</span>
                  <span class="rating-summary text-metacritic">{{ deal.gameInfo.metacriticScore }}</span>
                  <a
                    :href="'https://www.metacritic.com' + deal.gameInfo.metacriticLink"
                    target="_blank"
                    class="reviews-link"
                  >
                    Read official critic reviews <i class="fa fa-external-link-alt"></i>
                  </a>
                </div>
                <div v-else class="rating-block empty-rating">
                  <span class="rating-label"><i class="fa fa-star"></i> Metacritic</span>
                  <p class="rating-detail">No Metacritic reviews listed.</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<style scoped>
.deal-details-page {
  width: 100%;
}

.deal-layout {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.left-col, .right-col {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  width: 100%;
}

/* Cover Card & Title */
.cover-card {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.deal-cover-img {
  width: 100%;
  aspect-ratio: 16 / 9;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid var(--border-color);
}

.cover-details {
  .game-title {
    font-size: 1.5rem;
    font-weight: 600;
    margin-block-end: 0.5rem;
    
    a {
      color: var(--text-primary);
      &:hover {
        color: var(--accent);
      }
    }
  }

  .pub-date {
    font-size: 0.85rem;
    color: var(--text-secondary);
  }
}

/* CTA Card */
.cta-card {
  padding: 1.5rem;
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.dealer-btn {
  width: 100%;
  padding-block: 0.75rem;
  font-size: 1rem;
}

.disclaimer {
  font-size: 0.75rem;
  color: var(--text-muted);
  line-height: 1.4;
}

/* Right Column Cards */
.analysis-card, .checker-card, .ratings-card {
  padding: 1.5rem;
  
  h3 {
    font-size: 1.1rem;
    font-weight: 600;
    color: var(--text-primary);
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }
}

.divider {
  height: 1px;
  background: var(--border-color);
  margin-block: 1rem;
}

/* Pricing Stats */
.pricing-stats {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.stat-label {
  font-size: 0.75rem;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
  font-weight: 600;
}

.price-store-val {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 0.75rem;
}

.sale-val {
  font-size: 1.8rem;
  font-weight: 700;
  color: var(--accent);
  line-height: 1;
}

.store-tag {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  font-size: 0.85rem;
  color: var(--text-secondary);
  background: color-mix(in srgb, var(--text-primary) 3%, transparent);
  border: 1px solid var(--border-color);
  border-radius: 6px;
  padding: 0.25rem 0.5rem;
}

.store-icon {
  width: 14px;
  height: 14px;
  object-fit: contain;
}

.retail-val {
  font-size: 1.25rem;
  text-decoration: line-through;
  color: var(--text-muted);
  font-weight: 500;
}

.cheapest-val {
  display: flex;
  align-items: baseline;
  gap: 0.5rem;
}

.accent-val {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--savings-green);
}

.date-val {
  font-size: 0.8rem;
  color: var(--text-secondary);
}

/* Store Comparisons */
.better-deals-alert {
  display: flex;
  gap: 0.75rem;
  background: color-mix(in srgb, var(--danger-solid) 5%, transparent);
  border: 1px solid color-mix(in srgb, var(--danger-solid) 15%, transparent);
  padding: 1rem;
  border-radius: 8px;
  
  .info-icon {
    font-size: 1.25rem;
    color: var(--danger);
    margin-top: 0.1rem;
  }
}

.alert-title {
  font-weight: 600;
  font-size: 0.9rem;
  color: var(--danger);
  margin-block-end: 0.5rem;
}

.better-stores-list {
  list-style: none;
  font-size: 0.85rem;
  color: var(--text-secondary);
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  
  li::before {
    content: "•";
    color: var(--danger-solid);
    display: inline-block;
    width: 1em;
    margin-left: 0.25rem;
  }
}

.best-deal-badge {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  background: color-mix(in srgb, var(--savings-green) 5%, transparent);
  border: 1px solid color-mix(in srgb, var(--savings-green) 15%, transparent);
  padding: 1rem;
  border-radius: 8px;
  color: var(--savings-green);
  font-size: 0.95rem;
  font-weight: 500;

  .check-icon {
    font-size: 1.25rem;
  }
}

/* Community Ratings */
.ratings-row {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.rating-block {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  width: 100%;
}

.rating-label {
  font-size: 0.8rem;
  color: var(--text-secondary);
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 0.35rem;
  
  i {
    font-size: 0.95rem;
  }
}

.rating-summary {
  font-size: 1.75rem;
  font-weight: 700;
  line-height: 1;
}

.text-steam {
  color: var(--rating-steam);
}

.text-metacritic {
  color: var(--rating-metacritic);
}

.rating-detail {
  font-size: 0.85rem;
  color: var(--text-secondary);
  line-height: 1.4;
  
  strong {
    color: var(--text-primary);
  }
}

.reviews-link {
  font-size: 0.8rem;
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  width: fit-content;
}

.empty-rating {
  justify-content: center;
  color: var(--text-muted);
}

@media (min-width: 768px) {
  .deal-layout {
    flex-direction: row;
    align-items: flex-start;
  }

  .left-col {
    width: 380px;
    flex-shrink: 0;
  }

  .right-col {
    flex-grow: 1;
  }

  .ratings-row {
    flex-direction: row;
  }

  .rating-block {
    flex: 1;
  }

  .border-right {
    border-right: 1px solid var(--border-color);
    padding-right: 1.5rem;
  }
}
</style>
