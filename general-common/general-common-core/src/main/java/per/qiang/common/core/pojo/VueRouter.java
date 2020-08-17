package per.qiang.common.core.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class VueRouter<T> implements Serializable {

    private static final long serialVersionUID = -3327478146308500708L;

    @JsonIgnore
    private String id;
    @JsonIgnore
    private String parentId;

    private String path;
    private String name;
    private String component;
    private String redirect;
    private RouterMeta meta;
    private Boolean hidden = false;
    private Boolean alwaysShow = false;
    private List<VueRouter<T>> children;

    @JsonIgnore
    private Boolean hasParent = false;

    @JsonIgnore
    private Boolean hasChildren = false;

    public void initChildren() {
        this.children = new ArrayList<>();
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public RouterMeta getMeta() {
        return meta;
    }

    public void setMeta(RouterMeta meta) {
        this.meta = meta;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Boolean getAlwaysShow() {
        return alwaysShow;
    }

    public void setAlwaysShow(Boolean alwaysShow) {
        this.alwaysShow = alwaysShow;
    }

    public List<VueRouter<T>> getChildren() {
        return children;
    }

    public void setChildren(List<VueRouter<T>> children) {
        this.children = children;
    }

    public Boolean getHasParent() {
        return hasParent;
    }

    public void setHasParent(Boolean hasParent) {
        this.hasParent = hasParent;
    }

    public Boolean getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(Boolean hasChildren) {
        this.hasChildren = hasChildren;
    }
}
