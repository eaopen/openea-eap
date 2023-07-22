<template>
    <div class="cropper-content">
      <div class="cropper-box">
        <div class="cropper">
          <vue-cropper
              :ref="refName"
              :img="option.img"
              :outputSize="option.outputSize"
              :outputType="option.outputType"
              :info="option.info"
              :canScale="option.canScale"
              :autoCrop="option.autoCrop"
              :autoCropWidth="option.autoCropWidth"
              :autoCropHeight="option.autoCropHeight"
              :fixed="option.fixed"
              :fixedNumber="option.fixedNumber"
              :full="option.full"
              :fixedBox="option.fixedBox"
              :canMove="option.canMove"
              :canMoveBox="option.canMoveBox"
              :original="option.original"
              :centerBox="option.centerBox"
              :height="option.height"
              :infoTrue="option.infoTrue"
              :maxImgSize="option.maxImgSize"
              :enlarge="option.enlarge"
              :mode="option.mode"
              @realTime="realTime"
              @imgLoad="imgLoad">
          </vue-cropper>
        </div>
        <!--底部操作工具按钮-->
        <div class="footer-btn">
          <div class="scope-btn">
            <label class="btn" for="uploads">选择图片</label>
            <input type="file" id="uploads" style="position:absolute; clip:rect(0 0 0 0);" accept="image/png, image/jpeg, image/gif, image/jpg" @change="selectImg($event)">
            <el-button size="mini" type="danger" plain icon="el-icon-zoom-in" @click="changeScale(1)">放大</el-button>
            <el-button size="mini" type="danger" plain icon="el-icon-zoom-out" @click="changeScale(-1)">缩小</el-button>
            <el-button size="mini" type="danger" plain @click="rotateLeft">左旋转</el-button>
            <el-button size="mini" type="danger" plain @click="rotateRight">右旋转</el-button>
          </div>
          <div class="upload-btn">
            <el-button size="mini" type="success" @click="uploadImg('blob')">上传图片 <i class="el-icon-upload"></i></el-button>
          </div>
        </div>
      </div>
      <!--预览效果图-->
      <div class="show-preview">
        <div :style="previews.div" class="preview">
          <img :src="previews.url" :style="previews.img">
        </div>
      </div>
    </div>
  </template>

  <script>
  import { VueCropper } from 'vue-cropper'
  export default {
    name: "CropperImage",
    props: {
      outerScale: {
            type: Number
        },
        outerwidth: {
            type: Number
        },
        outerheight: {
            type: Number
        },
        name: {
            type: String
        },
        id: {
            type: String
        }
    },
    components: {
      VueCropper
    },
    created(){
      this.refName = 'cropper'+uuid
      if(this.outerheight && this.outerwidth){
        if(this.outerwidth > 300){
          this.option.autoCropWidth = 300
          this.option.autoCropHeight = 300 * this.outerheight / this.outerwidth
        }else {
          this.option.autoCropWidth = this.outerwidth
          this.option.autoCropHeight = this.outerheight
        }
      }else {
        this.option.autoCropWidth = 300
        this.option.autoCropHeight = 200
      }
    },
    data() {
      return {
        refName: '',
        previews: {},
        option:{
          img: '',             //裁剪图片的地址
          outputSize: 1,       //裁剪生成图片的质量(可选0.1 - 1)
          outputType: 'jpeg',  //裁剪生成图片的格式（jpeg || png || webp）
          info: true,          //图片大小信息
          canScale: true,      //图片是否允许滚轮缩放
          autoCrop: true,      //是否默认生成截图框
          autoCropWidth: 0,  //默认生成截图框宽度
          autoCropHeight: 0, //默认生成截图框高度
          fixed: false,         //是否开启截图框宽高固定比例
          fixedNumber: [1, 0.63], //截图框的宽高比例
          full: false,         //false按原比例裁切图片，不失真
          fixedBox: true,      //固定截图框大小，不允许改变
          canMove: false,      //上传图片是否可以移动
          canMoveBox: true,    //截图框能否拖动
          original: false,     //上传图片按照原始比例渲染
          centerBox: true,    //截图框是否被限制在图片里面
          height: true,        //是否按照设备的dpr 输出等比例图片
          infoTrue: false,     //true为展示真实输出图片宽高，false展示看到的截图框宽高
          maxImgSize: 3000,    //限制图片最大宽度和高度
          enlarge: 1,          //图片根据截图框输出比例倍数
          mode: '650px 500px'  //图片默认渲染方式
        }
      };
    },
    methods: {
      //初始化函数
      imgLoad (msg) {
        console.log("工具初始化函数====="+msg)
      },
      //图片缩放
      changeScale (num) {
        num = num || 1
        this.$refs[this.refName].changeScale(num)
      },
      //向左旋转
      rotateLeft () {
        this.$refs[this.refName].rotateLeft()
      },
      //向右旋转
      rotateRight () {
        this.$refs[this.refName].rotateRight()
      },
      //实时预览函数
      realTime (data) {
        this.previews = data
      },
      //选择图片
      selectImg (e) {
        let file = e.target.files[0]
        if (!/.(jpg|jpeg|png|JPG|PNG)$/.test(e.target.value)) {
          this.$message({
            message: '图片类型要求：jpeg、jpg、png',
            type: "error"
          });
          return false
        }
        //转化为blob
        let reader = new FileReader()
        reader.onload = (e) => {
          let data
          if (typeof e.target.result === 'object') {
            data = window.URL.createObjectURL(new Blob([e.target.result]))
          } else {
            data = e.target.result
          }
          this.option.img = data
        }
        //转化为base64
        reader.readAsDataURL(file)
      },
      //上传图片
      uploadImg (type) {
        let _this = this;
        if (type === 'blob') {
          //获取截图的blob数据
          this.$refs[this.refName].getCropBlob(async (data) => {
            let formData = new FormData();
            formData.append('file',data, 'DX.jpg')
            if(this.id){
              formData.append('id', this.id)
            }
            //调用axios上传
            let res = await Vue.baseService.post(Vue.__ctx + "/sys/etechFile/webuploader/uploadImg", formData)
            console.log(res)
            if(res.code === "200"){
              _this.$message({
                message: res.msg,
                type: "success"
              });
              _this.$emit('uploadImgSuccess',res.data);
            }else {
              _this.$message({
                message: '文件服务异常，请联系管理员！',
                type: "error"
              });
            }
          })
        }
      },
    },
  }
  </script>

  <style scoped lang="scss">
  .cropper-content{
    display: flex;
    display: -webkit-flex;
    justify-content: flex-end;
    .cropper-box{
      flex: 0 0 640px;
      .cropper{
        width: auto;
        height: 500px;
      }
    }

    .show-preview{
      flex: 1;
      -webkit-flex: 1;
      display: flex;
      display: -webkit-flex;
      justify-content: center;
      .preview{
        overflow: hidden;
        border:1px solid #67c23a;
        background: #cccccc;
      }
    }
  }
  .footer-btn{
    margin-top: 30px;
    display: flex;
    display: -webkit-flex;
    justify-content: flex-end;
    .scope-btn{
      display: flex;
      display: -webkit-flex;
      justify-content: space-between;
      padding-right: 10px;
    }
    .upload-btn{
      flex: 1;
      -webkit-flex: 1;
      display: flex;
      display: -webkit-flex;
      justify-content: center;
    }
    .btn {
      outline: none;
      display: inline-block;
      line-height: 1;
      white-space: nowrap;
      cursor: pointer;
      -webkit-appearance: none;
      text-align: center;
      -webkit-box-sizing: border-box;
      box-sizing: border-box;
      outline: 0;
      -webkit-transition: .1s;
      transition: .1s;
      font-weight: 500;
      padding: 8px 15px;
      font-size: 12px;
      border-radius: 3px;
      color: #fff;
      background-color: #409EFF;
      border-color: #409EFF;
      margin-right: 10px;
    }
  }
</style>
