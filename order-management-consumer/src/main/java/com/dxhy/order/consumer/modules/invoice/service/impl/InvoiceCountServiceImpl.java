package com.dxhy.order.consumer.modules.invoice.service.impl;

import com.dxhy.order.api.ApiOrderInvoiceInfoService;
import com.dxhy.order.api.ApiTaxEquipmentService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.modules.fiscal.service.UnifyService;
import com.dxhy.order.consumer.modules.invoice.service.InvoiceCountService;
import com.dxhy.order.model.InvoiceCount;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.model.a9.sld.SldKcByFjhResponseExtend;
import com.dxhy.order.model.a9.sld.SldKcRequest;
import com.dxhy.order.model.a9.sld.SldKcmxByFjh;
import com.dxhy.order.model.vo.*;
import com.dxhy.order.utils.DateUtilsLocal;
import com.dxhy.order.utils.HttpInvoiceRequestUtil;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;

/**
 * @Author fankunfeng
 * @Date 2019-04-11 16:14:21
 * @Describe
 */
@Service
@Slf4j
public class InvoiceCountServiceImpl implements InvoiceCountService {
    
    @Reference
    private ApiOrderInvoiceInfoService orderInvoiceInfoService;
    
    @Resource
    private UnifyService unifyService;
    
    @Reference
    ApiTaxEquipmentService apiTaxEquipmentService;
    
    
    @Override
    public List<CountHjjeVO> getSixMonthOfInvoiceHjje(List<String> nsrsbh) {
        List<InvoiceCount> invoiceCounts = getHjjeAndHjse(nsrsbh);
        log.info("统计返回结果：{}", JsonUtils.getInstance().toJsonString(invoiceCounts));
        if (invoiceCounts == null || invoiceCounts.size() == 0) {
            return null;
        }
        /**
         *组装返回数据
         *1.用Map，key为月份，value为 CountHjjeVO
         */
        Map<String, CountHjjeVO> map = new LinkedHashMap<>();
        for (InvoiceCount invoiceCount : invoiceCounts) {
            CountHjjeVO countHjjeVO;
            if (map.containsKey(invoiceCount.getTime())) {
                countHjjeVO = map.get(invoiceCount.getTime());
            } else {
                countHjjeVO = new CountHjjeVO();
                countHjjeVO.setLzje(ConfigureConstant.STRING_0);
                countHjjeVO.setHzje(ConfigureConstant.STRING_0);
            }
            if (ConfigureConstant.STRING_0.equals(invoiceCount.getKplx())) {
                countHjjeVO.setLzje(invoiceCount.getHjje());
            } else if (ConfigureConstant.STRING_1.equals(invoiceCount.getKplx())) {
                countHjjeVO.setHzje(invoiceCount.getHjje());
            }
            countHjjeVO.setTime(invoiceCount.getTime());
            countHjjeVO.setHjje(String.valueOf(new BigDecimal(countHjjeVO.getLzje()).add(new BigDecimal(countHjjeVO.getHzje()))));
            map.put(invoiceCount.getTime(), countHjjeVO);
        }
        List<CountHjjeVO> list = new ArrayList<>();
        List<CountHjjeVO> key = new ArrayList<>(map.values());
    
        for (int i = 0; i < key.size(); i++) {
            CountHjjeVO countHjjeVO = key.get(i);
            /**
             * 添加月度环比
             */
            if (i != key.size() && i + 1 < key.size()) {
                String currentHjje = countHjjeVO.getHjje();
                String lastHjje = key.get(i + 1).getHjje();
                if (StringUtils.isBlank(lastHjje) || ConfigureConstant.STRING_000.equals(lastHjje)) {
                } else {
                    String yhb = (new BigDecimal(currentHjje).subtract(new BigDecimal(lastHjje))).divide(new BigDecimal(lastHjje), ConfigureConstant.INT_2, RoundingMode.HALF_UP).multiply(new BigDecimal(ConfigureConstant.INT_100)).setScale(ConfigureConstant.INT_0).toPlainString() + ConfigureConstant.STRING_PERCENT;
                    countHjjeVO.setYhb(yhb);
                }
            
            }
    
            list.add(countHjjeVO);
        }
        return list;
    }

