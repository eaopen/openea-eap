<template>
  <div class="list-container">
    <div
      class="list-row"
      v-for="(row, index) in list"
      :key="row.key"
      style="margin: 5px 0"
    >
      <div style="width: calc(100% - 90px); float: left">
        <div
          :class="'col-xs-' + col"
          class="form-item"
          v-for="(item, subIndex) in row.children"
          :key="subIndex"
        >
          <label class="form-item-label">
            {{ item.showName }}
            <span style="color: #ef2121" v-if="item.required">*</span></label
          >
          <div class="form-item-content">
            <mult-input
              v-if="item.controlType == 1"
              v-model="item.val"
              @change="changeValue"
              :placeholder="item.placeholder"
              :permission="permission"
              :required="item.required"
            />
            <mult-select
              v-if="item.controlType == 2"
              v-model="item.val"
              :options="options[item.formatKey]"
              @change="changeValue"
              :placeholder="item.placeholder"
              :permission="permission"
              :required="item.required"
            />
            <mult-async
              v-if="item.controlType == 3"
              v-model="item.val"
              :format-key="item.formatKey"
              :search-key="item.searchKey"
              :return-key="item.returnKey"
              @change="changeValue"
              :placeholder="item.placeholder"
              :permission="permission"
              :required="item.required"
            />
            <mult-date
              v-if="item.controlType == 4"
              v-model="item.val"
              @change="changeValue"
              :placeholder="item.placeholder"
              :permission="permission"
              :required="item.required"
            />
          </div>
        </div>
        <div style="clear: both"></div>
      </div>
      <div style="width: 90px; float: right">
        <el-button
          type="success"
          size="mini"
          icon="el-icon-plus"
          @click="addData"
          v-if="index === list.length - 1 && permission && permission == 'w'"
        >
          添加
        </el-button>
        <el-button
          type="danger"
          size="mini"
          icon="el-icon-delete"
          @click="delData(index)"
          v-if="index !== list.length - 1 && permission && permission == 'w'"
        >
          删除
        </el-button>
      </div>
      <div style="clear: both"></div>
    </div>
  </div>
</template>

<script>
import Vue from "vue";
import multInput from "./mult/multInput.vue";
import multSelect from "./mult/multSelect.vue";
import multAsync from "./mult/multAsync.vue";
import multDate from "./mult/multDate.vue";
export default {
  //permission w=编辑 r= 只读
  components: { multInput, multSelect, multAsync, multDate },
  props: ["preset", "value", "permission", "desc"], //value 格式为 [{key1: '', key2: '', key3: ''},{}]
  name: "multMain",
  data() {
    return {
      options: [],
      col: 3,
      list: [],
      addItem: [],
      requiredList: [],
    };
  },
  created() {
    if (this.preset) {
      let temp = JSON.parse(this.preset);
      // console.log(this.preset)
      this.col = temp.colCount ? 12 / temp.colCount : 3;
      this.addItem = temp.addItem;
      let l = [];
      temp.addItem.forEach((i) => {
        if (i.required) {
          l.push(i.key);
        }
      });
      this.requiredList = l;
    }
    // console.log(this.value);
    this.list = this.formatValue();
    this.getDictOptions(this.addItem);
  },
  mounted() {
    setTimeout(() => {
      let list = this.formatReturnData();
      this.isValid(list);
    }, 200);
  },
  methods: {
    updateValue() {
      // console.log(11)
      setTimeout(() => {
        this.list = this.formatValue();
      });
    },
    formatValue() {
      if (!this.value) {
        let list = []
        if (!this.permission || this.permission !== "w") {
          return;
        }
        let cache = {
          key: this.getUuid(),
          children: this.addItem,
        };
        list.push(JSON.parse(JSON.stringify(cache)));
        return list
      } else {
        let temp = JSON.parse(JSON.stringify(this.value));
        let list = [];
        if (temp && temp.length) {
          temp.forEach((item) => {
            let cache = {
              key: this.getUuid(),
              orderby: item.orderby,
              children: JSON.parse(JSON.stringify(this.addItem)),
            };
            for (let key in item) {
              if (key !== "orderby") {
                let index = cache.children.findIndex((i) => i.key == key);
                cache.children[index].val = item[key];
              }
            }
            list.push(cache);
          });
        }
        if (!this.permission || this.permission !== "w") {
          return;
        }
        let cache = {
          key: this.getUuid(),
          children: this.addItem,
        };
        list.push(JSON.parse(JSON.stringify(cache)));
        console.log(list, "format Value");
        return list;
      }
    },
    // 验证必填
    isValid(arr) {
      let flag = true;
      if (this.requiredList.length) {
        if (arr && arr.length) {
          arr.forEach((item) => {
            this.requiredList.forEach((i) => {
              if (!item[i]) {
                flag = false;
              }
            });
          });
        } else {
          flag = false;
        }
      }
      if (!flag) {
        // 字段错误
        this.$parent.$validity[this.desc] = "必填";
      } else if (this.$parent.$validity[this.desc]) {
        delete this.$parent.$validity[this.desc];
      }
    },
    getDictOptions(list) {
      let _this = this;
      list.forEach((item) => {
        if (item.controlType == 2 && item.formatKey) {
          Vue.baseService
            .get(
              Vue.__ctx + "/sys/dataDict/getDictData?dictKey=" + item.formatKey,
              ""
            )
            .then((res) => {
              _this.$set(_this.options, item.formatKey, res.data);
            });
        }
      });
    },
    addData() {
      if (!this.permission || this.permission !== "w") {
        return;
      }
      let cache = {
        key: this.getUuid(),
        children: this.addItem,
      };
      this.list.push(JSON.parse(JSON.stringify(cache)));
      this.$emit("addItem");
    },
    delData(i) {
      this.$emit("delItem", this.list[i]);
      this.$nextTick(() => {
        this.list.splice(i, 1);
        this.changeValue();
      });
    },
    formatReturnData() {
      let list = [];
      if (this.list && this.list.length) {
        this.list.forEach((item) => {
          let temp = {};
          item.children.forEach((i) => {
            if (i.val) {
              temp[i.key] = i.val;
            }
          });
          if (item.orderby) temp.orderby = item.orderby;
          if (Object.keys(temp).length) {
            list.push(temp);
          }
        });
      }
      return list;
    },
    changeValue() {
      let list = this.formatReturnData();
      this.isValid(list);
      console.log(list);
      this.$emit("update:name", list);
      this.$emit("input", list);
      this.$emit("change", list);
    },
    getUuid() {
      var chars = [
        "0",
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "A",
        "B",
        "C",
        "D",
        "E",
        "F",
        "G",
        "H",
        "I",
        "J",
        "K",
        "L",
        "M",
        "N",
        "O",
        "P",
        "Q",
        "R",
        "S",
        "T",
        "U",
        "V",
        "W",
        "X",
        "Y",
        "Z",
        "a",
        "b",
        "c",
        "d",
        "e",
        "f",
        "g",
        "h",
        "i",
        "j",
        "k",
        "l",
        "m",
        "n",
        "o",
        "p",
        "q",
        "r",
        "s",
        "t",
        "u",
        "v",
        "w",
        "x",
        "y",
        "z",
      ];
      var nums = "";
      for (var i = 0; i < 32; i++) {
        var id = parseInt(Math.random() * 61);
        nums += chars[id];
      }
      return nums;
    },
  },
};
</script>

