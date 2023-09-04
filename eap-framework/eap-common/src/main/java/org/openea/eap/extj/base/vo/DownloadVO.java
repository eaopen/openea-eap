package org.openea.eap.extj.base.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DownloadVO {

    @Schema(
            description = "名称"
    )
    private String name;
    @Schema(
            description = "请求接口"
    )
    private String url;

    public static DownloadVOBuilder builder() {
        return new DownloadVOBuilder();
    }

    public DownloadVO(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public static class DownloadVOBuilder {
        private String name;
        private String url;

        DownloadVOBuilder() {
        }

        public DownloadVOBuilder name(String name) {
            this.name = name;
            return this;
        }

        public DownloadVOBuilder url(String url) {
            this.url = url;
            return this;
        }

        public DownloadVO build() {
            return new DownloadVO(this.name, this.url);
        }

        public String toString() {
            return "DownloadVO.DownloadVOBuilder(name=" + this.name + ", url=" + this.url + ")";
        }
    }
}
