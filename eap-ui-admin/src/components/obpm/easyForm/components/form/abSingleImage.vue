<template>
    <div>
        <div class="list-img-box" :style="{'width': listWidth + 'px'}">
            <div v-if="form.path" class="img-inner">
                <img @click="preview()" :src="form.path" alt="自定义封面" :style="{'maxWidth': listWidth - 2 + 'px', 'maxHeight': '118px'}">
                <div class="delete-item"><i class="el-icon-error" style="font-size: 24px;" @click="delPath()"> </i></div>
            </div>
            <div v-else class="upload-btn"  @click="uploadPicture('flagImg')">
                <i class="el-icon-plus" style="font-size: 30px;"></i>
                <span>点击上传</span>
            </div>
        </div>
        <input type="hidden" v-model="form.id" placeholder="添加图片">
        <el-dialog title="裁切图片" :visible.sync="cropperModel" width="1000px" top="50px" :close-on-click-modal="false" center>
            <cropper-image v-if="cropperModel" :outerScale="outerScale" :outerwidth="outerwidth" :outerheight="outerheight" :id="form.id" @uploadImgSuccess="handleUploadSuccess">
            </cropper-image>
        </el-dialog>
    </div>
</template>

<script>
import cropperImage from "./cropperImage.vue"
export default {
    name: "AbSingleImage",
    components: {cropperImage},
    props: {
        outerScale: {
            type: Number,
            default: 1.5
        },
        outerwidth: {
            type: Number
        },
        outerheight: {
            type: Number
        },
        value:{
            type: String
        },
        abValidate:{
            type: String
        }
    },
    computed:{
        listWidth(){
            if(this.outerheight && this.outerwidth){
                return 120 * this.outerwidth / this.outerheight
            }else {
                return 160
            }
        }
    },
    mounted(){
        console.log(this.abValidate)
        if(this.value){
            this.form.id = this.value
            this.form.path = window.__ctx +'/sys/etechFile/webuploader/previewImg/'+ this.value;
        }
    },
    data() {
        return {
            form: {
                path: '',
                name: '',
                id: ''
            },
            ruleValidate: {
                mainImage: [
                    { required: true, message: '请上传封面', trigger: 'blur' }
                ],
            },
            //裁切图片参数
            cropperModel: false,
            cropperName: '',
            imgName: '',
            imgVisible: false
        }
    },
    watch:{
        value(v){
            this.form.id = v;
            if(v){
                this.form.path = window.__ctx +'/sys/etechFile/webuploader/previewImg/'+ v
            }else {
                this.form.path = ""
            }
        }
    },
    methods: {
        delPath(){
            this.$parent.delFileHandler(this.form.id)
            this.$emit('input',"")
        },
        preview(){
            window.open(this.form.path)
        },
        //封面设置
        uploadPicture(name) {
            this.form.name = name;
            this.cropperModel = true;
        },
        //图片上传成功后
        handleUploadSuccess(data) {
            if(data.path){
                this.form.path = window.__ctx +'/sys/etechFile/webuploader/previewImg/'+ data.id;
                this.form.id = data.id
                this.$emit('input', this.form.id)
                this.$parent.addFileHandler(this.form.id)
            }
            this.cropperModel = false;
        }
    }
}
</script>

<style scoped lang="scss">
.list-img-box {
    height: 120px;
    border: 1px #eee solid;
    text-align: center;
    .upload-btn{
        display: flex;
        height: 120px;
        align-items: center;
        justify-content: center;
    }
   .img-inner{
     position: relative;
   }
   .delete-item {
        position: absolute;
        right: -11px;
        top: -11px;
        z-index: 1;
        width: 24px;
        height: 24px;
        line-height: 24px;
        text-align: center;
        font-size: 18px;
        border-radius: 50%;
        cursor: pointer;
        background-color: #fff;
        img {
            cursor: pointer;
        }
        &:hover {
            color: #ce2121;
        }
    }
}
</style>

