
<template>
  <!-- param传参实例：{"order": "asc","offset": 0,"limit": 10,"fieldRelation": "OR","businessname":  "{key}","businessnum": "{key}","businessname^VLK": "{key}","businessnum^VLK": "{key}","test":"{key2}"} -->
  <el-autocomplete
    style="width: 100%"
    size="mini"
    v-model="inputValue"
    :fetch-suggestions="querySearchAsync"
    :placeholder="placeholder"
    :hide-loading="true"
    @select="hselect"
  >
    <template slot-scope="{ item }">
      <div class="name">{{ item[name] }} {{ item[name1] }}</div>
    </template>
  </el-autocomplete>
</template>

<script>
import Vue from "vue";

export default {
  props: [
    "value",
    "path",
    "param",
    "value2",
    "placeholder",
    "shownames",
    "method",
    "returnname"
  ],
  data() {
    return {
      inputValue: "",
      timeout: null,
      name: "",
      name1: "",
    };
  },
  methods: {
    hselect(item) {
      let name = "";
      if(this.returnname){
        name = item[this.returnname];
      }else {
        if (this.name1) {
          name = item[this.name] + " " + item[this.name1];
        } else {
          name = item[this.name];
        }
      }
      
      this.inputValue = name;
      let temp = Object.assign({}, item, {
        name,
      });
      this.$emit("handleselect", temp);
      this.$emit("update:name", name);
      this.$emit("input", name);
    },
    querySearchAsync(queryString, cb) {
      this.$emit("handleselect", this.inputValue);
      this.$emit("update:name", this.inputValue);
      this.$emit("input", this.inputValue);
      cb([]);
      let that = this;
      if (this.inputValue) {
        let paramObj = "";
        let path = this.path;
        if (this.method == "get") {
          path = path.replace(/\{key}/g, queryString)
        } else {
          let param = this.param;
          let input = JSON.stringify(this.inputValue);
          param = param.replace(/\{key}/g, queryString);
          if (param.indexOf("{key2}") !== -1) {
            if (this.value2) {
              param = param.replace(/\{key2}/g, this.value2);
            } else {
              param = param.replace(/\{key2}/g, "");
            }
          }
          console.log(param, "最终参数");
          paramObj = JSON.parse(param);
        }

        Vue.baseService
          [this.method || 'postForm'](Vue.__ctx + path, paramObj)
          .then(function (data) {
            if (data.code == 200) {
              if (data.rows) {
                cb(data.rows);
              } else if (data.data) {
                cb(data.data);
              } else {
                cb([]);
              }
            } else {
              cb([]);
            }
          });
      }
    },
  },
  mounted: function () {
    this.inputValue = this.value;
    if (this.shownames) {
      this.name = this.shownames.split(",")[0];
      this.name1 = this.shownames.split(",")[1];
    }
  },
  watch: {
    value(val) {
      this.inputValue = val;
      this.$emit("update:name", this.inputValue);
      this.$emit("input", this.inputValue);
    },
  },
};
</script>
