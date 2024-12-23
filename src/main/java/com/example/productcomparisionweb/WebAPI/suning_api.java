package com.example.productcomparisionweb.WebAPI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import com.example.productcomparisionweb.entity.product;

public class suning_api {
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return  sb.toString();
    }
    public static JSONObject postRequestFromUrl(String url, String body) throws IOException, JSONException {
        URL realUrl = new URL(url);
        URLConnection conn = realUrl.openConnection();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        PrintWriter out = new PrintWriter(conn.getOutputStream());
        out.print(body);
        out.flush();
        InputStream instream = conn.getInputStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(instream, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            instream.close();
        }
    }
    public static JSONObject getRequestFromUrl(String url) throws IOException, JSONException {
        URL realUrl = new URL(url);
        URLConnection conn = realUrl.openConnection();
        InputStream instream = conn.getInputStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(instream, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            instream.close();
        }
    }
    public static List<product> parseProductsFromJson(JSONObject json) {
        List<product> products = new ArrayList<>();
        
        try {
            System.out.println("Suning API Response: " + json.toString());
            
            if (json.has("error") && !json.getString("error").isEmpty()) {
                System.err.println("API Error: " + json.getString("error"));
                return products;
            }
            
            if (!json.has("items")) {
                System.err.println("No items field in response");
                return products;
            }
            
            JSONArray items;
            Object itemsObj = json.get("items");
            if (itemsObj instanceof JSONObject) {
                items = ((JSONObject) itemsObj).getJSONArray("item");
            } else if (itemsObj instanceof JSONArray) {
                items = (JSONArray) itemsObj;
            } else {
                System.err.println("Unexpected items format");
                return products;
            }
            
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                product p = new product();
                
                p.setProductname(item.getString("title"));
                p.setPlatform("苏宁");
                p.setCurrent_price(Double.parseDouble(item.getString("price")));
                p.setImage_url(item.getString("pic_url"));
                p.setBarcode(item.getString("item_id"));
                p.setSpecification(item.optString("area", ""));
                
                JSONObject priceHistory = new JSONObject();
                priceHistory.put("current", item.getString("price"));
                priceHistory.put("original", item.optString("original_price", item.getString("price")));
                p.setHistorical_prices(priceHistory.toString());
                
                products.add(p);
            }
        } catch (JSONException e) {
            System.err.println("JSON Parsing Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return products;
    }
    public static void main(String[] args) throws IOException, JSONException {
        // 请求示例 url 默认请求参数已经URL编码处理
        String url = "https://api-gw.onebound.cn/suning/item_search/?key=t3858097232&secret=72323c5f&q=鞋子&start_price=&end_price=&page=&cat=&discount_only=&sort=&page_size=&seller_info=&nick=&ppath=";
        JSONObject json = getRequestFromUrl(url);
        System.out.println(json.toString());
    }
}