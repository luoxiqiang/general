package per.qiang.common.core.pojo;

import per.qiang.common.core.entity.Dept;

public class DeptWrapper extends Dept {

    public static final Long TOP_DEPT_ID = 0L;
    private static final long serialVersionUID = 9001522873728331476L;

    private String createTimeFrom;

    private String createTimeTo;

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
}
