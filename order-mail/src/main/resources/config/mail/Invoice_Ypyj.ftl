<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Document</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            font-size: 14px;
            text-decoration: none;
            list-style: none;
        }
        
        .box>p {
            line-height: 40px;
            padding-left: 20px;
        }
        
        .indent {
            padding-left: 50px;
        }
        
        section {
            padding-left: 50px;
        }
        
        section div {
            width: 800px;
            border: 1px solid #000000;
            border-right: none;
            border-bottom: none;
        }
        
        section div p {
            display: flex;
        }
        
        section div p span {
            width: 200px;
            text-align: center;
            border-right: 1px solid #000000;
            border-bottom: 1px solid #000000;
            word-wrap: break-word
        }
        
        section div p:nth-child(1) span {
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
        
        .line_height span {
            padding: 10px 0;
        }
        .table td{
            padding:5px;
        }
    </style>

</head>

<body>
    <div class="box">
        <p>亲爱的用户：</p>
        <p class="indent">您好！您的税盘内有以下票种余量不足，请及时补充票源！</p>

            <div>
                <table border="1" cellspacing="0" class="table">
                    <tr>
                        <p>
                        <td> <span>税盘名称</span></td>
                        <td> <span>税盘号</span></td>
                        <td> <span>发票种类</span></td>
                        <td> <span>剩余张数</span></td>
                        </p>
                    </tr>
                    <tr>
                        <p class="line_height">
                           <td><span>${temData[0]}</span></td>
                           <td> <span>${temData[1]}</span></td>
                           <td><span>${temData[2]}</span></td>
                           <td><span>${temData[3]}</span></td>
                        </p>
                    </tr>
                </table>
            </div>

        <p class="title">【 此邮件为系统自动触发， 请勿回复】</p>
       <#-- <footer>
            <div class="logo">
                <img src="cid:pic0" />
            </div>
        </footer>-->
    </div>
</body>

</html>