<template>
  <div class="home">
    <div v-if="isLoading" class="flex-column is-loading">
      <h2>Loading . . .</h2>
    </div>
    <div v-else class="flex-column">
      <title-card
        title="GameDeals"
        :descriptions="descriptions"
        :showSearchButton="true"
        imgClass="main-img"
      />
      <deal-tile
        v-for="deal in deals"
        :deal="deal"
        :storeData="storeData"
        :key="deal.dealID"
      />
    </div>
  </div>
</template>

<script>
// @ is an alias to /src
import HelloWorld from "@/components/HelloWorld.vue";
import DealTile from "@/components/DealTile.vue";
import TitleCard from "@/components/TitleCard.vue";
const ListOfStores = require("../../sampledata/ListOfStores.json");
const ListOfDeals = require("../../sampledata/ListOfDeals.json");

export default {
  name: "Home",
  data() {
    return {
      isLoading: false,
      deals: new Array(),
      storeData: new Array(),
      descriptions: [
        "Powered by the CheapShark API",
        "This website allows you to find the best current deals on video games across many different websites",
        "Please use the search bar to find a game that you are interested in or take a look at the deals below",
      ],
    };
  },
  props: {},
  components: {
    HelloWorld,
    DealTile,
    TitleCard,
  },
  methods: {
    getListOfDeals() {
      this.isLoading = true;
      fetch("https://www.cheapshark.com/api/1.0/deals?onSale=1", {})
        .then((res) => {
          return res.json();
        })
        .then((json) => {
          json.forEach((deal) => {
            this.deals.push(deal);
          });
        })
        .catch((err) => {
          console.error(err);
          alert(
            "Unable to perform API call because of this error:",
            err,
            "\nThis page will display dummy data to continue to show the structure of the page"
          );
          this.deals = ListOfDeals.listOfDeals;
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
  },
  created() {
    this.getStoreData();
    this.getListOfDeals();
  },
};
</script>

<style scoped>
.flex-column {
  display: flex;
  flex-direction: column;
  justify-content: baseline;
  align-items: center;
}

.is-loading {
  height: 95vh;
}
</style>