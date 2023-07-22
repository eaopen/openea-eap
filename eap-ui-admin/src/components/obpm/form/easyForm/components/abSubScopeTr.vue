<template>
<tr>
	<slot v-bind:permission="permission" v-bind:table-permission="tablePermission"></slot>
</tr>
</template>

<script>
import Vue from 'vue';

export default {
  	props: ['permissiona','tablePermissiona'],
  	data :function () {
	    return {
	    	permission:{},
	    	tablePermission:{}
	    }
	},
 	created : function(){
 		// 修改克隆了的 permission
 		if(this.permissiona){
 			this.permission  = this.abTools.CloneUtil.deep(this.permissiona);
 		}
 		if(this.tablePermissiona){
 			this.tablePermission  = this.abTools.CloneUtil.deep(this.tablePermissiona);
 		}
 	},
	methods: {
		
  	}
}
</script>
