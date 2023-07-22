<template>
  <el-button-group style="width: 100px;display: flex;justify-content: flex-end; padding-right: 5px;">
    <el-button type="default" size="large" icon="el-icon-delete-solid"></el-button>
    <el-button type="default" size="large" icon="el-icon-refresh" @click="refreshGrid"></el-button>
    <el-button type="default" size="large" icon="el-icon-s-tools"></el-button>
  </el-button-group>
</template>
<script>
  export default {
    name: 'rightButton',
    data() {
      return {
        onsave: false,
      }
    },
    methods: {
      refreshGrid(){
        this.$emit('refreshGrid')
      }
    },
  }
</script>
<style lang="scss" scoped>
  .el-button--large {
    padding: 7px 10px !important;
  }
</style>

