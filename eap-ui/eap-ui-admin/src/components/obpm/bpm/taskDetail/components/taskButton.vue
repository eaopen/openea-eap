<template>
  <el-button @click="buttonClick" size="mini" :type="myType.type">{{ data.name }}</el-button>
</template>
<script>
export default {
  name: "task-button",
  props: ["data"],
  data(){
    return {
      // <div ng-if="button.alias==\'start\'" 		buttonAlias="{{button.alias}}"   	ng-click="buttonClick(true)" 			class="btn btn-success fa fa-send">{{button.name}}</div>\
			// 		<div ng-if="button.alias==\'draft\'" 		buttonAlias="{{button.alias}}"   	ng-click="buttonClick(false)" 			class="btn btn-primary fa fa-clipboard">保存草稿</div>\
			// 		<div ng-if="button.alias==\'save\'" 		buttonAlias="{{button.alias}}"   	ng-click="buttonClick(false)" 			class="btn btn-primary fa fa-clipboard">保存</div>\
			// 		<div ng-if="button.alias==\'agree\'" 		buttonAlias="{{button.alias}}"   	ng-click="buttonClick(true,422,690)" 	class="btn btn-success fa fa-check-square-o">{{button.name}}</div>\
			// 		<div ng-if="button.alias==\'oppose\'" 		buttonAlias="{{button.alias}}"   	ng-click="buttonClick(true,422,690)" 	class="btn btn-primary fa fa-close">{{button.name}}</div>\
			// 		<div ng-if="button.alias==\'reject\'" 		buttonAlias="{{button.alias}}"   	ng-click="buttonClick(false,422,690)" 	class="btn btn-danger fa fa-lastfm">{{button.name}}</div>\
			// 		<div ng-if="button.alias==\'reject2Start\'" buttonAlias="{{button.alias}}"   	ng-click="buttonClick(false,300,500)" 	class="btn btn-danger fa fa-lastfm">{{button.name}}</div>\
			// 		<div ng-if="button.alias==\'lock\'" 		buttonAlias="{{button.alias}}"   	ng-click="buttonClick(false)" 			class="btn btn-primary fa fa-lock">锁定</div>\
			// 		<div ng-if="button.alias==\'unlock\'" 		buttonAlias="{{button.alias}}"   	ng-click="buttonClick(false)" 			class="btn btn-primary fa fa-unlock">解锁</div>\
			// 		<div ng-if="button.alias==\'taskOpinion\'" 	buttonAlias="{{button.alias}}"   	ng-click="buttonClick(false,500,900)" 	class="btn btn-primary fa fa-navicon">审批历史</div>\
			// 		<div ng-if="button.alias==\'flowImage\'" 	buttonAlias="{{button.alias}}"   	ng-click="buttonClick(false,600,800)" 	class="btn btn-primary fa fa-image">流程图</div>\
			//         <div ng-if="button.alias==\'manualEnd\'" 	buttonAlias="{{button.alias}}"   	ng-click="buttonClick(false,300,500)" 	class="btn btn-danger fa fa-ioxhost">人工终止</div>\
			// 		<div ng-if="button.alias==\'print\'" 		buttonAlias="{{button.alias}}"   	ng-click="buttonClick(false)" 			class="btn btn-primary fa fa-print">打印</div>\
			// 		<div ng-if="\'start,draft,save,agree,oppose,reject,reject2Start,lock,unlock,taskOpinion,flowImage,manualEnd,print,\'.indexOf(button.alias)==-1" 		buttonAlias="{{button.alias}}"   	ng-click="buttonClick(false)" 			class="btn btn-primary">{{button.name}}</div>\
      buttonType: {
        start: {
            name: '',
            type: 'success',
            icon: '',
            params: ''
        },
        draft: {
            name: '',
            type: 'primary',
            icon: '',
            params: ''
        },
        save: {
            name: '',
            type: 'primary',
            icon: '',
            params: ''
        },
        agree: {
            name: '',
            type: 'success',
            icon: '',
            params: ''
        },
        oppose: {
            name: '',
            type: 'primary',
            icon: '',
            params: ''
        },
        reject: {
            name: '',
            type: 'danger',
            icon: '',
            params: ''
        },
        reject2Start: {
            name: '',
            type: 'danger',
            icon: '',
            params: ''
        },
        lock: {
            name: '',
            type: 'danger',
            icon: '',
            params: ''
        },
        unlock: {
            name: '',
            type: 'success',
            icon: '',
            params: ''
        },
        taskOpinion: {
            name: '',
            type: 'primary',
            icon: '',
            params: ''
        },
        flowImage: {
            name: '',
            type: 'primary',
            icon: '',
            params: ''
        },
        manualEnd: {
            name: '',
            type: 'primary',
            icon: '',
            params: ''
        },
        print: {
            name: '',
            type: 'primary',
            icon: '',
            params: ''
        },
        other: {
            name: '',
            type: 'success',
            icon: '',
            params: ''
        },
      }
    }
  },
  computed:{
    myType(){
      if(this.data.alias && this.buttonType[this.data.alias]){
        return this.buttonType[this.data.alias]
      }else {
        return this.buttonType.other
      }
    }
  },
  methods:{
    buttonClick(){
      if(this.data.configPage){
        let str = this.data.configPage.replace('/bpm/task/','').replace('Dialog.html','')
        this.$emit('openDialog',str)
      }

    }
  }
}
</script>

