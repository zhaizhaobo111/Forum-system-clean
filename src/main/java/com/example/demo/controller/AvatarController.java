package com.example.demo.controller;

import com.example.demo.common.AppResult;
import com.example.demo.model.User;
import com.example.demo.services.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static com.example.demo.common.AppCpnfig.USER_SESSION;

@Api(tags = "头像接口")
@Slf4j
@RestController
public class AvatarController {

    @Value("${avatar.default-path:D:\\桌面\\截图.jpg}")
    private String defaultAvatarPath;

    @Value("${avatar.upload-path:D:\\avatar\\}")
    private String uploadAvatarPath;

    @javax.annotation.Resource
    private IUserService userService;

    /**
     * 获取头像
     * @param userId 用户ID（可选）
     * @return 头像图片
     */
    @ApiOperation("获取头像")
    @GetMapping("/Get_avatar")
    public ResponseEntity<Resource> getAvatar(
            @ApiParam("用户ID（可选，不传则返回默认头像）")
            @RequestParam(value = "userId", required = false) Long userId) {

        try {
            File avatarFile = null;

            if (userId != null) {
                // 尝试获取用户自定义头像
                String userAvatarPath = uploadAvatarPath + userId + ".jpg";
                File userAvatar = new File(userAvatarPath);
                if (userAvatar.exists()) {
                    avatarFile = userAvatar;
                }
            }

            // 如果没有用户头像，使用默认头像
            if (avatarFile == null || !avatarFile.exists()) {
                avatarFile = new File(defaultAvatarPath);
            }

            if (!avatarFile.exists()) {
                log.warn("头像文件不存在：{}", avatarFile.getAbsolutePath());
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(avatarFile);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + avatarFile.getName() + "\"")
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);

        } catch (Exception e) {
            log.error("获取头像失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取头像（简化版，直接返回默认头像）
     */
    @ApiOperation("获取默认头像")
    @GetMapping("/avatar/default")
    public ResponseEntity<Resource> getDefaultAvatar() {
        try {
            File avatarFile = new File(defaultAvatarPath);

            if (!avatarFile.exists()) {
                log.warn("默认头像文件不存在：{}", defaultAvatarPath);
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(avatarFile);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"avatar.jpg\"")
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);

        } catch (Exception e) {
            log.error("获取默认头像失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 上传头像
     * @param file 上传的文件
     * @param request HTTP请求
     * @return 上传结果
     */
    @ApiOperation("上传头像")
    @PostMapping("/avatar/upload")
    public AppResult uploadAvatar(
            @ApiParam("头像文件") @RequestParam("file") MultipartFile file,
            HttpServletRequest request) {

        try {
            // 获取当前登录用户
            HttpSession session = request.getSession(false);
            if (session == null) {
                return AppResult.failed("未登录");
            }
            User user = (User) session.getAttribute(USER_SESSION);
            if (user == null) {
                return AppResult.failed("未登录");
            }

            // 检查文件是否为空
            if (file.isEmpty()) {
                return AppResult.failed("请选择文件");
            }

            // 检查文件类型
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".jpg")
                    && !originalFilename.toLowerCase().endsWith(".jpeg")
                    && !originalFilename.toLowerCase().endsWith(".png")) {
                return AppResult.failed("只支持JPG、JPEG、PNG格式");
            }

            // 检查文件大小（最大2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                return AppResult.failed("文件大小不能超过2MB");
            }

            // 创建上传目录
            File uploadDir = new File(uploadAvatarPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成文件名（使用用户ID）
            String fileName = user.getId() + ".jpg";
            String filePath = uploadAvatarPath + fileName;

            // 保存文件
            File destFile = new File(filePath);
            file.transferTo(destFile);

            // 更新数据库中的头像路径
            String avatarUrl = "/Get_avatar?userId=" + user.getId();
            userService.updateAvatarUrl(user.getId(), avatarUrl);

            // 更新session中的用户信息
            user.setAvatarUrl(avatarUrl);
            session.setAttribute(USER_SESSION, user);

            log.info("头像上传成功：userId={}, filePath={}", user.getId(), filePath);
            return AppResult.success("头像上传成功");

        } catch (Exception e) {
            log.error("头像上传失败", e);
            return AppResult.failed("上传失败：" + e.getMessage());
        }
    }
}
