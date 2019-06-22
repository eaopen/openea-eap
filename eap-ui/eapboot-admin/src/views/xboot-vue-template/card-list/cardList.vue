<style lang="less">
@import "./cardList.less";
</style>
<template>
  <div class="search">
    <Row>
      <Col>
        <Card>     
          <Tabs value="1">
            <TabPane label="文件/应用类" name="1">
              <div class="wrapper">
                <Card v-for="(item, i) in data" :key="i" class="card">
                  <div class="content">
                    <img @click="showPic(item)" v-if="item.type.indexOf('image')>=0" class="img" :src="item.url">
                    <div v-else-if="item.type.indexOf('video')>=0" class="video" @click="showVideo(item)">
                      <video class="cover">
                        <source :src="item.url"></source>
                      </video>
                      <img class="play" src="@/assets/play.png"/>
                    </div>
                    <div v-else class="other">
                      <div class="name">{{item.name}}</div>
                      <div class="key">{{item.fkey}}</div>
                      <div class="info">文件类型：{{item.type}} 文件大小：{{((item.size * 1.0) / (1024 * 1024)).toFixed(2)}} MB 创建时间：{{item.createTime}}</div>
                    </div>
                    <div class="actions">
                      <div class="btn">
                        <Tooltip content="下载" placement="top">
                            <Icon @click="download(item)" type="md-download" size="16"/>
                        </Tooltip>
                      </div>
                      <div class="btn">
                        <Tooltip content="重命名" placement="top">
                            <Icon @click="rename(item)" type="md-create" size="16"/>
                        </Tooltip>
                      </div>
                      <div class="btn">
                        <Tooltip content="复制" placement="top">
                            <Icon @click="copy(item)" type="md-copy" size="16"/>
                        </Tooltip></div>
                      <div class="btn-no">
                        <Tooltip content="删除" placement="top">
                            <Icon @click="remove(item)" type="md-trash" size="16"/>
                        </Tooltip>
                      </div>
                    </div>
                  </div>
                </Card>
              </div>
            </TabPane>
            <TabPane label="项目类" name="2">
              <div class="wrapper">
                <Card @click="showProject(item)" v-for="(item, i) in data2" :key="i" class="card">
                  <div class="project">
                    <img class="img" :src="item.url">
                    <div class="content">
                      <div class="title">{{item.title}}</div>
                      <div class="desc">{{item.description}}</div>
                      <div class="info">
                        <div class="time"><Time :time="new Date()" :interval="1"/></div>
                        <div class="avatar">
                          <div v-for="(iitem, i) in item.author" :key="i" class="item">
                            <Tooltip :content="iitem.name" placement="top">
                              <Avatar :src="iitem.avatar" size="small" class="c"/>
                            </Tooltip>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </Card>
              </div>
            </TabPane>
          </Tabs>
              
        </Card>
      </Col>
    </Row>

    <Modal
      v-model="picVisible"
      :title="picTitle"
      draggable>
      <img :src="file.url" alt="无效的图片链接" style="width: 100%;margin: 0 auto;display: block;">
      <div slot="footer">
        <span>文件类型：{{file.type}} 文件大小：{{file.msize}} 创建时间：{{file.createTime}}</span>
      </div>
    </Modal>

    <Modal
      v-model="videoVisible"
      :title="videoTitle"
      :width="800"
      draggable>
      <div id="dplayer"></div>
      <div slot="footer">
        <span>文件类型：{{file.type}} 文件大小：{{file.msize}} 创建时间：{{file.createTime}}</span>
      </div>
    </Modal>
  </div>
</template>

<script>
import "dplayer/dist/DPlayer.min.css";
import DPlayer from "dplayer";
export default {
  name: "card-list",
  data() {
    return {
      picVisible: false,
      videoVisible: false,
      picTitle: "",
      videoTitle: "",
      file: {},
      data: [
        {
          url:
            "https://gw.alipayobjects.com/zos/rmsportal/iXjVmWVHbCJAyqvDxdtx.png",
          name: "XBoot",
          type: "image",
          fkey: "EapBoot企业应用平台",
          size: 28659,
          createTime: "2018-10-22"
        },
        {
          url:
            "https://gw.alipayobjects.com/zos/rmsportal/iZBVOIhGJiAnhplqjvZW.png",
          name: "Exrick",
          type: "image",
          fkey: "EapBoot企业应用平台",
          size: 18659,
          createTime: "2018-10-22"
        },
        {
          url:
            "https://cloud.video.taobao.com//play/u/95292294/p/1/e/6/t/1/210877258798.mp4",
          name: "这是个可以发弹幕的视频播放器",
          type: "video",
          fkey: "EapBoot企业应用平台",
          size: 8659,
          createTime: "2018-10-22"
        },
        {
          url:
            "https://icarusion.gitee.io/iview/e1cf12c07bf6458992569e67927d767e.png",
          name: "XBoot",
          type: "txt",
          fkey: "EapBoot企业应用平台",
          size: 8659,
          createTime: "2018-10-22"
        }
      ],
      data2: [
        {
          url:
            "https://gw.alipayobjects.com/zos/rmsportal/iXjVmWVHbCJAyqvDxdtx.png",
          title: "XBoot",
          description: "EapBoot企业应用平台",
          author: [
            {
              name: "XBoot",
              avatar: "https://s1.ax1x.com/2018/05/19/CcdVQP.png"
            },
            {
              name: "Exrick",
              avatar: "https://i.loli.net/2018/10/14/5bc2ccaa3149c.png"
            }
          ],
          createTime: "2018-10-22"
        },
        {
          url:
            "https://gw.alipayobjects.com/zos/rmsportal/iZBVOIhGJiAnhplqjvZW.png",
          title: "Exrick",
          description: "EapBoot企业应用平台",
          author: [
            {
              name: "XBoot",
              avatar: "https://s1.ax1x.com/2018/05/19/CcdVQP.png"
            },
            {
              name: "Exrick",
              avatar: "https://i.loli.net/2018/10/14/5bc2ccaa3149c.png"
            }
          ],
          createTime: "2018-10-22"
        }
      ]
    };
  },
  methods: {
    init() {},
    showPic(v) {
      this.file = v;
      this.file.msize = ((v.size * 1.0) / (1024 * 1024)).toFixed(2) + " MB";
      this.picTitle = v.name + "(" + v.fkey + ")";
      this.picVisible = true;
    },
    showVideo(v) {
      const dp = new DPlayer({
        container: document.getElementById("dplayer"),
        screenshot: true,
        video: {
          url: v.url
        },
        danmaku: {
          id: v.fkey,
          api: "https://api.prprpr.me/dplayer/"
        }
      });
      this.file = v;
      this.file.msize = ((v.size * 1.0) / (1024 * 1024)).toFixed(2) + " MB";
      this.videoTitle = v.name + "(" + v.fkey + ")";
      this.videoVisible = true;
    },
    download(v) {
      window.open(
        v.url + "?attname=&response-content-type=application/octet-stream"
      );
    },
    copy(v) {
      this.$Message.info("点击了 " + v.name + " 的复制图标");
    },
    remove(v) {
      this.$Message.info("点击了 " + v.name + " 的删除图标");
    },
    rename(v) {
      this.$Message.info("点击了 " + v.name + " 的重命名图标");
    }
  },
  mounted() {
    this.init();
  }
};
</script>