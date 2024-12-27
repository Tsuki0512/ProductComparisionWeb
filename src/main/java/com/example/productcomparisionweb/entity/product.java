package com.example.productcomparisionweb.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
@TableName("product")
public class product {
    @TableId(type = IdType.AUTO)
    private int pid;
    private String productname;
    private String platform;
    private double current_price;
    private String category;
    private String specification;
    private String barcode;
    private String image_url;
    private String historical_prices;
    private boolean isTracked;
    private Integer trackedCount;

    @Override
    public String toString() {
        return "product{" +
                "pid=" + pid +
                ", productname='" + productname + '\'' +
                ", platform='" + platform + '\'' +
                ", current_price=" + current_price +
                ", category='" + category + '\'' +
                ", specification='" + specification + '\'' +
                ", barcode='" + barcode + '\'' +
                ", image_url='" + image_url + '\'' +
                ", historical_prices='" + historical_prices + '\'' +
                '}';
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public double getCurrent_price() {
        return current_price;
    }

    public void setCurrent_price(double current_price) {
        this.current_price = current_price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getHistorical_prices() {
        return historical_prices;
    }

    public void setHistorical_prices(String historical_prices) {
        this.historical_prices = historical_prices;
    }

    public boolean getIsTracked() {
        return isTracked;
    }

    public void setIsTracked(boolean isTracked) {
        this.isTracked = isTracked;
    }

    public Integer getTrackedCount() {
        return trackedCount;
    }

    public void setTrackedCount(Integer trackedCount) {
        this.trackedCount = trackedCount;
    }
}
