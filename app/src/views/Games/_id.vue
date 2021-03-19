<template>
  <div class="game">
    <div v-if="isLoading" class="flex-column is-loading">
      <h2>Loading . . .</h2>
    </div>
    <div v-else>
      <title-card
        title="Game Details"
        :descriptions="descriptions"
        :showSearchButton="false"
        imgClass="games-img"
      />
      <div class="flex-column content">
        <img
          v-if="game.info.thumb"
          class="thumbnail-image"
          :src="game.info.thumb"
          :alt="'Cover art for ' + game.info.title"
        />
        <h2 class="title">{{ game.info.title }}</h2>
        <h3>
          The best deal ever offered for this game was ${{
            game.cheapestPriceEver.price
          }}
          on {{ convertDate(game.cheapestPriceEver.date) }}
        </h3>
        <div v-if="game.deals.length === 0">
          <h3>
            There are currently no deals available for {{ game.info.title }}
          </h3>
        </div>
        <div v-else-if="game.deals.length === 1" class="flex-column">
          <hr />
          <div class="child-container">
            <div class="flex-row cell-left">
              <img
                :src="
                  'https://www.cheapshark.com' +
                  getStoreImg(game.deals[0].storeID, 'logo')
                "
                class="logo-image"
              />
            </div>
            <div class="flex-row cell-right">
              <div class="flex-column cell">
                <h3>
                  There is a deal for {{ game.info.title }} available at
                  {{ getStoreName(game.deals[0].storeID) }} for
                  <strong>${{ game.deals[0].price }}</strong>
                </h3>
                <h3>
                  <a
                    @click="openDealer(game.deals[0].dealID)"
                    class="yellow-link"
                    >Click here</a
                  >
                  to view this deal on {{ getStoreName(game.deals[0].storeID) }}
                </h3>
              </div>
            </div>
          </div>
          <hr />
        </div>
        <div v-else>
          <h3>Deals for {{ game.info.title }} in order from best to worst:</h3>
          <div
            v-for="deal in game.deals"
            :key="deal.dealID"
            class="flex-column"
          >
            <hr />
            <div class="child-container">
              <div class="flex-row cell-left">
                <img
                  :src="
                    'https://www.cheapshark.com' +
                    getStoreImg(deal.storeID, 'logo')
                  "
                  class="logo-image"
                />
              </div>
              <div class="flex-row cell-right">
                <div class="flex-column cell">
                  <h3>
                    Sale Price: <strong>${{ deal.price }}</strong>
                  </h3>
                  <h3>Retail Price: ${{ deal.retailPrice }}</h3>
                  <h3>
                    You save
                    <strong>{{ parseFloat(deal.savings).toFixed(2) }}%</strong>
                  </h3>
                  <h3>
                    <a @click="openDealer(deal.dealID)" class="yellow-link"
                      >Click here</a
                    >
                    to view this deal on {{ getStoreName(deal.storeID) }}
                  </h3>
                </div>
              </div>
            </div>
            <hr />
          </div>
        </div>

        <h3 @click="addToFavorites(game)" class="yellow-link">
          Add to Favorites <i class="fa fa-heart"></i>
        </h3>
      </div>
    </div>
  </div>
</template>

<script>
import TitleCard from "../../components/TitleCard.vue";
const gameData = require("../../../sampledata/Game.json");
const ListOfStores = require("../../../sampledata/ListOfStores.json");

