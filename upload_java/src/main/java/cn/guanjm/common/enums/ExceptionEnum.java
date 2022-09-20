package cn.guanjm.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {
    FILE_UPLOAD_FAIL(500, "文件上传失败")
    ;
    private int code;
    private String msg;
}
