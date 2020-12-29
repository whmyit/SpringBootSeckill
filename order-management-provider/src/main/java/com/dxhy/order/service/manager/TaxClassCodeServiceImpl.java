package com.dxhy.order.service.manager;

import com.dxhy.order.api.ApiTaxClassCodeService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.dao.TaxClassCodeDao;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.entity.OilEntity;
import com.dxhy.order.model.entity.TaxClassCodeEntity;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 税收分类编码接口实现类
 *
 * @author liangyuhuan
 * @date 2018/7/27
 */
@Slf4j
@Service
public class TaxClassCodeServiceImpl implements ApiTaxClassCodeService {
    
    @Resource
    private TaxClassCodeDao taxClassCodeDao;
    
    @Override
    public PageUtils queryTaxClassCode(Map<String, Object> map) {
    
        int pageSize = (int) map.get("limit");
        int currPage = (int) map.get("page");
        PageHelper.startPage(currPage, pageSize);
        List<TaxClassCodeEntity> taxClassList = taxClassCodeDao.selectTaxClassCode(map);
        //处理科学计数法
        BigDecimal bigDecimal = null;
        for (TaxClassCodeEntity taxClassCode : taxClassList) {
            if (taxClassCode.getTjjbm() != null && !"".equals(taxClassCode.getTjjbm()) && taxClassCode.getTjjbm().indexOf("E") > 0) {
                bigDecimal = new BigDecimal(taxClassCode.getTjjbm());
                taxClassCode.setTjjbm(bigDecimal.toPlainString());
            }
        }
        PageInfo<TaxClassCodeEntity> pageInfo = new PageInfo<>(taxClassList);
        PageUtils page = new PageUtils(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageSize(), pageInfo.getPageNum());
        return page;
        
    }

    @Override
    public TaxClassCodeEntity queryTaxClassCodeEntity(String taxClassCode) {
        if (StringUtils.isBlank(taxClassCode)) {
            return null;
        }
        TaxClassCodeEntity taxClassCodeEntity = taxClassCodeDao.queryTaxClassCodeEntityBySpbm(taxClassCode);
        if (taxClassCodeEntity == null || StringUtils.isBlank(taxClassCodeEntity.getZzssl())) {
            return null;
        }
        return taxClassCodeEntity;
    }
    
    
    @Override
    public OilEntity queryOilBySpbm(String spbm) {
    	if(StringUtils.isBlank(spbm)){
            return null;
        }
        TaxClassCodeEntity queryTaxClassCodeEntityBySpbm = taxClassCodeDao.queryTaxClassCodeEntityBySpbm(spbm);
        //判断是否为成品油的商品编码
        if (queryTaxClassCodeEntityBySpbm != null && ConfigureConstant.STRING_Y.equals(queryTaxClassCodeEntityBySpbm.getCpy())) {
            return convertToOilEntity(queryTaxClassCodeEntityBySpbm);
        } else {
            return null;
        }
    }
    
    /**
     * bean转换
     *
     * @param queryTaxClassCodeEntityBySpbm
     * @return
     */
    private OilEntity convertToOilEntity(TaxClassCodeEntity queryTaxClassCodeEntityBySpbm) {
        OilEntity oilEntity = new OilEntity();
        oilEntity.setSpbm(queryTaxClassCodeEntityBySpbm.getSpbm());
        oilEntity.setSpbmmc(queryTaxClassCodeEntityBySpbm.getSpmc());
        return oilEntity;
    }
}
