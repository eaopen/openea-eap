package org.openea.eap.extj.util.wxutil;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.net.ssl.SSLContext;


import org.openea.eap.extj.util.StringUtil;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.util.ServletUtil;
import org.openea.eap.extj.util.XSSEscape;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.openea.eap.framework.common.util.spring.EapAppUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

public class HttpUtil {
    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);
    private static PoolingHttpClientConnectionManager connectionManager = null;
    private static RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).setConnectionRequestTimeout(3000).build();

    private HttpUtil() {
        throw new IllegalAccessError("工具类不能实例化");
    }

    public static CloseableHttpClient getHttpClient() {
        return getHttpClientBuilder().build();
    }

    public static CloseableHttpClient getHttpClient(SSLContext sslContext) {
        return getHttpClientBuilder(sslContext).build();
    }

    public static HttpClientBuilder getHttpClientBuilder() {
        return HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfig);
    }

    public static HttpClientBuilder getHttpClientBuilder(SSLContext sslContext) {
        return sslContext != null ? getHttpClientBuilder().setSSLContext(sslContext) : getHttpClientBuilder();
    }

    public static String sendHttpPost(String httpUrl, SSLContext sslContext) {
        HttpPost httpPost = new HttpPost(httpUrl);
        return sendHttpPost(httpPost, sslContext);
    }

    public static String sendHttpPost(String httpUrl) {
        HttpPost httpPost = new HttpPost(httpUrl);
        return sendHttpPost((HttpPost)httpPost, (SSLContext)null);
    }

    public static String sendHttpPost(String httpUrl, String params) {
        return sendHttpPost(httpUrl, (String)params, (SSLContext)null);
    }

    public static String sendHttpPost(String httpUrl, String params, SSLContext sslContext) {
        HttpPost httpPost = new HttpPost(httpUrl);

        try {
            StringEntity stringEntity = new StringEntity(params, "UTF-8");
            stringEntity.setContentType("application/x-www-form-urlencoded");
            httpPost.setEntity(stringEntity);
        } catch (Exception var5) {
            log.error(var5.getMessage(), var5);
        }

        return sendHttpPost(httpPost, sslContext);
    }

    public static String sendHttpPost(String httpUrl, Map<String, String> maps) {
        return sendHttpPost(httpUrl, (Map)maps, (SSLContext)null);
    }

    public static String sendHttpPost(String httpUrl, Map<String, String> maps, SSLContext sslContext) {
        HttpPost httpPost = wrapHttpPost(httpUrl, maps);
        return sendHttpPost((HttpPost)httpPost, (SSLContext)null);
    }

    public static HttpPost wrapHttpPost(String httpUrl, Map<String, String> maps) {
        HttpPost httpPost = new HttpPost(httpUrl);
        List<NameValuePair> nameValuePairs = new ArrayList();
        Iterator var4 = maps.entrySet().iterator();

        while(var4.hasNext()) {
            Map.Entry<String, String> m = (Map.Entry)var4.next();
            nameValuePairs.add(new BasicNameValuePair((String)m.getKey(), (String)m.getValue()));
        }

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (Exception var6) {
            log.error(var6.getMessage(), var6);
        }

        return httpPost;
    }

    public static String sendHttpPost(String httpUrl, File file) {
        return sendHttpPost(httpUrl, (Map)ImmutableMap.of("media", file), (Map)null, (SSLContext)null);
    }

    public static String sendHttpPost(String httpUrl, File file, Map<String, String> maps) {
        return sendHttpPost(httpUrl, (Map)ImmutableMap.of("media", file), maps, (SSLContext)null);
    }

    public static String sendHttpPost(String httpUrl, List<File> fileLists, Map<String, String> maps) {
        return sendHttpPost(httpUrl, (List)fileLists, maps, (SSLContext)null);
    }

    public static String sendHttpPost(String httpUrl, Map<String, File> fileMap, Map<String, String> maps) {
        return sendHttpPost(httpUrl, (Map)fileMap, maps, (SSLContext)null);
    }

    public static String sendHttpPost(String httpUrl, List<File> fileLists, Map<String, String> maps, SSLContext sslContext) {
        Map<String, File> fileMap = new HashMap(16);
        if (fileLists == null || fileLists.isEmpty()) {
            Iterator var5 = fileLists.iterator();

            while(var5.hasNext()) {
                File file = (File)var5.next();
                fileMap.put("media", file);
            }
        }

        return sendHttpPost(httpUrl, (Map)fileMap, maps, sslContext);
    }

    public static String sendHttpPost(String httpUrl, Map<String, File> fileMap, Map<String, String> maps, SSLContext sslContext) {
        HttpPost httpPost = new HttpPost(httpUrl);
        MultipartEntityBuilder meBuilder = MultipartEntityBuilder.create();
        Iterator var6;
        Map.Entry m;
        if (null != maps) {
            var6 = maps.entrySet().iterator();

            while(var6.hasNext()) {
                m = (Map.Entry)var6.next();
                meBuilder.addPart((String)m.getKey(), new StringBody((String)m.getValue(), ContentType.TEXT_PLAIN));
            }
        }

        if (null != fileMap) {
            var6 = fileMap.entrySet().iterator();

            while(var6.hasNext()) {
                m = (Map.Entry)var6.next();
                FileBody fileBody = new FileBody((File)m.getValue());
                meBuilder.addPart((String)m.getKey(), fileBody);
            }
        }

        HttpEntity reqEntity = meBuilder.build();
        httpPost.setEntity(reqEntity);
        return sendHttpPost(httpPost, sslContext);
    }

    public static String sendHttpPost(HttpPost httpPost) {
        return sendHttpPost((HttpPost)httpPost, (SSLContext)null);
    }

    public static String sendHttpPost(HttpPost httpPost, SSLContext sslConext) {
        CloseableHttpClient httpClient = getHttpClient(sslConext);
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        String responseContent = null;

        try {
            response = httpClient.execute(httpPost);
            entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception var15) {
            log.error(var15.getMessage(), var15);
        } finally {
            try {
                if (entity != null) {
                    EntityUtils.consumeQuietly(entity);
                }

                if (response != null) {
                    response.close();
                }
            } catch (Exception var14) {
                log.error(var14.getMessage(), var14);
            }

        }

        return responseContent;
    }

    public static String sendHttpGet(String httpUrl) {
        return sendHttpGet((String)httpUrl, (SSLContext)null);
    }

    public static String sendHttpGet(String httpUrl, SSLContext sslConext) {
        HttpGet httpGet = new HttpGet(httpUrl);
        return sendHttpGet(httpGet, sslConext);
    }

    public static String sendHttpGet(HttpGet httpGet) {
        return sendHttpGet((HttpGet)httpGet, (SSLContext)null);
    }

    public static String sendHttpGet(HttpGet httpGet, SSLContext sslConext) {
        CloseableHttpClient httpClient = getHttpClient(sslConext);
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        String responseContent = null;

        try {
            response = httpClient.execute(httpGet);
            entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, "UTF-8");
        } catch (Exception var15) {
            log.error(var15.getMessage(), var15);
        } finally {
            try {
                if (entity != null) {
                    EntityUtils.consumeQuietly(entity);
                }

                if (response != null) {
                    response.close();
                }
            } catch (Exception var14) {
                log.error(var14.getMessage(), var14);
            }

        }

        return responseContent;
    }

    public static String sendHttpHeaderGet(String httpUrl, Map<String, String> headers) {
        HttpGet httpGet = new HttpGet(httpUrl);
        Iterator var3 = headers.entrySet().iterator();

        while(var3.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry)var3.next();
            String key = ((String)entry.getKey()).toString();
            String value = ((String)entry.getValue()).toString();
            httpGet.setHeader(key, value);
        }

        return sendHttpGet((HttpGet)httpGet, (SSLContext)null);
    }

    public static File sendHttpGetFile(String httpUrl, File file) {
        if (file == null) {
            return null;
        } else {
            HttpGet httpGet = new HttpGet(httpUrl);
            CloseableHttpClient httpClient = getHttpClient();
            CloseableHttpResponse response = null;
            HttpEntity entity = null;
            InputStream inputStream = null;
            FileOutputStream fileOutputStream = null;

            try {
                response = httpClient.execute(httpGet);
                entity = response.getEntity();
                inputStream = entity.getContent();
                fileOutputStream = new FileOutputStream(file);
                int len = 0;
                byte[] buf = new byte[1024];
                while((len = inputStream.read(buf, 0, 1024)) != -1) {
                    fileOutputStream.write(buf, 0, len);
                }
            } catch (Exception var18) {
                log.error(var18.getMessage(), var18);
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }

                    if (inputStream != null) {
                        inputStream.close();
                    }

                    if (entity != null) {
                        EntityUtils.consumeQuietly(entity);
                    }

                    if (response != null) {
                        response.close();
                    }
                } catch (Exception var17) {
                    log.error(var17.getMessage(), var17);
                }

            }

            return file;
        }
    }

    public static File sendHttpPostFile(String httpUrl, Map<String, String> maps, File file) {
        if (file == null) {
            return null;
        } else {
            HttpPost httpPost = wrapHttpPost(httpUrl, maps);
            CloseableHttpClient httpClient = getHttpClient();
            CloseableHttpResponse response = null;
            HttpEntity entity = null;
            InputStream inputStream = null;
            FileOutputStream fileOutputStream = null;

            try {
                response = httpClient.execute(httpPost);
                entity = response.getEntity();
                inputStream = entity.getContent();
                fileOutputStream = new FileOutputStream(file);
                int len = 0;
                byte[] buf = new byte[1024];
                while((len = inputStream.read(buf, 0, 1024)) != -1) {
                    fileOutputStream.write(buf, 0, len);
                }
            } catch (Exception var19) {
                log.error(var19.getMessage(), var19);
            } finally {
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }

                    if (inputStream != null) {
                        inputStream.close();
                    }

                    if (entity != null) {
                        EntityUtils.consumeQuietly(entity);
                    }

                    if (response != null) {
                        response.close();
                    }
                } catch (Exception var18) {
                    log.error(var18.getMessage(), var18);
                }

            }

            return file;
        }
    }

    public static boolean isWxError(JSONObject jsonObject) {
        return null == jsonObject || jsonObject.getIntValue("errcode") != 0;
    }

    public static JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr) {
        return httpRequest(requestUrl, requestMethod, outputStr, (String[])null);
    }

    public static JSONObject httpRequest(String requestUrl, String requestMethod, String outputStr, String... token) {
        JSONObject jsonObject = null;

        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(requestMethod);
            conn.setConnectTimeout(50000);
            conn.setReadTimeout(60000);
            conn.setRequestProperty("Content-Type", "application/json");
            if (StringUtil.isNotEmpty(token)) {
                conn.setRequestProperty("Authorization", XSSEscape.escape(token[0]));
                if (token.length > 1 && StringUtil.isNotEmpty(token[1])) {
                    Map<String, Object> requestHeader = JsonUtil.stringToMap(token[1]);
                    Iterator var8 = requestHeader.keySet().iterator();

                    while(var8.hasNext()) {
                        String field = (String)var8.next();
                        conn.setRequestProperty(field, requestHeader.get(field) + "");
                    }
                }
            }

            String agent = ServletUtil.getUserAgent();
            if (StringUtil.isNotEmpty(agent)) {
                conn.setRequestProperty("User-Agent", agent);
            }

            if (StringUtil.isNotEmpty(outputStr)) {
                OutputStream outputStream = conn.getOutputStream();

                try {
                    outputStream.write(outputStr.getBytes("UTF-8"));
                    outputStream.close();
                } finally {
                    if (Collections.singletonList(outputStream).get(0) != null) {
                        outputStream.close();
                    }

                }
            }

            InputStream inputStream = conn.getInputStream();

            try {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");

                try {
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    try {
                        String str = null;
                        StringBuffer buffer = new StringBuffer();

                        while((str = bufferedReader.readLine()) != null) {
                            buffer.append(str);
                        }

                        bufferedReader.close();
                        inputStreamReader.close();
                        inputStream.close();
                        conn.disconnect();
                        jsonObject = JSONObject.parseObject(buffer.toString());
                    } finally {
                        if (Collections.singletonList(bufferedReader).get(0) != null) {
                            bufferedReader.close();
                        }

                    }
                } finally {
                    if (Collections.singletonList(inputStreamReader).get(0) != null) {
                        inputStreamReader.close();
                    }

                }
            } finally {
                if (Collections.singletonList(inputStream).get(0) != null) {
                    inputStream.close();
                }

            }
        } catch (Exception var40) {
            log.error(var40.getMessage());
        }

        return jsonObject;
    }

    public static boolean httpCronRequest(String requestUrl, final String requestMethod, final String outputStr, final String token) {
        boolean falg = false;

        try {
            final URL url = new URL(requestUrl);
            final HttpURLConnection[] conn = new HttpURLConnection[]{null};
            Callable<String> task = new Callable<String>() {
                public String call() throws Exception {
                    try {
                        conn[0] = (HttpURLConnection)url.openConnection();
                    } catch (Exception var30) {
                        HttpUtil.log.error(var30.getMessage());
                    }

                    conn[0].setDoOutput(true);
                    conn[0].setDoInput(true);
                    conn[0].setUseCaches(false);
                    conn[0].setRequestMethod(requestMethod);
                    conn[0].setRequestProperty("Content-Type", "application/json");
                    if (StringUtil.isNotEmpty(token)) {
                        conn[0].setRequestProperty("Authorization", token);
                    }

                    if (null != outputStr) {
                        OutputStream outputStream = conn[0].getOutputStream();

                        try {
                            outputStream.write(outputStr.getBytes("UTF-8"));
                            outputStream.close();
                        } finally {
                            if (Collections.singletonList(outputStream).get(0) != null) {
                                outputStream.close();
                            }

                        }
                    }

                    InputStream inputStream = conn[0].getInputStream();

                    try {
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");

                        try {
                            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                            try {
                                String str = null;
                                StringBuffer buffer = new StringBuffer();

                                while((str = bufferedReader.readLine()) != null) {
                                    buffer.append(str);
                                }

                                bufferedReader.close();
                                inputStreamReader.close();
                                inputStream.close();
                                conn[0].disconnect();
                                String var6 = "url连接ok";
                                return var6;
                            } finally {
                                if (Collections.singletonList(bufferedReader).get(0) != null) {
                                    bufferedReader.close();
                                }

                            }
                        } finally {
                            if (Collections.singletonList(inputStreamReader).get(0) != null) {
                                inputStreamReader.close();
                            }

                        }
                    } finally {
                        if (Collections.singletonList(inputStream).get(0) != null) {
                            inputStream.close();
                        }

                    }
                }
            };
            ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) EapAppUtil.getBean(ThreadPoolTaskExecutor.class);
            Future<String> future = executor.submit(task);

            try {
                String rst = (String)future.get(3L, TimeUnit.SECONDS);
                if ("url连接ok".equals(rst)) {
                    falg = true;
                }
            } catch (TimeoutException var11) {
                log.error("连接url超时");
            } catch (Exception var12) {
                log.error("获取异常," + var12.getMessage());
            }
        } catch (MalformedURLException var13) {
            var13.printStackTrace();
        }

        return falg;
    }

    static {
        SSLContext sslcontext = SSLContexts.createSystemDefault();
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE).register("https", new SSLConnectionSocketFactory(sslcontext)).build();
        connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setMaxTotal(1000);
        connectionManager.setDefaultMaxPerRoute(200);
    }
}
