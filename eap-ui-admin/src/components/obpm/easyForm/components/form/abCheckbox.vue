<template>
	<div><slot :tempData="tempData" ></slot></div>
</template>

<script >
import Vue from 'vue'
/**
 * <ab-checkbox v-model="data.kjcs.ah" v-ab-permission="permission.kjcs.cskj.ah" v-ab-validate="{}" desc="爱好">
        <div slot-scope="scope">
            <label class=" checkbox-inline"><input type="checkbox" name="testttt" v-model="scope.tempData.currentValue" value="y"/>羽毛器</label>
            <label class=" checkbox-inline"><input name="testttt" type="checkbox" v-model="scope.tempData.currentValue" value="l"/>篮球</label>
            <label class=" checkbox-inline"><input type="checkbox" name="testttt" v-model="scope.tempData.currentValue" value="s"/>游泳</label>
        </div>
  </ab-checkbox>
 */
export default {
  props:['value'],
  data :function () {
	    return {
	     tempData: {currentValue:[]},
	    }
	},
  watch : {
  	// modelValue转viewValue的过程
    value : function (viewValue, oldVal){
    	if(viewValue === oldVal)return ;
    	
		var valueArr = [];
		if (viewValue) {
			valueArr = viewValue.split(",");
		}
      this.currentValue = valueArr;
    },
    
    // viewValue转modelValue的过程
    'tempData.currentValue':function (newVal,oldVal){
      if(newVal === oldVal)return ;
      var modelValue = "";
      if(newVal && newVal.length>0){
      	modelValue = newVal.join(",");
      }
      this.$emit('change', modelValue);
      this.$emit('input', modelValue);
    }
  },
  created :function(){
  	if(this.value){
  		this.tempData.currentValue = this.value.split(",");
  	}
  }
}
</script>

<style scoped>
</style>
