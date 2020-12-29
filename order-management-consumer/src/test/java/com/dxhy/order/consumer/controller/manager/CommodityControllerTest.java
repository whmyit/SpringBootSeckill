package com.dxhy.order.consumer.controller.manager;

import com.dxhy.order.consumer.modules.manager.controller.CommodityController;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(value = SpringJUnit4ClassRunner.class)
@SpringBootTest
@Slf4j
public class CommodityControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @InjectMocks
    private CommodityController commodityController;

    @Before
    public void setUp(){
        //this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        this.mockMvc= MockMvcBuilders.standaloneSetup(commodityController).build();

    }


    @Test
    public void queryCommodity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/commodity/queryCommodity")
                .param("xhfNsrsbh","[\"150001194112132161\"]"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
