package com.dxhy.order.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.dxhy.order.api.IValidateInterfaceOrder;
import com.dxhy.order.api.IValidateInterfaceSpecialInvoice;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.OrderInfoContentEnum;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.protocol.v4.invoice.HZSQDSCZXX;
import com.dxhy.order.protocol.v4.invoice.HZSQDSC_REQ;
import com.dxhy.order.protocol.v4.invoice.HZSQDTXX;
import com.dxhy.order.protocol.v4.invoice.HZSQDXZ_REQ;
import com.dxhy.order.protocol.v4.order.DDMXXX;
import com.dxhy.order.utils.CheckParamUtil;
import com.dxhy.order.utils.DateUtils;
import com.dxhy.order.utils.DateUtilsLocal;
import com.dxhy.order.utils.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单数据校验接口
 *
 * @author ZSC-DXHY
 */
@Slf4j
@Service
public class ValidateInterfaceSpecialInvoiceImpl implements IValidateInterfaceSpecialInvoice {
    
    @Resource
    private IValidateInterfaceOrder validateInterfaceOrder;
    
    private final String LOGGER_MSG = "(订单开票数据校验)";
    
    
    /**
     * 校验红字申请单上传接口数据合法性(不包含非空校验)
     */
    @Override
    public Map<String, String> checkSpecialInvoiceUpload(HZSQDSC_REQ hzsqdscReq) {
        Map<String, String> checkResultMap = new HashMap<>(10);
        checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
    
        if (hzsqdscReq == null) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115001);
        }
    
        /**
         * 红字申请单上传-批次号校验
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_115002, hzsqdscReq.getHZSQDSCPC().getSQBSCQQPCH());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        /**
         * 红字申请单上传-申请方税号校验
         */
        checkResultMap = validateInterfaceOrder.checkNsrsbhParam(OrderInfoContentEnum.CHECK_ISS7PRI_115006, OrderInfoContentEnum.CHECK_ISS7PRI_107017, OrderInfoContentEnum.CHECK_ISS7PRI_107163, hzsqdscReq.getHZSQDSCPC().getNSRSBH());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        /**
         * 红字申请单上传-开票点
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107009, hzsqdscReq.getHZSQDSCPC().getKPZD());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        /**
         * 红字申请单上传-请求发票类型
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_115010, hzsqdscReq.getHZSQDSCPC().getFPLXDM());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        //红字申请单上传-请求发票类型合法性
        if (!OrderInfoEnum.ORDER_INVOICE_TYPE_004.getKey().equals(hzsqdscReq.getHZSQDSCPC().getFPLXDM())
                && !OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(hzsqdscReq.getHZSQDSCPC().getFPLXDM())
                && !OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(hzsqdscReq.getHZSQDSCPC().getFPLXDM())
                && !OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(hzsqdscReq.getHZSQDSCPC().getFPLXDM())
                && !OrderInfoEnum.ORDER_INVOICE_TYPE_007.getKey().equals(hzsqdscReq.getHZSQDSCPC().getFPLXDM())
                && !OrderInfoEnum.ORDER_INVOICE_TYPE_026.getKey().equals(hzsqdscReq.getHZSQDSCPC().getFPLXDM())) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115011);
        }
    
        /**
         * 红字申请单上传-申请类别
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_115014, hzsqdscReq.getHZSQDSCPC().getSQLB());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        //红字申请单上传-申请类别合法性
        if (!ConfigureConstant.STRING_1.equals(hzsqdscReq.getHZSQDSCPC().getSQLB()) && !ConfigureConstant.STRING_0.equals(hzsqdscReq.getHZSQDSCPC().getSQLB())) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115015);
        }
    
        if (hzsqdscReq.getHZSQDSCZXX() == null || hzsqdscReq.getHZSQDSCZXX().size() < 1) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115001);
        }
    
        if (hzsqdscReq.getHZSQDSCZXX().size() > ConfigureConstant.INT_10) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115077);
        }
    
    
        for (HZSQDSCZXX hzsqdsczxx : hzsqdscReq.getHZSQDSCZXX()) {
            HZSQDTXX hzsqdtxx = hzsqdsczxx.getHZSQDTXX();
            List<DDMXXX> ddmxxx = hzsqdsczxx.getDDMXXX();
        
        
            /**
             * 红字申请单上传-申请上传表流水号
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_115004, hzsqdtxx.getSQBSCQQLSH());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
    
            /**
             * 红字申请单上传-信息表类型
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_115016, hzsqdtxx.getXXBLX());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
            //红字申请单上传-信息表类型合法性
            if (!ConfigureConstant.STRING_1.equals(hzsqdtxx.getXXBLX()) && !ConfigureConstant.STRING_0.equals(hzsqdtxx.getXXBLX())) {
                return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115017);
            }
    
    
            if (StringUtils.isBlank(hzsqdtxx.getSQSM())) {
                return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115037);
            } else if (!OrderInfoEnum.SPECIAL_INVOICE_REASON_1100000000.getKey().equals(hzsqdtxx.getSQSM()) && !OrderInfoEnum.SPECIAL_INVOICE_REASON_1010000000.getKey().equals(hzsqdtxx.getSQSM()) && !OrderInfoEnum.SPECIAL_INVOICE_REASON_0000000100.getKey().equals(hzsqdtxx.getSQSM())) {
                return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115038);
            }
    
            if (!OrderInfoEnum.SPECIAL_INVOICE_REASON_1100000000.getKey().equals(hzsqdtxx.getSQSM())) {
                //原发票代码
                if (StringUtils.isBlank(hzsqdtxx.getYFPDM())) {
                    return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115018);
                } else if (hzsqdtxx.getYFPDM().length() > 12) {
                    return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115019);
                }
                //原发票号码
                if (StringUtils.isBlank(hzsqdtxx.getYFPHM())) {
                    return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115020);
                } else if (hzsqdtxx.getYFPHM().length() > 8) {
                    return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115021);
                }
                
            }
            
            
            //申请类别为1时 购方 销方必填
            if ("1".equals(hzsqdscReq.getHZSQDSCPC().getSQLB())) {
                //销售方纳税人识别号
                if (StringUtils.isBlank(hzsqdtxx.getXHFSBH())) {
                    return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115025);
                }
                checkResultMap = validateInterfaceOrder.checkNsrsbhParam(OrderInfoContentEnum.CHECK_ISS7PRI_107016, OrderInfoContentEnum.CHECK_ISS7PRI_107017, OrderInfoContentEnum.CHECK_ISS7PRI_107163, hzsqdtxx.getXHFSBH());
                if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                    return checkResultMap;
                }
                //销售方名称
                if (StringUtils.isBlank(hzsqdtxx.getXHFMC())) {
                    return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115027);
                }
                //合计金额
                if (StringUtils.isBlank(hzsqdtxx.getHJJE())) {
                    return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115033);
                } else if (Double.parseDouble(hzsqdtxx.getHJJE()) >= 0) {
                    return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115062);
                }
                //合计税额
                if (StringUtils.isBlank(hzsqdtxx.getHJSE())) {
                    return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115035);
                } else if (Double.parseDouble(hzsqdtxx.getHJSE()) >= 0.0) {
                    //hjse等于0.0的情况
                    if (Double.parseDouble(hzsqdtxx.getHJSE()) != 0.0) {
                        return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115063);
                    }
                }
            } else {
                
                //购买方纳税人识别号
                if (StringUtils.isBlank(hzsqdtxx.getGMFSBH())) {
                    return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115029);
                } else {
                    checkResultMap = validateInterfaceOrder.checkNsrsbhParam(OrderInfoContentEnum.CHECK_ISS7PRI_107027, OrderInfoContentEnum.CHECK_ISS7PRI_107025, OrderInfoContentEnum.CHECK_ISS7PRI_107023, hzsqdtxx.getGMFSBH());
                    if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                        return checkResultMap;
                    }
                }
    
    
                //购买方名称
                if (StringUtils.isBlank(hzsqdtxx.getGMFMC())) {
                    return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115031);
                }
            }
    
            /**
             * 红字申请单上传-销货方名称
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_115028, hzsqdtxx.getXHFMC());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
            
            checkResultMap = validateInterfaceOrder.checkNsrsbhParam(OrderInfoContentEnum.CHECK_ISS7PRI_115080, OrderInfoContentEnum.CHECK_ISS7PRI_107017, OrderInfoContentEnum.CHECK_ISS7PRI_107163, hzsqdtxx.getXHFSBH());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
    
    
            /**
             * 红字申请单上传-购货方名称
             */
            checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_115032, hzsqdtxx.getGMFMC());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
            
            checkResultMap = validateInterfaceOrder.checkNsrsbhParam(OrderInfoContentEnum.CHECK_ISS7PRI_115080, OrderInfoContentEnum.CHECK_ISS7PRI_107017, OrderInfoContentEnum.CHECK_ISS7PRI_107163, hzsqdtxx.getGMFSBH());
            if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                return checkResultMap;
            }
    
            //编码表版本号
            if (StringUtils.isNotBlank(hzsqdtxx.getBMBBBH()) && hzsqdtxx.getBMBBBH().length() > 20) {
                return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115040);
            }
            
            //填开时间
            if (StringUtils.isBlank(hzsqdtxx.getTKSJ())) {
                return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115023);
            } else if (!"yyyyMMddHHmmss".equals(DateUtilsLocal.checkDate(hzsqdtxx.getTKSJ()))) {
                return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115024);
            }
            
            
            //明细信息(申请表明细信息,可多条,若SQLB为0, 明细无需填写; 若SQLB为1,明细必须根据蓝字纸质发票填写)
            if (ddmxxx != null) {
                
                //申请单明细行数不得超过8行
                if (ddmxxx.size() > 8) {
                    return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115071);
                }
                int xmxh = 0;
                for (DDMXXX ddmxxx1 : ddmxxx) {
                    xmxh++;
                    if (StringUtils.isNotBlank(ddmxxx1.getXH()) && !ddmxxx1.getXH().equals(String.valueOf(xmxh))) {
                        return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115041);
                    }
    
                    //发票行性质
                    if (StringUtils.isBlank(ddmxxx1.getFPHXZ())) {
                        return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115042);
                    }
    
                    if (!"0".equals(ddmxxx1.getFPHXZ()) && !"1".equals(ddmxxx1.getFPHXZ()) && !"2".equals(ddmxxx1.getFPHXZ()) && !"6".equals(ddmxxx1.getFPHXZ())) {
                        return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115072);
                    }
    
    
                    /**
                     * 订单明细信息-商品编码
                     */
                    checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_107100, ddmxxx1.getSPBM(), xmxh);
                    if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                        return checkResultMap;
                    }
                    //商品编码必须为19位数字
                    if (StringUtils.isNotBlank(ddmxxx1.getSPBM())) {
                        boolean spbm = false;
                        for (int j = 0; j < ddmxxx1.getSPBM().length(); j++) {
                            char c = ddmxxx1.getSPBM().charAt(j);
                            if ((c < '0' || c > '9')) {
                                spbm = true;
                            }
                        }
                        if (ddmxxx1.getSPBM().length() != 19) {
        
                        }
                        if (spbm) {
                            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115082);
                        }
                    }
    
                    //自行编码
                    if (!StringUtils.isBlank(ddmxxx1.getZXBM())) {
                        if (ddmxxx1.getZXBM().length() > 16) {
                            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115045);
                        }
                    }
    
                    //优惠政策标识
                    if (StringUtils.isBlank(ddmxxx1.getYHZCBS())) {
                        return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115046);
                    }
    
                    if (!"0".equals(ddmxxx1.getYHZCBS()) && !"1".equals(ddmxxx1.getYHZCBS())) {
                        return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115074);
                    }
                    
                    //优惠政策标识
                    if ("1".equals(ddmxxx1.getYHZCBS())) {
                        //如果为1 增值税特殊管理必填
                        if (StringUtils.isBlank(ddmxxx1.getZZSTSGL())) {
                            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115048);
                        }
                    } else if ("0".equals(ddmxxx1.getYHZCBS())) {
                        //为0时优惠政策标识为空
                        if (StringUtils.isNotBlank(ddmxxx1.getZZSTSGL())) {
                            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115068);
                        }
                    }
    
    
                    /**
                     * 红字申请单上传-增值税特殊管理
                     */
                    checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_115048, ddmxxx1.getZZSTSGL());
                    if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                        return checkResultMap;
                    }
    
                    //项目金额不能为空，且不能为零
                    if (StringUtils.isBlank(ddmxxx1.getJE())) {
                        return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115056);
                    } else {
                        if ("0".equals(ddmxxx1.getJE()) || "0.00".equals(ddmxxx1.getJE())) {
                            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115056);
                        }
                    }
    
                    //含税标志
                    if (StringUtils.isBlank(ddmxxx1.getHSBZ())) {
                        return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115057);
                    }
                    //含税标志只能为0和1：0表示都不含税,1表示都含税
                    if (StringUtils.isNotBlank(ddmxxx1.getHSBZ()) && !OrderInfoEnum.HSBZ_1.getKey().equals(ddmxxx1.getHSBZ()) && !OrderInfoEnum.HSBZ_0.getKey().equals(ddmxxx1.getHSBZ())) {
                        return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115083);
                    }
                    //含税标志为0时,税额不能为空
                    if (OrderInfoEnum.HSBZ_0.getKey().equals(ddmxxx1.getHSBZ()) && StringUtils.isBlank(ddmxxx1.getSE())) {
                        return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115084);
                    }
                    //清单红字，含税标志为0
                    if ("6".equals(ddmxxx1.getFPHXZ())) {
                        if (!"0".equals(ddmxxx1.getHSBZ())) {
                            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115058);
                        }
                    }
                    //税率
                    if (!StringUtils.isBlank(ddmxxx1.getSL())) {
                        if (!"0".equals(ddmxxx1.getSL())) {
                            if ((ValidateUtil.checkNumberic(ddmxxx1.getSL()) > 4 || (ValidateUtil.checkNumberic(ddmxxx1.getSL())) < 2)) {
                                return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115060);
                            }
                        }
                    }
    
                    //税额
                    if (!StringUtils.isBlank(ddmxxx1.getSE())) {
                        if ((ValidateUtil.checkNumberic(ddmxxx1.getSE()) > 2 || (ValidateUtil.checkNumberic(ddmxxx1.getSE()) < 2))) {
                            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115061);
                        }
                    }
    
                    //如果YHZCBS为1, 且税率为0, 则LSLBS只能根据实际情况选择"0或1或2"中的一种, 不能选择3, 且ZZSTSGL内容也只能写与0/1/2对应的"出口零税/免税/不征税
                    if (("1".equals(ddmxxx1.getYHZCBS())) && ("0".equals(ddmxxx1.getSL()))) {
                        if ("3".equals(ddmxxx1.getLSLBS())) {
                            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115064);
                        }
                    }
                    //如果税率为0,但并不属于优惠政策(即普通的零税率),则YHZCBS填0,LSLBS填3,ZZSTSGL为空;
                    if ("0".equals(ddmxxx1.getSL()) && "0".equals(ddmxxx1.getYHZCBS())) {
                        if (!"0".equals(ddmxxx1.getYHZCBS()) && !"3".equals(ddmxxx1.getLSLBS()) && StringUtils.isNotBlank(ddmxxx1.getZZSTSGL())) {
                            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115065);
                        }
                    }
    
                    //如果税率不为0, 但属于优惠政策,则YHZCBS填1,LSLBS填空或不填,ZZSTSGL根据实际情况填写;
                    if (!"0".equals(ddmxxx1.getSL()) && "1".equals(ddmxxx1.getYHZCBS())) {
                        if (!"1".equals(ddmxxx1.getYHZCBS()) && StringUtils.isNotBlank(ddmxxx1.getLSLBS())) {
                            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115066);
                        }
                    }
    
                    //如果税率不为0, 且不属于优惠政策, 则YHZCBS填0,LSLBS填空或不填,ZZSTSGL不填或空.
                    if (!"0".equals(ddmxxx1.getSL()) && "0".equals(ddmxxx1.getYHZCBS())) {
                        if (!"0".equals(ddmxxx1.getYHZCBS()) && StringUtils.isNotBlank(ddmxxx1.getLSLBS()) && StringUtils.isNotBlank(ddmxxx1.getZZSTSGL())) {
                            validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115067);
                        }
                    }
                    //需要判断税额是否和传入的计算相一致
                    String mxsehj = null;
                    DecimalFormat df = new DecimalFormat("######0.00");
                    if ("6".equals(ddmxxx1.getFPHXZ())) {
                        //是清单红字的红字申请单发票行性质为：6
                        if (ddmxxx.size() > 1) {
                            //明细行数限制为1行
                            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115073);
                        }
                        //若对应的蓝字发票明细行存在多种商品编码,商品编码填充为空,单一商品编码填写对应的商品编码---待
        
                        if (!"详见对应正数发票及清单".equals(ddmxxx1.getXMMC())) {
                            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115079);
                        }
        
                        if (!StringUtils.isBlank(ddmxxx1.getGGXH()) || !StringUtils.isBlank(ddmxxx1.getDW()) || !StringUtils.isBlank(ddmxxx1.getSPSL()) || !StringUtils.isBlank(ddmxxx1.getDJ())) {
                            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115075);
                        }
    
                        if (!StringUtils.isBlank(ddmxxx1.getSPBM())) {
                            if (ddmxxx1.getSPBM().length() > 19) {
                                return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115044);
                            }
                        }
    
                    } else {
                        //不是清单红字的申请单
                        if (StringUtils.isBlank(ddmxxx1.getSPBM())) {
                            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115043);
                        } else if (ddmxxx1.getSPBM().length() > 19) {
                            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115044);
                        }
    
                        /**
                         * 红字申请单上传-项目名称
                         */
                        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_115049, ddmxxx1.getXMMC());
                        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                            return checkResultMap;
                        }
    
                        /**
                         * 红字申请单上传-规格型号
                         */
                        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_115051, ddmxxx1.getGGXH());
                        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                            return checkResultMap;
                        }
                        
                        /**
                         * 红字申请单上传-单位
                         */
                        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_115052, ddmxxx1.getDW());
                        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
                            return checkResultMap;
                        }
                        
                        
                        //项目数量
                        if (!StringUtils.isBlank(ddmxxx1.getSPSL())) {
                            if (ddmxxx1.getSPSL().length() > 20) {
                                return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115053);
                            }
                        }
                        //项目单价支持为空，比如维修费等
                        if (!StringUtils.isBlank(ddmxxx1.getDJ())) {
                            if ("0".equals(ddmxxx1.getDJ()) || "0.00".equals(ddmxxx1.getDJ())) {
                                return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115076);
                            }
                            if (ddmxxx1.getDJ().length() > 20) {
                                return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115054);
                            }
                        }
                        //税率
                        if (StringUtils.isBlank(ddmxxx1.getSL())) {
                            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115059);
                        }
                        //项目金额不能为空，且不能为零
                        if (StringUtils.isBlank(ddmxxx1.getJE())) {
                            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115056);
                        } else {
                            if ("0".equals(ddmxxx1.getJE()) || "0.00".equals(ddmxxx1.getJE())) {
                                return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115056);
                            }
                        }
                    }
    
                }
            }
            
        }
        
        return checkResultMap;
    }
    
    /**
     * 校验红字申请单下载接口
     *
     * @param hzsqdxzReq
     * @return
     */
    @Override
    public Map<String, String> checkSpecialInvoiceDownload(HZSQDXZ_REQ hzsqdxzReq) {
        
        Map<String, String> checkResultMap = new HashMap<>(10);
        checkResultMap.put(OrderManagementConstant.ERRORCODE, OrderInfoContentEnum.SUCCESS.getKey());
        
        if (hzsqdxzReq == null) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115001);
        }
        
        /**
         * 红字申请单下载-批次号
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_502010, hzsqdxzReq.getSQBXZQQPCH());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        /**
         * 红字申请单下载-税号
         */
        checkResultMap = validateInterfaceOrder.checkNsrsbhParam(OrderInfoContentEnum.CHECK_ISS7PRI_502009, OrderInfoContentEnum.CHECK_ISS7PRI_107017, OrderInfoContentEnum.CHECK_ISS7PRI_107163, hzsqdxzReq.getNSRSBH());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        
        /**
         * 红字申请单上传-请求发票类型
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_115010, hzsqdxzReq.getFPLXDM());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        //红字申请单上传-请求发票类型合法性
        if (!OrderInfoEnum.ORDER_INVOICE_TYPE_004.getKey().equals(hzsqdxzReq.getFPLXDM())
                && !OrderInfoEnum.ORDER_INVOICE_TYPE_0.getKey().equals(hzsqdxzReq.getFPLXDM())
                && !OrderInfoEnum.ORDER_INVOICE_TYPE_2.getKey().equals(hzsqdxzReq.getFPLXDM())
                && !OrderInfoEnum.ORDER_INVOICE_TYPE_51.getKey().equals(hzsqdxzReq.getFPLXDM())
                && !OrderInfoEnum.ORDER_INVOICE_TYPE_007.getKey().equals(hzsqdxzReq.getFPLXDM())
                && !OrderInfoEnum.ORDER_INVOICE_TYPE_026.getKey().equals(hzsqdxzReq.getFPLXDM())) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_115011);
        }
        
        //开票日期起日期
        if (StringUtils.isNotBlank(hzsqdxzReq.getTKRQQ())) {
            if (!DateUtilsLocal.YYYYMMDD.equals(DateUtilsLocal.checkDate(hzsqdxzReq.getTKRQQ()))) {
                return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_117010);
            }
        } else {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_117017);
        }
    
        //开票日期截止日期
        if (StringUtils.isNotBlank(hzsqdxzReq.getTKRQZ())) {
            if (!DateUtilsLocal.YYYYMMDD.equals(DateUtilsLocal.checkDate(hzsqdxzReq.getTKRQZ()))) {
                return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_117011);
            }
        } else {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_117018);
        }
        Date tkrqqDate = DateUtils.stringToDate(hzsqdxzReq.getTKRQQ(), "yyyyMMdd");
        Date tkrqzDate = DateUtils.stringToDate(hzsqdxzReq.getTKRQZ(), "yyyyMMdd");
    
        if (DateUtil.compare(tkrqqDate, tkrqzDate) > ConfigureConstant.INT_0) {
            log.error("{}同步冲红申请单，填开日期起不能大于填开日期止", LOGGER_MSG);
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_117020);
        }
    
        if (DateUtil.betweenDay(tkrqqDate, tkrqzDate, false) > ConfigureConstant.INT_5) {
            log.error("{}同步冲红申请单，填开日期区间大于5天", LOGGER_MSG);
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_117019);
        }
    
        /**
         * 红字申请单下载-销方税号
         */
        checkResultMap = validateInterfaceOrder.checkNsrsbhParam(OrderInfoContentEnum.CHECK_ISS7PRI_502007, OrderInfoContentEnum.CHECK_ISS7PRI_107017, OrderInfoContentEnum.CHECK_ISS7PRI_107163, hzsqdxzReq.getXHFSBH());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        /**
         * 红字申请单下载-购方税号
         */
        checkResultMap = validateInterfaceOrder.checkNsrsbhParam(OrderInfoContentEnum.CHECK_ISS7PRI_502008, OrderInfoContentEnum.CHECK_ISS7PRI_107017, OrderInfoContentEnum.CHECK_ISS7PRI_107163, hzsqdxzReq.getGMFSBH());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        /**
         * 红字申请单下载-信息表编号
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_502006, hzsqdxzReq.getXXBBH());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
    
        //信息表范围
        String xxbfw = hzsqdxzReq.getXXBFW();
        if (StringUtils.isBlank(xxbfw)) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_117015);
        } else if (!ConfigureConstant.STRING_0.equals(xxbfw) && !ConfigureConstant.STRING_1.equals(xxbfw) && !ConfigureConstant.STRING_2.equals(xxbfw)) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_117016);
        }
    
    
        /**
         * 红字申请单下载-分页页号
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_502002, hzsqdxzReq.getYS());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        if (!NumberUtil.isNumber(hzsqdxzReq.getYS()) || ConfigureConstant.STRING_0.equals(hzsqdxzReq.getYS())) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_502004);
        }
    
        /**
         * 红字申请单下载-分页个数
         */
        checkResultMap = CheckParamUtil.checkParam(OrderInfoContentEnum.CHECK_ISS7PRI_502003, hzsqdxzReq.getGS());
        if (!OrderInfoContentEnum.SUCCESS.getKey().equals(checkResultMap.get(OrderManagementConstant.ERRORCODE))) {
            return checkResultMap;
        }
        if (!NumberUtil.isNumber(hzsqdxzReq.getGS()) || Integer.parseInt(hzsqdxzReq.getGS()) > ConfigureConstant.INT_10 || Integer.parseInt(hzsqdxzReq.getGS()) <= ConfigureConstant.INT_0) {
            return validateInterfaceOrder.generateErrorMap(null, "", OrderInfoContentEnum.CHECK_ISS7PRI_502005);
        }
    
    
        return checkResultMap;
    }
    
    
}
