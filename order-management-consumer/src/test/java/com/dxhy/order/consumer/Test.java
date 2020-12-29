package com.dxhy.order.consumer;

import cn.hutool.core.util.RandomUtil;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class Test {
    public static void main(String[] args) throws Exception {
        String randomUUID = RandomUtil.randomString(32);
        String randomUUID1 = RandomUtil.randomString(36);
        System.out.println("secretID:" + randomUUID);
//        System.out.println((randomUUID + RandomUtils.generateNumString(4)).length());
        System.out.println("secretKey:" + randomUUID1);
    
    
        String srcStr = "GETapi.ele-cloud.com/order-api/invoice/api/v4/interfaceTest?Nonce=11886&SecretId=289efb7512e54146273b982456b03f42ea93&Timestamp=1484718385&content=ewogICAgIlBhcmFtZXRlciI6ICIxIgp9&encryptCode=0&zipCode=0";
        String secretKey = "27a06832a2214a4fa3b7105e4a72d370";
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(keySpec);
        byte[] signBytes = mac.doFinal(srcStr.getBytes(StandardCharsets.UTF_8));
        String signStr = Base64.encodeBase64URLSafeString(signBytes);
        System.out.println(signStr);
    
        //测试转换数据base64
        String str = "eyJDT01NT05fSU5WT0lDRSI6W3siQ09NTU9OX0lOVk9JQ0VfREVUQUlMIjpbeyJYTU1DIjoi54Ot5YqbIiwiSFNCWiI6IjAiLCJaWEJNIjoiIiwiTFNMQlMiOiIiLCJYTVNMIjoyLCJYTVhIIjoxLCJTUEJNIjoiMTEwMDAwMDAwMDAwMDAwMDAwMCIsIkdHWEgiOiJZU00yNU0tMDcxNS1TLUwiLCJYTURKIjoxMTY2Ni42NjUsIlNFIjo0NjY2LjY3LCJEVyI6IuWPsCIsIllIWkNCUyI6IjAiLCJYTUpFIjoyMzMzMy4zMywiU0wiOjAuMiwiRlBIWFoiOiIwIn1dLCJDT01NT05fSU5WT0lDRV9PUkRFUiI6eyJEREgiOiJSZXEyMDE4MTIxNzAwMDAwMTIiLCJERERBVEUiOiIiLCJUSERIIjpudWxsfSwiQ09NTU9OX0lOVk9JQ0VfSEVBRCI6eyJRRFhNTUMiOiIiLCJHTUZfTlNSU0JIIjoiOTE0NDAzMDAxOTI0NDkwMDRGIiwiWFNGX0RIIjoiNTkyODE4ODgiLCJLUFIiOiLmnY7msYDlhbAiLCJHTUZfU0oiOiIiLCJGSFIiOiLmnY7msYDlhbAiLCJRRF9CWiI6IjAiLCJLUExYIjowLCJHTUZfTUMiOiLmt7HlnLPluILmgKHlspvnjq_looPnqbrosIPlt6XnqIvmnInpmZDlhazlj7giLCJHTUZfRFoiOiLmt7HlnLPluILljZflsbHljLrom4flj6PljYrlspvoirHlm61B5Yy6NuagizIwMeeUteivnTowNzU1LTI2ODgzNzUxIiwiR01GX1NGIjoiIiwiQloiOiJGWkhJMTgwMDA5N0ZTICDkuK3lsbHnuqLmo4nnlLXplYDljoJZU03po47mn5xGWkhJMTgwMDA5N0ZTICDkuK3lsbHnuqLmo4nnlLXplYDljoJZU03po47mn5wiLCJCWVpENCI6IiIsIkJZWkQzIjoiIiwiQllaRDIiOiIiLCJCWVpEMSI6IiIsIkhKSkUiOiIyMzMzMy4zMyIsIk5TUlNCSCI6IjkxMTEwMTA4MjAxODA1MDUxNiIsIkJZWkQ1IjoiIiwiR01GX0dEREgiOiIiLCJYU0ZfTlNSU0JIIjoiOTExMTAxMDgyMDE4MDUwNTE2IiwiRlBRUUxTSCI6Ijk0OTE4MDM5LThkNWQtNGJjOC05YmJmLWE2NWFkODhmMGYzNjAwMSIsIkdNRl9XWCI6IiIsIkpTSEoiOjI4MDAwLCJHTUZfRU1BSUwiOiIiLCJCTUJfQkJIIjoiMSIsIk5TUk1DIjoi5YyX5Lqs5rGf5qOu6Ieq5o6n5pyJ6ZmQ5YWs5Y-4IiwiR01GX1FZTFgiOiIwMSIsIlRTQ0hCWiI6MCwiWFNGX1lIWkgiOiLkuK3lm73lt6XllYbpk7booYzljJfkuqzlpKfpg73luILmlK_ooYwgMDIwMDA4MDMxOTAyMDE1ODIyNCIsIkhKU0UiOjQ2NjYuNjcsIkdNRl9ZSFpIIjoi5oub5ZWG6ZO26KGM5rex5Zyz6JuH5Y-j5pSv6KGMODExMjgxNzkzNzEwMDAxPyIsIlhTRl9NQyI6IuWMl-S6rOaxn-ajruiHquaOp-aciemZkOWFrOWPuCIsIlhTRl9EWiI6IuWMl-S6rOW4gua1t-a3gOWMuuefpeaYpei3rzUx5Y-35oWO5piM5aSn5Y6mNTEx5a6kIDU5MjgxODg4IiwiUFlETSI6IiIsIlNLUiI6IuadjuaxgOWFsCJ9fV0sIkNPTU1PTl9JTlZPSUNFU19CQVRDSCI6eyJLUEpIIjoiMCIsIkZQTEIiOiIyIiwiU0xESUQiOiI5NSIsIkZQTFgiOiIxIiwiRlBRUVBDSCI6Ijk0OTE4MDM5LThkNWQtNGJjOC05YmJmLWE2NWFkODhmMGYzNiIsIktaWkQiOiIiLCJOU1JTQkgiOiI5MTExMDEwODIwMTgwNTA1MTYifX0";
        System.out.println(new String(Base64.decodeBase64(str.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8));
        //     System.out.println(Base64.encodeBase64URLSafeString(str.getBytes(StandardCharsets.UTF_8)));
        String data = "{\"COMMON_INVOICE\":[{\"COMMON_INVOICE_DETAIL\":[{\"FPHXZ\":\"0\",\"HSBZ\":\"1\",\"SL\":\"0.1\",\"SPBM\":\"1010115010300000000\",\"XMDJ\":\"9.9\",\"XMJE\":\"89.1\",\"XMMC\":\"春见耙耙柑2个340g起\",\"XMSL\":\"9\",\"XMXH\":0,\"YHZCBS\":\"0\"},{\"FPHXZ\":\"0\",\"HSBZ\":\"0\",\"LSLBS\":\"1\",\"SL\":\"0\",\"SPBM\":\"1010112090000000000\",\"XMDJ\":\"3.9\",\"XMJE\":\"97.5\",\"XMMC\":\"樱桃小番茄500g*1盒\",\"XMSL\":\"25\",\"XMXH\":0,\"YHZCBS\":\"1\",\"ZZSTSGL\":\"免税\"},{\"FPHXZ\":\"0\",\"HSBZ\":\"1\",\"SL\":\"0.16\",\"SPBM\":\"1030201030000000000\",\"XMDJ\":\"9.9\",\"XMJE\":\"89.1\",\"XMMC\":\"【2盒】康师傅蛋酥卷108g*2\",\"XMSL\":\"9\",\"XMXH\":0,\"YHZCBS\":\"0\"},{\"FPHXZ\":\"0\",\"HSBZ\":\"1\",\"SL\":\"0.1\",\"SPBM\":\"1010115010300000000\",\"XMDJ\":\"9.9\",\"XMJE\":\"89.1\",\"XMMC\":\"春见耙耙柑2个340g起\",\"XMSL\":\"9\",\"XMXH\":0,\"YHZCBS\":\"0\"},{\"FPHXZ\":\"0\",\"HSBZ\":\"1\",\"SL\":\"0.1\",\"SPBM\":\"1010115010300000000\",\"XMDJ\":\"6.45555556\",\"XMJE\":\"58.1\",\"XMMC\":\"春见耙耙柑2个340g起\",\"XMSL\":\"9\",\"XMXH\":0,\"YHZCBS\":\"0\"},{\"FPHXZ\":\"0\",\"HSBZ\":\"1\",\"SL\":\"0.1\",\"SPBM\":\"1010115013500000000\",\"XMDJ\":\"21.93\",\"XMJE\":\"43.86\",\"XMMC\":\"章姬草莓350g\",\"XMSL\":\"2\",\"XMXH\":0,\"YHZCBS\":\"0\"},{\"FPHXZ\":\"0\",\"HSBZ\":\"1\",\"SL\":\"0.1\",\"SPBM\":\"1010115012600000000\",\"XMDJ\":\"22.775\",\"XMJE\":\"45.55\",\"XMMC\":\"大·JJ级车厘子250g\",\"XMSL\":\"2\",\"XMXH\":0,\"YHZCBS\":\"0\"},{\"FPHXZ\":\"0\",\"HSBZ\":\"1\",\"SL\":\"0.1\",\"SPBM\":\"1010115011100000000\",\"XMDJ\":\"9.228\",\"XMJE\":\"46.14\",\"XMMC\":\"进口香蕉800g*1袋\",\"XMSL\":\"5\",\"XMXH\":0,\"YHZCBS\":\"0\"},{\"FPHXZ\":\"0\",\"HSBZ\":\"1\",\"SL\":\"0.1\",\"SPBM\":\"1010115010300000000\",\"XMDJ\":\"10.08333333\",\"XMJE\":\"30.25\",\"XMMC\":\"金秋砂糖橘*1盒900g\",\"XMSL\":\"3\",\"XMXH\":0,\"YHZCBS\":\"0\"},{\"FPHXZ\":\"0\",\"HSBZ\":\"1\",\"SL\":\"0.1\",\"SPBM\":\"1010115010300000000\",\"XMDJ\":\"8.36153846\",\"XMJE\":\"108.7\",\"XMMC\":\"春见耙耙柑2个340g起\",\"XMSL\":\"13\",\"XMXH\":0,\"YHZCBS\":\"0\"},{\"FPHXZ\":\"0\",\"HSBZ\":\"0\",\"LSLBS\":\"1\",\"SL\":\"0\",\"SPBM\":\"1010112090000000000\",\"XMDJ\":\"3.9\",\"XMJE\":\"144.3\",\"XMMC\":\"樱桃小番茄500g*1盒\",\"XMSL\":\"37\",\"XMXH\":0,\"YHZCBS\":\"1\",\"ZZSTSGL\":\"免税\"},{\"FPHXZ\":\"0\",\"HSBZ\":\"1\",\"SL\":\"0.1\",\"SPBM\":\"1010115010300000000\",\"XMDJ\":\"8.32105263\",\"XMJE\":\"158.1\",\"XMMC\":\"春见耙耙柑2个340g起\",\"XMSL\":\"19\",\"XMXH\":0,\"YHZCBS\":\"0\"}],\"COMMON_INVOICE_HEAD\":{\"BMB_BBH\":\"30.0\",\"FHR\":\"孙倩\",\"FPQQLSH\":\"111GTKGP550103374001\",\"GMF_MC\":\"北京银行股份有限公司东长安街支行\",\"GMF_NSRSBH\":\"9111010580111709XW\",\"GMF_QYLX\":\"01\",\"GMF_SJ\":\"18613858128\",\"HJJE\":\"926.7\",\"HJSE\":\"73.1\",\"JSHJ\":\"999.8\",\"KPLX\":\"0\",\"KPR\":\"张博洋\",\"NSRMC\":\"北京每日优鲜电子商务有限公司\",\"NSRSBH\":\"150001205110278555\",\"QDXMMC\":\"(详见销货清单)\",\"QD_BZ\":\"1\",\"SKR\":\"李娜\",\"TSCHBZ\":\"0\",\"XSF_DH\":\"010-64796389\",\"XSF_DZ\":\"北京市朝阳区广顺南大街19号院2号楼2层写字楼8号\",\"XSF_MC\":\"北京每日优鲜电子商务有限公司\",\"XSF_NSRSBH\":\"150001205110278555\",\"XSF_YHZH\":\"招商银北京北苑路支行110913772110601\"},\"COMMON_INVOICE_ORDER\":{\"DDH\":\"111GTKGP550103374001\"}}],\"COMMON_INVOICES_BATCH\":{\"FPLB\":\"2\",\"FPLX\":\"1\",\"FPQQPCH\":\"111GTKGP550103374\",\"KPJH\":\"2\",\"NSRSBH\":\"150001205110278555\",\"SLDID\":\"-1\"}}";
    
        //   String url = "{\"COMMON_INVOICE\":[{\"COMMON_INVOICE_DETAIL\":[{\"XMMC\":\"热力\",\"HSBZ\":\"0\",\"ZXBM\":\"\",\"LSLBS\":\"\",\"XMSL\":2,\"XMXH\":1,\"SPBM\":\"1100000000000000000\",\"GGXH\":\"YSM25M-0715-S-L\",\"XMDJ\":11666.665,\"SE\":4666.67,\"DW\":\"台\",\"YHZCBS\":\"0\",\"XMJE\":23333.33,\"SL\":0.2,\"FPHXZ\":\"0\"}],\"COMMON_INVOICE_ORDER\":{\"DDH\":\"Req201812170000012\",\"DDDATE\":\"\",\"THDH\":null},\"COMMON_INVOICE_HEAD\":{\"QDXMMC\":\"\",\"GMF_NSRSBH\":\"91440300192449004F\",\"XSF_DH\":\"59281888\",\"KPR\":\"李汀兰\",\"GMF_SJ\":\"\",\"FHR\":\"李汀兰\",\"QD_BZ\":\"0\",\"KPLX\":0,\"GMF_MC\":\"深圳市怡岛环境空调工程有限公司\",\"GMF_DZ\":\"深圳市南山区蛇口半岛花园A区6栋201电话:0755-26883751\",\"GMF_SF\":\"\",\"BZ\":\"FZHI1800097FS  中山红棉电镀厂YSM风柜FZHI1800097FS  中山红棉电镀厂YSM风柜\",\"BYZD4\":\"\",\"BYZD3\":\"\",\"BYZD2\":\"\",\"BYZD1\":\"\",\"HJJE\":\"23333.33\",\"NSRSBH\":\"911101082018050516\",\"BYZD5\":\"\",\"GMF_GDDH\":\"\",\"XSF_NSRSBH\":\"911101082018050516\",\"FPQQLSH\":\"94918039-8d5d-4bc8-91bf-a65ad88f0f96001\",\"GMF_WX\":\"\",\"JSHJ\":28000,\"GMF_EMAIL\":\"\",\"BMB_BBH\":\"1\",\"NSRMC\":\"北京江森自控有限公司\",\"GMF_QYLX\":\"01\",\"TSCHBZ\":0,\"XSF_YHZH\":\"中国工商银行北京大都市支行 0200080319020158224\",\"HJSE\":4666.67,\"GMF_YHZH\":\"招商银行深圳蛇口支行811281793710001?\",\"XSF_MC\":\"北京江森自控有限公司\",\"XSF_DZ\":\"北京市海淀区知春路51号慎昌大厦511室 59281888\",\"PYDM\":\"\",\"SKR\":\"李汀兰\"}}],\"COMMON_INVOICES_BATCH\":{\"KPJH\":\"0\",\"FPLB\":\"2\",\"SLDID\":\"95\",\"FPLX\":\"1\",\"FPQQPCH\":\"94918039-8d5d-4bc8-91bf-a65ad88f0f96\",\"KZZD\":\"\",\"NSRSBH\":\"911101082018050516\"}}";
        System.out.println("开票数据示例：" + data);
        System.out.println("base64加密开票数据示例----：" + Base64.encodeBase64URLSafeString(data.getBytes(StandardCharsets.UTF_8)));
    
    
        String url = "{\n" +
                "    \"COMMON_ORDER_BATCH\": {\n" +
                "        \"DDQQPCH\": \"dd123456789987987653\", \n" +
                "        \"NSRSBH\": \"150001205110278555\", \n" +
                "        \"SLDID\": \"118\", \n" +
                "        \"KPJH\": \"2\", \n" +
                "        \"FPLX\": \"1\", \n" +
                "        \"FPLB\": \"2\", \n" +
                "        \"KZZD\": \"扩展字段\"\n" +
                "    }, \n" +
                "    \"COMMON_ORDERS\": [\n" +
                "        {\n" +
                "            \"COMMON_ORDER_HEAD\": {\n" +
                "                \"DDQQLSH\": \"dd12345665465321332\", \n" +
                "                \"NSRSBH\": \"150001205110278555\", \n" +
                "                \"NSRMC\": \"纳税人名称\", \n" +
                "                \"KPLX\": \"0\", \n" +
                "                \"BMB_BBH\": \"33.0\", \n" +
                "                \"XSF_NSRSBH\": \"150001205110278555\", \n" +
                "                \"XSF_MC\": \"销售方名称\", \n" +
                "                \"XSF_DZ\": \"销售方地址\", \n" +
                "                \"XSF_DH\": \"123456987\", \n" +
                "                \"XSF_YH\": \"销售方银行名称\", \n" +
                "                \"XSF_ZH\": \"销售方银行账号\", \n" +
                "                \"GMF_NSRSBH\": \"150001205110278556\", \n" +
                "                \"GMF_MC\": \"购买方名称\", \n" +
                "                \"GMF_DZ\": \"购买方地址\", \n" +
                "                \"GMF_QYLX\": \"01\", \n" +
                "                \"GMF_SF\": \"\", \n" +
                "                \"GMF_GDDH\": \"12346546\", \n" +
                "                \"GMF_SJ\": \"15652241402\", \n" +
                "                \"GMF_EMAIL\": \"396061885@qq.com\", \n" +
                "                \"GMF_YH\": \"购买方银行名称\", \n" +
                "                \"GMF_ZH\": \"购买方银行账号\", \n" +
                "                \"KPR\": \"开票人\", \n" +
                "                \"SKR\": \"收款人\", \n" +
                "                \"FHR\": \"复核人\", \n" +
                "                \"YFP_DM\": \"\", \n" +
                "                \"YFP_HM\": \"\", \n" +
                "                \"QD_BZ\": \"0\", \n" +
                "                \"QDXMMC\": \"清单发票项目名称\", \n" +
                "                \"JSHJ\": \"30.00\", \n" +
                "                \"HJJE\": \"0\", \n" +
                "                \"HJSE\": \"0\", \n" +
                "                \"BZ\": \"备注\", \n" +
                "                \"CHYY\": \"冲红原因\", \n" +
                "                \"TSCHBZ\": \"0\",\n" +
                "                \"DDH\": \"3216549687\", \n" +
                "                \"THDH\": \"退货单号\", \n" +
                "                \"DDDATE\": \"2018-12-22 15:22:22\", \n" +
                "                \"BYZD1\": \"\", \n" +
                "                \"BYZD2\": \"备用字段2\", \n" +
                "                \"BYZD3\": \"备用字段3\", \n" +
                "                \"BYZD4\": \"备用字段4\", \n" +
                "                \"BYZD5\": \"备用字段5\"\n" +
                "            }, \n" +
                "            \"ORDER_INVOICE_ITEMS\": [\n" +
                "                {\n" +
                "                    \"XMXH\": \"\", \n" +
                "                    \"FPHXZ\": \"0\", \n" +
                "                    \"SPBM\": \"1010115010100000000\", \n" +
                "                    \"ZXBM\": \"1413223\", \n" +
                "                    \"YHZCBS\": \"0\", \n" +
                "                    \"LSLBS\": \"\", \n" +
                "                    \"ZZSTSGL\": \"\", \n" +
                "                    \"XMMC\": \"项目名称11121312\", \n" +
                "                    \"GGXH\": \"规格\", \n" +
                "                    \"DW\": \"单位\", \n" +
                "                    \"XMSL\": \"1\", \n" +
                "                    \"XMDJ\": \"30.00\", \n" +
                "                    \"XMJE\": \"30.00\", \n" +
                "                    \"HSBZ\": \"0\", \n" +
                "                    \"SL\": \"0.06\", \n" +
                "                    \"SE\": \"\", \n" +
                "                    \"BYZD1\": \"\", \n" +
                "                    \"BYZD2\": \"备用字段2\", \n" +
                "                    \"BYZD3\": \"备用字段3\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}\n";

//        url = "{\n" +
//                "    \"ZFPCH\": \"zf64564561234564\", \n" +
//                "    \"FP_DM\": \"5000191650\", \n" +
//                "    \"FPQH\": \"00011133\", \n" +
//                "    \"FPZH\": \"00011133\", \n" +
//                "    \"ZFLX\": \"0\", \n" +
//                "    \"ZFYY\": \"1231321\"\n" +
//                "}\n";
//
//        url = "{\n" +
//                "    \"RED_INVOICE_FORM_BATCH\": {\n" +
//                "        \"SQBSCQQPCH\": \"661803007415181220192533\", \n" +
//                "        \"NSRSBH\": \"911101082018050516\", \n" +
//                "        \"SLDID\": \"88\", \n" +
//                "        \"KPJH\": \"0\", \n" +
//                "        \"FPLX\": \"1\", \n" +
//                "        \"FPLB\": \"0\", \n" +
//                "        \"SQLB\": \"0\", \n" +
//                "        \"KZZD\": \"扩展字段\"\n" +
//                "    }, \n" +
//                "    \"RED_INVOICE_FORM_UPLOADS\": [\n" +
//                "        {\n" +
//                "            \"RED_INVOICE_FORM_HEAD\": {\n" +
//                "                \"SQBSCQQLSH\": \"661803007415181220192533\", \n" +
//                "                \"YYSBZ\": \"0000000000\", \n" +
//                "                \"XXBLX\": \"0\", \n" +
//                "                \"YFP_DM\": \"5000111560\", \n" +
//                "                \"YFP_HM\": \"74421093\", \n" +
//                "                \"YFP_KPRQ\": \"2018-12-18 10:37:08\", \n" +
//                "                \"TKSJ\": \"20181220103708\", \n" +
//                "                \"XSF_NSRSBH\": \"911101082018050516\", \n" +
//                "                \"XSF_MC\": \"北京江森自控有限公司\", \n" +
//                "                \"GMF_NSRSBH\": \"91460100MA5RCQQD9T\", \n" +
//                "                \"GMF_MC\": \"海口申源物业服务有限公司\", \n" +
//                "                \"HJJE\": \"3230.77\", \n" +
//                "                \"HJSE\": \"549.23\", \n" +
//                "                \"SQSM\": \"0000000100\", \n" +
//                "                \"BMB_BBH\": \"33.0\", \n" +
//                "                \"KZZD1\": \"扩展字段1\", \n" +
//                "                \"KZZD2\": \"扩展字段2\"\n" +
//                "            }, \n" +
//                "            \"ORDER_INVOICE_ITEMS\": [\n" +
//                "                {\n" +
//                "                    \"XMXH\": \"1\", \n" +
//                "                    \"FPHXZ\": \"0\", \n" +
//                "                    \"SPBM\": \"1100000000000000000\", \n" +
//                "                    \"ZXBM\": \"\", \n" +
//                "                    \"YHZCBS\": \"0\", \n" +
//                "                    \"LSLBS\": \"\", \n" +
//                "                    \"ZZSTSGL\": \"\", \n" +
//                "                    \"XMMC\": \"*电力热力水燃气*热力\", \n" +
//                "                    \"GGXH\": \"\", \n" +
//                "                    \"DW\": \"单\", \n" +
//                "                    \"XMSL\": \"1.0\", \n" +
//                "                    \"XMDJ\": \"3230.77\", \n" +
//                "                    \"XMJE\": \"3230.77\", \n" +
//                "                    \"HSBZ\": \"1\", \n" +
//                "                    \"SL\": \"0.17\", \n" +
//                "                    \"SE\": \"549.23\"\n" +
//                "                }\n" +
//                "            ]\n" +
//                "        }\n" +
//                "    ]\n" +
//                "}\n";
//
//        url = "{\n" +
//                "    \"SQBXZQQPCH\": \"654654161324654674\", \n" +
//                "    \"NSRSBH\": \"911101082018050516\", \n" +
//                "    \"SLDID\": \"\", \n" +
//                "    \"KPJH\": \"\", \n" +
//                "    \"FPLX\": \"1\", \n" +
//                "    \"FPLB\": \"0\", \n" +
//                "    \"TKRQ_Q\": \"20181201\", \n" +
//                "    \"TKRQ_Z\": \"20181220\", \n" +
//                "    \"GMF_NSRSBH\": \"\", \n" +
//                "    \"XSF_NSRSBH\": \"911101082018050516\", \n" +
//                "    \"XXBBH\": \"\", \n" +
//                "    \"XXBFW\": \"0\", \n" +
//                "    \"PAGENO\": \"1\", \n" +
//                "    \"PAGESIZE\": \"10\"\n" +
//                "}\n";
//        url = "{\n" +
//                "    \"NSRSBH\": \"150001196104213403\", \n" +
//                "    \"DDQQLSH\": \"111GTKGP527437599001\", \n" +
//                "    \"DDH\": \"111GTKGP527437599001\"\n" +
//                "}\n";
        url = "{\"DDQQPCH\":\"DX15897718581598005\",\"FPLX\":\"2\"}";
        System.out.println(Base64.encodeBase64URLSafeString(url.getBytes(StandardCharsets.UTF_8)));


//
//        String reqStr = "{\"HZFPSQBSCS_BATCH\":{\"NSRSBH\":\"911101082018050516\"},\"HZFPSQBSCS\":[{\"HZFPSQBSC_HEAD\":{\"KZZD2\":\"\",\"GMF_NSRSBH\":\"91110108600040399G\",\"GMF_MC\":\"微软（中国）有限公司\",\"KZZD1\":\"\",\"SQBSCQQLSH\":\"71c9cdc1-2a3c-4af6-abbc-851873cd026d\",\"YYSBZ\":\"0000000000\",\"TKSJ\":\"20181101085426\",\"YFP_DM\":\"5000153560\",\"YFP_HM\":\"29436572\",\"SQSM\":\"0000000100\",\"BMB_BBH\":\"1.0\",\"XXBLX\":\"0\"},\"HZFPSQBSC_DETAIL\":[{\"XMMC\":\"热力\",\"HSBZ\":\"0\",\"ZXBM\":\"\",\"LSLBS\":\"\",\"XMSL\":10,\"ZZSTSGL\":\"\",\"XMXH\":1,\"SPBM\":\"1100000000000000000\",\"GGXH\":null,\"XMDJ\":1497.6,\"SE\":2396.16,\"DW\":\"个\",\"YHZCBS\":\"0\",\"XMJE\":14976,\"SL\":0.16,\"FPHXZ\":\"0\"}]}]}";
////        R r = interfaceService.specialInvoiceRushRed(reqStr);
////        System.out.println(r);
//
//        HZFPSQBSCS_REQ req = JsonUtils.getInstance().parseObject(reqStr, HZFPSQBSCS_REQ.class);
//
//        System.out.println(JsonUtils.getInstance().toJsonString(req));
    
    
        //处理超限额订单
//        //价税分离
//        String requst = FileUtils.readFileToString(new File("d://jsfl.txt"), "utf-8");
//
//        List<CommonOrderInfo> orderInfo = JSON.parseArray(requst, CommonOrderInfo.class);
//        System.out.println(JsonUtils.getInstance().toJsonString(orderInfo));
//
//
//        String contet = FileUtils.readFileToString(new File("D:\\Desktop\\12222.txt"), Charsets.UTF_8);
//        String url1 = "http://sims.dxyun.com:52380/order-api/invoice/api/v3/AllocateInvoices";
//
//        String request = "Nonce=71971&SecretId=289efb7512e54146273b982456b03f42ea93&Signature=QJ6qP_e6uIGiAOpol4wkRw8TzPQ&Timestamp=1571994941&content=" + contet + "&encryptCode=0&zipCode=0";
//        String s = HttpUtils.doPost(url1, request);
//        System.out.println(s);
//
    
    
    }

}
