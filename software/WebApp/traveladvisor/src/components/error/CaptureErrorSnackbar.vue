<template>
  <FrameError @error="showSnackbar">
    <div class="CaptureErrorSnackbar">
      <slot />
      <v-snackbar color="red" :timeout="6000" v-model="snackbar">{{ message }}</v-snackbar>
    </div>
  </FrameError>
</template>

<script>
import FrameError from "./FrameError";

export default {
  components: {
    FrameError
  },
  data() {
    return {
      snackbar: false,
      message: ""
    };
  },
  methods: {
    showSnackbar(error) {
      this.snackbar = true;
      this.message = error;
    }
  }
};
</script>