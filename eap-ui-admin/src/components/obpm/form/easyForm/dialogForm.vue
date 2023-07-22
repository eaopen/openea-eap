<template>
    <div v-if="loaded" style="position: relative; padding-bottom: 30px;">
        <ab-custom-form style="padding: 0 15px" ref="abCustomForm"></ab-custom-form>
        <div style="width: 100%; text-align: right; padding:10px 15px 0 ; border-top: 1px #eee solid; position: absolute; bottom: 0">
          <el-button type="default" size="mini" @click="cancelDialog">取消</el-button>
          <el-button type="primary" size="mini" @click="save">提交</el-button>
        </div>
    </div>
</template>
<script>
import Vue from "vue"
import { filterHtml } from '@/utils/easyForm'
import { getFormConfig } from '@/api/obpm/form.js'
export default {
  name: 'easyForm',
  props: ['params'],
  data() {
    return {
        loaded: false,
        btnLoading: false,
        isDefaultUrl: true,//是否是默认的请求地址,

        formHtml: '',
        formData: '',
        permission: '',
        tablePermission: '',
        initData: ''
    }
  },
  methods: {
    validateForm(){
      return true
    },
    cancelDialog(){
      this.$store.dispatch('dialog/hided1')
    },
    save(){
      let url = '/form/formDefData/saveData'
      // console.log(this.$refs.save)
      if(this.params.key){
        url = `${url}?key=${this.params.key}&sql=${this.params.sql||''}`
      }
      this.$axios.post(url, this.$refs.abCustomForm.data).then(res=>{
        console.log(res)
        if(res.isOk){
          this.$store.dispatch('dialog/hided1')
          this.$alert('修改完成')
        }else {
          this.$alert(res.msg)
        }
      })

    },
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
    closeDialog(){
        let closeDom = window.parent.$(".work-dialog .el-dialog__headerbtn .el-dialog__close");
        if (closeDom.length && closeDom.length>0){
            console.log("closeDom")
            closeDom.click()
        }
        else{
            console.log("close Dialog")
            $.Dialog.close(window);
        }
    },
  },
  created: function () {
    // params.type == async
    if(this.params.async && this.params){
      if(!this.params.key){
        return
      }
      getFormConfig({
        key: this.params.key,
        id: this.params.id||'',
        sql: this.params.sql||'',
        tryNew: 1
      }).then(res=>{
      this.formHtml = res.data.html
      this.formData = res.data.data
      this.permission = res.data.permission
      this.tablePermission = res.data.tablePermission
      this.initData = res.data.initData
      let { html, customScript } = filterHtml(this.formHtml)
        let obj = {}
        obj.permission = this.permission
        obj.tablePermission = this.tablePermission
        obj.data = this.formData
        console.log('obj:', html)
        Vue.component('ab-custom-form',{
          template: html,
          mixins: [customScript],
          data(){
            return obj
          }
        })
        this.loaded = true;
      })
    }else {
      this.formHtml = this.params.formHtml
      this.formData = this.params.formData
      this.permission = this.params.permission
      this.tablePermission = this.params.tablePermission
      this.initData = this.params.initData
      let { html, customScript } = filterHtml(this.formHtml)
        let obj = {}
        obj.permission = this.permission
        obj.tablePermission = this.tablePermission
        obj.data = this.formData
        console.log('obj:', html)
        Vue.component('ab-custom-form',{
          template: html,
          mixins: [customScript],
          data(){
            return obj
          }
        })
        this.loaded = true;
    }
  },
}
</script>

