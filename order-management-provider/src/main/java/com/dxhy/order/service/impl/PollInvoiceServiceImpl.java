package com.dxhy.order.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.dxhy.invoice.protocol.sl.sld.SldJspxxRequest;
import com.dxhy.invoice.protocol.sl.sld.SldJspxxResponse;
import com.dxhy.order.api.RedisService;
import com.dxhy.order.config.OpenApiConfig;
import com.dxhy.order.constant.*;
import com.dxhy.order.dao.*;
import com.dxhy.order.model.*;
import com.dxhy.order.model.a9.ResponseBaseBean;
import com.dxhy.order.model.a9.kp.*;
import com.dxhy.order.model.a9.sld.*;
import com.dxhy.order.model.entity.SpecialInvoiceReversalEntity;
import com.dxhy.order.model.mqdata.FpkjMqData;
import com.dxhy.order.service.IPollinvoiceService;
import com.dxhy.order.service.IRabbitMqSendMessage;
import com.dxhy.order.service.UnifyService;
import com.dxhy.order.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static com.dxhy.order.constant.ConfigureConstant.INVOICEING_CONTENT;

/**
 * 轮询开票业务
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2019/9/3 11:06
 */
@Service
@Slf4j
public class PollInvoiceServiceImpl implements IPollinvoiceService {
    private static final String LOGGER_MSG = "(轮询开票业务)";
    
    @Resource
    private OrderProcessInfoMapper orderProcessInfoMapper;
    
    @Resource
    private OrderInvoiceInfoMapper orderInvoiceInfoMapper;
    
    @Resource
    private OrderInfoMapper orderInfoMapper;
    
    @Resource
    private OrderItemInfoMapper orderItemInfoMapper;
    
    @Resource
    private InvoiceBatchRequestItemMapper invoiceBatchRequestItemMapper;
    
    @Resource
    private IRabbitMqSendMessage iRabbitMqSendMessage;
    
    @Resource
    private InvoiceBatchRequestMapper invoiceBatchRequestMapper;
    
    @Resource
    private UnifyService unifyService;
    
    @Resource
    private SpecialInvoiceReversalDao specialInvoiceReversalDao;
    
    @Resource
    private RedisService redisService;
    
    /**
     * 轮询开票服务接口
     *
     * @param message
     */
    @Override
    public void pollInvoice(String message) {
    
        /**
         * 开票队列接收数据统一修改为FpkjMqData对象接收数据,然后由服务进行内部转换.
         * 队列中传递简短数据方便存取
         * 去除队列中数据后判断数据是否为空,如果为空打印异常日志
         */
        FpkjMqData fpkjMqData = JsonUtils.getInstance().parseObject(message, FpkjMqData.class);
        if (ObjectUtil.isNull(fpkjMqData)) {
            log.error("{}发票开具轮训请求参数为空!", LOGGER_MSG);
        }
    
        List<String> shList = NsrsbhUtils.transShListByNsrsbh(fpkjMqData.getNsrsbh());
    
        /**
         * 从数据库中获取数据,组装请求底层数据
         */
        OrderInfo orderInfo = orderInfoMapper.selectOrderInfoByDdqqlsh(fpkjMqData.getFpqqlsh(), shList);
        if (ObjectUtil.isNull(orderInfo)) {
            log.error("{}发票开具轮训查询订单信息为空!", LOGGER_MSG);
        }
    
        List<OrderItemInfo> orderItemInfos = orderItemInfoMapper.selectOrderItemInfoByOrderId(orderInfo.getId(), shList);
        if (ObjectUtil.isEmpty(orderItemInfos)) {
            log.error("{}发票开具轮训查询订单明细信息为空!", LOGGER_MSG);
        }
    
        OrderInvoiceInfo orderInvoiceInfoReq = new OrderInvoiceInfo();
        orderInvoiceInfoReq.setFpqqlsh(fpkjMqData.getFpqqlsh());
        OrderInvoiceInfo orderInvoiceInfo = orderInvoiceInfoMapper.selectOrderInvoiceInfo(orderInvoiceInfoReq, shList);
        if (ObjectUtil.isNull(orderInvoiceInfo)) {
            log.error("{}发票开具轮训查询发票表信息为空!", LOGGER_MSG);
        }
    
    
        String terminalCode = fpkjMqData.getTerminalCode();
        /**
         * 开票业务逻辑寻轮处理
         * 1.判断是否是自动轮训开票,
         *  1.轮训开票,首先获取可用受理点,
         *  2.轮训可用受理点调用开票接口进行开票
         *  3.如果状态返回受理点不可用或者是正在使用继续循环开票,直到最后一次循环.
         *  4.返回最终状态
         * 2.判断是否是指定开票点进行开票.
         *  1.指定开票点开票,
         *  2.调用开票接口进行开票
         *  3.返回最终状态
         * 3.结果处理
         *  1.判断是否为成功状态,如果为成功状态更新对应表
         *  2.如果为开票点忙,放队列中重新开票
         *  3.如果失败,更新为失败.
         */
    
    
        /**
         * 数据转换,业务对象转换为开票接口对象进行开票
         */
        AllocateInvoicesReq allocateInvoicesReq = buildInvoiceRequest(fpkjMqData, orderInfo, orderItemInfos, orderInvoiceInfo, shList);
    
        /**
         * 轮训开票点进行开票
         * 第一次默认使用之前配置的开票点进行开票,
         */
        loopInvoice("", orderInvoiceInfo.getSld(), orderInfo, allocateInvoicesReq, fpkjMqData, terminalCode, shList);
    
    }
    
