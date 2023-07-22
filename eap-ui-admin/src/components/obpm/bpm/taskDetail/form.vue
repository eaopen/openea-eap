<template>
     <div>
        <div class="ibox-title" style="padding: 10px 15px;border: none">
            <button class="btn btn-primary fa-save" v-on:click="saveData" :disabled="btnLoading">提交</button>
            <button v-if="showCancelButton" class="btn btn-primary fa-reply" @click="closeDialog">关闭</button>
        </div>
        <div v-if="loaded">
            <ab-custom-form style="padding: 0 15px"></ab-custom-form>
        </div>
    </div>
</template>
<script>
import Vue from "vue"
import { getFormConfig } from '@/api/obpm/form.js'
export default {
  name: 'easyForm',
  props: ['formKey'],
  data() {
    return {
        loaded: false,
        btnLoading: false,
        isDefaultUrl: true,//是否是默认的请求地址,
        showCancelButton: true
    }
  },
  methods: {
    saveData: async function () {
        var errorMsg = Vue.formService.getValidateMsg(this.$children[0]);
        if (errorMsg) {
            top.$.Dialog.alert("表单校验不通过！" + errorMsg, 7);
            return;
        }
        // 业务校验
        if (this.$children[0].custValid) {
            let r = await this.$children[0].custValid();   //await 用于等待后续函数的返回值
            console.log(r)
            if (r === false) {
                return
            }
        }

        let formActionUrl = this.$children[0].formActionUrl;
        let logType = this.$children[0].logType;
        //是否调用的默认的保存接口，默认保存有处理文件的保存和删除，如果不是默认的下面手动调用一次
        this.isDefaultUrl = formActionUrl && formActionUrl.indexOf("/form/formDefData/saveData?") === -1 ? false : true;
        formActionUrl = formActionUrl || "/form/formDefData/saveData?key=" + $.getParam("key") + "&sql=" + $.getParam("sql") + (logType ? `&log_type=${logType}` : '');
        let formActionData = this.$children[0].formActionUrl ? (this.$children[0].formActionData || this.$children[0].$data.data) : this.$children[0].$data.data;
        var url = Vue.__ctx + formActionUrl;
        let that = this
        that.btnLoading = true
        var post = Vue.baseService.post(url, formActionData);
        post.then(function (data) {
            that.btnLoading = false
            if (data.isOk) {
                that.afterHandlerFile();
                if (that.$children[0].afterSave) {
                    that.$children[0].afterSave(data);
                    if (parent && parent.reloadGrid) {
                        parent.reloadGrid();
                    } else if (window.opener && window.opener.reloadGrid) {
                        opener.reloadGrid();
                    }
                    setTimeout(() => {
                        top.$.Toast.success("操作成功")
                        $.Dialog.close(window);
                    }, 800)
                } else if (that.$children[0].beforeCloseWindow) {
                    that.$children[0].beforeCloseWindow(data);
                    if (parent && parent.reloadGrid) {
                        parent.reloadGrid();
                    } else if (window.opener && window.opener.reloadGrid) {
                        opener.reloadGrid();
                    }
                    setTimeout(() => {
                        top.$.Toast.success("操作成功")
                        $.Dialog.close(window);$.Dialog.close(window);
                    }, 800)
                } else {
                    if (parent && parent.reloadGrid) {
                        parent.reloadGrid();
                    } else if (window.opener && window.opener.reloadGrid) {
                        opener.reloadGrid();
                    }
                    top.$.Toast.success("操作成功")
                    $.Dialog.close(window);
                }
                if (parent && parent.reloadGrid) {
                    parent.reloadGrid();
                } else if (window.opener && window.opener.reloadGrid) {
                    opener.reloadGrid();
                }
                // 去掉"是否继续"
                // top.$.Dialog.confirm("提示信息", "操作成功", function () {
                //     if (that.$children[0].beforeCloseWindow) {
                //         that.$children[0].beforeCloseWindow(data);
                //     }
                //     $.Dialog.close(window);
                // }, function () {
                // }, {btn: ['确定'], icon: 6});
            } else {
                if (data.msg.startsWith("提示")){
                    $.Toast.warning(data.msg);
                    $.Dialog.close(window);
                    if (parent && parent.reloadGrid) {
                        parent.reloadGrid();
                    } else if (window.opener && window.opener.reloadGrid) {
                        opener.reloadGrid();
                    }
                }else{
                    $.Dialog.error(data.msg);
                }


            }

        }, function (e) {
            alert(e);
        });
    },
    afterHandlerFile() {
        if (this.isDefaultUrl) {
            return;
        }
        let formActionData = this.$children[0].formActionData || this.$children[0].$data.data;
        if (formActionData.add_file_list || formActionData.del_file_list) {
            Vue.baseService.post(Vue.__ctx + "/sys/sysFile/webuploader/afterSaveFile", {
                'add_file_list': formActionData.add_file_list,
                'del_file_list': formActionData.del_file_list
            }).then(function (data) {
                if (!data.isOk) {
                    toastr.error(data.msg);
                }
            });
        }
    },
  },
  created: function () {
    getFormConfig({
      key: this.formKey
    }).then(res=>{
      const html = res.data.html.replace(/\u200B/g, '');
      Vue.component('ab-custom-form',{
        template: html
      })
      this.loaded = true;
      // console.log(this)

    })
  },
}
</script>

