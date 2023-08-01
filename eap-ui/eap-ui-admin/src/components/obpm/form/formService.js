import Vue from 'vue'
import formValidator from './formValidator'
import formServiceExtend from './formServiceExtend'

import userList from './easyForm/components/form/selectUserList.vue'
Vue.component('userList', userList);
import abCheckbox from './easyForm/components/form/abCheckbox.vue'
Vue.component('abCheckbox', abCheckbox);
import abUploadProgress from './easyForm/components/form/abUploadProgress.vue'
Vue.component('abUploadProgress', abUploadProgress);
import abUploadProgressMult from './easyForm/components/form/abUploadProgressMult.vue'
Vue.component('abUploadProgressMult', abUploadProgressMult);

import abEditor from './easyForm/components/form/abEditor.vue'
Vue.component('abEditor', abEditor);

import abDict from './easyForm/components/form/abDict.vue'
Vue.component('abDict', abDict);
import abDictMult from './easyForm/components/form/abDictMult.vue'
Vue.component('abDictMult', abDictMult);
import abSelectAsync from './easyForm/components/form/abSelectAsync.vue'
Vue.component('abSelectAsync', abSelectAsync);
import abSelectAsync2 from './easyForm/components/form/abSelectAsync2.vue'
Vue.component('abSelectAsync2', abSelectAsync2);
import abAsyncMult from './easyForm/components/form/abAsyncMult.vue'
Vue.component('abAsyncMult', abAsyncMult);
import abUserMult from './easyForm/components/form/abUserMult.vue'
Vue.component('abUserMult', abUserMult);
import abInputSelect from './easyForm/components/form/abInputSelect.vue'
Vue.component('abInputSelect', abInputSelect);
import abInputSelectId from './easyForm/components/form/abInputSelectId.vue'
Vue.component('abInputSelectId', abInputSelectId);
import abRemark from './easyForm/components/form/abRemark.vue'
Vue.component('abRemark', abRemark);
import abMult from './easyForm/components/form/abMult.vue'
Vue.component('abMult', abMult);
import abDate from './easyForm/components/form/abDate.vue'
Vue.component('abDate', abDate);
import datePicker from './easyForm/components/form/datePicker.vue'
Vue.component('datePicker', datePicker);
import abSerialNo from './easyForm/components/form/abSerialNo.vue'
Vue.component('abSerialNo', abSerialNo);
import abUpload from './easyForm/components/form/abUpload.vue'
Vue.component('abUpload', abUpload);
import abUpload2 from './easyForm/components/form/abUpload2.vue'
Vue.component('abUpload2', abUpload2);
import abUploadDialog from './easyForm/components/form/abUploadDialog.vue'
Vue.component('abUploadDialog', abUploadDialog);
import abSubAdd from './easyForm/components/form/abSubAdd.vue'
Vue.component('abSubAdd', abSubAdd);
import abSubDel from './easyForm/components/form/abSubDel.vue'
Vue.component('abSubDel', abSubDel);
import abSubDetail from './easyForm/components/form/abSubDetail.vue'
Vue.component('abSubDetail', abSubDetail);
import abCustDialog from './easyForm/components/form/abCustDialog.vue'
Vue.component('abCustDialog', abCustDialog);
import abCustQuery from './easyForm/components/form/abCustQuery.js'
Vue.directive('abCustQuery', abCustQuery);
import abDynRights from './easyForm/components/form/abDynRights.vue'
Vue.component('abDynRights', abDynRights);
import abSubScope from './easyForm/components/form/abSubScope.vue'
Vue.component('abSubScope', abSubScope);
import abSubScopeTr from './easyForm/components/form/abSubScopeTr.vue'
Vue.component('abSubScopeTr', abSubScopeTr);
import abSubImp from './easyForm/components/form/abSubImp.vue'
Vue.component('abSubImp', abSubImp);
import abSubExp from './easyForm/components/form/abSubExp.vue'
Vue.component('abSubExp', abSubExp);

import abTaskOpinion from './easyForm/components/form/abTaskOpinion.vue'
Vue.component('abTaskOpinion', abTaskOpinion);
import abDynamicSelect from './easyForm/components/form/abDynamicSelect.vue'
Vue.component('abDynamicSelect', abDynamicSelect);
//表单number组件
import abNumber from './easyForm/components/form/abNumber.vue'
Vue.component('abNumber', abNumber);
import exportExcel from './easyForm/components/form/exportExcel.vue'
Vue.component('exportExcel', exportExcel);

