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
  width: 100%;
  margin: 15px 0 15px 0;
}

hr {
  width: 100%;
  margin: 0;
}

.cell-left {
  flex: 3;
}

.cell-right {
  flex: 5;
}

.cell {
  display: flex;
  flex: 1;
  margin-left: 30px;
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
  align-content: center;
  width: 100%;
}

.thumbnail-image {
  width: 100%;
  height: auto;
  max-width: 500px;
}

.game-title {
  margin-top: 15px;
}

.store {
}

.store-icon {
}

.sale-price {
}

.normal-price {
}

.metacritic-score {
}

.steam-rating-text {
}

.steam-rating-percent {
}

.steam-rating-count {
}

.release-date {
}

.last-change {
}

@media only screen and (min-width: 450px) {
  .child-container {
    flex-direction: row;
  }

  .cell {
    text-align: left;
  }
}
</style>