    @Override
    public List<CountHjseVO> getSixMonthOfInvoiceHjse(List<String> nsrsbh) {
        List<InvoiceCount> invoiceCounts = getHjjeAndHjse(nsrsbh);
        log.info("统计返回结果：{}",JsonUtils.getInstance().toJsonString(invoiceCounts));
        if (invoiceCounts == null || invoiceCounts.size() == 0) {
            return null;
        }
        //组装返回数据
        /**
         *组装返回数据
         *1.用Map，key为月份，value为 CountHjseVO
         */
        Map<String, CountHjseVO> map = new LinkedHashMap<>();
        for (InvoiceCount invoiceCount : invoiceCounts) {
            CountHjseVO countHjseVO;
            if (map.containsKey(invoiceCount.getTime())) {
                countHjseVO = map.get(invoiceCount.getTime());
            } else {
                countHjseVO = new CountHjseVO();
                countHjseVO.setLzse(ConfigureConstant.STRING_0);
                countHjseVO.setHzse(ConfigureConstant.STRING_0);
            }
            if (ConfigureConstant.STRING_0.equals(invoiceCount.getKplx())) {
                countHjseVO.setLzse(invoiceCount.getHjse());
            } else if (ConfigureConstant.STRING_1.equals(invoiceCount.getKplx())) {
                countHjseVO.setHzse(invoiceCount.getHjse());
            }
            countHjseVO.setHjse(String.valueOf(new BigDecimal(countHjseVO.getLzse()).add(new BigDecimal(countHjseVO.getHzse()))));
            countHjseVO.setTime(invoiceCount.getTime());
            map.put(invoiceCount.getTime(), countHjseVO);
        }
        List<CountHjseVO> list = new ArrayList<>();
        List<CountHjseVO> key = new ArrayList<>(map.values());
    
        for (int i = 0; i < key.size(); i++) {
            CountHjseVO countHjseVO = key.get(i);
            /**
             * 添加月度环比
             */
            if (i != key.size() && i + 1 < key.size()) {
                String currentHjse = countHjseVO.getHjse();
                String lastHjse = key.get(i + 1).getHjse();
                if (StringUtils.isBlank(lastHjse) || ConfigureConstant.STRING_000.equals(lastHjse)) {
                } else {
                    String yhb = (new BigDecimal(currentHjse).subtract(new BigDecimal(lastHjse))).divide(new BigDecimal(lastHjse), ConfigureConstant.INT_2, RoundingMode.HALF_UP).multiply(new BigDecimal(ConfigureConstant.INT_100)).setScale(ConfigureConstant.INT_0).toPlainString() + ConfigureConstant.STRING_PERCENT;
                    countHjseVO.setYhb(yhb);
                }
            
            }
            
            list.add(countHjseVO);
        }
        return list;
    }

    private List<InvoiceCount> getHjjeAndHjse(List<String> nsrsbh) {
        log.info("根据纳税人识别号查询：{}", nsrsbh);
        Date starttime = DateUtilsLocal.getFirstDayOfNMonthAgo(6);
        Date endtime = DateUtilsLocal.getFirstDayOfNMonthAgo(-1);
        String timeFormatFlag = ConfigureConstant.STRING_0;
        String kplxFlag = ConfigureConstant.STRING_1;
        String timeFlag = ConfigureConstant.STRING_1;

        return orderInvoiceInfoService.getCountOfMoreMonth(starttime, endtime,nsrsbh, null, timeFormatFlag, timeFlag, null, kplxFlag);
    }

    @Override
    public List<CountBySldVO> getCountOfInvoiceBySld(Date starttime, Date endtime, List<String> nsrsbh, String sld) {
        log.info("入参：starttime:{}，endtime:{},nsrsbh:{},sld:{}", starttime, endtime, nsrsbh, sld);
        String timeFormatFlag = ConfigureConstant.STRING_1;
        String sldFlag = null;
        if (StringUtils.isBlank(sld)) {
            sldFlag = ConfigureConstant.STRING_1;
        }

        List<InvoiceCount> countOfMoreMonth = orderInvoiceInfoService.getCountOfMoreMonth(starttime, endtime, nsrsbh, sld, timeFormatFlag, null, sldFlag, null);
        log.info("根据受理点查询结果：{}", JsonUtils.getInstance().toJsonString(countOfMoreMonth));
        if (countOfMoreMonth == null || countOfMoreMonth.size() == 0) {
            return null;
        }
        return changeDataBySld(countOfMoreMonth);
    }

