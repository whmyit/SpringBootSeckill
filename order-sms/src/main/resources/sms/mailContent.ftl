<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>电子发票邮件正文</title>
<style>
body {
	margin: 0;
}

#content {
	width: 1080px;
	height: 400px;
	margin: 0 auto;
}

#left {
	background: #e8f0f3;
	height: 100%;
	width: 200px;
	float: left;
	border: 1px #dddddd solid;
	margin: 5px 30px 0 0;
}

#right {
	background: #e8f0f3;
	height: 100%;
	width: 200px;
	float: right;
	border: 1px #dddddd solid;
	margin: 5px 0 0 30px;
}

#main {
	background: #ffffff;
}
</style>
</head>
<body>
	<div id="content">
		<div id="main">
			<table width="606" align="left" border="0" cellspacing="0"
				cellpadding="0"
				style="font-family:verdana;font-size:14px;line-height:180%">
				<tbody>
					<tr>
						<td width="34">&nbsp;</td>
						<td width="538">
							<p><strong>[本邮件为系统自动发送，请勿直接回复]</strong></p>
							<p style="margin:0;padding:25px 0 15px">
								尊敬的顾客,您好!
							</p>
							<p style="text-indent:2em;margin:0;padding:0 0 20px 0">
								您于${ddrq}购物并选择开具电子发票，我们将电子发票发送给您，以便作为您的维权保修凭证、报销凭证。
							</p>
							<p style="text-indent:2em;margin:0;padding:0 0 0 0">
								发票代码：${fpdm}
							</p>
							<p style="text-indent:2em;margin:0;padding:0 0 0 0">
								发票号码：${fphm}
							</p>
							<p style="text-indent:2em;margin:0;padding:0 0 0 0">
								开票日期：${kprq}
							</p>
							<p style="text-indent:2em;margin:0;padding:0 0 0 0">
								发票抬头：${gfmc}
							</p>
							<p style="text-indent:2em;margin:0;padding:0 0 20px 0">
								开票金额：￥${kphjje}
							</p>
							<p style="text-indent:2em;margin:0;padding:0 0 20px 0;align:left">
								如有需要，可以打印本电子发票，其法律效力、基本用途、基本使用规定等与税务机关监制的增值税普通发票相同。
							</p>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</body>
</html>