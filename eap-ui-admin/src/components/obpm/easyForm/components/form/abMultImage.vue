<template>
    <div>
        <div class="list-img-box">
            <div class="img-item" v-for="item, index in list" :key="item.id" :style="{'width': imgWidth + 'px'}">
                <img :src="item.path" @click="preview(item)">
                <div class="delete-item"><i class="el-icon-error" style="font-size: 18px;" @click="delPath(index)"> </i></div>
            </div>
            <div class="upload-btn"  @click="uploadPicture('flagImg')" v-if="list.length < maxcount">
                <i class="el-icon-plus" style="font-size: 30px;"></i>
            </div>
        </div>
        <input type="hidden" v-model="currentValue">
        <el-dialog v-if="cropperModel" title="裁切图片" :visible.sync="cropperModel" width="1000px" top="50px" :close-on-click-modal="false" center>
            <cropper-image v-if="cropperModel" :outerwidth="outerwidth" :outerheight="outerheight" @uploadImgSuccess="handleUploadSuccess">
            </cropper-image>
        </el-dialog>
    </div>
</template>

<script>
import cropperImage from "./cropperImage.vue"
export default {
    name: "AbMultImage",
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
        maxcount:{
            type: Number,
            default: 10
        },
        mincount: {
            type: Number,
            default: 0
        }
    },
    mounted(){
        this.list = []
        if(this.value){
            let arr = this.value.split(",")
            arr.forEach(item=>{
                this.list.push({
                    id: item,
                    path: window.__ctx +'/sys/etechFile/webuploader/previewImg/'+ item
                })
            })
        }
    },
    data() {
        return {
            currentValue: "",
            list : [],
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
            this.list = []
            if(v){
                let arr = v.split(",")
                arr.forEach(item=>{
                    this.list.push({
                        id: item,
                        path: window.__ctx +'/sys/etechFile/webuploader/previewImg/'+ item
                    })
                })
            }
        }
    },
    computed:{
        imgWidth(){
            if(this.outerheight && this.outerwidth){
                return 80 * this.outerwidth / this.outerheight
            }else {
                return 120
            }
        }
    },
    methods: {
        delPath(index){
            this.$parent.delFileHandler(this.list[index].id)
            this.list.splice(index, 1)
            this.currentValue = this.list.map(item=>item.id).join(",")
            this.$emit('input',this.currentValue )
        },
        preview(item){
            window.open(item.path)
        },
        //封面设置
        uploadPicture(name) {
            this.cropperModel = true;
        },
        //图片上传成功后
        handleUploadSuccess(data) {
            if(data.path){
                this.list.push({
                    id: data.id,
                    path: window.__ctx +'/sys/etechFile/webuploader/previewImg/'+ data.id
                })
                this.$parent.addFileHandler(data.id)
                this.currentValue = this.list.map(item=>item.id).join(",")
                this.$emit('input',this.currentValue )
            }
            this.cropperModel = false;
        }
    }
}
</script>

<style scoped lang="scss">
.list-img-box {
    width: 100%;
    text-align: left;
    .img-item {
        float: left;
        margin-right: 10px;
        margin-bottom: 10px;
        height: 80px;
        border: 1px #efefef solid;
        position: relative;
        img {
            height: 78px;
            cursor: pointer;
        }
        .delete-item {
            position: absolute;
            right: -8px;
            top: -8px;
            z-index: 1;
            width: 14px;
            height: 14px;
            font-size: 18px;
            cursor: pointer;
        }
    }
    .upload-btn{
        float: left;
        width: 120px;
        height: 80px;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 10px;
        border: 1px #efefef solid;
        cursor: pointer;
    }
}
</style>

