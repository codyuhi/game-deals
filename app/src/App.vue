<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const searchString = ref('')
const mobileSearchOpen = ref(false)
const searchInputRef = ref<HTMLInputElement | null>(null)
const router = useRouter()
const route = useRoute()

const handleSearch = () => {
  const query = searchString.value.trim()
  if (query) {
    router.push(`/SearchResults/${encodeURIComponent(query)}`)
    searchString.value = ''
    mobileSearchOpen.value = false
  }
}

const toggleMobileSearch = () => {
  mobileSearchOpen.value = !mobileSearchOpen.value
  if (mobileSearchOpen.value) {
    nextTick(() => {
      searchInputRef.value?.focus()
    })
  }
}
</script>

<template>
  <div id="layout-root">
    <!-- Navigation Header -->
    <nav class="main-navbar">
      <div class="nav-container">
        <router-link to="/" class="brand-link">
          <i class="fa fa-gamepad brand-icon"></i>
          <span class="brand-text">GameDeals</span>
        </router-link>

        <div class="nav-actions">
          <router-link to="/Favorites" class="nav-link favorites-link desktop-only">
            <i class="fa fa-heart"></i>
            <span>Favorites</span>
          </router-link>

          <form @submit.prevent="handleSearch" class="nav-search-form desktop-only">
            <input
              v-model="searchString"
              type="text"
              placeholder="Search games..."
              class="form-input search-input-sm"
              aria-label="Search games"
            />
            <button type="submit" class="btn-primary search-btn-sm" aria-label="Submit search">
              <i class="fa fa-search"></i>
            </button>
          </form>

          <!-- Mobile Search Toggle Button -->
          <button
            type="button"
            class="mobile-search-toggle-btn mobile-only"
            @click="toggleMobileSearch"
            aria-label="Toggle mobile search"
          >
            <i :class="mobileSearchOpen ? 'fa fa-times' : 'fa fa-search'"></i>
          </button>
        </div>
      </div>

      <!-- Expandable Mobile Search Bar -->
      <div v-if="mobileSearchOpen" class="mobile-search-bar mobile-only">
        <form @submit.prevent="handleSearch" class="mobile-search-form">
          <input
            ref="searchInputRef"
            v-model="searchString"
            type="text"
            placeholder="Search PC games..."
            class="form-input mobile-search-input"
            aria-label="Search PC games"
          />
          <button type="submit" class="btn-primary mobile-search-submit" aria-label="Search">
            <i class="fa fa-search"></i>
          </button>
        </form>
      </div>
    </nav>

    <!-- Main Content Area -->
    <div class="content-wrapper">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </div>

    <!-- Footer -->
    <footer class="main-footer">
      <div class="footer-container">
        <p>
          <a href="https://github.com/codyuhi/game-deals" target="_blank" rel="noopener" class="github-link">
            <i class="fab fa-github"></i> View source on GitHub
          </a>
        </p>
        <p class="copyright">&copy; Cody Uhi 2021 - Modernized in 2026</p>
      </div>
    </footer>

    <!-- Mobile Bottom Navigation Bar -->
    <nav class="mobile-bottom-nav mobile-only" aria-label="Mobile navigation">
      <router-link
        to="/"
        class="bottom-nav-item"
        :class="{ active: route.path === '/' || route.path.startsWith('/deals/') }"
      >
        <i class="fa fa-gamepad"></i>
        <span>Deals</span>
      </router-link>

      <button
        type="button"
        class="bottom-nav-item bottom-nav-btn"
        :class="{ active: mobileSearchOpen || route.path.startsWith('/SearchResults/') }"
        @click="toggleMobileSearch"
      >
        <i class="fa fa-search"></i>
        <span>Search</span>
      </button>

      <router-link
        to="/Favorites"
        class="bottom-nav-item"
        :class="{ active: route.path.startsWith('/Favorites') }"
      >
        <i class="fa fa-heart"></i>
        <span>Favorites</span>
      </router-link>
    </nav>
  </div>
</template>

<style scoped>
#layout-root {
  display: flex;
  flex-direction: column;
  min-block-size: 100dvh;
}

.main-navbar {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(9, 13, 22, 0.9);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-bottom: 1px solid var(--border-color);
  padding-block: 0.6rem;
  padding-top: max(0.6rem, var(--sat));
}

