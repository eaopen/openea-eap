<template>
  <div>
    <el-row>
      <el-col style="max-width: 490px; margin-left: 10px">
        <el-autocomplete
          size="mini"
          v-model="input"
          :fetch-suggestions="querySearchAsync"
          placeholder="输入并选择"
          :hide-loading="true"
          @select="hselect"
          style="width: 100%"
          @blur="input = ''"
          :disabled="disabled"
        >
          <template slot-scope="{ item }">
            <div class="name">
              {{ item.fullname_ }} {{ item.account_ }} {{ item.group_name_ }}
            </div>
          </template>
        </el-autocomplete>
      </el-col>
      <el-col style="max-width: 100px; padding-left: 5px">
        <el-button type="primary" size="mini" @click="showDialog" :disabled="disabled">批量添加</el-button>
      </el-col>
    </el-row>
    <div
      style="
        padding: 10px;
        width: 100%;
        max-width: 600px;
        max-height: 400px;
        overflow-y: auto;
      "
    >
      <el-row style="border-left: 1px #aaa solid">
        <draggable
          v-model="list"
          @update="datadragEnd"
          :options="{ animation: 500 }"
        >
          <el-col :span="24" v-for="(item, index) in list" :key="item.id_">
            <el-row>
              <el-col
                :span="2"
                style="
                  border-bottom: 1px #aaa solid;
                  border-right: 1px #aaa solid;
                  line-height: 24px;
                  background: #efefef;
                  text-align: center;
                  color: #333;
                  font-weight: 600;
                "
                >{{ index + 1 }}</el-col
              >
              <el-col
                :span="6"
                style="
                  padding-left: 5px;
                  border-bottom: 1px #aaa solid;
                  border-right: 1px #aaa solid;
                  line-height: 24px;
                  background-color: #efefef;
                  color: #333;
                  font-weight: 600;
                "
                >{{ item.fullname_ }}</el-col
              >
              <el-col
                :span="6"
                style="
                  padding-left: 5px;
                  border-bottom: 1px #aaa solid;
                  border-right: 1px #aaa solid;
                  line-height: 24px;
                "
                >{{ item.account_ }}</el-col
              >
              <el-col
                :span="7"
                style="
                  padding-left: 5px;
                  border-bottom: 1px #aaa solid;
                  border-right: 1px #aaa solid;
                  line-height: 24px;
                "
                >{{ item.group_name_ }}</el-col
              >
              <el-col
                      v-if="!disabled"
                      :span="3"
                      style="
                  border-bottom: 1px #aaa solid;
                  border-right: 1px #aaa solid;
                  height: 25px;
                  text-align: center;
                  padding-top: 5px;
                "
              >
                <el-popover placement="top" width="160" v-model="item.visible">
                  <p>确定删除吗?</p>
                  <div style="text-align: right; margin: 0">
                    <el-button
                      size="mini"
                      type="text"
                      @click="item.visible = false"
                      >取消</el-button
                    >
                    <el-button
                      type="primary"
                      size="mini"
                      @click="delItem(item, index)"
                      >确定</el-button
                    >
                  </div>
                  <span
                    slot="reference"
                    style="color: #2d8cf0; cursor: pointer; margin-top: 5px"
                    >删除</span
                  >
                </el-popover>
              </el-col>
            </el-row>
          </el-col>
        </draggable>
      </el-row>
    </div>
    <el-dialog title="批量添加" :visible.sync="dialogState">
      <el-input
              type="textarea"
              v-model="multInput"
              autocomplete="off"
              size="mini"
              style="border: 1px #aaa solid"
              placeholder="请输入或粘贴员工工号，多个用英文逗号 , 隔开"
      ></el-input>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogState = false" size="mini">取 消</el-button>
        <el-button type="primary" @click="queryUser" size="mini"
          >添 加</el-button
        >
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import Vue from "vue";
  import draggable from "vuedraggable";

  function sortRule(a, b) {
    return a.orderby - b.orderby;
  }

  export default {
    name: "abUserMult",
    components: {
      draggable,
    },
    props: {
      value: {},
    abPermission: {},
    disabled: {
      type: Boolean,
      defalut: false, //默认不禁用
    },
  },
  data: function () {
    return {
      dialogState: false,
      idList: [],
      list: [], //初始化人员列表
      // active: "1"
      // lastdate: "2021-03-02 17:02:45"
      // lastuid: "417137619324108800"
      // orderby: 1
      // postdate: "2021-03-02 17:02:45"
      // postuid: "417137619324108800"
      // taskid: ""
      // uid: "11217098"
      // uname: "丁建解 11217098 人交中心2"
      input: "",
      multInput: "", // 弹窗输入框绑定
      importList: [], //导入名单
      importState: false,
      errs: [],
      hideValue: "",
    };
  },
  mounted() {
    this.hideValue = this.value;
    if (this.value && this.value.length) {
      let arrs = this.value.map((item) => item.uid);
      let str = arrs.join(",");
      this.getUserList(str);
    }
  },
  watch: {
    // value: function (newValue) {
    //   this.hideValue = newValue;
    //   if (newValue && newValue.length) {
    //     let arrs = newValue.map((item) => item.uid);
    //     let str = arrs.join(",");
    //     this.getUserList(str);
    //   }
    // },
  },
  methods: {
    datadragEnd() {},
    formatList() {
      let arr = [];
      if (this.list.length) {
        this.list.forEach((item, index) => {
          let cache = {
            active: item.active,
            fullname_: item.fullname_,
            uid: item.id_,
            orderby: index + 1,
          };
          arr.push(cache);
        });
      }
      console.log(arr);
      return arr;
    },
    getUserList(str) {
      Vue.baseService
        .postForm(Vue.__ctx + "/etech/formCustDialog/listData_ygxz", {
          order: "asc",
          offset: 0,
          limit: 10,
          fieldRelation: "OR",
          id_: str,
          "id_^VIN": str,
        })
        .then((res) => {
          if (res.rows && res.rows.length) {
            res.rows.forEach((item) => {
              this.$set(item, "visible", false);
            });
            this.list = res.rows.sort(sortRule);
            console.log(this.list);
          }
        });
    },
    showDialog() {
      this.multInput = "";
      this.dialogState = true;
    },
    queryUser() {
      console.log(this.multInput)
      Vue.baseService
        .postForm(Vue.__ctx + "/etech/formCustDialog/listData_ygxz", {
          order: "asc",
          offset: 0,
          limit: 10,
          fieldRelation: "OR",
          account_: this.multInput,
          "account_^VIN": this.multInput
        })
        .then((res) => {
          if (res.rows && res.rows.length) {
            this.checkUser(res.rows);
          } else {
            this.checkUser([]);
          }
        });
    },
    checkUser(list) {
      if (!list.length) {
        this.dialogState = false;
        this.$message.error("没有找到用户，请确认您输入的员工编号");
        return;
      }
      this.importState = list;
      let arr = this.multInput.split(",");
      let errs = [];
      let errs2 = [];
      let rightList = [];
      arr.forEach((item) => {
        if (!list.find((i) => i.account_ == item)) {
          errs.push(item);
        }
      });
      list.forEach((item) => {
        if (this.list.find((i) => i.account_ === item.account_)) {
          errs2.push(item);
        } else {
          this.$set(item, "visible", false);
          rightList.push(item);
        }
      });
      this.list = [...rightList, ...this.list];
      let v = this.formatList();
      this.$emit("update:name", v);
      this.$emit("input", v);
      this.$emit("change", v);
      this.dialogState = false;
      if (errs.length || errs2.length) {
        let str = "";
        if (errs.length) {
          str += "员工号";
          str += errs.join(",");
          str += "没有找到对应的员工信息。";
        }
        if (errs2.length) {
          str += errs2.map((item) => item.fullname_).join(",");
          str += "信息已存在";
        }
        this.$message.error(str);
      }
    },
    querySearchAsync(queryString, cb) {
      if (!queryString) {
        cb(null);
      } else {
        Vue.baseService
          .postForm(Vue.__ctx + "/etech/formCustDialog/listData_ygxz", {
            order: "asc",
            offset: 0,
            limit: 10,
            fieldRelation: "OR",
            fullname_: queryString,
            "fullname_^VLK": queryString,
            account_: queryString,
            "account_^VLK": queryString,
            email_: queryString,
            "email_^VRHK": queryString
          })
          .then((res) => {
            cb(res.rows);
          });
      }
    },
    hselect(item) {
      let i = this.list.find((u) => u.id_ === item.id_);
      if (!i) {
        let cache = Object.assign({}, item, {
          visible: false,
        });
        this.list.unshift(item);
        this.hideValue = this.formatList();
        let v = this.formatList();
        this.$emit("update:name", v);
        this.$emit("input", v);
        this.$emit("change", v);
      } else {
        this.$message.error("用户已存在");
      }
    },
    delItem(item, index) {
      item.visible = false;
      this.list.splice(index, 1);
      let v = this.formatList();
      this.$emit("update:name", v);
      this.$emit("input", v);
      this.$emit("change", v);
    },
  },
};
</script>