    /**
     * 调用底层请求数据组装
     *
     * @param fpkjMqData
     * @param orderInfo
     * @param orderItemInfos
     * @param orderInvoiceInfo
     * @return
     */
    public AllocateInvoicesReq buildInvoiceRequest(FpkjMqData fpkjMqData, OrderInfo orderInfo, List<OrderItemInfo> orderItemInfos, OrderInvoiceInfo orderInvoiceInfo, List<String> shList) {
        /**
         * 批次数据转换成底层接口数据
         */
        CommonInvoicesBatch commonInvoicesBatch = new CommonInvoicesBatch();
        
        commonInvoicesBatch.setFPQQPCH(fpkjMqData.getFpqqpch());
        commonInvoicesBatch.setNSRSBH(orderInfo.getXhfNsrsbh());
        commonInvoicesBatch.setSLDID(orderInvoiceInfo.getSld());
        commonInvoicesBatch.setKPJH(orderInvoiceInfo.getFjh());
        commonInvoicesBatch.setFPLB(orderInfo.getFpzlDm());
        commonInvoicesBatch.setKZZD("");
        if (OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(orderInfo.getFpzlDm())) {
            commonInvoicesBatch.setFPLX(ConfigureConstant.STRING_2);
        } else {
            commonInvoicesBatch.setFPLX(ConfigureConstant.STRING_1);
        }
        
        AllocateInvoicesReq allocateInvoicesReq = new AllocateInvoicesReq();
        CommonInvoice[] commonInvoices = new CommonInvoice[1];
        
        
        CommonInvoice commonInvoice = new CommonInvoice();
        
        /**
         * 订单业务数据转换为底层数据.
         */
        CommonInvoiceHead commonInvoiceHead = KpTransitionUtils.transitionCOMMON_INVOICE_HEAD(orderInfo);
        commonInvoiceHead.setFPQQLSH(fpkjMqData.getKplsh());

        commonInvoice.setCOMMON_INVOICE_HEAD(commonInvoiceHead);
        
        //差额开票的订单 把明细中的差额放到请求头的差额信息中 红字发票不需要传扣除额
        if (orderItemInfos.size() <= ConfigureConstant.INT_2 && StringUtils.isNotBlank(orderItemInfos.get(0).getKce())) {
            commonInvoice.getCOMMON_INVOICE_HEAD().setKCE(orderItemInfos.get(0).getKce());
        }
        
        CommonInvoiceOrder commonInvoiceOrder = KpTransitionUtils.getCOMMON_INVOICE_ORDER(orderInfo);
        commonInvoiceOrder.setDDH(orderInfo.getDdh());
        commonInvoiceOrder.setDDDATE(DateUtilsLocal.getYMDHMIS(orderInfo.getDdrq()));
        
        
        if (OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_1.getKey().equals(orderInfo.getKplx())) {
            /**
             * 可以通过原发票代码号码得到蓝字发票信息,使用原蓝票订单号作为退回单号
             */
            OrderInvoiceInfo selectInvoiceInfoByFpdhm = null;
            if (StringUtils.isNotBlank(orderInfo.getYfpDm()) && StringUtils.isNotBlank(orderInfo.getYfpHm())) {
                selectInvoiceInfoByFpdhm = orderInvoiceInfoMapper.selectOrderInvoiceInfoByFpdmAndFphm(orderInfo.getYfpDm(), orderInfo.getYfpHm(), shList);
            }
            
            
            //红字发票根据原蓝票的清单标志决定是否需要合并明细行
            if (!OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(orderInfo.getFpzlDm())) {
                boolean isQd = false;
                if (selectInvoiceInfoByFpdhm != null) {
                    if (OrderInfoEnum.QDBZ_CODE_1.getKey().equals(selectInvoiceInfoByFpdhm.getQdbz()) || OrderInfoEnum.QDBZ_CODE_3.getKey().equals(selectInvoiceInfoByFpdhm.getQdbz())) {
                        isQd = true;
                    }
                } else {
                    if (orderItemInfos.size() > ConfigureConstant.INT_8) {
                        isQd = true;
                    }
                }

                boolean result = !(OrderInfoEnum.TAX_EQUIPMENT_BWFWQ.getKey().equals(fpkjMqData.getTerminalCode()) || OrderInfoEnum.TAX_EQUIPMENT_FGBW.getKey().equals(fpkjMqData.getTerminalCode()) || OrderInfoEnum.TAX_EQUIPMENT_FGUKEY.getKey().equals(fpkjMqData.getTerminalCode())
                        || OrderInfoEnum.TAX_EQUIPMENT_BWPZ.getKey().equals(fpkjMqData.getTerminalCode())) && isQd;
                if (result) {
                    CommonInvoiceDetail[] commonInvoiceDetails = KpTransitionUtils.transitionCOMMON_INVOICE_DETAIL_Merge(orderItemInfos);
                    commonInvoice.setCOMMON_INVOICE_DETAIL(commonInvoiceDetails);
                } else {
                    CommonInvoiceDetail[] commonInvoiceDetails = KpTransitionUtils.transitionCOMMON_INVOICE_DETAIL(orderItemInfos);
                    commonInvoice.setCOMMON_INVOICE_DETAIL(commonInvoiceDetails);
                }
            } else {
                CommonInvoiceDetail[] commonInvoiceDetails = KpTransitionUtils.transitionCOMMON_INVOICE_DETAIL(orderItemInfos);
                commonInvoice.setCOMMON_INVOICE_DETAIL(commonInvoiceDetails);
            }
            commonInvoiceOrder.setTHDH(orderInfo.getThdh());
            
        } else {
            CommonInvoiceDetail[] commonInvoiceDetails = KpTransitionUtils.transitionCOMMON_INVOICE_DETAIL(orderItemInfos);
            commonInvoice.setCOMMON_INVOICE_DETAIL(commonInvoiceDetails);
        }


        //清单标志转换
        if(!OrderInfoEnum.QDBZ_CODE_4.getKey().equals(commonInvoice.getCOMMON_INVOICE_HEAD().getQD_BZ())){
            if(commonInvoice.getCOMMON_INVOICE_DETAIL().length > 8){

                if(OrderInfoEnum.QDBZ_CODE_0.getKey().equals(commonInvoice.getCOMMON_INVOICE_HEAD().getQD_BZ())){
                    //普通清单票
                    commonInvoice.getCOMMON_INVOICE_HEAD().setQD_BZ(OrderInfoEnum.QDBZ_CODE_1.getKey());

                }else if(OrderInfoEnum.QDBZ_CODE_2.getKey().equals(commonInvoice.getCOMMON_INVOICE_HEAD().getQD_BZ())){
                    //农产品收购票
                    commonInvoice.getCOMMON_INVOICE_HEAD().setQD_BZ(OrderInfoEnum.QDBZ_CODE_3.getKey());
                }
            }else{
                //无清单
                if(StrUtil.isBlank(commonInvoice.getCOMMON_INVOICE_HEAD().getQD_BZ())){
                    commonInvoice.getCOMMON_INVOICE_HEAD().setQD_BZ(OrderInfoEnum.QDBZ_CODE_0.getKey());
                }

                if(OrderInfoEnum.QDBZ_CODE_1.getKey().equals(commonInvoice.getCOMMON_INVOICE_HEAD().getQD_BZ())){
                    //普通清单票
                    commonInvoice.getCOMMON_INVOICE_HEAD().setQD_BZ(OrderInfoEnum.QDBZ_CODE_0.getKey());

                }else if(OrderInfoEnum.QDBZ_CODE_3.getKey().equals(commonInvoice.getCOMMON_INVOICE_HEAD().getQD_BZ())){
                    //农产品收购票
                    commonInvoice.getCOMMON_INVOICE_HEAD().setQD_BZ(OrderInfoEnum.QDBZ_CODE_2.getKey());
                }
            }

        }
        commonInvoice.setCOMMON_INVOICE_ORDER(commonInvoiceOrder);
        commonInvoices[0] = commonInvoice;
        
        
        allocateInvoicesReq.setCOMMON_INVOICES_BATCH(commonInvoicesBatch);
        allocateInvoicesReq.setCOMMON_INVOICE(commonInvoices);
        allocateInvoicesReq.setTerminalCode(fpkjMqData.getTerminalCode());
        return allocateInvoicesReq;
    }
    
