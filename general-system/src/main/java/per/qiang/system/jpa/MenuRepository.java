package per.qiang.system.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import per.qiang.common.core.entity.Menu;

import java.util.List;


public interface MenuRepository extends JpaRepository<Menu, Long>, JpaSpecificationExecutor<Menu> {

    @Query(value = "SELECT M.* FROM MENU M WHERE M.TYPE <> 1 AND M.ID IN (SELECT DISTINCT RM.MENU_ID FROM ROLE_MENU RM" +
            " LEFT JOIN ROLE R ON (RM.ROLE_ID = R.ID) LEFT JOIN USER_ROLE UR ON (UR.ROLE_ID = R.ID) LEFT JOIN `USER` U" +
            " ON (U.ID = UR.USER_ID) WHERE U.USERNAME = ?1) ORDER BY M.ORDER_NUM", nativeQuery = true)
    List<Menu> findUserMenus(String username);

    @Query(value = "SELECT DISTINCT M.PERMS FROM ROLE R LEFT JOIN USER_ROLE UR ON (R.ID = UR.ROLE_ID) LEFT JOIN `USER` U " +
            "ON (U.ID = UR.USER_ID) LEFT JOIN ROLE_MENU RM ON (RM.ROLE_ID = R.ID) LEFT JOIN MENU M ON (M.ID = RM.MENU_ID) " +
            "WHERE U.USERNAME = ?1 AND M.PERMS IS NOT NULL AND M.PERMS <> ''", nativeQuery = true)
    List<String> findUserPermissions(String username);

    List<Menu> findByParentId(Long id);

    @Modifying
    @Query(value = "delete from role_menu where menu_id = ?1", nativeQuery = true)
    void deleteRoleMenuByMenuId(Long id);
}