// 一对多新增按钮
import multMain from './easyForm/components/form/multMain.vue'
Vue.component('multMain', multMain);

import EtCityCascader from './easyForm/components/form/EtCityCascader.vue'
Vue.component('EtCityCascader', EtCityCascader);
import EtCascader from './easyForm/components/form/EtCascader.vue'
Vue.component('EtCascader', EtCascader);

import batchImportForm from './easyForm/components/form/batchImportForm.vue'
Vue.component('batchImportForm', batchImportForm);

import componentCreater from './easyForm/components/componentCreater/index.vue'
Vue.component('componentCreater', componentCreater);

// 裁剪组件
// import cropperImage from './easyForm/components/form/cropperImage.vue'
// Vue.component('cropperImage', cropperImage);
// 使用裁剪功能的单文件上传组件
import abSingleImage from './easyForm/components/form/abSingleImage.vue'
Vue.component('abSingleImage', abSingleImage);

// 使用裁剪功能的多文件上传组件
import abMultImage from './easyForm/components/form/abMultImage.vue'
Vue.component('abMultImage', abMultImage);

var FormService = {};
console.log('开始构建 FormService')
/**
 * 自定义表单初始化
 * @html 自定义表单的HTML 代码
 * @data 表单的数据，会混入到自定义表单组件中
 */
FormService.initCustFormFu = function (html, data) {
	// 流程那里 url 表单不处理
	if (data.form && data.form.type !== "INNER") return;

	//var html = data.form.formHtml;
	window.custFormComponentMixin = {};
	var custComponentData = data;

	// 处理 custFormComponentMixin
	if (html.indexOf("<script>") != -1) {
		var reg = /<script[^>]*?>[\s\S]*?<\/script>/i;
		var patten = reg.exec(html)
		var script = patten[0];

		var len1 = script.indexOf(">")
		var len2 = script.lastIndexOf("<")
		var custScriptStr = script.substring(len1 + 1, len2);
		html = html.replace(reg, "");
		var showPanel = ''
		var buttonFix = false

		if (custScriptStr.indexOf('buttonFix') !== -1) {
			buttonFix = true
		}
		if (custScriptStr) {
			try {
				// TODO严格模式下问题
				eval(custScriptStr.substring(custScriptStr.indexOf("window.custFormComponentMixin")));// eslint-disable-line

				if (window.custFormComponentMixin.data().foldPanelIndexArr){
					showPanel = window.custFormComponentMixin.data().foldPanelIndexArr.join(",");
				}

			} catch (e) {
				console.error("解析表单 JavaScript脚本 异常", custScriptStr, e);
			}
		}

		// 用于第三层子表数据暂存 _temp.xxx = [] //则该子表的数据暂存
		custComponentData.subTempData = {};
		//用来作为下拉级联option的载体 一个表单页最多兼容一百个下拉框
		custComponentData.abCustQueryOpMap = {};
		for (var i = 0; i < 200; i++) {
			custComponentData.abCustQueryOpMap["op" + i] = [];
		}
	}

	/**
	 * 公共的常用 表单 js
	 */
	var custFormMethods = {
		abGetNumber: function (data) {
			if (data === undefined || data === "") {
				return 0;
			}
			var number = Number(data);
			if (number === NaN) {
				console.info("计算对象中存在 NaN");
				return 0;
			}
			return number;
		},
		delFileHandler(id){
			console.log(this.data)
			if(!this.data["del_file_list"]){
				this.data["del_file_list"]  = []
				this.data["del_file_list"].push(id)
			}else {
				this.data["del_file_list"].push(id)
			}
			console.log(this.data)
		},
		addFileHandler(id){
			if(!this.data["add_file_list"]){
				this.data["add_file_list"] = []
			}
			this.data["add_file_list"].push(id)
		},
		abSubAvg: function (subDataList, key) {
			if (!subDataList || !key || subDataList.length == 0) {
				return "not calculate";
			}
			var sum = 0;
			subDataList.forEach(function (data) {
				if (!data[key]) return;
				sum = sum + Number(data[key]);
			})
			return sum / subDataList.length;
		},
		abSubSum: function (subDataList, key) {
			if (!subDataList || !key || subDataList.length == 0) {
				return "not calculate";
			}
			var sum = 0;
			subDataList.forEach(function (data) {
				if (!data[key]) return;
				sum = sum + Number(data[key]);
			})
			return sum;
		},
		changeRelate(ralateName, value) {
			if (this.$ref[ralateName]) {
				this.$ref[ralateName].updateValue(value)
			}
		}
	}

	// TODO noTaskOpinion=true remove or hide ab-task-opinion
  // console.log(html)
	Vue.component('ab-custom-form', {
		name: "customForm",
		props: ["notaskopinion"],
		mixins: [custFormComponentMixin],
		template: html,
		data: function () {
			return custComponentData;
		},
		// methods: custFormMethods,
		// watch: {
		// 	"notaskopinion"(value) {
		// 		if (value) {
		// 			$(".taskopinionhis").hide();
		// 		}
		// 	}
		// },
		mounted: function () {

		}
	});
};


