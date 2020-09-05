package per.qiang.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import per.qiang.auth.jpa.OauthClientDetailsRepository;
import per.qiang.auth.entity.OauthClientDetails;
import per.qiang.common.core.pojo.QueryRequest;
import per.qiang.common.core.util.JpaUtil;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class OauthClientDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final RedisClientDetailsService redisClientDetailsService;
    private final OauthClientDetailsRepository oauthClientDetailsRepository;

    public Page<OauthClientDetails> findOauthClientDetails(QueryRequest request, OauthClientDetails oauthClientDetails) {
        Specification<OauthClientDetails> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> list = new ArrayList<>();
            if (StringUtils.isNotBlank(oauthClientDetails.getClientId())) {
                Path<String> clientId = root.get("clientId");
                list.add(criteriaBuilder.equal(clientId, oauthClientDetails.getClientId()));
            }
            return criteriaBuilder.and(list.toArray(new Predicate[0]));
        };
        Sort sort = Sort.by("asc".equalsIgnoreCase(request.getOrder()) ? Sort.Direction.ASC : Sort.Direction.DESC
                , StringUtils.isBlank(request.getField()) ? "clientId" : request.getField());
        Pageable pageable = PageRequest.of(request.getPageNum() - 1, request.getPageSize(), sort);
        Page<OauthClientDetails> result = oauthClientDetailsRepository.findAll(spec, pageable);
        result.getContent().forEach(clientDetails -> {
            clientDetails.setOriginSecret(null);
            clientDetails.setClientSecret(null);
        });
        return result;
    }

    public OauthClientDetails findById(String clientId) {
        Optional<OauthClientDetails> optional = oauthClientDetailsRepository.findById(clientId);
        return optional.orElse(null);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createOauthClientDetails(OauthClientDetails oauthClientDetails) throws Exception {
        OauthClientDetails clientDetails = findById(oauthClientDetails.getClientId());
        if (clientDetails != null) {
            throw new Exception("该Client已存在");
        }
        oauthClientDetails.setOriginSecret(oauthClientDetails.getClientSecret());
        oauthClientDetails.setClientSecret(passwordEncoder.encode(oauthClientDetails.getClientSecret()));
        OauthClientDetails save = oauthClientDetailsRepository.saveAndFlush(oauthClientDetails);
        if (StringUtils.isNoneBlank(save.getClientId())) {
            log.info("缓存Client -> {}", oauthClientDetails);
            this.redisClientDetailsService.loadClientByClientId(oauthClientDetails.getClientId());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateOauthClientDetails(OauthClientDetails oauthClientDetails) {
        oauthClientDetails.setClientSecret(null);
        Optional<OauthClientDetails> clientDetails = oauthClientDetailsRepository.findById(oauthClientDetails.getClientId());
        if (clientDetails.isPresent()) {
            OauthClientDetails details = clientDetails.get();
            JpaUtil.copyNotNullProperties(oauthClientDetails, details);
            oauthClientDetailsRepository.saveAndFlush(details);
        }
        log.info("更新Client -> {}", oauthClientDetails);
        this.redisClientDetailsService.removeRedisCache(oauthClientDetails.getClientId());
        this.redisClientDetailsService.loadClientByClientId(oauthClientDetails.getClientId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteOauthClientDetails(String clientIds) {
        String[] clientIdArray = StringUtils.splitByWholeSeparatorPreserveAllTokens(clientIds, ",");
        Arrays.stream(clientIdArray).forEach(clientId -> {
            oauthClientDetailsRepository.deleteById(clientId);
            redisClientDetailsService.removeRedisCache(clientId);
        });
    }
}
