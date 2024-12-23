package com.example.productcomparisionweb.WebAPI;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class TBSpider {
    private final String cookie;

    public TBSpider(String cookie) {
        this.cookie = cookie;
    }

    public JSONObject requestSearch(String keyword, String cookie, int offset, int limit) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(
            "D:\\Anaconda\\python.exe",  // 使用你的 Python 解释器路径
            "data_source/tb_service.py",
            keyword,
            cookie,
            String.valueOf(offset),
            String.valueOf(limit)
        );
        
        pb.directory(new File(System.getProperty("user.dir")));
        pb.redirectErrorStream(true);
        
        // 设置环境变量以处理编码
        pb.environment().put("PYTHONIOENCODING", "utf-8");
        
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream(), "UTF-8")
        );
        
        String line;
        String jsonLine = null;  // 用于存储最后一行（JSON数据）
        
        // 读取所有输出，但只保留 JSON 格式的行
        while ((line = reader.readLine()) != null) {
            System.out.println("Python output: " + line);
            if (line.trim().startsWith("{")) {  // 只保存 JSON 格式的行
                jsonLine = line;
            }
        }
        
        int exitCode = process.waitFor();
        
        if (exitCode == 0 && jsonLine != null) {
            return new JSONObject(jsonLine.trim());
        } else {
            throw new RuntimeException("Python script execution failed with exit code: " + exitCode);
        }
    }

    // 测试方法
    public static void main(String[] args) {
        String testCookie = "cna=oQzLHs4sWUoCASeuh25t0oIn; thw=cn; tracknick=tb521793254; miid=9052935523587062088; xlly_s=1; _samesite_flag_=true; t=17897c4b0b2e028070b92b99646c672d; _tb_token_=557f150f157a3; 3PcFlag=1734877173893; cookie2=1cdd31fc31b51d0187bbd3dd76de12c3; unb=2201497041782; lgc=tb521793254; cancelledSubSites=empty; cookie17=UUphy%2FZzmjYleP0RSA%3D%3D; dnk=tb521793254; _l_g_=Ug%3D%3D; sg=421; _nk_=tb521793254; cookie1=WqJ5WeasQVgEIW5O1%2BcQh%2BJSLEEjSLBikpRqNT5Qp68%3D; sgcookie=E100GFtso8%2FgHdBPy3E1SqUDs6CvXPVUu3TE9goZttQaQf6%2FqA4KxtRygj%2BeACrnZ8e%2FVTIhi6kAGiDZCvuqHd5wrTWJ4avYLwCSLphWX28tgDijhEwf5Fl9X2OGTV%2Bjo2Ba; havana_lgc2_0=eyJoaWQiOjIyMDE0OTcwNDE3ODIsInNnIjoiNGIyY2MzMzJjMzQwYjc3Yzk3ZjMwYWIyMzk1ZTQxNWEiLCJzaXRlIjowLCJ0b2tlbiI6IjF2djVTNnN3bEN4YzlGU2tpVzdydUdnIn0; _hvn_lgc_=0; havana_lgc_exp=1765981188150; cookie3_bak=1cdd31fc31b51d0187bbd3dd76de12c3; cookie3_bak_exp=1735136388150; wk_cookie2=12d7593fc6113661fca8bc6dea5f9daf; wk_unb=UUphy%2FZzmjYleP0RSA%3D%3D; uc1=pas=0&cookie21=V32FPkk%2FgPzW&cookie15=V32FPkk%2Fw0dUvg%3D%3D&existShop=false&cookie16=UIHiLt3xCS3yM2h4eKHS9lpEOw%3D%3D&cookie14=UoYdWTEDbi%2F0sA%3D%3D; sn=; uc3=nk2=F5RAQI563zecntE%3D&id2=UUphy%2FZzmjYleP0RSA%3D%3D&lg2=URm48syIIVrSKA%3D%3D&vt3=F8dD379%2FUlRcvDhx3Co%3D; csg=5a607854; env_bak=FM%2BgytvMDConPljlb1PB9vTtvhn1ijukQHA2xzkZ6pvL; skt=5f9ae64fc6dd6bf0; existShop=MTczNDg3NzE4OA%3D%3D; uc4=nk4=0%40FY4L7HGVeP4ZeVPeg3BG3dKzITaO8w%3D%3D&id4=0%40U2grEJGMpeUQDX0j35L0ldGUNgmowdvc; _cc_=WqG3DMC9EA%3D%3D; havana_sdkSilent=1734963590448; sdkSilent=1734963882917; tk_trace=oTRxOWSBNwn9dPyorMJE%2FoPdY8zMG1aAN%2F0TkjYGZjkj6rqwJF%2F6JaoyjNidYzDl6Zu2%2BR0guwY8bym8KeKdTfYRGz%2BjwoaeW4fFNb%2BM8%2BXzo%2FhmcgVDNHDadJR1QD5cIWqE610kWydNCKO%2B%2F8OvjlKrt2va6%2B9ILdi4npiyQKNpZNl2jK84vBqFMop0iDbSwBLeWVpzdGzL594p7ypZ5m8VN4ngX6CWC2JG9ryKJqwBLkWdoCJON%2BKOvWclLsWUSyolK3OGr91%2Bwep5T%2FgFvRb6d%2FKEh4%2FaUNWKJXWnnqz5OgVp4cGpuSnhKdvr3ns7B8tLTtMNCn7sB5RiRPEqaVNcMZkU2No%2B17yVszoeJjixrfBFFQJLwq5d9lF4bwAL7mXE0DBNFkZyttWXV7K51DzdRKMs1btPd3lbKvoSEEcSFqlpr6s0WJvlEuZih3YBkoIeFo9bMjHbpPZp2yy09j6Lpxd%2FzKzwqRfViG2dzv2AAcDs6839iKjC4EsfB3kr6PdOBtKgJNUR6tlRczQd5f0OSYMab%2BPkF7i8GBrgCe7037MTBzGt8PTB2LsDWLS7Euz%2Fgwo0UI8IOVupy5uDKQgRmPyGRImdoSUywxcqIWwsnuAVhzqI45bB8Od6Ms6Im2vBvNa2zceWR0T92j8okJmVjTvLCxlQBq6liwZKoV3IQJYDXBuZuAZ3GTfXjlP8wDDTWJ%2FXn7%2Fa1GmNjh3ajzR8yX2LFOdvw0eA8vB5B5EDQeuCVkvGE0x831TzMyYNIIyJBqMsW%2FUapWBhtsTy7Vx0Y9gVqCPmYjAcFPvI8qiFYONL2vP%2BBLnU2k6eV81eS%2BDJAUrf7EY5AH9fJqZZZD9du%2BMgZ8lmw7lErJ2QjzYNyPZFrIJ3aOQ6kxXDgxunz7UOccPopr8QtS38aLRkZ5uyaVfFVGLKykudiZGKEJGOB2wLkT9%2FcP6oRjB7QO7Qjush7%2F4JSXDUIM4u7%2FgMHpNDzTmsH%2BF%2BPUDA; tkSid=1734923079923_560361177_0.0; mtop_partitioned_detect=1; _m_h5_tk=63a87fbc5428b00d965c0e3d8724a08b_1734932805237; _m_h5_tk_enc=85340f327da2e40571269771ff19d8eb; isg=BHNzIYO_EHJiNtwppnVjp_hvAnedqAdqpyDz-iUQ6hLZJJLGtnrlupv23lTKv19i; tfstk=gZdZhqNJnfhNHlAZmwf4Tww6JkfOG1mWnIsfoEYc5GjiBd9FontKkVC6oi-2AMF6CABf0rSHbtOjWS9UuUKe0t3Ocij2cEvsRbGWWFCAinoSNb_Lr9Zk3RXgIq4hPs7gYDf8bFCAiDaQiYGyW3xg9s9ci25huNVGiif0x27fuZfcjtq3KajhmsxcIJbhlaV0nRbGK27AxNfcinfna5x9Du_lI4hwE67_MtWNqFjUaeddSI29WMP0iB-5QgmVYSVDTN8Btcd_axskeG652HcQ_s8huHWWQ0Pw0EYJn9R4jVtkohdF6_kjWTJHdL6wLcVVAC_VEsXUoSYFidXDg_cuYwpH5KA9brPcWCO53gBEoSBftQ6kEhzxyefGonBJG0FOjEYJwL1r_uQwKU5F4aEA-OxWMdr03Obd8ggE8CzKmdCtIMHzH-BhEwSSWt2YHObd8ggE8-eAB7bFVVBc.\n";
        TBSpider spider = new TBSpider(testCookie);
        try {
            JSONObject result = spider.requestSearch("手机", testCookie, 0, 30);
            System.out.println("Search result: " + result.toString(2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 