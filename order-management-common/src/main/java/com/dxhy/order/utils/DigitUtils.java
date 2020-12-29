package com.dxhy.order.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * @author ZSC-DXHY
 */
public class DigitUtils {
    
    public static BigDecimal formatDoublePrecision(BigDecimal digit) {
        return digit.setScale(2, RoundingMode.HALF_UP);
    }
}
