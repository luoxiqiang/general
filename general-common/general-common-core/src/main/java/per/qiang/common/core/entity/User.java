package per.qiang.common.core.entity;


import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
public class User implements Serializable {

    private static final long serialVersionUID = -3937428657922319575L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long id;

    @Excel(name = "用户名")
    private String username;

    private String password;
    @Excel(name = "性别", replace = {"男_0", "女_1", "保密_2"})
    private String sex;

    @Excel(name = "真实姓名")
    private String realName;

    @Excel(name = "部门ID")
    private Long deptId;

    @Excel(name = "邮箱")
    private String email;

    @Excel(name = "头像")
    private String avatar;

    @Excel(name = "手机")
    private String mobile;

    @Excel(name = "锁定标识", replace = {"未锁定_true", "已锁定_false"})
    private boolean notLocked;

    private boolean notExpired;

    @Excel(name = "是否可用", replace = {"可用_true", "不可用_false"})
    private Boolean status;

    @Excel(name = "创建时间", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Excel(name = "修改时间", format = "yyyy-MM-dd HH:mm:ss")
    private Date modifyTime;

    @Excel(name = "最后登录时间", format = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;

    @Excel(name = "用户描述")
    private String description;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isNotLocked() {
        return notLocked;
    }

    public void setNotLocked(boolean notLocked) {
        this.notLocked = notLocked;
    }

    public boolean isNotExpired() {
        return notExpired;
    }

    public void setNotExpired(boolean notExpired) {
        this.notExpired = notExpired;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
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

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}