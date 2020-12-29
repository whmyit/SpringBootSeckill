package com.dxhy.order.model.ukey;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 百望盘阵和UKey统一机器编号数据返回
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-22 9:31
 */
@Getter
@Setter
public class QueryJqbh implements Serializable {
    
    /**
     * 是否启用
     */
    private String sfqy;
    
    /**
     * 创建时间
     */
    private String cjsj;
    
    /**
     * 税控扩展信息
     */
    private String skkzxx;
    
    /**
     * 机器编号名称
     */
    private String mc;
    
    /**
     * 离线开票时长
     */
    private String lxkpsc;
    
    /**
     * 当前时钟
     */
    private String dqsz;
    
    /**
     * id
     */
    private String id;
    
    /**
     *
     */
    private String gzzt;
    
    /**
     * 离线金额
     */
    private String lxzsljje;
    
    /**
     * 报税截止日期
     */
    private String bsjzrq;
    
    /**
     * 修改人
     */
    private String xgr;
    
    /**
     *
     */
    private String lxkzxx;
    
    /**
     *
     */
    private String sjzt;
    
    /**
     * 纳税人名称
     */
    private String nsrmc;
    
    /**
     * 创建人
     */
    private String cjr;
    
    /**
     * 离线开票
     */
    private String lxkpzs;
    
    /**
     * 销方税号
     */
    private String nsrsbh;
    
    /**
     * 报税申请日期
     */
    private String bsqqrq;
    
    /**
     * 机器编号
     */
    private String jqbh;
    
    /**
     * 上传截止日期
     */
    private String scjzrq;
    
    /**
     * 开票截止时间
     */
    private String kpjzsj;
    
    
    /**
     * 当前数据结构为:
     * {
     *         "sfqy": 1,
     *         "cjsj": "2020-08-20 19:04:16",
     *         "lxfsljje": "21474836.47",
     *         "skkzxx": "[{\"bsrq\":\"20200731\",\"dzkpxe\":\"21474836.47\",\"fpzldm\":\"004\",\"fsfpbz\":\"0\",\"fsfpts\":\"999\",\"fsljxe\":\"21474836.47\",\"zsljxe\":\"21474836.47\"},{\"bsrq\":\"20200731\",\"dzkpxe\":\"21474836.47\",\"fpzldm\":\"007\",\"fsfpbz\":\"0\",\"fsfpts\":\"999\",\"fsljxe\":\"21474836.47\",\"zsljxe\":\"21474836.47\"},{\"bsrq\":\"20200731\",\"dzkpxe\":\"21474836.47\",\"fpzldm\":\"025\",\"fsfpbz\":\"0\",\"fsfpts\":\"999\",\"fsljxe\":\"21474836.47\",\"zsljxe\":\"21474836.47\"},{\"bsrq\":\"20200731\",\"dzkpxe\":\"21474836.47\",\"fpzldm\":\"026\",\"fsfpbz\":\"0\",\"fsfpts\":\"999\",\"fsljxe\":\"21474836.47\",\"zsljxe\":\"21474836.47\"}]",
     *         "mc": "237000120526税盘",
     *         "lxkpsc": 999,
     *         "dqsz": "20200820191946",
     *         "id": 16,
     *         "gzzt": "0",
     *         "lxzsljje": "21474836.47",
     *         "bsjzrq": "20200831",
     *         "xgr": "wuzhen8",
     *         "lxkzxx": "00000000000000000000000000000000000000000000",
     *         "sjzt": 0,
     *         "nsrmc": "测试07",
     *         "cjr": "wuzhen8",
     *         "lxkpzs": 268435455,
     *         "nsrsbh": "110110202040307",
     *         "bsqqrq": "20200801",
     *         "jqbh": "237000120526",
     *         "scjzrq": "1",
     *         "kpjzsj": "20200915000000"
     *       }
     */
}
