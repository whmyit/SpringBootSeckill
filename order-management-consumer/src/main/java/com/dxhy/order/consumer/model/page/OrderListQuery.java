package com.dxhy.order.consumer.model.page;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;

/**
 *
 * @ClassName ：OrderListQuery
 * @Description ：列表查询前端参数
 * @author ：杨士勇
 * @date ：2019年7月1日 下午10:42:58
 *
 *
 */

@Data
@ApiModel(value = "OrderListQuery")
public class OrderListQuery {
    
    @ApiModelProperty(value = "0 未开票 1 开票中 2 开票成功 3 开票失败 以json数组传递", name = "开票状态", example = "0")
    private String ddzt;
    @ApiModelProperty(value = "订单来源  可以传多个值  以json数组传输 0 Excel导入 1 手工录入 2 api原始订单接口 3 自动开票录入数据 4 其他 5 静态码扫码开票 6 动态码扫码开票",name = "订单来源", example = "[\"1\"]")
    private String ddly;
    @ApiModelProperty(value = "最小金额", name = "最小金额", example = "100.00")
    private String minKphjje;
    @ApiModelProperty(value = "最大金额", name = "最大金额", example = "200.00")
    private String maxKphjje;
    @ApiModelProperty(value = "作废标志: 0：正常 1：已作废", name = "作废标志", example = "0")
    private String zfbz;
    @ApiModelProperty(value = "开始时间", name = "开始时间", example = "2019-08-01", required = true)
    private String startTime;
    @ApiModelProperty(value = "结束时间", name = "结束时间", example = "2019-08-01")
    private String endTime;
    @ApiModelProperty(value = "订单号", name = "订单号", example = "wzk1342532535")
    private String ddh;
    @ApiModelProperty(value = "购方名称", name = "购方名称", example = "xx贸易有限公司")
    private String ghfmc;
    @ApiModelProperty(value = "销方名称", name = "销方名称", example = "xx贸易有限公司")
    private String xhfmc;
    @ApiModelProperty(value = "组织机构代码", name = "组织机构代码", example = "002")
    private String deptId;
    @ApiModelProperty(value = "购方税号", name = "购方税号", example = "150001194112132161")
    private String ghfNsrsbh;
    @ApiModelProperty(value = "以 json 数组传递 发票类型 0 专用发票 51 电子发票 2 普通发票", name = "发票种类代码", example = "0")
    private String fpzldm;
    @ApiModelProperty(value = "门店号", name = "门店号", example = "10001")
    private String mdh;
    @ApiModelProperty(value = "受理点", name = "受理点", example = "5")
    private String sld;
    @ApiModelProperty(value = "开票人", name = "开票人", example = "杨xx")
    private String kpr;
    @Size(max = 12, message = "发票代码长度错误")
    @ApiModelProperty(value = "发票代码", name = "发票代码", example = "1400111555，111005117101")
    private String fpdm;
    
    @Size(max = 8, message = "发票号码长度错误")
    @ApiModelProperty(value = "发票起码", name = "发票起码", example = "51000926")
    private String fpqh;
    @ApiModelProperty(value = "发票止码", name = "发票止码", example = "51000926")
    private String fpzh;
    @ApiModelProperty(value = "发票号码", name = "发票号码", example = "51000926")
    private String fphm;
    @ApiModelProperty(value = "业务类型", name = "业务类型", example = "100005")
    private String ywlx;
    @ApiModelProperty(value = "推送标志  0：未推送 1：推送成功 2： 推送失败 ", name = "推送标志", example = "0")
    private String tsbz;
    @ApiModelProperty(value = "冲红标志  可以传多个 以json数组传 不传的话默认查询所有  0:正常1:全部冲红成功2:全部冲红中3:全部冲红失败4:部分冲红成功5:部分冲红中6:部分冲红失败", name = "冲红标志", example = "[\"0\"]")
    private String chbz;
    @Size(max = 1, message = "开票类型长度只能为1")
    @ApiModelProperty(value = "开票类型 0：蓝票 1：红票", name = "开票类型", example = "0")
    private String kplx;
    @Size(min = 1, message = "销方税号不能为空")
    @ApiModelProperty(value = "销货方纳税人识别号 已json数组传输", name = "销方税号", example = "[\"150001194112132161\"]", required = true)
    private String xhfNsrsbh;
    @ApiModelProperty(value = "排序字段", name = "orderBy", example = "updateTime", required = true)
    private String orderBy;
    @ApiModelProperty(value = "分页参数 每页展示的条数", name = "每页展示的条数", example = "10")
    private int pageSize;
    @ApiModelProperty(value = "分页参数 当前页", name = "当前页", example = "10")
    private int currPage;
    @ApiModelProperty(value = "清单标志(0:无清单;1:清单;2:农产品无清单;3:农产品清单;4:成品油)", name = "清单标志", example = "['0','1']")
    private String qdbz;
    @ApiModelProperty(value = "差异金额(0:全部;1:无差异;2:有差异", name = "差异金额", example = "0")
    private String cyje;
    @ApiModelProperty(value = "开票状态(0:全部;1:未开票;2:已开票", name = "开票状态", example = "0")
    private String kpzt;

    @ApiModelProperty(value = "数据来源", name = "数据来源", example = "1")
    private String sjly;

    @ApiModelProperty(value = "查询时间", name = "查询时间 createTime 根据createTime查询 kprq 根据kprq查询", example = "createTime")
    private String queryTime;

    @ApiModelProperty(value = "审核状态", name = "审核状态 （0 初始状态 1 待审核状态 2 审核通过 3 审核驳回）", example = "0")
    private String checkStatus;
    
	
}
