<template>
  <Row type="flex" justify="center" align="middle" class="reset-wrap" @keydown.enter.native="submitReset">
    <Col :xs="{span:22}" style="width: 368px;">
    <Row class="header">
      <img src="../assets/xboot.png" width="220px" />
      <div class="description">EapBoot企业应用平台</div>
    </Row>
  
    <Row class="reset">
      <Tabs value="1">
        <TabPane :label="label" name="1" :icon="icon">
        </TabPane>
      </Tabs>
      <Form ref="resetForm" :model="form" :rules="rules" class="form">
        <div v-if="type=='0'">
          <FormItem prop="mobile" :error="errorMobile">
            <Input v-model="form.mobile" size="large" clearable placeholder="请输入注册或绑定手机号"></Input>
          </FormItem>
          <FormItem prop="code">
            <Row type="flex" justify="space-between">
              <Input v-model="form.code" size="large" clearable placeholder="请输入短信验证码" :maxlength="10" class="input-verify" />
              <Button size="large" :loading="sending" @click="showVaptcha" v-if="!sended" class="send-verify">
                        <span v-if="!sending">获取验证码</span>
                        <span v-else>发送中</span>
                      </Button>
              <Button size="large" disabled v-if="sended" class="count-verify">{{countButton}}</Button>
            </Row>
          </FormItem>
        </div>
        <div v-else>
          <FormItem prop="email" :error="errorEmail">
            <Input v-model="form.email" size="large" clearable placeholder="请输入绑定邮箱"></Input>
          </FormItem>
          <FormItem prop="code">
            <Row type="flex" justify="space-between">
              <Input v-model="form.code" size="large" clearable placeholder="请输入您收到的邮件中的验证码" :maxlength="10" class="input-verify" />
              <Button size="large" :loading="sending" @click="showVaptcha" v-if="!sended" class="send-verify">
                        <span v-if="!sending">发送验证码</span>
                        <span v-else>发送中</span>
                      </Button>
              <Button size="large" disabled v-if="sended" class="count-verify">{{countButton}}</Button>
            </Row>
          </FormItem>
        </div>
        <FormItem prop="password">
          <Poptip trigger="focus" placement="right" width="250">
            <Input type="password" :maxlength="20" v-model="form.password" @on-change="strengthChange" size="large" clearable placeholder="请输入密码，长度为6-20个字符" />
            <div v-bind:class="tipStyle" slot="content">
              <span class="words">强度 : {{strength}}</span>
              <Slider v-model="strengthValue" :step="33" style="width:95%"></Slider>
              请至少输入 6 个字符。请不要使<br>用容易被猜到的密码。
            </div>
          </Poptip>
        </FormItem>
        <FormItem prop="confirmPass">
          <Input type="password" :maxlength="20" v-model="form.confirmPass" size="large" clearable placeholder="请再次输入确认密码" />
        </FormItem>
      </Form>
  
      <Button style="margin-bottom:3vh" type="primary" size="large" :loading="loading" @click="submitReset" long>重置密码</Button>
      <Row type="flex">
        <a @click="$router.go(-1)" class="back">
          <Icon type="md-arrow-round-back" style="margin-bottom:3px;" /> 返回</a>
      </Row>
    </Row>
  
    <Row class="foot">
      <Row type="flex" justify="space-around" class="help">
        <a class="item" href="https://github.com/Exrick/x-boot" target="_blank">帮助</a>
        <a class="item" href="https://github.com/Exrick/x-boot" target="_blank">隐私</a>
        <a class="item" href="https://github.com/Exrick/x-boot" target="_blank">条款</a>
      </Row>
      <Row type="flex" justify="center" class="copyright">
        Copyright © 2018 - Present <a href="http://exrick.cn" target="_blank" style="margin:0 5px;">Exrick</a> 版权所有
      </Row>
    </Row>
    <div id="vaptchaContainer"></div>
    </Col>
  </Row>
</template>