    private List<CountBySldVO> changeDataBySld(List<InvoiceCount> countOfMoreMonth) {
        List<CountBySldVO> list = new ArrayList<>();
        for (InvoiceCount invoiceCount : countOfMoreMonth) {
            //如果sld为空，跳过
            if (StringUtils.isBlank(invoiceCount.getSld())) {
                continue;
            }
            CountBySldVO vo = new CountBySldVO();
            vo.setCount(invoiceCount.getCount());
            vo.setJshj(invoiceCount.getJshj());
            vo.setHjse(invoiceCount.getHjse());
            vo.setSld(invoiceCount.getSld());
            vo.setFjh(invoiceCount.getFjh());
            vo.setSldMc(invoiceCount.getSldMc());
            vo.setHjbhje(invoiceCount.getHjje());
            vo.setNsrmc(invoiceCount.getXhfmc());
            vo.setNsrsbh(invoiceCount.getNsrsbh());
            list.add(vo);
        }
        log.info("返回值：{}", JsonUtils.getInstance().toJsonString(list));
        return list;
    }

    @Override
    public List<InvoiceCountByTimeVO> getSixMonthOfInvoice(List<String> nsrsbh) {
        log.info("根据纳税人识别号查询：{}", nsrsbh);
        Date starttime = DateUtilsLocal.getFirstDayOfNMonthAgo(6);
        Date endtime = DateUtilsLocal.getFirstDayOfNMonthAgo(-1);
        String timeFlag = ConfigureConstant.STRING_0;

        List<InvoiceCount> moneyOfMoreMonth = orderInvoiceInfoService.getMoneyOfMoreMonth(starttime, endtime, nsrsbh, timeFlag,null);
        if (moneyOfMoreMonth == null || moneyOfMoreMonth.size() == 0) {
            return null;
        }
        return changeData(moneyOfMoreMonth);
    }

	@Override
    public PageUtils getCountByTime(Date start, Date end, List<String> list, String timeFlag, String pageSize,
			String currPage) {
        log.info("入参startTime:{},endTime:{},nsrsbh:{},timeFlag:{},pageSize:{},currpage:{}", start, end, JsonUtils.getInstance().toJsonString(list),
                timeFlag, pageSize, currPage);
    	/**
         * 此处按时间查询发票统计量
         * 1.时间由前端控制
         * 2.分机号（机器编号）不控制
         * 3.发票种类代码需要统计区分
         * 注意：查询结果JQBH无效,SLD无效
         */
        String fpzldmFlag = ConfigureConstant.STRING_1;
        
        List<InvoiceCount> moneyOfMoreMonth = orderInvoiceInfoService.getMoneyOfMoreMonth(start, end, list, timeFlag, fpzldmFlag);
        log.info("结果：{}", JsonUtils.getInstance().toJsonString(moneyOfMoreMonth));
        List<InvoiceCountByTimeVO> changeData = changeData(moneyOfMoreMonth);
        
        
        int totalPage = changeData.size();
        int size = Integer.parseInt(pageSize);
        int curr = Integer.parseInt(currPage);
        
        List<InvoiceCountByTimeVO> resultList;
        
        if (curr * size > totalPage) {
            resultList = changeData.subList((curr - 1) * size, totalPage);
            
        } else {
            resultList = changeData.subList((curr - 1) * size, (curr - 1) * size + size);
            
        }
        PageUtils pageUttl =  new PageUtils(resultList, totalPage, size, curr);
        
        return  pageUttl;
    }

