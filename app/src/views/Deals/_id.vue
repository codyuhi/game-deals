<template>
  <div class="deal">
    <title-card
      title="Deal Details"
      :descriptions="descriptions"
      :showSearchButton="false"
      imgClass="deals-img"
    />
    <div class="flex-column content">
      <img
        class="thumbnail-image"
        :src="deal.gameInfo.thumb"
        :alt="'Cover art for ' + deal.gameInfo.name"
      />
      <h2 class="title">
        <router-link
          class="yellow-link"
          :to="'/games/' + deal.gameInfo.gameID"
          >{{ deal.gameInfo.name }}</router-link
        >
      </h2>
      <p>
        Released {{ convertDate(deal.gameInfo.releaseDate) }} by
        {{ deal.gameInfo.publisher }}
      </p>
      <p>
        On sale for ${{ deal.gameInfo.salePrice }} on
        {{ getStoreName(deal.gameInfo.storeID) }}
        <img
          :src="
            'https://www.cheapshark.com' + getStoreIcon(deal.gameInfo.storeID)
          "
        />
        (Usually ${{ deal.gameInfo.retailPrice }})
      </p>
      <p>
        The best deal ever offered for this game was ${{
          deal.cheapestPrice.price
        }}
        on {{ convertDate(deal.cheapestPrice.date) }}
      </p>
      <div v-if="deal.cheaperStores.length > 1">
        <p>There are currently better deals on the following sites:</p>
        <p v-for="d in deal.cheaperStores" :key="d" class="small-text">
          - {{ d }} -
        </p>
      </div>
      <p v-else-if="deal.cheaperStores.length === 1">
        There is currently a better deal at {{ deal.cheaperStores[0] }}
      </p>
      <p v-else>This is the best deal for this game at the moment</p>
      <div v-if="deal.gameInfo.steamRatingText">
        <p>Rated {{ deal.gameInfo.steamRatingText }} on Steam</p>
        <p>
          {{ deal.gameInfo.steamRatingPercent }}% positive out of
          {{ deal.gameInfo.steamRatingCount }} Steam ratings
        </p>
      </div>
      <p v-if="deal.gameInfo.metacriticScore">
        Rated {{ deal.gameInfo.metacriticScore }} on Metacritic.
        <a
          class="yellow-link"
          :href="'https://www.metacritic.com' + deal.gameInfo.metacriticLink"
          >Read reviews</a
        >
      </p>
      <b-button
        size="sm"
        class="my-2 my-sm-0 dealer-button"
        type="submit"
        @click="openDealer()"
        >Go to Dealer Site</b-button
      >
      <p class="smallest-text">
        Clicking the button above will redirect you to the dealer site through a
        CheapShark link. This supports the API provider
      </p>
    </div>
  </div>
</template>

<script>
import TitleCard from "../../components/TitleCard.vue";
const dealData = require("../../../sampledata/Deal.json");
const ListOfStores = require("../../../sampledata/ListOfStores.json");

export default {
  props: {
    searchString: String,
  },
  components: { TitleCard },
  data() {
    return {
      deal: {},
      storeData: new Array(),
      descriptions: [],
    };
  },
  computed: {},
  methods: {
    getDealData() {
      //   fetch(
      //     "https://www.cheapshark.com/api/1.0/deals?id=" + this.$route.path.slice(7),
      //     {}
      //   )
      //     .then((res) => {
      //       return res.json();
      //     })
      //     .then((json) => {
      //       this.deal = json;
      //     })
      //     .catch((err) => {
      //       console.error(err);
      //     });
      this.deal = dealData.deal;
      this.descriptions.push(
        this.deal.gameInfo
          ? "More information about the deal for " +
              this.deal.gameInfo.name +
              " on " +
              this.getStoreName(this.deal.gameInfo.storeID)
          : ""
      );
    },
    getStoreData() {
      if (localStorage.storeData) {
        this.storeData = JSON.parse(localStorage.storeData);
        return;
      }
      //   fetch("https://www.cheapshark.com/api/1.0/stores", {
      //   })
      //     .then((res) => {
      //       return res.json();
      //     })
      //     .then((json) => {
      //       this.storeData = json;
      //     localStorage.storeData = JSON.stringify(this.storeData)
      //     })
      //     .catch((err) => {
      //       console.error(err);
      //     });
      this.storeData = ListOfStores.listOfStores;
    },
    getStoreName(storeId) {
      for (let i = 0; i < this.storeData.length; i++) {
        if (storeId === this.storeData[i].storeID) {
          return this.storeData[i].storeName;
        }
      }
      return "Anonymous store";
    },
    getStoreIcon(storeId) {
      for (let i = 0; i < this.storeData.length; i++) {
        if (storeId === this.storeData[i].storeID) {
          return this.storeData[i].images.icon;
        }
      }
    },
    convertDate(unixDate) {
      const format = {
        day: "numeric",
        month: "short",
        year: "numeric",
      };
      return new Date(unixDate * 1000).toLocaleString("en-US", format);
    },
    openDealer() {
      window.open(
        "https://www.cheapshark.com/redirect?dealID=" +
          this.$route.path.slice(7)
      );
    },
  },
  created() {
    this.getStoreData();
    this.getDealData();
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

.title {
}

.thumbnail-image {
  width: 100%;
  height: auto;
  max-width: 500px;
}

.small-text {
  font-size: 13px;
}

.smallest-text {
  font-size: 10px;
}

.dealer-button {
  margin: 15px !important;
}

.yellow-link {
  text-shadow: 1px 1px 1px var(--dark);
}
</style>