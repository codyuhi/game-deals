<template>
  <div class="search-results">
    <p>{{ searchResults }}</p>
  </div>
</template>

<script>
const searchResults = require("../../../sampledata/SearchResults.json");

export default {
  data() {
    return {
      searchResults: new Array(),
    };
  },
  methods: {
    getSearchResults() {},
  },
  created() {
    this.getSearchResults();
  },
};
</script>