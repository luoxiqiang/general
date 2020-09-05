package per.qiang.system.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import per.qiang.system.entity.OperateLog;
import per.qiang.system.pojo.OperateLogWrapper;
import per.qiang.common.core.pojo.QueryRequest;
import per.qiang.common.core.util.CommonUtil;
import per.qiang.common.core.util.PoiUtil;
import per.qiang.system.annotation.ControllerEndpoint;
import per.qiang.system.service.OperateLogService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("operateLog")
public class OperateLogController {

    private final OperateLogService operateLogService;

    @GetMapping
    public ResponseEntity<?> logList(OperateLogWrapper operateLogWrapper, QueryRequest request) {
        Map<String, Object> dataTable = CommonUtil.getDataTable(operateLogService.findLogs(operateLogWrapper, request));
        return ResponseEntity.ok(dataTable);
    }

    @DeleteMapping("{ids}")
    @PreAuthorize("hasAuthority('operatelog:delete')")
    @ControllerEndpoint(exceptionMessage = "删除日志失败")
    public void deleteLogss(@NotBlank(message = "{required}") @PathVariable String ids) {
        String[] logIds = ids.split(",");
        operateLogService.deleteLogs(logIds);
    }


    @PostMapping("excel")
    @PreAuthorize("hasAuthority('operatelog:export')")
    @ControllerEndpoint(exceptionMessage = "导出Excel失败")
    public void export(QueryRequest request, OperateLogWrapper operateLogWrapper, HttpServletResponse response) throws IOException {
        List<OperateLog> logs = operateLogService.findLogs(operateLogWrapper, request).getContent();
        PoiUtil.exportExcel(logs, OperateLog.class, response);
    }
}
