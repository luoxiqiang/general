package per.qiang.common.core.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Dept implements Serializable {

    private static final long serialVersionUID = 3105959232057134266L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    @Excel(name = "ID")
    private Long id;

    @Excel(name = "上级部门ID")
    private Long parentId;

    @NotNull
    @Excel(name = "部门名称")
    private String deptName;
    @Excel(name = "排序")
    private Integer orderNum;

    @Excel(name = "创建时间",format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Excel(name = "修改时间",format = "yyyy-MM-dd HH:mm:ss")
    private Date modifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Dept() {}
    public Dept(Long id,Long parentId, String deptName, Integer orderNum, Date createTime, Date modifyTime) {
        this.id = id;
        this.parentId = parentId;
        this.deptName = deptName;
        this.orderNum = orderNum;
        this.createTime = createTime;
        this.modifyTime = modifyTime;
    }
}