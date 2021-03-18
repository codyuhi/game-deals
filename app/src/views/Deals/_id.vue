<template>
  <div class="deal">
    <p>{{ deal }}</p>
  </div>
</template>

<script>
const dealData = require("../../../sampledata/Deal.json");
const ListOfStores = require("../../../sampledata/ListOfStores.json");

export default {
  data() {
    return {
      deal: {},
      storeData: new Array(),
    };
  },
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
  },
  created() {
    this.getStoreData();
    this.getDealData();
  },
};
</script>

<style scoped>
</style>