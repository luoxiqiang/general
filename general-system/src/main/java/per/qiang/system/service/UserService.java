package per.qiang.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import per.qiang.common.core.entity.User;
import per.qiang.common.core.pojo.QueryRequest;
import per.qiang.common.security.pojo.AuthUser;
import per.qiang.common.security.util.AuthUtil;
import per.qiang.system.jpa.UserRepository;
import per.qiang.system.mapper.UserMapper;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class UserService extends ServiceImpl<UserMapper, User> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(rollbackFor = Exception.class)
    public void updateLoginTime(String username) {
        User user = new User();
        user.setUsername(username);
        user.setLastLoginTime(new Date());
        userRepository.updateByUsername(user);
    }


    public User findByName(String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        return this.baseMapper.selectOne(queryWrapper);
    }


    public IPage<User> findUserDetailList(AuthUser authUser, QueryRequest request) {
        Page<User> page = new Page<>(request.getPageNum(), request.getPageSize());
        page.addOrder(OrderItem.asc("id"));
        return this.baseMapper.findUserDetailPage(page, authUser);
    }


    public User findUserDetail(String username) {
        User param = new User();
        param.setUsername(username);
        List<User> users = baseMapper.findUserDetail(param);
        return CollectionUtils.isNotEmpty(users) ? users.get(0) : null;
    }


    @Transactional(rollbackFor = Exception.class)
    public void createUser(AuthUser authUser) {
        // 创建用户
        authUser.setCreateTime(new Date());
        authUser.setAvatar(AuthUser.DEFAULT_AVATAR);
        authUser.setPassword(passwordEncoder.encode(AuthUser.DEFAULT_PASSWORD));
        authUser.setNotExpired(AuthUser.DEFAULT_NOTEXPIRED);
        authUser.setNotLocked(AuthUser.DEFAULT_NOTLOCKED);
        User user = new User();
        BeanUtils.copyProperties(authUser, user);
        save(user);
        // 保存用户角色
        String[] roles = StringUtils.splitByWholeSeparatorPreserveAllTokens(authUser.getRoleId(), ",");
        setUserRoles(user, roles);
        // 保存用户数据权限关联关系
        String[] deptIds = StringUtils.splitByWholeSeparatorPreserveAllTokens(authUser.getDeptIds(), ",");
        setUserDataPermissions(user, deptIds);
    }


    @Transactional(rollbackFor = Exception.class)
    public void updateUser(AuthUser authUser) {
        // 更新用户
        authUser.setPassword(null);
        authUser.setUsername(null);
        authUser.setCreateTime(null);
        authUser.setModifyTime(new Date());
        User user = new User();
        BeanUtils.copyProperties(authUser, user);
        updateById(user);

        baseMapper.deleteUserRolesByUserId(user.getId());
        String[] roles = StringUtils.splitByWholeSeparatorPreserveAllTokens(authUser.getRoleId(), ",");
        setUserRoles(user, roles);

        baseMapper.deleteUserDataPermissionByUserId(user.getId());
        String[] deptIds = StringUtils.splitByWholeSeparatorPreserveAllTokens(authUser.getDeptIds(), ",");
        setUserDataPermissions(user, deptIds);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteUsers(String[] userIds) {
        List<String> list = Arrays.asList(userIds);
        removeByIds(list);
        // 删除用户角色
        list.forEach(userId -> {
            baseMapper.deleteUserRolesByUserId(Long.parseLong(userId));
            baseMapper.deleteUserDataPermissionByUserId(Long.parseLong(userId));
        });
    }


    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(User user) throws Exception {
        user.setPassword(null);
        user.setUsername(null);
        user.setStatus(null);
        if (isCurrentUser(user.getId())) {
            updateById(user);
        } else {
            throw new Exception("您无权修改别人的账号信息！");
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void updateAvatar(String avatar) {
        User user = new User();
        user.setAvatar(avatar);
        String currentUsername = AuthUtil.getCurrentUsername();
        this.baseMapper.update(user, new LambdaQueryWrapper<User>().eq(User::getUsername, currentUsername));
    }


    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(String password) {
        User user = new User();
        user.setPassword(passwordEncoder.encode(password));
        String currentUsername = AuthUtil.getCurrentUsername();
        this.baseMapper.update(user, new LambdaQueryWrapper<User>().eq(User::getUsername, currentUsername));
    }


    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(String[] usernames) {
        User params = new User();
        params.setPassword(passwordEncoder.encode(AuthUser.DEFAULT_PASSWORD));

        List<String> list = Arrays.asList(usernames);
        this.baseMapper.update(params, new LambdaQueryWrapper<User>().in(User::getUsername, list));

    }

    private void setUserRoles(User user, String[] roles) {
        Arrays.stream(roles).forEach(roleId -> {
            baseMapper.saveUserRole(user.getId(), Long.parseLong(roleId));
        });
    }

    private void setUserDataPermissions(User user, String[] deptIds) {
        Arrays.stream(deptIds).forEach(deptId -> {
            baseMapper.saveUserDataPermission(user.getId(), Long.parseLong(deptId));
        });
    }

    private boolean isCurrentUser(Long id) {
        AuthUser currentUser = AuthUtil.getCurrentUser();
        return currentUser != null && id.equals(currentUser.getId());
    }


    public String findUserDataPermissionByUserId(String userId) {
        return baseMapper.findUserDataPermissionByUserId(userId).stream().map(
                permission -> String.valueOf(permission.get("deptId"))).collect(Collectors.joining(",")
        );
    }
}