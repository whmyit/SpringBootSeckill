package com.dxhy.order.consumer.modules.manager.controller;

import com.alibaba.fastjson.JSON;
import com.dxhy.order.api.ApiCommodityService;
import com.dxhy.order.constant.*;
import com.dxhy.order.consumer.annotation.SysLog;
import com.dxhy.order.consumer.config.SystemConfig;
import com.dxhy.order.consumer.model.NewOrderExcel;
import com.dxhy.order.consumer.modules.manager.constant.CommodityEnum;
import com.dxhy.order.consumer.modules.user.service.UserInfoService;
import com.dxhy.order.consumer.protocol.usercenter.DeptEntity;
import com.dxhy.order.consumer.protocol.usercenter.UserEntity;
import com.dxhy.order.file.common.ExcelReadContext;
import com.dxhy.order.file.handle.ExcelReadHandle;
import com.dxhy.order.model.CommodityCodeInfo;
import com.dxhy.order.model.CommodityTaxClassCodeReq;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.model.entity.CommodityCodeEntity;
import com.dxhy.order.model.entity.SysDictionary;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.NsrsbhUtils;
import com.dxhy.order.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品编码控制层
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 20:25
 */
@Slf4j
@RestController
@RequestMapping("/commodity")
@Api(value = "商品信息", tags = {"管理模块"})
public class CommodityController {
    private final static String LOGGER_MSG = "(商品信息)";
    @Reference
    private ApiCommodityService commodityService;
    @Resource
    private UserInfoService userInfoService;
    
    /**
     * 查询商品编码
     * 在使用
     *
     * @return
     */
    @PostMapping("/queryCommodity")
    @ApiOperation(value = "商品信息列表查询", notes = "商品信息管理-商品信息列表查询")
    @SysLog(operation = "商品信息列表查询", operationDesc = "商品信息列表查询", key = "商品信息管理")
    public R queryCommodity(@RequestParam Map<String, Object> map) {
    
        log.info("参数 {}", map);
        String xhfNsrsbh = (String) map.get("xhfNsrsbh");
        if (StringUtils.isBlank(xhfNsrsbh)) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
    
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);

