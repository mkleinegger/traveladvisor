import { mapGetters } from 'vuex';

export default {
    props: {
        capture: {
            default: false,
            type: Boolean
        }
    },
    data() {
        return {
            error: null
        };
    },
    computed: mapGetters({
        errorLocation: "locations/error",
        errorBranchen: "branchen/error",
        errorBoni: "bonuses/error",
        errorUser: 'users/error',
        errorRezensionen: 'rezensionen/error'
    }),
    errorCaptured(error) {
        this.throwError(error);
    },
    watch: {
        errorLocation() {
            if (this.errorLocation != null) {
                this.throwError(this.errorLocation);
            }
        },
        errorBranchen() {
            if (this.errorBranchen != null) {
                this.throwError(this.errorBranchen);
            }
        },
        errorBoni() {
            if (this.errorBoni != null) {
                this.throwError(this.errorBoni);
            }
        },
        errorUser() {
            if (this.errorUser != null) {
                this.throwError(this.errorUser);
            }
        },
        errorRezensionen() {
            if (this.errorRezensionen != null) {
                this.throwError(this.errorRezensionen);
            }
        }
    },
    methods: {
        reset() {
            this.error = null;
            this.$emit("reset");
        },
        throwError(error) {
            this.error = error;
            this.$emit("error", error);

            // Optionally capture errors.
            if (this.capture) return false;
        }
    },
    render() {
        return this.$scopedSlots.default({
            error: this.error,
            reset: this.reset
        });
    }
};