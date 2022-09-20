package cn.guanjm.service.impl;

import cn.guanjm.common.config.UploadProperties;
import cn.guanjm.common.enums.ExceptionEnum;
import cn.guanjm.common.exception.UmException;
import cn.guanjm.service.UploadService;

import cn.guanjm.utils.MD5FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Service
@EnableConfigurationProperties(UploadProperties.class)
public class UploadServiceImpl implements UploadService {

    private static final String KEY_PREFIX = "upload:file";

    UploadProperties prop;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    public UploadServiceImpl(UploadProperties uploadProperties) {
        this.prop = uploadProperties;
    }

    @Override
    public String uploadFile(MultipartFile file) {
        try {

            // 获取文件的md5值
            String md5 = MD5FileUtil.getMD5String(file.getBytes());
            // 存到redis数据库
            BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(KEY_PREFIX);

            SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
            String format = f.format(new Date());

            SimpleDateFormat dirF = new SimpleDateFormat("yyyy-MM-dd");
            String directory = dirF.format(new Date());

            // 上传路径
            File destPath = new File(prop.getBaseUrl(), directory + "/" + format + file.getOriginalFilename());

            // 上级文件不存在，则创建
            if(!destPath.getParentFile().exists()) {
                destPath.getParentFile().mkdir();
            }

            file.transferTo(destPath);

            operations.put(md5, destPath.getPath());

            System.out.println("文件上传路径：" + destPath.getPath());

            return destPath.getPath();
        } catch (IOException e) {
            System.out.println(e);
            throw new UmException(ExceptionEnum.FILE_UPLOAD_FAIL);
        }
    }

    /**
     * 校验文件md5，有说明上传过了
     */
    @Override
    public String verifyFile(String hash) {
        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(KEY_PREFIX);
        // 秒传：文件唯一md5 hash，存到redis里
        if (operations.hasKey(hash)) {
            String filePath = operations.get(hash).toString();
            File file = new File(filePath);
            if (file.exists()) {
                return operations.get(hash).toString();
            }
            return null;
        }
        return null;
    }
}