<template>
  <el-select
    v-model="val"
    :placeholder="ph"
    size="mini"
    :class="{ 'is-error': err }"
    :disabled="!permission || permission !== 'w'"
  >
    <el-option
      v-for="item in options"
      :key="item.key"
      :label="item.name"
      :value="item.key"
    >
    </el-option>
  </el-select>
</template>
<script>
export default {
  name: "multSelect",
  props: ["value", "placeholder", "options", "permission", "required"],
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
        return "请选择";
      }
    },
  },
};
</script>