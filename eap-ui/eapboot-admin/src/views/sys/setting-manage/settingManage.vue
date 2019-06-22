<style lang="less">
@import "./settingManage.less";
</style>
<template>
  <div>
    <Card>
      <Tabs v-model="tabName" :animated="false" style="overflow: visible">
        <TabPane label="文件对象存储配置" name="oss">
          <div style="display:flex;position:relative">
          <Form 
            ref="ossForm"
            :model="oss" 
            :label-width="110" 
            label-position="right"
            :rules="ossValidate"
            >
            <FormItem label="服务提供商：" prop="serviceName">
              <Select v-model="oss.serviceName" @on-change="changeOss" placeholder="请选择" style="width: 200px">
                <Option value="LOCAL_OSS">本地服务器</Option>
                <Option value="MINIO_OSS">MinIO</Option>
                <Option value="QINIU_OSS">七牛云</Option>
                <Option value="ALI_OSS">阿里云</Option>
                <Option value="TENCENT_OSS">腾讯云</Option>
              </Select>
            </FormItem>
            <div v-if="oss.serviceName!='LOCAL_OSS'">
              <FormItem label="accessKey：" prop="accessKey">
                <Input type="text" v-model="oss.accessKey" placeholder="请输入accessKey" style="width: 350px"/>
              </FormItem>
              <FormItem label="secretKey：" prop="secretKey">
                <Tooltip placement="right" content="无法编辑时请先点击查看图标">
                  <Input class="input-see" type="text" v-model="oss.secretKey" :readonly="!changedOssSK" @on-click="seeSecret(oss.serviceName)" icon="ios-eye" placeholder="请输入secretKey" style="width: 350px"/>
                </Tooltip>
              </FormItem>
              <FormItem v-if="oss.serviceName=='QINIU_OSS'" label="zone存储区域：" prop="zone">
                <Select v-model="oss.zone" placeholder="请选择存储区域" style="width: 350px">
                  <Option :value="-1">自动判断</Option>
                  <Option :value="0">华东</Option>
                  <Option :value="1">华北</Option>
                  <Option :value="2">华南</Option>
                  <Option :value="3">北美</Option>
                  <Option :value="4">东南亚</Option>
                </Select>
              </FormItem>
              <FormItem label="bucket空间：" prop="bucket">
                <Input type="text" v-model="oss.bucket" placeholder="请输入bucket空间名" style="width: 350px"/>
              </FormItem>
              <FormItem v-if="oss.serviceName=='TENCENT_OSS'" label="所属地域：" prop="bucketRegion">
                <Select v-model="oss.bucketRegion" placeholder="请选择或输入搜索" style="width: 350px" filterable>
                  <Option v-for="(item, i) in dictBucketRegions" :key="i" :value="item.value">{{item.title}}</Option>
                </Select>
              </FormItem>
              <FormItem label="endpoint域名：" prop="endpoint" :error="errorMsg">
                <Input type="text" v-model="oss.endpoint" :placeholder="endpointPH" style="width: 350px">
                  <Select v-model="oss.http" slot="prepend" style="width: 80px" prop="http">
                    <Option value="http://">http://</Option>
                    <Option value="https://">https://</Option>
                  </Select>
                </Input>
              </FormItem>
            </div>
            <div v-else>
              <FormItem label="存储磁盘路径：" prop="filePath" :error="errorMsg2">
                <Input type="text" v-model="oss.filePath" placeholder="例如：D:/upload" style="width: 350px"/>
              </FormItem>
              <FormItem label="文件预览接口：" prop="endpoint" :error="errorMsg">
                <Input type="text" v-model="oss.endpoint" placeholder="请输入文件预览接口" style="width: 350px">
                  <Select v-model="oss.http" slot="prepend" style="width: 80px" prop="http">
                    <Option value="http://">http://</Option>
                    <Option value="https://">https://</Option>
                  </Select>
                </Input>
              </FormItem>
            </div>
            <FormItem>
              <Button type="primary" style="width: 100px;margin-right:5px" :loading="saveLoading" @click="saveEditOss">保存并启用</Button>
            </FormItem>
           </Form>
           <Spin fix v-if="loading"></Spin>
          </div>
        </TabPane>
        <TabPane label="短信配置" name="sms">
          <div style="display:flex;position:relative">
            <Form 
              ref="smsForm"
              :model="sms" 
              :label-width="110" 
              label-position="right"
              :rules="smsValidate"
              > 
              <FormItem label="服务提供商：" prop="serviceName">
                <Select v-model="sms.serviceName" placeholder="请选择" style="width: 200px">
                  <Option value="ALI_SMS">阿里云</Option>
                </Select>
              </FormItem>
              <FormItem label="accessKey：" prop="accessKey">
                <Input type="text" v-model="sms.accessKey" placeholder="请输入" style="width: 350px"/>
              </FormItem>
              <FormItem label="secretKey：" prop="secretKey">
                <Tooltip placement="right" content="无法编辑时请先点击查看图标">
                    <Input class="input-see" type="text" v-model="sms.secretKey" :readonly="!changedSmsSK" @on-click="seeSecret(sms.serviceName)" icon="ios-eye" placeholder="请输入" style="width: 350px"/>
                </Tooltip>
              </FormItem>
              <FormItem label="短信签名：" prop="signName">
                <Input type="text" v-model="sms.signName" placeholder="请输入短信签名，例如XBoot" style="width: 350px"/>
              </FormItem>
              <FormItem label="使用场景：" prop="type">
                <Select v-model="sms.type" @on-change="changeSmsType" placeholder="请选择" style="width: 350px">
                  <Option value="-1">通用验证码</Option>
                  <Option value="0">注册</Option>
                  <Option value="1">登录</Option>
                  <Option value="2">修改绑定手机号</Option>
                  <Option value="3">修改密码</Option>
                  <Option value="4">重置密码</Option>
                  <Option value="5">工作流消息</Option>
                </Select>
              </FormItem>
              <FormItem label="模版CODE：" prop="templateCode">
                <Input type="text" v-model="sms.templateCode" placeholder="请输入场景对应短信模版CODE，例如SMS_123456789" style="width: 350px"/>
              </FormItem>
              <FormItem>
                <Button type="primary" style="width: 100px;margin-right:5px" :loading="saveLoading" @click="saveEditSms">保存更改</Button>
              </FormItem>
            </Form>
            <Spin fix v-if="loading"></Spin>
          </div>
        </TabPane>
        <TabPane label="邮件配置" name="email">
          <div style="display:flex;position:relative">
            <Form 
              ref="emailForm"
              :model="email" 
              :label-width="110" 
              label-position="right"
              :rules="emailValidate"
              > 
              <FormItem label="邮箱服务器：" prop="host">
                <Input type="text" v-model="email.host" placeholder="请输入邮箱服务器Host，例如QQ邮箱为smtp.qq.com" style="width: 350px"/>
              </FormItem>
              <FormItem label="发送邮箱账号：" prop="username">
                <Input type="text" v-model="email.username" placeholder="请输入发送者Email账号" style="width: 350px"/>
              </FormItem>
              <FormItem label="邮箱授权码：" prop="password">
                <Tooltip placement="right" content="无法编辑时请先点击查看图标">
                    <Input class="input-see" type="text" v-model="email.password" :readonly="!changedEmailPass" @on-click="seeSecret('EMAIL_SETTING')" icon="ios-eye" placeholder="请输入邮箱授权码，进入邮箱-设置中可找到" style="width: 350px"/>
                </Tooltip>
              </FormItem>
              <FormItem>
                <Button type="primary" style="width: 100px;margin-right:5px" :loading="saveLoading" @click="saveEditEmail">保存更改</Button>
              </FormItem>
            </Form>
            </Form>
            <Spin fix v-if="loading"></Spin>
          </div>
        </TabPane>
        <TabPane label="Vaptcha验证码" name="vaptcha">
          <div style="display:flex;position:relative">
            <Form 
              ref="vaptchaForm"
              :model="vaptcha" 
              :label-width="110" 
              label-position="right"
              :rules="vaptchaValidate"
              > 
              <FormItem label="VID：" prop="vid">
                <Input type="text" v-model="vaptcha.vid" placeholder="请输入验证单元VID" style="width: 350px"/>
              </FormItem>
              <FormItem label="secretKey：" prop="secretKey">
                <Tooltip placement="right" content="无法编辑时请先点击查看图标">
                    <Input class="input-see" type="text" v-model="vaptcha.secretKey" :readonly="!changedVaptchaSK" @on-click="seeSecret('VAPTCHA_SETTING')" icon="ios-eye" placeholder="请输入验证单元Key" style="width: 350px"/>
                </Tooltip>
              </FormItem>
              <FormItem>
                <Button type="primary" style="width: 100px;margin-right:5px" :loading="saveLoading" @click="saveEditVaptcha">保存更改</Button>
              </FormItem>
            </Form>
            <Spin fix v-if="loading"></Spin>
          </div>
        </TabPane>
        <TabPane label="其他配置" name="other">
          <div style="display:flex;">
            <Form 
              ref="otherForm"
              :model="other" 
              :label-width="110" 
              label-position="right"
              :rules="otherValidate"
              > 
              <FormItem label="应用部署域名：" prop="domain">
                <Tooltip placement="right" content="拼接部分资源访问链接使用">
                  <Input type="text" v-model="other.domain" placeholder="请输入应用部署域名前缀，如http://xboot.exrick.cn" style="width: 350px"/>
                </Tooltip>
              </FormItem>
              <FormItem label="IP黑名单：" prop="blacklist">
                <Input type="textarea" v-model="other.blacklist" :rows="4" placeholder="多个以回车分隔" style="width: 350px"/>
              </FormItem>
              <FormItem>
                <Button type="primary" style="width: 100px;margin-right:5px" :loading="saveLoading" @click="saveEditOther">保存更改</Button>
              </FormItem>
            </Form>
            <Spin fix v-if="loading"></Spin>
          </div>
        </TabPane>
      </Tabs>   
    </Card>
  </div>
