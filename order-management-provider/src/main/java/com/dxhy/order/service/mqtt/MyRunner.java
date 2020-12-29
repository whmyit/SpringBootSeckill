//package com.dxhy.order.service.mqtt;
//
//import com.dxhy.order.api.ApiFangGeInterfaceService;
//import com.dxhy.order.api.RedisService;
//import com.dxhy.order.constant.Constant;
//import com.dxhy.order.model.RegistrationCode;
//import com.dxhy.order.utils.JsonUtils;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.Set;
//
///**
// * @Description mqtt 发送消息
// * @Author xueanna
// * @Date 2019/7/19 19:36
// */
//@Component
//public class MyRunner implements CommandLineRunner {
//    @Resource
//    private RedisService redisService;
//    @Resource
//    private ApiFangGeInterfaceService apiFangGeInterfaceService;
//
//    @Override
//    public void run(String... args) throws Exception {
//        final long timeInterval = 1000;// 十秒执行一次
//
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        //todo 后期改为xxl定时任务触发
//                        /**
//                         * 模糊查询redis里面的key，获取注册的所有税盘信息
//                         */
//                        Set<String> set = redisService.keys("*" + Constant.FG_TAX_DISK_INFO + "*");
//                        for (String key : set) {
//                            String s = redisService.get(key);
//                            RegistrationCode registrationCode = JsonUtils.getInstance().parseObject(s, RegistrationCode.class);
//                            //消费下一条数据
//                            apiFangGeInterfaceService.pushMqttMsg(registrationCode.getXhfNsrsbh(), registrationCode.getJqbh());
//                            //获取订单数据
//                        }
//                        Thread.sleep(timeInterval);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };
//        Thread thread = new Thread(runnable);
//        thread.start();
//    }
//}
