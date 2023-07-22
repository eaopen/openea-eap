var bpmService = {};
/**
打开任务传入的参数
taskId, 任务ID
instanceId, 实例ID  草稿详情
defId,定义ID
nodeId: 节点ID
**/
bpmService.params ;
//初始化动作
bpmService.init = function(params,fn){
	this.params = params;

	var dataUrl = Vue.__ctx + "/bpm/task/getTaskData";// taskId=xxx
	if(!params.taskId){
		dataUrl =  Vue.__ctx + "/bpm/instance/getInstanceData";
	}

	var defer = Vue.baseService.postForm(dataUrl,params);
	Vue.tools.getResultData(defer,function(data){
		//可以再表单中扩展配置逻辑
		data.flowRequestParam  = bpmService.getSubmitFlowParam(data);

		if(fn)fn(data);
	},"alert");

};

//获取表单的数据
bpmService.getFormData = function(custFormContext,button){
	var validateForm = "start,agree".indexOf(button.alias)!=-1;

	var formType = custFormContext.form.type;
	if(formType=='INNER'){
		var errorMsg = Vue.formService.getValidateMsg(custFormContext);
		if(errorMsg && validateForm){
			$.Dialog.alert("表单校验不通过！"+errorMsg,7);
			return false;
		}
		return custFormContext.data;
	}
	if(formType=='FRAME'){
		var iframeObj=document.getElementById("frmFrame").contentWindow;
		//尝试校验
		try{
			if(validateForm){
				if(iframeObj.isValid() === false){
					return false;
				}
			}
		}
		catch(e){
		}
		//尝试取值
		try {
			return iframeObj.getData();
		} catch (e) {
			console.error("URL 表单获取 数据失败！"+e)
			return {test:"jeff"};
		}
	}

		alert("不支持的表单类型:"+formType)

};

// 流程提交需要的参数
bpmService.getSubmitFlowParam = function(data){
	if(!data){
		alert("error");
		return;
	}

	if(data.task) {
		return {taskId:data.task.id,nodeId:data.task.nodeId,instanceId:data.task.instId};
	}

	if(data.instance){
		return {instanceId:data.instance.id,defId:data.instance.defId};
	}

	return {
			defId: data.defId,
			instanceId: this.params.instanceId
		}
}
/**
 * 流程按钮解析。
 * 关于按钮样式，对话框宽高属性，不做可配置行，因为前段无法统一，
 * 但是、请求资源路径要求一致，比如/bpm/task/   /bpm/instance.
 * 前缀自己前段控制
 */
