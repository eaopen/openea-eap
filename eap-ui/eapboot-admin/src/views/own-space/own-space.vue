<style lang="less" scoped>
@import "./own-space.less";
</style>

<template>
  <div class="own-space">
    <Card class="own-space-new">
      <div class="own-wrap">
        <div style="width:240px">
          <Menu active-name="基本信息" theme="light" @on-select="changeMenu">
            <MenuItem name="基本信息">基本信息</MenuItem>
            <MenuItem name="安全设置">安全设置</MenuItem>
            <MenuItem name="第三方账号绑定">第三方账号绑定</MenuItem>
            <MenuItem name="消息通知">消息通知</MenuItem>
          </Menu>
        </div>
        <div style="padding: 8px 40px;width:100%">
          <div class="title">{{currMenu}}</div>
          <div>
            <div v-if="currMenu=='基本信息'">
              <Form ref="userForm" :model="userForm" :label-width="90" label-position="left">
                <FormItem label="用户头像：">
                  <div class="upload-list" v-for="item in uploadList" :key="item.url">
                    <template v-if="item.status == 'finished'">
                      <img :src="item.url">
                      <div class="upload-list-cover">
                        <Icon type="ios-eye-outline" @click.native="handleView(item.url)"></Icon>
                        <Icon type="ios-trash-outline" @click.native="handleRemove(item)"></Icon>
                      </div>
                    </template>
                    <template v-else>
                      <Progress v-if="item.showProgress" :percent="item.percentage" hide-info></Progress>
                    </template>
                  </div>
                  <Upload
                    ref="upload"
                    :show-upload-list="false"
                    :default-file-list="defaultList"
                    :on-success="handleSuccess"
                    :on-error="handleError"
                    :format="['jpg','jpeg','png','gif']"
                    :max-size="5120"
                    :on-format-error="handleFormatError"
                    :on-exceeded-size="handleMaxSize"
                    :before-upload="handleBeforeUpload"
                    type="drag"
                    :action="uploadFileUrl"
                    :headers="accessToken"
                    style="display: inline-block;width:58px;"
                  >
                    <div style="width: 58px;height:58px;line-height: 58px;">
                      <Icon type="md-camera" size="20"></Icon>
                    </div>
                  </Upload>
                </FormItem>
                <FormItem label="昵称：" prop="nickName">
                  <Input v-model="userForm.nickName" style="width: 250px"/>
                </FormItem>
                <FormItem label="性别：">
                  <RadioGroup v-model="userForm.sex">
                    <Radio
                      v-for="(item, i) in dictSex"
                      :key="i"
                      :label="item.value"
                    >{{item.title}}</Radio>
                  </RadioGroup>
                </FormItem>
                <FormItem label="个人简介：" prop="description">
                  <Input
                    v-model="userForm.description"
                    type="textarea"
                    style="width: 250px"
                    :autosize="{minRows: 3,maxRows: 5}"
                    placeholder="个人简介"
                  ></Input>
                </FormItem>
                <FormItem label="国家/地区：">
                  <Select v-model="area" style="width: 250px">
                    <Option :value="86">中国</Option>
                  </Select>
                </FormItem>
                <FormItem label="所在省市：">
                  <al-cascader
                    v-model="userForm.addressArray"
                    @on-change="changeAddress"
                    data-type="code"
                    level="2"
                    style="width:250px"
                  />
                </FormItem>
                <FormItem label="街道地址：" prop="street">
                  <Input v-model="userForm.street" style="width: 250px"/>
                </FormItem>
                <FormItem label="所属部门：">
                  <span>{{ userForm.departmentTitle }}</span>
                </FormItem>
                <FormItem label="用户类型：">
                  <span>{{ userForm.typeTxt }}</span>
                </FormItem>
                <FormItem label="注册时间：">
                  <span>{{ userForm.createTime }}</span>
                </FormItem>
                <FormItem>
                  <Button
                    type="primary"
                    style="width: 100px;margin-right:5px"
                    :loading="saveLoading"
                    @click="saveEdit"
                  >保存</Button>
                </FormItem>
              </Form>
            </div>
            <div v-if="currMenu=='安全设置'" class="safe">
              <div class="item">
                <div>
                  <div class="title">账户密码</div>
                  <div v-if="userForm.passStrength" class="desc">
                    当前密码强度：
                    <span
                      v-if="userForm.passStrength=='弱'"
                      class="red"
                    >{{userForm.passStrength}}</span>
                    <span v-if="userForm.passStrength=='中'" class="middle">{{userForm.passStrength}}</span>
                    <span v-if="userForm.passStrength=='强'" class="green">{{userForm.passStrength}}</span>
                  </div>
                </div>
                <div>
                  <a @click="changePass">修改</a>
                </div>
              </div>
              <div class="item">
                <div>
                  <div class="title">绑定手机</div>
                  <div class="desc">
                    <span v-if="userForm.mobile">已绑定手机：{{userForm.mobile}}</span>
                    <span v-else>未绑定手机号</span>
                  </div>
                </div>
                <div>
                  <a @click="showChangeMobile">修改</a>
                </div>
              </div>
              <div class="item">
                <div>
                  <div class="title">绑定邮箱</div>
                  <div class="desc">
                    <span v-if="userForm.email">已绑定邮箱：{{userForm.email}}</span>
                    <span v-else>未绑定邮箱</span>
                  </div>
                </div>
                <div>
                  <a @click="showChangeEmail">修改</a>
                </div>
              </div>
              <div class="item">
                <div>
                  <div class="title">密保问题</div>
                  <div class="desc">未设置密保问题，密保问题可有效保护账户安全</div>
                </div>
                <div>
                  <a>暂不支持设置</a>
                </div>
              </div>
            </div>
            <div v-if="currMenu=='第三方账号绑定'" class="safe">
              <div class="item">
                <div style="display:flex;align-items:center">
                  <Icon type="logo-github" size="42" color="#181617" style="margin-right: 16px;"/>
                  <div>
                    <div class="title">Github</div>
                    <div class="desc">
                      <span v-if="github.related">已绑定Github账号：{{github.username}}</span>
                      <span v-else>当前未绑定Github账号</span>
                    </div>
                  </div>
                </div>
                <div>
                  <a v-if="!github.related" @click="toRelateGithub()">立即绑定</a>
                  <a v-else @click="unRelateGithub()">解除绑定</a>
                </div>
              </div>
              <div class="item">
                <div style="display:flex;align-items:center">
                  <icon name="brands/qq" scale="2.5" style="margin: 0 16px 0 6px;color:#2eabff"></icon>
                  <div>
                    <div class="title">QQ</div>
                    <div class="desc">
                      <span v-if="qq.related">已绑定QQ账号：{{qq.username}}</span>
                      <span v-else>当前未绑定QQ账号</span>
                    </div>
                  </div>
                </div>
                <div>
                  <a v-if="!qq.related" @click="toRelateQQ()">立即绑定</a>
                  <a v-else @click="unRelateQQ()">解除绑定</a>
                </div>
              </div>
              <div class="item">
                <div style="display:flex;align-items:center">
                  <icon name="brands/weibo" scale="2.5" style="margin: 0 16px 0 2px;color:#e22429"></icon>
                  <div>
                    <div class="title">微博</div>
                    <div class="desc">
                      <span v-if="weibo.related">已绑定微博账号：{{weibo.username}}</span>
                      <span v-else>当前未绑定微博账号</span>
                    </div>
                  </div>
                </div>
                <div>
                  <a v-if="!weibo.related" @click="toRelateWeibo()">立即绑定</a>
                  <a v-else @click="unRelateWeibo()">解除绑定</a>
                </div>
              </div>
              <div class="item">
                <div style="display:flex;align-items:center">
                  <icon name="brands/weixin" scale="2.5" style="margin: 0 16px 0 2px;color:#60c126"></icon>
                  <div>
                    <div class="title">微信</div>
                    <div class="desc">当前未绑定微信账号</div>
                  </div>
                </div>
                <div>
                  <a>暂不支持</a>
                </div>
              </div>
              <Spin fix v-if="jumping">跳转中...</Spin>
            </div>
            <div v-if="currMenu=='消息通知'" class="safe">
              <div class="item">
                <div>
                  <div class="title">系统消息</div>
                  <div class="desc">系统消息将以站内信的形式通知</div>
                </div>
                <div>
                  <i-switch v-model="messageOpen" :true-value="1" :false-value="0">
                    <span slot="open">开</span>
                    <span slot="close">关</span>
                  </i-switch>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Card>

    <!-- 密码验证 -->
    <Modal title="身份验证" v-model="passCheckVisible" fullscreen footer-hide>
      <div class="pass-check" @keydown.enter="checkPassChange">
        <Icon type="md-lock" size="30" style="margin-bottom:10px;"/>
        <div class="title" style="margin-bottom:40px;">密码认证</div>
        <div class="desc">请输入您的密码</div>
        <Input
          autofocus
          v-model="password"
          size="large"
          placeholder="请输入您的密码"
          type="password"
          style="width:300px;margin-bottom:40px;"
        />
        <div style="margin-bottom:60px;">
          <Button size="large" @click="passCheckVisible=false" style="margin-right:20px;">取消</Button>
          <Button
            :loading="checkPassLoading"
            :disabled="!password"
            @click="checkPassChange"
            type="primary"
            size="large"
          >提交</Button>
        </div>
      </div>
    </Modal>

    <Modal
      title="修改手机号"
      v-model="editMobileVisible"
      :closable="false"
      :mask-closable="false"
      :width="500"
    >
      <Form
        ref="mobileEditForm"
        :model="mobileEditForm"
        :label-width="60"
        :rules="mobileEditValidate"
      >
        <FormItem label="手机号" prop="mobile">
          <Input v-model="mobileEditForm.mobile" @on-change="hasChangePhone" placeholder="请输入新手机号"/>
        </FormItem>
        <FormItem label="验证码" prop="code" :error="codeError">
          <Input
            v-model="mobileEditForm.code"
            placeholder="请输入您收到的短信验证码"
            style="width:200px;margin-right:5px;"
          />
          <div style="display:inline-block;position:relative;">
            <Button
              :loading="sendingSms"
              @click="getIdentifyCode"
              :disabled="canGetIdentifyCode"
              v-if="!sended"
              style="width: 100px;"
            >
              <span v-if="!sendingSms">发送验证码</span>
              <span v-else>发送中</span>
            </Button>
            <Button disabled v-if="sended" style="width: 100px;">{{countButton}}</Button>
          </div>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="text" @click="cancelInputCodeBox">取消</Button>
        <Button type="primary" :loading="checkCodeLoading" @click="submitEditMobile">提交</Button>
      </div>
    </Modal>

    <Modal
      title="修改邮箱"
      v-model="editEmailVisible"
      :closable="false"
      :mask-closable="false"
      :width="500"
    >
      <Form ref="emailEditForm" :model="emailEditForm" :label-width="90" :rules="emailEditValidate">
        <FormItem label="新邮箱地址" prop="email">
          <Input v-model="emailEditForm.email" @on-change="hasChangeEmail" placeholder="请输入新邮箱地址"></Input>
        </FormItem>
        <FormItem label="验证码" prop="code" :error="codeError">
          <Input
            v-model="emailEditForm.code"
            placeholder="请输入您收到的邮件中的验证码"
            style="width:200px;margin-right:5px;"
          />
          <div style="display:inline-block;position:relative;">
            <Button
              :loading="sendEmailLoading"
              @click="sendVerifyEmail"
              :disabled="canSendEditEmail"
              v-if="!sendedEmail"
              style="width: 100px;"
            >
              <span v-if="!sendEmailLoading">发送验证邮件</span>
              <span v-else>发送中</span>
            </Button>
            <Button disabled v-if="sendedEmail" style="width: 100px;">{{countButtonEmail}}</Button>
          </div>
        </FormItem>
      </Form>
      <div slot="footer">
        <Button type="text" @click="cancelEditEmail">取消</Button>
        <Button type="primary" :loading="editEmailLoading" @click="submitEditEmail">提交</Button>
      </div>
    </Modal>

    <Modal title="图片预览" v-model="viewImage" draggable>
      <img :src="imgUrl" style="width: 80%;margin: 0 auto;display: block;">
      <div slot="footer">
        <Button @click="viewImage=false">关闭</Button>
      </div>
    </Modal>
  </div>
