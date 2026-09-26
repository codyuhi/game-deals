<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps<{
  title: string
  descriptions: string[]
  showSearchButton?: boolean
  imgClass?: string // Picks the page's Cedar accent strip color
}>()

const searchString = ref('')
const router = useRouter()

// Each page gets a Cedar pigment for the strip across the top of its title card
const accentColor = computed(() => {
  switch (props.imgClass) {
    case 'deals-img':
      return '#3b6b4c' // Lichen
    case 'games-img':
      return '#204b67' // Juniper
    case 'search-img':
      return '#70486c' // Heather
    case 'favorites-img':
      return '#c7370f' // Rust
    default:
      return '#1f513f' // Spruce
  }
})

const handleSearch = () => {
  const query = searchString.value.trim()
  if (query) {
    router.push(`/SearchResults/${encodeURIComponent(query)}`)
  } else {
    router.push('/')
  }
}
</script>

<template>
  <div class="title-card-container" :style="{ '--title-accent': accentColor }">
    <div class="accent-strip"></div>
    <div class="title-card-content">
      <div class="icon-wrapper">
        <i class="fa fa-gamepad gamepad-icon"></i>
      </div>
      <h1 class="hero-title">{{ title }}</h1>
      <p v-for="desc in descriptions" :key="desc" class="hero-description">
        {{ desc }}
      </p>

      <form v-if="showSearchButton" @submit.prevent="handleSearch" class="search-form">
        <input
          v-model="searchString"
          type="text"
          placeholder="Search PC games..."
          class="form-input search-input"
        />
        <button type="submit" class="btn-primary">
          <i class="fa fa-search"></i> Search
        </button>
      </form>
    </div>
    
    <div class="bottom-divider"></div>
  </div>
</template>

<style scoped>
.title-card-container {
  width: 100%;
  padding-block: 2.25rem 2rem;
  position: relative;
  overflow: hidden;
  background-color: var(--bg-secondary);
}

.accent-strip {
  position: absolute;
  inset-block-start: 0;
  inset-inline: 0;
  block-size: 4px;
  background: var(--title-accent);
}

@media (min-width: 640px) {
  .title-card-container {
    padding-block: 3.5rem 3rem;
  }
}

.title-card-content {
  max-width: 800px;
  margin-inline: auto;
  padding-inline: 1.25rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  position: relative;
  z-index: 2;
}

.icon-wrapper {
  margin-block-end: 0.75rem;
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: color-mix(in srgb, var(--text-primary) 3%, transparent);
  border: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: inset 0 0 10px color-mix(in srgb, var(--text-primary) 2%, transparent);
}

.gamepad-icon {
  font-size: 1.5rem;
  color: var(--accent);
}

.hero-title {
  font-size: clamp(1.85rem, 5vw, 3.25rem);
  letter-spacing: -0.03em;
  margin-block-end: 0.75rem;
  color: var(--text-emphasis);
  line-height: 1.15;
}

.hero-description {
  font-size: clamp(0.9rem, 2.5vw, 1.1rem);
  color: var(--text-secondary);
  max-width: 600px;
  line-height: 1.5;
  margin-block-end: 0.35rem;
}

.hero-description:last-of-type {
  margin-block-end: 1.5rem;
}

.search-form {
  display: flex;
  gap: 0.5rem;
  width: 100%;
  max-width: 480px;
}

.search-input {
  flex-grow: 1;
  height: 44px;
  min-height: 44px;
  font-size: 16px;
}

.search-form button {
  min-height: 44px;
  padding-inline: 1.25rem;
  white-space: nowrap;
}

.bottom-divider {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 1px;
  background: linear-gradient(90deg, transparent 0%, var(--border-color) 50%, transparent 100%);
}
</style>
