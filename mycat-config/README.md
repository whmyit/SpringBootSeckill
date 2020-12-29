# 分库分表
## 1 表结构的修改
### 1.1 新增加了一个column
1. invoice_batch_request_item
2. order_item_info
3. order_process_info_ext

    `nsrsbh` varchar(20) NOT NULL COMMENT '销售方纳税人识别号'

```sql
ALTER TABLE invoice_batch_request_item ADD COLUMN nsrsbh varchar(20) NOT NULL COMMENT '销售方纳税人识别号';

ALTER TABLE order_item_info ADD COLUMN nsrsbh varchar(20) NOT NULL COMMENT '销售方纳税人识别号';

ALTER TABLE order_process_info_ext ADD COLUMN nsrsbh varchar(20) NOT NULL COMMENT '销售方纳税人识别号';

```


## 2. 参数模拟
### 2.1 税号
| 税号 | db index |
| --- | --- |
| 911101082018050516 | 0 |
| 91110108201805051x | 1 |

### 2.2 建库sql
#### 2.2.1 全局表

#### 2.2.2 分库表

### 2.3 数据准备
> mysqldump jd_sales_order1 -uroot -p  --complete-insert -t    >jd_sales_order.sql



