package per.qiang.common.job.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties(XxlConfig.class)
public class XxlJobConfigurer {
    private final Logger logger = LoggerFactory.getLogger(XxlJobConfigurer.class);

    public XxlJobConfigurer(XxlConfig xxlConfig) {
        this.xxlConfig = xxlConfig;
    }
    private final XxlConfig xxlConfig;

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        logger.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(xxlConfig.getAdmin().getAddresses());
        xxlJobSpringExecutor.setAppname(xxlConfig.getExecutor().getAppname());
        xxlJobSpringExecutor.setAddress(xxlConfig.getExecutor().getAddress());
        xxlJobSpringExecutor.setIp(xxlConfig.getExecutor().getIp());
        xxlJobSpringExecutor.setPort(xxlConfig.getExecutor().getPort());
        xxlJobSpringExecutor.setAccessToken(xxlConfig.getAccessToken());
        xxlJobSpringExecutor.setLogPath(xxlConfig.getExecutor().getLogpath());
        xxlJobSpringExecutor.setLogRetentionDays(xxlConfig.getExecutor().getLogretentiondays());

        return xxlJobSpringExecutor;
    }
}
