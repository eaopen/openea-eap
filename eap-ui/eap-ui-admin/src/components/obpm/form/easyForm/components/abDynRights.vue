<template>

</template>

<script>
    import Vue from 'vue'

    export default {
  	props: ["value","permission","tablePermission"],
  	data :function () {
	    return {
	    	dynRights : null,
	    	sourcePerimission : null,
	    	sourceTablePermission : null,
	    }
	},
  	created : function(){
  		this.sourcePerimission = Vue.tools.CloneUtil.deep(this.permission);
  		this.sourceTablePermission = Vue.tools.CloneUtil.deep(this.tablePermission);
  		if(this.$slots.default[0].text){
  			this.dynRights = JSON.parse(this.$slots.default[0].text);
  		}
  		this.handle();
 	},
 	mounted : function(){
 	},
	methods: {
		//处理权限
	    handle : function(){
	    	if(!this.dynRights){
	    		return;
	    	}
	    	for(var key in this.value){
	    		window.eval("var " + key);
		    	eval(key + " = this.value." + key + ";");
	    	}
	    	var that = this;
    	 	this.dynRights.forEach(function(_item_){
    	 		var func;
    	 		eval("func = function() { " + _item_.script + " };");
    	 		var permission = that.sourcePerimission;
    	 		
    	 		_item_.permissions.split(",").forEach(function(ite){
    	 			var p;
        	 		eval("p = " + ite + ";");//源权限
        	 		//权限越界检查
        	 		if(p=="n"||(p=="r"&&(_item_.rights=="w"||_item_.rights=="b"))){
        	 			console.error("权限【"+ite+"】不能从【"+p+"】修改成【"+_item_.rights+"】");
        	 			return;
        	 		}
        	 		
        	 		if(func()){//满足条件修改
        	 			eval("that." + ite + " = '" + _item_.rights + "';");
        	 		}else if(_item_.reset=="1"){
        	 			eval("that." + ite + " = '" + p + "';");
        	 		}
        	 		console.info("动态权限修改结果：" + JSON.stringify(that.permission));
        	 		that.$emit('update:permission',that.permission);
    	 		});
	    	});
	    }
  	},
 	watch : {
 		value : {
 			handler : function(newVal,oldVal){
 	 			this.handle();
 			},
 			deep:true
 		}
 	}
}
</script>
