package com.dxhy.order.consumer.model.manager;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author fankunfeng
 * @Date 2019-04-22 10:18:27
 * @Describe 菜单树
 */
@Setter
@Getter
public class MenuTree {
    private Integer id;
    private Integer parentId;
    private String parentName;
    private String name;
    private String url;
    private String perms;
    private Integer type;
    private String icon;
    private Integer orderNum;
    private String systemSign;
    private List<MenuTree> children;

    public MenuTree(Integer id, Integer parentId, String parentName, String name, String url, String perms, Integer type, String icon, Integer orderNum, String systemSign) {
        this.id = id;
        this.parentId = parentId;
        this.parentName = parentName;
        this.name = name;
        this.url = url;
        this.perms = perms;
        this.type = type;
        this.icon = icon;
        this.orderNum = orderNum;
        this.systemSign = systemSign;
        children = new ArrayList<>();
    }

    public void add(MenuTree tree1) {
        children.add(tree1);
    }

    public static List<MenuTree> buildTree(List<MenuTree> list, Integer parentId) {
        List<MenuTree> menus = new ArrayList<>();
        for (MenuTree menu : list) {
            Integer menuId = menu.getId();
            Integer pid = menu.getParentId();
            if (pid.equals(parentId)) {
                List<MenuTree> menuLists = buildTree(list, menuId);
                menu.setChildren(menuLists);
                menus.add(menu);
            }
        }
        return menus;
    }
}
