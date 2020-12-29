package com.dxhy.order.model.a9.c48ydtj;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * @author: wangyang
 * @date: 2020/7/3 19:05
 * @description
 */
@Getter
@Setter
public class YdtjParam implements Serializable {
    
    private static final long serialVersionUID = 1205933704848803832L;
    private List<String> nsrsbhs;
    private String kpnf;
    private String kpyf;
    private String fpzlDm;
    private String jqbh;
    private Integer currPage;
    private Integer pageSize;
    private String terminalCode;
}
