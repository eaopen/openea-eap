<template>
  <div>
    <div v-show="abPermission=='w'||abPermission=='b'" :style="style">
      <textarea></textarea>
    </div>
    <div v-show="abPermission=='r'" v-html="value"></div>
  </div>
</template>
<script>
import Vue from 'vue';

export default {
  props:{
    //uedtor的配置
    config : {
      default : function () {
        var json = {
          focus : true,
          toolbars : [ [ 'source', 'undo', 'redo', 'bold', 'italic', 'removeformat', 'formatmatch', 'autotypeset', 'blockquote', 'pasteplain', '|', 'forecolor', 'backcolor', 'insertorderedlist', 'insertunorderedlist' ] ],
          initialFrameHeight : this.$attrs.height || 150,
          autoClearEmptyNode : true,
          enableAutoSave: false
        }
        if(this.$attrs.width){
          json.initialFrameWidth = this.$attrs.width;
        }
        return json;
      }
    },
    value : {
      required: true
    },
    abPermission : {
      required : false,
      default : "w"
    },

  },
  data: function () {
    return {
      editor : null ,
      insideChange : false,//ueditor是否内部值变化
      style :{
        border : ""
      },
    }
  },
  created : function(){
  },
  mounted : function(){
    this.$el.style.cssText = "";//不要只为了表单设计渲染的style

    this.editor = new UE.ui.Editor(this.config);
    this.editor.render(this.$el.children[0].children[0]);
    /* //百度UEditor数据更新时，更新value
 this.editor.addListener('contentChange', () function() {
   this.$emit('input', this.editor.getContent());
   this.insideChange = true;
 }); */
    var that = this;

    this.editor.ready(function() {
      if(that.value){
        that.editor.setContent(that.value);
      }
    });

    //百度UEditor在源码模式修改代码时，无法触发contentChange，所以只能用无脑地监听了！
    setInterval(function() {
      that.handleStyle();
      if(that.value != that.editor.getContent()){
        that.insideChange = true;
        that.$emit('input', that.editor.getContent());
      }
    },500);
  },
  methods: {
    handleStyle : function(){
      if(!this.$vnode.context.$validity){
        this.$vnode.context.$validity = {};
      }
      if(this.abPermission == "b"){
        if(!this.getSubTextFromHtml(this.value)){
          this.$vnode.context.$validity[this.$attrs.desc] = "必填";
          this.style.border = "1px solid red";
        }else{
          delete this.$vnode.context.$validity[this.$attrs.desc];
          this.style.border = "";
        }
      }
    },
    getSubTextFromHtml : function(htmlStr){
      if(!htmlStr){
        return "";
      }

      var div = document.createElement('div');
      div.innerHTML = htmlStr.replaceAll("​","").replace(/&.+?;/g, '');//"​"这个是ueditor的一个隐藏字符！！丫的，坑了我很久，可以复制到chrome看
      return div.innerText;
    }
  },
  watch : {
    value : function(newVal,oldVal){
      this.handleStyle();
      if(this.insideChange){
        this.insideChange = false;
      }else{
        if (newVal != oldVal) {
          this.editor.setContent(this.value);
        }
      }
    }
  }
}
</script>
<style scoped>
.ab-invalid {
  border : 1px solid red;
}
</style>
