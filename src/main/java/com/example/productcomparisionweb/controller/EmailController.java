package com.example.productcomparisionweb.controller;

import com.example.productcomparisionweb.common.Result;
import com.example.productcomparisionweb.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public Result sendEmail(@RequestParam String to,
                          @RequestParam String subject,
                          @RequestParam String content) {
        try {
            boolean success = emailService.sendSimpleEmail(to, subject, content);
            if (success) {
                return Result.success("邮件发送成功");
            } else {
                return Result.error("邮件发送失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("邮件发送异常：" + e.getMessage());
        }
    }

    @GetMapping("/test")
    public Result testEmail() {
        try {
            String to = "3220104111@zju.edu.cn";
            String subject = "测试邮件";
            String content = "这是一封测试邮件，用于验证邮件服务是否正常工作。";
            
            boolean success = emailService.sendSimpleEmail(to, subject, content);
            if (success) {
                return Result.success("测试邮件发送成功");
            } else {
                return Result.error("测试邮件发送失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("测试邮件发送异常：" + e.getMessage());
        }
    }
} 