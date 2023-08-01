import Vue from 'vue'

/**
 *  表单计算
 */
Vue.directive("AbCalculate",{
	inserted:function(el,binding,vnode){
		// 第一次不计算，只有值改变才需要计算
		if(!binding.value)return;
		// 校验 必须 v-model
        for(var i=0,d;d=vnode.data.directives[i++];){
            if(d.name=="model"){
                return;
            }
        }
        console.error("AbCalculate 指令依赖 v-model 或者 :model 指令", vnode.elm)
	},

	update :function(el, binding, vnode , oldVnode) {
		// to Number 然后 trim
		try{
			var data = vnode.context.data;
			var result = binding.value;
			if("not calculate"===result){
				return ;
			}
			var decimals = vnode.elm.getAttribute("calc-decimals");
			if(!decimals){
				decimals = 2;
			}else{
                decimals = Number(decimals);
			}
			var numberStr = result+"";
			if(numberStr.indexOf(".")!=-1){
				numberStr = parseFloat(result).toFixed(decimals) + "";
			}
			el.value = numberStr;
            el.dispatchEvent(new Event('input'))
		}catch (e) {
			if(e !== "undefined"){
				console.error( "表单计算异常，存在非数字类型参与运算 ["+binding.value+"]" + e);
			}
			console.info("表单计算跳过，参与运算值存在空 "+e);
		}
	}
})
