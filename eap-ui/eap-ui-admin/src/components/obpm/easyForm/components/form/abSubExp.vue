<template>
	<a v-on:click="expSub">
		<slot></slot>
	</a>
</template>

<script>
import Vue from 'vue';

export default {
	//value：子表数据；tableKey：子表key；
	//columnKeys（可选）：默认字段，多个","分隔；fixed（可选）：当存在默认字段时只显示默认字段，1/0（是/否）
  	props: ['value','tableKey','columnKeys','fixed'],
  	data :function () {
	    return {
	    	
	    }
	},
	mounted : function(){
 	},
	methods: {
		expSub : function(){
			var that = this;
			var conf = {
				height:200,
				width:800,  
				url: "/form/tableExcel/tableExcelExpDialog.html?tableKey=" + this.tableKey,
				title:"导出对话框",
				topOpen:true,
				btn:true, 
				closeBtn:1,
			}
			if(this.columnKeys){
				var passData = {
					columnKeys : this.columnKeys,
					fixed : this.fixed||"0"
				};
				conf.passData = passData;
			}
			
			conf.ok = function(index,innerWindow){
				var expColumns = innerWindow.getData();//获取导出的字段
				expColumns = expColumns.filter(function(column){
					return column.exp;
				});
				
				var list = [];
				var columnKeys = [];
				expColumns.forEach(function(column){
					columnKeys.push(column.key);
				});
				that.value.forEach(function(item){
					var json = {};
					expColumns.forEach(function(column){
						json[column.key] = item[column.key]||"";
					});
					list.push(json);
				});
				var url = Vue.__ctx + "/form/formDefData/exportExcel";
				var form = $("<form>");//定义一个form表单
				form.attr("method", "post");
	            form.attr("action", url);//URL
	            
	            var inputT = $("<input>");
                inputT.attr("type", "hidden");
                inputT.attr("name", "tableKey");
                inputT.attr("value", that.tableKey);
                form.append(inputT);
                
                var inputL = $("<input>");
                inputL.attr("type", "hidden");
                inputL.attr("name", "list");
                inputL.attr("value", JSON.stringify(list));
                form.append(inputL);
                
                var inputC = $("<input>");
                inputC.attr("type", "hidden");
                inputC.attr("name", "columnKeys");
                inputC.attr("value", columnKeys.join(","));
                form.append(inputC);
                $("body").append(form);//将表单放置在web中
                form.submit();//表单提交 
                form.remove();//移除该临时元素
                
                jQuery.Dialog.close(innerWindow);
			};
			
			jQuery.Dialog.open(conf); 
	    }
  	}
}
</script>
