<template>
	<div v-on:click="impSub">
   		<input style="display: none;" type="file" accept=".xls,.xlsx">
   			<slot></slot>
   		</input>
    </div>
</template>

<script>
import Vue from 'vue';

export default {
  	props: ['value','tableKey'],
  	data :function () {
	    return {
	    	
	    }
	},
	mounted : function(){
		var input = this.$el.children[0];
		var tableKey = this.tableKey;
		var that = this;
		this.$nextTick(function () {
			this.$el.addEventListener('change', function (e) {
		        var file = e.target.files
		        if (file.length === 0) {
		        	jQuery.Toast.error("请选择文件");
	          		return;
		        }
		        var reader = new FileReader()
		        if (typeof FileReader === 'undefined') {
	          		return;
		        }
	        	reader.readAsArrayBuffer(file[0]);
		        reader.onload = function () {
	        		var blob = new Blob([reader.result], {});
	        		var fd = new FormData();
	        		fd.append("file", blob);
	        		fd.append("tableKey", tableKey);
	        		var xhr = new XMLHttpRequest();
	        		var url = Vue.__ctx + "/form/formDefData/importExcel";
	        		xhr.open('post', url , true);
	        		xhr.onreadystatechange = function () {
	        			input.value = "";
	        			if (xhr.readyState != 4 || xhr.status != 200) {
	        				return;
	        			}
	        			var response = parseToJson(xhr.response);
	        			if(!response.isOk){
	        				jQuery.Toast.error(response.cause);
	        				return;
	        			}
	        			
	        			//把数据增加到子表中
	        			var list = that.value;
	        	    	if(!list){
	        	    		list = [];
	        	    		that.$emit('input', list);
	        	    	}
	        	    	
	        	    	list.pushAll(response.data);
	        		};
	        		xhr.send(fd);
		        };
			}.bind(this));
	    });
 	},
	methods: {
		impSub : function(){
			this.$el.children[0].click();
	    }
  	}
}
</script>
