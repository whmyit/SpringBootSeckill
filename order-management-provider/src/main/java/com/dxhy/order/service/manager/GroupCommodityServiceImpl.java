package com.dxhy.order.service.manager;

import com.dxhy.order.api.ApiGroupCommodityService;
import com.dxhy.order.api.ApiInvoiceCommonService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.dao.CommodityDao;
import com.dxhy.order.dao.GroupCommodityDao;
import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.GroupCommodity;
import com.dxhy.order.response.StatusCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分组税编业务实现层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:03
 */
@Slf4j
@Service
public class GroupCommodityServiceImpl implements ApiGroupCommodityService {

    @Resource
    private GroupCommodityDao groupCommodityDao;
    
    @Resource
    private CommodityDao commodityDao;
    
    @Resource
    private ApiInvoiceCommonService apiInvoiceCommonService;
    @Override
    public List<GroupCommodity> queryGroupList(String userId) {
        List<GroupCommodity> list = groupCommodityDao.selectGroupListByUserId(userId);
        return list;
    }

    @Override
    public R saveGroup(GroupCommodity groupCommodity, List<String> shList) {
        //分组目前只有一级  不考虑是否为叶子节点
        groupCommodity.setIsLeaf("0");
        R r = new R();
        String id = groupCommodity.getId();
        if (StringUtils.isNotBlank(id)) {
            log.info("修改接口开始执行 id = {}", id);
            int i = groupCommodityDao.updateGroup(groupCommodity, shList);
            if (i > 0) {
                R.ok().put("msg", "保存成功");
            } else {
                R.ok().put("msg", "保存失败").put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
            }
        } else {
            groupCommodity.setId(apiInvoiceCommonService.getGenerateShotKey());
            log.info("添加接口开始执行");
            int i = groupCommodityDao.insertGroup(groupCommodity);
            if (i > 0) {
                R.ok().put("msg", "添加成功");
            } else {
                R.ok().put("msg", "添加失败").put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
            }
        }
        return r;
    }

	@Override
	public R checkGroup(Map<String, String> map) {
		R r = new R();
		String groupCode = map.get("groupCode");
		String groupName = map.get("groupName");
		String userId = map.get("userId");
        
        // 集团账号，根据userid和groupname去查询
		if (StringUtils.isNotBlank(groupCode)) {
			int i = groupCommodityDao.selectGroupByCodeAndUserId(groupCode, userId);
			if (i > 0) {
				r.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999).put("msg", "该编码的分组已经存在");
				log.info("校验失败 groupCode = {}", groupCode);
			} else {
				R.ok().put("msg", "校验通过");
				log.info("校验通过 groupCode = {}", groupCode);
			}
		}
		if (StringUtils.isNotBlank(groupName)) {
			int i = groupCommodityDao.selectGroupByNameAndUserId(groupName, userId);
			if (i > 0) {
				r.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999).put("msg", "该名称的分组已经存在");
				log.info("校验失败 groupName = {}", groupCode);
			} else {
				R.ok().put("msg", "校验通过");
				log.info("校验通过 groupName = {}", groupCode);
			}
		}
        
        
        return r;
	}
    
    @Override
    public R removeGroup(String id, List<String> shList) {
        R r = new R();
        int i = groupCommodityDao.deleteGroupById(id, shList);
        if (i > 0) {
            //不需要删除分组下的商品 该商品改为没有分组
            int k = commodityDao.updateByGropId(id, shList);
            log.info("修改商品分组条数 {}", k);
            R.ok().put("msg", "删除分组成功");
        } else {
            R.ok().put("msg", "删除分组失败").put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
        }
        return r;
    }

    @Override
    public R uploadGrop(List<GroupCommodity> groupCommodityList) {
        R r = checkParams(groupCommodityList);
        return r;
    }
    private R checkParams(List<GroupCommodity> groupCommodityList) {
        R r = new R();
        List<Map<String, Object>> list = new ArrayList<>();
        int k = 0;
        int num = 0;
        Map<String, Object> map = new HashMap<>(10);
        if (groupCommodityList != null) {
            for (GroupCommodity groupCommodity : groupCommodityList) {
                map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_0000);
                k++;
                //获取分组编码
                String groupCode = groupCommodity.getGroupCode();
                String name = groupCommodity.getGroupName();
                String taxpayerCode = groupCommodity.getXhfNsrsbh();
                String codeVerify = "^([0-9]*$){2,20}$";
                //分组编码校验
                if (StringUtils.isNotBlank(groupCode)) {
                    if (!groupCode.matches(codeVerify)) {
                        map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                        Map<String, Object> errorMap = getMap(StatusCodes.NON_CONFORMITY, "第" + k + "行,分组编码"+groupCode +"不符合规范");
                        list.add(errorMap);
                    } else {
                        int i = groupCommodityDao.selectGroupByName(name, taxpayerCode);
                        if (i > 0) {
                            map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                            Map<String, Object> errorMap = getMap(StatusCodes.IS_REPEAT, "第" + k + "行,分组编码"+groupCode+"已经存在");
                            list.add(errorMap);
                        }
                    }
                } else {
                    map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                    Map<String, Object> errorMap = getMap(StatusCodes.IS_NULL, "第" + k + "行,分组编码不能为空");
                    list.add(errorMap);
                }
                //分组名称校验
                String gropName = groupCommodity.getGroupName();

                if (StringUtils.isNotBlank(gropName)) {
                    int length = gropName.length();
                    if (length > 100) {
                        map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                        Map<String, Object> errorMap = getMap(StatusCodes.IS_EXCESSLENGTH, "第" + k + "行,分组名称长度不能大于100");
                        list.add(errorMap);
                    }else{
                        int i = groupCommodityDao.selectGroupByName(gropName,taxpayerCode);
                        if (i > 0) {
                            map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                            Map<String, Object> errorMap = getMap(StatusCodes.IS_REPEAT, "第" + k + "行," + gropName + "分组名称已经存在");
                            list.add(errorMap);
                        }
                    }
                } else {
                    map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                    Map<String, Object> errorMap = getMap(StatusCodes.IS_NULL, "第" + k + "行,分组名称不能为空");
                    list.add(errorMap);
                }
                if (ConfigureConstant.STRING_0000.equals(map.get(OrderManagementConstant.CODE))) {
                    //目前不考虑分组的节点  写死为0
                    groupCommodity.setIsLeaf("0");
                    groupCommodity.setId(apiInvoiceCommonService.getGenerateShotKey());
                    log.info("添加接口开始执行");
                    int i = groupCommodityDao.insertGroup(groupCommodity);
                    if (i > 0) {
                        map.put("msg", "添加成功");
                        num++;
                    } else {
                        map.put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999);
                        map.put("msg", "添加失败");
                    }
                }
            }
        }
        return r.put("list", list).put("num","导入数据共"+groupCommodityList.size()+"条,成功"+num+"条");
    }
    /**
     * 封装错误信息
     * @param code
     * @param msg
     * @return
     */
    private  Map<String,Object> getMap(String code , String msg){
        Map<String, Object> map = new HashMap<>(10);
        map.put(OrderManagementConstant.CODE, code);
        map.put("msg", msg);
        return map;
    }
}
