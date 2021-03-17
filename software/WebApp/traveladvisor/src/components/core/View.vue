<template>
    <v-content >
    <div id="core-view">
      <CaptureErrorSnackbar>
        <v-fade-transition mode="out-in">
          <router-view class="ml-6 mr-2" />
        </v-fade-transition>
      </CaptureErrorSnackbar>
    </div>
  </v-content>
</template>

<script>
import CaptureErrorSnackbar from "@/components/error/CaptureErrorSnackbar";

export default {
  name: "CoreView",
  components: {
    CaptureErrorSnackbar
  },
  metaInfo() {
    return {
      title: "TravelAdvisor"
    };
  }
};
</script>

<style>
</style>