    /**
     * 数据转换
     * 1.把纳税人识别号放入key为时间的map,组合Map(nsrsbh,InvoiceCountByTimeVO)
     * 2.把时间放入key为时间的map
     * 3.获取Values
     *
     * @param moneyOfMoreMonth
     * @return
     */
    private List<InvoiceCountByTimeVO> changeData(List<InvoiceCount> moneyOfMoreMonth) {
        /**
         * 按照税号区分统计结果
         * key = nsrbsh
         * value = List.InvoiceCount
         */
        Map<String,List<InvoiceCount>> nsrsbhMap = new LinkedHashMap<>();
        for (InvoiceCount invoiceCount : moneyOfMoreMonth) {
            if(nsrsbhMap.containsKey(invoiceCount.getNsrsbh())){
                List<InvoiceCount> list = nsrsbhMap.get(invoiceCount.getNsrsbh());
                list.add(invoiceCount);
            }else{
                List<InvoiceCount> list = new ArrayList<>();
                list.add(invoiceCount);
                nsrsbhMap.put(invoiceCount.getNsrsbh(),list);
            }
        }
        log.info("按照税号整理结果：{}", JsonUtils.getInstance().toJsonString(nsrsbhMap));
        Set<Map.Entry<String, List<InvoiceCount>>> nsrsbhs = nsrsbhMap.entrySet();
        /**
         * 计算每个税号的合计金额，合计税额，价税合计
         */
        List<InvoiceCountByTimeVO> result = new ArrayList<>();
        for (Map.Entry<String, List<InvoiceCount>> nsrsbh : nsrsbhs) {
            //获取每个税号的统计情况
            List<InvoiceCount> invoiceCounts = nsrsbhMap.get(nsrsbh.getKey());
            Map<String, InvoiceCountByTimeVO> voLinkedHashMap = new LinkedHashMap<>();
            for (InvoiceCount fpCount : invoiceCounts) {
                if (voLinkedHashMap.containsKey(fpCount.getTime())) {
                    InvoiceCountByTimeVO count = voLinkedHashMap.get(fpCount.getTime());
                    voLinkedHashMap.put(fpCount.getTime(), insetInvoiceCount(count, fpCount));
                } else {
                    voLinkedHashMap.put(fpCount.getTime(), insetInvoiceCount(null, fpCount));
                }
            }
            log.info("放入map结果：{}", JsonUtils.getInstance().toJsonString(voLinkedHashMap));
            //获取转换后的统计结果
            List<InvoiceCountByTimeVO> key = new ArrayList<>(voLinkedHashMap.values());
            //增加月度环比
            for (int i = 0; i < key.size(); i++) {
                InvoiceCountByTimeVO invoiceCountByTimeVO = key.get(i);
                /**
                 * 添加月度环比
                 */
                if (i + 1 < key.size()) {
                    String currentHjse = invoiceCountByTimeVO.getHjse();
                    String lastHjse = key.get(i + 1).getHjse();
                    if (StringUtils.isBlank(lastHjse) || ConfigureConstant.STRING_000.equals(lastHjse)) {
                    } else {
                        String yhb = (new BigDecimal(currentHjse).subtract(new BigDecimal(lastHjse))).divide(new BigDecimal(lastHjse), ConfigureConstant.INT_2, RoundingMode.HALF_UP).multiply(new BigDecimal(ConfigureConstant.INT_100)).setScale(ConfigureConstant.INT_0).toPlainString() + ConfigureConstant.STRING_PERCENT;
                        invoiceCountByTimeVO.setYhb(yhb);
                    }
        
                }
            }
            result.addAll(key);
        }

        log.info("返回值：{}", JsonUtils.getInstance().toJsonString(result));
        return result;
    }

    private InvoiceCountByTimeVO insetInvoiceCount(InvoiceCountByTimeVO count2, InvoiceCount fpCount) {
        InvoiceCountByTimeVO count = new InvoiceCountByTimeVO();
        count.setXhfmc(fpCount.getXhfmc());
        count.setTime(fpCount.getTime());
        if (count2 == null) {
            count.setJshj(fpCount.getJshj());
            count.setHjse(fpCount.getHjse());
            count.setCount(fpCount.getCount());
            count.setDpCount(ConfigureConstant.STRING_0);
            count.setZpCount(ConfigureConstant.STRING_0);
            count.setPpCount(ConfigureConstant.STRING_0);
            if (ConfigureConstant.STRING_0.equals(fpCount.getFpzldm())) {
                count.setZpCount(fpCount.getCount());
            } else if (ConfigureConstant.STRING_2.equals(fpCount.getFpzldm())) {
                count.setPpCount(fpCount.getCount());
            } else if (ConfigureConstant.STRING_51.equals(fpCount.getFpzldm())) {
                count.setDpCount(fpCount.getCount());
            }
            return count;
        } else {
            count2.setJshj(String.valueOf(new BigDecimal(fpCount.getJshj()).add(new BigDecimal(count2.getJshj()))));
            count2.setHjse(String.valueOf(new BigDecimal(fpCount.getHjse()).add(new BigDecimal(count2.getHjse()))));
            count2.setCount(String.valueOf(new BigInteger(fpCount.getCount()).add(new BigInteger(count2.getCount()))));
            if (ConfigureConstant.STRING_0.equals(fpCount.getFpzldm())) {
                count2.setZpCount(String.valueOf(new BigInteger(count2.getZpCount()).add(new BigInteger(fpCount.getCount()))));
            } else if (ConfigureConstant.STRING_2.equals(fpCount.getFpzldm())) {
                count2.setPpCount(String.valueOf(new BigInteger(count2.getPpCount()).add(new BigInteger(fpCount.getCount()))));
            } else if (ConfigureConstant.STRING_51.equals(fpCount.getFpzldm())) {
                count2.setDpCount(String.valueOf(new BigInteger(count2.getDpCount()).add(new BigInteger(fpCount.getCount()))));
            }
            return count2;
        }
    }
    
