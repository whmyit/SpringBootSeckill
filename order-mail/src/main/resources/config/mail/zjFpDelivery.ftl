<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>邮件交付</title>

    <style>
        * {
            margin: 0;
            padding: 0;
            list-style: none;
            text-decoration: none;
            font-size: 14px;
            box-sizing: border-box;
        }
        
        html,
        body,
        .wrapper {
            height: 100%;
            width: 100%;
        }
        
        .wrapper {
            padding: 40px;
        }
        
        footer p {
            line-height: 30px;
        }
        
        section {
            padding-top: 10px;
        }
        
        h3 {
            font-weight: normal;
            padding-bottom: 20px;
        }
        
        section p {
            padding-left: 28px;
            padding-bottom: 15px;
        }
        
        .add-padding {
            padding-bottom: 20px;
        }
        
        .no-padding {
            padding-bottom: 0;
        }
        
        .PDF {
            padding-top: 10px;
            padding-bottom: 30px;
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
            font-weight: 600;
            color: #666666;
            margin-bottom: 20px;
            margin-left: -10px;
        }
    </style>
</head>

<body>
    <div class="wrapper">

        <section>
            <h3>尊敬的客户：</h3>
            <p class="no-padding">您好！</p>
            <p class="add-padding">您于${temData[0]}年${temData[1]}月${temData[2]}日选择开具的电子发票，已开具成功！我们将电子发票发送给您，请查收，谢谢。</p>
            <p>发票信息如下：</p>
            <p>开票日期：${temData[0]}年${temData[1]}月${temData[2]}日</p>
            <p>发票代码：${temData[3]}</p>
            <p>发票号码：${temData[4]}</p>
            <p>销方名称：${temData[5]}</p>
            <p>
                购方名称：${temData[6]}</p>
            <p>
                开票金额：￥${temData[7]}</p>
            <p class="PDF">附件是电子发票PDF文件，供下载使用。</p>
        </section>
        <p class="title">【 此邮件为系统自动触发， 请勿回复】</p>
        <footer>
           <#-- <div class="logo">
                <img src="cid:pic0" />
            </div>-->
        </footer>
    </div>
</body>

</html>