    /**
     * 轮训请求开票接口进行开票
     *
     * @param oldSldId
     * @param sldId
     * @param orderInfo
     * @param allocateInvoicesReq
     * @param fpkjMqData
     * @param terminalCode
     * @param shList
     */
    public void loopInvoice(String oldSldId, String sldId, OrderInfo orderInfo, AllocateInvoicesReq allocateInvoicesReq, FpkjMqData fpkjMqData, String terminalCode, List<String> shList) {
        ResponseBaseBean kpResponseBean = new ResponseBaseBean();
        kpResponseBean.setCode(ConfigureConstant.STRING_9999);
        String sldid = "";
        String kpjh = "";
        String sldmc = "";
        /**
         * 默认使用延时队列处理数据
         * 如果受理点为空,设置延时队列标识为不使用,轮训开票点去开票,
         * 如果开票点轮训完毕,设置延时队列为true,存入延时队列
         * 是否放入延时队列
         */
        boolean loopAll = true;
        try {
            /**
             * 根据受理点判断是否进行轮训请求,
             * 不为空默认使用原有受理点进行开票请求
             * 如果请求后返回受理点正忙,设置受理点为空
             */
            if (!OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(orderInfo.getFpzlDm()) && StringUtils.isBlank(sldId)) {
                loopAll = false;
                //受理点为空的话轮询受理点
                List<Map<String, Object>> availableSld = getAvailableSld(orderInfo.getFpzlDm(), orderInfo.getXhfNsrsbh(),
                        orderInfo.getQdBz(), terminalCode);
                if (availableSld.size() <= 0) {
                    log.error("税号为：{},发票类别为：{}的企业没有可用的开票点", orderInfo.getXhfNsrsbh(),
                            orderInfo.getFpzlDm());
                }
                //遍历所有的受理点，找到可用的受理点，如果无可用的受理点，将数据重新放入队列
    
                int i = 0;
                for (Map<String, Object> map : availableSld) {
                    sldid = String.valueOf(map.get("sldid"));
                    kpjh = String.valueOf(map.get("kpjh"));
                    sldmc = String.valueOf(map.get("sldmc"));
                    /**
                     * 受理点和上次请求受理点一样,跳过当前受理点,使用下一个受理点进行开票,后续受理点使用改成随机
                     */
                    if (StringUtils.isNotBlank(oldSldId) && oldSldId.equals(sldId)) {
                        continue;
                    }
                    allocateInvoicesReq.getCOMMON_INVOICES_BATCH().setSLDID(sldid);
                    allocateInvoicesReq.getCOMMON_INVOICES_BATCH().setKPJH(kpjh);
                    
                    
                    //需要测试先注释掉调用开票的接口
                    kpResponseBean = HttpInvoiceRequestUtil.invoiceIssuing(OpenApiConfig.invoiceIssuing, terminalCode, allocateInvoicesReq);
                    
                    /**
                     * 挡板数据
                     */
                    /*kpResponseBean.setCode(ConfigureConstant.STRING_0000);
                    kpResponseExtend result = new kpResponseExtend();
                    result.setStatusCode(ConfigureConstant.STRING_0000);
                    result.setStatusMessage("发票开具成功");
                    kpResponseBean.setMsg("开具成功");
                    FpkjResponse fpkj = new FpkjResponse();;
                    fpkj.setStatusCode(OrderInfoContentEnum.INVOICE_ERROR_CODE_010000.getKey());
                    fpkj.setStatusMessage("开具成功");
                    result.setFpkj(fpkj);
                    kpResponseBean.setResult(result);*/
                    /**
                     * 挡板数据
                     */
                    
                    log.info("{}轮询开票点调用底层开票接口,出参:{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(kpResponseBean));
                    /**
                     * 只判断是否需要继续轮训开票,其他不需要判断,
                     */
                    if (OrderInfoEnum.INVOICE_ERROR_CODE_7503.getKey().equals(kpResponseBean.getCode())
                            && kpResponseBean.getMsg().contains(INVOICEING_CONTENT)) {
                        log.info("{} 开票点正忙，继续轮询其他开票点", LOGGER_MSG);
                        if (i == availableSld.size() - 1) {
                            loopAll = true;
                            break;
                        }
                        i++;
                    }
                }
                
                
            } else {
                log.info("{}调用发票底层开票系统入参：{}", LOGGER_MSG, JsonUtils.getInstance().toJsonString(allocateInvoicesReq));
                
                kpResponseBean = HttpInvoiceRequestUtil.invoiceIssuing(OpenApiConfig.invoiceIssuing, terminalCode, allocateInvoicesReq);
                
                /**
                 * 挡板数据
                 */
               /* kpResponseBean.setCode(ConfigureConstant.STRING_0000);
                kpResponseExtend result = new kpResponseExtend();
                result.setStatusCode(ConfigureConstant.STRING_0000);
                result.setStatusMessage("发票开具成功");
                kpResponseBean.setMsg("开具成功");
                FpkjResponse fpkj = new FpkjResponse();
                fpkj.setStatusCode(OrderInfoContentEnum.INVOICE_ERROR_CODE_010000.getKey());
                fpkj.setStatusMessage("开具成功");
                result.setFpkj(fpkj);
                kpResponseBean.setResult(result);*/
                
                /**
                 * 挡板数据
                 */
                
                log.info("调用发票底层开票系统出参：{}", JsonUtils.getInstance().toJsonString(kpResponseBean));
                
            }
            
            /**
             * 处理开票结果返回
             */
            if (ConfigureConstant.STRING_0000.equals(kpResponseBean.getCode())) {
                //查询受理点名称
                
                log.info("发票数据接收成功");
                if (!OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(orderInfo.getFpzlDm())) {
                    Set<SearchSld> searchSlds = new HashSet<>();
                    String invoiceType = orderInfo.getFpzlDm();
                    String url = OpenApiConfig.querySldList;
                    if (OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
                        url = OpenApiConfig.queryKpdXxBw;
                    } else if (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode)) {
                        url = OpenApiConfig.queryNsrXnsbxx;
                        /**
                         * 如果是新税控转换发票种类代码
                         */
                        invoiceType = CommonUtils.transFplxdm(invoiceType);
                    }
                    HttpInvoiceRequestUtil.getSldList(searchSlds, url, invoiceType, "", orderInfo.getXhfNsrsbh(), null, sldid, terminalCode);
    
                    if (StringUtils.isNotBlank(sldid) && StringUtils.isNotBlank(sldmc) && StringUtils.isNotBlank(kpjh)) {
                        // 更新发票表中的受理点信息
                        OrderInvoiceInfo orderInvoiceInfoUpdate = new OrderInvoiceInfo();
                        orderInvoiceInfoUpdate.setSld(sldid);
                        orderInvoiceInfoUpdate.setFjh(kpjh);
                        orderInvoiceInfoUpdate.setSldMc(new ArrayList<>(searchSlds).get(0).getSldMc());
                        orderInvoiceInfoUpdate.setKplsh(fpkjMqData.getKplsh());
                        orderInvoiceInfoMapper.updateInvoiceStatusByKplsh(orderInvoiceInfoUpdate, shList);
                        
                        /**
                         * todo 满足mycat临时使用的缓存,后期优化
                         * 目前开票接收成功后,添加发票请求流水号与销方税号对应关系的缓存
                         * 添加发票请求批次号与销方税号对应关系
                         *
                         */
                        String cacheKplsh = String.format(Constant.REDIS_KPLSH, fpkjMqData.getKplsh());
                        String cacheFpqqpch = String.format(Constant.REDIS_FPQQPCH, fpkjMqData.getFpqqpch());
                        if (StringUtils.isBlank(redisService.get(cacheKplsh))) {
                            redisService.set(cacheKplsh, orderInfo.getXhfNsrsbh(), Constant.REDIS_EXPIRE_TIME_DEFAULT);
                        }
                        if (StringUtils.isBlank(redisService.get(cacheFpqqpch))) {
                            redisService.set(cacheFpqqpch, orderInfo.getXhfNsrsbh(), Constant.REDIS_EXPIRE_TIME_DEFAULT);
                        }
                        
                        
                    }
                    
                }
                /**
                 * 挡板模拟推送
                 */
                /*
                String push = "{\"DDH\":\"hs_1219093513_mtz\",\"EWM\":\"Qk3CAwAAAAAAAD4AAAAoAAAASwAAAEsAAAABAAEAAAAAAIQDAAAAAAAAAAAAAAAAAAACAAAAAAAA///////////////////gAAAAAz8zw8/z88AgAAAAAz8zw8/z88AgAAA/88z8D8wzz8DgAAA/88z8D8wzz8DgAAAwM/D8w8A888PgAAAwM/D8w8A888PgAAAwM/8z8PAP88MgAAAwM/8z8PAP88MgAAAwMwM8Pw/zADMgAAAwMwM8Pw/zADMgAAA/88/w/8w8PwPgAAA/88/w/8w8PwPgAAAAAwP8M8w/MwDgAAAAAwP8M8w/MwDgAAD//zMAzzA8PzzgAAD//zMAzzA8PzzgAADwMDwwAM/zADPgAADwMDwwAM/zADPgAAAMzDP/zMPw/MPgAAAMzDP/zMPw/MPgAAAPww8PAwAMzADgAAAPww8PAwAMzADgAADPzP//PDwMzDzgAADPzP//PDwMzDzgAADzwPM/8zPzM8PgAADzwPM/8zPzM8PgAAD/DMwDA8w8MMPgAAD/DMwDA8w8MMPgAAAzADD8D/DM/PDgAAAzADD8D/DM/PDgAAADDPDM/zAMzDzgAAADDPDM/zAMzDzgAADMAzDPA8/wM8MgAADMAzDPA8/wM8MgAAD8D88wDA/8DMDgAAD8D88wDA/8DMDgAAD/8PAA8PAMD8zgAAD/8PAA8PAMD8zgAAA/z8DDwPAMzDzgAAA/z8DDwPAMzDzgAADP8/DPPA/zM8MgAADP8/DPPA/zM8MgAAD8D//wPAA/M8PgAAD8D//wPAA/M8PgAADDMD/MM8z8/8DgAADDMD/MM8z8/8DgAAD8/AAMwwAMzADgAAD8/AAMwwAMzADgAAADw//MAM/zM8MgAAADw//MAM/zM8MgAADw/AP/z8P/DwPgAADw/AP/z8P/DwPgAADDA8MPPwwwD8zgAADDA8MPPwwwD8zgAAD/DDw/PAw8zD/gAAD/DDw/PAw8zD/gAAA/MD/A8MzzAA8gAAA/MD/A8MzzAA8gAAD//zzPAAz/P//gAAD//zzPAAz/P//gAAAAAzMzMzMzMAAgAAAAAzMzMzMzMAAgAAA/8z8D8P/z8/8gAAA/8z8D8P/z8/8gAAAwMwDMPwwA8wMgAAAwMwDMPwwA8wMgAAAwMwAz/DAM8wMgAAAwMwAz/DAM8wMgAAAwM/zwAAz/8wMgAAAwM/zwAAz/8wMgAAA/88AwMw8DM/8gAAA/88AwMw8DM/8gAAAAAw8zwPAM8AAgAAAAAw8zwPAM8AAgAAA=\",\"FPLB\":\"2\",\"FPLX\":\"0\",\"FPQQLSH\":\"367887493297819648001\",\"FPQQPCH\":\"367887493297819648\",\"FP_DM\":\"5000181320\",\"FP_HM\":\"23154493\",\"FWM\":\"+9/08100>1<*054605+3*93//-2/61030*>/456574573886<2152>--0/19328*>1-8><210215+-081//>6-43034+</4125<643-<<>23\",\"HJBHSJE\":3.0188679244E8,\"JQBH\":\"661545615484\",\"JYM\":\"74877058272028533309\",\"KPHJSE\":1.811320756E7,\"KPRQ\":\"2019-12-20 14:20:04\",\"PDF_URL\":\"http://10.1.29.132:6060/null\",\"STATUSCODE\":\"2100\",\"STATUSMSG\":\"发票赋码成功\"}";
                InvoicePush invoicePush = JsonUtils.getInstance().parseObject(push, InvoicePush.class);
                invoicePush.setDDH(parseObject.getCOMMON_INVOICE()[0].getCOMMON_INVOICE_ORDER().getDDH());
                invoicePush.setFP_DM(RandomStringUtils.random(12, "0123456789"));
                invoicePush.setFP_HM(RandomStringUtils.random(8, "0123456789"));
                invoicePush.setFPLB(parseObject.getCOMMON_INVOICES_BATCH().getFPLB());
                invoicePush.setFPLX(parseObject.getCOMMON_INVOICES_BATCH().getFPLX());
                invoicePush.setFPQQLSH(parseObject.getCOMMON_INVOICE()[0].getCOMMON_INVOICE_HEAD().getFPQQLSH());
                invoicePush.setFPQQPCH(parseObject.getCOMMON_INVOICES_BATCH().getFPQQPCH());
                invoicePush.setHJBHSJE(Double.valueOf(parseObject.getCOMMON_INVOICE()[0].getCOMMON_INVOICE_HEAD().getHJJE()));
                invoicePush.setKPHJSE(Double.valueOf(parseObject.getCOMMON_INVOICE()[0].getCOMMON_INVOICE_HEAD().getHJSE()));
                invoicePush.setKPRQ(new Date());
                invoicePush.setNSRSBH(parseObject.getCOMMON_INVOICES_BATCH().getNSRSBH());
                invoicePush.setSTATUSCODE("2100");
                invoicePush.setSTATUSMSG("模拟发票开具");
                R receiveInvoice = invoiceDataService.receiveInvoice(invoicePush);
                log.info("推送结果：{}",JsonUtils.getInstance().toJsonString(receiveInvoice));


                */
                
                
            } else if (OrderInfoEnum.INVOICE_ERROR_CODE_7503.getKey().equals(kpResponseBean.getCode()) && kpResponseBean.getMsg().contains(INVOICEING_CONTENT)) {
                /**
                 * 判断请求受理点是否为空,如果不为空表示指定受理点开票不允许轮训
                 */
                if (loopAll) {
                    log.warn("{}数据存入延时队列,数据为:{}", LOGGER_MSG, fpkjMqData);
        
                    /**
                     * 数据发送到延时队列,设置消息失效时间,失效后自动指定到开票队列
                     */
                    iRabbitMqSendMessage.autoSendRabbitMqMessageDelay(orderInfo.getXhfNsrsbh(), NsrQueueEnum.DELAY_MESSAGE.getValue(), JsonUtils.getInstance().toJsonString(fpkjMqData), NsrQueueEnum.FPKJ_MESSAGE.getValue() + orderInfo.getXhfNsrsbh(), ConfigureConstant.LONG_10000);
                } else {
        
                    loopInvoice(sldid, null, orderInfo, allocateInvoicesReq, fpkjMqData, terminalCode, shList);
        
                }
    
            } else {
                log.info("{}开票接受失败", LOGGER_MSG);
                //对异常订单进行处理

                /**
                 * 经过与底层沟通,C48和A9底层返回失败数据需要更新为可编辑数据,设置code值为E1111
                 */
                String errorMsg = kpResponseBean.getMsg();
                if (OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_A9.getKey().equals(terminalCode)) {
                    errorMsg = ConfigureConstant.STRING_E1111 + errorMsg;
                }

                updateAbnormalOrder(kpResponseBean.getCode(), kpResponseBean.getMsg(), fpkjMqData.getFpqqpch(), fpkjMqData.getKplsh(), orderInfo.getXhfNsrsbh());
                
            }
        } catch (Exception e) {
            log.error("{}订单开票异常，异常信息为:{}", LOGGER_MSG, e);

            /**
             * 经过与底层沟通,C48和A9底层返回失败数据需要更新为可编辑数据,设置code值为E1111
             */
            String errorMsg = "订单调用底层接口异常";
            if (OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode) || OrderInfoEnum.TAX_EQUIPMENT_A9.getKey().equals(terminalCode)) {
                errorMsg = ConfigureConstant.STRING_E1111 + errorMsg;
            }

            updateAbnormalOrder(ConfigureConstant.STRING_9999, "订单调用底层接口异常", fpkjMqData.getFpqqpch(), fpkjMqData.getKplsh(), orderInfo.getXhfNsrsbh());
        }
        
    }
    
    /**
     * 更新异常订单的状态
     */
    private void updateAbnormalOrder(String code, String msg, String fpqqpch, String kplsh, String xhfNsrsbh) {
        
        List<String> shList = new ArrayList<>();
        shList.add(xhfNsrsbh);
        InvoiceBatchRequest selectBatchByFpqqpch = invoiceBatchRequestMapper
                .selectInvoiceBatchRequestByFpqqpch(fpqqpch, shList);
        if (selectBatchByFpqqpch == null) {
            log.error("没有找到此批次的发票信息，发票请求批次号:{}", fpqqpch);
        } else {
            // 先更新批次表的状态
            log.info("异常订单开始更新批次表:{}", fpqqpch);
            
            InvoiceBatchRequest invoiceBatchRequest = new InvoiceBatchRequest();
            invoiceBatchRequest.setStatus(code);
            invoiceBatchRequest.setMessage(msg);
            invoiceBatchRequest.setXhfNsrsbh(selectBatchByFpqqpch.getXhfNsrsbh());
            invoiceBatchRequest.setId(selectBatchByFpqqpch.getId());
            int updateByPrimaryKeySelective = invoiceBatchRequestMapper
                    .updateInvoiceBatchRequest(invoiceBatchRequest, shList);
            if (updateByPrimaryKeySelective <= 0) {
                log.error("异常订单更新发票批次表失败,:{}", fpqqpch);
            } else {
                // 修改批次明细表的状态
                InvoiceBatchRequestItem invoiceBatchRequestItem = invoiceBatchRequestItemMapper
                        .selectInvoiceBatchItemByKplsh(kplsh, shList);
                if (invoiceBatchRequestItem == null) {
                    log.error("异常订单状态更新，发票请求批次明细表不存在,fpqqpch:{},kpls:{}",
                            fpqqpch,
                            kplsh);
                } else {
                    InvoiceBatchRequestItem batchRequestItem = new InvoiceBatchRequestItem();
                    batchRequestItem.setKplsh(kplsh);
                    batchRequestItem.setStatus(code);
                    batchRequestItem.setMessage(msg);
                    
                    int batchItem = invoiceBatchRequestItemMapper
                            .updateInvoiceBatchItemByKplsh(batchRequestItem, shList);
                    if (batchItem <= 0) {
                        log.error("异常订单状态更新，批次明细表异常状态更新失败,批次号:{},开票流水:{}",
                                fpqqpch,
                                kplsh);
                    }
                    // 更新发票表状态
                    OrderInvoiceInfo orderInvoiceInfo1 = new OrderInvoiceInfo();
                    orderInvoiceInfo1.setKplsh(kplsh);
                    OrderInvoiceInfo orderInvoiceInfo = orderInvoiceInfoMapper.selectOrderInvoiceInfo(orderInvoiceInfo1, shList);
                    
                    
                    if (orderInvoiceInfo == null) {
                        log.error("异常订单，更新发票表，发票不存在,发票请求流水号:{}", kplsh);
                    } else {
                        // 更新发票表
                        String invoiceId = orderInvoiceInfo.getId();
                        String orderId = orderInvoiceInfo.getOrderInfoId();
                        String invoiceType = orderInvoiceInfo.getKplx();
                        String fpzlDm = orderInvoiceInfo.getFpzlDm();
                        String hzsqdbh = orderInvoiceInfo.getHzxxbbh();
                        orderInvoiceInfo = new OrderInvoiceInfo();
                        orderInvoiceInfo.setId(invoiceId);
                        orderInvoiceInfo.setKpzt(OrderInfoEnum.INVOICE_STATUS_3.getKey());
                        if (!OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(invoiceType)) {
                            if (OrderInfoEnum.RED_INVOICE_2.getKey().equals(orderInvoiceInfo.getChBz())) {
                                orderInvoiceInfo.setChBz(OrderInfoEnum.RED_INVOICE_3.getKey());
                            } else if (OrderInfoEnum.RED_INVOICE_5.getKey().equals(orderInvoiceInfo.getChBz())) {
                                orderInvoiceInfo.setChBz(OrderInfoEnum.RED_INVOICE_6.getKey());
                            }
                        }
                        int updateByPrimaryKeySelective2 = orderInvoiceInfoMapper
                                .updateOrderInvoiceInfoByInvoiceId(orderInvoiceInfo, shList);
                        if (updateByPrimaryKeySelective2 <= 0) {
                            log.error("异常订单，更新发票表失败");
                        }
                        //更新订单处理表
                        OrderProcessInfo orderProcessInfo1 = orderProcessInfoMapper
                                .selectByOrderId(orderId, shList);
                        if (orderProcessInfo1 == null) {
                            log.error("异常订单,更新发票处理表失败，处理表数据不存在");
                        } else {
                            OrderProcessInfo orderProcessInfo = new OrderProcessInfo();
                            orderProcessInfo.setId(orderProcessInfo1.getId());
                            orderProcessInfo.setSbyy(msg);
                            if (!OrderInfoEnum.ORDER_BILLING_INVOICE_TYPE_0.getKey().equals(invoiceType)) {
                                orderProcessInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_8.getKey());
                            } else {
                                orderProcessInfo.setDdzt(OrderInfoEnum.ORDER_STATUS_6.getKey());
                            }
                            
                            int updateProcessInfoDdztByOrderInfoId = orderProcessInfoMapper
                                    .updateOrderProcessInfoByProcessId(orderProcessInfo, shList);
                            if (updateProcessInfoDdztByOrderInfoId <= 0) {
                                log.error("订单异常，更新订单处理表异常");
                            }
                            if (StringUtils.isNotBlank(hzsqdbh) && OrderInfoEnum.INVOICE_BILLING_TYPE_1.getKey().equals(invoiceType) && OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(fpzlDm)) {
                                //如果是红字专票 根据申请单编号更新红字信息表状态
                                SpecialInvoiceReversalEntity specialInvoiceReversal = new SpecialInvoiceReversalEntity();
                                specialInvoiceReversal.setXxbbh(hzsqdbh);
                                specialInvoiceReversal.setKpzt(OrderInfoEnum.SPECIAL_INVOICE_STATUS_3.getKey());
                                int updateSpecialInvoiceReversalByCode = specialInvoiceReversalDao.updateInvoiceStatusByXxbbh(specialInvoiceReversal);
                                if (updateSpecialInvoiceReversalByCode <= 0) {
                                    log.error("更新红字申请单状态失败，红字信息表编号:{}", specialInvoiceReversal.getXxbbh());
                                }
                            }
                            
                        }
    
    
                    }
                }
            }
        }
    }
    
    /**
     * 处理受理点
     * 获取sldid,当sldid为-1时,先调用querySld接口,获取所有受理点列表
     * 再根据sldid 调用selectSldJspxx接口 获取对应的分机号
     * 最后分别赋值sldid（sldid）,kpjh（fjh）
     *
     * @param invoiceType
     * @return
     */
    private List<Map<String, Object>> getAvailableSld(String invoiceType, String nsrsbh, String qdbz, String terminalCode) {
        
        Map<String, Object> result = new HashMap<>(5);
        
        //受理点id
        String sldid;
        
        SearchSldResponse sldSearchRequest = new SearchSldResponse();
        Set<SearchSld> sldList = new HashSet<>();
        SldJspxxRequest sldJspxxRequest = new SldJspxxRequest();
    
        SldJspxxResponse sldJspxxResponse;
        sldSearchRequest.setFpzlDm(invoiceType);
        sldSearchRequest.setNsrsbh(nsrsbh);
        String url = OpenApiConfig.querySldList;
        if (OrderInfoEnum.TAX_EQUIPMENT_BW_ACTIVEX.getKey().equals(terminalCode)) {
            url = OpenApiConfig.queryKpdXxBw;
        } else if (OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey().equals(terminalCode)) {
            url = OpenApiConfig.queryNsrXnsbxx;
            /**
             * 如果是新税控转换发票种类代码
             */
            invoiceType = CommonUtils.transFplxdm(invoiceType);
    
        }
    
        if (OrderInfoEnum.QDBZ_CODE_4.getKey().equals(qdbz)) {
            // 成品油的获取成品油的受理点
            HttpInvoiceRequestUtil.getSldList(sldList, url, invoiceType, OrderInfoEnum.OIL_TYPE_1.getKey(), nsrsbh, null, null, terminalCode);
            HttpInvoiceRequestUtil.getSldList(sldList, url, invoiceType, OrderInfoEnum.OIL_TYPE_2.getKey(), nsrsbh, null, null, terminalCode);
    
    
        } else {//非成品油的受理点
            HttpInvoiceRequestUtil.getSldList(sldList, url, invoiceType, "", nsrsbh, null, null, terminalCode);
        
        }
        //查询受理点的金税盘信息
        List<Map<String, Object>> resultList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(sldList)) {
            if (!OrderInfoEnum.TAX_EQUIPMENT_C48.getKey().equals(terminalCode)) {
                //税控设备是A9的话 查询受理点和库存信息
                for (SearchSld searchSld : sldList) {
                    SldKcRequest request = new SldKcRequest();
                    sldid = searchSld.getSldId();
                    request.setFpzldm(invoiceType);
                    request.setNsrsbh(nsrsbh);
                    request.setSldid(sldid);
                    //查询受理点
                    SldKcResponse queryKcxx = HttpInvoiceRequestUtil.querySldFpfs(OpenApiConfig.querySldFpfs, request, terminalCode);
                    if (queryKcxx.getResult() != null) {
                        List<SldKcmx> sldKykcList = queryKcxx.getResult().getKcmxes();
                        if (sldKykcList != null && sldKykcList.size() > 0) {
                            for (SldKcmx sldKcmx : sldKykcList) {
                                if (Integer.parseInt(sldKcmx.getFpfs()) > 0) {
                                    result.put("sldid", sldid);
                                    result.put("kpjh", searchSld.getFjh());
                                    result.put("sldmc", searchSld.getSldMc());
                                    resultList.add(result);
                                }
                            }
                        }
                    }
                }
    
            } else {
                //税控设备是c48的话 还按以前的逻辑
                for (SearchSld searchSld : sldList) {
    
                    sldid = searchSld.getSldId();
                    String sldMc = searchSld.getSldMc();
                    sldJspxxRequest.setFpzldm(invoiceType);
                    //因业务逻辑需求 使用状态写死 2:使用中
                    sldJspxxRequest.setSyzt("2");
                    sldJspxxRequest.setNsrsbh(nsrsbh);
                    sldJspxxRequest.setSldid(sldid);
    
                    //查询金税盘信息
                    sldJspxxResponse = unifyService.selectSldJspxx(sldJspxxRequest, terminalCode);
    
                    //循环查询上下票管理列表 syfs(剩余份数)>0,为kpjh(开票机号),受理点(sld)赋值
                    //找出符合开票要求的受理点，然后轮询
                    result = dealWithSldEnd(sldJspxxResponse, sldid, sldMc);
    
                    if ((boolean) result.get("flag")) {
                        resultList.add(result);
                    }
                }
            }
    
        }
        return resultList;
    }
    
    /**
     * 处理受理点
     * 循环查询上下票管理列表 ，fpfs(发票份数)>0,为kpjh(开票机号),受理点(sld)赋值
     *
     * @param sldJspxxResponse
     * @param sldid
     * @param sldMc
     * @return
     */
    private Map<String, Object> dealWithSldEnd(SldJspxxResponse sldJspxxResponse, String sldid, String sldMc) {
        Map<String, Object> result = new HashMap<>(5);
        //剩余份数
        Integer fpfs;
        //完成标志
        Boolean flag = Boolean.FALSE;
    
        if (sldJspxxResponse != null && sldJspxxResponse.getStatusCode().equals(OrderInfoContentEnum.SUCCESS.getKey()) && sldJspxxResponse.getSldJspxxList().size() > 0) {
            for (int j = 0; j < sldJspxxResponse.getSldJspxxList().size(); j++) {
                fpfs = sldJspxxResponse.getSldJspxxList().get(j).getFpfs();
                //剩余份数大于0时 赋值
                if (fpfs > 0) {
                    String kpjh = sldJspxxResponse.getSldJspxxList().get(j).getFjh();
                    result.put("sldid", sldid);
                    result.put("kpjh", kpjh);
                    flag = Boolean.TRUE;
                    result.put("flag", flag);
                    result.put("sldmc", sldMc);
                    break;
                }
            }
            if (!flag) {
                result.put("message", "受理点处于停止状态,不可用");
                result.put("flag", flag);
            }
        } else {
            result.put("message", "受理点处于停止状态,不可用");
            result.put("flag", flag);
        }
        return result;
    }
    
}
