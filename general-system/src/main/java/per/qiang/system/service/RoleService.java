package per.qiang.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import per.qiang.common.core.entity.Role;
import per.qiang.common.core.pojo.QueryRequest;
import per.qiang.system.pojo.RoleWrapper;
import per.qiang.system.jpa.RoleRepositoty;
import per.qiang.system.mapper.RoleMapper;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class RoleService extends ServiceImpl<RoleMapper,Role> {

    private final RoleRepositoty roleRepositoty;
    private final RoleMapper roleMapper;

    public IPage<RoleWrapper> findRoles(Role role, QueryRequest queryRequest) {
        Page<Role> page =  new Page<>(queryRequest.getPageNum(), queryRequest.getPageSize());
        page.addOrder(OrderItem.asc("createTime"));
        return roleMapper.findRolePage(page, role);
    }

    public List<Role> findUserRole(String userName) {
        return roleMapper.findUserRole(userName);
    }

    public List<Role> findAllRoles() {
        LambdaQueryWrapper<Role> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Role::getId);
        return roleMapper.selectList(queryWrapper);
    }

    public Role findByName(String roleName) {
        return roleRepositoty.findByRoleName(roleName);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createRole(RoleWrapper roleWrapper) {
        roleWrapper.setCreateTime(new Date());
        Role role = new Role();
        BeanUtils.copyProperties(roleWrapper,role);
        this.save(role);
        if (StringUtils.isNotBlank(roleWrapper.getMenuIds())) {
            String[] menuIds = StringUtils.splitByWholeSeparatorPreserveAllTokens(roleWrapper.getMenuIds(), ",");
            setRoleMenus(role, menuIds);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteRoles(String[] ids) {
        List<String> list = Arrays.asList(ids);
        roleMapper.deleteBatchIds(list);
        list.forEach(id->{
            roleMapper.deleteRoleMenusByRoleId(Long.parseLong(id));
            roleMapper.deleteUserRolesByRoleId(Long.parseLong(id));
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateRole(RoleWrapper roleWrapper) {
        roleWrapper.setRoleName(null);
        roleWrapper.setModifyTime(new Date());
        Role role = new Role();
        BeanUtils.copyProperties(roleWrapper,role);
        baseMapper.updateById(role);
        roleMapper.deleteRoleMenusByRoleId(role.getId());
        if (StringUtils.isNotBlank(roleWrapper.getMenuIds())) {
            String[] menuIds = StringUtils.splitByWholeSeparatorPreserveAllTokens(roleWrapper.getMenuIds(), ",");
            setRoleMenus(role, menuIds);
        }
    }

    private void setRoleMenus(Role role, String[] menuIds) {
        Arrays.stream(menuIds).forEach(menuId -> {
            roleMapper.insertRoleMenu(role.getId(),menuId);
        });
    }
}
