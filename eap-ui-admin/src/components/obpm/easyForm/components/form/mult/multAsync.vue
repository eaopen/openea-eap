<template>
  <el-select
    v-model="val"
    filterable
    clearable
    remote
    :placeholder="ph"
    :remote-method="remoteMethod"
    :loading="loading"
    size="mini"
     :class="{'is-error': err}"
    :disabled="!permission || permission !=='w'"
    
  >
    <el-option
      v-for="item in options"
      :key="item[returnKey]"
      :label="item[searchKey]"
      :value="item[returnKey]"
    >
    </el-option>
  </el-select>
</template>

<script>
import Vue from "vue";

export default {
  name: 'multAsync',
  props: [
    "value",
    "formatKey",
    "placeholder",
    "searchKey",
    "returnKey",
    "selectState",
    "permission",
    "required"
  ],
  data() {
    return {
      val: "",
      options: [],
      timeout: null,
      loading: false,
      err: false
    };
  },
  methods: {
    remoteMethod(query) {
      let vm = this
      if (query !== "") {
        // this.loading = true;
        setTimeout(() => {
          // this.loading = false;
          let param = {
            order: "asc",
            offset: 0,
            limit: 10,
          };
          param[this.searchKey] = query;
          param[this.searchKey + "^VLK"] = query;
          let cache = {};
          cache[this.searchKey] = query;
          cache[this.returnKey] = query;
          Vue.baseService
            .postForm(
              Vue.__ctx + "/form/formCustDialog/listData_" + this.formatKey,
              param
            )
            .then(function (data) {
              if (data.code == 200) {
                if (data.rows && data.rows.length) {
                  vm.options = data.rows;
                } else {
                  vm.options = [cache];
                }
              } else {
                vm.options = [];
              }
            });
        }, 200);
      } else {
        this.options = [];
      }
    },
  },
  computed: {
    ph() {
      if (!this.permission || this.permission !== "w") {
        return "";
      }else if (this.placeholder) {
        return this.placeholder;
      } else {
        return "请输入并选择";
      }
    },
  },
  watch: {
    val(v, ov) {
      // console.log(v)
       if(this.required){
          if(v){
              this.err = false
          }else {
              this.err = true
          }
      }
      this.$emit("update:name", v);
      this.$emit("input", v);
      this.$emit("change", v);
    },
  },
  mounted: function () {
    // console.log('mounted')
    if (this.value) {
      let vm = this
      this.val = this.value - 0;
      this.loading = false;
      let param = {
        order: "asc",
        offset: 0,
        limit: 10,
      };
      param[this.returnKey] = this.val;;
      param[this.returnKey + "^VLK"] = this.val;;
      Vue.baseService
        .postForm(
          Vue.__ctx + "/form/formCustDialog/listData_" + this.formatKey,
          param
        )
        .then(function (data) {
          if (data.code == 200) {
            if (data.rows) {
              vm.options = data.rows;
            } 
          } else {
            vm.options = [];
          }
        });
    }
  },
};
</script>