Vue.component('ab-url-form', {
	props: ["form"],
	name: "urlForm",
	template: "<iframe id='frmFrame' src='' @load='setIframeHeight(this)' :height='vueIframeHeight' name='customUrlForm'  style='width:100%;border:none;'></iframe>",
	data: function () {
		return { url: "", vueIframeHeight: '' };
	},
	methods: {
		setIframeHeight(iframe) {
			let target;
			if (iframe.length) {
				for (let i = 0; i < iframe.length; i++) {
					if (iframe[i].name == "customUrlForm") {
						target = iframe[i].frameElement;
					}
				}
			}

			if (target) {
				let element = target.contentWindow || target.contentDocument.parentWindow;
				if (element.document.body) {
					let height = 30 + (element.document.documentElement.scrollHeight || element.document.body.scrollHeight);
					this.vueIframeHeight = height;
				}
			}

		}
	},
	mounted: function () {
		if (this.form.type != "FRAME") return;
		var url = this.form.formValue;
		url = url.startWith("http") ? url : getProjectUrl(url);
		this.url = url;
		$(this.$el).attr("src", url);
	}
});
/**
 * 校验,依赖 v-model 或者 组件的 input 事件
 *
 */
Vue.directive("AbValidate", {
	inserted: function (el, binding, vnode) {
    console.log('校验')
		// if (!document.location.pathname.endsWith("vueFormDetail.html")) {
		// 	if (binding.value && binding.value.required) { //在必填字段前面加上'i-required'class
		// 		$(vnode.elm).parents(".form-item-content").prev().addClass("i-required");
		// 	}
		// 	if (!$(el).attr("placeholder") || $(el).attr("placeholder") == '') {
		// 		let label = $(vnode.elm).parents(".form-item-content").prev().text() || $(vnode.elm).attr("desc");
		// 		let tag = vnode.tag, componentTag = vnode.componentOptions ? vnode.componentOptions.tag : '';
		// 		if (tag == 'input' || tag == 'textarea') {
		// 			$(el).attr("placeholder", '请输入' + label);
		// 		} else if (tag == 'select' || componentTag == 'ab-dict' || componentTag == 'ab-dict-mult' || componentTag == 'ab-date') {
		// 			$(el).attr("placeholder", '请选择' + label);
		// 		} else if (componentTag == 'ab-select-async' || componentTag == 'ab-async-mult') {
		// 			$(el).attr("placeholder", '请输入' + label + '搜索');
		// 		}
		// 	}
		// }

		// formValidator.doValidate(el, binding, vnode);

		// //  原生 input 没有 update 动作，这里做input事件补偿
		// var validateHandler = function (e) {
		// 	if (e.target && e.target !== el) return;

		// 	var value = e.target ? e.target.value : e;
		// 	formValidator.doValidate(el, binding, vnode, value, true);
		// }

		// if (vnode.componentInstance) {
		// 	vnode.componentInstance.$on("input", function (value) {
		// 		validateHandler(value);
		// 	})
		// } else {
		// 	addEvent(el, 'input', validateHandler);
		// }

	},
	update: function (el, binding, vnode, oldVnode) {
		if (el.value === undefined) return;

		var oldValue = vnode.elm.$_oldValue;
		var value = el.value;
		if (vnode.componentOptions && vnode.componentOptions.tag === 'ab-dict') {
			//组件中获取dom的value值有延迟会导致表单验证不对，所以从这里取值
			value = vnode.componentInstance.value;
		}
		if (oldValue !== value || el.type === "radio") {
			formValidator.doValidate(el, binding, vnode, value);
		}
		vnode.elm.$_oldValue = value;
	},
	unbind: function (el, binding, vnode, oldVnode) {
		// 清除该字段校验
		if (vnode.context.$validity[vnode.data.attrs.desc]) {
			delete vnode.context.$validity[vnode.data.attrs.desc];
		}
	}

})

