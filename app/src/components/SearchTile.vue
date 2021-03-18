<template>
  <div
    class="search-tile flex-column"
    :title="'View more information about ' + game.external"
  >
    <hr />
    <router-link :to="'/games/' + game.gameID">
      <div class="child-container">
        <div class="flex-row cell-left">
          <img
            :src="game.thumb"
            class="thumbnail-image"
            :alt="'Cover art for ' + game.external"
          />
        </div>
        <div class="flex-row cell-right">
          <div class="flex-column">
            <div class="flex-row">
              <h2 class="game-title">{{ game.external }}</h2>
            </div>
            <div class="flex-column">
              <p>
                Cheapest current deal for {{ game.external }} is <strong>${{
                  game.cheapest
                }}</strong>
              </p>
              <p>
                <router-link
                  :to="'/deals/' + game.cheapestDealID"
                  class="yellow-link"
                  >Click here</router-link
                >
                to view more information about this deal
              </p>
            </div>
          </div>
        </div>
      </div>
    </router-link>
    <hr />
  </div>
</template>

<script>
export default {
  props: {
    game: Object,
  },
};
</script>

<style scoped>
*,
*:hover {
  color: #242423 !important;
  text-decoration: none;
}

p {
  margin: 15px 25px 15px 25px;
}

.container {
  width: 80vw;
}

.container:hover {
  cursor: pointer;
}

.child-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  margin: 15px 0 15px 0;
}

hr {
  width: 100%;
  margin: 0;
}

.cell-left {
  flex: 4;
}

.cell-right {
  flex: 5;
}

.flex-row {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: center;
  width: 100%;
}

.flex-column {
  display: flex;
  flex-direction: column;
  align-content: center;
  width: 100%;
}

.thumbnail-image {
  width: 100%;
  height: auto;
  max-width: 80vw;
}

.game-title {
  margin: 15px;
}

.yellow-link {
  color: #f5cb5c !important;
  text-shadow: 1px 1px 1px var(--dark);
  z-index: 5;
}

@media only screen and (min-width: 450px) {
  .thumbnail-image {
    max-width: 500px;
    margin-left: 25px;
  }

  .child-container {
    flex-direction: row;
  }

  .cell {
    text-align: left;
  }
}
</style>