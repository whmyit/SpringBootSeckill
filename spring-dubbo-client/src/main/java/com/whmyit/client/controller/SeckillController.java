package com.whmyit.client.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.whmyit.api.Enum.ResultEnum;
import com.whmyit.api.common.GlobalResult;
import com.whmyit.api.dto.Exposer;
import com.whmyit.api.dto.SeckillExecution;
import com.whmyit.api.entity.Seckill;
import com.whmyit.api.entity.SuccessKilled;
import com.whmyit.api.handle.ResultHandle;
import com.whmyit.api.service.SeckillService;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @Author: whmyit@163.com
 * @Description: SeckillW WEB Request
 * @Date: Created in 10:51  2018/11/13
 */
@RequestMapping("seckill")
@RestController()
public class SeckillController {

    @Reference //dubbo 注解
    private SeckillService seckillService;

    @GetMapping("/{seckillId}/detail")
    public Seckill getSeckill(@PathVariable("seckillId") Long seckillId) {
        return seckillService.getSeckill(seckillId);
    }

    @GetMapping("/list")
    public List<Seckill> getSeckillList() {
        return seckillService.getSeckillList();
    }

    @PostMapping("/{seckillId}/exposer")
    public Exposer exposer(@PathVariable("seckillId") Long seckillId) {
        Exposer exposer= seckillService.exportSeckillUrl(seckillId);
        return exposer;
    }

    @GetMapping("/{seckillId}/{md5}/excute")
    public Object seckillExecution(@PathVariable("seckillId") Long seckillId,
                                       @PathVariable("md5") String md5) {
        if (seckillId == null || md5==null ) {
            return new GlobalResult<SeckillExecution>(ResultEnum.RES_PARAM_NULL.getCode(),ResultEnum.RES_PARAM_NULL.getMsg());
        }

        SeckillExecution seckillExecution=seckillService.executeSeckill(seckillId,13021921680L,md5);

        return seckillExecution;
    }

    @GetMapping("/time/now")
    public Date date() {
        return new Date();
    }



}