</template>

<script>
import {
  seeSecretSet,
  checkOssSet,
  getOssSet,
  editOssSet,
  getSmsSet,
  editSmsSet,
  getSmsTemplateCode,
  getEmailSet,
  editEmailSet,
  getVaptchaSet,
  editVaptchaSet,
  getOtherSet,
  editOtherSet,
  getDictDataByType
} from "@/api/index";
export default {
  name: "setting-manage",
  data() {
    return {
      tabName: "oss",
      loading: false, // 表单加载状态
      saveLoading: false,
      oss: {
        serviceName: "",
        accessKey: "",
        secretKey: "",
        endpoint: "",
        bucket: ""
      },
      changedOssSK: false, // 是否修改oss的secrectKey
      sms: {
        serviceName: "ALI_SMS",
        accessKey: "",
        secretKey: "",
        signName: "",
        type: "",
        templateCode: ""
      },
      changedSmsSK: false, // 是否修改短信的secrectKey
      email: {
        host: "",
        username: "",
        password: ""
      },
      changedEmailPass: false, // 是否修改邮件密码
      errorMsg: "",
      errorMsg2: "",
      vaptcha: {
        vID: "",
        secretKey: ""
      },
      changedVaptchaSK: false, // 是否修改Vaptcha的secrectKey
      other: {
        domain: "",
        blacklist: ""
      },
      ossValidate: {
        // 表单验证规则
        serviceName: [{ required: true, message: "请选择", trigger: "blur" }],
        accessKey: [{ required: true, message: "不能为空", trigger: "blur" }],
        secretKey: [{ required: true, message: "不能为空", trigger: "blur" }],
        endpoint: [{ required: true, message: "不能为空", trigger: "blur" }],
        bucket: [{ required: true, message: "不能为空", trigger: "blur" }]
      },
      smsValidate: {
        // 表单验证规则
        serviceName: [{ required: true, message: "请选择", trigger: "blur" }],
        accessKey: [{ required: true, message: "不能为空", trigger: "blur" }],
        secretKey: [{ required: true, message: "不能为空", trigger: "blur" }],
        signName: [{ required: true, message: "不能为空", trigger: "blur" }],
        type: [{ required: true, message: "不能为空", trigger: "blur" }],
        templateCode: [{ required: true, message: "不能为空", trigger: "blur" }]
      },
      emailValidate: {
        // 表单验证规则
        host: [{ required: true, message: "不能为空", trigger: "blur" }],
        username: [{ required: true, message: "不能为空", trigger: "blur" }],
        password: [{ required: true, message: "不能为空", trigger: "blur" }]
      },
      vaptchaValidate: {
        // 表单验证规则
        vID: [{ required: true, message: "不能为空", trigger: "blur" }],
        secretKey: [{ required: true, message: "不能为空", trigger: "blur" }]
      },
      otherValidate: {
        // 表单验证规则
        domain: [{ required: true, message: "不能为空", trigger: "blur" }]
      },
      endpointPH: "请输入空间访问域名",
      dictBucketRegions: [
      ]
    };
  },
  methods: {
    init() {
      this.initOssSet();
      this.initSmsSet();
      this.initEmailSet();
      this.initVaptchaSet();
      this.initOtherSet();
      this.getDictBucketRegions();
    },
    getDictBucketRegions() {
      getDictDataByType("tencent_bucket_region").then(res => {
        if (res.success) {
          this.dictBucketRegions = res.result;
        }
      });
    },
    initOssSet() {
      this.loading = true;
      checkOssSet().then(res => {
        if (res.success && res.result) {
          let oss = res.result;
          getOssSet(oss).then(res => {
            this.loading = false;
            if (res.result) {
              // 转换null为""
              for (let attr in res.result) {
                if (res.result[attr] == null) {
                  res.result[attr] = "";
                }
              }
              this.oss = res.result;
            } else {
              this.changedOssSK = true;
            }
          });
        } else {
          this.loading = false;
          this.changedOssSK = true;
        }
      });
    },
    changeOss(v) {
      if (v == "ALI_OSS") {
        this.endpointPH = "请输入EndPoint(地域节点) 非Bucket域名";
      } else if (v == "TENCENT_OSS") {
        this.endpointPH =
          "请输入完整请求域名";
      } else {
        this.endpointPH = "请输入空间访问域名";
      }
      getOssSet(v).then(res => {
        if (res.result) {
          // 转换null为""
          for (let attr in res.result) {
            if (res.result[attr] == null) {
              res.result[attr] = "";
            }
          }
          this.oss = res.result;
          this.changedOssSK = false;
        } else {
          this.oss = { serviceName: v };
          if (this.oss.serviceName == "LOCAL_OSS") {
            this.oss.http = "http://";
            this.oss.endpoint = "127.0.0.1:9999/xboot/file/view";
          }
          this.changedOssSK = true;
        }
      });
    },
    initSmsSet() {
      this.loading = true;
      getSmsSet(this.sms.serviceName).then(res => {
        this.loading = false;
        if (res.result) {
          // 转换null为""
          for (let attr in res.result) {
            if (res.result[attr] == null) {
              res.result[attr] = "";
            }
          }
          this.sms = res.result;
          this.sms.type = "-1";
          this.changeSmsType(this.sms.type);
        } else {
          this.changedSmsSK = true;
        }
      });
    },
    changeSmsType(v) {
      getSmsTemplateCode(v).then(res => {
        if (res.success) {
          this.sms.templateCode = res.result;
        }
      });
    },
    initEmailSet() {
      this.loading = true;
      getEmailSet().then(res => {
        this.loading = false;
        if (res.result) {
          // 转换null为""
          for (let attr in res.result) {
            if (res.result[attr] == null) {
              res.result[attr] = "";
            }
          }
          this.email = res.result;
        } else {
          this.changedEmailPass = true;
        }
      });
    },
    initVaptchaSet() {
      this.loading = true;
      getVaptchaSet().then(res => {
        this.loading = false;
        if (res.result) {
          // 转换null为""
          for (let attr in res.result) {
            if (res.result[attr] == null) {
              res.result[attr] = "";
            }
          }
          this.vaptcha = res.result;
        } else {
          this.changedVaptchaSK = true;
        }
      });
    },
    initOtherSet() {
      this.loading = true;
      getOtherSet().then(res => {
        this.loading = false;
        if (res.result) {
          // 转换null为""
          for (let attr in res.result) {
            if (res.result[attr] == null) {
              res.result[attr] = "";
            }
          }
          this.other = res.result;
        }
      });
    },
    seeSecret(name) {
      if (!name) {
        return;
      }
      seeSecretSet(name).then(res => {
        if (res.success) {
          if (this.tabName == "oss") {
            this.oss.secretKey = res.result;
            this.changedOssSK = true;
          } else if (this.tabName == "sms") {
            this.sms.secretKey = res.result;
            this.changedSmsSK = true;
          } else if (this.tabName == "email") {
            this.email.password = res.result;
            this.changedEmailPass = true;
          } else if (this.tabName == "vaptcha") {
            this.vaptcha.secretKey = res.result;
            this.changedVaptchaSK = true;
          }
        }
      });
    },
    saveEditOss() {
      if (this.oss.serviceName == "LOCAL_OSS") {
        if (!this.oss.http) {
          this.errorMsg = "请选择http协议";
          return;
        } else {
          this.errorMsg = "";
        }
        if (!this.oss.endpoint) {
          this.errorMsg = "请输入文件预览接口";
          return;
        } else {
          this.errorMsg = "";
        }
        if (!this.oss.filePath) {
          this.errorMsg2 = "请输入存储磁盘路径";
          return;
        } else {
          this.errorMsg2 = "";
        }
        editOssSet(this.oss).then(res => {
          this.saveLoading = false;
          if (res.success) {
            this.$Message.success("保存成功");
          }
        });
      } else {
        this.$refs.ossForm.validate(valid => {
          if (valid) {
            if (!this.oss.http) {
              this.errorMsg = "请选择http协议";
              return;
            } else {
              this.errorMsg = "";
            }
            this.saveLoading = true;
            this.oss.changed = this.changedOssSK;
            editOssSet(this.oss).then(res => {
              this.saveLoading = false;
              if (res.success) {
                this.$Message.success("保存成功");
              }
            });
          }
        });
      }
    },
    saveEditSms() {
      this.$refs.smsForm.validate(valid => {
        if (valid) {
          this.saveLoading = true;
          this.sms.changed = this.changedSmsSK;
          editSmsSet(this.sms).then(res => {
            this.saveLoading = false;
            if (res.success) {
              this.$Message.success("保存成功");
            }
          });
        }
      });
    },
    saveEditEmail() {
      this.$refs.emailForm.validate(valid => {
        if (valid) {
          this.saveLoading = true;
          this.email.changed = this.changedEmailPass;
          editEmailSet(this.email).then(res => {
            this.saveLoading = false;
            if (res.success) {
              this.$Message.success("保存成功");
            }
          });
        }
      });
    },
    saveEditVaptcha() {
      this.$refs.vaptchaForm.validate(valid => {
        if (valid) {
          this.saveLoading = true;
          this.vaptcha.changed = this.changedVaptchaSK;
          editVaptchaSet(this.vaptcha).then(res => {
            this.saveLoading = false;
            if (res.success) {
              this.$Message.success("保存成功");
            }
          });
        }
      });
    },
    saveEditOther() {
      this.$refs.otherForm.validate(valid => {
        if (valid) {
          this.saveLoading = true;
          editOtherSet(this.other).then(res => {
            this.saveLoading = false;
            if (res.success) {
              this.$Message.success("保存成功");
            }
          });
        }
      });
    }
  },
  mounted() {
    let name = this.$route.query.name;
    if (name) {
      this.tabName = name;
    }
    this.init();
  }
};
</script>