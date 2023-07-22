<template>
  <el-col :span="10" class="mt5" style="height: 36px; display: flex; align-items: center;">
    <b>排序：</b>
    <el-button tyle="default" size="mini" >11</el-button>
    <b style="margin-left: 16px;">筛选：</b>
    <span v-if="!filterList || !filterList.length">暂无筛选条件</span>
    <el-button @click="removeFilter(f)" tyle="default" size="mini" v-for="f in filterList" :key="f.name">{{f.name}}</el-button>
    <!-- {{ filterList }} -->
  </el-col>
</template>
<script>
  import { mapState } from 'vuex'
  export default {
    name: 'filters',
    props: ['code'],
    computed:{
      ...mapState({
        configs: state=>state.list.configs,
      }),
      filterList(){
        if(this.configs && this.configs[this.code] && this.configs[this.code].filter && Object.keys(this.configs[this.code].filter).length){
          let arr = []
          let obj = this.configs[this.code].filter
          Object.keys(obj).forEach(k=>{
            arr.push(Object.assign({}, obj[k], {key: k}))
          })
          return arr
        }else {
          return []
        }
      }
    },
    methods:{
      removeFilter(f){
        let filter = JSON.parse(JSON.stringify(this.configs[this.code].filter))
        delete filter[f.key]
        this.$store.dispatch('list/setListConfigs', {
          code: this.code,
          filter
        })
        this.$emit('clearFilter')
      }
    }
  }
</script>
<style lang="scss" scoped>
  .el-button--mini {
    padding: 4px 6px;
  }
</style>

