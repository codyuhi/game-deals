<template>
  <div class="title-card flex-column">
    <i class="fa fa-gamepad yellow-text title"></i>
    <h1 class="fancy-font title yellow-text">{{ title }}</h1>
    <p
      class="yellow-text description"
      v-for="description in descriptions"
      :key="description"
    >
      {{ description }}
    </p>
    <b-nav-form v-if="showSearchButton">
      <b-form-input
        size="sm"
        class="mr-sm-2"
        placeholder="Game Name"
        v-model="searchString"
      ></b-form-input>
      <router-link :to="computedSearchLink"
        ><b-button size="sm" class="my-2 my-sm-0" type="submit"
          >Search</b-button
        >
      </router-link>
    </b-nav-form>
  </div>
</template>

<script>
export default {
  props: {
    title: String,
    descriptions: Array,
    showSearchButton: Boolean,
  },
  data() {
    return {
      searchString: "",
    };
  },
  computed: {
    computedSearchLink() {
      return this.searchString && this.searchString !== ""
        ? "/SearchResults/" + this.searchString
        : "/";
    },
  },
};
</script>

<style scoped>
.title-card {
  background-repeat: none;
  background-size: cover;
  background-position: 0px;
  background-image: linear-gradient(
      90deg,
      rgba(232, 237, 223, 0.88) 0%,
      rgba(51, 53, 51, 0.44) 58.88%,
      rgba(36, 36, 35, 0) 100%
    ),
    url("../assets/deal-dark.jpg");
  width: 100vw;
  height: auto;
  text-align: center;
  padding: 50px 0 50px 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.title {
  font-size: 75px;
}

.yellow-text {
  color: #f5cb5c;
  text-shadow: 2px 2px 8px #333533;
}

.description {
  font-size: 17px;
  max-width: 80vw;
  text-align: center;
}

@media only screen and (min-width: 450px) {
  .title {
    font-size: 150px;
  }
  .description {
    font-size: 25px;
  }
}
</style>