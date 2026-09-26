<script setup lang="ts">
import { ref } from 'vue'
import type { FavoriteGroup, FavoriteItem } from '../types'

const dialogRef = ref<HTMLDialogElement | null>(null)
const groups = ref<FavoriteGroup[]>([])
const selectedGroup = ref<string>('')
const newGroupNickname = ref<string>('')
const isCreatingNew = ref<boolean>(false)
const gameToSave = ref<FavoriteItem | null>(null)
const errorMessage = ref<string>('')

const open = (item: FavoriteItem) => {
  gameToSave.value = item
  errorMessage.value = ''
  isCreatingNew.value = false
  newGroupNickname.value = ''

  if (localStorage.favorites) {
    try {
      groups.value = JSON.parse(localStorage.favorites)
    } catch (e) {
      groups.value = []
    }
  } else {
    groups.value = []
  }

  if (groups.value.length > 0) {
    selectedGroup.value = groups.value[0].nickname
  } else {
    isCreatingNew.value = true
  }

  dialogRef.value?.showModal()
}

const close = () => {
  dialogRef.value?.close()
}

const save = () => {
  if (!gameToSave.value) return

  let nickname = ''
  if (isCreatingNew.value) {
    nickname = newGroupNickname.value.trim()
    if (!nickname) {
      errorMessage.value = 'Please enter a group nickname.'
      return;
    }
  } else {
    nickname = selectedGroup.value
    if (!nickname) {
      errorMessage.value = 'Please select a group.'
      return;
    }
  }

  let favoriteIndex = groups.value.findIndex(g => g.nickname.toLowerCase() === nickname.toLowerCase())
  
  if (favoriteIndex === -1 && !isCreatingNew.value) {
    errorMessage.value = 'Selected group does not exist.'
    return;
  }

  const newFavorite: FavoriteItem = {
    name: gameToSave.value.name,
    id: gameToSave.value.id
  }

  if (favoriteIndex === -1) {
    // Create new group
    const newGroup: FavoriteGroup = {
      nickname: nickname,
      favoriteList: [newFavorite]
    }
    groups.value.push(newGroup)
  } else {
    // Add to existing group
    const group = groups.value[favoriteIndex]
    const exists = group.favoriteList.some(item => item.id === newFavorite.id)
    
    if (exists) {
      errorMessage.value = `"${newFavorite.name}" is already in the "${group.nickname}" group.`
      return;
    }
    group.favoriteList.push(newFavorite)
  }

  localStorage.favorites = JSON.stringify(groups.value)
  close()
}

defineExpose({
  open,
  close
})
</script>

<template>
  <dialog ref="dialogRef" class="favorite-dialog glass-card" @click.self="close">
    <div class="dialog-content">
      <header class="dialog-header">
        <h3>Add to Favorites</h3>
        <button class="close-btn" @click="close" aria-label="Close dialog">
          <i class="fa fa-times"></i>
        </button>
      </header>

      <div class="dialog-body">
        <p class="game-name-hint">
          Add <strong>{{ gameToSave?.name }}</strong> to a favorites group.
        </p>

        <div v-if="errorMessage" class="error-banner">
          <i class="fa fa-exclamation-circle"></i> {{ errorMessage }}
        </div>

        <div class="form-group" v-if="groups.length > 0 && !isCreatingNew">
          <label for="group-select">Choose Group</label>
          <select id="group-select" v-model="selectedGroup" class="form-input select-input">
            <option v-for="g in groups" :key="g.nickname" :value="g.nickname">
              {{ g.nickname }}
            </option>
          </select>
          <button type="button" class="toggle-link" @click="isCreatingNew = true">
            Create new group instead
          </button>
        </div>

        <div class="form-group" v-else>
          <label for="new-group-input">New Group Nickname</label>
          <input
            id="new-group-input"
            type="text"
            v-model="newGroupNickname"
            placeholder="e.g. Wishlist, Backlog"
            class="form-input"
            @keyup.enter="save"
            required
          />
          <button
            type="button"
            class="toggle-link"
            v-if="groups.length > 0"
            @click="isCreatingNew = false"
          >
            Select existing group instead
          </button>
        </div>
      </div>

      <footer class="dialog-footer">
        <button class="btn-secondary" @click="close">Cancel</button>
        <button class="btn-primary" @click="save">Save Favorite</button>
      </footer>
    </div>
  </dialog>
</template>

<style scoped>
.favorite-dialog {
  border: 1px solid var(--border-color);
  max-width: 450px;
  width: 90vw;
  padding: 1.5rem;
  color: var(--text-primary);
  margin: auto;
  outline: none;

  &::backdrop {
    background: var(--overlay);
    backdrop-filter: blur(6px);
  }
}

.dialog-content {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  h3 {
    font-size: 1.25rem;
  }
}

.close-btn {
  background: transparent;
  border: none;
  color: var(--text-secondary);
  cursor: pointer;
  font-size: 1.1rem;
  padding: 0.25rem;
  transition: color 0.2s ease;

  &:hover {
    color: var(--text-primary);
  }
}

.dialog-body {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.game-name-hint {
  font-size: 0.95rem;
  color: var(--text-secondary);
  strong {
    color: var(--text-primary);
  }
}

.error-banner {
  background: color-mix(in srgb, var(--danger-solid) 10%, transparent);
  border: 1px solid color-mix(in srgb, var(--danger-solid) 20%, transparent);
  color: var(--danger);
  padding: 0.75rem;
  border-radius: 8px;
  font-size: 0.85rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;

  label {
    font-size: 0.85rem;
    font-weight: 500;
    color: var(--text-secondary);
  }
}

.select-input {
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%2394a3b8'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M19 9l-7 7-7-7'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 0.75rem center;
  background-size: 1.25rem;
  padding-right: 2rem;
}

.toggle-link {
  background: transparent;
  border: none;
  color: var(--accent);
  cursor: pointer;
  font-size: 0.8rem;
  text-align: left;
  padding: 0.25rem 0;
  width: fit-content;

  &:hover {
    color: var(--accent-hover);
    text-decoration: underline;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: 0.5rem;
}
</style>
