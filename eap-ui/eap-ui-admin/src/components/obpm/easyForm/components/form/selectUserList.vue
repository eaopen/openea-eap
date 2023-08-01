<template>
	<div>
    <el-autocomplete
				class="inline-input"
				v-model="input"
				:fetch-suggestions="queryUser"
				placeholder="输入用户名/工号查询"
				@select="handleSelect"
				size="mini"
				style="width: 100%;"
			  >
			  <template slot-scope="{ item }">
				<div :class="{'is-disabled':item.status_ === 0}">
					<span>{{ item.account_ }}</span>
					<span>{{ item.fullname_ }}</span>
					<span>{{ item.group_name_ }}</span>
				</div>
			  </template>
			</el-autocomplete>
      <div class="select-list">
        <span v-for="(item,index) in fullList" :key="item.account_" style="padding-right: 10px">
          <span>{{item.fullname_}}</span>
						<span>
							<i class="el-icon-circle-close" style="color: #2196f3; cursor: pointer" @click="deleteSelect(index)"></i>
						</span>
        </span>
			</div>
  </div>
</template>

<script >
export default {
  props:['value'],
  data(){
    return {
      input: '',
      fullList: []
    }
  },
  created(){
    if(this.value && this.value.length){
      let ids = this.value.join(",")
      this.getUserInfo(ids)
    }
  },
  methods:{
    getUserInfo(str){
      Vue.baseService
        .postForm(Vue.__ctx + "/etech/formCustDialog/listData_ygxz", {
          order: "asc",
          offset: 0,
          limit: 10,
          "id_^VIN": str,
        })
        .then((res) => {
          if (res.rows && res.rows.length) {
            this.fullList = res.rows
          }
        });
    },
    handleSelect(item){
      let user = this.fullList.find(i=>i.id_== item.id_)
      if(!user) this.fullList.unshift(item)
      this.updateValue()
    },
    deleteSelect(index){
      this.fullList.splice(index,1)
      this.updateValue()
    },
    updateValue(){
      let v = this.fullList.map(item=>item.id_);
      this.$emit("update:name", v);
      this.$emit("input", v);
      this.$emit("change", v);
    },
    queryUser(str, cb){
      Vue.baseService
        .postForm(Vue.__ctx + "/etech/formCustDialog/listData_ygxz", {
          order: "asc",
          offset: 0,
          limit: 10,
          fieldRelation: "OR",
          "account_^VLK": str,
          "fullname_^VLK": str
        })
        .then((res) => {
          if (res.rows && res.rows.length) {
            cb(res.rows)
          }else {
            cb([])
          }
        });
    }
  }
}
</script>

<style scoped>
</style>
