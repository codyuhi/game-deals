<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'

const STORAGE_KEY = 'game_deals_theme'
const THEME_COLORS = { light: '#ebe7df', dark: '#161614' } as const
type Appearance = 'light' | 'dark'

const readSaved = (): Appearance | null => {
  try {
    const saved = localStorage.getItem(STORAGE_KEY)
    return saved === 'light' || saved === 'dark' ? saved : null
  } catch {
    return null
  }
}

const media = window.matchMedia('(prefers-color-scheme: dark)')
// index.html applies the saved or OS theme before first paint; start from that
const theme = ref<Appearance>(document.documentElement.classList.contains('dark') ? 'dark' : 'light')

watch(
  theme,
  (value) => {
    document.documentElement.classList.toggle('dark', value === 'dark')
    document.querySelector('meta[name="theme-color"]')?.setAttribute('content', THEME_COLORS[value])
  },
  { immediate: true },
)

// Follow the OS until the user picks a theme
const onSystemChange = () => {
  if (!readSaved()) theme.value = media.matches ? 'dark' : 'light'
}
onMounted(() => media.addEventListener('change', onSystemChange))
onBeforeUnmount(() => media.removeEventListener('change', onSystemChange))

const toggle = () => {
  const next: Appearance = theme.value === 'dark' ? 'light' : 'dark'
  try {
    localStorage.setItem(STORAGE_KEY, next)
  } catch {
    // storage unavailable: the toggle still applies for this visit
  }
  theme.value = next
}
</script>

<template>
  <button
    type="button"
    class="theme-toggle"
    :title="`Switch to ${theme === 'dark' ? 'light' : 'dark'} mode`"
    aria-label="Toggle color theme"
    @click="toggle"
  >
    <i :class="theme === 'dark' ? 'fa fa-sun sun' : 'fa fa-moon'"></i>
  </button>
</template>

<style scoped>
.theme-toggle {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 44px;
  min-height: 44px;
  border-radius: 6px;
  border: 1px solid var(--border-color-hover);
  background: var(--bg-secondary);
  color: var(--text-primary);
  cursor: pointer;
  touch-action: manipulation;
  transition: background-color 0.2s ease;

  &:hover {
    background: var(--bg-inset);
  }

  &:active {
    transform: scale(0.98);
  }
}

.sun {
  color: #f3c969;
}
</style>