<script>
import {
  vaptchaID,
  sendResetSms,
  resetByMobile,
  sendResetEmail,
  resetByEmail
} from "@/api/index";
import Cookies from "js-cookie";
var vaptchaObject;
export default {
  data() {
    const validatePassword = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error("密码长度不得小于6位"));
      } else {
        callback();
      }
    };
    const validateConfirmPass = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error("密码长度不得小于6位"));
      } else if (value !== this.form.password) {
        callback(new Error("两次输入的密码不一致"));
      } else {
        callback();
      }
    };
    const validateMobile = (rule, value, callback) => {
      var reg = /^[1][3,4,5,6,7,8][0-9]{9}$/;
      if (!reg.test(value)) {
        callback(new Error("手机号格式错误"));
      } else {
        callback();
      }
    };
    return {
      label: "使用手机号重置密码",
      icon: "md-phone-portrait",
      loading: false,
      type: "0",
      form: {
        mobile: "",
        email: "",
        token: "",
        password: "",
        passStrength: ""
      },
      sended: false,
      sending: false,
      count: 60,
      countButton: "60 s",
      tipStyle: "password-tip-none",
      strength: "无",
      select: "86",
      strengthValue: 0,
      errorMobile: "",
      errorEmail: "",
      rules: {
        mobile: [
          {
            required: true,
            message: "手机号不能为空",
            trigger: "blur"
          },
          {
            validator: validateMobile,
            trigger: "blur"
          }
        ],
        email: [
          {
            required: true,
            message: "请输入邮箱地址",
            trigger: "blur"
          },
          {
            type: "email",
            message: "邮箱格式不正确",
            trigger: "blur"
          }
        ],
        password: [
          {
            required: true,
            message: "密码不能为空",
            trigger: "blur"
          },
          {
            validator: validatePassword,
            trigger: "blur"
          }
        ],
        code: [
          {
            required: true,
            message: "验证码不能为空",
            trigger: "blur"
          }
        ],
        confirmPass: [
          {
            required: true,
            message: "确认密码不能为空",
            trigger: "blur"
          },
          {
            validator: validateConfirmPass,
            trigger: "blur"
          }
        ]
      }
    };
  },
  methods: {
    initVaptcha() {
      let that = this;
      vaptcha({
        //配置参数
        vid: vaptchaID, // 验证单元id
        type: "invisible" // 展现类型 隐藏式
      }).then(function(vaptchaObj) {
        vaptchaObject = vaptchaObj;
        vaptchaObj.listen("pass", function() {
          that.form.token = vaptchaObj.getToken();
          // 发送验证码
          if (that.type == "0") {
            that.sendSmsVerifyCode();
          } else if (that.type == "1") {
            that.sendEmailVerifyCode();
          }
        });
      });
    },
    checkStrengthValue(v) {
      // 评级制判断密码强度 最高5
      let grade = 0;
      if (/\d/.test(v)) {
        grade++; //数字
      }
      if (/[a-z]/.test(v)) {
        grade++; //小写
      }
      if (/[A-Z]/.test(v)) {
        grade++; //大写
      }
      if (/\W/.test(v)) {
        grade++; //特殊字符
      }
      if (v.length >= 10) {
        grade++;
      }
      return grade;
    },
    strengthChange() {
      if (!this.form.password) {
        this.tipStyle = "password-tip-none";
        this.strength = "无";
        this.strengthValue = 0;
        return;
      }
      let grade = this.checkStrengthValue(this.form.password);
      if (grade <= 1) {
        this.tipStyle = "password-tip-weak";
        this.strength = "弱";
        this.strengthValue = 33;
      } else if (grade >= 2 && grade <= 4) {
        this.tipStyle = "password-tip-middle";
        this.strength = "中";
        this.strengthValue = 66;
      } else {
        this.tipStyle = "password-tip-strong";
        this.strength = "强";
        this.strengthValue = 100;
      }
    },
    showVaptcha() {
      if (this.type == "0") {
        var reg = /^[1][3,4,5,6,7,8][0-9]{9}$/;
        if (!reg.test(this.form.mobile)) {
          this.errorMobile = "手机号格式错误";
          return;
        } else {
          this.errorMobile = "";
        }
      } else if (this.type == "1") {
        var reg = /^[A-Za-z\d]+([-_.][A-Za-z\d]+)*@([A-Za-z\d]+[-.])+[A-Za-z\d]{2,4}$/;
        if (!reg.test(this.form.email)) {
          this.errorEmail = "邮箱格式错误";
          return;
        } else {
          this.errorEmail = "";
        }
      }
      vaptchaObject.validate(); // 若没验证验证码 开始验证
    },
    sendSmsVerifyCode() {
      this.sending = true;
      sendResetSms(this.form).then(res => {
        this.sending = false;
        vaptchaObject.reset();
        if (res.success) {
          this.$Message.success("发送短信验证码成功");
          this.sended = true;
          this.countDown();
        }
      });
    },
    sendEmailVerifyCode() {
      this.sending = true;
      sendResetEmail(this.form).then(res => {
        this.sending = false;
        vaptchaObject.reset();
        if (res.success) {
          this.$Message.success("发送邮件验证码成功，请注意查收");
          this.sended = true;
          this.countDown();
        }
      });
    },
    countDown() {
      let that = this;
      if (this.count == 0) {
        this.sended = false;
        this.count = 60;
        return;
      } else {
        this.countButton = this.count + " s";
        this.count--;
      }
      setTimeout(function() {
        that.countDown();
      }, 1000);
    },
    submitReset() {
      this.$refs.resetForm.validate(valid => {
        if (valid) {
          this.form.passStrength = this.strength;
          this.loading = true;
          if (this.type == "0") {
            resetByMobile(this.form).then(res => {
              this.loading = false;
              if (res.success) {
                this.$Message.success("重置密码成功，请牢记您的新密码");
                this.$router.push({
                  name: "login"
                });
              }
            });
          } else if (this.type == "1") {
            resetByEmail(this.form).then(res => {
              this.loading = false;
              if (res.success) {
                this.$Message.success("重置密码成功，请牢记您的新密码");
                this.$router.push({
                  name: "login"
                });
              }
            });
          }
        }
      });
    }
  },
  mounted() {
    let type = this.$route.query.type;
    if (type == "1") {
      this.type = type;
      this.label = "使用邮箱重置密码";
      this.icon = "ios-mail-outline";
    }
    this.initVaptcha();
  }
};
</script>

<style lang="less">
@import "./reset.less";
</style>
