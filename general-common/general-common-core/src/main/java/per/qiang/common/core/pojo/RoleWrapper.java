package per.qiang.common.core.pojo;

import per.qiang.common.core.entity.Role;

public class RoleWrapper extends Role {
    private static final long serialVersionUID = 6373737205985846504L;
    private String menuIds;

    public String getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(String menuIds) {
        this.menuIds = menuIds;
    }
}
