<template>
  <div class="home">
    <!-- <img alt="Vue logo" src="../assets/logo.png"/>
    <HelloWorld msg="Welcome to Your Vue.js App"/> -->
    <p>TEST</p>
    <router-link to="/game/test">Test Game</router-link>
  </div>
</template>

<script>
// @ is an alias to /src
import HelloWorld from '@/components/HelloWorld.vue'

export default {
  name: 'Home',
  components: {
    HelloWorld
  }
}
</script>
