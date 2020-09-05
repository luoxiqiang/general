package per.qiang.system.controller;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import per.qiang.common.core.entity.Menu;
import per.qiang.common.core.pojo.VueRouter;
import per.qiang.common.core.util.PoiUtil;
import per.qiang.system.annotation.ControllerEndpoint;
import per.qiang.system.service.MenuService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserRouters(@PathVariable String username) {
        Map<String, Object> result = new HashMap<>(2);
        List<VueRouter<Menu>> userRouters = this.menuService.getUserRouters(username);
        String userPermissions = this.menuService.findUserPermissions(username);
        String[] permissionArray = new String[0];
        if (StringUtils.isNoneBlank(userPermissions)) {
            permissionArray = StringUtils.splitByWholeSeparatorPreserveAllTokens(userPermissions, ",");
        }
        result.put("routes", userRouters);
        result.put("permissions", permissionArray);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<?> menuList(Menu menu) {
        Map<String, Object> menus = this.menuService.findMenus(menu);
        return ResponseEntity.ok(menus);
    }

    @GetMapping("/permissions")
    public String findUserPermissions(String username) {
        return this.menuService.findUserPermissions(username);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('menu:add')")
    @ControllerEndpoint(operation = "新增菜单/按钮", exceptionMessage = "新增菜单/按钮失败")
    public void addMenu(@Valid Menu menu) {
        this.menuService.saveMenu(menu);
    }

    @DeleteMapping("/{ids}")
    @PreAuthorize("hasAuthority('menu:delete')")
    @ControllerEndpoint(operation = "删除菜单/按钮", exceptionMessage = "删除菜单/按钮失败")
    public void deleteMenus(@PathVariable String ids) {
        String[] ids_a = ids.split(",");
        this.menuService.deleteMenus(ids_a);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('menu:update')")
    @ControllerEndpoint(operation = "修改菜单/按钮", exceptionMessage = "修改菜单/按钮失败")
    public void updateMenu(@Valid Menu menu) {
        this.menuService.saveMenu(menu);
    }

    @PostMapping("excel")
    @PreAuthorize("hasAuthority('menu:export')")
    @ControllerEndpoint(operation = "导出菜单数据", exceptionMessage = "导出Excel失败")
    public void export(Menu menu, HttpServletResponse response) throws IOException {
        List<Menu> menus = this.menuService.findMenuList(menu);
        PoiUtil.exportExcel(menus, Menu.class, response);
    }
}
