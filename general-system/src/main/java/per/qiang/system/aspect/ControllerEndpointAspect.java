package per.qiang.system.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import per.qiang.common.core.util.CommonUtil;
import per.qiang.common.security.util.AuthUtil;
import per.qiang.system.annotation.ControllerEndpoint;
import per.qiang.system.service.OperateLogService;

import java.lang.reflect.Method;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class ControllerEndpointAspect extends BaseAspectSupport {

    private final OperateLogService operateLogService;

    @Pointcut("@annotation(per.qiang.system.annotation.ControllerEndpoint)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint point) throws Exception {
        Object result;
        Method targetMethod = resolveMethod(point);
        ControllerEndpoint annotation = targetMethod.getAnnotation(ControllerEndpoint.class);
        String operation = annotation.operation();
        long start = System.currentTimeMillis();
        try {
            result = point.proceed();
            if (StringUtils.isNotBlank(operation)) {
                String username = AuthUtil.getCurrentUsername();
                String ip = CommonUtil.getHttpServletRequestIpAddress();
                operateLogService.saveLog(point, targetMethod, ip, operation, username, start);
            }
            return result;
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
            String exceptionMessage = annotation.exceptionMessage();
            String message = throwable.getMessage();
            String error = CommonUtil.containChinese(message) ? exceptionMessage + "，" + message : exceptionMessage;
            throw new Exception(error);
        }
    }
}



