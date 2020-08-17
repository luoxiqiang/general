package per.qiang.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import per.qiang.common.core.constant.PageConstant;
import per.qiang.common.core.entity.Dept;
import per.qiang.common.core.pojo.DeptTree;
import per.qiang.common.core.pojo.DeptWrapper;
import per.qiang.common.core.pojo.QueryRequest;
import per.qiang.common.core.pojo.Tree;
import per.qiang.common.core.util.TreeUtil;
import per.qiang.system.jpa.DeptRepository;
import per.qiang.system.mapper.DeptMapper;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class DeptService extends ServiceImpl<DeptMapper, Dept> {

    public Map<String, Object> findDepts(QueryRequest request, DeptWrapper deptWrapper) {
        Map<String, Object> result = new HashMap<>(2);
        try {
            List<Dept> depts = findDepts(deptWrapper, request);
            List<DeptTree> trees = new ArrayList<>();
            buildTrees(trees, depts);
            List<? extends Tree<?>> deptTree = TreeUtil.build(trees);

            result.put(PageConstant.ROWS, deptTree);
            result.put(PageConstant.TOTAL, depts.size());
        } catch (Exception e) {
            log.error("获取部门列表失败", e);
            result.put(PageConstant.ROWS, null);
            result.put(PageConstant.TOTAL, 0);
        }
        return result;
    }

    public List<Dept> findDepts(DeptWrapper deptWrapper, QueryRequest request) {
        QueryWrapper<Dept> queryWrapper = new QueryWrapper<>();

        if (StringUtils.isNotBlank(deptWrapper.getDeptName())) {
            queryWrapper.lambda().like(Dept::getDeptName, deptWrapper.getDeptName());
        }
        if (StringUtils.isNotBlank(deptWrapper.getCreateTimeFrom()) && StringUtils.isNotBlank(deptWrapper.getCreateTimeTo())) {
            queryWrapper.lambda()
                    .ge(Dept::getCreateTime, deptWrapper.getCreateTimeFrom())
                    .le(Dept::getCreateTime, deptWrapper.getCreateTimeTo());
        }
        return baseMapper.selectList(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createDept(Dept dept) {
        if (dept.getParentId() == null) {
            dept.setParentId(DeptWrapper.TOP_DEPT_ID);
        }
        dept.setCreateTime(new Date());
        this.save(dept);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateDept(Dept dept) {
        if (dept.getParentId() == null) {
            dept.setParentId(DeptWrapper.TOP_DEPT_ID);
        }
        dept.setModifyTime(new Date());
        this.baseMapper.updateById(dept);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteDepts(String[] deptIds) {
        this.delete(Arrays.asList(deptIds));
    }

    private void buildTrees(List<DeptTree> trees, List<Dept> depts) {
        depts.forEach(dept -> {
            DeptTree tree = new DeptTree();
            tree.setId(dept.getId().toString());
            tree.setParentId(dept.getParentId().toString());
            tree.setLabel(dept.getDeptName());
            tree.setOrderNum(dept.getOrderNum());
            trees.add(tree);
        });
    }

    private void delete(List<String> deptIds) {
        removeByIds(deptIds);
        deptIds.forEach(deptId -> this.baseMapper.deleteUserDataPermissionByDeptId(deptId));
        LambdaQueryWrapper<Dept> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dept::getParentId, deptIds);
        List<Dept> depts = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(depts)) {
            List<String> deptIdList = new ArrayList<>();
            depts.forEach(d -> deptIdList.add(String.valueOf(d.getId())));
            this.delete(deptIdList);
        }
    }
}
