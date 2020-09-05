package per.qiang.common.fdfs.config;


import org.springframework.context.annotation.Bean;
import per.qiang.common.fdfs.service.FileService;

public class FdfsConfigurer {

    @Bean
    public FileService fileService() {
        return new FileService();
    }
}
