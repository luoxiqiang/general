package per.qiang.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import per.qiang.common.core.entity.LoginLog;
import per.qiang.common.core.entity.User;

import java.util.List;
import java.util.Map;

public interface LoginLogMapper extends BaseMapper<LoginLog> {

    List<Map<String, Object>> findLastTenDaysVisitCount(User user);
}
