package com.example.productcomparisionweb.controller;

import com.example.productcomparisionweb.common.Result;
import com.example.productcomparisionweb.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;
import com.example.productcomparisionweb.entity.product;
import com.example.productcomparisionweb.WebAPI.JDSpider;
import com.example.productcomparisionweb.WebAPI.TBSpider;
import org.json.JSONArray;

import java.util.List;
import java.util.ArrayList;

@RestController
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductMapper productMapper;

    @GetMapping("/search")
    public Result search(@RequestParam String keyword, 
                        @RequestParam(required = false) String jdCookie,
                        @RequestParam(required = false) String tbCookie) {
        try {
            System.out.println("收到搜索请求 - 关键词: " + keyword); // 添加调试日志
            List<product> allProducts = new ArrayList<>();
            
            // 使用京东爬虫
            if (jdCookie != null && !jdCookie.isEmpty()) {
                System.out.println("开始京东搜索"); // 添加调试日志
                JDSpider jdSpider = new JDSpider(jdCookie);
                JSONObject jdResult = jdSpider.requestSearch(keyword, jdCookie, 0, 30);
                System.out.println("京东搜索结果: " + jdResult.toString(2)); // 添加调试日志
                
                if (jdResult.getInt("code") == 200) {
                    JSONArray jdProducts = jdResult.getJSONArray("data");
                    for (int i = 0; i < jdProducts.length(); i++) {
                        JSONObject item = jdProducts.getJSONObject(i);
                        product p = new product();
                        p.setProductname(item.getString("productname"));
                        p.setPlatform(item.getString("platform"));
                        p.setCurrent_price(item.getDouble("current_price"));
                        p.setImage_url(item.getString("image_url"));
                        p.setBarcode(item.getString("barcode"));
                        p.setHistorical_prices(item.getString("historical_prices"));
                        p.setSpecification(item.getString("link"));
                        allProducts.add(p);
                    }
                }
            }

            // 使用淘宝爬虫
            if (tbCookie != null && !tbCookie.isEmpty()) {
                System.out.println("开始淘宝搜索"); // 添加调试日志
                TBSpider tbSpider = new TBSpider(tbCookie);
                JSONObject tbResult = tbSpider.requestSearch(keyword, tbCookie, 0, 30);
                System.out.println("淘宝搜索结果: " + tbResult.toString(2)); // 添加调试日志
                
                JSONArray tbProducts = tbResult.getJSONArray("results");
                for (int i = 0; i < tbProducts.length(); i++) {
                    JSONObject item = tbProducts.getJSONObject(i);
                    product p = new product();
                    p.setProductname(item.getString("name"));
                    p.setPlatform("淘宝");
                    p.setCurrent_price(Double.parseDouble(item.getString("price")));
                    p.setImage_url(item.getString("imageURL"));
                    p.setBarcode(item.getString("id"));
                    p.setSpecification(item.getString("link"));
                    allProducts.add(p);
                }
            }
            
            System.out.println("总共找到商品数量: " + allProducts.size()); // 添加调试日志
            
            // 保存所有商品到数据库并获取生成的 pid
            for (product p : allProducts) {
                productMapper.insertProduct(p);
                // 获取数据库生成的 pid
                int generatedPid = p.getPid();
                System.out.println("Generated PID: " + generatedPid);
            }
            
            return Result.success(allProducts);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("搜索失败：" + e.getMessage());
        }
    }
}
