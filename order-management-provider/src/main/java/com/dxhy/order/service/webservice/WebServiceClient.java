package com.dxhy.order.service.webservice;

import com.dxhy.order.config.OpenApiConfig;
import com.dxhy.order.constant.ConfigurerInfo;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Map;

/*import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;*/

@Slf4j
public class WebServiceClient {

    /**
     * 方式1.代理类工厂的方式,需要拿到对方的接口
     */
    public static void clientForProxyFactry() {
//        // 接口地址
//        String address = "http://localhost:8081/order-api/ws/helloWebService?wsdl";
//        // 代理工厂
//        JaxWsProxyFactoryBean jwpf = new JaxWsProxyFactoryBean();
//        // 设置代理地址
//        jwpf.setAddress(address);
//        // 设置接口类型
//        jwpf.setServiceClass(HelloWebService.class);
//        // 创建一个代理接口实现
//        HelloWebService cs = (HelloWebService) jwpf.create();
//        // 数据准备
//        String userName = "Leftso";
//        // 调用代理接口的方法调用并返回结果
//        String result = cs.Hello(userName);
//        System.out.println("返回结果:" + result);
    }

    /**
     * 动态调用方式（该接口加上用户名和密码不能使用）
     */
   /* public static String clientForDynamicProxy(String wsdlAddress, String methodName, Map<String,String> praMap) {
        log.info("webservice客户端调用，wsdl地址：{}，接口名称：{}，请求参数：{}", wsdlAddress, methodName, praMap);
        String result = null;
        // 创建动态客户端
        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        Client client = dcf.createClient(wsdlAddress);
        // 需要密码的情况需要加上用户名和密码
        // client.getOutInterceptors().add(new ClientLoginInterceptor(USER_NAME,
        // PASS_WORD));
        Object[] objects = new Object[0];
        try {
            // invoke("方法名",参数1,参数2,参数3....);
            objects = client.invoke(methodName,
                    praMap.get(ConfigurerInfo.TIMESTAMP)
                    ,praMap.get(ConfigurerInfo.NONCE)
                    ,praMap.get(ConfigurerInfo.SECRETID)
                    ,praMap.get(ConfigurerInfo.SIGNATURE)
                    ,praMap.get(ConfigurerInfo.ENCRYPTCODE)
                    ,praMap.get(ConfigurerInfo.ZIPCODE)
                    ,praMap.get(ConfigurerInfo.CONTENT));
            if(objects.length>0){
                result = (String)objects[0];
                log.info("webservice客户端调用结束，出参：{}",result);
            }else{
                log.info("webservice客户端调用无响应");
            }
        } catch (Exception e) {
            log.info("webservice客户端调用异常：{}",e);
        }
        return result;
    }*/

