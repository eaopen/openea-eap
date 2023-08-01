import Vue from 'vue'
import fa from "element-ui/src/locale/lang/fa";

var FormValidator = {};

/**添加自定义规则，可覆盖默认规则**/
FormValidator.addRuls = function(key,rule){
	FormValidator.rules[key] = rule;
}

/**
 * 校验处理入口 
 */
FormValidator.doValidate = function(el,binding,vnode,value,update){
	// 隐藏状态不校验
	if(vnode.elm.style.display == "none"){
		if(vnode.context.$validity && vnode.context.$validity[vnode.data.attrs.desc]){
			delete vnode.context.$validity[vnode.data.attrs.desc];
		}
		return ;
	}
	if(vnode.data.attrs.desc==='表单控件案例-市'){
		debugger;
	}
	//获取dom上的value，change事件直接拿value。keyup就算了
	if(!update){
		if(value===undefined || value==="" || el.type==="radio"){
			var value = getElemetValue(vnode);
			if(value === "___skip___") return ;
		}
	}
	var validateJson = binding.value;
	// if(vnode.elm.required){
	// 	validateJson.required = true;
	// }
	if(binding.value.required){
		validateJson.required = true;
	}

	// 执行校验
	var validity = validRules(value,validateJson);
	//添加校验标记
	setValidity(validity,vnode,value, validateJson);
}

//只会在第一次时才会需要获取值
function getElemetValue(vnode){
	//说明已渲染，则直接获取value即可
	if(vnode.data.model){
		return vnode.data.model.value || vnode.elm.value;
	}
	
	for(var i=0,d;d=vnode.data.directives[i++];){
		if(d.name=="model"){
			if(d.oldValue === d.value && typeof d.value !== 'undefined'){ 
				return "___skip___";
			}
			return d.value;
		}
	}
	
	console.error("ab-validate 指令依赖 v-model 或者 :model 指令",vnode.elm)
	return "___skip___";
}

//设置校验标记
function setValidity(validity,vnode,value, validateJson){
	var formContext = vnode.context;
	
	if(!formContext.$validity){
		formContext.$validity = {};
	}
	
	if(!vnode.data.attrs.desc) {
		//获取model 的key作为标识
	}
	if(validity === true){
		if (validateJson == 'b'){
			if (value === undefined){
				updateValidCalss(false,vnode);
			} else if (value === null){
				updateValidCalss(false,vnode);
			}else if (value === ''){
				updateValidCalss(false,vnode);
			} else {
				updateValidCalss(true,vnode);
				delete formContext.$validity[vnode.data.attrs.desc];
			}

		}else{
			let currentVal = formContext.$validity[vnode.data.attrs.desc];
			if (currentVal == '必填'){
				if (validateJson.required){
					if (value === undefined){
						updateValidCalss(false,vnode);
					} else if (value === null){
						updateValidCalss(false,vnode);
					}else if (value === ''){
						updateValidCalss(false,vnode);
					} else {
						updateValidCalss(true,vnode);
						delete formContext.$validity[vnode.data.attrs.desc];
					}
				} else {

					updateValidCalss(true,vnode);
					delete formContext.$validity[vnode.data.attrs.desc];
				}
			} else {
				updateValidCalss(true,vnode);
				delete formContext.$validity[vnode.data.attrs.desc];
			}

		}

	}else{
		formContext.$validity[vnode.data.attrs.desc] = validity;
		updateValidCalss(false,vnode);
	}


}

//兼容 ie9
function updateValidCalss(isValid,vnode){
	var removeClass = isValid ? 'ab-invalid':'ab-valid';
	var addClass = isValid ? 'ab-valid':'ab-invalid';
	if(vnode.elm.classList){
		vnode.elm.classList.remove(removeClass);
		vnode.elm.classList.add(addClass);
		return ;
	}
	
	var str = vnode.elm.className.replace(removeClass,addClass);
	if(str.indexOf(addClass) ===-1){
		str = str + " " + addClass;
	}
	vnode.elm.className = str;
	console.info(vnode.elm.className);
}


/**
 * 校验入口
 * @param value
 * @param validRule
 * @returns
 */
