<template>
   <el-table
      :data="list"
      size="mini"
      style="width: 100%">
      <el-table-column
        prop="taskName"
        label="任务名称"
        width="180">
      </el-table-column>
      <el-table-column
        prop="createTime"
        label="任务创建时间"
        width="120">
      </el-table-column>
      <el-table-column
        prop="realCompleteDay"
        label="处理时间"
        width="120">
      </el-table-column>
      <el-table-column
        prop="assignInfo"
        label="候选人"
        width="180">
      </el-table-column>
      <el-table-column
        prop="approverName"
        label="执行人"
        width="180">
      </el-table-column>
      <el-table-column
        prop="planCompleteDay"
        label="处理状态"
        width="180">
      </el-table-column>
      <el-table-column
        label="查看表单"
        width="180">
        <template slot-scope="scope">
          <div>
            <el-button @click="showDialog(scope.row)">打开表单</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
</template>
<script>
import {getHistory,getInstanceData2} from "@/api/obpm/bpm.js"
import easyForm from "@/components/obpm/form/easyForm/dialogForm.vue"
export default {
  name: "approve-histoty",
  props: ["instId"],
  components: { easyForm },
  data(){
    return {
      list: []
    }
  },
  methods:{
    _getHistory(){
      getHistory(this.instId).then(res=>{
        this.list = res.data
      })
    },
    showDialog(row){
      getInstanceData2(row).then(res=>{
        this.$store.dispatch('dialog/showd1',{
          component: easyForm,
          width: '50%',
          params: {
            formHtml: res.data.form.formHtml,
            formData: res.data.data,
            permission: res.data.permission,
            initData: res.data.initData,
            tablePermission: res.data.tablePermission
          }
        })
      })
    }
  },
  created(){
    this._getHistory()
  }
}
</script>

