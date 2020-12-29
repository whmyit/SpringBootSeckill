package com.dxhy.order.service.manager;

import com.dxhy.order.api.ApiFpExpressService;
import com.dxhy.order.api.ApiKdniaoTrackApiService;
import com.dxhy.order.api.ApiSenderService;
import com.dxhy.order.constant.ConfigureConstant;
import com.dxhy.order.constant.KdniaoEnum;
import com.dxhy.order.constant.OrderManagementConstant;
import com.dxhy.order.dao.FpExpressDao;
import com.dxhy.order.model.PageUtils;
import com.dxhy.order.model.R;
import com.dxhy.order.model.dto.KdniaoQueryReq;
import com.dxhy.order.model.dto.KdniaoRes;
import com.dxhy.order.model.entity.FpExpress;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author sunpe
 * @date 2018/9/20
 */
@Slf4j
@Service
public class FpExpressServiceImpl implements ApiFpExpressService {
    private static final String LOGGER_MSG = "发票快递实现类";
    @Resource
    private FpExpressDao fpExpressDao;
    @Resource
    private ApiKdniaoTrackApiService kdniaoTrackApiService;
    
    @Resource
    private ApiSenderService senderService;


    @Override
    public int track(FpExpress record) {
    	//如果联系人不存在的话 直接新增
    	R r = senderService.dealSenderInfo(record);
        if (!ConfigureConstant.STRING_0000.equals(r.get(OrderManagementConstant.CODE))) {
            log.error("数据库保存收件人，寄件人信息失败");
            return -1;
        }
        record.setCreateTime(new Date());
        record.setExpressState(KdniaoEnum.WQS.code());
    
        return fpExpressDao.insertFpExpress(record);
    }

    @Override
    public PageUtils queryListByPage(Map map) {
        //当前页码
        Integer currPage = (Integer)map.get("page");
        //每页条数
        Integer pageSize = (Integer)map.get("limit");
        log.info("{},分页查询发票快递信息入参:{}", LOGGER_MSG, map);
        PageHelper.startPage(currPage, pageSize);
        List<FpExpress> list = fpExpressDao.queryList(map);
        PageInfo<FpExpress> pageInfo = new PageInfo<>(list);
        PageUtils page = new PageUtils(pageInfo.getList(), (int) pageInfo.getTotal(), pageInfo.getPageSize(), pageInfo.getPageNum());
        return page;
    }

    @Override
    public KdniaoRes getOrderTraces(KdniaoQueryReq req) {
        try{
            KdniaoRes res = kdniaoTrackApiService.getOrderTraces(req);
            if(res!=null){
                if(isOverByKdniao(res.getState())){
                    //更新签收状态
                    FpExpress record = new FpExpress();
                    record.setId(req.getOrderCode());
                    record.setExpressState(res.getState());
                    fpExpressDao.updateByPrimaryKeySelective(record);
                }
            }
            return res;
        }catch (Exception e){
            log.info("{}，跟踪订单异常,参数:{},", LOGGER_MSG, req.toString(), e);
        }
        return null;
    }

    @Override
    public List<FpExpress> queryWqs() {
        return fpExpressDao.queryWqs();
    }

    @Override
    public List<FpExpress> expressCompanyList(Map<String, Object> map) {
        log.info("{},查询快递公司名称/编码列表", LOGGER_MSG);
        return fpExpressDao.expressCompanyList(map);
    }

    private boolean isOverByKdniao(String code){
        return KdniaoEnum.YQS.code().equals(code)||
               KdniaoEnum.WTJ.code().equals(code);
    }

	@Override
	public int updateExpressInfo(FpExpress fpExpress) {
		int updateByPrimaryKeySelective = fpExpressDao.updateByPrimaryKeySelective(fpExpress);
		return updateByPrimaryKeySelective;
	}
}
