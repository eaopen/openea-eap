<template>
  <div>
    <div v-if="text">
      <div v-on:click="openUploadDialog()"><slot><button type="button" class="btn btn-primary">{{this.text}}</button></slot></div>
    </div>
    <div v-else>
      <button type="button" class="btn btn-primary" v-on:click="openUploadDialog()"><slot>详情</slot></button>
    </div>
  </div>
</template>

<script>
export default {
  props: ['param','text','url'],

  data() {
    return {
      data:{

      },
      detailsUrl:''
    }
  },
  components:{

  },
  mounted : function(){
    var param=parseToJson(this.param);
    debugger
    if(!param){
      this.detailsUrl=this.url;
    }else {
      for(let key in param){
        this.addURLParam(this.url,key,param[key]);
      }
      this.detailsUrl =this.url;
    }
  },
  methods: {
    openUploadDialog(){
      var that = this;
      console.log()
      var conf = {
        height : 600,
        width : 800,
        url : that.detailsUrl,
        title : "详情页面",
        topOpen : true,
        btn : true,
        closeBtn : 1,
      };
      conf.ok = function(index, innerWindow) {
        $.Dialog.close(innerWindow);
      }
      jQuery.Dialog.open(conf);
    },
    addURLParam(url,name,value){
      url += (url.indexOf("?")==-1?"?":"&");
      url += encodeURIComponent(name)+"="+encodeURIComponent(value);
      this.url= url;
    }
  },

  created: function () {

  },
  watch: {


  }
}
</script>

<style >

</style>