.nav-container {
  max-width: 1200px;
  margin-inline: auto;
  padding-inline: 1.25rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
}

.brand-link {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: var(--text-primary);
  text-decoration: none;
  touch-action: manipulation;
}

.brand-icon {
  font-size: 1.4rem;
  color: var(--accent);
}

.brand-text {
  font-family: var(--font-heading);
  font-size: 1.25rem;
  font-weight: 800;
  letter-spacing: -0.02em;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 1.25rem;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  color: var(--text-secondary);
  font-family: var(--font-heading);
  font-weight: 600;
  font-size: 0.95rem;
  transition: color 0.2s ease;
  text-decoration: none;
}

.nav-link:hover {
  color: var(--accent);
}

.nav-link i {
  transition: transform 0.2s ease;
}

.nav-link:hover i {
  color: #f43f5e;
  transform: scale(1.1);
}

.nav-search-form {
  display: flex;
  align-items: center;
  gap: 0.35rem;
}

.search-input-sm {
  width: 180px;
  height: 38px;
  min-height: 38px;
  font-size: 16px; /* iOS zoom prevention */
  padding: 0.25rem 0.75rem;
}

.search-btn-sm {
  height: 38px;
  width: 38px;
  min-height: 38px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

/* Mobile Search Toggle Button in top nav */
.mobile-search-toggle-btn {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  color: var(--text-secondary);
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
  cursor: pointer;
  touch-action: manipulation;
  transition: var(--transition);
}

.mobile-search-toggle-btn:active {
  transform: scale(0.95);
  color: var(--accent);
}

/* Expandable Mobile Search Bar */
.mobile-search-bar {
  padding: 0.6rem 1.25rem 0.5rem;
  background: rgba(15, 21, 36, 0.95);
  border-top: 1px solid var(--border-color);
  animation: slideDown 0.2s ease-out;
}

@keyframes slideDown {
  from { opacity: 0; transform: translateY(-8px); }
  to { opacity: 1; transform: translateY(0); }
}

.mobile-search-form {
  display: flex;
  gap: 0.5rem;
}

.mobile-search-input {
  flex-grow: 1;
  height: 44px;
  font-size: 16px;
}

.mobile-search-submit {
  height: 44px;
  padding-inline: 1.25rem;
}

.content-wrapper {
  flex-grow: 1;
  width: 100%;
}

@media (max-width: 767px) {
  .content-wrapper {
    padding-bottom: calc(4.5rem + var(--sab));
  }
}

.main-footer {
  background: var(--bg-secondary);
  border-top: 1px solid var(--border-color);
  padding-block: 2rem;
  text-align: center;
}

.footer-container {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  align-items: center;
  font-size: 0.85rem;
  color: var(--text-muted);
}

.github-link {
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: 0.35rem;
  text-decoration: none;
}

.github-link:hover {
  color: var(--accent);
}

/* Page Transitions */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Mobile Bottom Navigation Bar */
.mobile-bottom-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: 100;
  background: rgba(9, 13, 22, 0.94);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 0.45rem 1rem calc(0.45rem + var(--sab)) 1rem;
  box-shadow: 0 -4px 20px rgba(0, 0, 0, 0.5);
}

.bottom-nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 0.25rem;
  color: var(--text-muted);
  text-decoration: none;
  font-size: 0.725rem;
  font-weight: 600;
  font-family: var(--font-heading);
  padding: 0.35rem 1rem;
  border-radius: 8px;
  min-height: 44px;
  min-width: 64px;
  transition: all 0.2s ease;
  touch-action: manipulation;
}

.bottom-nav-item i {
  font-size: 1.15rem;
  transition: transform 0.2s ease;
}

.bottom-nav-item.active {
  color: var(--accent);
}

.bottom-nav-item.active i {
  transform: scale(1.1);
  filter: drop-shadow(0 0 6px var(--accent-glow));
}

.bottom-nav-item:active {
  transform: scale(0.95);
}

.bottom-nav-btn {
  background: transparent;
  border: none;
  cursor: pointer;
}

/* Visibility helpers */
.mobile-only {
  display: none !important;
}

.desktop-only {
  display: flex !important;
}

@media (max-width: 767px) {
  .mobile-only {
    display: flex !important;
  }

  .desktop-only {
    display: none !important;
  }
}
</style>
