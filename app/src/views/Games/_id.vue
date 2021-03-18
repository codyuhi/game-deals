<template>
  <div class="game">
    <title-card
      title="Game Details"
      :descriptions="descriptions"
      :showSearchButton="true"
      imgClass="games-img"
    />
    <p>{{ game }}</p>
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
      game: {},
      storeData: new Array(),
      descriptions: [],
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
      this.descriptions = ["More information about " + this.game.info.title];
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