<script setup lang="ts">
import { ref, onMounted } from 'vue'
import TitleCard from '../components/TitleCard.vue'
import type { FavoriteGroup } from '../types'

const favorites = ref<FavoriteGroup[]>([])
const descriptions = ['A list of your favorites grouped the way you like them']

// Inline editing states
const editingGroupNickname = ref<string | null>(null)
const editNicknameInput = ref<string>('')
const editError = ref<string>('')

// Deletion confirmation states
const groupConfirmDelete = ref<string | null>(null)
const itemConfirmDelete = ref<{ groupNickname: string; itemId: string } | null>(null)

const loadFavorites = () => {
  if (localStorage.favorites) {
    try {
      favorites.value = JSON.parse(localStorage.favorites)
    } catch (e) {
      favorites.value = []
    }
  } else {
    favorites.value = []
  }
}

const saveFavorites = () => {
  localStorage.favorites = JSON.stringify(favorites.value)
}

// Rename Group logic
const startRename = (nickname: string) => {
  editingGroupNickname.value = nickname
  editNicknameInput.value = nickname
  editError.value = ''
}

const cancelRename = () => {
  editingGroupNickname.value = null
  editNicknameInput.value = ''
  editError.value = ''
}

const saveRename = (oldNickname: string) => {
  const newName = editNicknameInput.value.trim()
  if (!newName) {
    editError.value = 'Nickname cannot be empty.'
    return
  }

  if (newName.toLowerCase() === oldNickname.toLowerCase()) {
    cancelRename()
    return
  }

  const exists = favorites.value.some(
    g => g.nickname.toLowerCase() === newName.toLowerCase()
  )

  if (exists) {
    editError.value = `"${newName}" is already taken.`
    return
  }

  const group = favorites.value.find(g => g.nickname === oldNickname)
  if (group) {
    group.nickname = newName
    saveFavorites()
  }

  cancelRename()
}

// Delete Group logic
const requestDeleteGroup = (nickname: string) => {
  groupConfirmDelete.value = nickname
}

const cancelDeleteGroup = () => {
  groupConfirmDelete.value = null
}

const confirmDeleteGroup = (nickname: string) => {
  favorites.value = favorites.value.filter(g => g.nickname !== nickname)
  saveFavorites()
  groupConfirmDelete.value = null
}

// Delete Item logic
const requestDeleteItem = (groupNickname: string, itemId: string) => {
  itemConfirmDelete.value = { groupNickname, itemId }
}

const cancelDeleteItem = () => {
  itemConfirmDelete.value = null
}

const confirmDeleteItem = (groupNickname: string, itemId: string) => {
  const group = favorites.value.find(g => g.nickname === groupNickname)
  if (group) {
    group.favoriteList = group.favoriteList.filter(item => item.id !== itemId)
    
    // If the group is now empty, let's keep the empty group so the user can add to it later,
    // or we can keep it. The legacy app kept it.
    saveFavorites()
  }
  itemConfirmDelete.value = null
}

onMounted(() => {
  loadFavorites()
})
</script>

<template>
  <div class="favorites-page">
    <TitleCard
      title="Favorites"
      :descriptions="descriptions"
      imgClass="favorites-img"
    />

    <main class="page-container">
      <div v-if="favorites.length < 1" class="empty-state glass-card">
        <i class="fa fa-heart-broken empty-icon"></i>
        <h2>No favorites saved yet</h2>
        <p>Browse deals and click "Add to Favorites" to organize your game lists here.</p>
        <router-link to="/" class="btn-primary start-browsing-btn">
          Browse Deals <i class="fa fa-search"></i>
        </router-link>
      </div>

      <div v-else class="groups-grid">
        <section
          v-for="group in favorites"
          :key="group.nickname"
          class="group-card glass-card"
        >
          <!-- Card Header: Title or Rename Mode -->
          <header class="group-header">
            <div v-if="editingGroupNickname === group.nickname" class="rename-container">
              <input
                v-model="editNicknameInput"
                type="text"
                class="form-input rename-input"
                @keyup.enter="saveRename(group.nickname)"
                @keyup.esc="cancelRename"
                required
                autofocus
              />
              <div class="rename-actions">
                <button
                  @click="saveRename(group.nickname)"
                  class="icon-action-btn success-btn"
                  title="Save Name"
                >
                  <i class="fa fa-check"></i>
                </button>
                <button
                  @click="cancelRename"
                  class="icon-action-btn cancel-btn"
                  title="Cancel"
                >
                  <i class="fa fa-times"></i>
                </button>
              </div>
              <p v-if="editError" class="inline-error">{{ editError }}</p>
            </div>

            <div v-else class="title-container">
              <h3>{{ group.nickname }}</h3>
              <div class="header-actions">
                <button
                  @click="startRename(group.nickname)"
                  class="icon-action-btn"
                  title="Rename Group"
                >
                  <i class="fa fa-edit"></i>
                </button>
                <button
                  @click="requestDeleteGroup(group.nickname)"
                  class="icon-action-btn delete-btn"
                  title="Delete Group"
                >
                  <i class="fa fa-trash"></i>
                </button>
              </div>
            </div>
          </header>

          <!-- Group Confirmation Dialog Overlay -->
          <div v-if="groupConfirmDelete === group.nickname" class="confirm-overlay">
            <p>Delete group <strong>{{ group.nickname }}</strong>?</p>
            <div class="confirm-actions">
              <button @click="cancelDeleteGroup" class="btn-secondary btn-confirm-sm">Cancel</button>
              <button @click="confirmDeleteGroup(group.nickname)" class="btn-danger btn-confirm-sm">Delete</button>
            </div>
          </div>

          <!-- Card Body: Favorites List -->
          <div v-else class="group-body">
            <div v-if="group.favoriteList.length < 1" class="empty-list-hint">
              No games in this group.
            </div>

            <ul v-else class="favorite-items-list">
              <li
                v-for="item in group.favoriteList"
                :key="item.id"
                class="favorite-item"
              >
                <!-- Confirm delete item row override -->
                <template
                  v-if="
                    itemConfirmDelete?.groupNickname === group.nickname &&
                    itemConfirmDelete?.itemId === item.id
                  "
                >
                  <div class="item-delete-confirm">
                    <span class="confirm-txt">Remove?</span>
                    <button
                      @click="confirmDeleteItem(group.nickname, item.id)"
                      class="text-danger-link"
                    >
                      Yes
                    </button>
                    <span class="sep">/</span>
                    <button @click="cancelDeleteItem" class="text-secondary-link">No</button>
                  </div>
                </template>

                <template v-else>
                  <router-link :to="'/games/' + item.id" class="game-link">
                    {{ item.name }}
                  </router-link>
                  <button
                    @click="requestDeleteItem(group.nickname, item.id)"
                    class="remove-item-btn"
                    title="Remove from group"
                  >
                    <i class="fa fa-times"></i>
                  </button>
                </template>
              </li>
            </ul>
          </div>
        </section>
      </div>
    </main>
  </div>
