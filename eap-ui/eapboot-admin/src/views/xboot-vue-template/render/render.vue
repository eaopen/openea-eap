<style lang="less">
@import "./render.less";
</style>
<template>
  <div class="render">
    <Row>
      <Col>
        <Card>
          <Row>
            <Alert show-icon>
              iView自3.2.0版本起，Table组件已支持 slot-scope 用法，建议使用该新用法，
              <a href="https://run.iviewui.com/50ahQHrs" target="_blank">查看官方示例</a>。
              以下为原小于3.2.0版本中Render函数的相关用法示例
            </Alert>
            <Table border :columns="columns" :data="data" ref="table"></Table>
          </Row>
        </Card>
      </Col>
    </Row>

    <Drawer width="640" v-model="infoDrawerVisible" title="详细信息">
      <Avatar :src="infoForm.avatar" size="large" style="margin-bottom:10px;"/>
      <p class="render-info-title">基本信息</p>
      <div class="demo-drawer-profile">
        <Row>
          <Col span="12">用户名： {{infoForm.name}}</Col>
          <Col span="12">注册邮箱： {{infoForm.email}}</Col>
        </Row>
        <Row>
          <Col span="12">生日: {{infoForm.birth}}</Col>
          <Col span="12">
            个人网页：
            <a :href="infoForm.website" target="_blank">{{infoForm.website}}</a>
          </Col>
        </Row>
      </div>
      <Divider/>
      <p class="render-info-title">联系方式</p>
      <div class="demo-drawer-profile">
        <Row>
          <Col span="12">Email： {{infoForm.email}}</Col>
          <Col span="12">手机号： {{infoForm.mobile}}</Col>
        </Row>
        <Row>
          <Col span="12">
            GitHub：
            <a :href="infoForm.github" target="_blank">{{infoForm.github}}</a>
          </Col>
        </Row>
      </div>
    </Drawer>

    <Drawer title="编辑" v-model="editDrawerVisible" width="720">
      <Form :model="infoForm" label-position="top">
        <Row :gutter="32">
          <Col span="12">
            <FormItem label="姓名">
              <Input v-model="infoForm.name"/>
            </FormItem>
          </Col>
          <Col span="12">
            <FormItem label="个人网站">
              <Input v-model="infoForm.website">
                <span slot="prepend">http://</span>
              </Input>
            </FormItem>
          </Col>
        </Row>
        <Row :gutter="32">
          <Col span="12">
            <FormItem label="状态">
              <Select v-model="infoForm.status">
                <Option :value="0">启用</Option>
                <Option :value="-1">禁用</Option>
              </Select>
            </FormItem>
          </Col>
          <Col span="12">
            <FormItem label="审核状态">
              <Select v-model="infoForm.value">
                <Option :value="0">待审核</Option>
                <Option :value="1">审核通过</Option>
                <Option :value="-1">审核驳回</Option>
              </Select>
            </FormItem>
          </Col>
        </Row>
        <Row :gutter="32">
          <Col span="12">
            <FormItem label="创建时间">
              <DatePicker
                v-model="infoForm.date"
                :options="options"
                type="date"
                style="display: block"
                placement="bottom-end"
              ></DatePicker>
            </FormItem>
          </Col>
        </Row>
        <FormItem label="描述">
          <Input type="textarea" v-model="infoForm.desc" :rows="4"/>
        </FormItem>
      </Form>
      <div class="demo-drawer-footer">
        <Button style="margin-right: 8px" @click="editDrawerVisible = false">取消</Button>
        <Button type="primary" @click="submitEdit">提交</Button>
      </div>
    </Drawer>

    <Modal v-model="picVisible" title="图片预览" footer-hide draggable>
      <img :src="infoForm.avatar" alt="无效的图片链接" style="width: 100%;margin: 0 auto;display: block;">
    </Modal>
  </div>
</template>