export default {
  components: { TitleCard },
  data() {
    return {
      isLoading: false,
      game: {},
      storeData: new Array(),
      descriptions: [],
    };
  },
  methods: {
    getGameData() {
      this.isLoading = true;
      fetch(
        "https://www.cheapshark.com/api/1.0/games?id=" +
          this.$route.path.slice(7),
        {}
      )
        .then((res) => {
          return res.json();
        })
        .then((json) => {
          this.game = json;
          this.descriptions = [
            "More information about " + this.game.info.title,
          ];
        })
        .catch((err) => {
          console.error(err);
          alert(
            "Unable to perform API call because of this error:",
            err,
            "\nThis page will display dummy data to continue to show the structure of the page"
          );
          this.game = gameData.game;
          this.descriptions = [
            "More information about " + this.game.info.title,
          ];
        })
        .finally((fin) => {
          this.isLoading = false;
        });
    },
    getStoreData() {
      if (localStorage.storeData) {
        this.storeData = JSON.parse(localStorage.storeData);
        return;
      }
      fetch("https://www.cheapshark.com/api/1.0/stores", {})
        .then((res) => {
          return res.json();
        })
        .then((json) => {
          this.storeData = json;
          localStorage.storeData = JSON.stringify(this.storeData);
        })
        .catch((err) => {
          console.error(err);
          alert(
            "Unable to perform API call because of this error:",
            err,
            "\nThis page will display dummy data to continue to show the structure of the page"
          );
          this.storeData = ListOfStores.listOfStores;
        });
    },
    convertDate(unixDate) {
      const format = {
        day: "numeric",
        month: "short",
        year: "numeric",
      };
      return new Date(unixDate * 1000).toLocaleString("en-US", format);
    },
    openDealer(dealId) {
      window.open("https://www.cheapshark.com/redirect?dealID=" + dealId);
    },
    getStoreName(storeId) {
      for (let i = 0; i < this.storeData.length; i++) {
        if (storeId === this.storeData[i].storeID) {
          return this.storeData[i].storeName;
        }
      }
      return "Anonymous store";
    },
    getStoreImg(storeId, type) {
      switch (type) {
        case "banner":
          for (let i = 0; i < this.storeData.length; i++) {
            if (storeId === this.storeData[i].storeID) {
              return this.storeData[i].images.banner;
            }
          }
          break;
        case "logo":
          for (let i = 0; i < this.storeData.length; i++) {
            if (storeId === this.storeData[i].storeID) {
              return this.storeData[i].images.logo;
            }
          }
          break;
        case "icon":
          for (let i = 0; i < this.storeData.length; i++) {
            if (storeId === this.storeData[i].storeID) {
              return this.storeData[i].images.icon;
            }
          }
          break;
      }
    },
    addToFavorites(game) {
      let favorites;
      let favoriteGroup;
      let favoriteIndex = -1;
      if (!localStorage.favorites) {
        localStorage.favorites = "[]";
        favorites = [];
      } else {
        favorites = JSON.parse(localStorage.favorites);
      }
      let promptString =
        "Please type in the nickname of the favorites group you want to add this to (enter an existing nickname to add to an existing group)\nExisting nicknames: ";
      if (favorites.length < 1) {
        promptString += "None. Enter a new nickname";
      }
      for (let i = 0; i < favorites.length; i++) {
        if (i !== 0) {
          promptString += ", ";
        }
        promptString += favorites[i].nickname;
      }
      let nickname = prompt(promptString);
      for (let i = 0; i < favorites.length; i++) {
        if (nickname === favorites[i].nickname) {
          favoriteIndex = i;
          break;
        }
      }
      const newFavorite = {
        name: game.info.title,
        id: this.$route.path.slice(7),
      };
      if (favoriteIndex === -1) {
        favoriteGroup = {
          nickname: nickname,
          favoriteList: [newFavorite],
        };
        favorites.push(favoriteGroup);
      } else {
        favoriteGroup = favorites[favoriteIndex];
        for (let i = 0; i < favoriteGroup.favoriteList.length; i++) {
          if (favoriteGroup.favoriteList[i].id === newFavorite.id) {
            alert(
              `${newFavorite.name} is already present in the ${nickname} favorite group. Try a different group/game.`
            );
            return;
          }
        }
        favoriteGroup.favoriteList.push(newFavorite);
        favorites[favoriteIndex] = favoriteGroup;
      }
      localStorage.favorites = JSON.stringify(favorites);
    },
  },
  created() {
    this.getStoreData();
    this.getGameData();
  },
};
</script>

<style scoped>
.content {
  margin: 25px;
}

h2 {
  margin: 15px 0 15px 0;
}

h3 {
  font-size: 1rem;
  margin: 25px;
}

.child-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  margin: 15px 0 15px 0;
}

hr {
  width: 100%;
  margin: 0;
}

.cell-left {
  flex: 1;
}

.cell-right {
  flex: 1;
}

.thumbnail-image {
  width: 100%;
  height: auto;
  max-width: 500px;
}

.logo-image {
  width: 25vw;
  height: auto;
}

.yellow-link {
  color: #f5cb5c !important;
  text-shadow: 1px 1px 1px var(--dark);
  z-index: 5;
}

.yellow-link:hover {
  cursor: pointer;
}

@media only screen and (min-width: 450px) {
  h3 {
    font-size: 1.75rem;
  }

  .logo-image {
    max-width: 300px;
    margin: 15px 25px 15px 25px;
  }

  .child-container {
    flex-direction: row;
  }

  .cell {
    text-align: left;
    margin-left: 25px;
  }
}
</style>