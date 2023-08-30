package com.example.springbootsecurityconcise.controller;


import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * 生成验证码响应到页面
 */
@Controller
@RequestMapping("/captcha")
public class CaptchachaController {

    // 生成验证码的属性
    // 宽度
    private int width = 120;
    // 高度
    private int height = 30;
    // 内容在图片中的起始位置
    private int drawY = 20;
    // 文字的间隔
    private int space = 15;
    // 验证码文字个数
    private int charCount = 6;
    // 验证码内容数组，去掉字母O和数字0
    private String chars[] = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N",/*"O",*/"P"
            ,"Q","R","S","T","U","V","W","X","Y","Z",/*"0",*/"1","2","3","4","5","6","7","8","9"};

    /**
     * 绘制一个图片，将图片响应给请求
     * @param request
     * @param response
     */
    @GetMapping("/code")
    public void makeCaptchaCode(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // 创建一个背景透明的图片，图片格式使用rgb表示颜色，画布
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 获取画笔
        Graphics graphics = image.getGraphics();
        // 设置画笔颜色 白色
        graphics.setColor(Color.white);
        // 把画布涂成白色 fillRect(矩形的起始x，矩形的起始y，矩形的宽度，矩形的高度)
        graphics.fillRect(0, 0, width, height);

        // 画内容
        // 创建字体
        Font font = new Font("黑体", Font.BOLD, 18);
        // 画笔设置字体和颜色
        graphics.setFont(font);
        graphics.setColor(Color.black);
        // 获取随机值
        int ran = 0;
        int len = chars.length;
        StringBuffer stringBuffer = new StringBuffer();
        for(int i = 0; i < charCount; i++){
            ran = new Random().nextInt(len);
            // 保存随机值
            stringBuffer.append(chars[ran]);
            // 设置随机颜色
            graphics.setColor(randomColor());
            // 画的内容，间隔，起始
            graphics.drawString(chars[ran], (i+1)*space, drawY);
        }
        // 绘制干扰线
        for(int i = 0; i < 4; i++){
            graphics.setColor(randomColor());
            int line[] = randomLine();
            graphics.drawLine(line[0], line[1], line[2], line[3]);
        }

        // 生成的验证码存到session
        request.getSession().setAttribute("code", stringBuffer.toString());
        System.out.println("captcha: " + stringBuffer.toString());

        // 设置响应没有缓存
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        // 设置响应格式
        response.setContentType("image/png");

        // 输出图像 w(输出的图像，图像格式，输出到哪)
        ServletOutputStream outputStream = response.getOutputStream();
        ImageIO.write(image, "png", outputStream);
        outputStream.flush();
        outputStream.close();
    }

    /**
     * 生成随机颜色
     * @return
     */
    public Color randomColor(){
        Random random = new Random();
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);
        return new Color(r, g, b);
    }

    /**
     * 生成干扰线的随机起始点
     * @return
     */
    public int[] randomLine(){
        Random random = new Random();
        int x1 = random.nextInt(width/2);
        int y1 = random.nextInt(height);
        int x2 = random.nextInt(width);
        int y2 = random.nextInt(height);
        return new int[]{x1, y1, x2, y2};
    }
}
