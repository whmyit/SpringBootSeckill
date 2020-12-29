import com.dxhy.order.utils.HttpInvoiceRequestUtil;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 接口测试工具类
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2019/12/11 9:41
 */
@Slf4j
public class TestInvoiceRest {
    
    /**
     * 发票状态查询接口测试
     */
    private static void testQueryInvoiceStatus() {
        String invoiceQueryRequest = "";
        invoiceQueryRequest = "{\"FPLX\":\"2\",\"FPQQPCH\":\"a1f78b0f37d4432da74eca1f00cd5b97\"}";
//        invoiceQueryRequest = "{\"FPLX\":\"2\",\"FPQQPCH\":\"1562554164413358LEaGKt\"}";
        System.out.println(JsonUtils.getInstance().toJsonString(HttpInvoiceRequestUtil.queryInvoiceStatus("http://10.1.2.51:58089/invoice/business/v1.0/invoiceStatusQuery", invoiceQueryRequest, "001")));
    }
    
    
    public static void main(String[] args) {
        
        /**
         * 接口测试
         */
        //发票状态查询结果
        testQueryInvoiceStatus();
        
        //发票状态查询结果
        
    }
}
