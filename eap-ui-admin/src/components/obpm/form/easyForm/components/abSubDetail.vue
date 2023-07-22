<template>
<a v-on:click="showSub">
	<slot></slot>
</a>
</template>

<script>
import Vue from 'vue';

export default {
  	props: ['value',"dialogId","subTempData","pkey"],
  	data :function () {
	    return {
	    }
	},
 	mounted : function(){
 		
 	},
	methods: {
	    showSub : function(){
	    	if(!this.pkey){
	    		this.showSub_old();
	    		return;
	    	}
	    	
	    	var listName = this.dialogId.substring(this.dialogId.indexOf("-") + 1) + "List";
	    	var that = this;
	    	
	    	var json = Vue.tools.CloneUtil.deep(this.value);
	    	json[listName] = Vue.tools.CloneUtil.list(this.value[listName]||[]);
	    	Vue.set(this.subTempData,this.pkey,json);
	    	this.$emit('update:subTempData',this.subTempData);
			var conf = {
				height : '60%',
				width : '80%',
				title : $(this.$el).text(),
				btn : true,
				type : "1",
				content : $('#' + this.dialogId)
			};
			conf.ok = function(index, innerWindow) {
				Vue.set(that.value,listName,that.subTempData[that.pkey][listName]);
				layer.close(index);
			};
			$.Dialog.open(conf);
	    },
	    showSub_old : function(){
	    	var listName = this.dialogId.split("-")[1]+"List";
	    	var that = this;
	    	
	    	Vue.set(this.$parent.$data.subTempData,listName,Vue.tools.CloneUtil.list(this.value[listName]||[]));
			var conf = {
				height : '60%',
				width : '80%',
				title : $(this.$el).text(),
				btn : true,
				type : "1",
				content : $('#' + this.dialogId)
			};
			conf.ok = function(index, innerWindow) {
				Vue.set(that.value,listName,that.$parent.$data.subTempData[listName]);
				layer.close(index);
			};
			$.Dialog.open(conf);
	    }
  	}
}
</script>
