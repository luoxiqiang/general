package per.qiang.common.doc.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "swagger")
public class SwaggerProperties {
    /**
     * 接口扫描路径，如Controller路径
     */
    private String basePackage;
    /**
     * 文档标题
     */
    private String title = "swagger-bootstrap-ui";
    /**
     * 文档描述
     */
    private String description = "<div style='font-size:14px;color:red;'>swagger-bootstrap-ui RESTful APIs</div>";
    /**
     * 文档描述颜色
     */
    private String descriptionColor = "#42b983";
    /**
     * 文档描述字体大小
     */
    private String descriptionFontSize = "14";
    /**
     * 服务url
     */
    private String termsOfServiceUrl = "127.0.0.1";
    /**
     * 版本
     */
    private String version = "1.0";

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionColor() {
        return descriptionColor;
    }

    public void setDescriptionColor(String descriptionColor) {
        this.descriptionColor = descriptionColor;
    }

    public String getDescriptionFontSize() {
        return descriptionFontSize;
    }

    public void setDescriptionFontSize(String descriptionFontSize) {
        this.descriptionFontSize = descriptionFontSize;
    }

    public String getTermsOfServiceUrl() {
        return termsOfServiceUrl;
    }

    public void setTermsOfServiceUrl(String termsOfServiceUrl) {
        this.termsOfServiceUrl = termsOfServiceUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
