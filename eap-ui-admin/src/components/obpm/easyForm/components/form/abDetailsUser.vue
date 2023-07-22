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
  props: ['param','text'],

  data() {
    return {
      data:{

      },
      detailsUser:''
    }
  },
  components:{

  },
  mounted : function(){
      this.detailsUrl="/org/user/userDetail.html?id="+this.param

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
  },

  created: function () {

  },
  watch: {


  }
}
</script>

<style >

</style>