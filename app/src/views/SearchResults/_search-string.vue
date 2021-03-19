<template>
  <div class="search-results flex-column">
    <div v-if="isLoading" class="flex-column is-loading">
      <h2>Loading . . .</h2>
    </div>
    <div v-else>
      <title-card
        title="Search Results"
        :descriptions="descriptions"
        :showSearchButton="true"
        imgClass="search-img"
      />
      <div v-if="searchResults.length < 1" class="empty-result flex-column">
        <h2>
          No Games have been found with the name "{{
            this.$route.path.slice(15)
          }}"
        </h2>
        <h2>Please try another search</h2>
      </div>
      <div v-else>
        <search-tile
          v-for="result in searchResults"
          :key="result.gameID"
          :game="result"
        />
      </div>
    </div>
  </div>
</template>

<script>
const searchResults = require("../../../sampledata/SearchResults.json");
const { default: TitleCard } = require("../../components/TitleCard.vue");
const { default: SearchTile } = require("../../components/SearchTile.vue");

export default {
  components: { TitleCard, SearchTile },
  data() {
    return {
      isLoading: false,
      searchResults: new Array(),
      descriptions: ['Search Results for "' + this.$route.path.slice(15) + '"'],
    };
  },
  methods: {
    getSearchResults() {
      this.isLoading = true;
      fetch(
        "https://www.cheapshark.com/api/1.0/games?title=" +
          this.$route.path.slice(15),
        {}
      )
        .then((res) => {
          return res.json();
        })
        .then((json) => {
          this.searchResults = json;
        })
        .catch((err) => {
          console.error(err);
          alert(
            "Unable to perform API call because of this error:",
            err,
            "\nThis page will display dummy data to continue to show the structure of the page"
          );
          this.searchResults = searchResults.results;
        })
        .finally((fin) => {
          this.isLoading = false;
        });
    },
  },
  created() {
    this.getSearchResults();
  },
  watch: {
    $route(to, from) {
      this.getSearchResults();
      this.descriptions = [
        'Search Results for "' + this.$route.path.slice(15) + '"',
      ];
    },
  },
};
</script>

<style scoped>
.empty-result {
  text-align: center;
  padding: 250px 0 250px 0;
}

.is-loading {
  height: 95vh;
}
</style>