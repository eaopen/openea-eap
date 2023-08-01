<template>
  <div>
    <el-row v-if="permission == 'w' && !disabled">
      <el-col style=" margin-left: 10px">
        <el-autocomplete
          size="mini"
          v-model="input"
          :fetch-suggestions="querySearchAsync"
          placeholder="输入并选择"
          :hide-loading="true"
          @select="hselect"
          style="width: 100%"
          @blur="input = ''"
        >
          <template slot-scope="{ item }">
            <div class="name">
              <span>{{ item[show[0]] }}</span>
              <span v-if="item[show[1]]">{{ item[show[1]] }}</span>
              <span v-if="item[show[2]]">{{ item[show[2]] }}</span>
            </div>
          </template>
        </el-autocomplete>
      </el-col>
    </el-row>
    <div
    v-if="list.length"
      style="
        padding: 10px;
        width: 100%;
        max-width: 600px;
        max-height: 400px;
        overflow-y: auto;
      "
    >
      <el-row style="border-top: 1px #aaa solid; border-left: 1px #aaa solid">
        <draggable
          v-model="list"
          @update="datadragEnd"
          :options="{ animation: 500 }"
        >
          <el-col v-if="title" :span="24" >
            <el-row>
              <el-col
                  :span="2"
                  style="
                  border-bottom: 1px #aaa solid;
                  border-right: 1px #aaa solid;
                  line-height: 24px;
                  background: #efefef;
                  color: #333;
                  font-weight: 600;
                  padding-left: 5px;
                "
              >{{ '序号' }}</el-col
              >
              <el-col
                  v-if="show[0]"
                  :span="5"
                  style="
                  border-bottom: 1px #aaa solid;
                  border-right: 1px #aaa solid;
                  line-height: 24px;
                  background: #efefef;
                  color: #333;
                  font-weight: 600;
                  padding-left: 5px;
                "
              >{{ this.titles[0] }}</el-col
              >
              <el-col
                  :span="show[2] ? 8 : 14"
                  v-if="show[1]"
                  style="
                  border-bottom: 1px #aaa solid;
                  border-right: 1px #aaa solid;
                  line-height: 24px;
                  background: #efefef;
                  color: #333;
                  font-weight: 600;
                  padding-left: 5px;
                "
              >{{ this.titles[1] }}</el-col
              >
              <el-col
                  v-if="show[2]"
                  :span="6"
                  style="
                  border-bottom: 1px #aaa solid;
                  border-right: 1px #aaa solid;
                  line-height: 24px;
                  background: #efefef;
                  color: #333;
                  font-weight: 600;
                  padding-left: 5px;
                "
              >{{ this.titles[2] }}</el-col
              >
              <el-col
                  :span="3"
                  style="
                  padding-left: 5px;
                  border-bottom: 1px #aaa solid;
                  border-right: 1px #aaa solid;
                  line-height: 24px;
                  background: #efefef;
                  color: #333;
                  font-weight: 600;
                  text-align: center;
                ">操作
              </el-col>
            </el-row>
          </el-col>


          <el-col :span="24" v-for="(item, index) in list" :key="item.id_">
            <el-row>
              <el-col
                :span="2"
                style="
                  padding-left: 5px;
                  border-bottom: 1px #aaa solid;
                  border-right: 1px #aaa solid;
                  line-height: 24px;
                "
                >{{ index + 1 }}</el-col
              >
              <el-col
                v-if="show[0]"
                :span="5"
                style="
                  padding-left: 5px;
                  border-bottom: 1px #aaa solid;
                  border-right: 1px #aaa solid;
                  line-height: 24px;
                "
                >{{ item[show[0]] }}</el-col
              >
              <el-col
                :span="show[2] ? 8 : 14"
                v-if="show[1]"
                style="
                  padding-left: 5px;
                  border-bottom: 1px #aaa solid;
                  border-right: 1px #aaa solid;
                  line-height: 24px;
                "
                >{{ item[show[1]] }}</el-col
              >
              <el-col
                v-if="show[2]"
                :span="6"
                style="
                  padding-left: 5px;
                  border-bottom: 1px #aaa solid;
                  border-right: 1px #aaa solid;
                  line-height: 24px;
                "
                >{{ item[show[2]].toString().split(" ")[0] }}</el-col
              >
              <el-col
                :span="3"
                style="
                  border-bottom: 1px #aaa solid;
                  border-right: 1px #aaa solid;
                  height: 25px;
                  text-align: center;
                "
              >
                <el-popover placement="top" width="160" v-model="item.visible" v-if="permission == 'w' && !disabled">
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
                    style="color: #2d8cf0; cursor: pointer;"
                    >删除</span
                  >
                </el-popover>
              </el-col>
            </el-row>
          </el-col>
        </draggable>
      </el-row>
    </div>
  </div>
</template>

<script>
// import Vue from "vue";
import draggable from "vuedraggable";
function sortRule(a, b) {
  return a.orderby - b.orderby;
}
export default {
  name: "abMult",
  components: {
    draggable,
  },
  props: ['value','disabled','permission','param','formatparam','idname','path','showlist','title'],
  data: function () {
    return {
      dialogState: false,
      idList: [],
      list: [], //初始化人员列表
      show: [],
      input: "",
      importList: [], //导入名单
      titles: []
    };
  },
  created(){
    console.log(typeof this.showlist)
    this.show = JSON.parse(this.showlist)
    if (this.title){
      this.titles = this.title.split(",")
    }

  },
  mounted() {
    if (this.value && this.value.length) {
      let arrs = this.value.map((item) => item[this.idname]);
      if(arrs.length){
        this.getList(arrs);
      }
    }
  },
  methods: {
    datadragEnd() {},
    formatList() {
      let arr = [];
      if (this.list.length) {
        this.list.forEach((item, index) => {
          let cache = {
            active: item.active,
            orderby: index + 1,
          };
          // cache[this.idname] = item[this.idname];
          cache = Object.assign({},cache, item)
          arr.push(cache);
        });
      }
      return arr;
    },
    getList(arr) {
      // console.log(str)
      this.list = []
      arr.forEach(i=>{
        let param = { order: "asc", offset: 0, limit: 10, fieldRelation: "OR" };
        let p = JSON.parse(this.formatparam)
        p.forEach((item) => {
          param[item] = i;
        });
        Vue.baseService
          .postForm(Vue.__ctx + this.path, param)
          .then((res) => {
            if (res.rows && res.rows.length) {
              res.rows.forEach((item) => {
                this.$set(item, "visible", false);
              });
              this.list.push(res.rows[0])
            }
          });
      })
      
    },
    querySearchAsync(queryString, cb) {
      if (!queryString) {
        cb(null);
      } else {
        let param = { order: "asc", offset: 0, limit: 10, fieldRelation: "OR" };
        let p = JSON.parse(this.param)
        p.forEach((item) => {
          param[item] = queryString;
        });

        Vue.baseService
          .postForm(Vue.__ctx + this.path, param)
          .then((res) => {
            cb(res.rows);
          });
      }
    },
    hselect(item) {
      console.log(item)
      console.log(this.list)
      let i = this.list.find((u) => u[this.idname] === item[this.idname]);
      if (!i) {
        let cache = Object.assign({}, item, {
          visible: false,
        });
        this.list.push(item);
        this.hideValue = this.formatList();
        let v = this.formatList();
        this.$emit("update:name", v);
        this.$emit("input", v);
        this.$emit("change", v);
      } else {
        this.$message.error("记录已存在");
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

