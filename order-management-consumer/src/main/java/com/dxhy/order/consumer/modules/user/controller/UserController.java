package com.dxhy.order.consumer.modules.user.controller;

import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.model.manager.MenuTree;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.protocol.usercenter.MenuEntity;
import com.dxhy.order.consumer.protocol.usercenter.UserEntity;
import com.dxhy.order.model.R;
import com.dxhy.order.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户信息控制层
 *
 * @author ZSC-DXHY
 */
@RestController
@Slf4j
@Api(value = "用户信息", tags = {"管理模块"})
public class UserController {
    
    @Resource
    private UserInfoService userInfoService;
    
    /**
     * 在使用
     * 获取用户信息
     *
     * @return
     */
    @RequestMapping(path = {"/users/getUserInfo"}, method = {RequestMethod.POST})
    @ApiOperation(value = "获取用户信息", notes = "用户信息管理-获取用户信息")
    @SysLog(operation = "获取用户信息", operationDesc = "获取用户信息", key = "用户信息")
    public R getUserDetails() {
        R result = userInfoService.getUserInfo();
        return result;
    }
    
    
    /**
     * 在使用
     * 获取用户菜单信息
     *
     * @return
     */
    @RequestMapping(path = {"/menus"}, method = {RequestMethod.GET})
    @ApiOperation(value = "获取用户菜单", notes = "用户信息管理-获取用户菜单")
    @SysLog(operation = "获取用户菜单", operationDesc = "获取用户菜单", key = "用户信息")
    public R getMenus() {
        UserEntity ssoUser = userInfoService.getUser();
        log.info("获取的用户信息：{}", JsonUtils.getInstance().toJsonString(ssoUser));
        List<MenuEntity> menus = ssoUser.getMenus();
        List<MenuTree> m = new ArrayList<>();
        for (MenuEntity menu : menus) {
            MenuTree tree = new MenuTree(menu.getMenuId(), menu.getParentId(), "", menu.getName(),
                    //todo 用户中心提供的接口参数表中没有与systemSign<该菜单在系统中的标识，例如：'XXGL'>相关的字段，需确认？
                    menu.getUrl(), menu.getPermission(), Integer.valueOf(menu.getType()), menu.getIcon(), menu.getSort(), "");
            m.add(tree);
        }
        List<MenuTree> menuTrees = MenuTree.buildTree(m, 0);
        log.info("转换菜单树结构：{}", JsonUtils.getInstance().toJsonString(menuTrees));
        return R.ok().put("menus", menuTrees);
    }
    
    /**
     * 在使用
     * 获取后台部门信息接口
     *
     * @return
     */
    @RequestMapping(path = {"/getDepartment"}, method = {RequestMethod.GET})
    @ApiOperation(value = "获取用户部门信息", notes = "用户信息管理-获取用户部门信息")
    @SysLog(operation = "获取用户部门信息", operationDesc = "获取用户部门信息", key = "用户信息")
    public R getDepartments() {
        return R.ok().put("dept", userInfoService.getDepartment());
    }
}
