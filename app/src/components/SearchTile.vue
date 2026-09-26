<script setup lang="ts">
import type { SearchResult } from '../types'

defineProps<{
  game: SearchResult
}>()
</script>

<template>
  <div class="search-tile glass-card">
    <div class="tile-image-wrapper">
      <router-link :to="'/games/' + game.gameID" class="image-link" :aria-label="'View game details for ' + game.external">
        <img
          :src="game.thumb"
          class="thumbnail-image"
          :alt="'Cover art for ' + game.external"
          loading="lazy"
        />
      </router-link>
    </div>

    <div class="tile-info">
      <h3 class="game-title">
        <router-link :to="'/games/' + game.gameID" class="title-link">
          {{ game.external }}
        </router-link>
      </h3>
      <p class="cheapest-deal-text">
        Cheapest current deal is <strong>${{ game.cheapest }}</strong>
      </p>
    </div>

    <div class="tile-actions">
      <router-link :to="'/deals/' + game.cheapestDealID" class="btn-primary deal-button">
        View Deal <i class="fa fa-tag"></i>
      </router-link>
      <router-link :to="'/games/' + game.gameID" class="btn-secondary details-button">
        Game Info
      </router-link>
    </div>
  </div>
</template>

<style scoped>
.search-tile {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  gap: 1.25rem;
  padding: 1.25rem;
  margin-block-end: 1rem;
  width: 100%;

  &:hover {
    border-color: var(--border-color-hover);
    box-shadow: var(--shadow-raised);
  }
}

.tile-image-wrapper {
  width: 100%;
  aspect-ratio: 16 / 9;
  background-color: var(--bg-inset);
  border-radius: 6px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.image-link {
  display: block;
  width: 100%;
  height: 100%;
}

.thumbnail-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.tile-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex-grow: 1;
  gap: 0.5rem;
}

.game-title {
  font-size: 1.25rem;
  font-weight: 600;
  
  .title-link {
    color: var(--text-primary);
    
    &:hover {
      color: var(--accent);
    }
  }
}

.cheapest-deal-text {
  font-size: 0.95rem;
  color: var(--text-secondary);
  
  strong {
    color: var(--accent);
    font-size: 1.05rem;
  }
}

.tile-actions {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-top: auto;
}

.deal-button, .details-button {
  padding: 0.5rem 1rem;
  font-size: 0.85rem;
  flex-grow: 1;
  text-align: center;
}

@media (min-width: 640px) {
  .search-tile {
    flex-direction: row;
    align-items: center;
  }

  .tile-image-wrapper {
    width: 160px;
    height: 90px;
  }

  .tile-actions {
    flex-direction: column;
    margin-top: 0;
    align-items: stretch;
    width: 150px;
  }
  
  .deal-button, .details-button {
    flex-grow: 0;
  }
}
</style>
