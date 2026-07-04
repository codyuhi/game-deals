<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps<{
  title: string
  descriptions: string[]
  showSearchButton?: boolean
  imgClass?: string // We use this to compute gradients instead of background images
}>()

const searchString = ref('')
const router = useRouter()

const gradientStyle = computed(() => {
  switch (props.imgClass) {
    case 'main-img': // Amber/Gold Theme
      return 'radial-gradient(circle at 80% 20%, rgba(251, 191, 36, 0.08) 0%, rgba(9, 13, 22, 0) 60%)'
    case 'deals-img': // Emerald/Teal Theme
      return 'radial-gradient(circle at 80% 20%, rgba(16, 185, 129, 0.08) 0%, rgba(9, 13, 22, 0) 60%)'
    case 'games-img': // Sky/Blue Theme
      return 'radial-gradient(circle at 80% 20%, rgba(56, 189, 248, 0.08) 0%, rgba(9, 13, 22, 0) 60%)'
    case 'search-img': // Indigo/Purple Theme
      return 'radial-gradient(circle at 80% 20%, rgba(168, 85, 247, 0.08) 0%, rgba(9, 13, 22, 0) 60%)'
    case 'favorites-img': // Rose/Crimson Theme
      return 'radial-gradient(circle at 80% 20%, rgba(244, 63, 94, 0.08) 0%, rgba(9, 13, 22, 0) 60%)'
    default:
      return 'radial-gradient(circle at 50% 20%, rgba(255, 255, 255, 0.03) 0%, rgba(9, 13, 22, 0) 50%)'
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
  <div class="title-card-container" :style="{ backgroundImage: gradientStyle }">
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
  padding-block: 4rem 3.5rem;
  position: relative;
  overflow: hidden;
  background-color: var(--bg-secondary);
}

.title-card-content {
  max-width: 800px;
  margin-inline: auto;
  padding-inline: 1.5rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  position: relative;
  z-index: 2;
}

.icon-wrapper {
  margin-block-end: 1rem;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: inset 0 0 10px rgba(255, 255, 255, 0.02);
}

.gamepad-icon {
  font-size: 1.75rem;
  color: var(--accent);
  filter: drop-shadow(0 0 8px var(--accent-glow));
}

.hero-title {
  font-size: clamp(2.5rem, 4vw + 1rem, 4rem);
  letter-spacing: -0.03em;
  margin-block-end: 1rem;
  background: linear-gradient(135deg, #fff 30%, var(--text-secondary) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.hero-description {
  font-size: clamp(1rem, 1.5vw + 0.5rem, 1.15rem);
  color: var(--text-secondary);
  max-width: 600px;
  line-height: 1.6;
  margin-block-end: 0.5rem;

  &:last-of-type {
    margin-block-end: 2rem;
  }
}

.search-form {
  display: flex;
  gap: 0.75rem;
  width: 100%;
  max-width: 480px;
}

.search-input {
  flex-grow: 1;
  height: 42px;
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
