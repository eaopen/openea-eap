<template>
<span v-if="index !== 0">
  <el-popover placement="top" width="160" v-model="visible">
    <p>确认移除吗？</p>
    <div style="text-align: right; margin: 0">
      <el-button size="mini" type="text" @click="visible = false"
        >取消</el-button
      >
      <el-button type="primary" size="mini" @click="visible = false;delSub()"
        >确定</el-button
      >
    </div>
	<a slot="reference" style="color: #fff">
		<slot></slot>
	</a>
    <!-- <el-button slot="reference">删除</el-button> -->
  </el-popover>
</span>
</template>

<script>

    export default {
        props: ["value", "index"],
        data: function () {
            return {
                visible: false
            };
        },
        mounted: function () {
            console.log(this.index)
        },
        methods: {
    delSub: function () {
        this.value.splice(this.index, 1);
        this.$emit("new", this.value);
    },
  },
};
</script>
