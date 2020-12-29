package com.dxhy.order.consumer.download;

import com.dxhy.order.consumer.modules.order.controller.OrderTemplateDownloadController;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class DownLoadTest {

    private MockMvc mockMvc;

    @Autowired
    private OrderTemplateDownloadController templateDownloadController;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(templateDownloadController).build();//这个对象是Controller单元测试的关键
    }

    @Test
    public void wxAccountListTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/download/order"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


}
