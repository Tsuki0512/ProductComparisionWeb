package com.example.productcomparisionweb.controller;

import com.example.productcomparisionweb.common.Result;
import com.example.productcomparisionweb.mapper.PriceTrackingMapper;
import com.example.productcomparisionweb.entity.product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
@RequestMapping("/tracking")
public class PriceTrackingController {
    
    @Autowired
    private PriceTrackingMapper priceTrackingMapper;

    @GetMapping("/list")
    public Result getTrackedProducts(@RequestParam Integer uid) {
        try {
            List<Integer> trackedProducts = priceTrackingMapper.getTrackedProducts(uid);
            return Result.success(trackedProducts);
        } catch (Exception e) {
            return Result.error("获取收藏商品失败：" + e.getMessage());
        }
    }

    @GetMapping("/details")
    public Result getTrackedProductDetails(@RequestParam Integer uid) {
        try {
            List<product> products = priceTrackingMapper.getTrackedProductDetails(uid);
            return Result.success(products);
        } catch (Exception e) {
            return Result.error("获取收藏商品详情失败：" + e.getMessage());
        }
    }

    @PostMapping("/toggle")
    public Result toggleTracking(@RequestBody Map<String, Object> params) {
        try {
            Integer uid = (Integer) params.get("uid");
            Integer pid = (Integer) params.get("pid");
            Boolean track = (Boolean) params.get("track");
            
            if (track) {
                if (priceTrackingMapper.isProductTracked(uid, pid) > 0) {
                    return Result.error("商品已收藏");
                }
                priceTrackingMapper.addTracking(uid, pid);
                return Result.success("收藏成功");
            } else {
                priceTrackingMapper.removeTracking(uid, pid);
                return Result.success("取消收藏成功");
            }
        } catch (Exception e) {
            return Result.error("操作失败：" + e.getMessage());
        }
    }
} 