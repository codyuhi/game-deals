<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const searchString = ref('')
const router = useRouter()

const handleSearch = () => {
  const query = searchString.value.trim()
  if (query) {
    router.push(`/SearchResults/${encodeURIComponent(query)}`)
    searchString.value = ''
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
          <router-link to="/Favorites" class="nav-link favorites-link">
            <i class="fa fa-heart"></i>
            <span>Favorites</span>
          </router-link>

          <form @submit.prevent="handleSearch" class="nav-search-form">
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
        </div>
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
  background: rgba(9, 13, 22, 0.85);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--border-color);
  padding-block: 0.75rem;
}

.nav-container {
  max-width: 1200px;
  margin-inline: auto;
  padding-inline: 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.brand-link {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: var(--text-primary);
  
  &:hover {
    color: var(--text-primary);
  }
}

.brand-icon {
  font-size: 1.5rem;
  color: var(--accent);
}

.brand-text {
  font-family: var(--font-heading);
  font-size: 1.35rem;
  font-weight: 800;
  letter-spacing: -0.02em;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  flex-wrap: wrap;
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

  i {
    transition: transform 0.2s ease;
  }

  &:hover {
    color: var(--accent);
    
    i {
      color: #f43f5e;
      transform: scale(1.1);
    }
  }
}

.nav-search-form {
  display: flex;
  align-items: center;
  gap: 0.35rem;
}

.search-input-sm {
  width: 160px;
  height: 34px;
  font-size: 0.85rem;
  padding: 0.25rem 0.75rem;
}

.search-btn-sm {
  height: 34px;
  width: 34px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.content-wrapper {
  flex-grow: 1;
  width: 100%;
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
  
  &:hover {
    color: var(--accent);
  }
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

@media (max-width: 480px) {
  .nav-container {
    flex-direction: column;
    align-items: stretch;
    gap: 0.75rem;
  }

  .nav-actions {
    justify-content: space-between;
    width: 100%;
  }

  .search-input-sm {
    flex-grow: 1;
    width: auto;
  }
}
</style>
