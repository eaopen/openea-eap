<template>
  <Row type="flex" justify="center" align="middle" class="login" @keydown.enter.native="submitRelate">
    <Col :xs="{span:22}" style="width: 368px;">
    <Row class="header">
      <img src="../assets/xboot.png" width="220px" />
      <div class="description">EapBoot企业应用平台</div>
    </Row>
  
    <Row class="login-form">
      <Tabs value="1">
        <TabPane label="绑定XBoot账号" name="1" icon="md-person-add">
          <Form ref="relateLoginForm" :model="form" :rules="rules" class="form">
            <FormItem prop="username">
              <Input v-model="form.username" prefix="ios-contact" size="large" clearable placeholder="请输入用户名" autocomplete="off" />
            </FormItem>
            <FormItem prop="password">
              <Input type="password" v-model="form.password" prefix="ios-lock" size="large" clearable placeholder="请输入密码" autocomplete="off" />
            </FormItem>
            <FormItem>
              <div style="width: 100%;height: 36px;">
                <div id="vaptchaContainer"></div>
                <div v-if="loadingVaptcha" class="vaptcha-init-main">
                  <div class="vaptcha-loading">
                    <img src="@/assets/vaptcha-loading.gif" />
                    <span class="vaptcha-text">Vaptcha启动中...</span>
                  </div>
                </div>
              </div>
            </FormItem>
          </Form>
        </TabPane>
      </Tabs>
  
      <Row>
        <Button type="primary" size="large" :loading="loading" @click="submitRelate" long>
            <span v-if="!loading">立即绑定</span>
            <span v-else>绑定中...</span>
          </Button>
      </Row>
      <Row type="flex" justify="space-between" class="other-thing">
        <a @click="$router.go(-1)" class="back"><Icon type="md-arrow-round-back" style="margin-bottom:3px;"/> 返回</a>
        <router-link to="/regist"><a class="back">还没有账号？立即注册</a></router-link>
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
    </Col>
  </Row>
</template>

<script>
import Cookies from "js-cookie";
import { vaptchaID, relate, userInfo, getJWT } from "@/api/index";
import util from "@/libs/util.js";
var vaptchaObject;
export default {
  data() {
    return {
      loadingVaptcha: true,
      loading: false,
      verified: false,
      form: {
        username: "",
        password: "",
        socialType: null,
        id: "",
        token: ""
      },
      rules: {
        username: [
          {
            required: true,
            message: "账号不能为空",
            trigger: "blur"
          }
        ],
        password: [
          {
            required: true,
            message: "密码不能为空",
            trigger: "blur"
          }
        ]
      }
    };
  },
  methods: {
    submitRelate() {
      this.$refs.relateLoginForm.validate(valid => {
        if (valid) {
          if (!this.verified) {
            this.$Message.error("您还没有完成验证");
            return;
          }
          this.loading = true;
          relate(this.form).then(res => {
            if (res.success == true) {
              // 获取JWT
              getJWT({ JWTKey: res.result }).then(res => {
                if (res.success) {
                  this.setStore("accessToken", res.result);
                  this.$Message.success("绑定成功");
                  // 获取用户信息
                  userInfo().then(res => {
                    if (res.success == true) {
                      // 避免超过大小限制
                      delete res.result.permissions;
                      let roles = [];
                      res.result.roles.forEach(e => {
                        roles.push(e.name);
                      });
                      this.setStore("roles", roles);
                      // 保存7天
                      Cookies.set("userInfo", JSON.stringify(res.result), {
                        expires: 7
                      });
                      this.setStore("userInfo", res.result);
                      this.$store.commit("setAvatarPath", res.result.avatar);
                      // 加载菜单
                      util.initRouter(this);
                      this.$router.push({
                        name: "home_index"
                      });
                    } else {
                      this.loading = false;
                    }
                  });
                } else {
                  this.loading = false;
                }
              });
            } else {
              this.loading = false;
              vaptchaObject.reset();
              this.verified = false;
            }
          });
        }
      });
    },
    initVaptcha() {
      let that = this;
      vaptcha({
        //配置参数
        vid: vaptchaID, // 验证单元id
        type: "click", // 展现类型 点击式
        container: "#vaptchaContainer"
      }).then(function(vaptchaObj) {
        vaptchaObject = vaptchaObj;
        vaptchaObj.render(); // 加载验证按钮
        that.loadingVaptcha = false;
        vaptchaObj.listen("pass", function() {
          // 验证成功
          that.verified = true;
          that.form.token = vaptchaObj.getToken();
        });
      });
    }
  },
  mounted() {
    let q = this.$route.query;
    this.form.socialType = Number(q.socialType);
    this.form.id = q.id;
    this.initVaptcha();
  }
};
</script>

<style lang="less">
@import "./relate.less";
</style>
