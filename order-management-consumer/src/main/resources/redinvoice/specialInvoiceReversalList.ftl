<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8"/>
<title>开具红字增值税专用发票信息表</title>
<style type="text/css">
body {
	font-family: SimSun;
	font-size: 16px;
}
.sir{
	margin:0 auto;
	width:690px;
}
.sir .title {
	font-size: 20px;
	font-weight: bold;
	text-align: center;
}
.sir .invoice-date {
	padding: 20px 0 10px 0;
}
.sir table {
	width: 100%;
	border: 1px solid black;
}
table tr, td {
	border: 1px solid black;
	height: 30px;
	padding: 0 5px;
}
table .clear-border {
	border: none;
}
table .clear-border td {
	border: none;
}
table .clear-border td:first-child {
	border-left: 1px solid black;
}
table .clear-border td:last-child {
	border-right: 1px solid black;
}
.sir table .embeddedTable {
	width: 100%;
	border-collapse:collapse;
	border-width:0px;
	border-style:hidden;
}
.text-center {
	text-align: center;
}
.text-right {
	text-align: right;
}
.underline {
	width: 120px;
	border-bottom: 1px solid black;
	display:inline-block;
}
.bl {
	border-left: 1px solid black!important;
}
.bt {
	border-top: 1px solid black!important;
}
.bb {
	border-bottom: 1px solid black!important;
}
.clear-bt {
	border-top: 0px!important;
}
.clear-br {
	border-right: 0px!important;
}
.clear-bl {
	border-left: 0px!important;
}
.clear-bb {
	border-bottom: 0px!important;
}
.p-0 {
	padding: 0;
}
.pl-32 {
	padding-left: 32px;
}
.pl-64 {
	padding-left: 64px;
}
.pt-20 {
	padding-top: 20px;
}
.pb-10 {
	padding-bottom: 10px;
}
.w-60 {
	width: 60px;
}
.w-100 {
	width: 100px;
}
.w-185 {
	width: 185px;
}
.h-25 {
	height: 25px;
}
.f-10 {
	font-size: 10px;
}
</style>
</head>
<body>
	<div class="sir">
		<div class="title">
			开具红字增值税专用发票信息表
		</div>
		<div class="invoice-date">
			<span>填开日期：</span><span>${tksj?split("-")[0]}年${tksj?split("-")[1]}月${(tksj?split("-")[2])?split(" ")[0]}日</span>
		</div>
		<table cellspacing="0" cellpadding="0">
			<tr>
				<td class="w-60 text-center clear-bt clear-bb clear-bl" rowspan="2">销售方</td>
				<td class="w-100 text-center clear-bt clear-bb clear-bl">名&nbsp;&nbsp;&nbsp;&nbsp;称</td>
				<td class="w-185 clear-bt clear-bb clear-bl" colspan="2">${xhfMc}</td>
				<td class="w-60 text-center clear-bt clear-bb clear-bl" rowspan="2">购买方</td>
				<td class="w-100 text-center clear-bt clear-bb clear-bl">名&nbsp;&nbsp;&nbsp;&nbsp;称</td>
				<td class="w-185 clear-bt clear-br clear-bb clear-bl" colspan="2">${ghfMc}</td>
			</tr>
			<tr>
				<td class="w-100 text-center clear-bb clear-bl">纳税人识别号</td>
				<td class="w-185 clear-bb clear-bl" colspan="2">${xhfNsrsbh}</td>
				<td class="w-100 text-center clear-bb clear-bl">纳税人识别号</td>
				<td class="w-185 clear-br clear-bb clear-bl" colspan="2">${ghfNsrsbh}</td>
			</tr>

			<tr>
				<td class="w-60 text-center clear-bl">开具<br/>红字<br/>专用<br/>发票<br/>内容</td>
				<td class="p-0 clear-bl clear-br" colspan="7">
					<table class="embeddedTable" cellspacing="0" cellpadding="0" >
						<tr>
							<td class="w-185  text-center">货物（劳务服务）名称</td>
							<td class="w-100 text-center">数量</td>
							<td class="w-100 text-center">单价</td>
							<td class="w-100 text-center">金额</td>
							<td class="w-100 text-center">税率</td>
							<td class="w-100 text-center">税额</td>
						</tr>
						<#assign usedRowNum = 0>
						<#list items as item>
							<#assign usedRowNum += (item.xmmc?length/11)?ceiling>
							<tr class="h-25 clear-border">
								<td class="h-25 text-left ${(item.xmmc?length gt 11)?string('f-10','')}">${item.xmmc}</td>
								<td class="h-25 text-center">
									<#if item.xmsl??>
										${item.xmsl}
									</#if>
								</td>
								<td class="h-25 text-right">
									<#if item.xmdj??>
										${item.xmdj}
									</#if>
								</td>
								<td class="h-25 text-right">${item.xmje}</td>
								<td class="h-25 text-center">
									<#if (item.sl??) && (item.sl != "")>
										${item.sl?number*100}%
									</#if>
								</td>
								<td class="h-25 text-right">
									<#if item.se??>
										${item.se}
									</#if>
								</td>
							</tr>
						</#list>
						<#if usedRowNum lt 8>
							<#list 1..8-(usedRowNum) as i>
								<tr class="h-25 clear-border">
									<td class="h-25" colspan="6">&nbsp;</td>
								</tr>
							</#list>
						</#if>
						<tr class="clear-border">
							<td class="text-center">合计</td>
							<td> </td>
							<td> </td>
							<td class="text-right">￥${hjbhsje}</td>
							<td> </td>
							<td class="text-right">￥${hjse}</td>
						</tr>
					</table>
				</td>
			</tr>

			<tr>
				<td class="w-60 text-center clear-bt clear-bl clear-bb">说明</td>
				<td colspan="7" class="clear-border">
					<table class="embeddedTable" cellspacing="0" cellpadding="0">
						<tr class="clear-border">
							<td class="clear-br clear-bl" colspan="7">一、购买方
								<#if sqsm?substring(0, 1) == '1'>
									<input type="checkbox" checked="checked" disabled="disabled" readonly="readonly"/>
								<#else>
									<input type="checkbox" disabled="disabled" readonly="readonly"/>
								</#if>
							</td>
						</tr>
						<tr class="clear-border">
							<td class="clear-br clear-bl" colspan="7"><span class="pl-32">对应蓝字专用发票抵扣增值税销项税额情况：</span></td>
						</tr>
						<tr class="clear-border">
							<td class="clear-br clear-bl" colspan="7">
								<span class="pl-64">1.已抵扣
									<#if sqsm?substring(0, 2) == '11'>
										<input type="checkbox" checked="checked" disabled="disabled" readonly="readonly"/>
									<#else>
										<input type="checkbox" disabled="disabled" readonly="readonly"/>
									</#if>
								</span>
							</td>
						</tr>
						<tr class="clear-border">
							<td class="clear-br clear-bl" colspan="7">
								<span class="pl-64">2.未抵扣
									<#if sqsm?substring(0, 2) == '10'>
										<input type="checkbox" checked="checked" disabled="disabled" readonly="readonly"/>
									<#else>
										<input type="checkbox" disabled="disabled" readonly="readonly"/>
									</#if>
								</span>
							</td>
						</tr>
						<tr class="clear-border">
							<td class="pt-20 pb-10 clear-br clear-bl" colspan="7">
								<span class="pl-32">对应蓝字专用发票的代码：
									<#if sqsm?substring(0, 1) == '1'>
										<span class="underline"><#if yfpDm??> ${yfpDm}</#if></span>    号码：
										<span class="underline"><#if yfpDm??>${yfpHm}</#if></span>
									<#else>
										<span class="underline">&nbsp;</span>    号码：<span
												class="underline">&nbsp;</span>
									</#if>
								</span>
							</td>
						</tr>
						<tr class="clear-border ">
							<td class="clear-br clear-bl" colspan="7">二、销售方
								<#if sqsm?substring(0, 1) == '0'>
									<input type="checkbox" checked="checked" disabled="disabled" readonly="readonly"/>
								<#else>
									<input type="checkbox" disabled="disabled" readonly="readonly"/>
								</#if>
							</td>
						</tr>
						<tr class="clear-border">
							<td class="pt-20 pb-10 clear-br clear-bl" colspan="7">
								<span class="pl-32">对应蓝字专用发票的代码：
									<#if sqsm?substring(0, 1) == '0'>
										<span class="underline"><#if yfpDm??> ${yfpDm}</#if></span>    号码：
										<span class="underline"><#if yfpHm??>${yfpHm}</#if></span>
									<#else>
										<span class="underline">&nbsp;</span>    号码：<span
												class="underline">&nbsp;</span>
									</#if>
								</span>
							</td>
						</tr>
					</table>
				</td>
			</tr>

			<tr>
				<td class="bt bb text-center w-60 clear-bl clear-bb"><br/>红字发<br/>票信息<br/>表编号<br/>&nbsp;</td>
				<td class="clear-br clear-bb clear-bl" colspan="7">${xxbbh}</td>
			</tr>
		</table>
	</div>
</body>
</html>
