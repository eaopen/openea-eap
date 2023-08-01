<template>
  <el-date-picker
    :class="{ 'is-error': err }"
    size="mini"
    v-model="val"
    :type="type && type == 'time' ? 'datetime' : 'date'"
    :placeholder="ph"
    :value-format="
      type && type == 'time' ? 'yyyy-MM-dd HH:mm:ss' : 'yyyy-MM-dd'
    "
    :disabled="!permission || permission !== 'w'"
  >
  </el-date-picker>
</template>
<script>
export default {
  name: "multInput",
  props: ["value", "placeholder", "type", "format", "permission", "required"],
  data() {
    return {
      val: "",
      err: false,
    };
  },
  mounted() {
    if (this.value) this.val = "" + this.value;
  },
  watch: {
    val(v, ov) {
      if (this.required) {
        if (v) {
          this.err = false;
        } else {
          this.err = true;
        }
      }
      this.$emit("update:name", v);
      this.$emit("input", v);
      this.$emit("change", v);
    },
  },
  computed: {
    ph() {
      if (!this.permission || this.permission !== "w") {
        return "";
      } else if (this.placeholder) {
        return this.placeholder;
      } else {
        return this.type && this.type == "time" ? "请选择时间" : "请选择日期";
      }
    },
  },
};
</script>