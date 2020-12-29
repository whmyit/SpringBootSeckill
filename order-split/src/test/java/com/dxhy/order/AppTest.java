package com.dxhy.order;

import com.dxhy.order.constant.OrderSplitConfig;
import com.dxhy.order.constant.OrderSplitException;
import com.dxhy.order.model.CommonOrderInfo;
import com.dxhy.order.utils.JsonUtils;
import com.dxhy.order.utils.OrderSplitUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite(AppTest.class);
    }
    
    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        assertTrue(true);
    }
    
    public static void main(String[] args) throws OrderSplitException {
        String a = "{\n" +
                "  \"orderInfo\": {\n" +
                "    \"bbmBbh\": \"33.0\",\n" +
                "    \"bz\": \"\",\n" +
                "    \"createTime\": \"2020-04-02 11:40:53\",\n" +
                "    \"ddh\": \"52107058292432715814\",\n" +
                "    \"ddlx\": \"0\",\n" +
                "    \"ddrq\": \"2020-04-02 11:40:53\",\n" +
                "    \"dkbz\": \"0\",\n" +
                "    \"fhr\": \"陈爽\",\n" +
                "    \"fpqqlsh\": \"405535691004661760\",\n" +
                "    \"fpzlDm\": \"51\",\n" +
                "    \"ghfDh\": \"\",\n" +
                "    \"ghfDz\": \"\",\n" +
                "    \"ghfEmail\": \"\",\n" +
                "    \"ghfMc\": \"西北民航机场建设集团有限责任公司工会委员会\",\n" +
                "    \"ghfNsrsbh\": \"8161000005692208XP\",\n" +
                "    \"ghfQylx\": \"01\",\n" +
                "    \"ghfSj\": \"\",\n" +
                "    \"ghfYh\": \"\",\n" +
                "    \"ghfZh\": \"\",\n" +
                "    \"hjbhsje\": \"100000.00\",\n" +
                "    \"hjse\": \"0.00\",\n" +
                "    \"id\": \"405535759191592960\",\n" +
                "    \"kphjje\": \"100000.00\",\n" +
                "    \"kpjh\": \"\",\n" +
                "    \"kplx\": \"0\",\n" +
                "    \"kpr\": \"陈冠宇\",\n" +
                "    \"kpxm\": \"佳农 烟台特级红富士苹果 礼盒 15个装 单果重约230g 生鲜水果\",\n" +
                "    \"nsrmc\": \"工福（北京）科技发展有限公司\",\n" +
                "    \"nsrsbh\": \"15000120561127953X\",\n" +
                "    \"processId\": \"405535691646390272\",\n" +
                "    \"qdBz\": \"0\",\n" +
                "    \"skr\": \"李佳孟\",\n" +
                "    \"sld\": \"\",\n" +
                "    \"thdh\": \"\",\n" +
                "    \"tschbz\": \"0\",\n" +
                "    \"updateTime\": \"2020-04-02 11:40:53\",\n" +
                "    \"xhfDh\": \"010-82609318\",\n" +
                "    \"xhfDz\": \"北京市昌平区科技园区超前路37号院16号楼2层B0293\",\n" +
                "    \"xhfMc\": \"工福（北京）科技发展有限公司\",\n" +
                "    \"xhfNsrsbh\": \"15000120561127953X\",\n" +
                "    \"xhfYh\": \"光大银行北京苏州街支行\",\n" +
                "    \"xhfZh\": \"35330188000101150\",\n" +
                "    \"yfpDm\": \"\",\n" +
                "    \"yfpHm\": \"\",\n" +
                "    \"ywlx\": \"\"\n" +
                "  },\n" +
                "  \"orderItemInfo\": [\n" +
                "    {\n" +
                "      \"createTime\": \"2020-04-02 11:40:54\",\n" +
                "      \"fphxz\": \"0\",\n" +
                "      \"ggxh\": \"\",\n" +
                "      \"hsbz\": \"0\",\n" +
                "      \"id\": \"405535759195787264\",\n" +
                "      \"kce\": \"\",\n" +
                "      \"lslbs\": \"3\",\n" +
                "      \"orderInfoId\": \"405535759191592960\",\n" +
                "      \"se\": \"0.00\",\n" +
                "      \"sl\": \"0.00\",\n" +
                "      \"spbm\": \"1010115010100000000\",\n" +
                "      \"sphxh\": \"1\",\n" +
                "      \"wcje\": \"0.00\",\n" +
                "      \"xhfNsrsbh\": \"15000120561127953X\",\n" +
                "      \"xmdj\": \"116.62\",\n" +
                "      \"xmdw\": \"\",\n" +
                "      \"xmje\": \"14577.50\",\n" +
                "      \"xmmc\": \"*水果*佳农 烟台特级红富士苹果 礼>盒 15个装 单果重约230g 生鲜水果\",\n" +
                "      \"xmsl\": \"125.00\",\n" +
                "      \"yhzcbs\": \"0\",\n" +
                "      \"zzstsgl\": \"\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"createTime\": \"2020-04-02 11:40:54\",\n" +
                "      \"fphxz\": \"0\",\n" +
                "      \"ggxh\": \"\",\n" +
                "      \"hsbz\": \"0\",\n" +
                "      \"id\": \"405535691646390274\",\n" +
                "      \"kce\": \"\",\n" +
                "      \"lslbs\": \"3\",\n" +
                "      \"orderInfoId\": \"405535759191592960\",\n" +
                "      \"se\": \"0.00\",\n" +
                "      \"sl\": \"0.00\",\n" +
                "      \"spbm\": \"1010402000000000000\",\n" +
                "      \"sphxh\": \"2\",\n" +
                "      \"wcje\": \"0.00\",\n" +
                "      \"xhfNsrsbh\": \"15000120561127953X\",\n" +
                "      \"xmdj\": \"155.82\",\n" +
                "      \"xmdw\": \"\",\n" +
                "      \"xmje\": \"19477.50\",\n" +
                "      \"xmmc\": \"*海水产品*京东海外直采 马达加斯加老虎虾/黑虎虾（大号）800g\",\n" +
                "      \"xmsl\": \"125.00\",\n" +
                "      \"yhzcbs\": \"0\",\n" +
                "      \"zzstsgl\": \"\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"createTime\": \"2020-04-02 11:40:54\",\n" +
                "      \"fphxz\": \"0\",\n" +
                "      \"ggxh\": \"\",\n" +
                "      \"hsbz\": \"0\",\n" +
                "      \"id\": \"405535759199981568\",\n" +
                "      \"kce\": \"\",\n" +
                "      \"lslbs\": \"3\",\n" +
                "      \"orderInfoId\": \"405535759191592960\",\n" +
                "      \"se\": \"0.00\",\n" +
                "      \"sl\": \"0.00\",\n" +
                "      \"spbm\": \"1010112020000000000\",\n" +
                "      \"sphxh\": \"3\",\n" +
                "      \"wcje\": \"0.00\",\n" +
                "      \"xhfNsrsbh\": \"15000120561127953X\",\n" +
                "      \"xmdj\": \"57.62\",\n" +
                "      \"xmdw\": \"\",\n" +
                "      \"xmje\": \"7202.50\",\n" +
                "      \"xmmc\": \"*蔬菜*聚怀斋 焦作温县沙土铁棍山药（精选80/90公分）怀山药 新鲜蔬菜\",\n" +
                "      \"xmsl\": \"125.00\",\n" +
                "      \"yhzcbs\": \"0\",\n" +
                "      \"zzstsgl\": \"\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"createTime\": \"2020-04-02 11:40:54\",\n" +
                "      \"fphxz\": \"0\",\n" +
                "      \"ggxh\": \"\",\n" +
                "      \"hsbz\": \"0\",\n" +
                "      \"id\": \"405535759199981569\",\n" +
                "      \"kce\": \"\",\n" +
                "      \"lslbs\": \"3\",\n" +
                "      \"orderInfoId\": \"405535759191592960\",\n" +
                "      \"se\": \"0.00\",\n" +
                "      \"sl\": \"0.00\",\n" +
                "      \"spbm\": \"1010303020100000000\",\n" +
                "      \"sphxh\": \"4\",\n" +
                "      \"wcje\": \"0.00\",\n" +
                "      \"xhfNsrsbh\": \"15000120561127953X\",\n" +
                "      \"xmdj\": \"78.30\",\n" +
                "      \"xmdw\": \"\",\n" +
                "      \"xmje\": \"9787.50\",\n" +
                "      \"xmmc\": \"*畜禽产品*德青源 鼠你有福A+级鲜鸡蛋 48枚\",\n" +
                "      \"xmsl\": \"125.00\",\n" +
                "      \"yhzcbs\": \"0\",\n" +
                "      \"zzstsgl\": \"\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"createTime\": \"2020-04-02 11:40:54\",\n" +
                "      \"fphxz\": \"0\",\n" +
                "      \"ggxh\": \"\",\n" +
                "      \"hsbz\": \"0\",\n" +
                "      \"id\": \"405535759199981570\",\n" +
                "      \"kce\": \"\",\n" +
                "      \"lslbs\": \"3\",\n" +
                "      \"orderInfoId\": \"405535759191592960\",\n" +
                "      \"se\": \"0.00\",\n" +
                "      \"sl\": \"0.00\",\n" +
                "      \"spbm\": \"1010115010300000000\",\n" +
                "      \"sphxh\": \"5\",\n" +
                "      \"wcje\": \"0.00\",\n" +
                "      \"xhfNsrsbh\": \"15000120561127953X\",\n" +
                "      \"xmdj\": \"97.02\",\n" +
                "      \"xmdw\": \"\",\n" +
                "      \"xmje\": \"12127.50\",\n" +
                "      \"xmmc\": \"*水果*广西沃柑 高糖柑橘子桔子 新鲜水果\",\n" +
                "      \"xmsl\": \"125.00\",\n" +
                "      \"yhzcbs\": \"0\",\n" +
                "      \"zzstsgl\": \"\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"createTime\": \"2020-04-02 11:40:54\",\n" +
                "      \"fphxz\": \"0\",\n" +
                "      \"ggxh\": \"\",\n" +
                "      \"hsbz\": \"0\",\n" +
                "      \"id\": \"405535759204175872\",\n" +
                "      \"kce\": \"\",\n" +
                "      \"lslbs\": \"3\",\n" +
                "      \"orderInfoId\": \"405535759191592960\",\n" +
                "      \"se\": \"0.00\",\n" +
                "      \"sl\": \"0.00\",\n" +
                "      \"spbm\": \"1030107010100000000\",\n" +
                "      \"sphxh\": \"6\",\n" +
                "      \"wcje\": \"0.00\",\n" +
                "      \"xhfNsrsbh\": \"15000120561127953X\",\n" +
                "      \"xmdj\": \"106.82\",\n" +
                "      \"xmdw\": \"\",\n" +
                "      \"xmje\": \"13352.50\",\n" +
                "      \"xmmc\": \"*肉及肉制品*恒都 澳洲羊排 1200g/袋 羊肉 烧烤食材 生鲜自营\",\n" +
                "      \"xmsl\": \"125.00\",\n" +
                "      \"yhzcbs\": \"0\",\n" +
                "      \"zzstsgl\": \"\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"createTime\": \"2020-04-02 11:40:54\",\n" +
                "      \"fphxz\": \"2\",\n" +
                "      \"ggxh\": \"\",\n" +
                "      \"hsbz\": \"0\",\n" +
                "      \"id\": \"405535759204175873\",\n" +
                "      \"kce\": \"\",\n" +
                "      \"lslbs\": \"3\",\n" +
                "      \"orderInfoId\": \"405535759191592960\",\n" +
                "      \"se\": \"0.00\",\n" +
                "      \"sl\": \"0.00\",\n" +
                "      \"spbm\": \"1010401000000000000\",\n" +
                "      \"sphxh\": \"7\",\n" +
                "      \"wcje\": \"0.00\",\n" +
                "      \"xhfNsrsbh\": \"15000120561127953X\",\n" +
                "      \"xmdj\": \"195.02\",\n" +
                "      \"xmdw\": \"\",\n" +
                "      \"xmje\": \"24377.50\",\n" +
                "      \"xmmc\": \"*海水产品*三都港 深海有机宁德大黄花鱼500g 海鲜水产 生鲜\",\n" +
                "      \"xmsl\": \"125.00\",\n" +
                "      \"yhzcbs\": \"0\",\n" +
                "      \"zzstsgl\": \"\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"createTime\": \"2020-04-02 11:40:54\",\n" +
                "      \"fphxz\": \"1\",\n" +
                "      \"ggxh\": \"\",\n" +
                "      \"hsbz\": \"0\",\n" +
                "      \"id\": \"405535691654778880\",\n" +
                "      \"kce\": \"\",\n" +
                "      \"lslbs\": \"3\",\n" +
                "      \"orderInfoId\": \"405535759191592960\",\n" +
                "      \"se\": \"0.00\",\n" +
                "      \"sl\": \"0.00\",\n" +
                "      \"spbm\": \"1010401000000000000\",\n" +
                "      \"sphxh\": \"8\",\n" +
                "      \"wcje\": \"0.00\",\n" +
                "      \"xhfNsrsbh\": \"15000120561127953X\",\n" +
                "      \"xmdj\": \"\",\n" +
                "      \"xmdw\": \"\",\n" +
                "      \"xmje\": \"-902.50\",\n" +
                "      \"xmmc\": \"*海水产品*三都港 深海有机宁德大黄花鱼500g 海鲜水产 生鲜\",\n" +
                "      \"xmsl\": \"\",\n" +
                "      \"yhzcbs\": \"0\",\n" +
                "      \"zzstsgl\": \"\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"processInfo\": {\n" +
                "    \"createTime\": \"2020-04-02 11:40:54\",\n" +
                "    \"ddcjsj\": \"2020-04-02 11:40:53\",\n" +
                "    \"ddh\": \"52107058292432715814\",\n" +
                "    \"ddlx\": \"0\",\n" +
                "    \"ddly\": \"1\",\n" +
                "    \"ddzt\": \"0\",\n" +
                "    \"fpqqlsh\": \"405535691004661760\",\n" +
                "    \"fpzlDm\": \"51\",\n" +
                "    \"ghfMc\": \"西北民航机场建设集团有限责任公司工>会委员会\",\n" +
                "    \"ghfNsrsbh\": \"8161000005692208XP\",\n" +
                "    \"hjbhsje\": \"100000.00\",\n" +
                "    \"id\": \"405535691646390272\",\n" +
                "    \"kphjje\": \"100000.00\",\n" +
                "    \"kpse\": \"0.00\",\n" +
                "    \"kpxm\": \"佳农 烟台特级红富士苹果 礼盒 15个装 单果重约230g 生鲜水果\",\n" +
                "    \"orderInfoId\": \"405535759191592960\",\n" +
                "    \"orderStatus\": \"0\",\n" +
                "    \"sbyy\": \"\",\n" +
                "    \"updateTime\": \"2020-04-02 11:40:54\",\n" +
                "    \"xhfMc\": \"工福（北京）科技发展有限公司\",\n" +
                "    \"xhfNsrsbh\": \"15000120561127953X\",\n" +
                "    \"ywlx\": \"\"\n" +
                "  },\n" +
                "  \"singleSl\": false,\n" +
                "  \"terminalCode\": \"001\"\n" +
                "}";
        String b = "{\"limitJe\":99999.99,\"splitRule\":\"0\",\"splitType\":\"1\"}";
        OrderSplitConfig config = JsonUtils.getInstance().parseObject(b, OrderSplitConfig.class);
        CommonOrderInfo commonOrderInfo = JsonUtils.getInstance().parseObject(a, CommonOrderInfo.class);
        List<CommonOrderInfo> commonOrderInfoList = OrderSplitUtil.orderSplit(commonOrderInfo, config);
        System.out.println(JsonUtils.getInstance().toJsonString(commonOrderInfoList));
    }
}
