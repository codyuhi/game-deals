<template>
  <div class="home">
    <!-- <img alt="Vue logo" src="../assets/logo.png"/>
    <HelloWorld msg="Welcome to Your Vue.js App"/> -->
    <!-- <div class="deal-card" v-for="deal in deals" :key="deal.dealID">
      <p>{{ deal }}</p>
    </div> -->
    <div class="flex-column">
      <search-tile
        v-for="deal in deals"
        :deal="deal"
        :storeData="storeData"
        :key="deal.dealID"
      />
    </div>
    <router-link to="/games/test">Test Game</router-link>
  </div>
</template>

<script>
// @ is an alias to /src
import HelloWorld from "@/components/HelloWorld.vue";
import SearchTile from "@/components/SearchTile.vue";
const ListOfStores = require("../../sampledata/ListOfStores.json");
const ListOfDeals = require("../../sampledata/ListOfDeals.json");

export default {
  name: "Home",
  data() {
    return {
      isLoading: false,
      deals: new Array(),
      storeData: new Array(),
    };
  },
  props: {},
  components: {
    HelloWorld,
    SearchTile,
  },
  methods: {
    getListOfDeals() {
      // this.isLoading = true;
      // fetch("https://www.cheapshark.com/api/1.0/deals?onSale=1", {})
      //   .then((res) => {
      //     return res.json();
      //   })
      //   .then((json) => {
      //     json.forEach((deal) => {
      //       this.deals.push(deal);
      //     });
      //   })
      //   .catch((err) => {
      //     console.error(err);
      //   })
      //   .finally((fin) => {
      //     this.isLoading = false;
      //   });
      this.deals = ListOfDeals.listOfDeals;
    },
    getStoreData() {
      if (localStorage.storeData) {
        this.storeData = JSON.parse(localStorage.storeData);
        return;
      }
      // fetch("https://www.cheapshark.com/api/1.0/stores", {
      // })
      //   .then((res) => {
      //     return res.json();
      //   })
      //   .then((json) => {
        
      //     this.storeData = json;
      //   localStorage.storeData = JSON.stringify(this.storeData)
      //   })
      //   .catch((err) => {
      //     console.error(err);
      //   });
      this.storeData = ListOfStores.listOfStores;
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
  align-content: center;
}
</style>