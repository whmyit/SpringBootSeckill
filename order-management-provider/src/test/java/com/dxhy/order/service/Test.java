package com.dxhy.order.service;

import com.dxhy.order.utils.DecimalCalculateUtil;
import com.dxhy.order.utils.DistributedKeyMaker;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author ZSC-DXHY
 * @date 创建时间: 2019/5/24 9:19
 */
public class Test {
    static Set<String> keyset = new HashSet<>();
    
    public static void main(String[] args) {
        System.out.println(" ".trim());
        System.out.println(StringUtils.isBlank(" "));
        String a = "2932.50";
        String b = "2933.774";
        
        
        double aa = 1.2759d;
        double bb = -1.2759d;
        System.out.println(new BigDecimal("-1.275").setScale(2, RoundingMode.HALF_UP).toString());
        System.out.println(new BigDecimal(aa).setScale(2, RoundingMode.HALF_UP).toString());
        System.out.println(DecimalCalculateUtil.round(aa, 2));
        System.out.println(DecimalCalculateUtil.round(bb, 2));
        System.out.println(DecimalCalculateUtil.decimalFormat(aa, 2));
        System.out.println(DecimalCalculateUtil.decimalFormat(bb, 2));
    
        ExecutorService executorService = Executors.newFixedThreadPool(500);
        for (int i = 0; i < 1; i++) {
//            System.out.println("当前线程:"+Thread.currentThread().getId());
            executorService.execute(() -> {
                
                for (int j = 0; j < 10000; j++) {
                    String key = DistributedKeyMaker.generateShotKey();
//                    System.out.println("当前线程为:"+Thread.currentThread().getId()+",循环次数为:"+j+",生成的key:"+key);
                    if (keyset.contains(key)) {
                        System.err.println("1存在重复值:" + key);
                    }
//                    synchronized (test.class){
                    if (!keyset.add(key)) {
                        System.err.println("存在重复值:" + key);
                    }
//                    }
                
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(keyset.size());
                
            });
            
        }
        
        executorService.shutdown();
        
    }
}

class Task implements Runnable {
    
    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> METHOD to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the METHOD <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
    
    }
}
