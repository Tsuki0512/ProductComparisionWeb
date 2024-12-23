package com.example.productcomparisionweb.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.productcomparisionweb.entity.product;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ProductMapper {
    @Insert("INSERT INTO product(productname, platform, current_price, image_url, barcode, historical_prices, specification) " +
            "VALUES(#{productname}, #{platform}, #{current_price}, #{image_url}, #{barcode}, #{historical_prices}, #{specification})")
    @Options(useGeneratedKeys = true, keyProperty = "pid")
    int insertProduct(product product);
}
