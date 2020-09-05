package per.qiang.system.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import per.qiang.common.core.entity.User;
import per.qiang.common.core.pojo.QueryRequest;
import per.qiang.common.core.util.CommonUtil;
import per.qiang.common.core.util.PoiUtil;
import per.qiang.common.security.pojo.AuthUser;
import per.qiang.common.security.util.AuthUtil;
import per.qiang.system.annotation.ControllerEndpoint;
import per.qiang.system.pojo.LoginLogWrapper;
import per.qiang.system.service.LoginLogService;
import per.qiang.system.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final LoginLogService loginLogService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasAuthority('user:view')")
    public ResponseEntity<?> userList(QueryRequest queryRequest, AuthUser authUser) {
        Map<String, Object> dataTable = CommonUtil.getDataTable(userService.findUserDetailList(authUser, queryRequest));
        return ResponseEntity.ok(dataTable);
    }

    @GetMapping("success")
    @PreAuthorize("isAuthenticated()")
    public void loginSuccess(HttpServletRequest request) {
        String currentUsername = AuthUtil.getCurrentUsername();
        userService.updateLoginTime(currentUsername);
        LoginLogWrapper loginLog = new LoginLogWrapper();
        loginLog.setUsername(currentUsername);
        loginLog.setSystemBrowserInfo(request.getHeader("user-agent"));
        loginLogService.saveLoginLog(loginLog);
    }

    @GetMapping("index")
    public ResponseEntity<?> index() {
        Map<String, Object> data = new HashMap<>(5);
        // 获取系统访问记录
        Long totalVisitCount = loginLogService.findTotalVisitCount();
        data.put("totalVisitCount", totalVisitCount);
        Long todayVisitCount = loginLogService.findTodayVisitCount();
        data.put("todayVisitCount", todayVisitCount);
        Long todayIp = loginLogService.findTodayIp();
        data.put("todayIp", todayIp);
        // 获取近期系统访问记录
        List<Map<String, Object>> lastTenVisitCount = loginLogService.findLastTenDaysVisitCount(null);
        data.put("lastTenVisitCount", lastTenVisitCount);
        User user = new User();
        user.setUsername(AuthUtil.getCurrentUsername());
        List<Map<String, Object>> lastTenUserVisitCount = loginLogService.findLastTenDaysVisitCount(user);
        data.put("lastTenUserVisitCount", lastTenUserVisitCount);
        return ResponseEntity.ok(data);
    }

    @GetMapping("check/{username}")
    public boolean checkUserName(@NotBlank(message = "{required}") @PathVariable String username) {
        return this.userService.findByName(username) == null;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('user:add')")
    @ControllerEndpoint(operation = "新增用户", exceptionMessage = "新增用户失败")
    public void addUser(@Valid AuthUser authUser) {
        this.userService.createUser(authUser);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('user:update')")
    @ControllerEndpoint(operation = "修改用户", exceptionMessage = "修改用户失败")
    public void updateUser(@Valid AuthUser authUser) {
        this.userService.updateUser(authUser);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<?> findUserDataPermissions(@NotBlank(message = "{required}") @PathVariable String userId) {
        String dataPermissions = this.userService.findUserDataPermissionByUserId(userId);
        return ResponseEntity.ok(dataPermissions);
    }

    @DeleteMapping("/{userIds}")
    @PreAuthorize("hasAuthority('user:delete')")
    @ControllerEndpoint(operation = "删除用户", exceptionMessage = "删除用户失败")
    public void deleteUsers(@NotBlank(message = "{required}") @PathVariable String userIds) {
        String[] ids = userIds.split(",");
        this.userService.deleteUsers(ids);
    }

    @PutMapping("profile")
    @ControllerEndpoint(exceptionMessage = "修改个人信息失败")
    public void updateProfile(@Valid User user) throws Exception {
        this.userService.updateProfile(user);
    }

    @PutMapping("avatar")
    @ControllerEndpoint(exceptionMessage = "修改头像失败")
    public void updateAvatar(@NotBlank(message = "{required}") String avatar) {
        this.userService.updateAvatar(avatar);
    }

    @GetMapping("password/check")
    public boolean checkPassword(@NotBlank(message = "{required}") String password) {
        String currentUsername = AuthUtil.getCurrentUsername();
        User user = userService.findByName(currentUsername);
        return user != null && passwordEncoder.matches(password, user.getPassword());
    }

    @PutMapping("password")
    @ControllerEndpoint(exceptionMessage = "修改密码失败")
    public void updatePassword(@NotBlank(message = "{required}") String password) {
        userService.updatePassword(password);
    }

    @PutMapping("password/reset")
    @PreAuthorize("hasAuthority('user:reset')")
    @ControllerEndpoint(exceptionMessage = "重置用户密码失败")
    public void resetPassword(@NotBlank(message = "{required}") String usernames) {
        String[] usernameArr = usernames.split(",");
        this.userService.resetPassword(usernameArr);
    }

    @PostMapping("excel")
    @PreAuthorize("hasAuthority('user:export')")
    @ControllerEndpoint(operation = "导出用户数据", exceptionMessage = "导出Excel失败")
    public void export(QueryRequest queryRequest, AuthUser authUser, HttpServletResponse response) throws IOException {
        List<User> users = this.userService.findUserDetailList(authUser, queryRequest).getRecords();
        PoiUtil.exportExcel(users, User.class, response);
    }
}
