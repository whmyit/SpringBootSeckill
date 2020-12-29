package com.dxhy.order.consumer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dxhy.invoice.protocol.sk.doto.request.SkReqYhzxxcx;
import com.dxhy.order.api.RedisService;
import com.dxhy.order.constant.OrderInfoEnum;
import com.dxhy.order.consumer.config.OpenApiConfig;
import com.dxhy.order.consumer.modules.fiscal.service.UnifyService;
import com.dxhy.order.consumer.modules.fiscal.service.a9.SldManagerServiceA9;
import com.dxhy.order.model.a9.pdf.GetPdfRequest;
import com.dxhy.order.model.a9.pdf.GetPdfResponseExtend;
import com.dxhy.order.model.a9.query.FpYdtj;
import com.dxhy.order.model.a9.query.YhzxxResponse;
import com.dxhy.order.model.a9.zf.KbZfRequest;
import com.dxhy.order.model.a9.zf.KbZfResponseExtend;
import com.dxhy.order.model.a9.zf.ZfRequest;
import com.dxhy.order.model.c48.zf.DEPRECATE_INVOICES_RSP;
import com.dxhy.order.model.entity.InvoiceQuotaEntity;
import com.dxhy.order.utils.HttpInvoiceRequestUtil;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class) // SpringJUnit支持，由此引入Spring-Test框架支持！
@SpringBootTest(classes = ConsumerStarter.class) // 指定我们SpringBoot工程的Application启动类
@WebAppConfiguration
@Slf4j
public class InterfaceTestA9 {
    
    @Autowired
    private SldManagerServiceA9 sldManagerService;
    
    @Autowired
    private UnifyService unifyService;
    
    @Autowired
    private SldManagerServiceA9 sldManagerServiceA9;
    
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

   /* @Test
    public void testJspXX() {
        String request = "{\"fpzldm\":\"0\",\"nsrsbh\":\"140301206111099566\",\"sldid\":\"160\",\"syzt\":\"2\"}";
        JspxxRequest request2 = JsonUtils.getInstance().parseObject(request, JspxxRequest.class);
        JspxxResponseA9 queryJspxx = sldManagerService.queryJspxx(request2);
        System.out.println(JsonUtils.getInstance().toJsonString(queryJspxx));
    }
    */

    
    /**
     * ' 空白发票作废测试接口
     */
    @Test
    public void testKbValid() throws IOException {
        
        KbZfRequest request = new KbZfRequest();
        request.setFP_DM("1400131560");
        request.setFP_HM("59298479");
        request.setTerminalCode(OrderInfoEnum.TAX_EQUIPMENT_A9.getKey());
        KbZfResponseExtend zfInvoice = HttpInvoiceRequestUtil.kbZfInvoice(OpenApiConfig.blankInvoiceZf, request, OrderInfoEnum.TAX_EQUIPMENT_A9.getKey());
        System.out.println(JsonUtils.getInstance().toJsonString(zfInvoice));
        log.info("发票作废执行结果:{}");
    }
    
