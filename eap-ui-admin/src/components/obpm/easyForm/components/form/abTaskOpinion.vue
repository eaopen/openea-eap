<template>
  <div class="taskOpinionHis" :class="divCss">
    <table :class="tableCss" style="margin-top: 5px">
      <thead>
      <tr v-show="opinionList.length>0">
        <th v-for="f in showFields.split(',')">{{fieldMap[f]}}</th>
      </tr>
      </thead>
      <tr v-for="o in opinionList">
        <td v-for="f in showFields.split(',')">
          <span v-if="f === 'assignInfo'" style="height: 6px">{{assignInfo(o.assignInfo)}}</span>
          <span v-else-if="f === 'status'" style="height: 6px">{{OpinionStatusKeyMap[o.status]}}</span>
          <span v-else style="height: 6px">{{o[f]}}</span>
        </td>
      </tr>
    </table>
    <table :class="tableCss" v-if="inputOpinion  && !isReadOnly">
      <tr>
        <th style="width: 100px;">意见输入</th>
        <td>
          <textarea class="form-control" v-model="flowRequestParam.opinion" style="width: 100%;height: 100%;" placeholder="请录入审批意见"> </textarea>
        </td>
      </tr>
    </table>
  </div>
</template>

<script>
import Vue from 'vue';

export default {
  props: {
    flowRequestParam : {
    },
    showFields : {
      default : "taskName,approveTime,approverName,status,opinion,viewForm"
    },
    //表单风格，普通：normal，打印：print
    tableStyle : {
      default : "print"
    },
    inputOpinion :{
      default : false
    }
  },
  data :function () {
    return {
      isReadOnly :false,
      opinionList:[],
      OpinionStatusKeyMap:{},
      fieldMap : {
        taskName : "任务名称",
        createTime : "创建时间",
        approveTime : "处理时间",
        assignInfo : "候选人",
        approverName : "执行人",
        status : "处理状态",
        opinion : "备注/意见",
        viewForm : "查看表单"
      },
      divCss : "form-table-print",
      tableCss : "form-table form-table-print form-table-print-sub"
    }
  },
  created : function(){
    // 非草稿，且非任务处理时
    if(!this.flowRequestParam.taskId && this.flowRequestParam.started){
      this.isReadOnly = true;
    }

    if(!this.flowRequestParam || !this.flowRequestParam.instanceId){
      return;
    }

    if(this.tableStyle=="normal"){
      this.divCss = "ibox-content";
      this.tableCss = "form-table";
    }

    var defer = Vue.baseService.postForm(__ctx+"/etech/instance/getOpinionEx", {
      instId : this.flowRequestParam.instanceId,
      taskId : this.flowRequestParam.taskId ||''
    });
    var scope = this;
    $.getResultData(defer, function(data) {
      scope.opinionList = [];
      for(var j=0,item;item=data[j++];){
        if (item.assignInfo == "所有会签用户" && !item.approveTime) {
          return;
        }
        scope.opinionList.push(item);
      }
    })

    ToolsController.getEnum("com.sec.etech.bpm.constant.EtechOpinionStatus").then(function(data) {
      for(var key in data){
        scope.OpinionStatusKeyMap[data[key].key] = data[key].value;
      }
    });
  },
  methods: {
    assignInfo:function(info){
      if (!info) {
        return "";
      }
      if(info.indexOf("-")==-1){
        return info;
      }
      var msg = "";
      info.split(",").forEach(item => {
        if (!item) {
          return;
        }
        if (msg) {
          msg += "；";
        }
        var strs = item.split("-");
        var typeName = "";
        if (strs[0] === "user") {
          typeName = "[用户]";
        } else if (strs[0] === "role") {
          typeName = "[角色]";
        } else if (strs[0] === "group") {
          typeName = "[组]";
        } else if (strs[0] === "org") {
          typeName = "[组织]";
        } else if (strs[0] === "post") {
          typeName = "[岗位]";
          if (strs[2]) {
            return msg += typeName + strs[1] + "-" + strs[2];
          }
        } else if (strs[0] === "job") {
          typeName = "[职称]";
        }
        msg += typeName + strs[1];
      });
      return msg;
    },
    viewForm:function(instId, nodeId){
      return "查看表单";
    }
  }
}
</script>
