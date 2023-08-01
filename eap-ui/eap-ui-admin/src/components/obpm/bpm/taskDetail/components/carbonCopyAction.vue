<template>
    <el-form ref="form" :model="form">
      <el-form-item label="抄送人" prop="users" :rules="{
        required: true, message: '请填写备注', trigger: 'change'
      }">
      </el-form-item>
      <el-form-item label="备注" prop="opinion" :rules="{
        required: true, message: '请填写备注', trigger: 'blur'
      }">
        <el-input v-model="form.opinion"></el-input>
      </el-form-item>
    </el-form>
</template>
<script>
export default {
  name: "carbonCopyAction",
  data(){
    return {
      form:{
        users: '',
        opinion: '',
      },

      userList: []
    }
  }
}
</script>

