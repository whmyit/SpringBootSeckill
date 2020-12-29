package com.dxhy.common.generatepdf.constant;

/**
 *
 *
 * @ClassName ：PdfTemplateEnum
 * @Description ：pdf模板枚举类
 * @author ：杨士勇
 * @date ：2019年3月27日 下午4:04:46
 *
 *
 */
public enum PdfTemplateEnum {
    
    /**
     * 辽宁模板
     */
    LIAONING_DALIAN_2102("2102", "2102", "辽宁大连模板"),
    ZHEJIANG_NINGBO_3302("3302", "3302", "浙江宁波模板"),
    FUJIAN_XIAMEN_3502("3502", "3502", "福建厦门模板"),
    SHANDONG_QINGDAO_3702("3702", "3702", "山东青岛模板"),
    GUANGDONG_SHENZHEN_4403("4403", "4403", "广东深圳模板"),
   /* HEBEI_BAODING_1306("1306","1306","河北保定模板"),
    HEBEI_ZHANGJIAKOU_1307("1307","1307","河北张家口模板"),*/
    
    DEFAULT_0000("0000", "0000", "默认模板"),
    
    BEIJING_1100("1100","1100","北京地区模板"),
    TIANJIN_1200("1200","1200","天津地区模板"),
	HEBEI_15("13","1300","河北模板"),
	SHANXI_14("14","1400","山西模板"),
	NEIMENGU_15("15","1500","内蒙古模板"),
	LIAONING_21("21","2100","辽宁模板"),
	JILIN_22("22","2200","吉林模板"),
	HEILONGJIANG_23("23","2300","黑龙江模板"),
	SHANGHAI_31("31","3100","上海模板"),
	JIANGSU_32("32","3200","江苏模板"),
	ZHEJIANG_33("33","3300","浙江模板"),
	ANHUI_34("34","3400","安徽模板"),
	FUJIAN_35("35","3500","福建模板"),
	JIANGXI_36("36","3600","江西模板"),
	SHANGDONG_37("37","3700","山东模板"),
	HENAN_41("41","4100","河南模板"),
	HUBEI_42("42","4200","湖北模板"),
	HUNAN_43("43","4300","湖南模板"),
	GUANGDONG_44("44","4400","广东模板"),
	GUANGXI_45("45","4500","广西模板"),
	HAINAN_46("46","4600","海南模板"),
	CHONGQING_50("50","5000","重庆模板"),
	SICHUAN_51("51","5100","四川模板"),
	GUIZHOU_52("52","5200","贵州模板"),
    YUNNAN_53("53", "5300", "云南模板"),
    XIZANG_54("54", "5400", "西藏模板"),
    SHANXI_61("61", "6100", "陕西模板"),
    GANSU_62("62", "6200", "甘肃模板"),
    QINGHAI_63("63", "6300", "青海模板"),
    NINGXIA_64("64", "6400", "宁夏模板"),
    XINJIANG_65("65", "6500", "新疆模板");
    
    
    private final String code;
    private final String templateCode;
    private final String message;
    
    PdfTemplateEnum(String code, String templateCode, String message) {
        this.code = code;
        this.templateCode = templateCode;
        this.message = message;
    }
    
    public String code() {
        return code;
    }
    public String message() {
        return message;
    }
    public String templateCode() {
        return templateCode;
    }
    public static PdfTemplateEnum getCodeValue(String key) {

        for (PdfTemplateEnum item : values()) {
            if (item.code.equals(key)) {
                return item;
            }
        }
        return null;
    }
}
