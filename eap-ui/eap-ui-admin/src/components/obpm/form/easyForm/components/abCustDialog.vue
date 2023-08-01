<template>
	<a class="btn btn-primary fa-search"   @click= "showDialog()" ><slot></slot></a>
</template>
<script>

    /**
     * 自定义对话框，使用实例如下：
     * <ab-cust-dialog dialogKey="userSelector" :dialogSetting="dialogSetting" :searchParam="{对象形式，对话框参数传入}" value-name="name" value-mobile="mobile"></ab-cust-dialog>
     * props:["dialogKey","dialogSetting","model"],//dialogKey:列表对话框的key;dialogSetting:对话框的配置修改;model赋值对象
     * <ab-cust-dialog dialogKey="orgSelector" :dialogSetting="dialogSetting" :model="obj" value-name="name" value-mobile="mobile"></ab-cust-dialog>
     * model如果是object，那么会根据value-xxx字段赋值这个object对象
     * model如果是list数组，那么会根据value-xxx组装成的json,push到model中。。model.push(json) 注意由于数组只能是push不会减少数组内容，所以数组类型下不支持初始化选项的行为
     */
    export default {
        props:["dialogKey","dialogSetting","model","param","queryParam"],//dialogKey:列表对话框的key;dialogSetting:对话框的配置修改
        data: function () {
            return {
                initData:[],
                valueMap:{},
                searchParam:{},
                dialogName:""
            }
        },
        created : function(element){
            var that = this;
            //映射关系
            for(var key in this.$attrs){
                var val = this.$attrs[key];
                // 找到value开头的赋值配置
                if (key.indexOf("value-") !== 0) {
                    continue;
                }
                var name = key.replace("value-", "");
                this.valueMap[name] = val;
            };

            this.initData = this.getInitData();
        },
        methods: {
            //处理返回数据
            showDialog: function(){
                var that = this;
                var initData = this.getInitData();
                var dialogSetting = this.dialogSetting;
                var queryParam = this.getQueryParam();
                CustUtil.openCustDialog(that.dialogKey,queryParam, function(data) {
                    //忽略大小写的数据 value-这种属性的赋值形式不能存在大小写的，真郁闷，以后要改掉
                    var dataIgnoreCase = [];
                    data.forEach(function(item) {
                        var json = {};
                        for(var key in item){
                            json[key.toLowerCase()] = item[key];
                        }
                        dataIgnoreCase.push(json);
                    });
                    that.handleData(dataIgnoreCase);
                }, initData,dialogSetting,true);
            },
            handleData:function(data){
                var valueMap = this.valueMap;
                if(Array.isArray(this.model)){//model是列表
                    var list = this.model;

                    data.forEach(function(item){
                        if(JSON.stringify(valueMap)==="{}"){//无映射关系则把数据全返回
                            list.push(item);
                            return;
                        }

                        var json = {};
                        //处理映射关系
                        for(var key in valueMap){
                            var val = valueMap[key]+"";
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
                        list.push(json);
                    });
                }else{//组件是对象
                	let obj = {};
                    for(var key in valueMap){
                        var val = valueMap[key];
                        var list = [];
                        data.forEach(function(item) {
                            list.push(item[key]);
                        });
                        this.$set(this.model,val,list.join(','));
						obj[val] = list.join(',');
                    }
					this.$emit('change', obj);
                }
            },
            getInitData:function(){
                if(!this.model) return;
                if(Array.isArray(this.model)){//数组不需要初始化，不会对数组进行删除行为
                    return [];
                }

                var initData = [];
                var json = null;
                for(var key in this.valueMap){
                    var data = this.model[key];
                    if(!data){
                        continue;
                    }
                    if(!json){
                        json = {};
                    }
                    eval("json[key]=data");
                }

                if(!json){
                    return initData;
                }

                //切割json中的,当作多选
                for(var key in json){
                    var val = json[key]+"";
                    var list = val.split(",");
                    var index = 0;

                    list.forEach(function(item) {
                        if(!initData[index]){
                            initData[index] = {};
                        }
                        initData[index][key] = item;
                        index++;
                    });
                }

                return initData;
            },
            getQueryParam:function(){
            	if(this.queryParam)return this.queryParam;
            	
            	// 老版本的获取方式
                if(this.param){
	                try{
	                    var paramJson =  {};
	                    var path = "";
	                    for(var key in this.param){
	                        path = "this.$vnode.context."+this.param[key];
	                        paramJson[key] =  eval(path);
	                    }
	                    return paramJson;
	                }catch(e){
	                    console.error("获取对话框动态传参失败！",this.param,path);
	                }
                }
            	return {};
            }
        },
        mounted:function(){
            var $vm = this;
        }
    }
</script>
