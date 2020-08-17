package per.qiang.system.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import per.qiang.common.core.entity.Dept;
import per.qiang.common.core.pojo.DeptWrapper;
import per.qiang.common.core.pojo.QueryRequest;
import per.qiang.common.core.util.ExcelUtil;
import per.qiang.system.annotation.ControllerEndpoint;
import per.qiang.system.service.DeptService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("dept")
@RequiredArgsConstructor
public class DeptController {

    private final DeptService deptService;

    @GetMapping
    public ResponseEntity<?> deptList(QueryRequest request, DeptWrapper deptWrapper) {
        Map<String, Object> depts = this.deptService.findDepts(request, deptWrapper);
        return ResponseEntity.ok(depts);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('dept:add')")
    @ControllerEndpoint(operation = "新增部门", exceptionMessage = "新增部门失败")
    public void addDept(@Valid Dept dept) {
        this.deptService.createDept(dept);
    }

    @DeleteMapping("/{deptIds}")
    @PreAuthorize("hasAuthority('dept:delete')")
    @ControllerEndpoint(operation = "删除部门", exceptionMessage = "删除部门失败")
    public void deleteDepts(@NotBlank(message = "{required}") @PathVariable String deptIds) {
        String[] ids = deptIds.split(",");
        this.deptService.deleteDepts(ids);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('dept:update')")
    @ControllerEndpoint(operation = "修改部门", exceptionMessage = "修改部门失败")
    public void updateDept(@Valid Dept dept) {
        this.deptService.updateDept(dept);
    }

    @PostMapping("excel")
    @PreAuthorize("hasAuthority('dept:export')")
    @ControllerEndpoint(operation = "导出部门数据", exceptionMessage = "导出Excel失败")
    public void export(DeptWrapper deptWrapper, QueryRequest request, HttpServletResponse response) throws IOException {
        List<Dept> depts = this.deptService.findDepts(deptWrapper, request);
        ExcelUtil.exportExcel(depts, Dept.class, response);
    }
}