    /**
     * 发票作废测试
     */
    @Test
    public void testValid() throws IOException {
        ZfRequest request = new ZfRequest();
       /* request.setFp_DM("1400131560");
        request.setFpqh("59298134");
        request.setFpzh("59298134");
        request.setFpzldm("0");
        // request.setNsrsbh("150301199811285326 ");
        // request.setSldid("0");
        request.setZflx("1");
        request.setZfpch(UUID.randomUUID().toString());*/

        //DEPRECATE_INVOICES_RSP zfInvoice = HttpInvoiceRequestUtil.zfInvoice(OpenApiConfig.ykfpzf, request, OrderInfoEnum.TAX_EQUIPMENT_C48.getKey());

        request.setNSRSBH("91500108MA004CPN95");
        request.setSLDID("9");
        request.setFP_DM("111000010011");
        request.setFP_QH("52543338");
        request.setFP_ZH("52543338");
        request.setFPZLDM("026");
        request.setZFYY("shibai");
        request.setZFR("shibai");
//        request.setTerminalCode("taxControl");
        request.setZFPCH("werererr");
        DEPRECATE_INVOICES_RSP zfInvoice = HttpInvoiceRequestUtil.zfInvoice(OpenApiConfig.ykfpzf, request, OrderInfoEnum.TAX_EQUIPMENT_NEWTAX.getKey());

    }
    @Reference
    private RedisService redisService;
    @Test
    public void kpxe(){
        InvoiceQuotaEntity taxControl = unifyService.queryInvoiceQuotaInfoFromRedis("91500108MA004CPN95", "026", "009");
    }
    @Test
    public void ydtj(){
        SkReqYhzxxcx paramSkReqYhzxxcx = new SkReqYhzxxcx();
        //paramSkReqYhzxxcx.setFjh("0001");
        paramSkReqYhzxxcx.setNsrsbh("91500108MA004CPN95");
        paramSkReqYhzxxcx.setFpzlDm("026");
        paramSkReqYhzxxcx.setSsyf("202007");
        paramSkReqYhzxxcx.setFjh("016000014003");
//        paramSkReqYhzxxcx.setFjh("0");
//        //A9
        //paramSkReqYhzxxcx.setFjh("1");
        //paramSkReqYhzxxcx.setNsrsbh("140301206111099566");
//        //C48
//        paramSkReqYhzxxcx.setNsrsbh("15000120561127953X");
//        paramSkReqYhzxxcx.setFpzlDm("2");
//        paramSkReqYhzxxcx.setSsyf("202002");
        //YhzxxResponse taxControl = unifyService.queryYhzxx(paramSkReqYhzxxcx, OrderInfoEnum.TAX_EQUIPMENT_A9.getKey());

        //YhzxxResponse taxControl = unifyService.queryYhzxx(paramSkReqYhzxxcx, OrderInfoEnum.TAX_EQUIPMENT_C48.getKey());
        YhzxxResponse taxControl = unifyService.queryYhzxx(paramSkReqYhzxxcx, "009");
        Map<String, Object> map = convertToResultMap(taxControl.getResult().getFpYdtj().get(0));
        System.out.println(JsonUtils.getInstance().toJsonString(map));
        //unifyService.queryYhzxx(paramSkReqYhzxxcx, "009");
    }
    @Resource
    private SldManagerServiceA9 sldManagerServiceA;
    
    @Test
    public void getPdf() {
        GetPdfRequest pdfRequestBean = HttpInvoiceRequestUtil.getPdfRequestBean("395749547152564224", "91500108MA004CPN95", "taxControl", "111000010011", "52543320", "");
        GetPdfResponseExtend pdf = HttpInvoiceRequestUtil.getPdf(OpenApiConfig.getPdfFg, OpenApiConfig.getPdf, pdfRequestBean, "taxControl");
        //{"code":"0000","content":null,"result":{"fpqqpch":"395749547152564224","status_MESSAGE":"获取PDF,全部成功!","status_CODE":"050000","response_EINVOICE_PDF":[{"pdf_FILE":"UEsDBBQAAAAIAKiIZXjs+F3WhAAAAMIAAAAUAAAARG9jXzAvQ3VzdG9tVGFncy54bWyzsa/IzVEoSy0qzszPs1Uy1DNQUkjNS85PycxLt1UKDXHTtVCyt+OyyU9LsXIuLS7Jzw1JTC9WAGrKK7YCCtoqZZSUFFjp65eXl+sB+cUFqcl6+UXp+kYGhmZKdqj6FEIqC1I9XWyVoBJumTmpPvnJdnAFekBzbfSRpSA8uAJ0frEdFwBQSwMEFAAAAAgAqIhleCPILZFjAQAAgAEAABMAAABEb2NfMC9pbWFnZV8xMDIuamIym+7lZMTLJcXFyMDAAMIMBmBKGIhTo{"code":"0000","content":null,"result":{"fpqqpch":"395749547152564224","status_MESSAGE":"获取PDF,全部成功!","status_CODE":"050000","response_EINVOICE_PDF":[{"pdf_FILE":"UEsDBBQAAAAIAKiIZXjs+F3WhAAAAMIAAAAUAAAARG9jXzAvQ3VzdG9tVGFncy54bWyzsa/IzVEoSy0qzszPs1Uy1DNQUkjNS85PycxLt1UKDXHTtVCyt+OyyU9LsXIuLS7Jzw1JTC9WAGrKK7YCCtoqZZSUFFjp65eXl+sB+cUFqcl6+UXp+kYGhmZKdqj6FEIqC1I9XWyVoBJumTmpPvnJdnAFekBzbfSRpSA8uAJ0frEdFwBQSwMEFAAAAAgAqIhleCPILZFjAQAAgAEAABMAAABEb2NfMC9pbWFnZV8xMDIuamIym+7lZMTLJcXFyMDAAMIMBmBKGIhTo
    }

