package per.qiang.system.pojo;

import per.qiang.system.entity.OperateLog;

public class OperateLogWrapper extends OperateLog {
    private static final long serialVersionUID = -4561614303154015862L;
    private transient String createTimeFrom;
    private transient String createTimeTo;

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
