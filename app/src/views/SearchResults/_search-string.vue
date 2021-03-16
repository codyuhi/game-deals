<template>
  <div class="search-results">
    <h1>This is the search results page</h1>
    <router-link to="/">Return home</router-link>
  </div>
</template>

<script>
export default {
  props: ["searchString"],
};
</script>