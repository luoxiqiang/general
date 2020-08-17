package per.qiang.auth.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import per.qiang.common.core.entity.Dept;
import per.qiang.common.core.entity.User;

import java.util.List;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    @Query(value = "SELECT DISTINCT M.PERMS FROM ROLE R LEFT JOIN USER_ROLE UR ON ( R.ID = UR.ROLE_ID ) LEFT JOIN USER U " +
            "ON ( U.ID = UR.USER_ID ) LEFT JOIN ROLE_MENU RM ON ( RM.ROLE_ID = R.ID ) LEFT JOIN MENU M ON ( M.ID = RM.MENU_ID ) " +
            "WHERE U.USERNAME = ?1 AND M.PERMS IS NOT NULL AND M.PERMS <> ''", nativeQuery = true)
    Set<String> queryPermissions(String username);

    @Query("select new Dept(d.id,d.parentId,d.deptName,d.orderNum,d.createTime,d.modifyTime) from Dept d where d.id = ?1")
    Dept findUserDeptByDeptId(Long deptId);

    @Query(value = "SELECT id,role_name from role where id in (SELECT role_id from user_role where user_id = ?1)", nativeQuery = true)
    List<Object[]> findUserRolesByUserId(Long id);

}
