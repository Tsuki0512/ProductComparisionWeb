package com.example.productcomparisionweb.service;

public interface EmailService {
    /**
     * 发送简单文本邮件
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     * @return 是否发送成功
     */
    boolean sendSimpleEmail(String to, String subject, String content);
} 