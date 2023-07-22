<template>
  <select class="form-control" v-model="currentValue" v-on:input="$emit('input', $event.target.value)">
    <option value="">请选择</option>
    <option v-for="op in options" :value="op.value">{{op.text}}</option>
  </select>
</template>
<script>

import Vue from 'vue';
export default {
  props: ['value',"dialogKey","param","initParam","opVal","opTxt","isTimer"],
  data :function () {
    return {
      options:[],
      currentValue:"",
      queryParam:"",
      timer:null,
    }
  },
  created: function () {
    this.currentValue = this.value;
    this.getOptions();
  },
  methods: {
    getQueryParam : function(param){
      if(!param) return {};

      if(this.initParam){
        for(var key in this.initParam){
          if(!param[key] && this.initParam[key]){//为空取默认值
            param[key] = this.initParam[key];
          }
        }
      }
      return param;
    },
    getOptions : function(){
      var query = this.getQueryParam(this.param);
      if(JSON.stringify(this.queryParam) === JSON.stringify(query)){
        return;
      }
      this.queryParam = query;

      var url = Vue.__ctx + "/form/formCustDialog/queryListData_" + this.dialogKey;
      var that = this;
      var post = Vue.baseService.postForm(url, this.queryParam);
      post.then(function(result){
        var options = [];

        for(var i=0,item;item = result.rows[i++];){
          options.push({ value: item[that.opVal],text: item[that.opTxt]});
        }
        that.options = options;
      }, function (status) {
        console.error('动态下拉框加载失败！' + status);
      });
    }
  },
  watch : {
    value : function(newVal,oldVal){
      this.currentValue = this.value;
    },
    param : function(newVal,oldVal){
      if(!this.isTimer){
        this.getOptions();
        return;
      }
      if(this.timer){
        clearTimeout(this.timer);//关闭定时器，也就是说在0.5内还有变化，就吞掉上一个定时器-。-
      }
      var that = this;
      //0.5秒后才执行
      this.timer = setTimeout(function(){
        that.getOptions();
      },500);
    }
  }
}
</script>
