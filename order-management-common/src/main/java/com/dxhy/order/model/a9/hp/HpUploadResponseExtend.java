package com.dxhy.order.model.a9.hp;

import com.dxhy.order.model.a9.ResponseBaseBeanExtend;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
/**
 * 红票扩展
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:04
 */
@Setter
@Getter
public class HpUploadResponseExtend extends ResponseBaseBeanExtend {
    
    private List<HpUploadResponseHzfpsqbsc> response_HZFPSQBSC;
    private String sqbscqqpch;
    
}
