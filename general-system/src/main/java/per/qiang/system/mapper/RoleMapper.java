package per.qiang.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import per.qiang.common.core.entity.Role;
import per.qiang.system.pojo.RoleWrapper;

import java.util.List;


public interface RoleMapper extends BaseMapper<Role> {

    IPage<RoleWrapper> findRolePage(Page<?> page, Role role);

    @Insert("insert into role_menu values(#{roleId},#{menuId})")
    void insertRoleMenu(@Param("roleId") Long roleId, @Param("menuId") String menuId);

    @Delete("delete from role_menu where role_id=#{roleId}")
    void deleteRoleMenusByRoleId(@Param("roleId") Long roleId);

    @Delete("delete from user_role where role_id=#{roleId}")
    void deleteUserRolesByRoleId(@Param("roleId") Long roleId);

    List<Role> findUserRole(String userName);
}
