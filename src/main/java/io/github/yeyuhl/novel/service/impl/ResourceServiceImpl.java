package io.github.yeyuhl.novel.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import io.github.yeyuhl.novel.core.common.constant.ErrorCodeEnum;
import io.github.yeyuhl.novel.core.common.exception.BusinessException;
import io.github.yeyuhl.novel.core.common.resp.RestResp;
import io.github.yeyuhl.novel.core.constant.SystemConfigConsts;
import io.github.yeyuhl.novel.dto.resp.ImgVerifyCodeRespDto;
import io.github.yeyuhl.novel.manager.redis.VerifyCodeManager;
import io.github.yeyuhl.novel.service.ResourceService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 资源模块服务实现类
 *
 * @author yeyuhl
 * @date 2023/5/9
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceServiceImpl implements ResourceService {

    private final VerifyCodeManager verifyCodeManager;

    @Value("${novel.file.upload.path}")
    private String fileUploadPath;

    @Override
    public RestResp<ImgVerifyCodeRespDto> getImgVerifyCode() throws IOException {
        // 使用get32UUID生成唯一的会话ID
        String sessionId = IdWorker.get32UUID();
        return RestResp.ok(ImgVerifyCodeRespDto.builder()
            .sessionId(sessionId)
            .img(verifyCodeManager.genImgVerifyCode(sessionId))
            .build());
    }

    @SneakyThrows
    @Override
    public RestResp<String> uploadImage(MultipartFile file) {
        LocalDateTime now = LocalDateTime.now();
        // 构造一个由IMAGE_UPLOAD_DIRECTORY和格式化后的日期组成的保存路径
        String savePath = SystemConfigConsts.IMAGE_UPLOAD_DIRECTORY
                + now.format(DateTimeFormatter.ofPattern("yyyy")) + File.separator
                + now.format(DateTimeFormatter.ofPattern("MM")) + File.separator
                + now.format(DateTimeFormatter.ofPattern("dd"));
        // 获取文件的原始文件名
        String oriName = file.getOriginalFilename();
        assert oriName != null;
        // 构造新的文件名（根据会话ID和原始文件名组合而成）
        String saveFileName = IdWorker.get32UUID() + oriName.substring(oriName.lastIndexOf("."));
        File saveFile = new File(fileUploadPath + savePath, saveFileName);
        // 如果要保存的文件的父目录不存在
        if (!saveFile.getParentFile().exists()) {
            // 创建该目录
            boolean isSuccess = saveFile.getParentFile().mkdirs();
            // 创建失败抛出异常
            if (!isSuccess) {
                throw new BusinessException(ErrorCodeEnum.USER_UPLOAD_FILE_ERROR);
            }
        }
        // 将文件保存到指定位置
        file.transferTo(saveFile);
        if (Objects.isNull(ImageIO.read(saveFile))) {
            // 如果上传的文件不是图片则删除该文件，并抛出异常
            Files.delete(saveFile.toPath());
            throw new BusinessException(ErrorCodeEnum.USER_UPLOAD_FILE_TYPE_NOT_MATCH);
        }
        return RestResp.ok(savePath + File.separator + saveFileName);
    }

}
