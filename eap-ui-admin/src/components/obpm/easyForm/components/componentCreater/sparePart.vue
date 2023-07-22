<template>
    <el-dialog title="85备件" width="600px" :visible.sync="dialogVisible" :close-on-click-modal="false">
        <el-form :model="form" ref="form" label-width="80px" :rules="rules">
            <el-row>
                <el-col :span="12">
                    <el-form-item label="即将添加">
                       <b>{{ form.num }}</b> 
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="示例">
                        <span style="color: #666">{{ form.exam }}</span>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="制造单批号" prop="ipt1" label-width="100px">
                        <el-input @blur="createNum(0)" v-model="form.ipt1" placeholder="输入批号如196-02" autocomplete="off" size="mini"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="专业方向" prop="ipt2">
                        <el-select v-model="form.ipt2" size="mini" @change="createNum(0)">
                          <el-option :label="item.label" :value="item.value" v-for="item in snids" :key="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="备件信息" prop="ipt3">
                        <el-input @change="createNum(0)" v-model="form.ipt3" placeholder="请补全部套号所需信息" autocomplete="off" size="mini"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="部套名称" prop="ipt4">
                        <el-input v-model="form.ipt4" placeholder="请输入部套中文名称" autocomplete="off" size="mini"></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button size="mini" @click="dialogVisible = false">取 消</el-button>
            <el-button size="mini" type="primary" @click="submit()">确 定</el-button>
          </div>
    </el-dialog>
</template>
<script>
export default {
    name: 'sparePart',
    props: [ 'num','name'],
    data(){
        var ipt1Pass =  (rule, value, callback) => {
            let reg = /^[A-Za-z0-9\-]+$/
            if(!value){
                return callback(new Error('制造单批号不能为空'));
            }else if(value.length > 11 || value.length < 3){
                return callback(new Error('制造单批号只能有3-11位'));
            }else if(!reg.test(value)){
                return callback(new Error('制造单批号格式不正确'));
            }else {
                callback() 
            }
        }

        var ipt3Pass =  (rule, value, callback) => {
            // let reg = /^[0-9]+$/
            if(!value){
                callback()
            }else if(value.indexOf('-')!= -1){
                return callback(new Error('备件信息格式错误'));
            }else {
                callback() 
            }
        }
        return {
            dialogVisible: false,
            form:{
                ipt1: '',
                ipt2: '',
                ipt3: '',
                ipt4: '',
                ipt5: '',
                num: '',
                exam: '85.C196-03-a11G01'
            },
            rules:{
                ipt1:{required: true,validator:ipt1Pass, trigger: 'change'},
                ipt2:{required: true, message: '请选择专业方向', trigger: 'change'},
                ipt3:{validator:ipt3Pass, trigger: 'change'},
                ipt4:{required: true, message: '请输入部套名称', trigger: 'change'}
            },
            snids: [
                {
                    value: 'a',
                    label: 'a 本体'
                },
                {
                    value: 'b',
                    label: 'b 热力'
                },
                {
                    value: 'c',
                    label: 'c 系统'
                },
                {
                    value: 'd',
                    label: 'd 自控'
                },
                {
                    value: 'e',
                    label: 'e 辅机'
                },
                {
                    value: 'f',
                    label: 'f BOP'
                }
            ]
        }
    },
    watch:{
        num(val){
            this.splitNum(val)
        },
        name(val){
            this.form.ipt4 = val
        }
    },
    methods:{
        submit(submit){
            this.$refs.form.validate(valid=>{
                if(valid){
                    this.form.num = '85.' + this.form.ipt1 + '-' + this.form.ipt2 + this.form.ipt3 + 'G01'
                    this.dialogVisible = false
                    this.$emit('returnnum',{
                        num: this.form.num,
                        name: this.form.ipt4
                    })
                }
            })
            // validateField
        },
        createNum(){
            let flag = true
            this.$refs.form.validateField('ipt1', (msg)=>{
                if(msg) flag = false
            })
            this.$refs.form.validateField('ipt2', (msg)=>{
                if(msg) flag = false
            })
            this.$refs.form.validateField('ipt3', (msg)=>{
                if(msg) flag = false
            })
            if(flag) this.form.num = '85.' + this.form.ipt1 + '-' + this.form.ipt2 + this.form.ipt3 + 'G01'
        },
        splitNum(num){
            let reg = new RegExp("^85.[A-Z0-9\\-]{3,11}-[abcdef][A-Za-z0-9.]{0,20}G\\d{2}$")
            if(num && reg.test(num)){
                let str = num.replace('85.','').replace('G01','')
                let index = str.lastIndexOf('-')
                this.form.ipt1 = str.slice(0,index)
                this.form.ipt2 = str[index+1]
                this.form.ipt3 = str.slice(index+2)
                this.form.num = num
            }else {
                this.form.ipt1 = ''
                this.form.ipt2 = ''
                this.form.ipt3 = ''
                this.form.num = ''
            }
        },
        startCreateNum(){
            this.dialogVisible = true
        }
    }
}
</script>