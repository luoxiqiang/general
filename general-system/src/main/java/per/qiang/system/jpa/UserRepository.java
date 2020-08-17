package per.qiang.system.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import per.qiang.common.core.entity.User;

import javax.transaction.Transactional;

@Transactional
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    User findByUsername(String username);

    @Modifying
    @Query(value = "update `user` set last_login_time=:#{#user.lastLoginTime} where username=:#{#user.username}", nativeQuery = true)
    void updateByUsername(User user);

}
