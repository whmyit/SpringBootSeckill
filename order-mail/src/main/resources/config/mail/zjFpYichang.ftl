<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
    <style>
        * {
            padding: 0;
            margin: 0;
        }
        
        .box {
            font-size: 14px;
        }
        
        .box p {
            padding-left: 20px;
            line-height: 30px;
        }
        
        .logo {
            width: 411px;
        }
        
        .logo img {
            display: block;
            width: 411px;
            height: auto;
        }
        
        .title {
            margin-top: 30px;
            margin-top: 20px;
            color: #666666;
            font-weight: 600;
            margin-left: -10px;
        }
    </style>
</head>

<body>
    <div class="box">
        <p>订单号：${temData[0]}</p>
        <p>订单状态：
            <#if temData[1] = 6>
                开票失败
            <#else>
                ${temData[1]}
            </#if>
        </p>
        <p>订单总金额：${temData[2]}</p>
        <p>订单时间：${temData[3]}</p>
        <p>发票类型：${temData[4]}</p>
        <p>异常原因说明：${temData[5]}</p>
        <p class="title">【 此邮件为系统自动触发， 请勿回复】</p>
       <#-- <footer>
            <div class="logo">
                <img src="cid:pic0" />
            </div>
        </footer>-->
    </div>
</body>

</html>