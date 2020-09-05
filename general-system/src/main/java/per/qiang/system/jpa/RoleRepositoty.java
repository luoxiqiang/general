package per.qiang.system.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import per.qiang.common.core.entity.Role;


public interface RoleRepositoty extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {

    Role findByRoleName(String roleName);

}
