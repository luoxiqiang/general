package per.qiang.auth.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import per.qiang.common.core.entity.OauthClientDetails;

public interface OauthClientDetailsRepository extends JpaRepository<OauthClientDetails, String>, JpaSpecificationExecutor<OauthClientDetails> {
}
