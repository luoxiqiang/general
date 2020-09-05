package per.qiang.system.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import per.qiang.common.core.entity.Role;
import per.qiang.common.core.pojo.QueryRequest;
import per.qiang.system.pojo.RoleWrapper;
import per.qiang.common.core.util.CommonUtil;
import per.qiang.common.core.util.PoiUtil;
import per.qiang.system.annotation.ControllerEndpoint;
import per.qiang.system.service.RoleService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("role")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<?> roleList(QueryRequest queryRequest, Role role) {
        Map<String, Object> dataTable = CommonUtil.getDataTable(roleService.findRoles(role, queryRequest));
        return ResponseEntity.ok(dataTable);
    }

    @GetMapping("options")
    public ResponseEntity<?> roles() {
        List<Role> allRoles = roleService.findAllRoles();
        return ResponseEntity.ok(allRoles);
    }

    @GetMapping("check/{roleName}")
    public boolean checkRoleName(@NotBlank(message = "{required}") @PathVariable String roleName) {
        Role result = this.roleService.findByName(roleName);
        return result == null;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('role:add')")
    @ControllerEndpoint(operation = "新增角色", exceptionMessage = "新增角色失败")
    public void addRole(@Valid RoleWrapper roleWrapper) {
        roleService.createRole(roleWrapper);
    }

    @DeleteMapping("/{ids}")
    @PreAuthorize("hasAuthority('role:delete')")
    @ControllerEndpoint(operation = "删除角色", exceptionMessage = "删除角色失败")
    public void deleteRoles(@NotBlank(message = "{required}") @PathVariable String ids) {
        String[] ids_a = ids.split(",");
        this.roleService.deleteRoles(ids_a);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('role:update')")
    @ControllerEndpoint(operation = "修改角色", exceptionMessage = "修改角色失败")
    public void updateRole(@Valid RoleWrapper roleWrapper) {
        roleService.updateRole(roleWrapper);
    }

    @PostMapping("excel")
    @PreAuthorize("hasAuthority('role:export')")
    @ControllerEndpoint(operation = "导出角色数据", exceptionMessage = "导出Excel失败")
    public void export(QueryRequest queryRequest, Role role, HttpServletResponse response) throws IOException {
        List<RoleWrapper> roles = roleService.findRoles(role, queryRequest).getRecords();
        PoiUtil.exportExcel(roles, Role.class, response);
    }
}
