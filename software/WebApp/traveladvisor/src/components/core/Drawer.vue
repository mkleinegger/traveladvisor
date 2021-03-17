<template>
  <v-navigation-drawer
    id="app-drawer"
    v-model="inputValue"
    app
    src="https://images.unsplash.com/photo-1546299138-7574176aad1a?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80"
    dark
    floating
    mobile-break-point="991"
    persistent
    width="260"
  >
    <template v-slot:img="attrs">
      <v-img v-bind="attrs" gradient="to top, rgba(0, 0, 0, .7), rgba(0, 0, 0, .7)" />
    </template>

    <v-list-item two-line>
      <v-list-item-avatar color="white">
        <v-img src="logo.png" height="35" contain />
      </v-list-item-avatar>

      <v-list-item-title class="title">TravelAdvisor</v-list-item-title>
    </v-list-item>

    <v-divider class="mx-3 mb-3" />

    <v-list nav>
      <v-list-item
        v-for="(link, i) in links"
        :key="i"
        :to="link.to"
        active-class="green white--text"
        v-show="(user != null && link.type.includes(user.typ)) || link.type.includes(null)"
      >
        <v-list-item-action>
          <v-icon>{{ link.icon }}</v-icon>
        </v-list-item-action>

        <v-list-item-title v-text="link.text" />
      </v-list-item>
    </v-list>
  </v-navigation-drawer>
</template>

<script>
import { mapGetters } from "vuex";

export default {
  data: () => ({
    links: [
      {
        icon: "home",
        text: "Home",
        to: "/",
        type: ["besitzer", "besucher", null]
      },
      {
        icon: "person",
        text: "Account",
        to: "/account",
        type: ["besitzer", "besucher", null]
      },
      {
        icon: "room",
        text: "Locations",
        to: "/locations",
        type: ["besitzer"]
      },
      {
        icon: "card_giftcard",
        text: "Prämien",
        to: "/bonuses",
        type: ["besitzer"]
      }
    ]
  }),
  computed: {
    ...mapGetters({ user: "users/user", drawer: "application/drawer" }),
    inputValue: {
      get() {
        return this.drawer;
      },
      set(val) {
        this.$store.dispatch("application/setDrawer", val);
      }
    }
  }
};
</script>

<style lang="scss">
#app-drawer {
  .v-list__tile {
    border-radius: 4px;

    &--buy {
      margin-top: auto;
      margin-bottom: 17px;
    }
  }
}
</style>