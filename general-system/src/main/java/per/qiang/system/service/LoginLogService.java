package per.qiang.system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import per.qiang.system.entity.LoginLog;
import per.qiang.common.core.entity.User;
import per.qiang.system.pojo.LoginLogWrapper;
import per.qiang.common.core.pojo.QueryRequest;
import per.qiang.common.core.util.CommonUtil;
import per.qiang.system.jpa.LoginLogRepository;
import per.qiang.system.mapper.LoginLogMapper;


import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class LoginLogService extends ServiceImpl<LoginLogMapper,LoginLog> {

    private final LoginLogRepository loginLogRepository;
    private final LoginLogMapper loginLogMapper;

    @Transactional(rollbackFor = Exception.class)
    public void saveLoginLog(LoginLogWrapper loginLogWrapper) {
        loginLogWrapper.setLoginTime(new Date());
        String ip = CommonUtil.getHttpServletRequestIpAddress();
        loginLogWrapper.setIp(ip);
        loginLogWrapper.setLocation(CommonUtil.getCityInfo(ip));
        LoginLog loginLog = new LoginLog();
        BeanUtils.copyProperties(loginLogWrapper,loginLog);
        loginLogRepository.save(loginLog);
    }

    public IPage<LoginLog> findLoginLogs(LoginLogWrapper loginLog, QueryRequest request) {
        QueryWrapper<LoginLog> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(loginLog.getUsername())) {
            queryWrapper.lambda().eq(LoginLog::getUsername, loginLog.getUsername().toLowerCase());
        }
        if (StringUtils.isNotBlank(loginLog.getLoginTimeFrom()) && StringUtils.isNotBlank(loginLog.getLoginTimeTo())) {
            queryWrapper.lambda()
                    .ge(LoginLog::getLoginTime, loginLog.getLoginTimeFrom())
                    .le(LoginLog::getLoginTime, loginLog.getLoginTimeTo());
        }
        Page<LoginLog> page = new Page<>(request.getPageNum(), request.getPageSize());
        page.addOrder(OrderItem.desc("login_time"));
        return this.page(page, queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteLoginLogs(String[] ids) {
        List<String> list = Arrays.asList(ids);
        baseMapper.deleteBatchIds(list);
    }

    public Long findTotalVisitCount() {
        return loginLogRepository.count();
    }

    public Long findTodayVisitCount() {
        return loginLogRepository.findTodayVisitCount();
    }

    public Long findTodayIp() {
        return loginLogRepository.findTodayIp();
    }

    public List<Map<String, Object>> findLastTenDaysVisitCount(User user) {
        return loginLogMapper.findLastTenDaysVisitCount(user);
    }

    public List<LoginLog> findUserLastSevenLoginLogs(String username) {
        LoginLogWrapper loginLogWrapper = new LoginLogWrapper();
        loginLogWrapper.setUsername(username);

        QueryRequest request = new QueryRequest();
        request.setPageNum(1);
        // 近7日记录
        request.setPageSize(7);

        IPage<LoginLog> loginLogs = findLoginLogs(loginLogWrapper, request);
        return loginLogs.getRecords();
    }
}
