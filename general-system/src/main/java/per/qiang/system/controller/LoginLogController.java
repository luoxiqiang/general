package per.qiang.system.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import per.qiang.common.core.entity.LoginLog;
import per.qiang.common.core.pojo.LoginLogWrapper;
import per.qiang.common.core.pojo.QueryRequest;
import per.qiang.common.core.util.CommonUtil;
import per.qiang.common.core.util.ExcelUtil;
import per.qiang.common.security.util.AuthUtil;
import per.qiang.system.annotation.ControllerEndpoint;
import per.qiang.system.service.LoginLogService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("loginLog")
public class LoginLogController {

    private final LoginLogService loginLogService;

    @GetMapping
    public ResponseEntity<?> loginLogList(LoginLogWrapper loginLogWrapper, QueryRequest request) {
        Map<String, Object> dataTable = CommonUtil.getDataTable(this.loginLogService.findLoginLogs(loginLogWrapper, request));
        return ResponseEntity.ok(dataTable);
    }

    @GetMapping("currentUser")
    public ResponseEntity<?> getUserLastSevenLoginLogs() {
        String currentUsername = AuthUtil.getCurrentUsername();
        List<LoginLog> userLastSevenLoginLogs = this.loginLogService.findUserLastSevenLoginLogs(currentUsername);
        return ResponseEntity.ok(userLastSevenLoginLogs);
    }

    @DeleteMapping("{ids}")
    @PreAuthorize("hasAuthority('loginlog:delete')")
    @ControllerEndpoint(operation = "删除登录日志", exceptionMessage = "删除登录日志失败")
    public void deleteLogs(@NotBlank(message = "{required}") @PathVariable String ids) {
        String[] loginLogIds = ids.split(",");
        this.loginLogService.deleteLoginLogs(loginLogIds);
    }

    @PostMapping("excel")
    @PreAuthorize("hasAuthority('loginlog:export')")
    @ControllerEndpoint(operation = "导出登录日志数据", exceptionMessage = "导出Excel失败")
    public void export(QueryRequest request, LoginLogWrapper loginLogWrapper, HttpServletResponse response) throws IOException {
        List<LoginLog> loginLogs = this.loginLogService.findLoginLogs(loginLogWrapper, request).getRecords();
        ExcelUtil.exportExcel(loginLogs, LoginLog.class, response);
    }
}
