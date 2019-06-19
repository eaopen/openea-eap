-- 文件上传
CREATE TABLE `sys_oss`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `url`         varchar(200) COMMENT 'URL地址',
    `create_date` datetime COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = `InnoDB`
  DEFAULT CHARACTER SET utf8 COMMENT ='文件上传';

