# Docker Build & Up

目标: 快速部署体验系统，帮助了解系统之间的依赖关系。

## 功能文件列表

```text
.
├── Docker-HOWTO.md
├── docker-compose.yml             #eap单体版本
├── docker-compose-eoa.yml         #eoa单体版本
├── docker-compose-cloud.yml       #eoa cloud版本
├── docker.env
├── eap-server
│   ├── Dockerfile
│   └── nginx.conf
├── eoa-server
│   ├── Dockerfile
│   └── nginx.conf
└── eap-ui-admin
    ├── .dockerignore
    └── Dockerfile
```

## Maven build (Optional)

```shell
# 创建maven缓存volume
docker volume create --name eap-maven-repo

docker run -it --rm --name eap-maven \
    -v eap-maven-repo:/root/.m2 \
    -v $PWD:/usr/src/mymaven \
    -w /usr/src/mymaven \
    maven mvn clean install package '-Dmaven.test.skip=true'
```

## Docker Compose Build

```shell
docker compose --env-file docker.env build
```

## Docker Compose Up

```shell
docker compose --env-file docker.env up -d
```

第一次执行，由于数据库未初始化，因此eap-server容器会运行失败。执行如下命令初始化数据库：

```shell
docker exec -i eap-mysql \
    sh -c 'exec mysql -uroot -p"$MYSQL_ROOT_PASSWORD" --default-character-set=utf8mb4 ruoyi-vue-pro' \
    < ./sql/mysql/eap-db.sql
```

注意：这里用docker compose exec 会出现 `the input device is not a TTY` 报错

## Server:Port

- admin: http://localhost:8080
- API: http://localhost:48080
- mysql: root/123456, port: 3308
- redis: port: 6379
