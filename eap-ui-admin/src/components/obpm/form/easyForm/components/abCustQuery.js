
/**
 * <div v-ab-cust-query="userList" dialog-key="userSelector" query-desc="我是一个查询"
 * 		:param={orgId:data.main.orgId} :init-param="{orgId:'-1'}"
 * 		value-fullname="name" value-account="code" value-sex="sex"  >
 * </div>
 * 
 * @指令 v-ab-cust-query="list"： 返回数据的对象，类似自定义对话框的返回逻辑，对象可以json或数组，赋值逻辑会有所变化
 * @入参 :param ： 自定义对话框的过滤条件参数
 * @初始化参数 :init-param 如果没有传入参数则默认使用 初始化入参，一般用于定义 第一次默认查询
 * @返回值映射 value-key="k"：value-xxxx 是对话框返回的字段 “k” 则为 “userList[x].key” 如果为对象则为 userList.key
 */

import Vue from 'vue';
export default {
	inserted:function(el,binding,vnode){
		var dialogData = el.dialogData;
		if(!dialogData){
			dialogData = el.dialogData = {};
			dialogData.valueMap = {};
		}
		
		//映射关系
		for(var key in vnode.data.attrs){
			var val = vnode.data.attrs[key];
			// 找到value开头的赋值配置
			if (key.indexOf("value-") !== 0) {
				continue;
			}
			var name = key.replace("value-", "");
			dialogData.valueMap[name] = val;
		}
  		
		binding.def.update(el,binding,vnode);
	},
	update:function(el,binding,vnode) {
		var dialogData = el.dialogData;
		
		var query = getQueryParam(el,binding,vnode);
		//值无变化，不操作
		if(JSON.stringify(dialogData.query)==JSON.stringify(query)){
			return;
		}
		dialogData.query = query;
		clearTimeout(dialogData.timer);//关闭定时器
		//开始计时，延迟1秒操作
		dialogData.timer = setTimeout(function(){
			var param = query;
			var url = Vue.__ctx + "/form/formCustDialog/queryListData_" + vnode.data.attrs["dialog-key"];
  			var post = Vue.baseService.postForm(url, param);
	  		post.then(function(result){
	  			console.info(result.rows);
	  			handleResult(el,binding,vnode,result.rows);//处理结果
	  		}, function (status) {
		  		if (status == !0) {
		  			$.Toast.error('加载失败！' + status);
  			  	}
  			});
		},500);
	}
}

// 获取查询条件、讲默认值赋值
function getQueryParam(el,binding,vnode){
	if(!vnode.data.attrs.param) return {};
	
	var initParam = vnode.data.attrs["init-param"];
	if(initParam){
		for(var key in initParam){
			if(!vnode.data.attrs.param[key] && initParam[key]){//为空取默认值
				vnode.data.attrs.param[key] = initParam[key];
			}
		}
	}
	return vnode.data.attrs.param;
}

function handleQuery(dialog,query){
	return query;
}

function handleResult(el,binding,vnode,rows){
	var dialogData = el.dialogData;
	
	var data = rows;
	
	var value = binding.value;
	if(Array.isArray(value)){//赋值对象是数组
		value.splice(0,value.length);//清空数组
		
		data.forEach(function(item){
			if(JSON.stringify(dialogData.valueMap)==="{}"){//无映射关系则把数据全返回
				value.push(item);
				return;
			}
			
			var json = {};
			//处理映射关系
			for(var key in dialogData.valueMap){
				var val = dialogData.valueMap[key];
				//如果val是a.b这样的，则需要对json.a初始化，不然直接操作json.a.b会报错
				var strs = val.split(".");
				var exp = "json";
				for (var i=0;i<strs.length-1;i++){
					exp = exp + "."+strs[i];
					if(eval("!"+exp)){//为空则初始化
						eval(exp+" = {};");
					}
				}
				eval("json."+val+" = item[key];");
			}
			value.push(json);
		});
	}else{//赋值对象是对象
		for(var key in dialogData.valueMap){
			var val = dialogData.valueMap[key];
			var list = [];
			data.forEach(function(item) {
				list.push(item[key]);
			});
			Vue.set(value,val,list.join(','));
		}
	}
}