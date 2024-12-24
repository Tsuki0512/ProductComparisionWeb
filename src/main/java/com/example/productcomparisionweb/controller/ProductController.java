package com.example.productcomparisionweb.controller;

import com.example.productcomparisionweb.common.Result;
import com.example.productcomparisionweb.mapper.ProductMapper;
import com.example.productcomparisionweb.mapper.PriceTrackingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;
import com.example.productcomparisionweb.entity.product;
import com.example.productcomparisionweb.WebAPI.JDSpider;
import com.example.productcomparisionweb.WebAPI.TBSpider;
import org.json.JSONArray;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

@RestController
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private PriceTrackingMapper priceTrackingMapper;

    @GetMapping("/search")
    public Result search(@RequestParam String keyword, 
                        @RequestParam(required = false) String jdCookie,
                        @RequestParam(required = false) String tbCookie,
                        @RequestParam(required = false) String platform,
                        @RequestParam(required = false) Integer uid) {
        try {
            // 使用 Set 来存储商品，避免重复
            Set<String> addedBarcodes = new HashSet<>();
            List<product> allProducts = new ArrayList<>();
            
            // 如果用户已登录，获取该用户收藏的所有商品ID
            List<Integer> trackedProductIds = new ArrayList<>();
            if (uid != null) {
                trackedProductIds = priceTrackingMapper.getTrackedProducts(uid);
            }
            
            // 1. 先从数据库中搜索
            List<product> dbProducts = productMapper.searchByKeyword(keyword, 
                platform != null && !platform.equals("all") ? platform.equals("jd") ? "京东" : "淘宝" : null);
            
            // 如果提供了uid，检查每个商品是否被收藏
            if (uid != null) {
                for (product p : dbProducts) {
                    boolean isTracked = trackedProductIds.contains(p.getPid());
                    p.setIsTracked(isTracked);
                    addedBarcodes.add(p.getBarcode() + "_" + p.getPlatform());
                }
            }
            
            allProducts.addAll(dbProducts);

            // 2. 从电商平台搜索新商品
            if ((platform == null || platform.equals("all") || platform.equals("jd")) && jdCookie != null) {
                JDSpider jdSpider = new JDSpider(jdCookie);
                JSONObject jdResult = jdSpider.requestSearch(keyword, jdCookie, 0, 30);
                
                if (jdResult.getInt("code") == 200) {
                    JSONArray jdProducts = jdResult.getJSONArray("data");
                    for (int i = 0; i < jdProducts.length(); i++) {
                        JSONObject item = jdProducts.getJSONObject(i);
                        String barcode = item.getString("barcode");
                        String key = barcode + "_京东";
                        
                        // 如果已经添加过这个商品，跳过
                        if (addedBarcodes.contains(key)) {
                            continue;
                        }
                        
                        // 检查商品是否已存在
                        product existingProduct = productMapper.findByBarcodeAndPlatform(barcode, "京东");
                        if (existingProduct != null) {
                            // 更新现有商品
                            existingProduct.setProductname(item.getString("productname"));
                            existingProduct.setCurrent_price(item.getDouble("current_price"));
                            existingProduct.setImage_url(item.getString("image_url"));
                            existingProduct.setSpecification(item.getString("link"));
                            existingProduct.setHistorical_prices(item.getString("historical_prices"));
                            productMapper.updateProduct(existingProduct);
                            
                            if (uid != null) {
                                existingProduct.setIsTracked(trackedProductIds.contains(existingProduct.getPid()));
                            }
                            allProducts.add(existingProduct);
                            addedBarcodes.add(key);
                        } else {
                            // 创建新商品
                            product newProduct = new product();
                            newProduct.setProductname(item.getString("productname"));
                            newProduct.setPlatform("京东");
                            newProduct.setCurrent_price(item.getDouble("current_price"));
                            newProduct.setImage_url(item.getString("image_url"));
                            newProduct.setBarcode(barcode);
                            newProduct.setSpecification(item.getString("link"));
                            newProduct.setHistorical_prices(item.getString("historical_prices"));
                            
                            productMapper.insertProduct(newProduct);
                            
                            if (uid != null) {
                                newProduct.setIsTracked(trackedProductIds.contains(newProduct.getPid()));
                            }
                            allProducts.add(newProduct);
                            addedBarcodes.add(key);
                        }
                    }
                }
            }

            // 淘宝搜索逻辑
            if ((platform == null || platform.equals("all") || platform.equals("tb")) && tbCookie != null) {
                TBSpider tbSpider = new TBSpider(tbCookie);
                JSONObject tbResult = tbSpider.requestSearch(keyword, tbCookie, 0, 30);
                
                JSONArray tbProducts = tbResult.getJSONArray("results");
                for (int i = 0; i < tbProducts.length(); i++) {
                    JSONObject item = tbProducts.getJSONObject(i);
                    String barcode = item.getString("id");
                    String key = barcode + "_淘宝";
                    
                    // 如果已经添加过这个商品，跳过
                    if (addedBarcodes.contains(key)) {
                        continue;
                    }
                    
                    // 检查商品是否已存在
                    product existingProduct = productMapper.findByBarcodeAndPlatform(barcode, "淘宝");
                    if (existingProduct != null) {
                        // 更新现有商品
                        existingProduct.setProductname(item.getString("name"));
                        existingProduct.setCurrent_price(Double.parseDouble(item.getString("price")));
                        existingProduct.setImage_url(item.getString("imageURL"));
                        existingProduct.setSpecification(item.getString("link"));
                        existingProduct.setHistorical_prices(
                            String.format("{\"current\": \"%s\", \"original\": \"%s\"}", 
                            item.getString("price"), item.getString("price"))
                        );
                        productMapper.updateProduct(existingProduct);
                        
                        if (uid != null) {
                            existingProduct.setIsTracked(trackedProductIds.contains(existingProduct.getPid()));
                        }
                        allProducts.add(existingProduct);
                        addedBarcodes.add(key);
                    } else {
                        // 创建新商品
                        product newProduct = new product();
                        newProduct.setProductname(item.getString("name"));
                        newProduct.setPlatform("淘宝");
                        newProduct.setCurrent_price(Double.parseDouble(item.getString("price")));
                        newProduct.setImage_url(item.getString("imageURL"));
                        newProduct.setBarcode(barcode);
                        newProduct.setSpecification(item.getString("link"));
                        newProduct.setHistorical_prices(
                            String.format("{\"current\": \"%s\", \"original\": \"%s\"}", 
                            item.getString("price"), item.getString("price"))
                        );
                        
                        productMapper.insertProduct(newProduct);
                        
                        if (uid != null) {
                            newProduct.setIsTracked(trackedProductIds.contains(newProduct.getPid()));
                        }
                        allProducts.add(newProduct);
                        addedBarcodes.add(key);
                    }
                }
            }
            
            return Result.success(allProducts);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("搜索失败：" + e.getMessage());
        }
    }
}