function validRules(value,validRule){
	value = value == null ? "" : value+"";
	
	if(typeof value == 'string'){
		value = value.trim();
	}
	// 获取json。
	var json = validRule;
	if(typeof json == 'string') {
		if (validRule == 'b'){
			json = {required: true}
		} else if (validRule == 'b0'){
			json = {required: true, number: true}
		} else if (validRule == 'w0'){
			json = {number: true}
		} else{
			json = eval('(' + validRule + ')');
		}

	}
	
	var isRequired = json.required;
	
	// 非必填的字段且值为空 那么直接返回成功。
	if ((isRequired == false || isRequired == undefined) && value == ""){
		return true;
	}
	
	//number：true将number取消格式化
	if(json.number){
		value =Vue.tools.toNumber(value)+"";
	}
	
	// 遍历json规则。
	var validates=[] ;
	for (var name in json) {
		var validValue = json[name];
		validates.push({
				ruleName:name,// 规则名称
				validValue:validValue,//验证的值
				value:value//实际的值
			}
		);
	}
	
	//验证规则
	for (var i=0,v;v=validates[i++];) {
		if(i==validates.length)v.isLast=true;
		var _valid =  doValidate(v);
		//如果当前规则验证不通过则直接返回 错误信息
		if(_valid!== true ) return _valid ;
	}
	
	//全部验证通过则返回true
	return true;
	
}

/**
 * 执行校验
 * @param conf
 * @returns
 */
function doValidate (conf){
	var  _valid = true,
		ruleName = conf.ruleName.toLowerCase(),// 规则名称
		validValue = conf.validValue,//验证的值
		value = conf.value;//实际的值 
	
	var rule = FormValidator.rules[ruleName];
	if(!rule) return true;
	
	// 验证规则如下：
	// email:true,url:true.
	//验证规则是boolean类型
	if (typeof validValue  === "boolean") 
		_valid = (!rule.rule(value,validValue) && validValue == true) ? false:true; 	
	else 
		_valid = rule.rule(value, validValue);
	
	//校验不通过时，返回错误信息
	if (!_valid){
		var errMsg = FormValidator.format(rule.msg, validValue);
		return errMsg;
	}
	
	/*if(conf.isLast && element.attr("desc")){
		element.removeAttr("error-msg");
	}*/
	return _valid;
}

FormValidator.format = function(msg,args){
	//boolean类型的直接返回
	if (typeof args  === "boolean") 
		return  msg;
	if(typeof args =='object' && args.target){
		args = args.targetVal;
	}
	if (!(args instanceof Array)) //不是数组类型的
		args = [args];
	//数组类型的
	
	for(var i=0,arg;arg=args[i++];){
		msg = msg.replace(RegExp("\\{" + (i-1) + "\\}", "g"), arg);
	}
 
	return msg;		
}


FormValidator.rules = {
		"required":{
			rule : function(v) {
				if (v == "" || v.length == 0)
					return false;
				return true;
			},
			title : "必填",
			formRule : true,
			msg : "必填"
		  },
"number":{
		rule : function(v) {
			if ('暂缺' == v.trim()){
				return true
			}
			return /^-?((\d{1,3}(,\d{3})+?|\d+)(\.\d{1,6})?)$/
					.test(v.trim());
		},
		title : "数字",
		formRule : true,
		msg : "只能输入数字"
	},
"variable":{
		rule : function(v) {
			return /^[A-Za-z_]*$/gi.test(v.trim());
		},
		title : "字母或下划线",
		formRule : true,
		msg : "只能是字母和下划线"
	},
"fields":{
		rule : function(v){
			return /^[A-Za-z]{1}([a-zA-Z0-9_]{1,17})?$/gi.test(v.trim());
		},
		msg : "首字符为字母,最大长度18"
	},
"minlength":{
		rule : function(v, b) {
			return (v.length >= b);
		},
		msg : "长度不少于{0}"
	}, 
"maxlength":{
		rule : function(v, b) {
			return (v.trim().length <= b);
		},
		msg : "长度不超过{0}"
	},
"rangelength":{
		rule : function(v, args) {
			return (v.trim().length >= args[0] && v.trim().length <= args[1]);
		},
		msg : "长度必须在{0}之{1}间"
	},
"email":{
		rule : function(v) {
			return /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))$/i
					.test(v.trim());
		},
		title : "email",
		formRule : true,
		msg : "请输入一合法的邮箱地址"
	},
