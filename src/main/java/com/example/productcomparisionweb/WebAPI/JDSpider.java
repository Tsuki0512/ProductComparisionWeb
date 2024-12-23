package com.example.productcomparisionweb.WebAPI;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class JDSpider {
    private final String cookie;
    private static final String PYTHON_VENV_PATH = "venv/Scripts/python.exe";  // Windows路径
    // private static final String PYTHON_VENV_PATH = "venv/bin/python";  // Linux/Mac路径

    public JDSpider(String cookie) {
        this.cookie = cookie;
    }

    public JSONObject requestSearch(String keyword, String cookie, int offset, int limit) throws IOException, InterruptedException {
        // 获取项目根目录
        String projectRoot = System.getProperty("user.dir");
        String pythonPath = new File(projectRoot, PYTHON_VENV_PATH).getAbsolutePath();
        String scriptPath = new File(projectRoot, "data_source/jd_service.py").getAbsolutePath();

        ProcessBuilder pb = new ProcessBuilder(
            pythonPath,
            scriptPath,
            keyword,
            cookie,
            String.valueOf(offset),
            String.valueOf(limit)
        );
        
        pb.directory(new File(projectRoot));
        
        // 设置环境变量
        Map<String, String> env = pb.environment();
        env.put("PYTHONIOENCODING", "utf-8");
        env.put("PYTHONPATH", new File(projectRoot, "data_source").getAbsolutePath());
        
        pb.redirectErrorStream(true);
        
        Process process = pb.start();
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream(), "UTF-8")
        );
        
        String line;
        String jsonLine = null;  // 用于存储最后一行（JSON数据）
        
        // 读取所有输出，但只保留最后一行
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
        String testCookie = "__jdu=1734441028285497906205; areaId=15; PCSYCityID=CN_330000_330100_0; shshshfpa=c9e5a244-6a85-3315-09bc-be01233bb5f3-1734683529; shshshfpx=c9e5a244-6a85-3315-09bc-be01233bb5f3-1734683529; ipLoc-djd=15-1213-3038-59931; user-key=4bd6103a-f69b-4d4d-b8cd-09684f305ecf; cn=0; TrackID=1a0uqMmF462dAIlhduF5zHRs6s68KuMi4pROPrsh8P-O5RNEyS-8_UVGdQ7E8Nb-wykN64TFin7wsslYQjEjP2k-raG2bnJYJI6sh5wDwmQg; thor=609849FA0CE1D891E97C5B0A08535EC0816441A9F30B08E0CFE6BBB721C6A13368A16FAA94ACC93B0823240AABF5780E1305CBDABA882CBB0C5D91DF21DCE3974F8DA720F231AA8E0F3668F6E0AC59B2F135F482F8B73C9865745DB46320455F2F194E00AE9ABC8D68DEEBA89A55CD65B6D485740B4D96E25BC2AAFFEDD2A66A877674895E90F03739F876A0F979EB3CB450A4195C0B0C28456556B41E8E905C; light_key=AASBKE7rOxgWQziEhC_QY6yaMZwB_WlOK57tsnMNkJfSJCrFWXD6Lwo8qOyer0u9PD1NKcrk; pinId=bigGIzDRCuPOc-s8hkHP8A; pin=jd_GvevFPXgnAbA; unick=y57rte3kr5xt0f; _tp=Redlg0Fy%2BhvTV%2FjILgSZ%2BQ%3D%3D; _pst=jd_GvevFPXgnAbA; unpl=JF8EAMhnNSttDRsDUksKGhMQGVxcW11bS0QBbW4MUA4MQlEFEgVPQEB7XlVdWBRLFB9sZBRXXlNOVA4eCisSEXteXVdZDEsWC2tXVgQFDQ8VXURJQlZAFDNVCV9dSRZRZjJWBFtdT1xWSAYYRRMfDlAKDlhCR1FpMjVkXlh7VAQrAh0QFU5YVF9eAUgeB2pvA1xcX0hVASsDKxUge21XW14KTBAzblcEZB8MF1MGHwYaFV1LW1ZbWA1LFgBmZAxQWFBNXAQcARoWIEptVw; __jdv=76161171|baidu-pinzhuan|t_288551095_baidupinzhuan|cpc|0f3d30c8dba7459bb52f2eb5eba8ac7d_0_daffa9001c0941b1b33885be84186eca|1734878509214; shshshfpb=BApXSHOTZ7fZAEwxUmMLdobt113aY_QQpBnYpMgZp9xJ1Mi8QeIG2; 3AB9D23F7A4B3C9B=QOLNHKJJIRCM6BG24RD4TW26X2QJZV25XYA6KBMW7TENMODTJOX6PT2HQVD2VPMDV7YC6NWWUBEVFVHYAGASYONB2U; 3AB9D23F7A4B3CSS=jdd03QOLNHKJJIRCM6BG24RD4TW26X2QJZV25XYA6KBMW7TENMODTJOX6PT2HQVD2VPMDV7YC6NWWUBEVFVHYAGASYONB2UAAAAMT6FJK24AAAAAACUQZ66FHT2YBFMX; _gia_d=1; jsavif=1; __jda=143920055.1734441028285497906205.1734441028.1734878509.1734920548.7; __jdb=143920055.2.1734441028285497906205|7.1734920548; __jdc=143920055; flash=3_3wfLebA8xlDo2kKonM7saz32bKhJkkSlwjqnmYFHVd5o2kYBXjjEHoQx00zbAeue4qPCtZfwCbtYOsgMKAwr84IPvo6gqfd7RTMctPIdvUiw-TnVh4U26F752qUD31yXTucek42YlTSPSNDGZxydTs0X4VUMEKVpJ7gvpedZtljp2biLyriz";
        JDSpider spider = new JDSpider(testCookie);
        try {
            JSONObject result = spider.requestSearch("手机", testCookie, 0, 30);
            System.out.println("Search result: " + result.toString(2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 