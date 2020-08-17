package per.qiang.common.core.pojo;

import per.qiang.common.core.entity.User;

public class UserWrapper extends User {
    public static final String DEFAULT_AVATAR = "default.jpg";
    public static final CharSequence DEFAULT_PASSWORD = "123456";
    public static final Boolean DEFAULT_NOTLOCKED = true;
    public static final Boolean DEFAULT_NOTEXPIRED = true;
    private static final long serialVersionUID = -897602277754610718L;
    private String deptName;

    private String createTimeFrom;
    private String createTimeTo;
    private String roleId;
    private String roleName;
    private String deptIds;

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getCreateTimeFrom() {
        return createTimeFrom;
    }

    public void setCreateTimeFrom(String createTimeFrom) {
        this.createTimeFrom = createTimeFrom;
    }

    public String getCreateTimeTo() {
        return createTimeTo;
    }

    public void setCreateTimeTo(String createTimeTo) {
        this.createTimeTo = createTimeTo;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(String deptIds) {
        this.deptIds = deptIds;
    }
}
