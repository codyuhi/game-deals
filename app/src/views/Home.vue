<template>
  <div class="home">
    <!-- <img alt="Vue logo" src="../assets/logo.png"/>
    <HelloWorld msg="Welcome to Your Vue.js App"/> -->
    <div class="deal-card" v-for="deal in deals" :key="deal.dealID">
      <p>{{ deal }}</p>
    </div>
    <router-link to="/game/test">Test Game</router-link>
  </div>
</template>

<script>
// @ is an alias to /src
import HelloWorld from "@/components/HelloWorld.vue";

export default {
  name: "Home",
  data() {
    return {
      isLoading: false,
      deals: new Array(),
    };
  },
  props: {},
  components: {
    HelloWorld,
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
        })
        .finally((fin) => {
          this.isLoading = false;
        });
    },
  },
  created() {
    this.getListOfDeals();
  },
};
</script>
