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
import com.example.productcomparisionweb.WebAPI.jingdong_api;
import com.example.productcomparisionweb.WebAPI.suning_api;
import com.example.productcomparisionweb.WebAPI.JDSpider;
import com.example.productcomparisionweb.WebAPI.TBSpider;
import org.json.JSONArray;

import java.util.List;
import java.util.Map;
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
            //jdCookie = "__jdu=1734441028285497906205; areaId=15; PCSYCityID=CN_330000_330100_0; shshshfpa=c9e5a244-6a85-3315-09bc-be01233bb5f3-1734683529; shshshfpx=c9e5a244-6a85-3315-09bc-be01233bb5f3-1734683529; ipLoc-djd=15-1213-3038-59931; user-key=4bd6103a-f69b-4d4d-b8cd-09684f305ecf; cn=0; jcap_dvzw_fp=hmBt7aPsdiQfIaInFYntXG04Qk9ND3RTtq1o4Q38ReV-qV7Hv1lLnUkHtDaru-fbbmU_fqefYebanMSGjEvSLg==; TrackID=1a0uqMmF462dAIlhduF5zHRs6s68KuMi4pROPrsh8P-O5RNEyS-8_UVGdQ7E8Nb-wykN64TFin7wsslYQjEjP2k-raG2bnJYJI6sh5wDwmQg; thor=609849FA0CE1D891E97C5B0A08535EC0816441A9F30B08E0CFE6BBB721C6A13368A16FAA94ACC93B0823240AABF5780E1305CBDABA882CBB0C5D91DF21DCE3974F8DA720F231AA8E0F3668F6E0AC59B2F135F482F8B73C9865745DB46320455F2F194E00AE9ABC8D68DEEBA89A55CD65B6D485740B4D96E25BC2AAFFEDD2A66A877674895E90F03739F876A0F979EB3CB450A4195C0B0C28456556B41E8E905C; light_key=AASBKE7rOxgWQziEhC_QY6yaMZwB_WlOK57tsnMNkJfSJCrFWXD6Lwo8qOyer0u9PD1NKcrk; pinId=bigGIzDRCuPOc-s8hkHP8A; pin=jd_GvevFPXgnAbA; unick=y57rte3kr5xt0f; _tp=Redlg0Fy%2BhvTV%2FjILgSZ%2BQ%3D%3D; _pst=jd_GvevFPXgnAbA; unpl=JF8EAMhnNSttDRsDUksKGhMQGVxcW11bS0QBbW4MUA4MQlEFEgVPQEB7XlVdWBRLFB9sZBRXXlNOVA4eCisSEXteXVdZDEsWC2tXVgQFDQ8VXURJQlZAFDNVCV9dSRZRZjJWBFtdT1xWSAYYRRMfDlAKDlhCR1FpMjVkXlh7VAQrAh0QFU5YVF9eAUgeB2pvA1xcX0hVASsDKxUge21XW14KTBAzblcEZB8MF1MGHwYaFV1LW1ZbWA1LFgBmZAxQWFBNXAQcARoWIEptVw; __jdv=76161171|baidu-pinzhuan|t_288551095_baidupinzhuan|cpc|0f3d30c8dba7459bb52f2eb5eba8ac7d_0_daffa9001c0941b1b33885be84186eca|1734878509214; 3AB9D23F7A4B3C9B=QOLNHKJJIRCM6BG24RD4TW26X2QJZV25XYA6KBMW7TENMODTJOX6PT2HQVD2VPMDV7YC6NWWUBEVFVHYAGASYONB2U; 3AB9D23F7A4B3CSS=jdd03QOLNHKJJIRCM6BG24RD4TW26X2QJZV25XYA6KBMW7TENMODTJOX6PT2HQVD2VPMDV7YC6NWWUBEVFVHYAGASYONB2UAAAAMT6GJG4CYAAAAADDLV2E55XWCA6AX; _gia_d=1; __jda=143920055.1734441028285497906205.1734441028.1734920548.1734924724.8; __jdb=143920055.2.1734441028285497906205|8.1734924724; __jdc=143920055; jsavif=1; flash=3_GfNCKseHRcwLSrYMtbcRwHtILgJN3Gn4zL-22ZzNif6LX9Kx1r5ApO4HTtLc9Gr0AFFz9TRsIwB23ucohzhKg-OqZZuV359nlpyXORoFFj5UjkIkKIDgyILK0TG4rVWgJSuTDqFOkvZs2t7C4fxjxldQIAG3kw7i6FnrVao5c2YKXDq59D-Z; shshshfpb=BApXSYDCa8vZAEwxUmMLdobt113aY_QQpBnYpMgZq9xJ1Mi8QeIG2";
            //tbCookie = "cna=oQzLHs4sWUoCASeuh25t0oIn; thw=cn; tracknick=tb521793254; miid=9052935523587062088; xlly_s=1; _samesite_flag_=true; t=17897c4b0b2e028070b92b99646c672d; _tb_token_=557f150f157a3; 3PcFlag=1734877173893; cookie2=1cdd31fc31b51d0187bbd3dd76de12c3; unb=2201497041782; lgc=tb521793254; cancelledSubSites=empty; cookie17=UUphy%2FZzmjYleP0RSA%3D%3D; dnk=tb521793254; _l_g_=Ug%3D%3D; sg=421; _nk_=tb521793254; cookie1=WqJ5WeasQVgEIW5O1%2BcQh%2BJSLEEjSLBikpRqNT5Qp68%3D; sgcookie=E100GFtso8%2FgHdBPy3E1SqUDs6CvXPVUu3TE9goZttQaQf6%2FqA4KxtRygj%2BeACrnZ8e%2FVTIhi6kAGiDZCvuqHd5wrTWJ4avYLwCSLphWX28tgDijhEwf5Fl9X2OGTV%2Bjo2Ba; havana_lgc2_0=eyJoaWQiOjIyMDE0OTcwNDE3ODIsInNnIjoiNGIyY2MzMzJjMzQwYjc3Yzk3ZjMwYWIyMzk1ZTQxNWEiLCJzaXRlIjowLCJ0b2tlbiI6IjF2djVTNnN3bEN4YzlGU2tpVzdydUdnIn0; _hvn_lgc_=0; havana_lgc_exp=1765981188150; cookie3_bak=1cdd31fc31b51d0187bbd3dd76de12c3; cookie3_bak_exp=1735136388150; wk_cookie2=12d7593fc6113661fca8bc6dea5f9daf; wk_unb=UUphy%2FZzmjYleP0RSA%3D%3D; uc1=pas=0&cookie21=V32FPkk%2FgPzW&cookie15=V32FPkk%2Fw0dUvg%3D%3D&existShop=false&cookie16=UIHiLt3xCS3yM2h4eKHS9lpEOw%3D%3D&cookie14=UoYdWTEDbi%2F0sA%3D%3D; sn=; uc3=nk2=F5RAQI563zecntE%3D&id2=UUphy%2FZzmjYleP0RSA%3D%3D&lg2=URm48syIIVrSKA%3D%3D&vt3=F8dD379%2FUlRcvDhx3Co%3D; csg=5a607854; env_bak=FM%2BgytvMDConPljlb1PB9vTtvhn1ijukQHA2xzkZ6pvL; skt=5f9ae64fc6dd6bf0; existShop=MTczNDg3NzE4OA%3D%3D; uc4=nk4=0%40FY4L7HGVeP4ZeVPeg3BG3dKzITaO8w%3D%3D&id4=0%40U2grEJGMpeUQDX0j35L0ldGUNgmowdvc; _cc_=WqG3DMC9EA%3D%3D; havana_sdkSilent=1734963590448; sdkSilent=1734963882917; tk_trace=oTRxOWSBNwn9dPyorMJE%2FoPdY8zMG1aAN%2F0TkjYGZjkj6rqwJF%2F6JaoyjNidYzDl6Zu2%2BR0guwY8bym8KeKdTfYRGz%2BjwoaeW4fFNb%2BM8%2BXzo%2FhmcgVDNHDadJR1QD5cIWqE610kWydNCKO%2B%2F8OvjlKrt2va6%2B9ILdi4npiyQKNpZNl2jK84vBqFMop0iDbSwBLeWVpzdGzL594p7ypZ5m8VN4ngX6CWC2JG9ryKJqwBLkWdoCJON%2BKOvWclLsWUSyolK3OGr91%2Bwep5T%2FgFvRb6d%2FKEh4%2FaUNWKJXWnnqz5OgVp4cGpuSnhKdvr3ns7B8tLTtMNCn7sB5RiRPEqaVNcMZkU2No%2B17yVszoeJjixrfBFFQJLwq5d9lF4bwAL7mXE0DBNFkZyttWXV7K51DzdRKMs1btPd3lbKvoSEEcSFqlpr6s0WJvlEuZih3YBkoIeFo9bMjHbpPZp2yy09j6Lpxd%2FzKzwqRfViG2dzv2AAcDs6839iKjC4EsfB3kr6PdOBtKgJNUR6tlRczQd5f0OSYMab%2BPkF7i8GBrgCe7037MTBzGt8PTB2LsDWLS7Euz%2Fgwo0UI8IOVupy5uDKQgRmPyGRImdoSUywxcqIWwsnuAVhzqI45bB8Od6Ms6Im2vBvNa2zceWR0T92j8okJmVjTvLCxlQBq6liwZKoV3IQJYDXBuZuAZ3GTfXjlP8wDDTWJ%2FXn7%2Fa1GmNjh3ajzR8yX2LFOdvw0eA8vB5B5EDQeuCVkvGE0x831TzMyYNIIyJBqMsW%2FUapWBhtsTy7Vx0Y9gVqCPmYjAcFPvI8qiFYONL2vP%2BBLnU2k6eV81eS%2BDJAUrf7EY5AH9fJqZZZD9du%2BMgZ8lmw7lErJ2QjzYNyPZFrIJ3aOQ6kxXDgxunz7UOccPopr8QtS38aLRkZ5uyaVfFVGLKykudiZGKEJGOB2wLkT9%2FcP6oRjB7QO7Qjush7%2F4JSXDUIM4u7%2FgMHpNDzTmsH%2BF%2BPUDA; tkSid=1734923079923_560361177_0.0; mtop_partitioned_detect=1; _m_h5_tk=63a87fbc5428b00d965c0e3d8724a08b_1734932805237; _m_h5_tk_enc=85340f327da2e40571269771ff19d8eb; isg=BHNzIYO_EHJiNtwppnVjp_hvAnedqAdqpyDz-iUQ6hLZJJLGtnrlupv23lTKv19i; tfstk=gZdZhqNJnfhNHlAZmwf4Tww6JkfOG1mWnIsfoEYc5GjiBd9FontKkVC6oi-2AMF6CABf0rSHbtOjWS9UuUKe0t3Ocij2cEvsRbGWWFCAinoSNb_Lr9Zk3RXgIq4hPs7gYDf8bFCAiDaQiYGyW3xg9s9ci25huNVGiif0x27fuZfcjtq3KajhmsxcIJbhlaV0nRbGK27AxNfcinfna5x9Du_lI4hwE67_MtWNqFjUaeddSI29WMP0iB-5QgmVYSVDTN8Btcd_axskeG652HcQ_s8huHWWQ0Pw0EYJn9R4jVtkohdF6_kjWTJHdL6wLcVVAC_VEsXUoSYFidXDg_cuYwpH5KA9brPcWCO53gBEoSBftQ6kEhzxyefGonBJG0FOjEYJwL1r_uQwKU5F4aEA-OxWMdr03Obd8ggE8CzKmdCtIMHzH-BhEwSSWt2YHObd8ggE8-eAB7bFVVBc.\n";
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
                    p.setSpecification(item.getString("shopName"));
                    allProducts.add(p);
                }
            }
            
            System.out.println("总共找到商品数量: " + allProducts.size()); // 添加调试日志
            
            // 保存所有商品到数据库
            for (product p : allProducts) {
                productMapper.insertProduct(p);
            }
            
            return Result.success(allProducts);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("搜索失败：" + e.getMessage());
        }
    }
}
