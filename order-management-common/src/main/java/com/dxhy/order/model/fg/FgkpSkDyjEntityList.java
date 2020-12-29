package com.dxhy.order.model.fg;

import com.dxhy.order.model.a9.ResponseBaseBean;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * 打印机表(sk_dyj)
 *
 * @author bianj
 * @version 1.0.0 2019-06-06
 */
@Getter
@Setter
public class FgkpSkDyjEntityList extends ResponseBaseBean implements Serializable {
   
   private List<FgkpSkDyjEntity> data;
   
}