Vue.component('bpmButtons',{
	 props: ['buttons'],
	 data:function(){
		 return { }
	 },
	 methods:{
		 buttonClick : async function(button){
			 	var ii = layer.load();

				//执行前置js
				if(!this.execuFn(button.beforeScript)){
					layer.close(ii);
					return;
				}
			 //获取流程数据
			 var custFormContext = getCustFormComponent(this.$parent);

			 if (custFormContext.apply_inform) {
				 console.log("???????????")
			 }
			 let isUrlForm = false;
			 if (button.alias === "agree") {
				if (window.length) {
					for (let i = 0; i < window.length; i++) {
						const childWindow = window[i];
						if (childWindow.name && childWindow.name === "customUrlForm") {
							isUrlForm = true;
							let isSubmitFormOk = childWindow.vm.submitForm();
							if (!isSubmitFormOk) {
								console.error("自定义表单校验未通过！")
								layer.close(ii);
								return;
							}
						}
					}
				}
			 }

			 //客户表单自定义 fn:custValid 校验
			 let validateBtnArr = ["start","agree"];
			 if (!custFormContext.isValid4Oppose){
				 validateBtnArr.push("oppose");
			 }

			 if (validateBtnArr.indexOf(button.alias)>-1 && custFormContext.custValid) {
				 let isValid = await custFormContext.custValid();
				 console.log("custValid =", isValid)
				 if (isValid === false) {
					 layer.close(ii);
					 return;
				 }
			 }

			 if ("oppose" == button.alias && custFormContext.isValid4Oppose) {
				 let isValid = await custFormContext.isValid4Oppose();
				 console.log("isValid4Oppose =", isValid)
				 if (isValid === false) {
					 layer.close(ii);
					 return;
				 }
			 }
			 if ("reject" == button.alias && custFormContext.isValid4Reject) {
				 let isValid = await custFormContext.isValid4Reject();
				 console.log("isValid4Reject =", isValid)
				 if (isValid === false) {
					 layer.close(ii);
					 return;
				 }
			 }
			 if ("save" == button.alias && custFormContext.isValid4Save) {
				 let isValid = await custFormContext.isValid4Save();
				 console.log("isValid4Save =", isValid)
				 if (isValid === false) {
					 layer.close(ii);
					 return;
				 }
			 }
			 // 获取在线表单数据，并校验内置校验

			 var busData;
			 if (isUrlForm) { // 获取 UrlForm 的流程绑定数据
				 busData = this.getBpmBizData(custFormContext.flowRequestParam.taskId);
			 } else {
				 busData = bpmService.getFormData(custFormContext, button);
				 if (busData === false) {//获取数据失败
					 layer.close(ii);
					 return;
				 }
			 }

			 var scope = this;
			 var flowData = custFormContext.flowRequestParam;
			 flowData.data = busData;
			 flowData.action = button.alias;
			 flowData.extendConf = {
				 'nodeId': $.getParam("nodeId")
			 };
			 if (flowData.action != "reject"){
				 delete flowData.destination;
			 }

			 //获取更多完成动作配置
				var buttonStyle = this.getButtonStyle(button.alias);

				// 同意，反对的时候可以自动跳过录入意见
				var requiredOpinion = custFormContext.configSpecified.requiredOpinion;
				// 修改，同意时跳过，反对时仍需录入意见 10/1
			    if(!requiredOpinion && "agree,start".indexOf(button.alias)==-1){
				//if(!requiredOpinion && "oppose,agree,start".indexOf(button.alias)==-1){
					requiredOpinion = true;
				}

			    if ("signAgree".indexOf(button.alias) >= 0){
					requiredOpinion = false;
				}

				 if (custFormContext.apply_inform && "agree,oppose,reject".indexOf(button.alias)>-1){
					 requiredOpinion = true;
				 }
             //增加必需要录入意见的特殊情况处理
             //按格式加入下行的数组内即可, defKey:流程的defKey, nodeId: 流程节点的 nodeId, alias: 按钮动作
             const requiredOpinionConfig = [
                 {defKey: "SGC_programme", nodeIds: ["S1", "S2", "S3", "S4", "S5", "S6"], alias: ["agree"]},
				 // 内管流程的节点备注信息必填
				 {defKey: "innerCenter5_plus", nodeIds: ["Z1", "Z2", "Z3", "Z4", "Z5"], alias: ["agree"]},
				 {defKey: "innerCenter5", nodeIds: ["Z1", "Z2", "Z3", "Z4", "Z5"], alias: ["agree"]},
				 {defKey: "regulation_add1", nodeIds: ["I15.6"], alias: ["signAgree"]}
             ]
             for (let c of requiredOpinionConfig) {
             	if (custFormContext.task && custFormContext.instance){
					if (c["defKey"] === custFormContext.instance.defKey && c["nodeIds"].indexOf(custFormContext.task.nodeId) > -1 && c["alias"].indexOf(button.alias) > -1) {
						requiredOpinion = true;
						break;
					}
				}
             }
				if( button.configPage && requiredOpinion){
					layer.close(ii);

					var height = buttonStyle.dialogHeigth || 500;
					var width = buttonStyle.dialogWidth || 600;

					var conf = {title:button.name,url:button.configPage,height:height,width:width,passData:flowData,topOpen:false,offset:'200px'};
					conf.ok = function(index,innerWindow){
						if (innerWindow.location.pathname == '/bpm/task/adminOperation.html') {
							innerWindow.vm.submit()
						} else if (innerWindow.location.pathname == '/bpm/task/taskTrunActionDialog.html') {
							innerWindow.vm.submit()
						} else if (['/bpm/task/TaskHandler.html', '/bpm/task/TaskAbolishHandler.html'].indexOf(innerWindow.location.pathname) > -1) {
							let isOk = innerWindow.vm.submitForm();
							if (isOk){
								scope.closePage(custFormContext);
							}
						} else {
							if (!innerWindow.isValidate()) return;
							// flowData.data.list = window[2]
							// console.log(window)
							scope.postAction(flowData, innerWindow, custFormContext);
						}
					}
					layer.close(ii);
					if (button.alias == 'communicate') {
						conf.btn = ['关闭'];
						conf.offset = ['150px'];
					}
					if (button.alias == 'abolish') {
						conf.offset = ['150px'];
					}
					$.Dialog.open(conf);
				}else{
					if(!flowData.opinion){
						flowData.opinion = button.name;
					}

					layer.close(ii);
					if(window.nodeId && window.nodeId == 'A40' && button.alias == 'agree'){
						// A40节点
						let scp = window[2].AngularUtil.getScope()
						let rows = window[2].agExt.changeRows
						let flag = true
						if(flag){
							flowData.data.amend_bpm.prod_dbom_amend_nlist = rows
							scope.postAction(flowData,null,custFormContext);
						}else{
							$.Dialog.error('请确认沟通负责人/联系人')
							return
						}

					}else{
						scope.postAction(flowData,null,custFormContext);
					}

				}
			},
			postAction : function(flowData,innerWindow,custFormContext){
				var ii = layer.load();
				// 执行动作
				// console.log(flowData)
				var url =  Vue.__ctx + (flowData.taskId ? "/bpm/task/doAction":"/bpm/instance/doAction");
				var defer = Vue.baseService.post(url,flowData);
				Vue.tools.getResultMsg(defer,function(data){
					if (custFormContext && custFormContext.afterSave) {
						custFormContext.afterSave(data);
					}
					layer.close(ii);
					window.setTimeout(function(){
						top.layer.closeAll('dialog');
						layer.closeAll();
						parent.layer.closeAll(); // 父
						let frameid = $.getParam('frameid')
						if(frameid){
							console.log(frameid)
							// top.document.getElementById(frameid)
							let tabid = frameid.split('iframe')[0]
							top.document.getElementById(tabid).click()
							let url = top.document.getElementById(frameid).src
							top.document.getElementById(frameid).src = url
						}
						let closeSpanId = window.frameElement.id.split('iframe')[0]
						let closeBtn = ($('#'+closeSpanId + ' i', parent.document))
								closeBtn.click()
					},10)
					if(window.opener && window.opener.reloadGrid){
						window.opener.reloadGrid();
					}
				},function(){
                    layer.close(ii);
				});


			},
		 closePage : function(custFormContext){
			 var ii = layer.load();
			 	// 2022-10-25 16:05:18 @范彦青 afterSave 的调用写在 Vue.tools.getResultMsg 中，此处删除避免 bug
				//  if (custFormContext && custFormContext.afterSave) {
				// 	 custFormContext.afterSave(data);
				//  }
				 layer.close(ii);
				 window.setTimeout(function(){
					 top.layer.closeAll('dialog');
					 layer.closeAll();
					 parent.layer.closeAll(); // 父
					 let frameid = $.getParam('frameid')
					 if(frameid){
						 console.log(frameid)
						 // top.document.getElementById(frameid)
						 let tabid = frameid.split('iframe')[0]
						 top.document.getElementById(tabid).click()
						 let url = top.document.getElementById(frameid).src
						 top.document.getElementById(frameid).src = url
					 }
					 let closeSpanId = window.frameElement.id.split('iframe')[0]
					 let closeBtn = ($('#'+closeSpanId + ' i', parent.document))
					 closeBtn.click()
				 },10)
				 if(window.opener && window.opener.reloadGrid){
					 window.opener.reloadGrid();
				 }



		 },
			execuFn : function(fnStr){
				if(!fnStr)return true;
				var parentScope = getCustFormComponent(this.$parent);

				var script = "var tempFunction = function(scope){ "+fnStr+"};"
				var result =  eval(script+"tempFunction(parentScope);");
				if(result===false) return false;
				return true;
			},
			getButtonStyle:function(alias){
				var  buttonsStyle = {
					"start":{css:"btn btn-success fa fa-send",dialogHeigth:422,dialogWidth:690},
					"draft":{css:"btn btn-primary fa fa-clipboard"},
					"save":{css:"btn btn-primary fa fa-clipboard"},
					"agree":{css:"btn btn-success fa fa-check-square-o",dialogHeigth:422,dialogWidth:690},
					"oppose":{css:"btn btn-primary fa fa-close",dialogHeigth:422,dialogWidth:690},
					"reject":{css:"btn btn-danger fa fa-lastfm",dialogHeigth:422,dialogWidth:690},
					"reject2Start":{css:"btn btn-danger fa fa-lastfm",dialogHeigth:300,dialogWidth:500},
					"lock":{css:"btn btn-primary fa fa-lock"},
					"unlock":{css:"btn btn-primary fa fa-unlock"},
					"taskOpinion": {css: "btn btn-primary fa fa-navicon", dialogHeigth: '80%', dialogWidth: '75%'},
					"flowImage": {css: "btn btn-primary fa fa-image", dialogHeigth: '80%', dialogWidth: '90%'},
					"manualEnd": {css: "btn btn-danger fa fa-ioxhost", dialogHeigth: 300, dialogWidth: 500},
					"print": {css: "btn btn-primary fa fa-print", dialogHeigth: 300, dialogWidth: 500},
					'recall': {css: 'btn btn-warning fa fa-undo', dialogHeigth: 422, dialogWidth: 690},
					'reminder': {css: 'btn btn-warning fa fa-bell-o', dialogHeigth: 422, dialogWidth: 690},
					'revoke': {css: 'btn btn-danger fa fa-undo', dialogHeigth: 422, dialogWidth: 690}
				}
				if (buttonsStyle[alias]) return buttonsStyle[alias];
				return {css: "btn btn-success "};
			},
		 getBpmBizData(taskId) {
			 let that = this;
			 let bpmBizData;

			 $.ajax({
				 type: 'get',
				 url: `${Vue.__ctx}/common/bpm/form/getBpmBizData/${taskId}`,
				 data: {},
				 dataType: 'json',
				 async: false,
				 success: function (res) {
					 if (!res.isOk) {
						 top.$.Dialog.alert(res.msg);
						 return;
					 }

					 bpmBizData = res.data;
				 },
			 });

			 return bpmBizData;
		 },
	 },
	 template:'<div>                                                                                     						\
		 			<span v-for="btn in buttons" :class="getButtonStyle(btn.alias).css" v-on:click="buttonClick(btn)" style="margin-right: 5px;">                                            \
		 			{{btn.name}}</span>                                                               							\
	 		  </div>',

});

function getCustFormComponent(pageComponent){
	for(var i=0,c;c=pageComponent.$children[i++];){
		if(c.$options._componentTag==="ab-custom-form"){
			return c;
		}
		// url 表单则返回父页面
		if(c.$options._componentTag==="ab-url-form"){
			return pageComponent;
		}
	}
	// 不向下递归
	//console.error("页面中找不到 cust-form 的组件！！！");
	//return null;
	return pageComponent;
}
