package per.qiang.system.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import per.qiang.system.entity.OperateLog;
import per.qiang.system.pojo.OperateLogWrapper;
import per.qiang.common.core.pojo.QueryRequest;
import per.qiang.common.core.util.CommonUtil;
import per.qiang.common.core.util.DateUtil;
import per.qiang.system.jpa.OperateLogRepository;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class OperateLogService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final OperateLogRepository operateLogRepository;

    public Page<OperateLog> findLogs(OperateLogWrapper operateLogWrapper, QueryRequest request) {
        Specification<OperateLog> spec = (Specification<OperateLog>) (root, query, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotBlank(operateLogWrapper.getUsername())) {
                Path<String> username = root.get("username");
                list.add(criteriaBuilder.equal(username, operateLogWrapper.getUsername()));
            }
            if (StringUtils.isNotBlank(operateLogWrapper.getOperation())) {
                Path<String> operation = root.get("operation");
                list.add(criteriaBuilder.like(operation, "%" + operateLogWrapper.getOperation() + "%"));
            }
            if (StringUtils.isNotBlank(operateLogWrapper.getLocation())) {
                Path<String> location = root.get("location");
                list.add(criteriaBuilder.like(location, "%" + operateLogWrapper.getLocation() + "%"));
            }
            if (StringUtils.isNotBlank(operateLogWrapper.getCreateTimeFrom()) && StringUtils.isNotBlank(operateLogWrapper.getCreateTimeTo())) {
                Path<Date> createTime = root.get("createTime");
                try {
                    list.add(criteriaBuilder.greaterThanOrEqualTo(createTime, DateUtil.parse(DateUtil.DATE_PATTERN,operateLogWrapper.getCreateTimeFrom())));
                    list.add(criteriaBuilder.lessThanOrEqualTo(createTime, DateUtil.parse(DateUtil.DATE_PATTERN,operateLogWrapper.getCreateTimeTo())));
                } catch (Exception e) {
                    log.error("时间转换异常",e);
                }

            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        Sort sort = Sort.by("asc".equalsIgnoreCase(request.getOrder()) ? Sort.Direction.ASC : Sort.Direction.DESC
                , StringUtils.isBlank(request.getField())?"createTime":request.getField());
        Pageable pageable = PageRequest.of(request.getPageNum() - 1, request.getPageSize(), sort);

        return operateLogRepository.findAll(spec, pageable);
    }

    public void deleteLogs(String[] logIds) {
        List<OperateLog> list = new ArrayList<>();
        Arrays.stream(logIds).forEach(id -> {
            OperateLog operateLog = new OperateLog();
            operateLog.setId(Long.parseLong(id));
            list.add(operateLog);
        });
        operateLogRepository.deleteInBatch(list);
    }

    public void saveLog(ProceedingJoinPoint point, Method method, String ip, String operation, String username, long start) {
        OperateLog operateLog = new OperateLog();
        operateLog.setIp(ip);

        operateLog.setUsername(username);
        operateLog.setTime(System.currentTimeMillis() - start);
        operateLog.setOperation(operation);

        String className = point.getTarget().getClass().getName();
        String methodName = method.getName();
        operateLog.setMethod(className + "." + methodName + "()");

        Object[] args = point.getArgs();
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        String[] paramNames = u.getParameterNames(method);
        if (args != null && paramNames != null) {
            StringBuilder params = new StringBuilder();
            params = handleParams(params, args, Arrays.asList(paramNames));
            operateLog.setParams(params.toString());
        }
        operateLog.setCreateTime(new Date());
        operateLog.setLocation(CommonUtil.getCityInfo(ip));
        // 保存系统日志
        operateLogRepository.save(operateLog);
    }

    @SuppressWarnings("all")
    private StringBuilder handleParams(StringBuilder params, Object[] args, List paramNames) {
        try {
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Map) {
                    Set set = ((Map) args[i]).keySet();
                    List<Object> list = new ArrayList<>();
                    List<Object> paramList = new ArrayList<>();
                    for (Object key : set) {
                        list.add(((Map) args[i]).get(key));
                        paramList.add(key);
                    }
                    return handleParams(params, list.toArray(), paramList);
                } else {
                    if (args[i] instanceof Serializable) {
                        Class<?> aClass = args[i].getClass();
                        try {
                            aClass.getDeclaredMethod("toString", new Class[]{null});
                            // 如果不抛出 NoSuchMethodException 异常则存在 toString 方法 ，安全的 writeValueAsString ，否则 走 Object的 toString方法
                            params.append(" ").append(paramNames.get(i)).append(": ").append(objectMapper.writeValueAsString(args[i]));
                        } catch (NoSuchMethodException e) {
                            params.append(" ").append(paramNames.get(i)).append(": ").append(objectMapper.writeValueAsString(args[i].toString()));
                        }
                    } else if (args[i] instanceof MultipartFile) {
                        MultipartFile file = (MultipartFile) args[i];
                        params.append(" ").append(paramNames.get(i)).append(": ").append(file.getName());
                    } else {
                        params.append(" ").append(paramNames.get(i)).append(": ").append(args[i]);
                    }
                }
            }
        } catch (Exception ignore) {
            params.append("参数解析失败");
        }
        return params;
    }
}
