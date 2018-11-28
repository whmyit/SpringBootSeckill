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
import com.whmyit.api.util.MD5Utill;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;

/**
 * @Author: whmyit@163.com
 * @Description: SeckillW WEB Request
 * @Date: Created in 10:51  2018/11/13
 */
@RequestMapping("seckill")
@Controller
public class SeckillController {

    @Reference //dubbo 注解
    private SeckillService seckillService;

    @GetMapping("/{seckillId}/detail")
    public String getSeckill(@PathVariable("seckillId") Long seckillId,Model model) {
        if (seckillId==null)
            return "redirect:/seckill/list";
        model.addAttribute("seckill",seckillService.getSeckill(seckillId));
        return "seckill/seckill-detail";
    }

    @GetMapping("/list")
    public String getSeckillList(Model model) {
        model.addAttribute("hello","Hello, Spring Boot!");
        model.addAttribute("seckillList", seckillService.getSeckillList());
        return "seckill/seckill-list";
    }



    @GetMapping("/{seckillId}/exposer")
    @ResponseBody
    public Exposer exposer(@PathVariable("seckillId") Long seckillId) {
        Exposer exposer= seckillService.exportSeckillUrl(seckillId);
        return exposer;
    }

    @GetMapping("/{seckillId}/{md5}/execution")
    @ResponseBody
    public Object seckillExecution(@PathVariable("seckillId") Long seckillId,
                                       @PathVariable("md5") String md5) {
        if (seckillId == null || md5==null ) {
            return new GlobalResult<SeckillExecution>(ResultEnum.RES_PARAM_NULL.getCode(),ResultEnum.RES_PARAM_NULL.getMsg());
        }

        SeckillExecution seckillExecution=seckillService.executeSeckill(seckillId,13021921680L,md5);

        return seckillExecution;
    }

    //@GetMapping("/{seckillId}/{userPhone}/execution")
    //@ResponseBody
    //public Object seckillExecution(@PathVariable("seckillId") Long seckillId,@PathVariable("userPhone") Long userPhone) {
    //    if (seckillId == null ) {
    //        return new GlobalResult<SeckillExecution>(ResultEnum.RES_PARAM_NULL.getCode(),ResultEnum.RES_PARAM_NULL.getMsg());
    //    }
    //
    //    SeckillExecution seckillExecution=seckillService.executeSeckill(seckillId,userPhone, MD5Utill.getUrlMD5(seckillId));
    //
    //    return seckillExecution;
    //}

    @GetMapping("/time/now")
    @ResponseBody
    public Date date() {
        return new Date();
    }



}