</template>

<style scoped>
.favorites-page {
  width: 100%;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 4rem 2rem;
  max-width: 600px;
  margin-inline: auto;
  gap: 1.25rem;

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

.start-browsing-btn {
  margin-top: 0.5rem;
}

.groups-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
  align-items: start;
}

.group-card {
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  min-height: 200px;
  
  &:hover {
    border-color: var(--border-color-hover);
  }
}

.group-header {
  padding: 1rem 1.25rem;
  border-block-end: 1px solid var(--border-color);
  min-height: 57px;
  display: flex;
  align-items: center;
}

.title-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;

  h3 {
    font-size: 1.15rem;
    font-weight: 600;
    color: var(--text-primary);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    padding-right: 0.5rem;
  }
}

.header-actions {
  display: flex;
  gap: 0.35rem;
}

.icon-action-btn {
  background: transparent;
  border: none;
  color: var(--text-secondary);
  cursor: pointer;
  width: 28px;
  height: 28px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.85rem;
  transition: background-color 0.2s ease, color 0.2s ease;

  &:hover {
    background: color-mix(in srgb, var(--text-primary) 5%, transparent);
    color: var(--text-primary);
  }
  
  &.delete-btn:hover {
    background: color-mix(in srgb, var(--danger-solid) 10%, transparent);
    color: var(--danger);
  }
}

/* Rename layout */
.rename-container {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  width: 100%;
  position: relative;
}

.rename-input {
  height: 32px;
  padding: 0.25rem 0.5rem;
  font-size: 0.9rem;
  flex-grow: 1;
}

.rename-actions {
  display: flex;
  gap: 0.25rem;
  flex-shrink: 0;
}

.success-btn {
  color: var(--savings-green);
  &:hover {
    background: color-mix(in srgb, var(--savings-green) 10%, transparent);
    color: var(--savings-green);
  }
}

.cancel-btn {
  color: var(--danger);
  &:hover {
    background: color-mix(in srgb, var(--danger-solid) 10%, transparent);
    color: var(--danger);
  }
}

.inline-error {
  position: absolute;
  top: 34px;
  left: 0;
  font-size: 0.7rem;
  color: var(--danger);
}

/* Delete Confirm Overlay */
.confirm-overlay {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  text-align: center;
  flex-grow: 1;

  p {
    font-size: 0.95rem;
    color: var(--text-secondary);
  }
}

.confirm-actions {
  display: flex;
  gap: 0.5rem;
  width: 100%;
}

.btn-confirm-sm {
  flex: 1;
  padding: 0.35rem 0.5rem;
  font-size: 0.8rem;
  border-radius: 6px;
}

/* Group Body & Items */
.group-body {
  padding: 1.25rem;
  flex-grow: 1;
}

.empty-list-hint {
  font-size: 0.85rem;
  color: var(--text-muted);
  font-style: italic;
  text-align: center;
  padding-block: 1.5rem;
}

.favorite-items-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.favorite-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0.75rem;
  background: var(--bg-inset);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 0.9rem;
  transition: border-color 0.2s ease, background-color 0.2s ease;

  &:hover {
    border-color: var(--border-color-hover);
    background: color-mix(in srgb, var(--text-primary) 1%, transparent);
  }
}

.game-link {
  color: var(--text-primary);
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding-right: 0.5rem;

  &:hover {
    color: var(--accent);
  }
}

.remove-item-btn {
  background: transparent;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  font-size: 0.8rem;
  padding: 0.25rem;
  transition: color 0.2s ease;

  &:hover {
    color: var(--danger);
  }
}

/* Item inline delete prompt */
.item-delete-confirm {
  display: flex;
  align-items: center;
  gap: 0.35rem;
  font-size: 0.8rem;
  width: 100%;
}

.confirm-txt {
  color: var(--text-secondary);
}

.text-danger-link {
  background: transparent;
  border: none;
  color: var(--danger);
  font-weight: 600;
  cursor: pointer;
  padding: 0;
  
  &:hover {
    text-decoration: underline;
  }
}

.text-secondary-link {
  background: transparent;
  border: none;
  color: var(--text-secondary);
  font-weight: 600;
  cursor: pointer;
  padding: 0;

  &:hover {
    text-decoration: underline;
  }
}

.sep {
  color: var(--text-muted);
}
</style>
