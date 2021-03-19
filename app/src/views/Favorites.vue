<template>
  <div class="favorites flex-column">
    <title-card
      title="Favorites"
      :descriptions="descriptions"
      imgClass="favorites-img"
    />
    <div v-if="favorites.length < 1" class="empty-result flex-column">
      <h2>
        You have not saved any favorites! Find some games and favorite them so
        they can show up here!
      </h2>
    </div>
    <div v-else class="flex-column">
      <div class="fav-row">
        <div
          v-for="favgroup in favorites"
          :key="favgroup.nickname"
          class="favgroup-cell"
        >
          <h3>{{ favgroup.nickname }}</h3>
          <p @click="changeNickname(favgroup.nickname)" class="change-prompt">
            Change Nickname
          </p>
          <p @click="deleteGroup(favgroup.nickname)" class="change-prompt">
            Delete Group
          </p>
          <div
            v-for="fav in favgroup.favoriteList"
            :key="fav.id"
            class="fav-cell"
          >
            <router-link :to="'/games/' + fav.id">{{ fav.name }} </router-link>
            <p
              @click="deleteItem(favgroup.nickname, fav.id)"
              class="del-item-prompt"
            >
              Delete Item
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import TitleCard from "../components/TitleCard.vue";
export default {
  components: { TitleCard },
  data() {
    return {
      favorites: [],
      descriptions: ["A list of your favorites grouped the way you like them"],
    };
  },
  methods: {
    getFavorites() {
      if (localStorage.favorites) {
        this.favorites = JSON.parse(localStorage.favorites);
      }
    },
    changeNickname(nickname) {
      const newName = prompt(
        "Please enter the new nickname that will replace the nickname " +
          nickname
      );
      for (let i = 0; i < this.favorites.length; i++) {
        if (newName === this.favorites[i].nickname) {
          alert(
            newName +
              " is a nickname that is already taken! Try another nickname"
          );
          return;
        }
      }
      for (let i = 0; i < this.favorites.length; i++) {
        if (nickname === this.favorites[i].nickname) {
          this.favorites[i].nickname = newName;
        }
      }
      localStorage.favorites = JSON.stringify(this.favorites);
    },
    deleteGroup(nickname) {
      if (
        confirm("Are you sure you want to delete the group " + nickname + "?")
      ) {
        for (let i = 0; i < this.favorites.length; i++) {
          if (nickname === this.favorites[i].nickname) {
            this.favorites.splice(i, 1);
            localStorage.favorites = JSON.stringify(this.favorites);
            return;
          }
        }
      }
    },
    deleteItem(nickname, id) {
      if (
        confirm("Are you sure you want to delete this item from " + nickname)
      ) {
        for (let i = 0; i < this.favorites.length; i++) {
          if (nickname === this.favorites[i].nickname) {
            for (let j = 0; j < this.favorites[i].favoriteList.length; j++) {
              if (id === this.favorites[i].favoriteList[j].id) {
                this.favorites[i].favoriteList.splice(j, 1);
                localStorage.favorites = JSON.stringify(this.favorites);
                return;
              }
            }
          }
        }
      }
    },
  },
  created() {
    this.getFavorites();
  },
};
</script>

<style scoped>
.fav-row {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  margin: 150px 0 150px 0;
}

.favgroup-cell {
  display: flex;
  flex-direction: column;
  flex: 1;
  justify-content: center;
  align-items: center;
  padding: 35px 25px 35px 25px;
  margin: 15px;
  background-color: #cfdbd5;
  box-shadow: 2px 2px 2px #e8eddf;
}

.fav-cell {
  display: flex;
  flex-direction: column;
  justify-content: baseline;
  align-items: center;
  padding: 20px 10px 20px 10px;
  margin: 10px;
  background-color: #333533;
  color: #f5cb5c;
  min-width: 180px;
}

.flex-column {
  align-items: center;
}

.change-prompt {
  font-size: 0.75em;
}

.del-item-prompt {
  font-size: 0.75em;
  color: #e8eddf;
  margin: 15px;
}

.change-prompt:hover {
  cursor: pointer;
}

.empty-result {
  text-align: center;
  padding: 250px 0 250px 0;
  margin: 0 50px 0 50px;
}
</style>