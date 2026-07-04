<script setup lang="ts">
import { computed } from 'vue'
import type { Deal, Store, FavoriteItem } from '../types'

const props = defineProps<{
  deal: Deal
  storeData: Store[]
}>()

const emit = defineEmits<{
  (e: 'favorite', item: FavoriteItem): void
}>()

const convertDate = (unixDate: number) => {
  if (!unixDate) return 'N/A'
  const format: Intl.DateTimeFormatOptions = {
    day: 'numeric',
    month: 'short',
    year: 'numeric'
  }
  return new Date(unixDate * 1000).toLocaleString('en-US', format)
}

const storeInfo = computed(() => {
  const store = props.storeData.find(s => s.storeID === props.deal.storeID)
  return {
    name: store ? store.storeName : 'Anonymous store',
    icon: store ? `https://www.cheapshark.com${store.images.icon}` : undefined
  }
})

const handleFavorite = (e: Event) => {
  e.preventDefault()
  e.stopPropagation()
  emit('favorite', {
    name: props.deal.title,
    id: props.deal.gameID
  })
}
</script>

<template>
  <article class="deal-card glass-card">
    <div class="thumbnail-wrapper">
      <router-link :to="'/deals/' + deal.dealID" class="thumbnail-link" :aria-label="'View deal details for ' + deal.title">
        <img
          :src="deal.thumb"
          class="thumbnail-image"
          :alt="'Cover art for ' + deal.title"
          loading="lazy"
        />
      </router-link>
      <span class="badge badge-savings" v-if="parseFloat(deal.savings) > 0">
        -{{ parseFloat(deal.savings).toFixed(0) }}%
      </span>
    </div>

    <div class="card-body">
      <div class="store-row">
        <img
          v-if="storeInfo.icon"
          class="store-icon"
          :src="storeInfo.icon"
          :alt="storeInfo.name"
        />
        <span class="store-name">{{ storeInfo.name }}</span>
      </div>

      <h3 class="game-title">
        <router-link :to="'/games/' + deal.gameID" class="title-link">
          {{ deal.title }}
        </router-link>
      </h3>

      <p class="release-date" v-if="deal.releaseDate">
        Released {{ convertDate(deal.releaseDate) }}
      </p>

      <div class="prices-row">
        <div class="price-box">
          <span class="price-label">Sale Price</span>
          <span class="sale-price">${{ deal.salePrice }}</span>
        </div>
        <div class="price-box">
          <span class="price-label">Retail</span>
          <span class="normal-price">${{ deal.normalPrice }}</span>
        </div>
      </div>

      <div class="ratings-grid">
        <div class="rating-badge" :title="deal.dealRating + ' / 10 deal quality score'">
          <i class="fa fa-tag text-muted"></i>
          <span>{{ deal.dealRating }}/10 Deal</span>
        </div>
        
        <div v-if="deal.steamRatingText" class="rating-badge badge-steam" :title="deal.steamRatingPercent + '% positive on Steam'">
          <i class="fab fa-steam"></i>
          <span>{{ deal.steamRatingPercent }}%</span>
        </div>

        <div v-if="deal.metacriticScore && deal.metacriticScore !== '0'" class="rating-badge badge-metacritic" :title="'Metacritic: ' + deal.metacriticScore">
          <i class="fa fa-star"></i>
          <span>MC {{ deal.metacriticScore }}</span>
        </div>
      </div>
    </div>

    <div class="card-footer">
      <button @click="handleFavorite" class="fav-btn" aria-label="Add to favorites">
        <i class="fa fa-heart"></i> Favorite
      </button>
      <router-link :to="'/deals/' + deal.dealID" class="btn-primary view-deal-btn">
        Details <i class="fa fa-arrow-right"></i>
      </router-link>
    </div>
  </article>
</template>

<style scoped>
.deal-card {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
  max-width: 380px;
  width: 100%;

  &:hover {
    border-color: var(--border-color-hover);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.5), 0 0 15px rgba(255, 255, 255, 0.02);
    transform: translateY(-4px);
  }
}

.thumbnail-wrapper {
  position: relative;
  width: 100%;
  aspect-ratio: 16 / 9;
  background-color: rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

.thumbnail-link {
  display: block;
  width: 100%;
  height: 100%;
}

.thumbnail-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;

  &:hover {
    transform: scale(1.05);
  }
}

.badge-savings {
  position: absolute;
  top: 0.75rem;
  right: 0.75rem;
  z-index: 2;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.card-body {
  padding: 1.25rem;
  display: flex;
  flex-direction: column;
  flex-grow: 1;
}

.store-row {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.store-icon {
  width: 16px;
  height: 16px;
  object-fit: contain;
}

.store-name {
  font-size: 0.8rem;
  color: var(--text-secondary);
}

.game-title {
  font-size: 1.2rem;
  font-weight: 600;
  line-height: 1.3;
  margin-block-end: 0.35rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.title-link {
  color: var(--text-primary);
  
  &:hover {
    color: var(--accent);
  }
}

.release-date {
  font-size: 0.8rem;
  color: var(--text-muted);
  margin-block-end: 1rem;
}

.prices-row {
  display: flex;
  gap: 1.5rem;
  margin-block-end: 1rem;
  margin-top: auto;
}

.price-box {
  display: flex;
  flex-direction: column;
}

.price-label {
  font-size: 0.7rem;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.sale-price {
  font-size: 1.4rem;
  font-weight: 700;
  color: var(--accent);
}

.normal-price {
  font-size: 1.1rem;
  text-decoration: line-through;
  color: var(--text-muted);
}

.ratings-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.rating-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid var(--border-color);
  border-radius: 6px;
  padding: 0.25rem 0.5rem;
  font-size: 0.75rem;
  color: var(--text-secondary);
  font-weight: 500;
  
  i {
    font-size: 0.8rem;
  }
}

.badge-steam {
  color: var(--rating-steam);
  border-color: rgba(56, 189, 248, 0.15);
}

.badge-metacritic {
  color: var(--rating-metacritic);
  border-color: rgba(234, 88, 12, 0.2);
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1.25rem 1.25rem;
  border-block-start: 1px solid var(--border-color);
}

.fav-btn {
  background: transparent;
  border: none;
  color: var(--text-secondary);
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 0.35rem;
  font-size: 0.85rem;
  font-weight: 600;
  transition: color 0.2s ease;

  i {
    transition: transform 0.2s ease;
  }

  &:hover {
    color: #f43f5e;
    
    i {
      transform: scale(1.15);
    }
  }
}

.view-deal-btn {
  padding: 0.4rem 1rem;
  font-size: 0.85rem;
}
</style>
