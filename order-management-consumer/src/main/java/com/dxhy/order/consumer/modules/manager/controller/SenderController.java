package com.dxhy.order.consumer.modules.manager.controller;

import com.dxhy.order.api.ApiSenderService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.SenderEntity;
import com.dxhy.order.model.page.QueryPage;
import com.dxhy.order.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 发票邮寄控制层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:35
 */
@Slf4j
@RequestMapping("/sender")
@RestController
@Api(value = "发票邮寄", tags = {"管理模块"})
public class SenderController {
    
    private static final String LOGGER_MSG = "(发票邮寄控制层)";
    
    @Reference
    private ApiSenderService apiSenderService;
    
    @Resource
    private UserInfoService userInfoService;
    
    /**
     * 在使用
     * 查询寄件人列表
     *
     * @return
     */
    @RequestMapping(value = "/senderList", method = RequestMethod.GET)
    @ApiOperation(value = "查询寄件人列表", notes = "发票邮寄-查询寄件人列表")
    public R querySenderList(@RequestParam Map<String, Object> map) {
        //获取当前用户ID
        String userId = userInfoService.getUser().getUserId().toString();
        map.put("userId", userId);
        log.info("{} 寄件人列表初始化", LOGGER_MSG);
        log.info("查询所有寄件人列表开始。。。");
        QueryPage query = new QueryPage(map);
        List<SenderEntity> list = apiSenderService.senderList(query);
        int count = apiSenderService.sendersTotal(query);
        log.info("寄件人数量为：{}", count);
        log.info("查询所有寄件人列表结束。。。");
        //分页
        PageUtils page = new PageUtils(list, count, query.getLimit(), query.getCurrPage(), true);
        return R.ok().put("page", page);
    }
    
    /**
     * 查询收件人列表
     *
     * @return
     */
    @RequestMapping(value = "/recipientsList", method = RequestMethod.GET)
    @ApiOperation(value = "查询收件人列表", notes = "发票邮寄-查询收件人列表")
    public R queryRecipientsList(@RequestParam Map<String, Object> map) {
        //获取当前用户ID
        String userId = userInfoService.getUser().getUserId().toString();
        map.put("userId", userId);
        log.info("{} 收件人列表初始化", LOGGER_MSG);
        log.info("查询所有收件人列表开始。。。");
        QueryPage query = new QueryPage(map);
        List<SenderEntity> list = apiSenderService.recipientsList(query);
        int count = apiSenderService.recipientsTotal(query);
        log.info("查询所有收件人列表结束。。。");
        log.info("查询收件人数量为：{}", count);
        //分页
        PageUtils page = new PageUtils(list, count, query.getLimit(), query.getCurrPage(), true);
        return R.ok().put("page", page);
    }
    
    /**
     * 查询寄/收件人姓名列表
     *
     * @return
     */
    @RequestMapping(value = "/nameList", method = RequestMethod.GET)
    @ApiOperation(value = "查询姓名列表", notes = "发票邮寄-查询姓名列表")
    public R nameList(@RequestParam Map<String, Object> map) {
        log.debug("{}查询寄/收件人姓名列表开始,入参为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(map));
        //获取当前用户ID
        String userId = userInfoService.getUser().getUserId().toString();
        log.debug("{}查询寄/收件人姓名列表,返回参数为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(userId));
        map.put("userId", userId);
        log.debug("{}查询寄/收件人姓名列表数据,入参为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(map));
        List<SenderEntity> list = apiSenderService.nameList(map);
        log.debug("{}查询寄/收件人姓名列表数据,返回数据为:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(list));
        return R.ok().put("list", list);
    }
    
    /**
     * 通过ID查询寄件人信息
     *
     * @return
     */
    @RequestMapping(value = "/querySenderById", method = RequestMethod.GET)
    @ApiOperation(value = "查询寄件人", notes = "发票邮寄-查询寄件人")
    public R querySenderById(@RequestParam Map<String, Object> map) {
        //获取当前用户ID
        String userId = userInfoService.getUser().getUserId().toString();
        map.put("userId", userId);
        log.info("查询寄件人开始。。。");
        SenderEntity senderEntity = apiSenderService.querySenderById(map);
        log.info("查询寄件人信息为：{}", senderEntity);
        log.info("{} 寄件人回显", LOGGER_MSG);
        return R.ok().put("senderEntity", senderEntity);
    }
    
    /**
     * 通过ID查询收件人信息
     *
     * @return
     */
    @RequestMapping(value = "/queryRecipientsById", method = RequestMethod.GET)
    @ApiOperation(value = "查询收件人", notes = "发票邮寄-查询收件人")
    public R queryRecipientsById(@RequestParam Map<String, Object> map) {
        //获取当前用户ID
        String userId = userInfoService.getUser().getUserId().toString();
        map.put("userId", userId);
        log.info("查询收件人开始。。。");
        SenderEntity senderEntity = apiSenderService.queryRecipientsById(map);
        log.info("查询收件人信息为：{}", senderEntity);
        log.info("{} 收件人回显", LOGGER_MSG);
        return R.ok().put("senderEntity", senderEntity);
    }
    
    /**
     * 保存/更新寄件人/收件人信息
     *
     * @param senderEntity
     * @return
     */
    @PostMapping("/saveOrUpdate")
    @ApiOperation(value = "保存寄件人", notes = "发票邮寄-保存寄件人")
    public R saveInvoice(SenderEntity senderEntity) {
        String type = senderEntity.getType() == null ? "" : senderEntity.getType();
        log.info("参数 {} ", senderEntity);
        //获取当前用户ID
        String userId = userInfoService.getUser().getUserId().toString();
        senderEntity.setUserId(userId);
        if (ConfigureConstant.STRING_1.equals(type)) {
            R r = apiSenderService.updateRecipients(senderEntity);
            return r;
        } else {
            R r = apiSenderService.updateSender(senderEntity);
            return r;
        }
    }
    
    /**
     * 删除寄/收件人信息
     *
     * @param params
     * @return
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除寄件人", notes = "发票邮寄-删除寄件人")
    public R deleteSender(@RequestParam Map<String, Object> params) {
        //获取当前用户ID
        String userId = userInfoService.getUser().getUserId().toString();
        params.put("userId", userId);
        //获取寄件人ID
        String id = params.get("id").toString();
        log.info("接收参数为{}", id);
        apiSenderService.delete(params);
        return R.ok().put("msg", "删除成功").put(OrderManagementConstant.CODE, ConfigureConstant.STRING_0000);
    }
}
