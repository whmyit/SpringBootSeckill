package com.dxhy.order.model.a9.hp;

import lombok.Data;

import java.util.List;

/**
 * @author ：杨士勇
 * @ClassName ：ResponseHzfpsqbsc
 * @Description ：A9红字信息上传返回bean
 * @date ：2019年7月23日 下午5:50:33
 */
@Data
public class ResponseHzfpsqbsc {
    
    private String sqdh;
    private String xxbbh;
    private String status_CODE;
    private String status_MESSAGE;
    private String yfp_DM;
    private String yfp_HM;
    private String fplx;
    private String fplb;
    private String dslbz;
    private String tksj;
    private String xsf_NSRSBH;
    private String xsf_MC;
    private String gmf_NSRSBH;
    private String gmf_MC;
    private String hjje;
    private String hjse;
    private String sqsm;
    private String bmb_BBH;
    private String yysbz;
    private List<Commoninvdetail> commoninvdetails;
}
