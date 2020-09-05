package per.qiang.system.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import per.qiang.system.entity.LoginLog;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long>, JpaSpecificationExecutor<LoginLog> {

    @Query(value = "SELECT count(1) FROM login_log WHERE login_time between CURDATE() and DATE_ADD(CURDATE(), INTERVAL 1 DAY)", nativeQuery = true)
    Long findTodayVisitCount();

    @Query(value = "SELECT count(DISTINCT(ip)) FROM login_log WHERE login_time between CURDATE() and DATE_ADD(CURDATE(), INTERVAL 1 DAY)", nativeQuery = true)
    Long findTodayIp();



}
