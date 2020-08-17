package per.qiang.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import per.qiang.auth.jpa.UserRepository;
import per.qiang.common.core.entity.Dept;
import per.qiang.common.core.entity.User;
import per.qiang.common.security.pojo.AuthUser;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (null == user) throw new UsernameNotFoundException("用户不存在");
        Set<String> permissions = userRepository.queryPermissions(username);
        List<GrantedAuthority> authorities = AuthorityUtils.NO_AUTHORITIES;
        if (permissions != null && !permissions.isEmpty())
            authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(String.join(",", permissions));
        AuthUser authUser = new AuthUser();
        authUser.setAuthorities(authorities);
        BeanUtils.copyProperties(user, authUser);
        Dept dept = this.findUserDept(user.getDeptId());
        if (dept != null) authUser.setDeptName(dept.getDeptName());
        List<Object[]> roles = this.findUserRoles(user.getId());
        String roleNames = roles.stream().map(role -> role[1].toString()).collect(Collectors.joining("/"));
        String roleIds = roles.stream().map(role -> role[0].toString()).collect(Collectors.joining(","));
        authUser.setRoleName(roleNames);
        authUser.setRoleId(roleIds);
        return authUser;
    }

    public Dept findUserDept(Long deptId){
        if(deptId != null)  return userRepository.findUserDeptByDeptId(deptId);
        return null;
    }

    public List<Object[]> findUserRoles(Long id){
        return userRepository.findUserRolesByUserId(id);

    }
}