</template>

<script>
import {
  userInfo,
  userInfoEdit,
  relatedInfo,
  unRelate,
  githubLogin,
  qqLogin,
  weiboLogin,
  sendCodeEmail,
  editEmail,
  sendSms,
  changeMobile,
  unlock,
  uploadFile
} from "@/api/index";
import Cookies from "js-cookie";
export default {
  name: "ownspace_index",
  data() {
    const validePhone = (rule, value, callback) => {
      var re = /^1[0-9]{10}$/;
      if (!re.test(value)) {
        callback(new Error("请输入正确格式的手机号"));
      } else {
        callback();
      }
    };
    return {
      accessToken: {},
      uploadFileUrl: uploadFile,
      area: 86,
      messageOpen: 1,
      userForm: {
        id: "",
        avatar: "",
        username: "",
        sex: "",
        mobile: "",
        email: "",
        status: "",
        type: "",
        typeTxt: "",
        address: "",
        addressArray: []
      },
      mobileEditForm: {
        mobile: "",
        code: ""
      },
      emailEditForm: {
        email: "",
        code: ""
      },
      defaultList: [
        {
          url: ""
        }
      ],
      uploadList: [],
      viewImage: false,
      imgUrl: "",
      codeError: "",
      initPhone: "",
      initEmail: "",
      saveLoading: false,
      sended: false,
      count: 60,
      countButton: "60 s",
      sendedEmail: false,
      countEmail: 60,
      countButtonEmail: "60 s",
      savePassLoading: false,
      canGetIdentifyCode: false, // 是否可点获取验证码
      checkCodeLoading: false,
      checkPassLoading: false,
      sendingSms: false,
      editEmailLoading: false,
      mobileEditValidate: {
        mobile: [
          { required: true, message: "请输入手机号码" },
          { validator: validePhone }
        ]
      },
      emailEditValidate: {
        email: [
          { required: true, message: "请输入邮箱地址" },
          { type: "email", message: "邮箱格式不正确" }
        ]
      },
      editMobileVisible: false, // 显示填写验证码box
      editEmailVisible: false,
      sendEmailLoading: false,
      canSendEditEmail: true,
      passCheckVisible: false, // 密码验证
      password: "",
      changeType: "",
      github: {
        related: false,
        id: "",
        username: ""
      },
      qq: {
        related: false,
        id: "",
        username: ""
      },
      weibo: {
        related: false,
        id: "",
        username: ""
      },
      jumping: false,
      currMenu: "基本信息",
      dictSex: this.$store.state.dict.sex
    };
  },
  methods: {
    init() {
      this.accessToken = {
        accessToken: this.getStore("accessToken")
      };
      let v = JSON.parse(Cookies.get("userInfo"));
      // 转换null为""
      for (let attr in v) {
        if (v[attr] == null) {
          v[attr] = "";
        }
      }
      let str = JSON.stringify(v);
      let userInfo = JSON.parse(str);
      userInfo.addressArray = [];
      this.userForm = userInfo;
      this.initPhone = userInfo.mobile;
      this.mobileEditForm.mobile = userInfo.mobile;
      this.initEmail = userInfo.email;
      this.emailEditForm.email = userInfo.email;
      this.defaultList[0].url = userInfo.avatar;
      if (userInfo.address !== null && userInfo.address !== "") {
        this.userForm.address = userInfo.address;
        this.userForm.addressArray = JSON.parse(userInfo.address);
      }
      if (this.userForm.type == 0) {
        this.userForm.typeTxt = "普通用户";
      } else if (this.userForm.type == 1) {
        this.userForm.typeTxt = "管理员";
      }
      relatedInfo(userInfo.username).then(res => {
        if (res.success) {
          let r = res.result;
          this.github.related = r.github;
          this.github.id = r.githubId;
          this.github.username = r.githubUsername;
          this.qq.related = r.qq;
          this.qq.id = r.qqId;
          this.qq.username = r.qqUsername;
          this.weibo.related = r.weibo;
          this.weibo.id = r.weiboId;
          this.weibo.username = r.weiboUsername;
        } else {
          this.$Message.error("加载绑定第三方账号信息失败");
        }
      });
    },
    changePass() {
      this.$router.push({
        name: "change_pass"
      });
    },
    changeMenu(v) {
      this.currMenu = v;
    },
    toRelateGithub() {
      this.jumping = true;
      githubLogin().then(res => {
        if (res.success) {
          window.location.href = res.result;
        } else {
          this.jumping = false;
        }
      });
    },
    toRelateQQ() {
      this.jumping = true;
      qqLogin().then(res => {
        if (res.success) {
          window.location.href = res.result;
        } else {
          this.jumping = false;
        }
      });
    },
    toRelateWeibo() {
      this.jumping = true;
      weiboLogin().then(res => {
        if (res.success) {
          window.location.href = res.result;
        } else {
          this.jumping = false;
        }
      });
    },
    unRelateGithub() {
      this.$Modal.confirm({
        title: "确认解绑Github账号",
        content: "您确认要解除绑定 " + this.github.username + " ?",
        onOk: () => {
          let params = {
            socialType: 0,
            ids: [this.github.id],
            usernames: this.userForm.username
          };
          unRelate(params).then(res => {
            if (res.success == true) {
              this.$Message.success("操作成功");
              this.github.related = false;
            }
          });
        }
      });
    },
    unRelateQQ() {
      this.$Modal.confirm({
        title: "确认解绑QQ账号",
        content: "您确认要解除绑定 " + this.qq.username + " ?",
        onOk: () => {
          let params = {
            socialType: 1,
            ids: [this.qq.id],
            usernames: this.userForm.username
          };
          unRelate(params).then(res => {
            if (res.success == true) {
              this.$Message.success("操作成功");
              this.qq.related = false;
            }
          });
        }
      });
    },
    unRelateWeibo() {
      this.$Modal.confirm({
        title: "确认解绑微博账号",
        content: "您确认要解除绑定 " + this.weibo.username + " ?",
        onOk: () => {
          let params = {
            socialType: 2,
            ids: [this.weibo.id],
            usernames: this.userForm.username
          };
          unRelate(params).then(res => {
            if (res.success == true) {
              this.$Message.success("操作成功");
              this.weibo.related = false;
            }
          });
        }
      });
    },
    handleView(imgUrl) {
      this.imgUrl = imgUrl;
      this.viewImage = true;
    },
    handleRemove(file) {
      const fileList = this.$refs.upload.fileList;
      this.$refs.upload.fileList.splice(fileList.indexOf(file), 1);
    },
    handleSuccess(res, file) {
      if (res.success == true) {
        file.url = res.result;
        this.userForm.avatar = res.result;
        this.defaultList[0].url = res.result;
        this.$refs.upload.fileList.splice(0, 1);
      } else {
        this.$Message.error(res.message);
      }
    },
    handleError(error, file, fileList) {
      this.$Message.error(error.toString());
    },
    handleFormatError(file) {
      this.$Notice.warning({
        title: "不支持的文件格式",
        desc:
          "所选文件‘ " +
          file.name +
          " ’格式不正确, 请选择 .jpg .jpeg .png .gif格式文件"
      });
    },
    handleMaxSize(file) {
      this.$Notice.warning({
        title: "文件大小过大",
        desc: "所选文件‘ " + file.name + " ’大小过大, 不得超过 5M."
      });
    },
    handleBeforeUpload() {
      const check = this.uploadList.length < 2;
      if (!check) {
        this.$Notice.warning({
          title: "最多只能上传 1 张图片"
        });
      }
      return check;
    },
    showChangeMobile() {
      this.passCheckVisible = true;
      this.password = "";
      this.changeType = "mobile";
    },
    showChangeEmail() {
      this.passCheckVisible = true;
      this.password = "";
      this.changeType = "email";
    },
    checkPassChange() {
      this.checkPassLoading = true;
      unlock({ password: this.password }).then(res => {
        this.checkPassLoading = false;
        if (res.success == true) {
          this.passCheckVisible = false;
          if (this.changeType == "mobile") {
            this.editMobileVisible = true;
          } else if (this.changeType == "email") {
            this.editEmailVisible = true;
          }
        }
      });
    },
    saveEdit() {
      this.saveLoading = true;
      let params = this.userForm;
      userInfoEdit(params).then(res => {
        this.saveLoading = false;
        if (res.success == true) {
          this.$Message.success("保存成功");
          // 更新用户信息
          Cookies.set("userInfo", this.userForm);
          // 更新头像
          this.$store.commit("setAvatarPath", this.userForm.avatar);
        }
      });
    },
    changeAddress() {
      this.userForm.address = JSON.stringify(this.userForm.addressArray);
    },
    cancelInputCodeBox() {
      this.editMobileVisible = false;
      this.userForm.mobile = this.initPhone;
    },
    cancelEditEmail() {
      this.editEmailVisible = false;
      this.emailEditForm.email = this.initEmail;
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
    countDownEmail() {
      let that = this;
      if (this.countEmail == 0) {
        this.sendedEmail = false;
        this.countEmail = 60;
        return;
      } else {
        this.countButtonEmail = this.countEmail + " s";
        this.countEmail--;
      }
      setTimeout(function() {
        that.countDownEmail();
      }, 1000);
    },
    getIdentifyCode() {
      this.$refs["mobileEditForm"].validate(valid => {
        if (valid) {
          this.sendingSms = true;
          sendSms(this.mobileEditForm.mobile).then(res => {
            this.sendingSms = false;
            if (res.success) {
              this.$Message.success("发送短信验证码成功");
              // 开始倒计时
              this.sended = true;
              this.countDown();
            }
          });
        }
      });
    },
    submitEditMobile() {
      this.$refs["mobileEditForm"].validate(valid => {
        if (valid) {
          if (!this.mobileEditForm.code) {
            this.codeError = "请填写短信验证码";
            return;
          } else {
            this.codeError = "";
          }
          this.checkCodeLoading = true;
          changeMobile(this.mobileEditForm).then(res => {
            this.checkCodeLoading = false;
            if (res.success) {
              this.$Message.success("修改成功");
              this.userForm.mobile = this.mobileEditForm.mobile;
              this.initPhone = this.mobileEditForm.mobile;
              this.updateUserInfo();
              this.editMobileVisible = false;
            }
          });
        }
      });
    },
    hasChangePhone() {
      if (this.mobileEditForm.mobile == this.initPhone) {
        this.canGetIdentifyCode = true;
      } else {
        this.$refs["mobileEditForm"].validate(valid => {
          if (valid) {
            this.canGetIdentifyCode = false;
          } else {
            this.canGetIdentifyCode = true;
          }
        });
      }
    },
    hasChangeEmail() {
      if (this.emailEditForm.email == this.initEmail) {
        this.canSendEditEmail = true;
      } else {
        this.canSendEditEmail = false;
      }
    },
    sendVerifyEmail() {
      this.$refs["emailEditForm"].validate(valid => {
        if (valid) {
          this.sendEmailLoading = true;
          sendCodeEmail(this.emailEditForm.email).then(res => {
            this.sendEmailLoading = false;
            if (res.success) {
              this.$Message.success("发送邮件验证码成功，请注意查收");
              // 开始倒计时
              this.sendedEmail = true;
              this.countDownEmail();
            }
          });
        }
      });
    },
    submitEditEmail() {
      this.$refs["emailEditForm"].validate(valid => {
        if (valid) {
          if (!this.emailEditForm.code) {
            this.codeError = "验证码不能为空";
            return;
          } else {
            this.codeError = "";
          }
          this.editEmailLoading = true;
          editEmail(this.emailEditForm).then(res => {
            this.editEmailLoading = false;
            if (res.success) {
              this.initEmail = this.emailEditForm.email;
              this.userForm.email = this.emailEditForm.email;
              this.updateUserInfo();
              this.$Message.success("修改邮件成功");
              this.editEmailVisible = false;
            }
          });
        }
      });
    },
    updateUserInfo() {
      // 更新用户信息
      userInfo().then(res => {
        if (res.success) {
          // 避免超过大小限制
          delete res.result.permissions;
          if (this.getStore("saveLogin")) {
            // 保存7天
            Cookies.set("userInfo", JSON.stringify(res.result), {
              expires: 7
            });
          } else {
            Cookies.set("userInfo", JSON.stringify(res.result));
          }
          this.setStore("userInfo", res.result);
        }
      });
    }
  },
  mounted() {
    this.uploadList = this.$refs.upload.fileList;
    this.init();
    this.hasChangePhone();
  }
};
</script>
