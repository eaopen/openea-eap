<template>
<input type="text" v-model="value" v-ab-permission="abPermission" v-on:input="$emit('input', $event.target.value)" />
</template>

<script>
import Vue from 'vue';

export default {
	props: ['value',"serialNo","abPermission"],
  	methods: {
    
  	},
  	data :function () {
	    return {
	    }
	},
 	mounted : function(){
 		//无权限或者已经有值了，则不需要处理
		if(this.abPermission ==="n"||this.abPermission ==="r"||this.value){
			return;
		}
 		
		var url = Vue.__ctx + "/sys/serialNo/getNextIdByAlias";
		var that = this;
		var post = Vue.baseService.postForm(url, {
			alias:that.serialNo
		});
		$.getResultData(post, function(data) {
			that.$emit('input', data);
		});
 	}
}
</script>