    private Map<String, Object> convertToResultMap(FpYdtj fpYdtj) {
        Map<String,Object> resultMap = new HashMap<String,Object>();

        //外层数据解析 赋值 开具份数统计
        resultMap.put("nsrsbh", fpYdtj.getXsfNsrsbh());
        resultMap.put("fjh", fpYdtj.getFjh());
        resultMap.put("fpzlDm", fpYdtj.getFpzl());
        resultMap.put("qckcfs", fpYdtj.getQckc());
        resultMap.put("qmkcfs", fpYdtj.getQmkc());
        resultMap.put("gjfpfs", fpYdtj.getGjfp());
        resultMap.put("thfpfs", fpYdtj.getThfp());
        resultMap.put("shfpfs", fpYdtj.getThfp());
        resultMap.put("fpfpfs", fpYdtj.getFsfp());
        resultMap.put("zsfpkjfs", fpYdtj.getZsfp());
        resultMap.put("zsfpzffs", fpYdtj.getZszf());
        resultMap.put("fsfpkjfs", fpYdtj.getFsfp());
        resultMap.put("fsfpzffs", fpYdtj.getFszf());
        resultMap.put("zbrq", fpYdtj.getZbsj());
        resultMap.put("ssqj", fpYdtj.getTjnf() + fpYdtj.getTjyf());

        //存放总计金额
        Map<String,Object> jeTjMap = new HashMap<String,Object>();
        //根据税率金额统计
        List<Map<String,Object>> slList = new ArrayList<Map<String,Object>>();

        Map<String, Map<String, Object>> slMap = new HashMap<String, Map<String, Object>>();

        //销项正数金额
        if(StringUtils.isNotBlank(fpYdtj.getZsjeTj())){
            jeTjMap.put("zsje",fpYdtj.getZsjeTj());
        }

        //销项正数税额
        if(StringUtils.isNotBlank(fpYdtj.getZsseTj())){
            jeTjMap.put("zsse", fpYdtj.getZsseTj());

        }

        //销项正废金额
        if(StringUtils.isNotBlank(fpYdtj.getZfjeTj())){
            jeTjMap.put("zfje", fpYdtj.getZfjeTj());

        }
        //销项正废税额
        if(StringUtils.isNotBlank(fpYdtj.getZfseTj())){
            jeTjMap.put("zfse",fpYdtj.getZfseTj());

        }
        //销项负数金额
        if(StringUtils.isNotBlank(fpYdtj.getFsjeTj())){
            jeTjMap.put("fsje", fpYdtj.getFsjeTj());

        }
        //销项负数税额
        if(StringUtils.isNotBlank(fpYdtj.getFsseTj())){
            jeTjMap.put("fsse",fpYdtj.getFsseTj());

        }
        //销项负废金额
        if(StringUtils.isNotBlank(fpYdtj.getFfjeTj())){
            jeTjMap.put("ffje", fpYdtj.getFfjeTj());

        }
        //销项负废税额
        if(StringUtils.isNotBlank(fpYdtj.getFfseTj())){
            jeTjMap.put("ffse", fpYdtj.getFfseTj());

        }
        //实际销项金额
        if(StringUtils.isNotBlank(fpYdtj.getSjjeTj())){
            jeTjMap.put("sjje",fpYdtj.getSjjeTj());

        }
        //实际销项税额
        if(StringUtils.isNotBlank(fpYdtj.getSjseTj())){
            jeTjMap.put("sjse",fpYdtj.getSjseTj());
        }
        resultMap.put("hjjes", jeTjMap);

        String tjxxmx = fpYdtj.getTjxxmx();
        if(StringUtils.isNotBlank(tjxxmx)){
            JSONArray objects = JSONObject.parseArray(tjxxmx);
            for(int j = 0; j < objects.size(); j ++){
                String string = objects.get(j).toString();
                Map<String,Object> map =  JsonUtils.getInstance().parseObject(string,Map.class);
                if (map.size() > 0) {
                    List<Map> list = (List) map.get("mxxxs");
                    String sl = map.get("sl").toString();
                    Map<String, Map<String, Object>> stringMapMap = buildMapNewTax(list, sl);
                    Map<String, Object> map1 = stringMapMap.get(sl);
                    slList.add(map1);
                }

            }
        }

        Collections.sort(slList, new Comparator<Map<String,Object>>(){
            //重写排序规
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                if(Double.valueOf(String.valueOf(o1.get("sl"))) > Double.valueOf(String.valueOf(o2.get("sl")))){
                    return -1;
                }else if(Double.valueOf(String.valueOf(o1.get("sl"))) < Double.valueOf(String.valueOf(o2.get("sl")))){
                    return 1;
                }
                return 0;
            }
        });

