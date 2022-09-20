package cn.guanjm.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "um.upload")
@Data
public class UploadProperties {
    private String baseUrl;
    private List<String> allowTypes;
}
