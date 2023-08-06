package org.openea.eap.extj.util.wxutil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SendPostUtil {
    public SendPostUtil() {
    }

    public static String sendGet(String url, String param, Map<String, String> header) throws IOException {
        String result = "";
        BufferedReader in = null;
        String urlNameString = url + "?" + param;
        URL realUrl = new URL(urlNameString);
        URLConnection connection = realUrl.openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(15000);
        if (header != null) {
            Iterator<Map.Entry<String, String>> it = header.entrySet().iterator();

            while(it.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry)it.next();
                System.out.println((String)entry.getKey() + ":" + (String)entry.getValue());
                connection.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
            }
        }

        connection.setRequestProperty("accept", "*/*");
        connection.setRequestProperty("connection", "Keep-Alive");
        connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        connection.connect();
        Map<String, List<String>> map = connection.getHeaderFields();
        Iterator var12 = map.keySet().iterator();

        while(var12.hasNext()) {
            String key = (String)var12.next();
            System.out.println(key + "--->" + map.get(key));
        }

        String line;
        for(in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8")); (line = in.readLine()) != null; result = result + line) {
        }

        if (in != null) {
            in.close();
        }

        return result;
    }

    public static String sendPost(String url, String param, Map<String, String> header) throws IOException {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        URL realUrl = new URL(url);
        URLConnection conn = realUrl.openConnection();
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);
        if (header != null) {
            Iterator var8 = header.entrySet().iterator();

            while(var8.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry)var8.next();
                conn.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
            }
        }

        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        out = new PrintWriter(conn.getOutputStream());
        out.print(param);
        out.flush();

        String line;
        for(in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf8")); (line = in.readLine()) != null; result = result + line) {
        }

        if (out != null) {
            out.close();
        }

        if (in != null) {
            in.close();
        }

        return result;
    }
}