    @Override
    public R getFpyl(CountToB countToB, String terminalCode, String nsrmc) {
    
        SldKcRequest kccxRequest = new SldKcRequest();
        kccxRequest.setNsrsbh(countToB.getTaxpayerCode());
        if (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode)) {
            kccxRequest.setJqbh(countToB.getFjh());
        } else {
            kccxRequest.setFjh(countToB.getFjh());
        }
        SldKcByFjhResponseExtend sldKcByFjhResponseExtend = HttpInvoiceRequestUtil.queryKcxxByFjh(OpenApiConfig.querykcxxByFjh, kccxRequest, terminalCode);
    
        List<SldKcmxByFjh> kcmxes = sldKcByFjhResponseExtend.getKcmxes();
        if (ConfigureConstant.STRING_9999.equals(sldKcByFjhResponseExtend.getStatusCode()) || kcmxes == null || kcmxes.size() == 0) {
            log.error("未查到剩余票量相关数据！");
        
            return null;
        }
        log.info("查询开票余量结果：{}", JsonUtils.getInstance().toJsonString(sldKcByFjhResponseExtend));
    
        if (StringUtils.isNotBlank(countToB.getNsrmc())) {
            nsrmc = countToB.getNsrmc();
        }
    
        log.info("纳税人名称：{}", nsrmc);
        /**
         * 组装返回数据map，key = fjh，value = CountSurplusVO,
         * 1.如果分机号已存在，放入发票种类余量
         * 2.如果分机号不存在，put（fjh,CountSurplusVO）
         * 3.剩余票量
         */
        Map<String, CountSurplusVO> countSurplusMap = new LinkedHashMap<>();
        int count = ConfigureConstant.INT_0;
        for (SldKcmxByFjh kcmx : kcmxes) {
            CountSurplusVO countSurplusVO;
            if (countSurplusMap.containsKey(kcmx.getFjh())) {
                countSurplusVO = countSurplusMap.get(kcmx.getFjh());
            } else {
                countSurplusVO = new CountSurplusVO();
                countSurplusVO.setFjh(kcmx.getFjh());
                countSurplusVO.setNsrmc(nsrmc);
                countSurplusVO.setZpyl(ConfigureConstant.STRING_0);
                countSurplusVO.setPpyl(ConfigureConstant.STRING_0);
                countSurplusVO.setDpyl(ConfigureConstant.STRING_0);
            }
            if (ConfigureConstant.STRING_0.equals(kcmx.getFpzlDm())) {
                countSurplusVO.setZpyl(String.valueOf(Integer.parseInt(kcmx.getFpfs()) + Integer.parseInt(countSurplusVO.getZpyl())));
                count += Integer.parseInt(kcmx.getFpfs());
            }
            if (ConfigureConstant.STRING_2.equals(kcmx.getFpzlDm())) {
                countSurplusVO.setPpyl(String.valueOf(Integer.parseInt(kcmx.getFpfs()) + Integer.parseInt(countSurplusVO.getPpyl())));
                count += Integer.parseInt(kcmx.getFpfs());
            }
            if (ConfigureConstant.STRING_51.equals(kcmx.getFpzlDm())) {
                countSurplusVO.setDpyl(String.valueOf(Integer.parseInt(kcmx.getFpfs()) + Integer.parseInt(countSurplusVO.getDpyl())));
                count += Integer.parseInt(kcmx.getFpfs());
            }
            countSurplusMap.put(kcmx.getFjh(), countSurplusVO);
        }
    
        Collection<CountSurplusVO> values = countSurplusMap.values();
        R r = new R(true);
        r.put(ConfigureConstant.TAB_CODE, "");
        //当月剩余可开票量 p +d+z
        r.put(OrderManagementConstant.DATA, count);
        r.put(ConfigureConstant.STRING_CONTENT, values);
        return r;
    }
    
}
