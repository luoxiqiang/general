package per.qiang.system.pojo;

import per.qiang.common.core.entity.Dept;
import per.qiang.common.core.pojo.Tree;

public class DeptTree extends Tree<Dept> {

    private Integer orderNum;

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }
}
