package com.example.productcomparisionweb.controller;

import com.example.productcomparisionweb.common.Result;
import com.example.productcomparisionweb.entity.User;
import com.example.productcomparisionweb.mapper.ProductMapper;
import com.example.productcomparisionweb.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.json.JSONObject;
import com.example.productcomparisionweb.entity.product;
import com.example.productcomparisionweb.WebAPI.taobao_api;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductMapper productMapper;

    @GetMapping("/search")
    public Result search(@RequestParam String keyword) {
        try {
            // 调用淘宝API获取商品数据
            String url = "https://api-gw.onebound.cn/taobao/item_search/?key=t3858097232&secret=72323c5f&q=" +
                        keyword + "&start_price=0&end_price=0&page=1&cat=0&discount_only=&sort=&page_size=&seller_info=&nick=&ppath=&imgid=&filter=";
            
            JSONObject json = taobao_api.getRequestFromUrl(url);
            List<product> products = taobao_api.parseProductsFromJson(json);
            
            // 保存商品到数据库并获取pid
            for (product p : products) {
                productMapper.insertProduct(p);
                // 此时p.getPid()已经被自动设置为数据库生成的ID
                System.out.println(p.toString());
            }
            
            return Result.success(products);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("搜索失败：" + e.getMessage());
        }
    }
}
