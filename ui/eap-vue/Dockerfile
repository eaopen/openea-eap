FROM node:10 as builder
ADD . /app/
WORKDIR /app
RUN npm config set unsafe-perm true  && \
    npm config set registry https://registry.npm.taobao.org  && \
    npm install node-sass@latest && \
    npm install  && \
    npm run build

FROM nginx:1.15-alpine
WORKDIR /app
COPY --from=builder /app/dist /usr/share/nginx/html
