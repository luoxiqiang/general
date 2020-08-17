package per.qiang.system.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import per.qiang.common.core.entity.OperateLog;

public interface OperateLogRepository extends JpaRepository<OperateLog,Long>, JpaSpecificationExecutor<OperateLog> {
}
