<template>
  <div class="game">
    <p>{{ game }}</p>
  </div>
</template>

<script>
const gameData = require("../../../sampledata/Game.json");
const ListOfStores = require("../../../sampledata/ListOfStores.json");

export default {
  data() {
    return {
      game: {},
      storeData: new Array(),
    };
  },
  methods: {
    getGameData() {
      // fetch("https://www.cheapshark.com/api/1.0/games?id=" + this.$route.path.slice(7), {})
      //   .then((res) => {
      //     return res.json();
      //   })
      //   .then((json) => {
      //     this.game = json;
      //   })
      //   .catch((err) => {
      //     console.error(err);
      //   });
      this.game = gameData.game;
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
    this.getGameData();
  },
};
</script>

<style scoped>
</style>