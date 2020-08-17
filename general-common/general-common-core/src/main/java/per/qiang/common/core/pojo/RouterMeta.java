package per.qiang.common.core.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouterMeta implements Serializable {

    private static final long serialVersionUID = 5499925927195914L;

    public RouterMeta(String title, String icon, Boolean breadcrumb) {
        this.title = title;
        this.icon = icon;
        this.breadcrumb = breadcrumb;
    }

    private String title;
    private String icon;
    private Boolean breadcrumb = true;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getBreadcrumb() {
        return breadcrumb;
    }

    public void setBreadcrumb(Boolean breadcrumb) {
        this.breadcrumb = breadcrumb;
    }
}
