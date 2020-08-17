package per.qiang.spider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EntityScan("per.qiang.common.core.entity")
public class SpiderApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpiderApplication.class,args);
    }
}