/**
 * 获取表单的校验情况
 * 自定义表单为<custForm> 组件。当前componentScope 为页面的scope
 * 所以要先找到 custForm 组件的作用域
 * return
 */
FormService.getValidateMsg = function (componentScope) {
	var errorStr = "";
	if (!componentScope.$validity) {
		return errorStr;
	}

	if (FormService.invalidElementArr) {
		FormService.invalidElementArr.forEach(item => {
			item.css({"backgroundColor": "#ffffff"});
		})

	}
	FormService.invalidElementArr = [];



	for (var key in componentScope.$validity) {
		if (errorStr) { errorStr = errorStr + "<br/>" }
		errorStr = errorStr + "【" + key + "】" + ":" + componentScope.$validity[key];

		FormService.invalidElementArr.push($(`.ab-invalid[desc="${key}"]`));
		FormService.invalidElementArr.push($($(`.ab-invalid[desc="${key}"]`).find("input")[0]));

		// $(`.ab-invalid[desc="${key}"]`).css({"backgroundColor": "#c9dec1"});
		$($(`.ab-invalid[desc="${key}"]`).find("input")[0]).css({"backgroundColor": "#c9dec1"});
	}

	if (errorStr) {
		errorStr = "<div style='text-align:left'>" + errorStr + "</div>"
	}

	return errorStr;
}


/**
 *  基础权限，对控件做只读、必填、无权限, @v-model 必须
 *  v-ab-permission="permission.kjcs.cskj.xb" v-model=""
 *
 *  判断是否有展示权限，无则移除dom
 *  v-ab-permission:show="permission.kjcs.cskj.xb"
 *
 *   *  判断是否有编辑权限，无则移除dom
 *  v-ab-permission:edit="permission.kjcs.cskj.xb"
 */
Vue.directive("AbPermission", {
	inserted: function (el, binding, vnode) {
		handleElementPermission(el, binding, vnode);
	},
	update: function (el, binding, vnode) {
		// if (binding.oldValue && binding.oldValue === binding.value) {
		// 	return;
		// }
		handleElementPermission(el, binding, vnode);
	}
})

Vue.directive("AbBtnRights", {
	inserted: function (el, binding, vnode) {
		var btnRightsKey = binding.value;
		if (!btnRightsKey) return;
		if (window.localStorage) {
			var keyButtonPermision = 'buttonPermision_'+window.sessionStorage.getItem('systemAlias');
			var btnPermission = window.localStorage.getItem( keyButtonPermision );
			btnPermission = btnPermission ? JSON.parse(btnPermission) : {};

			if (btnPermission[btnRightsKey]) {
				return;
			}
			console.info(btnRightsKey + " no rights");
			el.remove();
		} else {
			console.info("浏览器版本太低不支持按钮权限！");
		}
	}
})

/**
 * 特殊组件权限处理
 * @param el
 * @param binding
 * @param vnode
 */
function componentProcessingPermission(el, binding, vnode) {
	let componentTag = vnode.componentOptions ? vnode.componentOptions.tag : '';
	if (componentTag === 'ab-dict') {
		//数据字典把权限给组件(组件获取不到v-ab-permission)
		vnode.componentInstance.bpmPermission = binding.value;
	}
}

/**
 * 处理控件权限
 *
 * @param el
 * @param binding
 * @param vnode
 * @returns
 */