        com.dxhy.order.model.PageUtils pageUtil = commodityService.queryCommodity(map, shList);
        return R.ok().put("page", pageUtil);
    }
    
    /**
     * 保存商品信息
     *
     * @return
     */
    @PostMapping("/saveCommodity")
    @ApiOperation(value = "商品信息保存", notes = "商品信息管理-商品信息保存")
    @SysLog(operation = "商品信息保存", operationDesc = "商品信息保存", key = "商品信息管理")
    public R saveCommodity(CommodityCodeEntity commodityCodeEntity) {
        if (commodityCodeEntity == null) {
            return R.error(OrderInfoContentEnum.GENERATE_READY_ORDER_DATA_ERROR);
        } else if (StringUtils.isBlank(commodityCodeEntity.getXhfNsrsbh())) {
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
        String[] xfshs = JsonUtils.getInstance().fromJson(commodityCodeEntity.getXhfNsrsbh(), String[].class);
        if (xfshs.length > 1) {
            log.error("当前操作不支持多税号进行操作.请求参数:{}", JsonUtils.getInstance().toJsonString(xfshs));
            return R.error(OrderInfoContentEnum.MORE_NSRSBH_ERROR);
        }
        String nsrsbh = xfshs[0];
    
    
        commodityCodeEntity.setXhfNsrsbh(nsrsbh);
        log.info("参数：{}", commodityCodeEntity);
        //获取当前登录人ID
        Long userId = userInfoService.getUser().getUserId();
        commodityCodeEntity.setUserId(userId.toString());
        if (StringUtils.isNotBlank(commodityCodeEntity.getTaxRate()) && commodityCodeEntity.getTaxRate().contains(ConfigureConstant.STRING_PERCENT)) {
            commodityCodeEntity.setTaxRate(StringUtil.formatSl(commodityCodeEntity.getTaxRate()));
        }
        boolean flag = commodityService.addOrEditCommodity(commodityCodeEntity);
        log.info("返回值：{}", flag);
        return flag ? R.ok().put("msg", "保存成功") : R.ok().put(OrderManagementConstant.CODE, ConfigureConstant.STRING_9999).put("msg", "保存失败");
    }
    
    /**
     * 删除
     *
     * @return
     */
    @PostMapping("/deleteCommodity")
    @ApiOperation(value = "商品信息删除", notes = "商品信息管理-商品信息删除")
    @SysLog(operation = "商品信息删除", operationDesc = "商品信息删除", key = "商品信息管理")
    public R deleteCommodity(@RequestBody String ids) {
        if (StringUtils.isBlank(ids)) {
            return R.error(OrderInfoContentEnum.INVOICE_PARAM_ERROR);
        }
    
        List<Map> idList = JSON.parseArray(ids, Map.class);
        log.info("参数：{}", ids);
        R r = commodityService.deleteCommodity(idList);
        log.info("返回值：{}", r);
        return r;
    }
    
    /**
     * 获取修改页面需要的参数
     *
     * @return
     */
    @PostMapping("/queryCommodityById")
    @ApiOperation(value = "商品信息查询", notes = "商品信息管理-商品信息查询")
    @SysLog(operation = "商品信息查询", operationDesc = "商品信息根据ID查询", key = "商品信息管理")
    public R queryCommodityById(@RequestParam String id,@RequestParam String xhfNsrsbh) {
        log.info("参数：{}", id);
        if (StringUtils.isEmpty(id)) {
            return R.error("参数错误");
        }
        /**
         * 单个税号非数组
         */
        if (StringUtils.isBlank(xhfNsrsbh)) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
    
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
    
        R r = commodityService.queryCommodityById(id, shList);
        return r;
    }
    
    /**
     * 校验
     *
     * @param map
     * @return
     */
    @PostMapping("/checkRepeat")
    @ApiOperation(value = "商品信息校验重复", notes = "商品信息管理-商品信息校验重复")
    @SysLog(operation = "商品信息重复信息查询", operationDesc = "商品信息查询校验", key = "商品信息管理")
    public R checkRepeat(@RequestParam Map<String, String> map) {
        DeptEntity dept = userInfoService.getDepartment();
        String xhfNsrsbh = map.get("xhfNsrsbh");
        if (StringUtils.isBlank(xhfNsrsbh)) {
            log.error("{},请求税号为空!", LOGGER_MSG);
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
        /**
         * 根据税号判断是否为集团帐号
         */
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
        if (shList.size() > 1) {
            map.put("deptId", dept.getDeptId());
            shList = null;
        }
    
        R r = commodityService.checkRepeat(map, shList);
        return r;
    }
    
    /**
     * 导入  加上自动匹配接口
     *
     * @param file
     * @param response
     * @param request
     * @return
     * @throws Exception
     */
    /*@PostMapping("/uploadExcel")
    @ApiOperation(value = "商品信息导入", notes = "商品信息管理-商品信息导入")
    @SysLog(operation = "商品信息导入", operationDesc = "商品信息Excel导入", key = "商品信息管理")
    public R newUploadExcel(@RequestParam("file") MultipartFile file,
                            @ApiParam(name = "xhfYh", value = "销货方银行", required = true) @RequestParam(value = "xhfYh", required = true) String xhfYh,
                            @ApiParam(name = "xhfDz", value = "销货方地址", required = true) @RequestParam(value = "xhfDz", required = true) String xhfDz,
                            @ApiParam(name = "xhfDh", value = "销货方电话", required = true) @RequestParam(value = "xhfDh", required = true) String xhfDh,
                            @ApiParam(name = "xhfZh", value = "销货方账号", required = true) @RequestParam(value = "xhfZh", required = true) String xhfZh,
                            @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true) @RequestParam(value = "xhfNsrsbh", required = true) String xhfNsrsbh,
                            @ApiParam(name = "xhfMc", value = "销货方纳税人名称", required = true) @RequestParam(value = "xhfMc", required = true) String xhfMc,
                            @ApiParam(name = "userid", value = "当前登陆人id", required = true) @RequestParam(value = "userid", required = true) String userid,
                            HttpServletResponse response, HttpServletRequest request) throws Exception {
        log.debug("读取excel信息");


        List<CommodityCodeEntity> commodityCodeEntities = ExcelUtils.getExcelCommodityCodeEntityInfo(file.getInputStream(), file.getOriginalFilename());
        List<CommodityCodeEntity> commodityCodeEntitiesList = new ArrayList<>();
        for (CommodityCodeEntity commodityCodeEntity : commodityCodeEntities) {
            commodityCodeEntitiesList.add(commodityCodeEntity);
        }

        Map<String, String> headToProperty = new HashMap<>(10);


        for (SpecialInvoiceImportExcelEnum flowStatus : SpecialInvoiceImportExcelEnum.values()) {
            headToProperty.put(flowStatus.getKey(), flowStatus.getValue());
        }

        ExcelReadContext context = new ExcelReadContext(NewOrderExcel.class, headToProperty, true);

        if (StringUtils.isBlank(file.getOriginalFilename())) {
            context.setFilePrefix(".xlsx");
        }else{
            context.setFilePrefix(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
        }
        context.setHeadRow(2);
        context.setNeedRowIndex(false);
        context.setSheetIndex(i);
        ExcelReadHandle handle = new ExcelReadHandle(context);

        List<SpecialExcelImport> specialExcelList = handle.readFromExcel(file.getInputStream(), SpecialExcelImport.class);

        //获取url  判断是否使用自动匹配
        Map<String, String> paraMap = new HashMap<>(10);
        paraMap.put("xhfYh", xhfYh);
        paraMap.put("xhfDz", xhfDz);
        paraMap.put("xhfDh", xhfDh);
        paraMap.put("xhfZh", xhfZh);
        paraMap.put("xhfNsrsbh", xhfNsrsbh);
        paraMap.put("xhfMc", xhfMc);
        paraMap.put("userid", userid);
        
        SysDictionary stringString = commodityService.querySysDictionary();
        R r;
        if (stringString != null) {
            String delFlag = stringString.getDelFlag();
            //0  使用  1不使用
            if (ConfigureConstant.STRING_0.equals(delFlag)) {
                r = intelligenceCode(commodityCodeEntitiesList, paraMap);
                r.put("switchType", "0");
            } else {
                r = noIntelligenceCode(commodityCodeEntitiesList, paraMap);
                r.put("switchType", ConfigureConstant.STRING_1);
            }
        } else {
            //为空默认使用不智能匹配
            r = noIntelligenceCode(commodityCodeEntitiesList, paraMap);
            r.put("switchType", ConfigureConstant.STRING_1);
        }
        return r;
    }
    */
    
    /**
     * 不使用智能编码
     *
     * @param commodityCodeEntityList
     * @param paraMap
     * @return
     */
    private R noIntelligenceCode(List<CommodityCodeEntity> commodityCodeEntityList, Map<String, String> paraMap) {
        String userId = paraMap.get("userid");
        log.info("用户Id：{}", userId);
        log.debug("商品编码导入");
        for (CommodityCodeEntity commodityCodeEntity : commodityCodeEntityList) {
            commodityCodeEntity.setUserId(userId);
        }
        return commodityService.uploadCommodityCode(commodityCodeEntityList);
    }
    
    /**
     * 启用智能匹配
     *
     * @param commodityCodeEntityList
     * @param paraMap
     * @return
     * @throws Exception
     */
    private R intelligenceCode(List<CommodityCodeEntity> commodityCodeEntityList, Map<String, String> paraMap) throws Exception {
        SysDictionary sysDictionary = commodityService.querySysDictionary();
        //遍历  调用接口  获取大数据接口
        String value = sysDictionary.getValue();
        List<Map<String, Object>> errorList = new ArrayList<>();
        int k = 0;
        boolean flag = true;
        Map<String, Object> errorMap = null;
        List errorMapMsg = null;
        for (CommodityCodeEntity codeEntity : commodityCodeEntityList) {
            errorMap = new HashMap<>(5);
            errorMapMsg = new ArrayList();
            k++;
            CommodityCodeEntity commodityCodeEntity = codeEntity;
            //校验必填项
            //商品名称
            String merchandiseName = commodityCodeEntity.getMerchandiseName();
        
            String taxLogo = commodityCodeEntity.getTaxLogo();
            if (StringUtils.isBlank(merchandiseName)) {
                errorMapMsg.add("未填写商品名称");
                flag = false;
            } else {
                // 不为空的时候处理 中文括号
                //中英文括号替换
                StringUtil.replaceStr(merchandiseName, true);
                commodityCodeEntity.setMerchandiseName(merchandiseName);
            }
            //含税价标志
            if (StringUtils.isBlank(taxLogo)) {
                errorMapMsg.add("未填写含税价标志");
                flag = false;
            }
            if (CollectionUtils.isNotEmpty(errorMapMsg)) {
                errorMap.put("count", "第" + k + "行");
                errorMap.put("msg", errorMapMsg);
                errorList.add(errorMap);
            }
            //校验通过  走逻辑
            if (StringUtils.isNotBlank(taxLogo) && StringUtils.isNotBlank(merchandiseName)) {
                // 创建Httpclient对象
                CloseableHttpClient httpclient = HttpClients.createDefault();
                // 建立HttpPost对象
                String url = value + commodityCodeEntity.getMerchandiseName();
                HttpGet httpGet = new HttpGet(url);
                log.info("大数据接口{}", url);
                //发送get,并返回一个HttpResponse对象
                HttpResponse httpResponse = httpclient.execute(httpGet);
                String resultString = "";
                if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    resultString = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
                    Map map = JSON.parseObject(resultString);
                    log.info("大数据返回值{}", map.toString());
                    if ("200".equals(String.valueOf(map.get(OrderManagementConstant.CODE)))) {
                        Map<String, String> mapDate = (Map<String, String>) map.get("data");
                        String taxClassCode = commodityCodeEntity.getTaxClassCode();
                        if (StringUtils.isBlank(taxClassCode)) {
                            //税收分类编码
                            commodityCodeEntity.setTaxClassCode(mapDate.get("taxKindCode"));
                        }
                        String taxRate = commodityCodeEntity.getTaxRate();
                        if (StringUtils.isBlank(taxRate)) {
                            //税率
                            String taxRate1 = handleTaxRate(mapDate.get("taxRate"));
                            commodityCodeEntity.setTaxRate(taxRate1);
                        }
                        String preferentialPoliciesType = commodityCodeEntity.getPreferentialPoliciesType();
                        if (StringUtils.isBlank(preferentialPoliciesType)) {
                            //优惠政策类型
                            String preference = mapDate.get("preference");
                            String type = "";
                            //空：非零税率，0:出口零税,1：免税，2：不征税 3:普通零税率
                            if ("出口零税".equals(preference)) {
                                type = OrderInfoEnum.LSLBS_0.getKey();
                            } else if ("免税".equals(preference)) {
                                type = OrderInfoEnum.LSLBS_1.getKey();
                            } else if ("不征税".equals(preference)) {
                                type = OrderInfoEnum.LSLBS_2.getKey();
                            } else if ("普通零税率".equals(preference)) {
                                type = OrderInfoEnum.LSLBS_3.getKey();
                            } else if ("非零税率".equals(preference)) {
                                type = "";
                            } else {
                                type = preference;
                            }
                            commodityCodeEntity.setPreferentialPoliciesType(type);
                        }
                        //税收分类名称  taxSimpleName
                        String taxClassificationName = commodityCodeEntity.getTaxClassificationName();
                        if (StringUtils.isBlank(taxClassificationName)) {
                            //税收分类名称
                            commodityCodeEntity.setTaxClassificationName(mapDate.get("taxSimpleName"));
                        }
                    }
                }
            }
        }
        log.info("数据校验", errorList.toString());
        if (flag) {
            return R.ok().put("list", commodityCodeEntityList).put("count", commodityCodeEntityList.size()).put("fail", errorList.size());
        } else {
            return R.ok().put(OrderManagementConstant.CODE, ResponseStatusCodes.PRODUCT_PRODUCT_NAME).put("errorList", errorList).put("count", commodityCodeEntityList.size()).put("fail", errorList.size());
        }
    }
    /**
     * 供优税小助手调用
     * @Description 商品信息列表查询
     * @Author xieyuanqiang
     * @Date 10:13 2018-07-21
     */
    @PostMapping("/queryCommodityInfoListByPage")
    @ApiOperation(value = "商品信息列表", notes = "商品信息管理-商品信息列表")
    @SysLog(operation = "商品信息列表查询", operationDesc = "商品信息列表查询", key = "商品信息管理")
    public R queryCommodityInfoListByPage( @ApiParam(name = "merchandiseName", value = "商品名称", required = false)@RequestParam(required = false) String merchandiseName,
                                           @ApiParam(name = "encoding", value = "购方自编码", required = false)@RequestParam(required = false)  String encoding,
                                           @ApiParam(name = "xhfNsrsbh", value = "销货方纳税人识别号", required = true)@RequestParam(required = true)  String xhfNsrsbh,
                                           @ApiParam(name = "currPage", value = "当前页面", required = false)@RequestParam(required = false) String currentPage,
                                           @ApiParam(name = "pageSize", value = "页面条数", required = false)@RequestParam(required = false) String pageSize,
                                           @ApiParam(name = "orderBy", value = "排序方式 0 : 创建时间正序排，1 : 创建时间倒叙排", required = false)@RequestParam(required = false) String orderBy) {

        log.debug("{}查询发票列表开始 入参,merchandiseName:{},encoding:{},xhfNsrsbh:{},currentPage:{},pageSize:{},orderBy:{}", LOGGER_MSG, merchandiseName,encoding,xhfNsrsbh,
                currentPage,pageSize,orderBy);
        Map<String,String> queryMap = convertToQueryMap(merchandiseName,encoding,xhfNsrsbh,currentPage,pageSize,orderBy);
        List<String> shList = NsrsbhUtils.transShListByXhfNsrsbh(xhfNsrsbh);
        PageUtils pageUtils = commodityService.queryCommodityInfoListByMap(queryMap, shList);
        return R.ok().put("data", pageUtils);
    }

    /**
     * 查询参数转换为map
     * @param merchandiseName
     * @param encoding
     * @param xhfNsrsbh
     * @param currentPage
     * @param pageSize
     * @param orderBy
     * @return
     */
    private Map<String,String> convertToQueryMap(String merchandiseName, String encoding, String xhfNsrsbh, String currentPage, String pageSize, String orderBy) {

        Map<String, String> paramMap = new HashMap<String, String>(5);

        if(StringUtils.isBlank(currentPage) || StringUtils.isBlank(pageSize)){
            //如果分页参数为空 不分页
            paramMap.put("currPage","1");
            paramMap.put("pageSize","0");

        }else{
            paramMap.put("currPage",currentPage);
            paramMap.put("pageSize",pageSize);
        }
        //默认排序规则根据创建时间倒序排
        if(StringUtils.isBlank(orderBy)){
            paramMap.put("orderBy","1");
        }
        paramMap.put("merchandiseName",merchandiseName);
        paramMap.put("encoding",encoding);
        paramMap.put("xhfNsrsbh",xhfNsrsbh);
        return paramMap;

    }


    /**
     * @Description 成品油商品信息列表查询
     * @Author xieyuanqiang
     * @Date 10:13 2018-07-21
     */
    @PostMapping("/queryCpyCommodityInfoList")
    @ApiOperation(value = "商品信息成品油列表", notes = "商品信息管理-商品信息成品油列表")
    @SysLog(operation = "商品信息成品油列表查询", operationDesc = "商品信息成品油列表查询", key = "商品信息管理")
    public R queryCpyCommodityInfoList(@RequestParam String merchandiseName, @RequestParam String encoding,
                                       @RequestParam String xhfNsrsbh, @RequestParam String cpylx) {
        log.info("成品油商品信息列表查询 参数 merchandiseName {} encoding {}", merchandiseName, encoding);
        if (StringUtils.isBlank(xhfNsrsbh)) {
            return R.error(OrderInfoContentEnum.TAXCODE_ISNULL);
        }
        List<String> shList = JSON.parseArray(xhfNsrsbh, String.class);
        List<CommodityCodeEntity> dataList = commodityService.queryCommodityInfoList(merchandiseName, encoding, shList, cpylx);
        return R.ok().put("data", dataList);
    }
    
    /**
     * 获取优惠政策类型列表
     *
     * @return
     */
    @RequestMapping(value = "/querypoLicyTypeList", method = RequestMethod.GET)
    @ApiOperation(value = "商品信息优惠政策列表", notes = "商品信息管理-商品信息优惠政策列表")
    @SysLog(operation = "商品优惠政策列表查询", operationDesc = "商品优惠政策列表查询", key = "商品信息管理")
    public R querypoLicyTypeList() {
        R r = commodityService.querypoLicyTypeList();
        return r;
    }
    
    /**
     * 保存导入信息 (智能匹配)
     *
     * @param commodityCodeEntities
     * @return
     */
    @PostMapping("/saveProducts")
    @ApiOperation(value = "商品信息保存", notes = "商品信息管理-商品信息保存")
    @SysLog(operation = "商品信息保存信息", operationDesc = "商品信息保存", key = "商品信息管理")
    public R saveProducts(@RequestBody List<CommodityCodeEntity> commodityCodeEntities) {
        //获取当前登录人ID
        Long userId = userInfoService.getUser().getUserId();
        List<CommodityCodeEntity> list = new ArrayList<>();
        for (CommodityCodeEntity commodityCodeEntity : commodityCodeEntities) {
            //优惠政策类型
            String preference = commodityCodeEntity.getPreferentialPoliciesType();
            String type = "";
            //空：非零税率，0:出口零税,1：免税，2：不征税 3:普通零税率
            if ("出口零税".equals(preference)) {
                type = "0";
            } else if ("免税".equals(preference)) {
                type = "1";
            } else if ("不征税".equals(preference)) {
                type = "2";
            } else if ("普通零税率".equals(preference)) {
                type = "3";
            } else if ("非零税率".equals(preference)) {
                type = "";
            } else {
                type = preference;
            }
            commodityCodeEntity.setUserId(userId.toString());
            list.add(commodityCodeEntity);
        }
        return commodityService.checkParams(list);
        
    }
    
    /**
     * 商品模板下载接口
     */
    @ApiOperation(value = "商品模板下载接口", notes = "商品信息管理-商品模板下载接口")
    @RequestMapping(value = "/downloadFile", method = RequestMethod.GET)
    @SysLog(operation = "商品模板下载接口", operationDesc = "商品模板下载接口", key = "商品模板下载接口")
    public void downloadFile(HttpServletRequest request, HttpServletResponse response) {
        log.info("商品税编模板");
        try {
            String resource = "";
            //weblogic获取附件地址单独处理
            if (ConfigureConstant.STRING_1.equals(SystemConfig.webServerType)) {
                resource = SystemConfig.downloadFileUrl + "CommodityCodeExcel.xlsx";
                log.info("文件路径：{}", resource);
            } else {
                resource = Thread.currentThread().getContextClassLoader().getResource("download/CommodityCodeExcel.xlsx").getPath();
            }
            response.reset();
            response.setContentType("application/x-msdownload");
            response.addHeader("Content-Disposition",
                    "attachment;filename=" + new String("商品税编模板".getBytes("gb2312"), "ISO8859-1") + ".xlsx");
            response.getOutputStream().write(FileUtils.readFileToByteArray(new File(resource)));
            log.info("商品税编模板下载完毕");
        } catch (Exception e) {
            log.error("商品税编模板下载异常 e:{}", e);
            throw new RuntimeException("商品税编模板下载异常");
        }
    }
    
    /**
     * 同步集团税编接口
     *
     * @date: Created on 2018年7月25日 下午3:30:28
     */
    @ApiOperation(value = "同步集团税编接口", notes = "商品信息管理-同步集团税编接口")
    @PostMapping("/syncGroupTaxClassCode")
    @SysLog(operation = "同步集团税编接口", operationDesc = "同步集团税编接口", key = "同步集团税编接口")
    public R syncGroupTaxClassCode(@RequestBody(required = true) CommodityTaxClassCodeReq groupTaxList) {
        log.info("同步集团税编接口,入参：{}", groupTaxList);
        R r;
        try {
            if (groupTaxList.getXhfNsrsbh().size() > 1) {
                log.error("不支持多税号同步接口", groupTaxList);
                return R.error(OrderInfoContentEnum.MORE_NSRSBH_ERROR);
            }
            String nsrsbh = groupTaxList.getXhfNsrsbh().get(0);
            UserEntity user = userInfoService.getUser();
            DeptEntity dept = userInfoService.querySysDeptEntityByTaxplayercode(nsrsbh);
            r = commodityService.syncGroupTaxClassCode(groupTaxList.getTaxClassCodeIdArray(),String.valueOf(user.getUserId()),nsrsbh,dept.getName());
        } catch (Exception e) {
            log.error("同步集团税编接口异常 e:{}", e);
            return R.error("同步集团税编接口异常");
        }
        return r;
    }
    
    /**
     * 初始化企业税收信息接口
     */
    @ApiOperation(value = "初始化企业税收信息接口", notes = "商品信息管理-初始化企业税收信息接口")
    @PostMapping("/initCommodityTaxClassCode")
    @SysLog(operation = "初始化企业税收信息接口", operationDesc = "初始化企业税收信息接口", key = "初始化企业税收信息接口")
    public R initCommodityTaxClassCode(@RequestBody(required = true) List<CommodityCodeInfo> info) {
        log.info("同步集团税编接口");
        
        try {
            commodityService.initCommodityTaxClassCode(info);
            log.info("初始化企业税收信息接口");
        } catch (Exception e) {
            log.error("初始化企业税收信息接口 e:{}", e);
            return R.error("初始化企业税收信息接口异常");
        }
        return R.ok();
    }
    
    /**
     * 企业商品库启用数据状态处理
     *
     * @param commodityIds 集团id
     * @param commodityIds 数据状态 0-启用；1-停用
     */
    @ApiOperation(value = "企业商品库启用数据状态处理", notes = "商品信息管理-企业商品库启用数据状态处理")
    @PostMapping("/commodityHandleDataStatus")
    @SysLog(operation = "企业商品库启用数据状态处理", operationDesc = "企业商品库启用数据状态处理", key = "企业商品库启用数据状态处理")
    public R commodityHandleDataStatus(@RequestBody() String commodityIds) {
    
        /**
         * 请求参数
         * {
         *   "dataStatus": "0",
         *   "commodityIds": [{"id":"423470035945869312","xhfNsrsbh":"15000120561127953X"},{"id":"423618689990086657","xhfNsrsbh":"150001194112132161"},{"id":"423470035945869312","xhfNsrsbh":"15000120561127953X"},{"id":"423618689990086657","xhfNsrsbh":"150001194112132161"},{"id":"423470035945869312","xhfNsrsbh":"15000120561127953X"},{"id":"423618689990086657","xhfNsrsbh":"150001194112132161"},{"id":"423470035945869312","xhfNsrsbh":"15000120561127953X"},{"id":"423618689990086657","xhfNsrsbh":"150001194112132161"}]
         * }
         */
        log.info("企业商品库启用数据状态处理，商品id:{}", commodityIds);
    
        R r;
        try {
            if (StringUtils.isBlank(commodityIds)) {
                return R.error(OrderInfoContentEnum.INVOICE_PARAM_ERROR);
            }
            Map mapList = JsonUtils.getInstance().parseObject(commodityIds, Map.class);
            String dataStatus = (String) mapList.get("dataStatus");
            List<Map> idList = JSON.parseArray(mapList.get("commodityIds").toString(), Map.class);
    
    
            r = commodityService.commodityHandleDataStatus(idList, dataStatus);
        } catch (Exception e) {
            log.error("处理失败 e:{}", e);
            return R.error("接口异常");
        }
        return r;
    }
    
    /**
     * 商品模板导入
     *
     * @param file
     * @throws Exception
     */
    @PostMapping("/uploadCommodityExcel")
    @ApiOperation(value = "商品信息模板导入", notes = "商品信息管理-商品信息模板导入")
    @SysLog(operation = "商品信息模板导入", operationDesc = "商品信息模板导入", key = "商品信息管理")
    public R uploadCommodityExcel(@RequestParam("file") MultipartFile file) throws Exception {
        log.info("商品模板导入");


        Map<String, String> headToProperty = new HashMap<>(10);

        for(CommodityEnum flowStatus : CommodityEnum.values()){
            headToProperty.put(flowStatus.getKey(),flowStatus.getValue());
        }
        ExcelReadContext context = new ExcelReadContext(NewOrderExcel.class,headToProperty,false);
        if(StringUtils.isBlank(file.getOriginalFilename())){
            context.setFilePrefix(".xlsx");
        }else{
            context.setFilePrefix(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")));
        }
        ExcelReadHandle handle = new ExcelReadHandle(context);

        List<CommodityCodeEntity> commodityCodeEntities = handle.readFromExcel(file.getInputStream(), CommodityCodeEntity.class);


        Map<String, String> map = new HashMap<>(5);
        //获取当前登录人ID
        Long userId = userInfoService.getUser().getUserId();
        map.put("userid",userId.toString());
        R r = noIntelligenceCode(commodityCodeEntities, map);
        return r;
    }
    
    /**
     * 税率转换
     *
     * @param taxRate
     * @return
     */
    private String handleTaxRate(String taxRate) {
        //17% 16% 11% 10% 6% 5% 3% 0%
        switch (taxRate) {
            case "0.17":
                taxRate = "17%";
                break;
            case "0.16":
                taxRate = "16%";
                break;
            case "0.11":
                taxRate = "11%";
                break;
            case "0.1":
                taxRate = "10%";
                break;
            case "0.09":
                taxRate = "9%";
                break;
            case "0.07":
                taxRate = "7%";
                break;
            case "0.06":
                taxRate = "6%";
                break;
            case "0.05":
                taxRate = "5%";
                break;
            case "0.04":
                taxRate = "4%";
                break;
            case "0.03":
                taxRate = "3%";
                break;
            case "0":
                taxRate = "0%";
                break;
            default:
                break;
        }
        return taxRate;
    }
    
}
