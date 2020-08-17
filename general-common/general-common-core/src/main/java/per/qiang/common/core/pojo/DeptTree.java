package per.qiang.common.core.pojo;

import per.qiang.common.core.entity.Dept;

public class DeptTree extends Tree<Dept> {

    private Integer orderNum;

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }
}
