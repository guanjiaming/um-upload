package cn.guanjm.common.exception;


import cn.guanjm.common.enums.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UmException extends RuntimeException{
    private ExceptionEnum exceptionEnum;
}
