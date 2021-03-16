<template>
  <div id="app">
    <div id="nav">
      <b-navbar toggleable="lg" type="dark" variant="info">
        <b-navbar-brand
          ><router-link to="/"
            ><i class="fa fa-gamepad"></i> GameDeals</router-link
          ></b-navbar-brand
        >

        <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>

        <b-collapse id="nav-collapse" is-nav>
          <!-- Right aligned nav items -->
          <b-navbar-nav class="ml-auto">
            <b-nav-form>
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
          </b-navbar-nav>
        </b-collapse>
      </b-navbar>
    </div>
    <router-view />
  </div>
</template>

<script>
import Vue from "vue";
import { BootstrapVue, IconsPlugin } from "bootstrap-vue";

import "bootstrap/dist/css/bootstrap.css";
import "bootstrap-vue/dist/bootstrap-vue.css";

Vue.use(BootstrapVue);
Vue.use(IconsPlugin);

export default {
  data() {
    return {
      searchString: "",
    };
  },
  methods: {},
  computed: {
    computedSearchLink() {
      return this.searchString && this.searchString !== ""
        ? "/SearchResults/" + this.searchString
        : "/";
    },
  },
};
</script>

<style>
:root {
  --darkest: #242423;
  --dark: #333533;
  --banana: #f5cb5c;
  --light: #cfdbd5;
  --lightest: #e8eddf;
}

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

router-link,
a {
  color: var(--banana) !important;
}

.navbar {
  background-color: var(--darkest) !important;
}

.btn {
  background-color: var(--banana) !important;
  color: var(--dark) !important;
}
</style>
