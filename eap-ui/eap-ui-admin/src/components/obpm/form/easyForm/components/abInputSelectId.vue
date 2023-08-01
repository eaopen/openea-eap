
<template>
<!-- param传参实例：{"order": "asc","offset": 0,"limit": 10,"fieldRelation": "OR","businessname":  "{key}","businessnum": "{key}","businessname^VLK": "{key}","businessnum^VLK": "{key}","test":"{key2}"} -->
  <!-- <el-autocomplete
    style="width: 100%"
    size="mini"
    v-model="inputValue"
    :fetch-suggestions="querySearchAsync"
    :placeholder="placeholder"
    :hide-loading="true"
    @select="hselect"
  >
    <template slot-scope="{ item }">
      <div class="name">{{ item[name] }} {{ item[name1] }}</div>
    </template>
  </el-autocomplete> -->
  <el-select
    v-model="inputValue"
    style="width: 100%"
    size="mini"
    filterable
    remote
    reserve-keyword
    :placeholder="placeholder"
    :remote-method="remoteMethod">
    <el-option
      v-for="item in list"
      :key="item[id]"
      :label="item.label"
      :value="item[id]">
    </el-option>
  </el-select>
</template>

<script>
import Vue from "vue";

export default {
  // /form/formCustDialog/listData_jhrwxz
  // plantaskid,plantasknum,plantaskname
  // '{"plantasknum": "{key}","plantaskname": "{key}","plantaskrelate": "{key2}","plantasknum^VLK:":"{key}","plantaskname^VLK": "{key}","order": "asc","offset": 0,"limit": 10}'
  // '{"plantaskid": "{key}","plantaskrelate": "{key2}","order": "asc","offset": 0,"limit": 10}'
  // ['id','num','processname']
  // {  order: asc,offset: 0,limit: 10, fieldRelation: OR}
  props: ["value", "path", "value2", "idparam","nameparam", "placeholder", "shownames", "value2"],
  data() {
    return {
      inputValue: "",
      timeout: null,
      id: "",
      name1: "",
      name2: "",
      list: [],
    };
  },
  methods: {
    formatList(list, auto = false){
      let arr = []
      list.forEach(item=>{
        let cache = {}
        let label = ''
        let shownames = this.shownames.split(',')
        console.log(shownames)
        shownames.forEach((i, index)=>{
          cache[i] = item[i]
          if(index){
            label += item[i] +' '
          }
        })
        cache.label = label
        arr.push(cache)
      })
      this.list = arr
      console.log(this.list)
      if(auto && this.list.length){
        this.inputValue = arr[0][this.id]
        this.$emit("update:name", this.inputValue);
        this.$emit("input", this.inputValue);
      }
    },
    updateValue(){
      console.log('updateValue')
      this.list = []
      this.inputValue = ''
      this.$emit("update:name", '');
      this.$emit("input", '');
    },
    creatMethods(){
      if(this.inputValue){
        let param = this.idparam;
        let that = this
        param = param.replace(/\{key}/g, this.inputValue);
        if(param.indexOf('{key2}')!== -1){
          if(this.value2){
            param = param.replace(/\{key2}/g, this.value2);
          }else {
            param = param.replace(/\{key2}/g, '');
          }
        }
        let paramObj = JSON.parse(param);
        Vue.baseService
          .postForm(Vue.__ctx + this.path, paramObj)
          .then(function (data) {
            if (data.code == 200) {
              if (data.rows && data.rows.length) {
                that.formatList(data.rows,true)
              } else if (data.data && data.rows.length) {
                that.formatList(data.data,true)
              } else {
                that.inputValue = ""
                that.list = []
                that.$emit("update:name", '');
                that.$emit("input", '');
              }
            } else {
              that.inputValue = ""
              that.list = []
              that.$emit("update:name", '');
              that.$emit("input", '');
            }
          });
      }
    },
    remoteMethod(queryString, cb) {
      this.list = []
      let that = this;
      if (queryString) {
        let param = this.nameparam;
        param = param.replace(/\{key}/g, queryString);
        if(param.indexOf('{key2}')!== -1){
          if(this.value2){
            param = param.replace(/\{key2}/g, this.value2);
          }else {
            param = param.replace(/\{key2}/g, '');
          }
        }
        let paramObj = JSON.parse(param);
        Vue.baseService
          .postForm(Vue.__ctx + this.path, paramObj)
          .then(function (data) {
            if (data.code == 200) {
              if (data.rows && data.rows.length) {
                that.formatList(data.rows)
              } else if (data.data && data.rows.length) {
                that.formatList(data.data)
              } else {
                that.inputValue = ""
                that.list = []
                that.$emit("update:name", '');
                that.$emit("input", '');
              }
            } else {
              that.inputValue = ""
              that.list = []
              that.$emit("update:name", '');
              that.$emit("input", '');
            }
          });
      }
    },
  },
  mounted: function () {
    if (this.shownames) {
      this.id = this.shownames.split(",")[0];
      this.name1 = this.shownames.split(",")[1];
      this.name2 = this.shownames.split(",")[2];
    }
    this.inputValue = this.value;
    if(this.value){
      this.creatMethods()
    }
  },
  watch:{
    value(val){
      this.inputValue = val
      this.creatMethods()
      this.$emit("update:name", this.inputValue);
      this.$emit("input", this.inputValue);
    }
  }
};
</script>
