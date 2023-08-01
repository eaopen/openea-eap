<template>
<el-button type="success" size="mini" icon="el-icon-plus" @click="addSub">
	<slot></slot>
</el-button>
</template>

<script>
import Vue from 'vue';

export default {
  	props: ['value',"initData","abInitFirstRow"],
  	data :function () {
	    return {
	    	
	    }
	},
	mounted : function(){
 		if(this.abInitFirstRow){
 			//如果有编辑权限才会自动添加行
 			var elPermission = this.getPermission();
 			if(!elPermission || elPermission === "n" || elPermission === "r"){
 				return;
 			}
 			
 			if(!this.value || this.value.length ==0){
 				this.addSub();
 			}
 		}else{
 			if(!this.value){
 	 			this.$emit('input', []);
 	 		}
 		}
 	},
	methods: {
	    addSub : function(){
	    	var list = this.value;
	    	if(!list){
	    		list = [];
	    		this.$emit('input', list);
	    	}
	    	var json = JSON.parse(JSON.stringify(this.initData));//深copy
	    	list.unshift(json);
	    },
  	}
}
</script>
