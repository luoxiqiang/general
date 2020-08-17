package per.qiang.system.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import per.qiang.common.core.constant.PageConstant;
import per.qiang.common.core.entity.Menu;
import per.qiang.common.core.pojo.*;
import per.qiang.common.core.util.TreeUtil;
import per.qiang.system.jpa.MenuRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;

    public List<VueRouter<Menu>> getUserRouters(String username) {
        List<VueRouter<Menu>> routes = new ArrayList<>();
        List<Menu> menus = menuRepository.findUserMenus(username);
        menus.forEach(menu -> {
            VueRouter<Menu> route = new VueRouter<>();
            route.setId(menu.getId().toString());
            route.setParentId(menu.getParentId().toString());
            route.setPath(menu.getPath());
            route.setComponent(menu.getComponent());
            route.setName(menu.getMenuName());
            route.setMeta(new RouterMeta(menu.getMenuName(), menu.getIcon(), true));
            routes.add(route);
        });
        return TreeUtil.buildVueRouter(routes);
    }


    public String findUserPermissions(String username) {
        List<String> userPermissions = menuRepository.findUserPermissions(username);
        return String.join(",", userPermissions);
    }

    public Map<String, Object> findMenus(Menu menu) {
        Map<String, Object> result = new HashMap<>(2);
        try {
            List<Menu> menus = menuRepository.findAll(Sort.by("orderNum"));
            List<MenuTree> trees = new ArrayList<>();
            buildTrees(trees, menus);

            if (StringUtils.equals(menu.getType(), MenuWrapper.TYPE_BUTTON)) {
                result.put(PageConstant.ROWS, trees);
            } else {
                List<? extends Tree<?>> menuTree = TreeUtil.build(trees);
                result.put(PageConstant.ROWS, menuTree);
            }
            result.put(PageConstant.TOTAL, menus.size());
        } catch (NumberFormatException e) {
            log.error("查询菜单失败", e);
            result.put(PageConstant.ROWS, null);
            result.put(PageConstant.TOTAL, 0);
        }
        return result;
    }

    private void buildTrees(List<MenuTree> trees, List<Menu> menus) {
        menus.forEach(menu -> {
            MenuTree tree = new MenuTree();
            tree.setId(menu.getId().toString());
            tree.setParentId(menu.getParentId().toString());
            tree.setLabel(menu.getMenuName());
            tree.setComponent(menu.getComponent());
            tree.setIcon(menu.getIcon());
            tree.setOrderNum(menu.getOrderNum());
            tree.setPath(menu.getPath());
            tree.setType(menu.getType());
            tree.setPerms(menu.getPerms());
            trees.add(tree);
        });
    }

    public List<Menu> findMenuList(Menu menu) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        if (StringUtils.isNotBlank(menu.getMenuName())) {
            ExampleMatcher matcher = ExampleMatcher.matching()
                    .withMatcher("menuName", ExampleMatcher.GenericPropertyMatcher::contains);
            Example<Menu> example = Example.of(menu, matcher);
            return menuRepository.findAll(example, sort);
        }
        return menuRepository.findAll(sort);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveMenu(Menu menu) {
        if (null != menu.getId()) menu.setModifyTime(new Date());
        else menu.setCreateTime(new Date());
        setMenu(menu);
        menuRepository.save(menu);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteMenus(String[] ids) {
        Arrays.stream(ids).forEach(id -> delete(Long.parseLong(id)));
    }

    private void setMenu(Menu menu) {
        if (menu.getParentId() == null) {
            menu.setParentId(MenuWrapper.TOP_MENU_ID);
        }
        if (MenuWrapper.TYPE_BUTTON.equals(menu.getType())) {
            menu.setPath(null);
            menu.setIcon(null);
            menu.setComponent(null);
            menu.setOrderNum(null);
        }
    }

    private void delete(Long id) {
        menuRepository.deleteById(id);
        menuRepository.deleteRoleMenuByMenuId(id);
        List<Menu> menus = menuRepository.findByParentId(id);
        if (CollectionUtils.isNotEmpty(menus)) {
            menus.forEach(menu -> delete(menu.getId()));
        }
    }
}