"url":{
		rule : function(v) {
			return /^(https?|ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(\#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i
					.test(v.trim());
		},
		title : "url",
		formRule : true,
		msg : "请输入一合法的网址"
	},
 "date":{
		rule : function(v) {
			var re = /^([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))/g
					.test(v.trim());
			return re;
		},
		title : "日期",
		formRule : true,
		msg : "请输入日期格式"
	},
"time":{
		rule : function(v) {
			var re = /^(?:[01]?\d|2[0-3])(?::[0-5]?\d){2}$/g
					.test(v.trim());
			return re;
		},
		title : "时间",
		formRule : true,
		msg : "请输入一合法的时间"
	},
 "chinese":{
		rule : function(v) {
			var re = /[\u4E00-\u9FA5\uF900-\uFA2D]/
					.test(v.trim());
			return re;
		},
		title : "汉字",
		formRule : true,
		msg : "请输中文字符"
	},
"qq":{
	rule : function(v) {
		return /^[1-9]*[1-9][0-9]*$/i.test(v.trim());
	},
	title : "QQ号",
	formRule : true,
	msg : "请输入正确的QQ号码"
	},
"phonenumber":{
	rule : function(v) {
		if(v.length>11) return false;
		return /((\d{11})|^((\d{7,8})|(\d{4}|\d{3})-(\d{7,8})|(\d{4}|\d{3})-(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1})|(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1}))$)/i.test(v.trim());
	},
	title : "手机号码",
	formRule : true,
	msg : "请输入正确的手机号码"
	},
"digits":{
		rule : function(v) {
			return /^\d+$/.test(v.trim());
		},
		title : "整数",
		formRule : true,
		msg : "请输入整数"
	},
"equalto":{
		rule : function(v, b) {
			var a = $("#" + b).val();
			return (v.trim() == a.trim());
		},
		msg : "两次输入不等"
	}, 
"range":{
		rule : function(v, args) {
			return v <= args[1] && v >= args[0];
		},
		msg : "请输入在{0}到{1}范围之内的数字"
	},
"maxvalue":{
		rule : function(v, max) {
			return $.fn.toNumber(v) <= $.fn.toNumber(max); 
		},
		msg : "输入的值不能大于{0}"
	},
"minvalue":{
		rule : function(v, min) {
			return $.fn.toNumber(v) >= $.fn.toNumber(min); 
		},
		msg :"输入的值不能小于{0}"
	},
"maxintlen":{
		// 判断数字整数位
		rule : function(v, b) {
			return (v + '').split(".")[0].replace("/,/g","").length <= b;
		},
		msg : "整数位最大长度为{0}"
	}, 
"maxdecimallen":{
		// 判断数字小数位
		rule : function(v, b) {
			return (v + '').replace(/^[^.]*[.]*/, '').length <= b;
		},
		msg : "小数位最大长度为{0}"
	},
"daterangestart":{
		rule : function(v, b,e) {
			return daysBetween(b.targetVal, v); 
		},
		msg : "日期必须大于或等于{0}"
	}, 
"daterangeend":{
		rule : function(v,b,e) {
			return daysBetween(v, b.targetVal);
		},
		msg : "日期必须小于或等于{0}"
	},
"empty":{
		// 空的字段（永远通过验证,返回true）  防止在验证JSON中出现有多余的逗号
		rule : function(v, b) {
		//	var start = $("#" + b).val();
			return true;
		},
		msg : ""
	},
"nodigitsstart":{
		// 不能以数字开头
		rule : function(v) {
			return !/^(\d+)(.*)$/.test(v.trim());
		},
		title : "不以数字开头",
		formRule : true,
		msg : "不能以数字开头"
	},
"varirule":{
		name : "varirule",
		rule : function(v) {
			return /^[a-zA-Z]\w*$/.test(v.trim());
		},
		title : "以字母开头",
		formRule : true,
		msg : "只能为字母开头,允许字母、数字和下划线"
	},
/**
 * eg:<input type="text" ng-model="data.name" ht-validate="{isexist:'bOEnt/isExist'}"/>
 * isexist:是请求地址的参数，需要自己实现，要验证的数据参数是‘key’；
 * 返回true:代表已存在，不可用；返回false:不存在，可用
 */
"isexist":{
		name : "isexist",
		rule : function(v,url) {
			if(!v) return true;
			var b;
			if(url.indexOf("?")<1){
				url+="?";
			}
			$.ajax({
				url : url+"&key="+v,
				type : 'POST',
				dataType : 'json',
				async : false,
				success : function(result) {
					b=!result;
				}
			});
			return b;
		},
		msg : "数据已被使用,请重新填写"
	}	
} 
 
export default  FormValidator;


