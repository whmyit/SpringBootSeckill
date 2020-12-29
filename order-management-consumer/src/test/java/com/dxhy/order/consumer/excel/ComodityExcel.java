package com.dxhy.order.consumer.excel;

import com.dxhy.order.consumer.ConsumerStarter;
import com.dxhy.order.consumer.utils.ExcelUtils;
import com.dxhy.order.model.entity.CommodityCodeEntity;
import com.dxhy.order.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2019/10/22 15:26
 */
@RunWith(SpringJUnit4ClassRunner.class) // SpringJUnit支持，由此引入Spring-Test框架支持！
@SpringBootTest(classes = ConsumerStarter.class) // 指定我们SpringBoot工程的Application启动类
@WebAppConfiguration
@Slf4j
public class ComodityExcel {
    
    
    @Test
    public void test() throws IOException {
        
        InputStream is = new FileInputStream(new File("C:/Users/ZSC-DXHY/Downloads/商品税编模板.xlsx"));
        List<CommodityCodeEntity> commodityCodeEntities = ExcelUtils.getCommodityExcelInfo(is, "商品税编模板.xlsx");
        System.out.println(JsonUtils.getInstance().toJsonString(commodityCodeEntities));
    }
    
}
