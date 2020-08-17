package per.qiang.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import per.qiang.common.core.entity.User;
import per.qiang.common.security.pojo.AuthUser;

import java.util.List;
import java.util.Map;

public interface UserMapper extends BaseMapper<User> {

    IPage<User> findUserDetailPage(Page<User> page, @Param("user") AuthUser user);

    @Insert("insert into user_role values(#{userId},#{roleId})")
    void saveUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Insert("insert into user_data_permission values(#{userId},#{deptId})")
    void saveUserDataPermission(@Param("userId") Long userId, @Param("deptId") Long deptId);

    @Delete("delete from user_role where user_id=#{userId}")
    void deleteUserRolesByUserId(@Param("userId") Long userId);

    @Delete("delete from user_data_permission where user_id=#{userId}")
    void deleteUserDataPermissionByUserId(@Param("userId") Long userId);

    @Select("select user_id userId,dept_id deptId from user_data_permission where user_id=#{userId}")
    List<Map<Object, Object>> findUserDataPermissionByUserId(@Param("userId") String userId);

    List<User> findUserDetail(@Param("user") User user);
}
