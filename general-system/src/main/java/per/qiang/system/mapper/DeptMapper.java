package per.qiang.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import per.qiang.common.core.entity.Dept;


public interface DeptMapper extends BaseMapper<Dept> {

    @Delete("delete from user_data_permission where dept_id=#{deptId}")
    void deleteUserDataPermissionByDeptId(@Param("deptId") String deptId);
}
