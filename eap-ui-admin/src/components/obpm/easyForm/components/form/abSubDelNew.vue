<template>
<el-button size="mini"type="danger" icon="el-icon-delete" @click="delSub">
	<slot></slot>
</el-button>
</template>

<script>
import Vue from "vue";

export default {
  props: ["value", "index"],
  data: function () {
    return {};
  },
  mounted: function () {},
  methods: {
    delSub: function () {
      this.value.splice(this.index, 1);
    },
  },
};
</script>
