//package com.dxhy.order.service;
//
//import com.alibaba.fastjson.JSON;
//import com.dxhy.order.ServiceStarter;
//import com.dxhy.order.api.*;
//import com.dxhy.order.constant.OrderInfoEnum;
//import com.dxhy.order.constant.OrderSplitConfig;
//import com.dxhy.order.constant.OrderSplitException;
//import com.dxhy.order.dao.*;
//import com.dxhy.order.model.*;
//import com.dxhy.order.service.impl.OrderRollbackServiceImpl;
//import com.dxhy.order.utils.DistributedKeyMaker;
//import com.dxhy.order.utils.JsonUtils;
//import com.dxhy.order.utils.OrderSplitUtil;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//
//import javax.annotation.Resource;
//import java.text.DecimalFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = ServiceStarter.class)
//@WebAppConfiguration
//public class OrderInfoTests {
//
//
//    @Resource
//    private OrderInfoMapper orderInfoMapper;
//
//    @Resource
//    OrderProcessInfoMapper orderProcessInfoMapper;
//
//    @Resource
//    OrderInvoiceInfoMapper orderInvoiceInfoMapper;
//
//    @Resource
//    OrderItemInfoMapper orderItemInfoMapper;
//
//    @Resource
//    InvalidInvoiceInfoMapper invalidInvoiceInfoMapper;
//
//    @Resource
//    OrderProcessInfoExtMapper orderProcessInfoExtMapper;
//
//    @Resource
//    OrderRollbackServiceImpl orderRollbackServiceImpl;
//
//    @Resource
//    InvoiceBatchRequestItemMapper invoiceBatchRequestItemMapper;
//
//    @Resource
//    private AuthenticationInfoMapper authenticationInfoMapper;
//
//    @Resource
//    TaxClassCodeDao taxClassCodeDao;
//    @Resource
//    private ValidateOrderInfo validateOrderInfo;
//
//    @Resource
//    private InvoiceDataService invoiceDateService;
//
//    @Resource
//    private ApiPushService pushService;
//
//    @Autowired
//    private ApiPushService apiPushService;
//    @Autowired
//    private ApiOrderInfoService apiOrderInfoService;
//    @Autowired
//    private ApiOrderItemInfoService apiOrderItemInfoService;
//    @Autowired
//    private ApiOrderInvoiceInfoService apiOrderInvoiceInfoService;
//    @Autowired
//    private ApiInvoiceCommonService apiInvoiceCommonService;
//
//    @Autowired
//    private CommodityDao CommodityDao;
//
//    @Resource
//    private ApiOrderProcessService orderProcessService;
//
//    @Resource
//    private ApiRushRedInvoiceRequestInfoService rushRedInvoiceRequestInfoService;
//
//    @Resource
//    private ICommonDisposeService iCommonDisposeService;
//    @Autowired
//    private InvoiceBatchRequestMapper invoiceBatchRequestMapper;
//    @Autowired
//    ApiTaxEquipmentService apiTaxEquipmentService;
//    @Autowired
//    BuyerDao buyerDao;
//    @Autowired
//    InvoiceDao invoiceDao;
//
//    @Autowired
//    UnifyService unifyService;
//
//    private static final DecimalFormat decimalFormat = new DecimalFormat("0000000");
//
//
//    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//    @Test
//    public void testBuyer() {
//    	/*Map map = new HashMap<>(5);
//    	map.put("taxpayerCode", "000000");
//    	//taxClassCodeDao.selectTaxClassCode(new HashMap<>(5));
//    	map.put("xhfNsrsbh", "bbbbbbb");
//    	List<BuyerEntity> selectBuyerList = buyerDao.selectBuyerList(map);*/
//        /*DrawerInfoEntity dra = new DrawerInfoEntity();
//        dra.setId("12342343");
//        dra.setModifyTime(new Date());
//        dra.setCreateTime(new Date());
//    	invoiceDao.insertDrawer(dra);*/
//        Map<String, Object> map = new HashMap<String, Object>(5);
//        map.put("parameter", "aaaaaa");
//        taxClassCodeDao.selectTaxClassCode(map);
//    }
//
//
//    @Test
//    public void testSplitSplit() throws OrderSplitException {
//    	OrderInfo selectOrderInfoByOrderId = apiOrderInfoService.selectOrderInfoByOrderId("374150793513881600");
//    	List<OrderItemInfo> selectOrderItemInfoByOrderId = apiOrderItemInfoService.selectOrderItemInfoByOrderId("374150793513881600");
//    	CommonOrderInfo common = new CommonOrderInfo();
//    	common.setOrderInfo(selectOrderInfoByOrderId);
//    	common.setOrderItemInfo(selectOrderItemInfoByOrderId);
//    	OrderSplitConfig config = new OrderSplitConfig();
//    	config.setLimitJe(99999.99);
//    	config.setSplitType("1");
//    	config.setSplitRule("1");
//    	OrderSplitUtil.orderSplit(common, config);
//    }
//
//
//
//
//
//
//    /*@Test
//    public void tax() {
//        String text = "{\r\n" +
//                "    \"flagbs\": \"0\", \r\n" +
//                "    \"hzfpxxbbh\": \"5001021812001135\", \r\n" +
//                "    \"orderInfo\": {\r\n" +
//                "        \"bbmBbh\": \"1.0\", \r\n" +
//                "        \"byzd1\": \"\", \r\n" +
//                "        \"byzd2\": \"\", \r\n" +
//                "        \"byzd3\": \"\", \r\n" +
//                "        \"byzd4\": \"\", \r\n" +
//                "        \"byzd5\": \"\", \r\n" +
//                "        \"bz\": \"\", \r\n" +
//                "        \"chyy\": \"已抵扣\", \r\n" +
//                "        \"czdm\": \"0\", \r\n" +
//                "        \"ddh\": \"\", \r\n" +
//                "        \"ddlx\": \"3\", \r\n" +
//                "        \"dkbz\": \"0\", \r\n" +
//                "        \"dsptbm\": \"\", \r\n" +
//                "        \"fhr\": \"\", \r\n" +
//                "        \"fpqqlsh\": \"\", \r\n" +
//                "        \"fpzlDm\": \"0\", \r\n" +
//                "        \"ghfDh\": \"\", \r\n" +
//                "        \"ghfDz\": \"\", \r\n" +
//                "        \"ghfEmail\": \"\", \r\n" +
//                "        \"ghfMc\": \"研发部\", \r\n" +
//                "        \"ghfNsrsbh\": \"150001205110278555\", \r\n" +
//                "        \"ghfQylx\": \"01\", \r\n" +
//                "        \"ghfSf\": \"\", \r\n" +
//                "        \"ghfSj\": \"\", \r\n" +
//                "        \"ghfYh\": \"\", \r\n" +
//                "        \"ghfZh\": \"\", \r\n" +
//                "        \"hjbhsje\": \"-3500.00\", \r\n" +
//                "        \"hjse\": \"-350.00\", \r\n" +
//                "        \"hyDm\": \"\", \r\n" +
//                "        \"hyMc\": \"\", \r\n" +
//                "        \"id\": \"\", \r\n" +
//                "        \"kphjje\": \"-3850.00\", \r\n" +
//                "        \"kpjh\": \"0\", \r\n" +
//                "        \"kplx\": \"1\", \r\n" +
//                "        \"kpr\": \"xing\", \r\n" +
//                "        \"kpxm\": \"详见对应正数发票及清单\", \r\n" +
//                "        \"mdh\": \"\", \r\n" +
//                "        \"nsrdzdah\": \"\", \r\n" +
//                "        \"nsrmc\": \"150001196104213403\", \r\n" +
//                "        \"nsrsbh\": \"150001196104213403\", \r\n" +
//                "        \"processId\": \"\", \r\n" +
//                "        \"pydm\": \"\", \r\n" +
//                "        \"qdBz\": \"0\", \r\n" +
//                "        \"qdXmmc\": \"\", \r\n" +
//                "        \"skr\": \"\", \r\n" +
//                "        \"sld\": \"132\", \r\n" +
//                "        \"sldMc\": \"专票开票点403\", \r\n" +
//                "        \"status\": \"\", \r\n" +
//                "        \"swjgDm\": \"\", \r\n" +
//                "        \"sykchje\": \"\", \r\n" +
//                "        \"thdh\": \"\", \r\n" +
//                "        \"tschbz\": \"1\", \r\n" +
//                "        \"xhfDh\": \"13123456123\", \r\n" +
//                "        \"xhfDz\": \"403销方地址\", \r\n" +
//                "        \"xhfMc\": \"403\", \r\n" +
//                "        \"xhfNsrsbh\": \"150001196104213403\", \r\n" +
//                "        \"xhfYh\": \"403销方银行\", \r\n" +
//                "        \"xhfZh\": \"6123456789111111111\", \r\n" +
//                "        \"yfpDm\": \"\", \r\n" +
//                "        \"yfpHm\": \"\", \r\n" +
//                "        \"ywlx\": \"\"\r\n" +
//                "    }, \r\n" +
//                "    \"orderItemInfo\": [\r\n" +
//                "        {\r\n" +
//                "            \"byzd1\": \"\", \r\n" +
//                "            \"byzd2\": \"\", \r\n" +
//                "            \"byzd3\": \"\", \r\n" +
//                "            \"byzd4\": \"\", \r\n" +
//                "            \"byzd5\": \"\", \r\n" +
//                "            \"fphxz\": \"6\", \r\n" +
//                "            \"ggxh\": \"\", \r\n" +
//                "            \"hsbz\": \"0\", \r\n" +
//                "            \"id\": \"\", \r\n" +
//                "            \"kce\": \"\", \r\n" +
//                "            \"lslbs\": \"\", \r\n" +
//                "            \"orderInfoId\": \"\", \r\n" +
//                "            \"se\": \"-350.00\", \r\n" +
//                "            \"sl\": \"0.10\", \r\n" +
//                "            \"spbm\": \"\", \r\n" +
//                "            \"sphxh\": \"1\", \r\n" +
//                "            \"wcje\": \"\", \r\n" +
//                "            \"xmdj\": \"\", \r\n" +
//                "            \"xmdw\": \"\", \r\n" +
//                "            \"xmje\": \"-3500.00\", \r\n" +
//                "            \"xmmc\": \"详见对应正数发票及清单\", \r\n" +
//                "            \"xmsl\": \"\", \r\n" +
//                "            \"yhzcbs\": \"0\", \r\n" +
//                "            \"zxbm\": \"\", \r\n" +
//                "            \"zzstsgl\": \"\"\r\n" +
//                "        }\r\n" +
//                "    ]\r\n" +
//                "}";
//        CommonOrderInfo commonOrderInfo = JsonUtils.getInstance().parseObject(text, CommonOrderInfo.class);
//        CommonOrderInfo taxSeparationService = priceTaxSeparationService.taxSeparationService(commonOrderInfo);
//        System.out.println(JsonUtils.getInstance().toJsonString(taxSeparationService));
//    }*/
//
//    @Test
//    public void allInvoiceCount() {
//        OrderInfo orderInfo = new OrderInfo();
//        orderInfo.setId("cyf123");
//        orderInfo.setProcessId("cyf1234");
//        orderInfo.setFpqqlsh("cyf12345");
//        orderInfo.setDdh("cyf123456");
//        orderInfo.setDdlx("0");
//        orderInfo.setDdrq(new Date());
//        orderInfo.setKplx("0");
//        orderInfo.setFpzlDm("51");
//        orderInfo.setKphjje("9999");
//        orderInfo.setCreateTime(new Date());
//        orderInfo.setUpdateTime(new Date());
//        orderInfoMapper.insert(orderInfo);
//    }
//
//    @Test
//    public void selectInvoiceByOrderTest() throws ParseException {
//
//        OrderInvoiceInfo orderInvoiceInfo = new OrderInvoiceInfo();
//
//        orderInvoiceInfo.setId("");
//        orderInvoiceInfo.setDyzt("");
//
//        InvoiceBatchRequest invoiceBatchRequest = new InvoiceBatchRequest();
//        invoiceBatchRequest.setFpqqpch("2018111500000000000000000000000000000737");
//
//        String text = "{\"orderInfo\":{\"bbmBbh\":\"1.0\",\"bz\":\"\",\"createTime\":\"2019-07-17 20:13:09\",\"ddh\":\"16700810334981683445\",\"ddlx\":\"0\",\"ddrq\":\"2019-07-17 20:13:09\",\"dkbz\":\"0\",\"fhr\":\"\",\"fpqqlsh\":\"order156336558991300\",\"fpzlDm\":\"2\",\"ghfDh\":\"123123123\",\"ghfDz\":\"撒旦法\",\"ghfEmail\":\"\",\"ghfMc\":\"张双超\",\"ghfNsrsbh\":\"150001194112132161\",\"ghfQylx\":\"01\",\"ghfSj\":\"\",\"ghfYh\":\" 阿道夫\",\"ghfZh\":\"123123\",\"hjbhsje\":\"52.17\",\"hjse\":\"2.83\",\"kphjje\":\"55.00\",\"kpjh\":\"0\",\"kplx\":\"0\",\"kpr\":\"zsc\",\"kpxm\":\"10\",\"nsrmc\":\"销项测试子公司C48\",\"nsrsbh\":\"150001194112132161\",\"qdBz\":\"0\",\"skr\":\"\",\"sld\":\"120\",\"tschbz\":\"0\",\"updateTime\":\"2019-07-17 20:13:09\",\"xhfDh\":\"010-81234567\",\"xhfDz\":\"销方地址010-81234567\",\"xhfMc\":\"销项测试子公司C48\",\"xhfNsrsbh\":\"150001194112132161\",\"xhfYh\":\"销方银行6123456789\",\"xhfZh\":\"6123456789\"},\"orderItemInfo\":[{\"fphxz\":\"0\",\"ggxh\":\"\",\"hsbz\":\"0\",\"kce\":\"5.00\",\"lslbs\":\"\",\"se\":\"2.83\",\"sl\":\"0.06\",\"spbm\":\"1010101010000000000\",\"sphxh\":\"1\",\"xmdj\":\"\",\"xmdw\":\"\",\"xmje\":\"52.17\",\"xmmc\":\"*谷物*10\",\"xmsl\":\"\",\"yhzcbs\":\"0\",\"zzstsgl\":\"\"}]}";
//        CommonOrderInfo commonOrderInfo = JsonUtils.getInstance().parseObject(text, CommonOrderInfo.class);
//        Map checkOrderInvoice = validateOrderInfo.checkOrderInvoice(commonOrderInfo);
//        System.out.println(checkOrderInvoice);
//    }
//
//
//    @Test
//    public void insertTestOrderInvoiceInfo() {
//        OrderInfo selectByPrimaryKey = orderInfoMapper.selectOrderInfoByOrderId("201901041042411081018048825131008");
//        List<OrderItemInfo> selectByOrderId = orderItemInfoMapper.selectOrderItemInfoByOrderId("201908081924061027153473709408224");
//        OrderProcessInfo orderProcessInfo = new OrderProcessInfo();
//        orderProcessInfo.setId("201908081924061027153473709408224");
//        OrderProcessInfo selectByOrderId2 = orderProcessInfoMapper.selectProcessInfoById(orderProcessInfo);
//
//        for (int i = 0; i < 50; i++) {
//            selectByPrimaryKey.setId(DistributedKeyMaker.generateShotKey());
//            selectByPrimaryKey.setFpzlDm("2");
//            selectByPrimaryKey.setFpqqlsh(UUID.randomUUID().toString());
//            selectByPrimaryKey.setDdh(DistributedKeyMaker.generateShotKey());
//            for (OrderItemInfo orderItemInfo : selectByOrderId) {
//                orderItemInfo.setOrderInfoId(selectByPrimaryKey.getId());
//                orderItemInfo.setId(DistributedKeyMaker.generateShotKey());
//                orderItemInfoMapper.insertOrderItemInfo(orderItemInfo);
//            }
//            selectByOrderId2.setDdzt("5");
//            selectByOrderId2.setOrderInfoId(selectByPrimaryKey.getId());
//            selectByOrderId2.setId(DistributedKeyMaker.generateShotKey());
//            selectByPrimaryKey.setProcessId(selectByOrderId2.getId());
//            orderProcessInfoMapper.insert(selectByOrderId2);
//            OrderInvoiceInfo orderInvoiceInfo = convertToOrderInvoiceInfo(selectByPrimaryKey);
//            orderInvoiceInfoMapper.insert(orderInvoiceInfo);
//            orderInfoMapper.insert(selectByPrimaryKey);
//        }
//
//
//    }
//
//    /**
//     *
//     * @param orderInfo
//     * @return
//     */
//    private OrderInvoiceInfo convertToOrderInvoiceInfo(OrderInfo orderInfo) {
//        OrderInvoiceInfo orderInvoiceInfo = new OrderInvoiceInfo();
//        orderInvoiceInfo.setId(DistributedKeyMaker.generateShotKey());
//        orderInvoiceInfo.setOrderInfoId(orderInfo.getId());
//        orderInvoiceInfo.setOrderProcessInfoId(orderInfo.getProcessId());
//        orderInvoiceInfo.setFpqqlsh(orderInfo.getFpqqlsh());
//        orderInvoiceInfo.setKplsh(UUID.randomUUID().toString());
//        orderInvoiceInfo.setDdh(orderInfo.getDdh());
//        orderInvoiceInfo.setMdh(orderInfo.getMdh());
//        orderInvoiceInfo.setGhfMc(orderInfo.getGhfMc());
//        orderInvoiceInfo.setGhfSj(orderInfo.getGhfSj());
//        orderInvoiceInfo.setKphjje(orderInfo.getKphjje());
//        orderInvoiceInfo.setHjbhsje(orderInfo.getHjbhsje());
//        orderInvoiceInfo.setKpse(orderInfo.getHjse());
//        orderInvoiceInfo.setKplx(orderInfo.getKplx());
//        orderInvoiceInfo.setKpzt("2");
//        orderInvoiceInfo.setKprq(new Date());
//        orderInvoiceInfo.setFpdm(decimalFormat.format(Math.random() * 10));
//        orderInvoiceInfo.setFphm(decimalFormat.format(Math.random() * 10));
//        orderInvoiceInfo.setFpzlDm(orderInfo.getFpzlDm());
//        orderInvoiceInfo.setKpr(orderInfo.getKpr());
//        orderInvoiceInfo.setCreateTime(new Date());
//        orderInvoiceInfo.setUpdateTime(new Date());
//        return orderInvoiceInfo;
//    }
//
//    @Test
//    public void testBatchInvalid() {
//
//		/*List<CommonOrderInfo> selectAllFromOrderInfo = orderInfoMapper.selectAllFromOrderInfo("3");
//		System.out.println("+++++++++++++++"+selectAllFromOrderInfo);*/
//        List<InvalidInvoiceInfo> orderInvoiceList = new ArrayList<InvalidInvoiceInfo>();
//        InvalidInvoiceInfo invalidInvoiceInfo = new InvalidInvoiceInfo();
//        invalidInvoiceInfo.setFpdm("134444");
//        invalidInvoiceInfo.setFphm("234324");
//        invalidInvoiceInfo.setFplx("0");
//        invalidInvoiceInfo.setId(DistributedKeyMaker.generateShotKey());
//        invalidInvoiceInfo.setSld("9");
//        invalidInvoiceInfo.setUpdateTime(new Date());
//        invalidInvoiceInfo.setCreateTime(new Date());
//        invalidInvoiceInfo.setZfBz("0");
//        invalidInvoiceInfo.setZfsj(new Date());
//        orderInvoiceList.add(invalidInvoiceInfo);
//        InvalidInvoiceInfo invalidInvoiceInfo1 = new InvalidInvoiceInfo();
//        invalidInvoiceInfo1.setFpdm("134444");
//        invalidInvoiceInfo1.setFphm("234324");
//        invalidInvoiceInfo1.setFplx("0");
//        invalidInvoiceInfo1.setId(DistributedKeyMaker.generateShotKey());
//        invalidInvoiceInfo1.setSld("9");
//        invalidInvoiceInfo1.setUpdateTime(new Date());
//        invalidInvoiceInfo1.setCreateTime(new Date());
//        invalidInvoiceInfo1.setZfBz("0");
//        invalidInvoiceInfo1.setZfsj(new Date());
//        orderInvoiceList.add(invalidInvoiceInfo);
//        orderInvoiceList.add(invalidInvoiceInfo1);
//        for (InvalidInvoiceInfo invalidInvoiceInfo2 : orderInvoiceList) {
//            invalidInvoiceInfoMapper.insert(invalidInvoiceInfo2);
//        }
//
//    }
//
//
//    @Test
//    public void validTest() {
//    	/*OrderInvoiceInfo orderInvocieInfo = new OrderInvoiceInfo();
//    	orderInvocieInfo.setKplsh("8cefbcbe9ade42dd8105b91f92d452f4001");
//        System.out.println("+++++++++++++++++" + orderInvoiceInfoMapper.selectByInvoiceInfo(orderInvocieInfo));*/
//
//    }
//
//    @Test
//    public void test3() {
//        List<OrderInfo> list = new ArrayList<OrderInfo>();
//        OrderInfo orderInfo = new OrderInfo();
//        orderInfo.setId("201809201807491042716956693757952");
//        list.add(orderInfo);
//
//    }
//
//    @Test
//    public void test4() {
//        List<InvoiceBatchRequestItem> list = new ArrayList<InvoiceBatchRequestItem>();
//        InvoiceBatchRequestItem item = new InvoiceBatchRequestItem();
//        item.setCreateTime(new Date());
//        item.setFpqqlsh("231432142134");
//        item.setFpqqpch("fdsjfsdlfjd");
//        item.setId("234123421432");
//        item.setInvoiceBatchId("adsfasdfsdf");
//        item.setKplsh("3251234243");
//        item.setMessage("fadfsdf");
//        item.setStatus("0");
//        item.setUpdateTime(new Date());
//        list.add(item);
//
//
//    }
//
//    @Test
//    public void test5() {
//        Map map = new HashMap(5);
//        Map queryCountByMap = orderInvoiceInfoMapper.queryCountByMap(map);
//        System.out.println("++++++++++++++" + JsonUtils.getInstance().toJsonString(queryCountByMap));
//    }
//
//    @Test
//    public void test6() {
//        String[] orderIdArrays = new String[]{"b9c3e338052f4179b2ca9d4df34a8201", "f4da01c4ad7f42f9a98e916ec8d1780c"};
//        List<OrderInvoiceInfo> list = new ArrayList<>();
//        for (String string : orderIdArrays) {
//            OrderInvoiceInfo orderInvocieInfo = new OrderInvoiceInfo();
//            orderInvocieInfo.setOrderInfoId(string);
//        	/*OrderInvoiceInfo selectByInvoiceInfo = orderInvoiceInfoMapper.selectByInvoiceInfo(orderInvocieInfo);
//        	list.add(selectByInvoiceInfo);*/
//        }
//        /*int updateByList = orderInvoiceInfoMapper.updateByList(list);
//        System.out.println("++++++++++++++" + updateByList);*/
//    }
//
//    @Test
//    public void auth() {
//        List<AuthenticationInfo> authenticateAction = authenticationInfoMapper.selectAuthticationAll("0");
//        System.out.println(authenticateAction);
//    }
//    /**
//     * 放入队列前测试推送
//     */
//    @Test
//    public void testPush() {
//
//        String string = "{\"DDH\":\"85840421231085986621\",\"EWM\":\"Qk3CAwAAAAAAAD4AAAAoAAAASwAAAEsAAAABAAEAAAAAAIQDAAAAAAAAAAAAAAAAAAACAAAAAAAA///////////////////gAAAAAzMwAM/zw8AgAAAAAzMwAM/zw8AgAAA/88D/z8//z8DgAAA/88D/z8//z8DgAAAwM8AzDwAPM8/gAAAwM8AzDwAPM8/gAAAwM8/zDDwM8/8gAAAwM8/zDDwM8/8gAAAwMzMP8PMzADMgAAAwMzMP8PMzADMgAAA/8/P8A/DwPwDgAAA/8/P8A/DwPwDgAAAAAwP8z/z/MwDgAAAAAwP8z/z/MwDgAAD//zMA/wA8Pz/gAAD//zMA/wA8Pz/gAAD88wwDAwzwAAAgAAD88wwDAwzwAAAgAAADDDD8AAA8D8DgAAADDDD8AAA8D8DgAAAAAM/wzMzwzMzgAAAAAM/wzMzwzMzgAADwP8/P8MA8zAzgAADwP8/P8MA8zAzgAADzAMMPPz/zM8DgAADzAMMPPz/zM8DgAAD8P8PPPAzwA8DgAAD8P8PPPAzwA8DgAAA/M8PAM8A8/PDgAAA/M8PAM8A8/PDgAAD8PzwMzzwAzDzgAAD8PzwMzzwAzDzgAAADwz/8AM/zM8MgAAADwz/8AM/zM8MgAAAPPMw/z8DwDMDgAAAPPMw/z8DwDMDgAADwMzD/DwD8z/DgAADwMzD/DwD8z/DgAAAwDPzPPAwMzDDgAAAwDPzPPAwMzDDgAAAPAPPz8/DzM8AgAAAPAPPz8/DzM8AgAADAPDDDA/DwD8MgAADAPDDDA/DwD8MgAADzMz/ADzzMzMDgAADzMz/ADzzMzMDgAADPDwA8/wAMzADgAADPDwA8/wAMzADgAAAw8M//A8/wM8MgAAAw8M//A8/wM8MgAAA/DwAwDAz8PMPgAAA/DwAwDAz8PMPgAADwADPA/MA8DAzgAADwADPA/MA8DAzgAAA/Dw8D8PwMzA/gAAA/Dw8D8PwMzA/gAAA/MAMPPD/zAA8gAAA/MAMPPD/zAA8gAAD//zMD/8zz///gAAD//zMD/8zz///gAAAAAzMzMzMzMAAgAAAAAzMzMzMzMAAgAAA/8w/8DP//M/8gAAA/8w/8DP//M/8gAAAwMwPAzzAM8wMgAAAwMwPAzzAM8wMgAAAwMzzAP/MM8wMgAAAwMzzAP/MM8wMgAAAwM8PwD8zzMwMgAAAwM8PwD8zzMwMgAAA/8/zzPP/DM/8gAAA/8/zzPP/DM/8gAAAAAz/DzDAM8AAgAAAAAz/DzDAM8AAgAAA=\",\"FPLB\":\"51\",\"FPLX\":\"0\",\"FPQQLSH\":\"b1a449d9cbb34509b52e56787d9102ed001\",\"FPQQPCH\":\"b1a449d9cbb34509b52e56787d9102ed\",\"FP_DM\":\"111005117101\",\"FP_HM\":\"20181118\",\"FWM\":\"46>1/71*7*24/5*<*//44<5<9/776-<*15>235</8090+067+<8+63/*/+02/2/5</3536>188*/*40*6<8/-749*853>225+>-080200<5<\",\"HJBHSJE\":10.91,\"JQBH\":\"661616316327\",\"JYM\":\"76962147061918373193\",\"KPHJSE\":1.09,\"KPRQ\":\"2018-12-20 11:04:45\",\"PDF_URL\":\"5c1b06cd14c9e013acf57891\",\"STATUSCODE\":\"2000\",\"STATUSMSG\":\"发票生成PDF成功\"}";
//        InvoicePush parseObject = JsonUtils.getInstance().parseObject(string, InvoicePush.class);
//        R receiveInvoice = invoiceDateService.receiveInvoice(parseObject);
//
//
//    }
//
//    @Test
//    public void testDsts() {
//        Map paramMap = new HashMap(5);
//        paramMap.put("fpzlDm", 51);
//        paramMap.put("startTime", "2018-12-01");
//        paramMap.put("endTime", "2018-12-10");
//        paramMap.put("zfbz", "0");
//        paramMap.put("xhfNsrsbh", "911101082018050516");
//        orderInvoiceInfoMapper.selectInvoiceByOrder(paramMap);
//    }
//
//    /**
//     * 放入队列后测试推送给企业
//     * 此测试可获取PDF字节流
//     */
//    @Test
//    public void testSplit() {
//        String _invoiceInfo = "{\"chBz\":\"0\",\"ddh\":\"51821521536947190447\",\"ewm\":\"Qk3CAwAAAAAAAD4AAAAoAAAASwAAAEsAAAABAAEAAAAAAIQDAAAAAAAAAAAAAAAAAAACAAAAAAAA///////////////////gAAAAAz/PA8/z88AgAAAAAz/PA8/z88AgAAA/8/8MAA8/w8DgAAA/8/8MAA8/w8DgAAAwM/MAzMDPM8/gAAAwM/MAzMDPM8/gAAAwM/DD8PAM888gAAAwM/DD8PAM888gAAAwMwA8PD/zADMgAAAwMwA8PD/zADMgAAA/8/P8P8z8PwPgAAA/8/P8P8z8PwPgAAAAAwP8MwDDMwDgAAAAAwP8MwDDMwDgAAD//wwwzzAMPzzgAAD//wwwzzAMPzzgAADw8D8MAM/zAAAgAADw8D8MAM/zAAAgAAAA/wA/z8M/DwPgAAAA/wA/z8M/DwPgAAAMAzPzDwzwzPzgAAAMAzPzDwzwzPzgAADw/zDzPAw8zDzgAADw/zDzPAw8zDzgAADwwM//8PMzM8AgAADwwM//8PMzM8AgAADwPPDAA/DwA8PgAADwPPDAA/DwA8PgAADMM/8AzzwMzPDgAADMM/8AzzwMzPDgAADP/M/M/wwAzD/gAADP/M/M/wwAzD/gAAAD88/PAwzzM8MgAAAD88/PAwzzM8MgAAAzPz/wDAwzz8DgAAAzPz/wDAwzz8DgAADAAP8M/PD8DwDgAADAAP8M/PD8DwDgAAAMPwz/8PwMzDDgAAAMPwz/8PwMzDDgAAD/AD/zPzzzM8DgAAD/AD/zPzzzM8DgAAAzPz/zPAD8A8DgAAAzPz/zPAD8A8DgAAADM8/MM/DwzMDgAAADM8/MM/DwzMDgAADzzw8wzzAMzADgAADzzw8wzzAMzADgAAADwP88AM/zM8MgAAADwP88AM/zM8MgAAADPPP/z8//PMPgAAADPPP/z8//PMPgAAD88A8DAzw8z/zgAAD88A8DAzw8z/zgAADD/AAPDDwMzA/gAADD/AAPDDwMzA/gAAA/MAPz8/PzAA8gAAA/MAPz8/PzAA8gAAD//wwPA8Dz///gAAD//wwPA8Dz///gAAAAAzMzMzMzMAAgAAAAAzMzMzMzMAAgAAA/8wwD8APzM/8gAAA/8wwD8APzM/8gAAAwMwDAMzAM8wMgAAAwMwDAMzAM8wMgAAAwMzM/PDAM8wMgAAAwMzM/PDAM8wMgAAAwM/8ADAPzMwMgAAAwM/8ADAPzMwMgAAA/8/Azwz8D8/8gAAA/8/Azwz8D8/8gAAAAAzD/MPAM8AAgAAAAAzD/MPAM8AAgAAA=\",\"fpdm\":\"111005117101\",\"fphm\":\"20181073\",\"fwm\":\"1><94-942--9+18260<>/30+*4/+<+0-1*+<1<*04/><891>5+7/1+//<85-*+496562+-85*460>>913+/41+*82>5++<0<827/<<49762+\",\"hjbhsje\":\"111.82\",\"id\":\"c662dbc481384535904851968313aefc\",\"jqbh\":\"661616316327\",\"jym\":\"78369242283632343666\",\"kplsh\":\"2f33e977a0f34192b87ab35a87051cb7001\",\"kprq\":\"2018-12-15 11:01:40\",\"kpse\":\"11.18\",\"kpzt\":\"2\",\"orderInfoId\":\"201812151101391073775062458433537\",\"pdfUrl\":\"5c146e9414c9e013acf57871\",\"updateTime\":\"2018-12-15 11:01:41\"}";
//        String invoicePush = "{\"DDH\":\"51821521536947190447\",\"EWM\":\"Qk3CAwAAAAAAAD4AAAAoAAAASwAAAEsAAAABAAEAAAAAAIQDAAAAAAAAAAAAAAAAAAACAAAAAAAA///////////////////gAAAAAz/PA8/z88AgAAAAAz/PA8/z88AgAAA/8/8MAA8/w8DgAAA/8/8MAA8/w8DgAAAwM/MAzMDPM8/gAAAwM/MAzMDPM8/gAAAwM/DD8PAM888gAAAwM/DD8PAM888gAAAwMwA8PD/zADMgAAAwMwA8PD/zADMgAAA/8/P8P8z8PwPgAAA/8/P8P8z8PwPgAAAAAwP8MwDDMwDgAAAAAwP8MwDDMwDgAAD//wwwzzAMPzzgAAD//wwwzzAMPzzgAADw8D8MAM/zAAAgAADw8D8MAM/zAAAgAAAA/wA/z8M/DwPgAAAA/wA/z8M/DwPgAAAMAzPzDwzwzPzgAAAMAzPzDwzwzPzgAADw/zDzPAw8zDzgAADw/zDzPAw8zDzgAADwwM//8PMzM8AgAADwwM//8PMzM8AgAADwPPDAA/DwA8PgAADwPPDAA/DwA8PgAADMM/8AzzwMzPDgAADMM/8AzzwMzPDgAADP/M/M/wwAzD/gAADP/M/M/wwAzD/gAAAD88/PAwzzM8MgAAAD88/PAwzzM8MgAAAzPz/wDAwzz8DgAAAzPz/wDAwzz8DgAADAAP8M/PD8DwDgAADAAP8M/PD8DwDgAAAMPwz/8PwMzDDgAAAMPwz/8PwMzDDgAAD/AD/zPzzzM8DgAAD/AD/zPzzzM8DgAAAzPz/zPAD8A8DgAAAzPz/zPAD8A8DgAAADM8/MM/DwzMDgAAADM8/MM/DwzMDgAADzzw8wzzAMzADgAADzzw8wzzAMzADgAAADwP88AM/zM8MgAAADwP88AM/zM8MgAAADPPP/z8//PMPgAAADPPP/z8//PMPgAAD88A8DAzw8z/zgAAD88A8DAzw8z/zgAADD/AAPDDwMzA/gAADD/AAPDDwMzA/gAAA/MAPz8/PzAA8gAAA/MAPz8/PzAA8gAAD//wwPA8Dz///gAAD//wwPA8Dz///gAAAAAzMzMzMzMAAgAAAAAzMzMzMzMAAgAAA/8wwD8APzM/8gAAA/8wwD8APzM/8gAAAwMwDAMzAM8wMgAAAwMwDAMzAM8wMgAAAwMzM/PDAM8wMgAAAwMzM/PDAM8wMgAAAwM/8ADAPzMwMgAAAwM/8ADAPzMwMgAAA/8/Azwz8D8/8gAAA/8/Azwz8D8/8gAAAAAzD/MPAM8AAgAAAAAzD/MPAM8AAgAAA=\",\"FPLB\":\"51\",\"FPLX\":\"0\",\"FPQQLSH\":\"2f33e977a0f34192b87ab35a87051cb7001\",\"FPQQPCH\":\"2f33e977a0f34192b87ab35a87051cb7\",\"FP_DM\":\"111005117101\",\"FP_HM\":\"20181073\",\"FWM\":\"1><94-942--9+18260<>/30+*4/+<+0-1*+<1<*04/><891>5+7/1+//<85-*+496562+-85*460>>913+/41+*82>5++<0<827/<<49762+\",\"HJBHSJE\":111.82,\"JQBH\":\"661616316327\",\"JYM\":\"78369242283632343666\",\"KPHJSE\":11.18,\"KPRQ\":\"2018-12-15 11:01:40\",\"PDF_URL\":\"5c146e9414c9e013acf57871\",\"STATUSCODE\":\"2000\",\"STATUSMSG\":\"发票生成PDF成功\"}";
//        InvoicePush parseObject = JsonUtils.getInstance().parseObject(invoicePush, InvoicePush.class);
//
//        apiPushService.pushToEnterprise(JsonUtils.getInstance().toJsonString(parseObject));
////        pushService.pushToEnterprise(invoiceInfo, parseObject);
//    }
//
//    @Test
//    public void testOrderList() {
//        Map paramMap = new HashMap(5);
//
//        List<String> ddztList = new ArrayList<String>();
//        ddztList.add("0");
//        ddztList.add("1");
//        ddztList.add("2");
//
//        paramMap.put("ddzt", ddztList);
//        paramMap.put("startTime", "2019-05-20 00:00:00");
//        paramMap.put("endTime", "2019-11-29 00:00:00");
//        paramMap.put("orderStatus", OrderInfoEnum.ORDER_VALID_STATUS_0.getKey());
//        paramMap.put("pageSize", Integer.parseInt("10"));
//        paramMap.put("currPage", Integer.parseInt("3"));
//        PageUtils pageUtils = orderProcessService.selectOrderInfo(paramMap);
//        System.out.println(JsonUtils.getInstance().toJsonString(pageUtils));
//        System.out.println("+++++++++" + pageUtils.getList().size());
//    }
//
//    @Test
//    public void testOrderMerge() {
//
////        String reqStr = "{\n" +
////                "  \"orderInfo\": {\n" +
////                "    \"bbmBbh\": \"13.0\",\n" +
////                "    \"createTime\": 1540365757000,\n" +
////                "    \"czdm\": \"10\",\n" +
////                "    \"ddh\": \"12018102400000000157\",\n" +
////                "    \"ddlx\": \"0\",\n" +
////                "    \"ddrq\": 1540365757000,\n" +
////                "    \"dkbz\": \"0\",\n" +
////                "    \"fhr\": \"\",\n" +
////                "    \"fpqqlsh\": \"201810241522371054996567276257280\",\n" +
////                "    \"fpzlDm\": \"2\",\n" +
////                "    \"ghfDh\": \"\",\n" +
////                "    \"ghfDz\": \"\",\n" +
////                "    \"ghfEmail\": \"\",\n" +
////                "    \"ghfMc\": \"啦啦啦啦\",\n" +
////                "    \"ghfNsrsbh\": \"911101050896860603\",\n" +
////                "    \"ghfQylx\": \"01\",\n" +
////                "    \"ghfSj\": \"\",\n" +
////                "    \"hjbhsje\": \"-0.0\",\n" +
////                "    \"hjse\": \"-0.0\",\n" +
////                "    \"id\": \"d25599a6809c4288b82846c2898a0919\",\n" +
////                "    \"kphjje\": \"-3157.0\",\n" +
////                "    \"kpjh\": \"1\",\n" +
////                "    \"kplx\": \"0\",\n" +
////                "    \"kpr\": \"张三\",\n" +
////                "    \"kpxm\": \"啦啦啦啦\",\n" +
////                "    \"nsrmc\": \"150001205110278555\",\n" +
////                "    \"nsrsbh\": \"150001205110278555\",\n" +
////                "    \"processId\": \"b2362a3d1a834387905c578caefea8cd\",\n" +
////                "    \"pydm\": \"000001\",\n" +
////                "    \"qdBz\": \"0\",\n" +
////                "    \"skr\": \"张三\",\n" +
////                "    \"sld\": \"106\",\n" +
////                "    \"sykchje\": \"3157.0\",\n" +
////                "    \"updateTime\": 1540365757000,\n" +
////                "    \"xhfDh\": \"133457778889\",\n" +
////                "    \"xhfDz\": \"北京市\",\n" +
////                "    \"xhfMc\": \"北京华品博睿科技有限公司\",\n" +
////                "    \"xhfNsrsbh\": \"150001205110278555\",\n" +
////                "    \"xhfYh\": \"中国银行622287888999800000\"\n" +
////                "  },\n" +
////                "  \"orderItemInfo\": [\n" +
////                "    {\n" +
////                "      \"createTime\": 1540365757000,\n" +
////                "      \"fphxz\": \"0\",\n" +
////                "      \"hsbz\": \"1\",\n" +
////                "      \"id\": \"981c288530b34eec96928e71689fc8a2\",\n" +
////                "      \"orderInfoId\": \"d25599a6809c4288b82846c2898a0919\",\n" +
////                "      \"se\": \"0.0\",\n" +
////                "      \"sl\": \"0.06\",\n" +
////                "      \"spbm\": \"1010101030000000000\",\n" +
////                "      \"sphxh\": \"1\",\n" +
////                "      \"xmdj\": \"3157.0\",\n" +
////                "      \"xmje\": \"-3157.0\",\n" +
////                "      \"xmmc\": \"啦啦啦啦\",\n" +
////                "      \"xmsl\": \"-1.0\",\n" +
////                "      \"yhzcbs\": \"0\"\n" +
////                "    }\n" +
////                "  ]\n" +
////                "}";
////
//        String reqStr1 = "{\"orderInfo\":{\"bbmBbh\":\"1.0\",\"bz\":\"\",\"createTime\":\"2019-07-02 20:03:44\",\"ddh\":\"70843398966041049177\",\"ddlx\":\"0\",\"ddrq\":\"2019-07-02 20:03:44.0\",\"dkbz\":\"0\",\"fhr\":\"B\",\"fpqqlsh\":\"201907022003441146026666418634752\",\"fpzlDm\":\"2\",\"ghfDh\":\"165456\",\"ghfDz\":\"好地方\",\"ghfEmail\":\"\",\"ghfMc\":\"128\",\"ghfNsrsbh\":\"1500000100030828A128\",\"ghfQylx\":\"01\",\"ghfSj\":\"\",\"ghfYh\":\"与恢复到\",\"ghfZh\":\"1564564814564545641\",\"hjbhsje\":\"-573.39\",\"hjse\":\"-44.91\",\"id\":\"201907022003441146026666418634753\",\"kphjje\":\"-618.30\",\"kpjh\":\"0\",\"kplx\":\"1\",\"kpr\":\"A\",\"kpxm\":\"232\",\"nsrmc\":\"销项测试子公司C48\",\"nsrsbh\":\"150001194112132161\",\"processId\":\"201907022003441146026666422829063\",\"qdBz\":\"0\",\"skr\":\"C\",\"sld\":\"120\",\"sykchje\":\"1854.90\",\"tschbz\":\"0\",\"updateTime\":\"2019-07-02 20:03:44\",\"xhfDh\":\"010-81234567\",\"xhfDz\":\"销方地址\",\"xhfMc\":\"销项测试子公司C48\",\"xhfNsrsbh\":\"150001194112132161\",\"xhfYh\":\"销方银行\",\"xhfZh\":\"6123456789\"},\"orderItemInfo\":[{\"createTime\":\"2019-07-02 20:03:44\",\"fphxz\":\"2\",\"ggxh\":\"\",\"hsbz\":\"0\",\"id\":\"201907022003441146026666418634754\",\"lslbs\":\"\",\"orderInfoId\":\"201907022003441146026666418634753\",\"se\":\"-1.05\",\"sl\":\"0.05\",\"spbm\":\"1010101010000000000\",\"sphxh\":\"1\",\"xmdj\":\"\",\"xmdw\":\"\",\"xmje\":\"-20.95\",\"xmmc\":\"*谷物*232\",\"xmsl\":\"\",\"yhzcbs\":\"0\",\"zzstsgl\":\"\"},{\"createTime\":\"2019-07-02 20:03:44\",\"fphxz\":\"1\",\"ggxh\":\"\",\"hsbz\":\"0\",\"id\":\"201907022003441146026666418634755\",\"lslbs\":\"\",\"orderInfoId\":\"201907022003441146026666418634753\",\"se\":\"0.03\",\"sl\":\"0.05\",\"spbm\":\"1010101010000000000\",\"sphxh\":\"2\",\"xmdj\":\"\",\"xmdw\":\"\",\"xmje\":\"0.63\",\"xmmc\":\"*谷物*232\",\"xmsl\":\"\",\"yhzcbs\":\"0\",\"zzstsgl\":\"\"},{\"createTime\":\"2019-07-02 20:03:44\",\"fphxz\":\"2\",\"ggxh\":\"\",\"hsbz\":\"0\",\"id\":\"201907022003441146026666418634756\",\"lslbs\":\"\",\"orderInfoId\":\"201907022003441146026666418634753\",\"se\":\"-2.09\",\"sl\":\"0.10\",\"spbm\":\"1070303010000000000\",\"sphxh\":\"3\",\"xmdj\":\"9.09090909\",\"xmdw\":\"\",\"xmje\":\"-20.91\",\"xmmc\":\"*中药饮片*dA01\",\"xmsl\":\"-2.3\",\"yhzcbs\":\"0\",\"zzstsgl\":\"\"},{\"createTime\":\"2019-07-02 20:03:44\",\"fphxz\":\"1\",\"ggxh\":\"\",\"hsbz\":\"0\",\"id\":\"201907022003441146026666418634757\",\"lslbs\":\"\",\"orderInfoId\":\"201907022003441146026666418634753\",\"se\":\"1.88\",\"sl\":\"0.10\",\"spbm\":\"1070303010000000000\",\"sphxh\":\"4\",\"xmdj\":\"\",\"xmdw\":\"\",\"xmje\":\"18.82\",\"xmmc\":\"*中药饮片*dA01\",\"xmsl\":\"\",\"yhzcbs\":\"0\",\"zzstsgl\":\"\"},{\"createTime\":\"2019-07-02 20:03:44\",\"fphxz\":\"2\",\"ggxh\":\"\",\"hsbz\":\"0\",\"id\":\"201907022003441146026666418634758\",\"lslbs\":\"\",\"orderInfoId\":\"201907022003441146026666418634753\",\"se\":\"-2.05\",\"sl\":\"0.05\",\"spbm\":\"1010101010000000000\",\"sphxh\":\"5\",\"xmdj\":\"\",\"xmdw\":\"\",\"xmje\":\"-40.95\",\"xmmc\":\"*谷物*145\",\"xmsl\":\"\",\"yhzcbs\":\"0\",\"zzstsgl\":\"\"},{\"createTime\":\"2019-07-02 20:03:44\",\"fphxz\":\"1\",\"ggxh\":\"\",\"hsbz\":\"0\",\"id\":\"201907022003441146026666418634759\",\"lslbs\":\"\",\"orderInfoId\":\"201907022003441146026666418634753\",\"se\":\"1.23\",\"sl\":\"0.05\",\"spbm\":\"1010101010000000000\",\"sphxh\":\"6\",\"xmdj\":\"\",\"xmdw\":\"\",\"xmje\":\"24.57\",\"xmmc\":\"*谷物*145\",\"xmsl\":\"\",\"yhzcbs\":\"0\",\"zzstsgl\":\"\"},{\"createTime\":\"2019-07-02 20:03:44\",\"fphxz\":\"2\",\"ggxh\":\"\",\"hsbz\":\"0\",\"id\":\"201907022003441146026666418634760\",\"lslbs\":\"\",\"orderInfoId\":\"201907022003441146026666418634753\",\"se\":\"-19.36\",\"sl\":\"0.06\",\"spbm\":\"1010101030000000000\",\"sphxh\":\"7\",\"xmdj\":\"107.54716981\",\"xmdw\":\"\",\"xmje\":\"-322.64\",\"xmmc\":\"*谷物*123\",\"xmsl\":\"-3.0\",\"yhzcbs\":\"0\",\"zzstsgl\":\"\"},{\"createTime\":\"2019-07-02 20:03:44\",\"fphxz\":\"1\",\"ggxh\":\"\",\"hsbz\":\"0\",\"id\":\"201907022003441146026666418634761\",\"lslbs\":\"\",\"orderInfoId\":\"201907022003441146026666418634753\",\"se\":\"14.91\",\"sl\":\"0.06\",\"spbm\":\"1010101030000000000\",\"sphxh\":\"8\",\"xmdj\":\"\",\"xmdw\":\"\",\"xmje\":\"248.43\",\"xmmc\":\"*谷物*123\",\"xmsl\":\"\",\"yhzcbs\":\"0\",\"zzstsgl\":\"\"},{\"createTime\":\"2019-07-02 20:03:44\",\"fphxz\":\"2\",\"hsbz\":\"0\",\"id\":\"201907022003441146026666418634762\",\"lslbs\":\"\",\"orderInfoId\":\"201907022003441146026666418634753\",\"se\":\"-3.91\",\"sl\":\"0.10\",\"spbm\":\"1010101010000000000\",\"sphxh\":\"9\",\"xmdj\":\"\",\"xmdw\":\"\",\"xmje\":\"-39.09\",\"xmmc\":\"*谷物*22\",\"xmsl\":\"\",\"yhzcbs\":\"0\",\"zzstsgl\":\"\"},{\"createTime\":\"2019-07-02 20:03:44\",\"fphxz\":\"1\",\"hsbz\":\"0\",\"id\":\"201907022003441146026666418634763\",\"lslbs\":\"\",\"orderInfoId\":\"201907022003441146026666418634753\",\"se\":\"3.13\",\"sl\":\"0.10\",\"spbm\":\"1010101010000000000\",\"sphxh\":\"10\",\"xmdj\":\"\",\"xmdw\":\"\",\"xmje\":\"31.27\",\"xmmc\":\"*谷物*22\",\"xmsl\":\"\",\"yhzcbs\":\"0\",\"zzstsgl\":\"\"},{\"createTime\":\"2019-07-02 20:03:44\",\"fphxz\":\"2\",\"hsbz\":\"0\",\"id\":\"201907022003441146026666418634764\",\"lslbs\":\"\",\"orderInfoId\":\"201907022003441146026666418634753\",\"se\":\"-4.91\",\"sl\":\"0.10\",\"spbm\":\"1010101030000000000\",\"sphxh\":\"11\",\"xmdj\":\"3.63636364\",\"xmdw\":\"\",\"xmje\":\"-49.09\",\"xmmc\":\"*谷物*14\",\"xmsl\":\"-13.5\",\"yhzcbs\":\"0\",\"zzstsgl\":\"\"},{\"createTime\":\"2019-07-02 20:03:44\",\"fphxz\":\"1\",\"hsbz\":\"0\",\"id\":\"201907022003441146026666422829056\",\"lslbs\":\"\",\"orderInfoId\":\"201907022003441146026666418634753\",\"se\":\"3.24\",\"sl\":\"0.10\",\"spbm\":\"1010101030000000000\",\"sphxh\":\"12\",\"xmdj\":\"\",\"xmdw\":\"\",\"xmje\":\"32.40\",\"xmmc\":\"*谷物*14\",\"xmsl\":\"\",\"yhzcbs\":\"0\",\"zzstsgl\":\"\"},{\"createTime\":\"2019-07-02 20:03:44\",\"fphxz\":\"2\",\"hsbz\":\"0\",\"id\":\"201907022003441146026666422829057\",\"lslbs\":\"\",\"orderInfoId\":\"201907022003441146026666418634753\",\"se\":\"-39.09\",\"sl\":\"0.10\",\"spbm\":\"1010101030000000000\",\"sphxh\":\"13\",\"xmdj\":\"97.72727273\",\"xmdw\":\"\",\"xmje\":\"-390.91\",\"xmmc\":\"*谷物*14\",\"xmsl\":\"-4.0\",\"yhzcbs\":\"0\",\"zzstsgl\":\"\"},{\"createTime\":\"2019-07-02 20:03:44\",\"fphxz\":\"1\",\"hsbz\":\"0\",\"id\":\"201907022003441146026666422829058\",\"lslbs\":\"\",\"orderInfoId\":\"201907022003441146026666418634753\",\"se\":\"7.82\",\"sl\":\"0.10\",\"spbm\":\"1010101030000000000\",\"sphxh\":\"14\",\"xmdj\":\"\",\"xmdw\":\"\",\"xmje\":\"78.18\",\"xmmc\":\"*谷物*14\",\"xmsl\":\"\",\"yhzcbs\":\"0\",\"zzstsgl\":\"\"},{\"createTime\":\"2019-07-02 20:03:44\",\"fphxz\":\"2\",\"ggxh\":\"\",\"hsbz\":\"0\",\"id\":\"201907022003441146026666422829059\",\"lslbs\":\"\",\"orderInfoId\":\"201907022003441146026666418634753\",\"se\":\"-2.57\",\"sl\":\"0.05\",\"spbm\":\"1010101010000000000\",\"sphxh\":\"15\",\"xmdj\":\"\",\"xmdw\":\"\",\"xmje\":\"-51.43\",\"xmmc\":\"*谷物*1137\",\"xmsl\":\"\",\"yhzcbs\":\"0\",\"zzstsgl\":\"\"},{\"createTime\":\"2019-07-02 20:03:44\",\"fphxz\":\"1\",\"ggxh\":\"\",\"hsbz\":\"0\",\"id\":\"201907022003441146026666422829060\",\"lslbs\":\"\",\"orderInfoId\":\"201907022003441146026666418634753\",\"se\":\"0.08\",\"sl\":\"0.05\",\"spbm\":\"1010101010000000000\",\"sphxh\":\"16\",\"xmdj\":\"\",\"xmdw\":\"\",\"xmje\":\"1.54\",\"xmmc\":\"*谷物*1137\",\"xmsl\":\"\",\"yhzcbs\":\"0\",\"zzstsgl\":\"\"},{\"createTime\":\"2019-07-02 20:03:44\",\"fphxz\":\"2\",\"ggxh\":\"\",\"hsbz\":\"0\",\"id\":\"201907022003441146026666422829061\",\"lslbs\":\"\",\"orderInfoId\":\"201907022003441146026666418634753\",\"se\":\"-2.24\",\"sl\":\"0.03\",\"spbm\":\"1010101040000000000\",\"sphxh\":\"17\",\"xmdj\":\"10.67961165\",\"xmdw\":\"\",\"xmje\":\"-74.76\",\"xmmc\":\"*谷物*789\",\"xmsl\":\"-7.0\",\"yhzcbs\":\"0\",\"zzstsgl\":\"\"},{\"createTime\":\"2019-07-02 20:03:44\",\"fphxz\":\"1\",\"ggxh\":\"\",\"hsbz\":\"0\",\"id\":\"201907022003441146026666422829062\",\"lslbs\":\"\",\"orderInfoId\":\"201907022003441146026666418634753\",\"se\":\"0.04\",\"sl\":\"0.03\",\"spbm\":\"1010101040000000000\",\"sphxh\":\"18\",\"xmdj\":\"\",\"xmdw\":\"\",\"xmje\":\"1.50\",\"xmmc\":\"*谷物*789\",\"xmsl\":\"\",\"yhzcbs\":\"0\",\"zzstsgl\":\"\"}]}";
//
//        CommonOrderInfo commonOrderInfo = JSON.parseObject(reqStr1, CommonOrderInfo.class);
//        System.out.println(JsonUtils.getInstance().toJsonString(rushRedInvoiceRequestInfoService.itemMerge(commonOrderInfo)));
//    }
//
//
//}