        resultMap.put("yhzxxs", slList);
        return resultMap;
    }
    private Map<String,Map<String,Object>> buildMapNewTax(List<Map> zsjeMap, String sl){
        Map<String, Map<String, Object>> slMap = new HashMap<String, Map<String, Object>>();
        BigDecimal hjje = new BigDecimal("0.00");
        BigDecimal hjse = new BigDecimal("0.00");

        for(int i = 0; i < zsjeMap.size(); i ++){
            Map map2 = zsjeMap.get(i);
            Map<String,Object> map = new HashMap<String,Object>();
            List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
            Map<String,Object> jeMap = new HashMap<String,Object>();
            jeMap.put("kplxmc", map2.get("kplxmc"));
            jeMap.put("hjjese", map2.get("hjjese"));

            Map<String, Object> map1 = slMap.get(sl);
            if(!ObjectUtils.isEmpty(map1)){
                List mxxxsList = (List) map1.get("mxxxs");
                mxxxsList.add(jeMap);
                map1.put("mxxxs", mxxxsList);
                map = map1;
            }else{
                list.add(jeMap);
                map.put("mxxxs", list);
                map.put("sl",sl);
            }
            slMap.put(sl, map);
        }
        Map<String, Object> map = slMap.get(sl);
        List<Map<String, Object>> mxxxs = (List<Map<String, Object>>) map.get("mxxxs");

        for(int u = 0; u < 2; u ++){
            if(u == 0){
                Map<String,Object> jeMap = new HashMap<String,Object>();
                for(int j = 0; j < zsjeMap.size(); j ++){
                    if("销项正数金额".equals(zsjeMap.get(j).get("kplxmc"))){
                        hjje = hjje.add(new BigDecimal(zsjeMap.get(j).get("hjjese").toString()));
                    }
                    if("销项正废金额".equals(zsjeMap.get(j).get("kplxmc"))){
                        hjje = hjje.add(new BigDecimal(zsjeMap.get(j).get("hjjese").toString()));
                    }
                    if("销项负数金额".equals(zsjeMap.get(j).get("kplxmc"))){
                        hjje = hjje.add(new BigDecimal(zsjeMap.get(j).get("hjjese").toString()));
                    }
                    if("销项负废金额".equals(zsjeMap.get(j).get("kplxmc"))){
                        hjje = hjje.add(new BigDecimal(zsjeMap.get(j).get("hjjese").toString()));
                    }
                }
                jeMap.put("kplxmc", "实际销项金额");
                jeMap.put("hjjese", hjje);
                mxxxs.add(jeMap);

            }if(u == 1){
                Map<String,Object> jeMap = new HashMap<String,Object>();
                for(int j = 0; j < zsjeMap.size(); j ++){

                    if("销项正数税额".equals(zsjeMap.get(j).get("kplxmc"))){
                        hjse = hjse.add(new BigDecimal(zsjeMap.get(j).get("hjjese").toString()));
                    }
                    if("销项正废税额".equals(zsjeMap.get(j).get("kplxmc"))){
                        hjse = hjse.add(new BigDecimal(zsjeMap.get(j).get("hjjese").toString()));
                    }
                    if("销项负数税额".equals(zsjeMap.get(j).get("kplxmc"))){
                        hjse = hjse.add(new BigDecimal(zsjeMap.get(j).get("hjjese").toString()));
                    }
                    if("销项负废税额".equals(zsjeMap.get(j).get("kplxmc"))){
                        hjse = hjse.add(new BigDecimal(zsjeMap.get(j).get("hjjese").toString()));
                    }
                }
                jeMap.put("kplxmc", "实际销项税额");
                jeMap.put("hjjese", hjse);
                mxxxs.add(jeMap);
            }
        }
        map.put("mxxxs", mxxxs);
        map.put("sl",sl);
        slMap.put(sl, map);
        return slMap;
    }
}
