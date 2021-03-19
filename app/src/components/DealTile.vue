<template>
  <div
    class="container flex-column"
    :title="'View this deal for ' + deal.title"
  >
    <hr />
    <router-link :to="'/deals/' + deal.dealID">
      <div class="child-container">
        <div class="flex-row cell-left">
          <img
            :src="deal.thumb"
            class="thumbnail-image"
            :alt="'Cover art for ' + deal.title"
          />
        </div>
        <div class="flex-row cell-right">
          <div class="flex-column">
            <div class="flex-column">
              <h2 class="game-title">
                <router-link :to="'/games/' + deal.gameID">{{
                  deal.title
                }}</router-link>
              </h2>
              <p class="release-date">
                Released {{ convertDate(deal.releaseDate) }}
              </p>
            </div>
            <div class="flex-row">
              <div class="cell">
                <p>
                  Sale Price:
                  <span class="sale-price">${{ deal.salePrice }}</span> on
                  <span class="store"
                    >{{ getStoreName(deal.storeID) }}
                    <img
                      class="store-icon"
                      :src="
                        'https://www.cheapshark.com' +
                        getStoreIcon(deal.storeID)
                      "
                  /></span>
                </p>
              </div>
              <div class="cell">
                <p>
                  Normal Price:
                  <span class="normal-price">${{ deal.normalPrice }}</span>
                </p>
              </div>
            </div>
            <div class="flex-row">
              <div class="cell">
                <p class="savings">
                  Savings: {{ parseFloat(deal.savings).toFixed(2) }}%
                </p>
              </div>
              <div class="cell">
                <p class="deal-rating">
                  Deal Rating: {{ deal.dealRating }} / 10
                </p>
              </div>
            </div>
            <div class="flex-row" v-if="deal.steamRatingText">
              <div class="cell">
                <p v-if="deal.steamRatingText">
                  Rated
                  <span class="steam-rating-text">{{
                    deal.steamRatingText
                  }}</span>
                  on Steam <i class="fa fa-steam"></i>
                </p>
              </div>
              <div class="cell">
                <p v-if="deal.steamRatingText">
                  (<span class="rating-percent">{{
                    deal.steamRatingPercent
                  }}</span
                  >% positive out of
                  <span class="rating-count">{{ deal.steamRatingCount }}</span>
                  Steam ratings)
                </p>
              </div>
            </div>
            <div class="flex-row">
              <div class="cell">
                <p v-if="deal.metacriticScore">
                  Metacritic Score:
                  <span class="metacritic-score">{{
                    deal.metacriticScore
                  }}</span>
                </p>
              </div>
              <div class="cell">
                <router-link to="/"
                  ><p @click="addToFavorites(deal)" class="yellow-text">
                    Add to Favorites <i class="fa fa-heart"></i></p
                ></router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </router-link>
    <hr />
  </div>
</template>

<script>
export default {
  props: {
    deal: Object,
    storeData: Array,
  },
  // data() {
  //   return {
  //   }
  // }
  methods: {
    convertDate(unixDate) {
      const format = {
        day: "numeric",
        month: "short",
        year: "numeric",
      };
      return new Date(unixDate * 1000).toLocaleString("en-US", format);
    },
    getStoreName(storeId) {
      const storeData = this.$props.storeData;
      for (let i = 0; i < storeData.length; i++) {
        if (storeId === storeData[i].storeID) {
          return storeData[i].storeName;
        }
      }
      return "Anonymous store";
    },
    getStoreIcon(storeId) {
      const storeData = this.$props.storeData;
      for (let i = 0; i < storeData.length; i++) {
        if (storeId === storeData[i].storeID) {
          return storeData[i].images.icon;
        }
      }
      return undefined;
    },
    addToFavorites(deal) {
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
        name: deal.title,
        id: deal.gameID,
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
};
</script>

<style scoped>
*,
*:hover {
  color: #242423 !important;
  text-decoration: none;
}

.container {
  width: 80vw;
}

.container:hover {
  cursor: pointer;
}

.child-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 80vw;
  margin: 15px 0 15px 0;
}

hr {
  width: 100%;
  margin: 0;
}

.cell-left {
  /* flex: 3; */
}

.cell-right {
  /* flex: 1; */
}

.cell {
  display: flex;
  flex: 1;
  text-align: center;
}

.flex-row {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  align-items: center;
  width: 100%;
}

.flex-column {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}

.thumbnail-image {
  width: 300px;
  height: auto;
  max-width: 95vw;
}

.game-title {
  margin-top: 15px;
}

.yellow-text {
  color: #f5cb5c !important;
  text-shadow: 1px 1px 1px var(--dark);
  z-index: 5;
}

@media only screen and (min-width: 450px) {
  .child-container {
    flex-direction: row;
  }

  .cell {
    margin-left: 30px;
    text-align: left;
  }
}
</style>