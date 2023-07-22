<template>
  <el-col :span="14" style="text-align: right">
    <el-pagination
      :current-page="data.curPage"
      layout="total, sizes, prev, pager, next, jumper"
      :page-size="data.pageSize"
      :page-sizes="[20, 100, 200, 500]"
      :total="data.total"
      @current-change="handleCurrentChange"
      @size-change="handleSizeChange"
    />
  </el-col>
</template>
<script>
  export default {
    name: 'Page',
    components: {},
    props: {
      data: {
        type: Object,
        default: null,
      },
      code: {
        type: String
      }
    },
    methods: {
      handleSizeChange(v) {
        this.$emit('handleChange', {
          pageSize: v,
        })
        this.$store.dispatch('list/setListConfigs',{code: this.code, pagesize: v})
      },
      handleCurrentChange(v) {
        this.$emit('handleChange', {
          curPage: v,
        })
        this.$store.dispatch('list/setListConfigs',{code: this.code, page: v})
      },
      // todo
    },
  }
</script>
