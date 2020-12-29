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
        <p>错误信息：${temData[0]}</p>
        <p class="title">【 此邮件为系统自动触发， 请勿回复】</p>
    </div>
</body>

</html>