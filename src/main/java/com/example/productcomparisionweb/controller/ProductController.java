package com.example.productcomparisionweb.controller;

import com.example.productcomparisionweb.common.Result;
import com.example.productcomparisionweb.mapper.ProductMapper;
import com.example.productcomparisionweb.mapper.PriceTrackingMapper;
import com.example.productcomparisionweb.mapper.UserMapper;
import com.example.productcomparisionweb.service.EmailService;
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
import java.util.Map;
import java.util.HashMap;

@RestController
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private PriceTrackingMapper priceTrackingMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private EmailService emailService;

    @GetMapping("/search")
    public Result search(@RequestParam String keyword, 
                        @RequestParam(required = false) String jdCookie,
                        @RequestParam(required = false) String tbCookie,
                        @RequestParam(required = false) String platform,
                        @RequestParam(required = false) Integer uid) {
        try {
            // 使用 Map 来存储最新的商品信息，key 为 pid
            Map<Integer, product> productMap = new HashMap<>();
            List<product> allProducts = new ArrayList<>();
            
            // 如果用户已登录，获取该用户收藏的所有商品ID
            List<Integer> trackedProductIds = new ArrayList<>();
            if (uid != null) {
                trackedProductIds = priceTrackingMapper.getTrackedProducts(uid);
            }
            
            // 1. 先从数据库中搜索
            List<product> dbProducts = productMapper.searchByKeyword(keyword, 
                platform != null && !platform.equals("all") ? platform.equals("jd") ? "京东" : "淘宝" : null);
            
            // 将数据库中的商品添加到 Map
            for (product p : dbProducts) {
                productMap.put(p.getPid(), p);
            }

            // 2. 从电商平台搜索并更新
            if ((platform == null || platform.equals("all") || platform.equals("jd")) && jdCookie != null) {
                JDSpider jdSpider = new JDSpider(jdCookie);
                JSONObject jdResult = jdSpider.requestSearch(keyword, jdCookie, 0, 30);
                
                if (jdResult.getInt("code") == 200) {
                    JSONArray jdProducts = jdResult.getJSONArray("data");
                    for (int i = 0; i < jdProducts.length(); i++) {
                        JSONObject item = jdProducts.getJSONObject(i);
                        String barcode = item.getString("barcode");
                        String key = barcode + "_京东";

                        // 检查商品是否已存在
                        System.out.println("Searching for product with barcode: " + barcode + ", platform: 京东");
                        product existingProduct = productMapper.findByBarcodeAndPlatform(barcode, "京东");
                        if (existingProduct == null) {
                            System.out.println("No existing product found in database");
                        } else {
                            System.out.println("Found product: pid=" + existingProduct.getPid() + 
                                ", barcode=" + existingProduct.getBarcode() + 
                                ", platform=" + existingProduct.getPlatform());
                        }
                        if (existingProduct != null) {
                            System.out.println("Found existing product in database, pid: " + existingProduct.getPid());
                            System.out.println("Original historical prices: " + existingProduct.getHistorical_prices());
                            // 更新现有商品
                            existingProduct.setProductname(item.getString("productname"));
                            existingProduct.setCurrent_price(item.getDouble("current_price"));
                            existingProduct.setImage_url(item.getString("image_url"));
                            existingProduct.setSpecification(item.getString("link"));
                            updateProductHistoricalPrices(existingProduct, item.getDouble("current_price"));
                            System.out.println("Updated product historical prices: " + existingProduct.getHistorical_prices());
                            
                            int updateResult = productMapper.updateProduct(existingProduct);
                            System.out.println("Update result: " + (updateResult > 0 ? "success" : "failed"));
                            
                            if (uid != null) {
                                existingProduct.setIsTracked(trackedProductIds.contains(existingProduct.getPid()));
                            }
                            productMap.put(existingProduct.getPid(), existingProduct);
                        } else {
                            // 创建新商品
                            product newProduct = new product();
                            newProduct.setProductname(item.getString("productname"));
                            newProduct.setPlatform("京东");
                            newProduct.setCurrent_price(item.getDouble("current_price"));
                            newProduct.setImage_url(item.getString("image_url"));
                            newProduct.setBarcode(barcode);
                            newProduct.setSpecification(item.getString("link"));
                            updateProductHistoricalPrices(newProduct, item.getDouble("current_price"));
                            
                            productMapper.insertProduct(newProduct);
                            
                            if (uid != null) {
                                newProduct.setIsTracked(trackedProductIds.contains(newProduct.getPid()));
                            }
                            productMap.put(newProduct.getPid(), newProduct);
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
                    
                    // 检查商品是否已存在
                    product existingProduct = productMapper.findByBarcodeAndPlatform(barcode, "淘宝");
                    if (existingProduct != null) {
                        // 更新现有商品
                        existingProduct.setProductname(item.getString("name"));
                        existingProduct.setCurrent_price(Double.parseDouble(item.getString("price")));
                        existingProduct.setImage_url(item.getString("imageURL"));
                        existingProduct.setSpecification(item.getString("link"));
                        updateProductHistoricalPrices(existingProduct, Double.parseDouble(item.getString("price")));
                        productMapper.updateProduct(existingProduct);
                        
                        if (uid != null) {
                            existingProduct.setIsTracked(trackedProductIds.contains(existingProduct.getPid()));
                        }
                        productMap.put(existingProduct.getPid(), existingProduct);
                    } else {
                        // 创建新商品
                        product newProduct = new product();
                        newProduct.setProductname(item.getString("name"));
                        newProduct.setPlatform("淘宝");
                        newProduct.setCurrent_price(Double.parseDouble(item.getString("price")));
                        newProduct.setImage_url(item.getString("imageURL"));
                        newProduct.setBarcode(barcode);
                        newProduct.setSpecification(item.getString("link"));
                        updateProductHistoricalPrices(newProduct, Double.parseDouble(item.getString("price")));
                        
                        productMapper.insertProduct(newProduct);
                        
                        if (uid != null) {
                            newProduct.setIsTracked(trackedProductIds.contains(newProduct.getPid()));
                        }
                        productMap.put(newProduct.getPid(), newProduct);
                    }
                }
            }
            
            // 最后将 Map 中的所有商品转换为列表返回
            allProducts = new ArrayList<>(productMap.values());
            
            return Result.success(allProducts);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("搜索失败：" + e.getMessage());
        }
    }

    private void updateProductHistoricalPrices(product existingProduct, double newPrice) {
        try {
            String currentTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new java.util.Date());
            JSONObject history;
            
            System.out.println("Updating historical prices for product: " + existingProduct.getPid());
            System.out.println("Current price to add: " + newPrice);
            
            // 解析现有的历史价格数据
            if (existingProduct.getHistorical_prices() != null && !existingProduct.getHistorical_prices().isEmpty()) {
                System.out.println("Found existing historical prices");
                history = new JSONObject(existingProduct.getHistorical_prices());
            } else {
                System.out.println("No existing historical prices, creating new");
                history = new JSONObject();
            }
            
            System.out.println("Current historical prices before update: " + history.toString());
            
            // 判断是否需要更新价格历史
            boolean shouldUpdate = false;
            double lastPrice = 0;
            
            if (history.isEmpty()) {
                shouldUpdate = true;
            } else {
                // 获取最近一次的价格记录
                String lastTime = null;
                
                // 找到最近的时间记录
                for (String time : history.keySet()) {
                    if (lastTime == null || time.compareTo(lastTime) > 0) {
                        lastTime = time;
                        lastPrice = Double.parseDouble(history.getString(time));
                    }
                }
                
                // 检查价格是否变化
                if (Math.abs(lastPrice - newPrice) > 0.01) {
                    shouldUpdate = true;
                    System.out.println("Price changed from " + lastPrice + " to " + newPrice);
                    
                    // 如果价格下降，发送降价提醒
                    if (newPrice < lastPrice) {
                        sendPriceDropNotification(existingProduct, lastPrice, newPrice);
                    }
                } else {
                    // 检查时间间隔是否超过3天
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    java.util.Date lastDate = sdf.parse(lastTime);
                    java.util.Date currentDate = new java.util.Date();
                    long diffInDays = (currentDate.getTime() - lastDate.getTime()) / (1000 * 60 * 60 * 24);
                    
                    if (diffInDays >= 3) {
                        shouldUpdate = true;
                        System.out.println("Time interval exceeded 3 days: " + diffInDays + " days");
                    }
                }
            }
            
            // 只在需要更新时添加新的价格记录
            if (shouldUpdate) {
                history.put(currentTime, String.format("%.2f", newPrice));
                existingProduct.setHistorical_prices(history.toString());
                System.out.println("Updated historical prices after update: " + history.toString());
            } else {
                System.out.println("No update needed: price unchanged and within 3 days");
            }

        } catch (Exception e) {
            System.out.println("Error updating historical prices: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 发送降价提醒邮件
     */
    private void sendPriceDropNotification(product product, double oldPrice, double newPrice) {
        try {
            System.out.println("\n=== Starting price drop notification process ===");
            System.out.println("Product: " + product.getProductname());
            System.out.println("Price dropped from " + oldPrice + " to " + newPrice);
            
            // 获取关注该商品的所有用户
            List<Integer> userIds = priceTrackingMapper.getUsersByProduct(product.getPid());
            System.out.println("Found " + userIds.size() + " users tracking this product");
            
            for (Integer uid : userIds) {
                System.out.println("\nProcessing user ID: " + uid);
                // 获取用户邮箱
                String email = userMapper.getEmailByUid(uid);
                System.out.println("User email: " + (email != null ? email : "null"));
                
                if (email != null && !email.isEmpty()) {
                    // 构建邮件内容
                    String subject = "商品降价提醒";
                    String content = String.format(
                        "您关注的商品 %s 降价了！\n\n" +
                        "原价：¥%.2f\n" +
                        "现价：¥%.2f\n" +
                        "降价：¥%.2f\n\n" +
                        "商品链接：%s\n",
                        product.getProductname(),
                        oldPrice,
                        newPrice,
                        oldPrice - newPrice,
                        product.getSpecification()
                    );
                    
                    System.out.println("Attempting to send email with content:");
                    System.out.println("Subject: " + subject);
                    System.out.println("Content: \n" + content);
                    
                    // 发送邮件
                    boolean emailSent = emailService.sendSimpleEmail(email, subject, content);
                    System.out.println("Email send result: " + (emailSent ? "SUCCESS" : "FAILED"));
                    
                    if (emailSent) {
                        System.out.println("Successfully sent price drop notification to user " + uid + " at " + email);
                    } else {
                        System.out.println("Failed to send price drop notification to user " + uid + " at " + email);
                    }
                } else {
                    System.out.println("Skipping user " + uid + " - no valid email address found");
                }
            }
            System.out.println("=== Price drop notification process completed ===\n");
        } catch (Exception e) {
            System.out.println("\n=== Error in price drop notification process ===");
            System.out.println("Product ID: " + product.getPid());
            System.out.println("Product name: " + product.getProductname());
            System.out.println("Error message: " + e.getMessage());
            System.out.println("Stack trace:");
            e.printStackTrace();
            System.out.println("=== Error details end ===\n");
        }
    }

    @GetMapping("/test/find")
    public Result testFind(@RequestParam String barcode, @RequestParam String platform) {
        try {
            System.out.println("Testing findByBarcodeAndPlatform");
            System.out.println("Barcode: " + barcode);
            System.out.println("Platform: " + platform);
            
            product result = productMapper.findByBarcodeAndPlatform(barcode, platform);
            
            if (result != null) {
                System.out.println("Found product: " + result.toString());
                return Result.success(result);
            } else {
                System.out.println("No product found");
                return Result.error("No product found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("Error: " + e.getMessage());
        }
    }
}
