<template>
    <el-dialog title="油泵部套" width="600px" :visible.sync="dialogVisible" :close-on-click-modal="false">
        <el-form :model="form" ref="form13" label-width="100px" :rules="rules">
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
                    <el-form-item label="油泵类型/代号" prop="ipt1" label-width="140px">
                        <el-select v-model="form.ipt1" size="mini" filter>
                            <el-option :label="item.label" :value="item.value" v-for="item in types"
                                :key="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="油泵品牌" prop="ipt2">
                        <el-select v-model="form.ipt2" size="mini" filter>
                            <el-option :label="item.label" :value="item.value" v-for="item in brands"
                                :key="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="电动机品牌" prop="ipt3" label-width="120px">
                        <el-select v-model="form.ipt3" size="mini" filter>
                            <el-option :label="item.label" :value="item.value" v-for="item in motorBrands"
                                :key="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="油泵流量" prop="ipt4">
                        <el-input v-model="form.ipt4" placeholder="需填写3位数字" autocomplete="off" size="mini"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="流水号" prop="ipt5">
                        <el-input v-model="form.ipt5" placeholder="自01" autocomplete="off" size="mini"></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="部套名称" prop="ipt6">
                        <el-input v-model="form.ipt6" placeholder="部套中文名称" autocomplete="off" size="mini"
                            readonly></el-input>
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
    name: 'oilPumpPart',
    props: ['num', 'name'],
    data() {
        var ipt4Pass = (rule, value, callback) => {
            if (!value) {
                callback(new Error('请输入流量'))
            } else if (value.length != 3) {
                return callback(new Error('请输入3位数，不满三位在开头补0至3位'));
            } else if (isNaN(value)) {
                callback(new Error('流量必须为纯数字'))
            }else if(value.toString().indexOf('.')!= -1){
                callback(new Error('流量不能带有小数点'))
            } else {
                callback()
            }
        }
        var ipt5Pass = (rule, value, callback) => {
            if (!value) {
                callback(new Error('请输入流水号'))
            } else if (isNaN(value)) {
                callback(new Error('流水号必须为纯数字'))
            }else if(value.toString().indexOf('.')!= -1){
                callback(new Error('流水号不能带有小数点'))
            } else {
                callback()
            }
        }
        return {
            dialogVisible: false,
            form: {
                ipt1: '',
                ipt2: '',
                ipt3: '',
                ipt4: '',
                ipt5: '',
                ipt6: '',
                num: '',
                exam: 'SH71ZX.198.01G01'
            },
            rules: {
                ipt1: { required: true, message: '请选择油泵类型/代号', trigger: 'change' },
                ipt2: { required: true, message: '请选择油泵品牌', trigger: 'change' },
                ipt3: { required: true, message: '请选择电动机品牌', trigger: 'change' },
                ipt4: { required: true, validator: ipt4Pass, trigger: ['change', 'blur'] },
                ipt5: { required: true, validator: ipt5Pass, trigger: ['change', 'blur'] },
            },
            types: [
                {
                    value: 'SH70',
                    label: '交流润滑油泵/SH70'
                },
                {
                    value: 'SH71',
                    label: '直流润滑油泵/SH71'
                }
            ],
            brands: [
                {
                    value: 'Z',
                    label: '涿州水泵-Z'
                },
                {
                    value: 'Q',
                    label: '权国泵业-Q'
                },
                {
                    value: 'K',
                    label: 'KSB-K'
                },
                {
                    value: 'A',
                    label: 'ALLWEILER-A'
                },
                {
                    value: 'D',
                    label: '大晃-D'
                },
                {
                    value: '9',
                    label: '其他-9'
                },
            ],
            motorBrands: [
                {
                    value: 'J',
                    label: '佳木斯-J'
                },
                {
                    value: 'X',
                    label: '西玛-X'
                },
                {
                    value: 'W',
                    label: '皖南-W'
                },
                {
                    value: 'B',
                    label: '国产ABB-B'
                },
                {
                    value: 'M',
                    label: '国产西门子-M'
                },
                {
                    value: 'S',
                    label: 'Siemens-S'
                },
                {
                    value: 'K',
                    label: 'Winkelmann-K'
                },
                {
                    value: 'A',
                    label: 'ABB-A'
                },
                {
                    value: 'N',
                    label: '上海南洋-N'
                },
                {
                    value: 'H',
                    label: '河南南阳-H'
                },
                {
                    value: '9',
                    label: '其他-9'
                },
            ],
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
    watch: {
        num(val) {
            setTimeout(() => {
                this.splitNum(val)
                this.form.num = this.num
                this.form.ipt6 = this.name
            }, 200)
        },
        'form.ipt1': 'createNum',
        'form.ipt2': 'createNum',
        'form.ipt3': 'createNum',
        'form.ipt4': 'createNum',
        'form.ipt5': 'createNum',
    },
    methods: {
        submit() {
            this.$refs.form13.validate(valid => {
                if (valid) {
                  const that = this
                    let num = this.form.num = this.form.ipt1 + this.form.ipt2 + this.form.ipt3 + '.' + this.form.ipt4 + '.' + this.form.ipt5 + 'G01'
                  Vue.baseService.post(
                    Vue.__ctx + "/Ashxs/PLAN/PLAN_nlist.ashx", "package=nlistcomponent&action=duplicateChecking&componentnum=" +num).then(function(data){
                    if (data && data!='""'){
                      $.Dialog.error(decodeUnicode(data))
                    }else{
                      that.dialogVisible = false
                      that.$emit('returnnum', {
                        num: this.form.num,
                        name: this.form.ipt6
                      })
                    }
                  }
                  );
                }
            })
            // validateField
        },
        createName() {
            if (this.form.ipt1 == 'SH70') {
                this.form.ipt6 = '主油泵'
            } else if (this.form.ipt1 == 'SH71') {
                this.form.ipt6 = '危急油泵'
            } else {
                this.form.ipt6 = ''
            }
        },
        createNum() {
            let flag = true
            this.createName()
            if (this.$refs.form13) {
                this.$refs.form13.validateField('ipt1', (msg) => {
                    if (msg) flag = false
                })
                this.$refs.form13.validateField('ipt2', (msg) => {
                    if (msg) flag = false
                })
                this.$refs.form13.validateField('ipt3', (msg) => {
                    if (msg) flag = false
                })
                this.$refs.form13.validateField('ipt4', (msg) => {
                    if (msg) flag = false
                })
                this.$refs.form13.validateField('ipt5', (msg) => {
                    if (msg) flag = false
                })
                if (flag) {
                    this.form.num = this.form.ipt1 + this.form.ipt2 + this.form.ipt3 + '.' + this.form.ipt4 + '.' + this.form.ipt5 + 'G01'
                } else {
                    this.form.num = ""
                }
            }
        },
        splitNum(num) {
            let reg = new RegExp(/^SH7[0|1][Z|Q|K|A|D|9][J|X|W|B|M|S|K|A|N|9]\.[0-9]{3}\.[0-9]{1,5}G01$/)
            if (num && reg.test(num)) {
                console.log('符合条件')
                let str = num.replace('G01', '')
                let arr = str.split('.')
                this.form.ipt5 = arr[2]
                this.form.ipt4 = arr[1]
                this.form.ipt1 = arr[0].slice(0, 4)
                this.form.ipt2 = arr[0].slice(4, 5)
                this.form.ipt3 = arr[0].slice(5, 6)
            } else {
                console.log('不符合条件')
                this.form.ipt1 = ''
                this.form.ipt2 = ''
                this.form.ipt3 = ''
                this.form.ipt4 = ''
                this.form.ipt5 = ''
                this.form.ipt6 = ''
                this.form.num = ''
            }
        },
        startCreateNum() {
            this.dialogVisible = true
        }
    }
}
</script>