    /**
     * axis动态调用webservice  （测试通过，可以使用）
     */
    public static String clientForAxis(String wsdlAddress, String method, Map<String,String> praMap) {
        log.info("webservice客户端调用，wsdl地址：{}，接口名称：{}，请求参数：{}", wsdlAddress, method, praMap);
//        String wsdlUrl = wsdlAddress;
        // 直接引用远程的wsdl文件
        // 以下都是套路
        Call call = null;
        String result="";
        try {
            Service service = new Service();
            call = (Call) service.createCall();
            call.setTargetEndpointAddress(new URL(wsdlAddress));
            call.setUsername(OpenApiConfig.wsAuthUsername);
            call.setPassword(OpenApiConfig.wsAuthPassword);
            call.setOperationName(new QName("urn:sap-com:document:sap:soap:functions:mc-style", method));// WSDL里面描述的接口名称
            call.addParameter("Zinput",org.apache.axis.encoding.XMLType.XSD_STRING,javax.xml.rpc.ParameterMode.IN);
            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);// 设置返回类型
            log.info("webservice调用接口开始，调用方法名称：{}，请求地址：{},请求参数：{}",method, wsdlAddress, JsonUtils.getInstance().toJsonString(praMap));
            result = (String) call.invoke(new Object[] { JsonUtils.getInstance().toJsonString( praMap )});
            log.info("webservice调用接口结束，调用方法名称：{}，请求地址：{},返回结果：{}",method, wsdlAddress,result);
        } catch (ServiceException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 静态调用方式（测试通过，可以使用）
     */
    /*public static String clientForStaticProxy(String wsdlAddress, String interfaceType, Map<String,String> praMap) {
        log.info("webservice客户端调用，wsdl地址：{}，接口类型：{}，请求参数：{}", wsdlAddress, interfaceType, praMap);
        String result = null;
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        //设置地址
        factoryBean.setAddress(wsdlAddress);
        factoryBean.setUsername(OpenApiConfig.wsAuthUsername);
        factoryBean.setPassword(OpenApiConfig.wsAuthPassword);
//      factoryBean.getInFaultInterceptors().add(new LoginInterceptor("rfcuser","654321a"));
        *//*factoryBean.setServiceClass(ZWSINVOICESERVICE.class);
        ZWSINVOICESERVICE impl = (ZWSINVOICESERVICE) factoryBean.create();
        Zsinput zsinput = new Zsinput();
        zsinput.setTimestamp(praMap.get(ConfigurerInfo.TIMESTAMP));
        zsinput.setNonce(praMap.get(ConfigurerInfo.NONCE));
        zsinput.setSecretid(praMap.get(ConfigurerInfo.SECRETID));
        zsinput.setSignature(praMap.get(ConfigurerInfo.SIGNATURE));
        zsinput.setEncryptcode(praMap.get(ConfigurerInfo.ENCRYPTCODE));
        zsinput.setZipcode(praMap.get(ConfigurerInfo.ZIPCODE));
        zsinput.setContent(praMap.get(ConfigurerInfo.CONTENT));
        Zsoutput zsoutput = null;
        log.info("webservice调用接口开始，接口类型：{}，请求地址：{},请求参数：{}",interfaceType, wsdlAddress, JsonUtils.getInstance().toJsonString(zsinput));
        if(OrderInfoEnum.INTERFACE_TYPE_INVOICE_PUSH_STATUS_1.getKey().equals(interfaceType)){//开票回推
            zsoutput = impl.zinvoicePush(zsinput);
        }else if (OrderInfoEnum.INTERFACE_TYPE_INVOICE_PUSH_STATUS_2.getKey().equals(interfaceType)){//作废回推
            zsoutput = impl.zinvoiceDelete(zsinput);
        }else if(OrderInfoEnum.INTERFACE_TYPE_INVOICE_PUSH_STATUS_6.getKey().equals(interfaceType)){//撤销状态回推
        }*//**//*
        log.info("webservice调用接口结束，接口类型：{}，请求地址：{},返回结果：{}",interfaceType, wsdlAddress, JsonUtils.getInstance().toJsonString(zsoutput));
        result = JsonUtils.getInstance().toJsonString(zsoutput);*//*
        return result;
    }
*/

    public static void main(String[] args) throws Exception {
      /* JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setUsername("rfcuser");
        factoryBean.setPassword("654321a");
        factoryBean.setServiceClass(ZWSINVOICESERVICE.class);
//        factoryBean.setAddress("http://gmdev.itg.net:8000/sap/bc/srt/wsdl/bndg_EB005E2719EEE3F1A9A60050568EE201/wsdl11/allinone/ws_policy/document");
        factoryBean.setAddress("http://gmdev.itg.net:8000/sap/bc/srt/rfc/sap/zws_invoice_service/210/zws_invoice_service/zws_invoice_service");
//        factoryBean.getInFaultInterceptors().add(new LoginInterceptor("rfcuser","654321a"));

        ZWSINVOICESERVICE impl = (ZWSINVOICESERVICE) factoryBean.create();
        Zsinput zsinput = new Zsinput();
        zsinput.setContent("eyJEREZQWlhYIjpbeyJEREZQWFgiOnsiQk1CQkJIIjoiMzMuMCIsIkJZWkQxIjoiIiwiQllaRDIiOiIiLCJCWVpEMyI6IiIsIkJZWkQ0IjoiIiwiQllaRDUiOiIiLCJCWiI6IiIsIkNIWVkiOiIiLCJEREgiOiI1MjU3NjgxNDEzODciLCJERFFRTFNIIjoiNDc5MTU2MjYzMDMzNjMwNzIxIiwiRERTSiI6IjIwMjAtMTAtMjIgMTU6MjI6NDYiLCJERFpUIjoiMDAwMDAwIiwiRERaVFhYIjoi5byA56Wo5oiQ5YqfIiwiRFRNIjoiIiwiRkhSIjoiIiwiRlBETSI6IjUwMDAyMDE1MzAiLCJGUEhNIjoiODU4Mjk5OTUiLCJGUExYRE0iOiIwMDQiLCJGV00iOiIwYytCejZiNmZZSXRkQ3p2bC96Nk9EbGRxMTVWdWJGb2MvbTBReUpFU2hPTG1pdW55Q2ZUeEFmbmtDdU8xekVTcEJZR01qbWltRkxwY05PaEJNamRLcDZQL3kxN3R1TjU3Rk5NZk9tbUN3d1Nkeis1ZjlzSTZYNjdCaHdwajMzTmh4R1dHRXFSYkxCbDVBc3ROelRnQlE9PSIsIkdNRkJNIjoiIiwiR01GREgiOiIxODY1OTIwNDU1NiIsIkdNRkRaIjoi5Y6m6Zeo5biC5rmW6YeM5Yy65LuZ5bKz6LevNDY4OOWPt+Wbvei0uOS4reW/gzI4MDHljZUiLCJHTUZEWllYIjoiIiwiR01GTFgiOiIwNCIsIkdNRk1DIjoi5Y+M5Y2V5L2N5rWL6K+V5a6i5oi3IiwiR01GU0JIIjoiOTEzNTAyMDA2MTIwMzQyNDQ5IiwiR01GU0YiOiIiLCJHTUZTSkgiOiIiLCJHTUZZSCI6Ijk5OTk5OTk5IiwiR01GWkgiOiIxMjM0NTY3ODkwIiwiSEpKRSI6IjU4NDkuMDYiLCJISlNFIjoiMzUwLjk0IiwiSlFCSCI6IjY2MTYwMDA4MTk3MyIsIkpTSEoiOiI2MjAwLjAwIiwiSllNIjoiNzI3NjE1MjM3MjM1MjAyMTcyNTEiLCJLUEZTIjoiMSIsIktQTFgiOiIwIiwiS1BSIjoi5byA56WoIiwiS1BSUSI6IjIwMjAtMTEtMDIgMTU6NTI6MDciLCJLUFpEIjoiIiwiTlNSTUMiOiLljqbpl6jlm73otLjpm4blm6LogqHku73mnInpmZDlhazlj7giLCJOU1JTQkgiOiIxNDAzMDEyMDYxMTEwOTk1NjYiLCJQREZEWiI6IiIsIlBERlpKTCI6IiIsIlFEQloiOiIwIiwiUURYTU1DIjoiIiwiU0tSIjoi6ZmI55yfIiwiVEhESCI6IiIsIlRTQ0hCWiI6IiIsIlhIRkRIIjoiMDU5Mi01ODk4ODg4IiwiWEhGRFoiOiLljqbpl6jluILmuZbph4zljLrku5nlsrPot680Njg45Y+35Zu96LS45Lit5b+DMjgwMeWNleWFgyIsIlhIRk1DIjoi5Y6m6Zeo5Zu96LS46ZuG5Zui6IKh5Lu95pyJ6ZmQ5YWs5Y+4IiwiWEhGU0JIIjoiMTQwMzAxMjA2MTExMDk5NTY2IiwiWEhGWUgiOiI5OTk5OTk5OTk5OTkiLCJYSEZaSCI6IkE5NTU4OTg0NTY0MzQxMiIsIllGUERNIjoiIiwiWUZQSE0iOiIiLCJZV0xYIjoiIn0sIkRES1pYWCI6W3siQllaRDEiOiIiLCJCWVpEMiI6IiIsIkJZWkQzIjoiIiwiRERIIjoiNTI1NzY4MTQxMzg3IiwiRERMWCI6IjAiLCJERFFRTFNIIjoiNDc5MTU2MjYzMDMzNjMwNzIxIn1dLCJERE1YWFgiOlt7IkRKIjoiMTguODY3OTI0NTMiLCJEVyI6IlRPTiIsIkZQSFhaIjoiMCIsIkdHWEgiOiIiLCJIU0JaIjoiMCIsIkpFIjoiNTg0OS4wNiIsIktDRSI6IiIsIkxTTEJTIjoiIiwiU0UiOiIzNTAuOTQiLCJTTCI6IjAuMDYiLCJTUEJNIjoiMTA3MDQwMjAxMDAwMDAwMDAwMCIsIlNQU0wiOiIzMTAuMDAwMDAwMDAiLCJYSCI6IjEiLCJYTU1DIjoiKuWMluWtpue6pOe7tCrlj4zljZXkvY3liqjmgIExMC0xIiwiWUhaQ0JTIjoiMCIsIlpaU1RTR0wiOiIifV19XSwiWlRETSI6IjAwMDAiLCJaVFhYIjoi5Y+R56Wo5byA5YW35oiQ5Yqf77yBIn0=");
        zsinput.setEncryptcode("1");
        zsinput.setNonce("aaaaaaa");
        zsinput.setSecretid("bbbbbbbbbbbbbbb");
        zsinput.setSignature("");
        zsinput.setTimestamp("ccccccccccc");
        zsinput.setZipcode("1");
        Zsoutput zsoutput = impl.zinvoicePush(zsinput);
        System.out.println(JsonUtils.getInstance().toJsonString(zsoutput));*/


        /*
        String wsdl = "http://gmdev.itg.net:8000/sap/bc/srt/rfc/sap/zws_invoice_service/210/zws_invoice_service/zws_invoice_service";
        String namespaceURI = "urn:sap-com:document:sap:soap:functions:mc-style";
        String localPart = "ZWS_INVOICE_SERVICE";
        SoapClient soapClient = new SoapClient(wsdl, namespaceURI, localPart);
        soapClient.setRequireAuth(true);
        soapClient.setUsername("rfcuser");
        soapClient.setPassword("654321a");
        ZWSINVOICESERVICE service = soapClient.create(ZWSINVOICESERVICE.class);
        Zsinput zsinput = new Zsinput();
        zsinput.setContent("eyJEREZQWlhYIjpbeyJEREZQWFgiOnsiQk1CQkJIIjoiMzMuMCIsIkJZWkQxIjoiIiwiQllaRDIiOiIiLCJCWVpEMyI6IiIsIkJZWkQ0IjoiIiwiQllaRDUiOiIiLCJCWiI6IiIsIkNIWVkiOiIiLCJEREgiOiI1MjU3NjgxNDEzODciLCJERFFRTFNIIjoiNDc5MTU2MjYzMDMzNjMwNzIxIiwiRERTSiI6IjIwMjAtMTAtMjIgMTU6MjI6NDYiLCJERFpUIjoiMDAwMDAwIiwiRERaVFhYIjoi5byA56Wo5oiQ5YqfIiwiRFRNIjoiIiwiRkhSIjoiIiwiRlBETSI6IjUwMDAyMDE1MzAiLCJGUEhNIjoiODU4Mjk5OTUiLCJGUExYRE0iOiIwMDQiLCJGV00iOiIwYytCejZiNmZZSXRkQ3p2bC96Nk9EbGRxMTVWdWJGb2MvbTBReUpFU2hPTG1pdW55Q2ZUeEFmbmtDdU8xekVTcEJZR01qbWltRkxwY05PaEJNamRLcDZQL3kxN3R1TjU3Rk5NZk9tbUN3d1Nkeis1ZjlzSTZYNjdCaHdwajMzTmh4R1dHRXFSYkxCbDVBc3ROelRnQlE9PSIsIkdNRkJNIjoiIiwiR01GREgiOiIxODY1OTIwNDU1NiIsIkdNRkRaIjoi5Y6m6Zeo5biC5rmW6YeM5Yy65LuZ5bKz6LevNDY4OOWPt+Wbvei0uOS4reW/gzI4MDHljZUiLCJHTUZEWllYIjoiIiwiR01GTFgiOiIwNCIsIkdNRk1DIjoi5Y+M5Y2V5L2N5rWL6K+V5a6i5oi3IiwiR01GU0JIIjoiOTEzNTAyMDA2MTIwMzQyNDQ5IiwiR01GU0YiOiIiLCJHTUZTSkgiOiIiLCJHTUZZSCI6Ijk5OTk5OTk5IiwiR01GWkgiOiIxMjM0NTY3ODkwIiwiSEpKRSI6IjU4NDkuMDYiLCJISlNFIjoiMzUwLjk0IiwiSlFCSCI6IjY2MTYwMDA4MTk3MyIsIkpTSEoiOiI2MjAwLjAwIiwiSllNIjoiNzI3NjE1MjM3MjM1MjAyMTcyNTEiLCJLUEZTIjoiMSIsIktQTFgiOiIwIiwiS1BSIjoi5byA56WoIiwiS1BSUSI6IjIwMjAtMTEtMDIgMTU6NTI6MDciLCJLUFpEIjoiIiwiTlNSTUMiOiLljqbpl6jlm73otLjpm4blm6LogqHku73mnInpmZDlhazlj7giLCJOU1JTQkgiOiIxNDAzMDEyMDYxMTEwOTk1NjYiLCJQREZEWiI6IiIsIlBERlpKTCI6IiIsIlFEQloiOiIwIiwiUURYTU1DIjoiIiwiU0tSIjoi6ZmI55yfIiwiVEhESCI6IiIsIlRTQ0hCWiI6IiIsIlhIRkRIIjoiMDU5Mi01ODk4ODg4IiwiWEhGRFoiOiLljqbpl6jluILmuZbph4zljLrku5nlsrPot680Njg45Y+35Zu96LS45Lit5b+DMjgwMeWNleWFgyIsIlhIRk1DIjoi5Y6m6Zeo5Zu96LS46ZuG5Zui6IKh5Lu95pyJ6ZmQ5YWs5Y+4IiwiWEhGU0JIIjoiMTQwMzAxMjA2MTExMDk5NTY2IiwiWEhGWUgiOiI5OTk5OTk5OTk5OTkiLCJYSEZaSCI6IkE5NTU4OTg0NTY0MzQxMiIsIllGUERNIjoiIiwiWUZQSE0iOiIiLCJZV0xYIjoiIn0sIkRES1pYWCI6W3siQllaRDEiOiIiLCJCWVpEMiI6IiIsIkJZWkQzIjoiIiwiRERIIjoiNTI1NzY4MTQxMzg3IiwiRERMWCI6IjAiLCJERFFRTFNIIjoiNDc5MTU2MjYzMDMzNjMwNzIxIn1dLCJERE1YWFgiOlt7IkRKIjoiMTguODY3OTI0NTMiLCJEVyI6IlRPTiIsIkZQSFhaIjoiMCIsIkdHWEgiOiIiLCJIU0JaIjoiMCIsIkpFIjoiNTg0OS4wNiIsIktDRSI6IiIsIkxTTEJTIjoiIiwiU0UiOiIzNTAuOTQiLCJTTCI6IjAuMDYiLCJTUEJNIjoiMTA3MDQwMjAxMDAwMDAwMDAwMCIsIlNQU0wiOiIzMTAuMDAwMDAwMDAiLCJYSCI6IjEiLCJYTU1DIjoiKuWMluWtpue6pOe7tCrlj4zljZXkvY3liqjmgIExMC0xIiwiWUhaQ0JTIjoiMCIsIlpaU1RTR0wiOiIifV19XSwiWlRETSI6IjAwMDAiLCJaVFhYIjoi5Y+R56Wo5byA5YW35oiQ5Yqf77yBIn0=");
        zsinput.setEncryptcode("1");
        zsinput.setNonce("471336");
        zsinput.setSecretid("289efb7512e54146273b982456b03f42ea93");
        zsinput.setSignature("");
        zsinput.setTimestamp("1604373891146");
        zsinput.setZipcode("1");
        Zsoutput zsoutput = service.zinvoicePush(zsinput);
        System.out.println(JsonUtils.getInstance().toJsonString(zsoutput));*/


        /* JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        Client client = dcf.createClient(wsdl);
        // 需要密码的情况需要加上用户名和密码
        client.getInFaultInterceptors().add(new LoginInterceptor("rfcuser", "654321a"));
        Object[] objects = new Object[0];
        try {
            // invoke("方法名",参数1,参数2,参数3....);
            objects = client.invoke( "ZinvoicePush",
                    "test" );
            System.out.println( JsonUtils.getInstance().toJsonString( objects ) );
        }catch (Exception e){

        }*/



        /*ZWSINVOICESERVICE_Service ZWSINVOICESERVICE = new  ZWSINVOICESERVICE_Service();
        Zsinput zsinput = new Zsinput();
        zsinput.setContent("");
        zsinput.setEncryptcode("");
        zsinput.setNonce("");
        zsinput.setSecretid("");
        zsinput.setSignature("");
        zsinput.setTimestamp("");
        zsinput.setZipcode("");
        Zsoutput zsoutput = ZWSINVOICESERVICE.getZWSINVOICESERVICE().zinvoicePush(zsinput);
        System.out.println(JsonUtils.getInstance().toJsonString(zsoutput));*/



        try {
            String wsdlUrl = "http://gmdev.itg.net:8000/sap/bc/srt/rfc/sap/zws_invoice_service/210/zws_invoice_service/zws_invoice_service";
//            String wsdlUrl = "http://gmdev.itg.net:8000/sap/bc/srt/wsdl/bndg_EB005E2719EEE3F1A9A60050568EE201/wsdl11/allinone/ws_policy/document?sap-client=210";
            // 直接引用远程的wsdl文件
            // 以下都是套路
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(new URL(wsdlUrl));
            call.setUsername("rfcuser");
            call.setPassword("654321a");
//            call.setOperationName("ZinvoicePush");//WSDL里面描述的接口名称

            /*********************动态调用方法 begin**********************/
            call.setOperationName(new QName("urn:sap-com:document:sap:soap:functions:mc-style", "ZinvoicePush"));// WSDL里面描述的接口名称
            call.addParameter("Zinput",org.apache.axis.encoding.XMLType.XSD_STRING,javax.xml.rpc.ParameterMode.IN);
            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);// 设置返回类型
            String result = (String) call.invoke(new Object[] { "测试人员" });
            System.out.println("result is " + result);
            /*********************动态调用方法 end**********************/


            /*********************静态调用方法 begin**********************/

/*
            QName reqQName = new QName("urn:sap-com:document:sap:soap:functions:mc-style", "Zsinput");
            call.registerTypeMapping( Zsinput.class, reqQName, new BeanSerializerFactory( Zsinput.class, reqQName), new BeanDeserializerFactory( Zsinput.class, reqQName));
            QName respQName = new QName("urn:sap-com:document:sap:soap:functions:mc-style", "Zsoutput");
            call.registerTypeMapping(Zsoutput.class, respQName, new BeanSerializerFactory(Zsoutput.class, respQName), new BeanDeserializerFactory(Zsoutput.class, respQName));
            call.setOperationName(new QName("urn:sap-com:document:sap:soap:functions:mc-style", "tns:ZinvoicePush"));
            Zsinput zsinput = new Zsinput();
            zsinput.setContent("eyJEREZQWlhYIjpbeyJEREZQWFgiOnsiQk1CQkJIIjoiMzMuMCIsIkJZWkQxIjoiIiwiQllaRDIiOiIiLCJCWVpEMyI6IiIsIkJZWkQ0IjoiIiwiQllaRDUiOiIiLCJCWiI6IiIsIkNIWVkiOiIiLCJEREgiOiI1MjU3NjgxNDEzODciLCJERFFRTFNIIjoiNDc5MTU2MjYzMDMzNjMwNzIxIiwiRERTSiI6IjIwMjAtMTAtMjIgMTU6MjI6NDYiLCJERFpUIjoiMDAwMDAwIiwiRERaVFhYIjoi5byA56Wo5oiQ5YqfIiwiRFRNIjoiIiwiRkhSIjoiIiwiRlBETSI6IjUwMDAyMDE1MzAiLCJGUEhNIjoiODU4Mjk5OTUiLCJGUExYRE0iOiIwMDQiLCJGV00iOiIwYytCejZiNmZZSXRkQ3p2bC96Nk9EbGRxMTVWdWJGb2MvbTBReUpFU2hPTG1pdW55Q2ZUeEFmbmtDdU8xekVTcEJZR01qbWltRkxwY05PaEJNamRLcDZQL3kxN3R1TjU3Rk5NZk9tbUN3d1Nkeis1ZjlzSTZYNjdCaHdwajMzTmh4R1dHRXFSYkxCbDVBc3ROelRnQlE9PSIsIkdNRkJNIjoiIiwiR01GREgiOiIxODY1OTIwNDU1NiIsIkdNRkRaIjoi5Y6m6Zeo5biC5rmW6YeM5Yy65LuZ5bKz6LevNDY4OOWPt+Wbvei0uOS4reW/gzI4MDHljZUiLCJHTUZEWllYIjoiIiwiR01GTFgiOiIwNCIsIkdNRk1DIjoi5Y+M5Y2V5L2N5rWL6K+V5a6i5oi3IiwiR01GU0JIIjoiOTEzNTAyMDA2MTIwMzQyNDQ5IiwiR01GU0YiOiIiLCJHTUZTSkgiOiIiLCJHTUZZSCI6Ijk5OTk5OTk5IiwiR01GWkgiOiIxMjM0NTY3ODkwIiwiSEpKRSI6IjU4NDkuMDYiLCJISlNFIjoiMzUwLjk0IiwiSlFCSCI6IjY2MTYwMDA4MTk3MyIsIkpTSEoiOiI2MjAwLjAwIiwiSllNIjoiNzI3NjE1MjM3MjM1MjAyMTcyNTEiLCJLUEZTIjoiMSIsIktQTFgiOiIwIiwiS1BSIjoi5byA56WoIiwiS1BSUSI6IjIwMjAtMTEtMDIgMTU6NTI6MDciLCJLUFpEIjoiIiwiTlNSTUMiOiLljqbpl6jlm73otLjpm4blm6LogqHku73mnInpmZDlhazlj7giLCJOU1JTQkgiOiIxNDAzMDEyMDYxMTEwOTk1NjYiLCJQREZEWiI6IiIsIlBERlpKTCI6IiIsIlFEQloiOiIwIiwiUURYTU1DIjoiIiwiU0tSIjoi6ZmI55yfIiwiVEhESCI6IiIsIlRTQ0hCWiI6IiIsIlhIRkRIIjoiMDU5Mi01ODk4ODg4IiwiWEhGRFoiOiLljqbpl6jluILmuZbph4zljLrku5nlsrPot680Njg45Y+35Zu96LS45Lit5b+DMjgwMeWNleWFgyIsIlhIRk1DIjoi5Y6m6Zeo5Zu96LS46ZuG5Zui6IKh5Lu95pyJ6ZmQ5YWs5Y+4IiwiWEhGU0JIIjoiMTQwMzAxMjA2MTExMDk5NTY2IiwiWEhGWUgiOiI5OTk5OTk5OTk5OTkiLCJYSEZaSCI6IkE5NTU4OTg0NTY0MzQxMiIsIllGUERNIjoiIiwiWUZQSE0iOiIiLCJZV0xYIjoiIn0sIkRES1pYWCI6W3siQllaRDEiOiIiLCJCWVpEMiI6IiIsIkJZWkQzIjoiIiwiRERIIjoiNTI1NzY4MTQxMzg3IiwiRERMWCI6IjAiLCJERFFRTFNIIjoiNDc5MTU2MjYzMDMzNjMwNzIxIn1dLCJERE1YWFgiOlt7IkRKIjoiMTguODY3OTI0NTMiLCJEVyI6IlRPTiIsIkZQSFhaIjoiMCIsIkdHWEgiOiIiLCJIU0JaIjoiMCIsIkpFIjoiNTg0OS4wNiIsIktDRSI6IiIsIkxTTEJTIjoiIiwiU0UiOiIzNTAuOTQiLCJTTCI6IjAuMDYiLCJTUEJNIjoiMTA3MDQwMjAxMDAwMDAwMDAwMCIsIlNQU0wiOiIzMTAuMDAwMDAwMDAiLCJYSCI6IjEiLCJYTU1DIjoiKuWMluWtpue6pOe7tCrlj4zljZXkvY3liqjmgIExMC0xIiwiWUhaQ0JTIjoiMCIsIlpaU1RTR0wiOiIifV19XSwiWlRETSI6IjAwMDAiLCJaVFhYIjoi5Y+R56Wo5byA5YW35oiQ5Yqf77yBIn0=");
            zsinput.setEncryptcode("1");
            zsinput.setNonce("471336");
            zsinput.setSecretid("289efb7512e54146273b982456b03f42ea93");
            zsinput.setSignature("");
            zsinput.setTimestamp("1604373891146");
            zsinput.setZipcode("1");
            Zsoutput output = (Zsoutput) call.invoke(new Object[]{zsinput});
            // 给方法传递参数，并且调用方法
            System.out.println("result is " + JSON.toJSONString(output));*/
            /*********************静态调用方法 end**********************/
        } catch (Exception e) {
            System.err.println(e.toString());
        }






    }

}
