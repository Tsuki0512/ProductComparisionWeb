package com.example.productcomparisionweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.productcomparisionweb.entity.product;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ProductMapper {
    @Insert("INSERT INTO product(productname, platform, current_price, category, specification, barcode, image_url, historical_prices) " +
            "VALUES(#{productname}, #{platform}, #{current_price}, #{category}, #{specification}, #{barcode}, #{image_url}, #{historical_prices})")
    @Options(useGeneratedKeys = true, keyProperty = "pid")
    int insertProduct(product product);
}