<script>
import expandRow from "./expand.vue";
export default {
  name: "render",
  components: { expandRow },
  data() {
    return {
      infoDrawerVisible: false, // 信息查看
      editDrawerVisible: false, // 编辑
      picVisible: false, // 图片预览
      columns: [
        // 表头
        {
          type: "selection",
          width: 60,
          align: "center"
        },
        {
          type: "expand",
          width: 50,
          render: (h, params) => {
            return h(expandRow, {
              props: {
                row: params.row
              }
            });
          }
        },
        {
          type: "index",
          width: 60,
          align: "center"
        },
        {
          title: "用户",
          key: "name",
          minWidth: 190,
          sortable: true,
          render: (h, params) => {
            return h("div", [
              h(
                "a",
                {
                  on: {
                    click: () => {
                      this.showDetail(params.row);
                    }
                  }
                },
                params.row.name
              )
            ]);
          }
        },
        {
          title: "图片(点击可预览)",
          key: "createTime",
          align: "center",
          width: 150,
          render: (h, params) => {
            return h("img", {
              attrs: {
                src: params.row.avatar,
                alt: "加载图片失败"
              },
              style: {
                cursor: "pointer",
                width: "40px",
                height: "40px",
                "margin-top": "5px",
                "object-fit": "contain"
              },
              on: {
                click: () => {
                  this.showPic(params.row);
                }
              }
            });
          }
        },
        {
          title: "状态",
          key: "status",
          align: "center",
          width: 150,
          render: (h, params) => {
            return h(
              "i-switch",
              {
                props: {
                  value: params.row.status,
                  size: "large",
                  "true-value": 0,
                  "false-value": -1
                },
                on: {
                  "on-change": v => {
                    this.changeStatus(params.row, v);
                  }
                }
              },
              [
                h("span", { slot: "open" }, "启用"),
                h("span", { slot: "close" }, "禁用")
              ]
            );
          }
        },
        {
          title: "价格设定",
          key: "price",
          align: "center",
          width: 150,
          render: (h, params) => {
            return h("InputNumber", {
              props: {
                value: params.row.price,
                min: 0,
                precision: 2
              },
              on: {
                "on-change": v => {
                  this.changePrice(params.row, v);
                }
              }
            });
          }
        },
        {
          title: "审核操作",
          key: "value",
          width: 200,
          render: (h, params) => {
            return h(
              "Select",
              {
                props: {
                  value: params.row.value,
                  transfer: true
                },
                on: {
                  "on-change": v => {
                    this.changeSelect(params.row, v);
                  }
                }
              },
              [
                h(
                  "Option",
                  {
                    props: {
                      value: 0
                    }
                  },
                  "待审核"
                ),
                h(
                  "Option",
                  {
                    props: {
                      value: 1
                    }
                  },
                  "审核通过"
                ),
                h(
                  "Option",
                  {
                    props: {
                      value: -1
                    }
                  },
                  "审核驳回"
                )
              ]
            );
          }
        },
        {
          title: "操作",
          key: "action",
          align: "center",
          width: 250,
          fixed: "right",
          render: (h, params) => {
            return h("div", [
              h(
                "Button",
                {
                  props: {
                    type: "primary",
                    size: "small",
                    icon: "ios-create-outline"
                  },
                  style: {
                    marginRight: "5px"
                  },
                  on: {
                    click: () => {
                      this.edit(params.row);
                    }
                  }
                },
                "编辑"
              ),
              h(
                "Dropdown",
                {
                  props: { transfer: true },
                  on: {
                    "on-click": v => {
                      this.changeDropDown(params.row, v);
                    }
                  }
                },
                [
                  h(
                    "Button",
                    {
                      props: { size: "small" },
                      style: {
                        height: "23.5px"
                      }
                    },
                    "更多操作 ∨"
                  ),

                  h("DropdownMenu", { slot: "list" }, [
                    h("DropdownItem", { props: { name: "reset" } }, "重置密码"),
                    h("DropdownItem", { props: { name: "delete" } }, "删除")
                  ])
                ]
              )
            ]);
          }
        }
      ],
      data: [], // 表单数据
      infoForm: {},
      options: {
        shortcuts: [
          {
            text: "今天",
            value() {
              return new Date();
            },
            onClick: picker => {
              this.$Message.info("Click today");
            }
          },
          {
            text: "昨天",
            value() {
              const date = new Date();
              date.setTime(date.getTime() - 3600 * 1000 * 24);
              return date;
            },
            onClick: picker => {
              this.$Message.info("Click yesterday");
            }
          },
          {
            text: "一周前",
            value() {
              const date = new Date();
              date.setTime(date.getTime() - 3600 * 1000 * 24 * 7);
              return date;
            },
            onClick: picker => {
              this.$Message.info("Click a week ago");
            }
          }
        ]
      }
    };
  },
  methods: {
    init() {
      this.getDataList();
    },
    getDataList() {
      // 以下为模拟数据
      this.data = [
        {
          id: "1",
          name: "SpringBoot(点我查看详细信息)",
          email: "xx@qq.com",
          birth: "",
          website: "",
          mobile: "",
          github: "",
          avatar: "",
          value: 0,
          status: 0,
          price: 18.88,
          date: "2018-08-08",
          desc: ""
        },
        {
          id: "2",
          name: "xxx(点我查看详细信息)",
          email: "xx@qq.com",
          birth: "2018-08-08",
          website: "",
          mobile: "",
          github: "",
          avatar: "",
          value: 1,
          status: -1,
          price: 66.66,
          date: "",
          desc: ""
        }
      ];
    },
    showDetail(v) {
      // 转换null为""
      for (let attr in v) {
        if (v[attr] == null) {
          v[attr] = "";
        }
      }
      let str = JSON.stringify(v);
      let data = JSON.parse(str);
      this.infoForm = data;
      this.infoDrawerVisible = true;
    },
    showPic(v) {
      this.infoForm.avatar = v.avatar;
      this.picVisible = true;
    },
    changeStatus(row, v) {
      this.$Message.success("修改用户 " + row.name + " 状态为 " + v + " 成功");
    },
    changePrice(row, v) {
      this.$Message.success("修改 " + row.name + " 价格为 " + v + " 成功");
    },
    changeSelect(row, v) {
      this.$Message.success("修改 " + row.name + " 审核状态为 " + v + " 成功");
    },
    changeDropDown(row, v) {
      this.$Message.info("点击了 " + row.name + " 的 " + v);
    },
    edit(v) {
      // 转换null为""
      for (let attr in v) {
        if (v[attr] == null) {
          v[attr] = "";
        }
      }
      let str = JSON.stringify(v);
      let data = JSON.parse(str);
      this.infoForm = data;
      this.editDrawerVisible = true;
    },
    submitEdit() {
      this.$Message.success("编辑成功");
      this.editDrawerVisible = false;
    }
  },
  mounted() {
    this.init();
  }
};
</script>