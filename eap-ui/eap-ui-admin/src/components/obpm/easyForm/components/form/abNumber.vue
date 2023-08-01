<template>
  <input class="form-control" v-model="viewValue"  v-on:blur="change2ViewValue()" v-on:input="updateModelData($event.target.value)" />
</template>

<script>
/*
 * 如：
 *<ab-number type="text" decimal-place="2" coin-value="￥" :comdify="true" class=" form-control" v-model="data.lcywdx.a" v-ab-permission="permission.lcywdx.sca.a" desc="三层a-a" v-ab-validate="{&quot;maxlength&quot;:50}"></ab-number>
 */

export default {
  props:{
    value:{}, // Modelvalue
    comdify:{default:false },//是否展示千分位
    decimalPlace:{default:0},//小数位数，如果指定则进行格式化
    coinValue:{ default:"" } //金额前缀 ￥
  },
  data :function () {
    return {
      viewValue:"",
      beInputting :false
    }
  },
  created : function(){

  },
  mounted : function(){
    this.viewValue =  this.formatNumber(this.value);
  },
  methods: {
    updateModelData : function(data){
      this.beInputting = true;
      this.$emit('input', $.toNumber(data,true));
    },
    change2ViewValue : function(){
      var that = this;
      this.beInputting = false;
      window.setTimeout(function(){
        that.viewValue =  that.formatNumber(that.value);
      },10);
    },
    formatNumber : function(tempValue){
      if(!tempValue) return '';
      if(this.comdify){
        tempValue = $.comdify(tempValue);
      }
      if(this.coinValue && tempValue){
        tempValue = this.coinValue + tempValue;
      }

      if (this.decimalPlace > 0) {
        if (tempValue.indexOf('.') != -1) {
          var ary = tempValue.split('.');
          var temp = ary[ary.length - 1];
          if (temp.length > 0 && temp.length < this.decimalPlace) {
            var zeroLen = '';
            for ( var i = 0; i < this.decimalPlace
            - temp.length; i++) {
              zeroLen = zeroLen + '0';
            }
            tempValue = tempValue + zeroLen;
          }else if(temp.length > 0 && temp.length > this.decimalPlace ){
            temp = temp.substring(0,this.decimalPlace);
            ary[ary.length - 1] =temp;
            tempValue =ary.join(".");
          }
        } else {
          var zeroLen = '';
          for ( var i = 0; i < this.decimalPlace; i++) {
            zeroLen = zeroLen + '0';
          }
          tempValue = tempValue + '.' + zeroLen;
        }
      }

      return tempValue;
    }
  },
  watch : {
    //数据更新时 更新格式化
    value : function(newVal,oldVal){
      if(this.beInputting) return ;
      this.viewValue =  this.formatNumber(newVal);
    }
  }
}
</script>
