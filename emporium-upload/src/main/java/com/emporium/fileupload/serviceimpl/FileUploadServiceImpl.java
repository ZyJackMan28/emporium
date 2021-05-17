package com.emporium.fileupload.serviceimpl;

import com.emporium.common.enums.EnumsStatus;
import com.emporium.common.exception.EpException;
import com.emporium.fileupload.config.ImageProperties;
import com.emporium.fileupload.service.FileUploadService;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@EnableConfigurationProperties({ImageProperties.class})
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    private FastFileStorageClient storageClient;
    @Autowired
    private ThumbImageConfig thumbImageConfig;
    @Autowired
    private ImageProperties imageProperties;

    // 支持的文件类型,静态final 常量名最好大写
    private static final List<String> ALLOW_TYPE = Arrays.asList("image/png", "image/jpeg","image/bmp");
    //文件上传的逻辑
    public String imageUpload(MultipartFile file) {
        //改造上传到fastDFS
        try {
            //校验文件
            String contentType = file.getContentType();
            if (!imageProperties.getAllowType().contains(contentType)){
                throw new EpException(EnumsStatus.FILE_TYPE_IS_INSUPPORTABLE);
            }
            //校验文件内容
            BufferedImage image = ImageIO.read(file.getInputStream());
            if(null == image){
                //如果读到内容不是image
                throw new EpException(EnumsStatus.FILE_TYPE_IS_INSUPPORTABLE);
            }
            //上传到fastDFS,扩展名，,substring 截取最后一个点，然后不要.
            //String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            //在最后一个点截取，以后碰到字符串等问题，善于利用spring工具类
            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            StorePath storePath = storageClient.uploadImageAndCrtThumbImage(file.getInputStream(), file.getSize(), extension, null);
            //返回路径
            return imageProperties.getBaseUrl() + storePath.getFullPath();
        } catch (IOException e){
            log.error("文件上传失败！",e);

            throw new EpException(EnumsStatus.FILEUPLOAD_IS_FAILED);
        }
        /*try {
            //校验文件
            String contentType = file.getContentType();
            if (!ALLOW_TYPE.contains(contentType)){
                throw new EpException(EnumsStatus.FILE_TYPE_IS_INSUPPORTABLE);
            }
            //校验文件内容
            BufferedImage image = ImageIO.read(file.getInputStream());
            if(null == image){
                //如果读到内容不是image
                throw new EpException(EnumsStatus.FILE_TYPE_IS_INSUPPORTABLE);
            }
            //准备目标文件路径
            File dest = new File("F:\\ffmep\\tupian", file.getOriginalFilename());
            //文件保存的地址
            file.transferTo(dest);
            //返回路径
            return "http://image.emporium.com/" + file.getOriginalFilename();
        } catch (IOException e){
            log.error("文件上传失败！",e);
            throw new EpException(EnumsStatus.FILEUPLOAD_IS_FAILED);
        }*/
        //保存文件到本地 可以查 api file


    }

}