function handleElementPermission(el, binding, vnode) {
	componentProcessingPermission(el, binding, vnode);
	if (!binding.value) return;
	// 只成功处理一次

	//重新计算时，[还原权限]
	if (binding.oldValue && !binding.oldValue.startsWith('w')) {
		if (binding.oldValue.startsWith('b')) {
			vnode.elm.required = false;
			vnode.elm.$_oldValue = undefined;
		} else if (binding.oldValue.startsWith('r')) {
			vnode.elm.readOnly = false;
			vnode.elm.disabled = false;
			// console.log(vnode.elm.css)
			if (vnode.elm.css) {
				vnode.elm.css.pointerEvents = "none"
			}
		} else if (binding.oldValue.startsWith('n')) {
			vnode.elm.style.display = ""
		}
	}

	if (binding.arg) {
		handleSpecialPermision(el, binding, vnode);
		return;
	}

	// 处理必填权限
	if (binding.value.startsWith("b")) {
		vnode.elm.$_oldValue = undefined;
		vnode.elm.required = true;

		if (!document.location.pathname.endsWith("vueFormDetail.html")) {

			$(vnode.elm).parents(".form-item-content").prev().addClass("i-required");

			if (!$(el).attr("placeholder") || $(el).attr("placeholder") == '') {
				let label = $(vnode.elm).parents(".form-item-content").prev().text() || $(vnode.elm).attr("desc");
				let tag = vnode.tag, componentTag = vnode.componentOptions ? vnode.componentOptions.tag : '';
				if (tag == 'input' || tag == 'textarea') {
					$(el).attr("placeholder", '请输入' + label);
				} else if (tag == 'select' || componentTag == 'ab-dict' || componentTag == 'ab-dict-mult' || componentTag == 'ab-date') {
					$(el).attr("placeholder", '请选择' + label);
				} else if (componentTag == 'ab-select-async' || componentTag == 'ab-async-mult') {
					$(el).attr("placeholder", '请输入' + label + '搜索');
				}
			}
		}

		formValidator.doValidate(el, binding, vnode);

		//  原生 input 没有 update 动作，这里做input事件补偿
		var validateHandler = function (e) {
			if (e.target && e.target !== el) return;

			var value = e.target ? e.target.value : e;
			formValidator.doValidate(el, binding, vnode, value, true);
		}

		if (vnode.componentInstance) {
			vnode.componentInstance.$on("input", function (value) {
				validateHandler(value);
			})
		} else {
			addEvent(el, 'input', validateHandler);
		}

	}
	else if (binding.value.startsWith("w")) {
		if (binding.value.indexOf("0") > 0){
			formValidator.doValidate(el, binding, vnode);
		}
	}
	//
	else if (binding.value.startsWith("r")) {
		vnode.elm.readOnly = true;
		vnode.elm.disabled = true;
	}
	else if (binding.value.startsWith("n")) {
		vnode.elm.style.display = "none"
		if (vnode.context.$validity && vnode.context.$validity[vnode.data.attrs.desc]) {
			delete vnode.context.$validity[vnode.data.attrs.desc];
		}
	}
}

function handleSpecialPermision(el, binding, vnode) {

	//无展示权限则移除掉标签
	if (binding.arg === "show" && binding.value == "n") {
		vnode.elm.style.display = "none";
		return;
	}

	//无编辑权限则移除掉控件
	if (binding.arg === "edit" && binding.value !== "w" && binding.value !== "b") {
		vnode.elm.style.display = "none";
		return;
	}
	vnode.elm.style.display = "";
}



function addEvent(element, event, listener) {
	if (element.addEventListener) {
		element.addEventListener(event, listener, false);
	} else if (element.attachEvent) {
		element.attachEvent('on' + event, listener);
	} else {
		element['on' + event] = listener;
	}
}

FormService.getCustFormComponent = function (pageComponent) {
	for (var i = 0, c; c = pageComponent.$children[i++];) {
		if (c.$options._componentTag === "ab-custom-form" || c.$options._componentTag === "ab-url-form") {
			return c;
		}
	}
	// 不向下递归
	console.error("页面中找不到 cust-form 的组件！！！");
	return null;
}

console.log('构建完毕FormSerice')
export default {
	// 全局安装时候
	install: function (Vue) {
		Vue.formService = FormService;
	},
	formService: FormService
}
