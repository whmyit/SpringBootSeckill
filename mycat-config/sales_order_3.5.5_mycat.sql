/*
 Navicat Premium Data Transfer

 Source Server         : 10.1.1.224-销项产品测试
 Source Server Type    : MySQL
 Source Server Version : 50720
 Source Host           : 10.1.1.224:3306
 Source Schema         : sales_order

 Target Server Type    : MySQL
 Target Server Version : 50720
 File Encoding         : 65001

 Date: 12/05/2020 11:27:26
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for authentication
-- ----------------------------
DROP TABLE IF EXISTS `authentication`;
CREATE TABLE `authentication`
(
    `id`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '鉴权表ID',
    `nsrsbh`      varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '纳税人识别号',
    `secretId`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标识用户身份的SecretId',
    `secretKey`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'secretKey',
    `auth_status` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '状态（0:有效,1:无效）',
    `create_time` datetime(0)                                                  NULL DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime(0)                                                  NULL DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_auth_nsrsbh` (`nsrsbh`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '对外接口鉴权表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for business_type_info
-- ----------------------------
DROP TABLE IF EXISTS `business_type_info`;
CREATE TABLE `business_type_info`
(
    `id`            varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '业务类型表ID',
    `business_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '业务类型名称',
    `business_id`   varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '业务类型ID',
    `xhf_nsrsbh`    varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '销货方纳税人识别号',
    `xhfMc`         varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '销货方名称',
    `ly`            varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL DEFAULT NULL COMMENT '数据来源：0接口采集 1页面添加',
    `description`   varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '业务类型描述',
    `status`        varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL DEFAULT '0' COMMENT '状态（0：有效；1：无效）',
    `create_time`   datetime(0)                                             NULL DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime(0)                                             NULL DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_business_type_id` (`business_id`) USING BTREE,
    INDEX `idx_business_name` (`business_name`) USING BTREE,
    INDEX `idx_business_nsrsbh` (`xhf_nsrsbh`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '业务类型表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for buyer_manage_info
-- ----------------------------
DROP TABLE IF EXISTS `buyer_manage_info`;
CREATE TABLE `buyer_manage_info`
(
    `id`              varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '购方信息表ID',
    `taxpayer_code`   varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '纳税人识别号',
    `purchase_name`   varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '购方名称',
    `address`         varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '购方地址',
    `phone`           varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '购方电话',
    `bank_of_deposit` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '开户银行',
    `bank_number`     varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '银行账号',
    `email`           varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '购方联系人邮箱',
    `remarks`         varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
    `create_time`     datetime(0)                                             NULL DEFAULT NULL COMMENT '创建时间',
    `create_user_id`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '创建人',
    `modify_time`     datetime(0)                                             NULL DEFAULT NULL COMMENT '更新时间',
    `modify_user_id`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '更新人ID',
    `ghf_qylx`        varchar(5) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL DEFAULT NULL COMMENT '购买方企业类型',
    `xhf_nsrsbh`      varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '父级税号',
    `xhf_mc`          varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '销货方名称',
    `buyer_code`      varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '购买方编码',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_purchase_name` (`purchase_name`) USING BTREE,
    INDEX `idx_taxpayer_code` (`taxpayer_code`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '购方信息表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for commodity_code
-- ----------------------------
DROP TABLE IF EXISTS `commodity_code`;
CREATE TABLE `commodity_code`
(
    `id`                          varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '商品编码ID',
    `sort_id`                     bigint(10)                                              NULL DEFAULT NULL COMMENT '序号',
    `taxpayer_code`               varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '纳税人识别号',
    `merchandise_name`            varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品名称',
    `encoding`                    varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '编码',
    `tax_items`                   varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品税目',
    `brief_code`                  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '简码',
    `tax_rate`                    varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '税率',
    `specification_model`         varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格型号',
    `metering_unit`               varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '计量单位',
    `unit_price`                  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '单价',
    `tax_logo`                    varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '\'含税价标志（0：否，1：是）\',',
    `hide_the_logo`               varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '\'隐藏标志（0：否，1：是）\',',
    `enjoy_preferential_policies` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '\'享受优惠政策（0：否，1：是）\',',
    `tax_class_code`              varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '税收分类编码',
    `tax_classification_name`     varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT '' COMMENT '税收分类名称',
    `preferential_policies_type`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '优惠政策类型jh：空：非零税率，0:出口零税,1：免税，2：不征税 3:普通零税率',
    `user_id`                     varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '当前登录人id    ',
    `create_time`                 datetime(0)                                             NULL DEFAULT NULL COMMENT '数据创建时间',
    `modify_time`                 datetime(0)                                             NULL DEFAULT NULL COMMENT '更新时间',
    `modify_user_id`              char(10) CHARACTER SET utf8 COLLATE utf8_general_ci     NULL DEFAULT NULL COMMENT '更新人',
    `group_id`                    varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '分组id',
    `enterprise_name`             varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '企业名称',
    `data_source`                 varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL DEFAULT NULL COMMENT '数据来源(0-集团共享；1-手工创建；2-模板导入；3-采集下级；)',
    `matching_state`              varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL DEFAULT NULL COMMENT '匹配状态(0-已匹配；1-未匹配)',
    `data_state`                  varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL DEFAULT NULL COMMENT '数据状态（0-启用；1-停用）',
    `tax_class_abbreviation`      varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '税收分类简称',
    `description`                 varchar(400) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
    `collect_ident`               varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL DEFAULT NULL COMMENT '采集标识（0：已采集；）',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_commodity_merchandise` (`merchandise_name`) USING BTREE,
    INDEX `idx_commodity_nsrsbh` (`taxpayer_code`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '商品编码表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for drawer_info
-- ----------------------------
DROP TABLE IF EXISTS `drawer_info`;
CREATE TABLE `drawer_info`
(
    `id`              varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT 'id',
    `taxpayer_code`   varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '纳税人识别号',
    `drawer_name`     varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '开票人名称',
    `re_check_name`   varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '复核人',
    `name_of_payee`   varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '收款人',
    `create_time`     datetime(0)                                             NULL DEFAULT NULL COMMENT '创建时间',
    `credate_user_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '创建人',
    `modify_time`     datetime(0)                                             NULL DEFAULT NULL COMMENT '更新时间',
    `modify_user_id`  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_drawer_nsrsbh` (`taxpayer_code`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '开票信息表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ewm_config
-- ----------------------------
DROP TABLE IF EXISTS `ewm_config`;
CREATE TABLE `ewm_config`
(
    `id`           varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '主键',
    `xhf_mc`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '销货方名称',
    `xhf_nsrsbh`   varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '销货方税号',
    `fpzldm`       varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL     DEFAULT NULL COMMENT '发票种类代码',
    `invalid_time` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci        NOT NULL COMMENT '二维码失效时间 天为单位',
    `create_time`  datetime(0)                                                   NOT NULL COMMENT '数据创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ewm_config_item
-- ----------------------------
DROP TABLE IF EXISTS `ewm_config_item`;
CREATE TABLE `ewm_config_item`
(
    `id`            varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '主键id',
    `ewm_coinfg_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci        NOT NULL COMMENT '二维码设置主表id',
    `fpzldm`        varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '发票种类代码',
    `sld`           varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '受理点id',
    `sld_mc`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '受理点名称',
    `create_time`   datetime(0)                                                   NOT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for ewm_gzh_config
-- ----------------------------
DROP TABLE IF EXISTS `ewm_gzh_config`;
CREATE TABLE `ewm_gzh_config`
(
    `id`               varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '主键id',
    `nsrsbh`           varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '纳税人识别号',
    `appid`            varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL,
    `appkey`           varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL,
    `gzh_subcribe_ewm` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '公众号关注二维码 此处存放url',
    `home_page_logo`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '扫码开票首页的banner 图片的url',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for fp_express
-- ----------------------------
DROP TABLE IF EXISTS `fp_express`;
CREATE TABLE `fp_express`
(
    `id`                   varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '快递表ID',
    `user_id`              varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '用户ID',
    `org_id`               varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '组织ID',
    `sender_name`          varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '寄件人姓名',
    `sender_address`       varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '寄件人地址',
    `sender_phone`         varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '寄件人电话',
    `sender_mail`          varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '寄件人邮箱',
    `sender_post_code`     varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '寄件人邮编',
    `recipients_name`      varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收件人姓名',
    `recipients_address`   varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收件人地址',
    `recipients_phone`     varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '收件人电话',
    `recipients_mail`      varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收件人邮箱',
    `recipients_post_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '收件人邮编',
    `express_company_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '快递公司名称',
    `express_company_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '快递公司编码',
    `express_number`       varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '快递单号',
    `buyer_name`           varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '购方名称',
    `express_items`        text CHARACTER SET utf8 COLLATE utf8_general_ci         NULL COMMENT '快递的物品信息:多个物品逗号分隔',
    `express_state`        varchar(5) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL DEFAULT NULL COMMENT '快递状态 0-无轨迹 1-已揽收 2-在途中 3-签收 4-问题件 ',
    `create_time`          datetime(0)                                             NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_express_user_id` (`user_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = latin1
  COLLATE = latin1_swedish_ci COMMENT = '发票快递表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for fp_kdbm
-- ----------------------------
DROP TABLE IF EXISTS `fp_kdbm`;
CREATE TABLE `fp_kdbm`
(
    `id`                   varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '快递编码表ID',
    `express_company_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '快递公司名称',
    `express_company_code` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '快递公司编码',
    `api_id`               varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '快递API',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '快递公司编码表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for fp_sender
-- ----------------------------
DROP TABLE IF EXISTS `fp_sender`;
CREATE TABLE `fp_sender`
(
    `id`                      varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '发票邮寄表ID',
    `user_id`                 varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '用户ID',
    `type`                    char(1) CHARACTER SET utf8 COLLATE utf8_general_ci      NULL DEFAULT NULL COMMENT '邮寄角色 0 寄件人 1 收件人',
    `sender_id`               varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '寄件人编号',
    `recipients_id`           varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '收件人编号',
    `recipients_company_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收件人公司名称',
    `name`                    varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '姓名',
    `address`                 varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址',
    `phone`                   varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '电话',
    `mail`                    varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
    `post_code`               varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '邮编',
    `create_time`             datetime(0)                                             NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '邮寄角色信息表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for group_commodity
-- ----------------------------
DROP TABLE IF EXISTS `group_commodity`;
CREATE TABLE `group_commodity`
(
    `id`              varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '主键',
    `group_code`      varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '分组编码',
    `group_name`      varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分组名称',
    `superior_coding` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '上级分组编码',
    `is_leaf`         varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL DEFAULT NULL COMMENT '是否为页子节点   0 是  1 不是',
    `create_time`     datetime(0)                                             NULL DEFAULT NULL COMMENT '创建时间',
    `user_id`         varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '创建人ID',
    `taxpayer_code`   varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '纳税人识别号',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '商品编码分组表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for group_tax_class_code
-- ----------------------------
DROP TABLE IF EXISTS `group_tax_class_code`;
CREATE TABLE `group_tax_class_code`
(
    `id`                      varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '主键',
    `sort_id`                 bigint(10)                                              NULL DEFAULT NULL COMMENT '序号',
    `taxpayer_code`           varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '纳税人识别号',
    `merchandise_name`        varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品名称',
    `encoding`                varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '编码',
    `tax_items`               varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '商品税目',
    `brief_code`              varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '简码',
    `specification_model`     varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '规格型号',
    `metering_unit`           varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '计量单位',
    `unit_price`              varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '单价',
    `tax_class_code`          varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '税收分类编码',
    `tax_classification_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT '' COMMENT '税收分类名称',
    `create_time`             datetime(0)                                             NULL DEFAULT NULL COMMENT '数据创建时间',
    `group_id`                varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '分组id',
    `data_source`             varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL DEFAULT NULL COMMENT '数据来源(0-集团共享；1-手工创建；2-模板导入；3-采集下级；)',
    `matching_state`          varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL DEFAULT NULL COMMENT '匹配状态(0-已匹配；1-未匹配)',
    `data_state`              varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL DEFAULT NULL COMMENT '数据状态（0-启用；1-停用；2-删除；）',
    `share_state`             varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL DEFAULT NULL COMMENT '共享状态（0-允许共享；1-待核实；）',
    `dept_id`                 varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '集团id',
    `tax_class_abbreviation`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '税收分类简称',
    `difference_flag`         varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL DEFAULT NULL COMMENT '差异标识（0-采集下级差异标识;1-原库里的数据差异标识）',
    `description`             varchar(400) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_group_taxcode_merchandise` (`merchandise_name`) USING BTREE,
    INDEX `idx_group_taxcode_nsrsbh` (`taxpayer_code`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '集团商品税编表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for invoice_batch_request
-- ----------------------------
DROP TABLE IF EXISTS `invoice_batch_request`;
CREATE TABLE `invoice_batch_request`
(
    `id`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '批量开票请求批次表id',
    `fpqqpch`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '发票请求批次号',
    `nsrsbh`      varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '销售方纳税人识别号',
    `sldid`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL COMMENT '受理点id',
    `kpjh`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL COMMENT '开票机号',
    `kplx`        varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '开票类型(1:纸,2:电)',
    `fplb`        varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NULL DEFAULT NULL COMMENT '发票类别',
    `status`      varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NULL DEFAULT NULL COMMENT '请求状态(研二返回的状态code)',
    `message`     varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '返回信息',
    `kzzd`        varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '扩展字段',
    `create_time` datetime(0)                                                    NULL DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime(0)                                                    NULL DEFAULT NULL COMMENT '请求时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_batch_info_fpqqpch` (`fpqqpch`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '批量请求开票接口主表'
  ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for invoice_batch_request_item
-- ----------------------------
DROP TABLE IF EXISTS `invoice_batch_request_item`;
CREATE TABLE `invoice_batch_request_item`
(
    `id`               varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '批量开票请求明细表id',
    `invoice_batch_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '批量开票请求主表id',
    `fpqqpch`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '发票请求批次号',
    `fpqqlsh`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '订单请求流水号',
    `kplsh`            varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '开票流水号对应接口中fpqqlsh',
    `status`           varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NULL DEFAULT NULL COMMENT '请求状态',
    `message`          varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '返回信息',
    `create_time`      datetime(0)                                                    NULL DEFAULT NULL COMMENT '创建时间',
    `update_time`      datetime(0)                                                    NULL DEFAULT NULL COMMENT '请求时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_batch_item_kplsh` (`kplsh`) USING BTREE,
    INDEX `idx_batch_item_batch_id` (`invoice_batch_id`) USING BTREE,
    INDEX `idx_batch_item_fpqqlsh` (`fpqqlsh`) USING BTREE,
    INDEX `idx_batch_item_fpqqpch` (`fpqqpch`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '批量请求开票接口明细表'
  ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for invoice_item_request
-- ----------------------------
DROP TABLE IF EXISTS `invoice_item_request`;
CREATE TABLE `invoice_item_request`
(
    `id`            varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键',
    `company_id`    varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '企业id',
    `report_period` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属期间',
    `xhf_nsrsbh`    varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '销货方纳税人识别号',
    `fpzl_dm`       varchar(5) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '发票种类代码',
    `hzrq`          varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '汇总时间（例如：2019-01）',
    `spbm`          varchar(19) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '项目编码',
    `xmmc`          varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '项目名称',
    `invoice_num`   varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发票份数',
    `xmje`          varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '项目金额',
    `se`            varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '税额',
    `kphjje`        varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '价税合计金额',
    `create_time`   datetime(0)                                            NOT NULL COMMENT '创建时间',
    `complete_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '完成标识（1未完成，2已完成，3汇总失败）',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_item_req_nsrsbh` (`xhf_nsrsbh`, `hzrq`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '项目汇总统计表接口'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for invoice_quota
-- ----------------------------
DROP TABLE IF EXISTS `invoice_quota`;
CREATE TABLE `invoice_quota`
(
    `id`             varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键',
    `invoice_amount` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发票限额',
    `create_time`    datetime(0)                                            NULL DEFAULT NULL COMMENT '数据创建时间',
    `user_id`        varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '当前登录人id',
    `invoice_type`   varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '发票种类  51 电子发票开票限额 ； 2 普通发票开票限额；0 专用发票开票限额; 41 卷票',
    `taxpayer_code`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '税号',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_quota_nsrsbh` (`taxpayer_code`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '发票限额'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for invoice_taxrate_request
-- ----------------------------
DROP TABLE IF EXISTS `invoice_taxrate_request`;
CREATE TABLE `invoice_taxrate_request`
(
    `id`                     varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键',
    `company_id`             varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '企业id',
    `xhf_nsrsbh`             varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '销货方纳税人识别号',
    `fpzl_dm`                varchar(5) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '发票种类代码',
    `kplx`                   varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '开票类型',
    `report_period`          varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属期间',
    `hzrq`                   varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '汇总日期',
    `sl`                     varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '税率',
    `invoice_amount_pt`      varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '正数有效发票金额',
    `tax_amount_pt`          varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '正数有效发票税额',
    `total_amount_pt`        varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '正数有效价税合计',
    `invoice_amount_nt`      varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '负数有效发票金额',
    `tax_amount_nt`          varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '负数有效发票税额',
    `total_amount_nt`        varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '负数有效价税合计',
    `invoice_amount_pt_void` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '正数作废发票金额',
    `tax_amount_pt_void`     varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '正数作废发票税额',
    `total_amount_pt_void`   varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '正数作废价税合计',
    `invoice_amount_nt_void` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '负数作废发票金额',
    `tax_amount_nt_void`     varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '负数作废发票税额',
    `total_amount_nt_void`   varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '负数作废价税合计',
    `create_time`            datetime(0)                                            NOT NULL COMMENT '创建时间',
    `complete_flag`          varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '完成标识（1未完成，2已完成，3汇总失败）',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_taxrate_nsrsbh` (`xhf_nsrsbh`, `hzrq`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '税率汇总统计表接口'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for invoice_type_code_ext
-- ----------------------------
DROP TABLE IF EXISTS `invoice_type_code_ext`;
CREATE TABLE `invoice_type_code_ext`
(
    `id`                   varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL COMMENT '发票种类扩展表主键',
    `invoice_type_code_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '发票种类主表id',
    `fpzl_dm`              varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '发票种类代码（0.增值税专票 2 增值税普通纸质发票 51 增值税普通电子发票）',
    `fpzl_dm_mc`           varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '发票种类代码（0.增值税专票 2 增值税普通纸质发票 51 增值税普通电子发票）',
    `create_time`          datetime(0)                                                  NOT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_invoice_code__id` (`invoice_type_code_id`) USING BTREE COMMENT '发票种类父级id索引'
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for invoice_warning_info
-- ----------------------------
DROP TABLE IF EXISTS `invoice_warning_info`;
CREATE TABLE `invoice_warning_info`
(
    `id`             varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '预警表ID',
    `nsrsbh`         varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci       NULL DEFAULT NULL COMMENT '纳税人识别号',
    `xhf_mc`         varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci      NULL DEFAULT NULL COMMENT '销货方名称',
    `spn`            varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci       NULL DEFAULT NULL COMMENT '税盘号',
    `fjh`            varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci   NULL DEFAULT NULL COMMENT '分机号',
    `sp_mc`          varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci      NULL DEFAULT NULL COMMENT '税盘名称',
    `zdh`            varchar(50) CHARACTER SET latin1 COLLATE latin1_swedish_ci   NULL DEFAULT NULL COMMENT '终端编号',
    `fpdm`           varchar(30) CHARACTER SET latin1 COLLATE latin1_swedish_ci   NULL DEFAULT NULL COMMENT '发票代码',
    `qshm`           varchar(30) CHARACTER SET latin1 COLLATE latin1_swedish_ci   NULL DEFAULT NULL COMMENT '起始号码',
    `fpfs`           varchar(20) CHARACTER SET latin1 COLLATE latin1_swedish_ci   NULL DEFAULT NULL COMMENT '发票份数',
    `fpzl_dm`        varchar(3) CHARACTER SET latin1 COLLATE latin1_swedish_ci    NULL DEFAULT NULL COMMENT '发票种类代码 ',
    `warning_num`    varchar(10) CHARACTER SET latin1 COLLATE latin1_swedish_ci   NULL DEFAULT NULL COMMENT '预警张数',
    `cell_phone`     varchar(15) CHARACTER SET latin1 COLLATE latin1_swedish_ci   NULL DEFAULT NULL COMMENT '手机号',
    `mail`           varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci       NULL DEFAULT NULL COMMENT '邮箱',
    `update_date`    datetime(0)                                                  NULL DEFAULT NULL COMMENT '更新时间',
    `update_user_id` varchar(255) CHARACTER SET latin1 COLLATE latin1_swedish_ci  NULL DEFAULT NULL COMMENT '更新人',
    `email_num`      int(10)                                                      NULL DEFAULT 0 COMMENT '邮件发送次数',
    `warning_flag`   varchar(10) CHARACTER SET latin1 COLLATE latin1_swedish_ci   NULL DEFAULT NULL COMMENT '是否邮件预警,0不预警 1预警',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_warning_nsrsbh` (`nsrsbh`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = latin1
  COLLATE = latin1_swedish_ci COMMENT = '余票预警表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for kpfw_spbm
-- ----------------------------
DROP TABLE IF EXISTS `kpfw_spbm`;
CREATE TABLE `kpfw_spbm`
(
    `id`         int(11)                                                 NOT NULL AUTO_INCREMENT COMMENT 'id主键',
    `spbm`       varchar(19) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '商品编码',
    `spmc`       varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '货物和劳务名称',
    `zzssl`      varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '增值税税率',
    `hzx`        varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL COMMENT 'Y：是；N：否',
    `bbh`        double(20, 2)                                           NULL DEFAULT NULL COMMENT '版本号',
    `kyzt`       varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL COMMENT '可用状态',
    `zzstsgl`    varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '增值税特殊管理',
    `qysj`       varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '启用时间',
    `gdqjzsj`    varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '过渡期截止时间',
    `gxsj`       varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '更新时间',
    `updatetime` date                                                    NULL DEFAULT NULL COMMENT '系统入库时间',
    `spbmjc`     varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '商品编码简称',
    `byzd1`      varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备用字段1',
    `byzd2`      varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备用字段2',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `INDEX_SPBM` (`spbm`) USING BTREE COMMENT '商品编码'
) ENGINE = InnoDB
  AUTO_INCREMENT = 4362
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for order_batch_request
-- ----------------------------
DROP TABLE IF EXISTS `order_batch_request`;
CREATE TABLE `order_batch_request`
(
    `id`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '批量订单请求批次表id',
    `ddqqpch`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '订单请求批次号',
    `xhf_nsrsbh`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '销售方纳税人识别号',
    `kpfs`        varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '开票方式(0:自动开票;1:手动开票),默认为0',
    `sldid`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL COMMENT '受理点id',
    `kpjh`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL COMMENT '开票机号',
    `fpzldm`      varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '发票种类代码(0:专票 2:普票41:卷票51:电子票)',
    `sfcpy`       varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NULL DEFAULT NULL COMMENT '是否是成品油(0:非成品油;1:成品油)',
    `status`      varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '批次状态(0:未开票;1:开票中;2:开票成功;3:开票异常)',
    `message`     varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '返回信息',
    `kzzd`        varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '扩展字段',
    `create_time` datetime(0)                                                    NOT NULL COMMENT '申请单创建时间',
    `update_time` datetime(0)                                                    NOT NULL COMMENT '请求时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_order_batch_xhfnsrsbh` (`xhf_nsrsbh`) USING BTREE,
    INDEX `idx_order_batch_fqqpch` (`ddqqpch`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '订单请求批次表'
  ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for order_info
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info`
(
    `id`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '订单唯一id',
    `process_id`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '处理表id',
    `fpqqlsh`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '发票请求流水号',
    `ddh`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '订单号',
    `thdh`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '退货单号',
    `ddlx`        varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '订单类型（0:原始订单,1:拆分后订单,2:合并后订单,3:系统冲红订单）',
    `dsptbm`      varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL COMMENT '电商平台编码',
    `nsrsbh`      varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '纳税人识别号',
    `nsrmc`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '纳税人名称',
    `nsrdzdah`    varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '纳税人电子档案号',
    `swjg_dm`     varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '税务机构代码',
    `dkbz`        varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL COMMENT '代开标志（0：自开，1：代开）',
    `pydm`        varchar(6) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL COMMENT '票样代码:默认000001',
    `kpxm`        varchar(90) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '主要开票项目',
    `bbm_bbh`     varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '编码表版本号',
    `xhf_mc`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '销货方名称',
    `xhf_nsrsbh`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '销货方识别号',
    `xhf_dz`      varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '销货方地址',
    `xhf_dh`      varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '销货方电话',
    `xhf_yh`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '销货方银行',
    `xhf_zh`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '销货方账号',
    `ghf_qylx`    varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL COMMENT '购货方企业类型（01企业，02机关事业单位，03个人，04其他）',
    `ghf_sf`      varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '购货方省份',
    `ghf_id`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '购货方编码id',
    `ghf_mc`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '购货方名称',
    `ghf_nsrsbh`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '购货方识别号',
    `ghf_dz`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '购货方地址',
    `ghf_dh`      varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '购货方电话',
    `ghf_yh`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '购货方银行',
    `ghf_zh`      varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '购货方账号',
    `ghf_sj`      varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '购货方手机',
    `ghf_email`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '购货方邮箱',
    `hy_dm`       varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '行业代码',
    `hy_mc`       varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '行业名称',
    `kpr`         varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '开票人',
    `skr`         varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '收款人',
    `fhr`         varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '复核人',
    `ddrq`        datetime(0)                                                   NOT NULL COMMENT '订单日期',
    `kplx`        varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '开票类型（0：蓝票；1：红票）',
    `fpzl_dm`     varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '发票种类代码（0.增值税专票 2 增值税普通纸质发票 51 增值税普通电子发票）',
    `yfp_dm`      varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '原发票代码',
    `yfp_hm`      varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL COMMENT '原发票号码',
    `chyy`        varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '冲红原因',
    `tschbz`      varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL COMMENT '特殊冲红标志0正常冲红(电子发票) 1特殊冲红(冲红纸质等)',
    `czdm`        varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL COMMENT '操作代码10正票正常开具 11正票错票重开 20 退货折让红票、21 错票重开红票、22换票冲红',
    `qd_bz`       varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL COMMENT '0-普通发票;1-普通发票(清单);\r\n2-收购发票;3-收购发票(清单);\r\n4-成品油发票',
    `qd_xmmc`     varchar(90) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '清单发票项目名称，需要打印清单时对应发票票  面项目名称  清单标识（QD_BZ）为1时必  填。为 0 不进行处理。',
    `kphjje`      varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '价税合计金额',
    `hjbhsje`     varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '合计不含税金额',
    `hjse`        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '开票税额',
    `mdh`         varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '门店号',
    `ywlx`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '处理模式 0惠民 1 团购',
    `tqm`         varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '提取码',
    `bz`          varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
    `kpjh`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '开票机号',
    `sld`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '受理点',
    `byzd1`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备用字段1',
    `byzd2`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备用字段2(1:专票冲红已抵扣冲红失败，异常重新开票校验不校验发票代码、号码)',
    `byzd3`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备用字段3',
    `byzd4`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备用字段4',
    `byzd5`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备用字段5',
    `create_time` datetime(0)                                                   NOT NULL COMMENT '创建时间',
    `update_time` datetime(0)                                                   NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_order_info_process_id` (`process_id`) USING BTREE,
    UNIQUE INDEX `idx_order_info_fpqqlsh` (`fpqqlsh`) USING BTREE,
    INDEX `idx_order_infor_ddh` (`ddh`) USING BTREE,
    INDEX `idx_order_info_nsrsbh` (`nsrsbh`) USING BTREE,
    INDEX `idx_order_yfpdm_hm` (`yfp_dm`, `yfp_hm`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '订单信息主表'
  ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for order_invoice_info
-- ----------------------------
DROP TABLE IF EXISTS `order_invoice_info`;
CREATE TABLE `order_invoice_info`
(
    `id`                    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '订单处理表主键',
    `order_info_id`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '订单表id',
    `order_process_info_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '订单处理表id',
    `fpqqlsh`               varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '发票请求流水号',
    `kplsh`                 varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '开票流水号',
    `ddh`                   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '订单号',
    `mdh`                   varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL     DEFAULT NULL COMMENT '门店编号',
    `ghf_mc`                varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '购货方名称',
    `ghf_sj`                varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL     DEFAULT NULL COMMENT '购货方手机',
    `kphjje`                varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '开票合计金额',
    `hjbhsje`               varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL     DEFAULT NULL COMMENT '合计不含税金额',
    `kpse`                  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL     DEFAULT NULL COMMENT '开票税额',
    `kplx`                  varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '开票类型(0蓝,1红)',
    `kpzt`                  varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '开票状态,(0:初始化;1:开票中;2:开票成功;3:开票失败;)',
    `kprq`                  datetime(0)                                                    NULL     DEFAULT NULL COMMENT '开票日期',
    `ddlx`                  varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NULL     DEFAULT NULL COMMENT '订单类型（0:原始订单,1:拆分后订单,2:合并后订单,3:系统冲红订单,4:自动开票订单）',
    `fpdm`                  varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL     DEFAULT NULL COMMENT '发票代码',
    `fphm`                  varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NULL     DEFAULT NULL COMMENT '发票号码',
    `fpzl_dm`               varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL DEFAULT '' COMMENT '发票种类代码0.增值税专票 2 增值税普通纸质发票 51 增值税普通电子发票',
    `jym`                   varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL     DEFAULT NULL COMMENT '校验码',
    `kpr`                   varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL     DEFAULT NULL COMMENT '开票人',
    `fwm`                   varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '防伪码',
    `ewm`                   varchar(5000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '二维码',
    `jqbh`                  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL     DEFAULT NULL COMMENT '机器编号',
    `pdf_url`               varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT 'pdfurl地址',
    `ch_bz`                 varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NULL     DEFAULT NULL COMMENT '冲红标志(0:正常;1:全部冲红成功;2:全部冲红中;3:全部冲红失败;4:部分冲红成功;5:部分冲红中;6:部分冲红失败;(特殊说明:部分冲红只记录当前最后一次操作的记录))',
    `sykchje`               varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL     DEFAULT NULL COMMENT '剩余可冲红金额',
    `sykchbhsje`            varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL     DEFAULT NULL COMMENT ' 剩余可冲红不含税金额',
    `chsj`                  datetime(0)                                                    NULL     DEFAULT NULL COMMENT '冲红时间',
    `chyy`                  varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '冲红原因',
    `zf_bz`                 varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NULL     DEFAULT NULL COMMENT '作废标志(0:正常;1:已作废;2:作废中;3:作废失败)',
    `zfsj`                  datetime(0)                                                    NULL     DEFAULT NULL COMMENT '作废时间',
    `zfyy`                  varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '作废原因',
    `sbyy`                  varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '开票失败原因',
    `rz_zt`                 varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NULL     DEFAULT NULL COMMENT '认证状态',
    `sld_mc`                varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL     DEFAULT NULL,
    `sld`                   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL     DEFAULT NULL COMMENT '受理点',
    `fjh`                   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL     DEFAULT NULL COMMENT '分机号',
    `xhf_mc`                varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '销货方名称',
    `xhf_nsrsbh`            varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL     DEFAULT NULL COMMENT '销货方纳税人识别号',
    `qd_bz`                 varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NULL     DEFAULT NULL COMMENT '0 不带清单 1 带清单',
    `dyzt`                  varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NULL     DEFAULT NULL COMMENT '打印状态 0 未打印 1 已打印',
    `push_status`           varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL DEFAULT '0' COMMENT '推送状态,(0:未推送;1:推送成功;2:推送失败;)',
    `email_push_status`     varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NULL     DEFAULT '0' COMMENT '0 为发送邮件 1 已发送邮件',
    `hzxxbbh`               varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL     DEFAULT NULL COMMENT '红字信息表编号',
    `create_time`           datetime(0)                                                    NOT NULL,
    `update_time`           datetime(0)                                                    NOT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_order_invoice_order_id` (`order_info_id`) USING BTREE,
    UNIQUE INDEX `idx_order_invoice_process_id` (`order_process_info_id`) USING BTREE,
    UNIQUE INDEX `idx_order_invoice_fpqqlsh` (`fpqqlsh`) USING BTREE,
    UNIQUE INDEX `idx_order_invoice_kplsh` (`kplsh`) USING BTREE,
    INDEX `idx_order_invoice_xhfnsrsbh` (`xhf_nsrsbh`) USING BTREE,
    INDEX `idx_order_invoice_fpdmhm` (`fpdm`, `fphm`) USING BTREE,
    INDEX `idx_order_invoice_kprq` (`kprq`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '订单和发票关系表'
  ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for order_item_info
-- ----------------------------
DROP TABLE IF EXISTS `order_item_info`;
CREATE TABLE `order_item_info`
(
    `id`            varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '订单明细表主键',
    `order_info_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '订单主表id',
    `sphxh`         varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL COMMENT '商品行序号',
    `xmmc`          varchar(90) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '项目名称',
    `xmdw`          varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目单位',
    `ggxh`          varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '规格型号',
    `xmsl`          varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '项目数量',
    `hsbz`          varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '含税标志 0 不含税 1含税',
    `xmdj`          varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '项目单价',
    `fphxz`         varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '发票行性质 0正常商品行  1折扣行  2被折扣行',
    `spbm`          varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '商品编码',
    `zxbm`          varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '自行编码',
    `yhzcbs`        varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL COMMENT '优惠政策标识',
    `lslbs`         varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL COMMENT '零税率标识',
    `zzstsgl`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '增值税特殊管理',
    `kce`           varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '扣除额',
    `xmje`          varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '项目金额',
    `sl`            varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '税率',
    `se`            varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '税额',
    `wcje`          varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '尾差金额',
    `byzd1`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '备用字段1',
    `byzd2`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '备用字段2',
    `byzd3`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '备用字段3',
    `byzd4`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '备用字段4',
    `byzd5`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '备用字段5',
    `create_time`   datetime(0)                                                   NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_order_item_order_id` (`order_info_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '订单信息明细表'
  ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for order_orgin_extend_info
-- ----------------------------
DROP TABLE IF EXISTS `order_orgin_extend_info`;
CREATE TABLE `order_orgin_extend_info`
(
    `id`              varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `order_id`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `fpqqlsh`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `origin_order_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `origin_fpqqlsh`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `origin_ddh`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL,
    `create_time`     datetime(0)                                                  NOT NULL,
    `update_time`     datetime(0)                                                  NOT NULL,
    `status`          varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '0' COMMENT '0 有效 1 无效',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_order_orgin_extend_info_order_id` (`order_id`) USING BTREE,
    INDEX `idx_order_orgin_extend_info_origin_order_id` (`origin_order_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for order_process_info
-- ----------------------------
DROP TABLE IF EXISTS `order_process_info`;
CREATE TABLE `order_process_info`
(
    `id`            varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '订单处理表主键',
    `order_info_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '订单表id',
    `ddqqpch`       varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci         NULL     DEFAULT NULL COMMENT '订单请求批次号',
    `fpqqlsh`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '发票请求流水号',
    `ddh`           varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '订单号',
    `tqm`           varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL     DEFAULT NULL COMMENT '提取码',
    `kphjje`        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '开票合计金额',
    `hjbhsje`       varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL     DEFAULT NULL COMMENT '合计不含税金额',
    `kpse`          varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL     DEFAULT NULL COMMENT '开票税额',
    `fpzl_dm`       varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '发票种类代码:0专票  2 普票   51 电子发票',
    `ghf_mc`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '购买方名称',
    `ghf_nsrsbh`    varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL     DEFAULT NULL COMMENT '购货方识别号',
    `xhf_nsrsbh`    varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL     DEFAULT NULL COMMENT '销货方纳税人识别号',
    `xhf_mc`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '销货方名称',
    `kpxm`          varchar(90) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL     DEFAULT NULL COMMENT '主要开票项目',
    `ddcjsj`        datetime(0)                                                    NOT NULL COMMENT '订单创建时间',
    `ddlx`          varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL DEFAULT '' COMMENT '订单类型（0:原始订单,1:拆分后订单,2:合并后订单,3:系统冲红订单,4:自动开票订单）',
    `ddzt`          varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '订单状态（0:初始化;1:拆分后;2:合并后;3:待开具;4:开票中;5:开票成功;6.开票失败;7.冲红成功;8.冲红失败;9.冲红中;10,自动开票中,11,删除状态）',
    `ddly`          varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '订单来源(0:Excel导入;1:手工录入;2:api原始订单接口;3:自动开票录入数据;4:其他;5:静态码扫码开票;6:动态码扫码开票;)',
    `ywlx`          varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '业务类型(区分企业业务线),可以企业自定义',
    `ywlx_id`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL     DEFAULT NULL COMMENT '业务类型Id',
    `kpfs`          varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NULL     DEFAULT NULL COMMENT '企业开票方式(0:自动开票;1:手动开票;2:静态码开票;3:动态码开票),默认为0',
    `sbyy`          varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL     DEFAULT NULL COMMENT '失败原因',
    `order_status`  varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci    NOT NULL COMMENT '订单状态（0：有效；1：无效）',
    `create_time`   datetime(0)                                                    NOT NULL,
    `update_time`   datetime(0)                                                    NOT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_order_process_order_id` (`order_info_id`) USING BTREE,
    UNIQUE INDEX `idx_order_process_fpqqlsh` (`fpqqlsh`) USING BTREE,
    INDEX `idx_order_process_xhf_nsrsbh` (`xhf_nsrsbh`) USING BTREE,
    INDEX `idx_order_process_ddqqpch` (`ddqqpch`) USING BTREE,
    INDEX `idx_order_process_ddcjsj` (`ddcjsj`) USING BTREE,
    INDEX `idx_order_process_update_time` (`update_time`) USING BTREE,
    INDEX `idx_order_process_ddzt` (`ddzt`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '订单处理记录表'
  ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for order_process_info_ext
-- ----------------------------
DROP TABLE IF EXISTS `order_process_info_ext`;
CREATE TABLE `order_process_info_ext`
(
    `id`                      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单处理表主键',
    `order_process_info_id`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单处理表id',
    `parent_order_info_id`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '父订单表id',
    `parent_order_process_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '父订单处理表id',
    `status`                  varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '状态（0：有效；1：无效）',
    `create_time`             datetime(0)                                                  NOT NULL,
    `update_time`             datetime(0)                                                  NOT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_process_ext_processid` (`order_process_info_id`) USING BTREE,
    INDEX `idx_process_ext_p_processid` (`parent_order_process_id`) USING BTREE,
    INDEX `idx_process_ext_p_orderid` (`parent_order_info_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '订单处理记录扩展表'
  ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for order_qrcode_extend
-- ----------------------------
DROP TABLE IF EXISTS `order_qrcode_extend`;
CREATE TABLE `order_qrcode_extend`
(
    `id`                             varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '订单二维码扩展表主键id',
    `order_info_id`                  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '订单id 关联订单主表',
    `auth_order_id`                  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '公众号 授权订单号',
    `open_id`                        varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '公众号open_id',
    `union_id`                       varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '公众号 union_id',
    `quick_response_code_type`       varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL DEFAULT NULL COMMENT '二维码类型 (0 静态码；1 动态码)',
    `fpzl_dm`                        varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL DEFAULT NULL,
    `fpqqlsh`                        varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '发票请求流水号',
    `tqm`                            varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL,
    `ddh`                            varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '订单号',
    `kphjje`                         varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '开票合计金额',
    `xhf_mc`                         varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '销货方名称',
    `xhf_nsrsbh`                     varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '销货方纳税人识别号',
    `zfzt`                           varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL DEFAULT NULL COMMENT '作废状态( 0 未作废 1 已作废 ）',
    `card_status`                    varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '公众号插卡状态( 0 初始化状态 1 插卡成功 2 插卡失败)',
    `ewmzt`                          varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL DEFAULT NULL COMMENT '二维码状态 ( 0  未使用 1 已使用 ）',
    `quick_response_code_url`        varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '二维码短链接',
    `quick_response_code_valid_time` datetime(0)                                             NULL DEFAULT NULL COMMENT '订单失效时间',
    `create_time`                    datetime(0)                                             NOT NULL,
    `update_time`                    datetime(0)                                             NOT NULL COMMENT 'f',
    `data_status`                    varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL COMMENT '数据状态( 0 : 有效 1：无效 )',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `index_order_info_id` (`order_info_id`) USING BTREE,
    UNIQUE INDEX `index_fpqqlsh` (`fpqqlsh`) USING BTREE,
    INDEX `index_tqm` (`tqm`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '订单二维码 扩展表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for paper_invalid_invoice_info
-- ----------------------------
DROP TABLE IF EXISTS `paper_invalid_invoice_info`;
CREATE TABLE `paper_invalid_invoice_info`
(
    `id`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '纸票作废主键',
    `zfpch`       varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '作废批次号',
    `fpdm`        varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '发票代码',
    `fphm`        varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '发票号码',
    `sld`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '受理点',
    `fplx`        varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL COMMENT '发票类型',
    `zfyy`        varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '作废原因',
    `zf_bz`       varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL COMMENT '作废标志(0:已作废;1:作废失败)',
    `zfsj`        datetime(0)                                                   NOT NULL COMMENT '作废时间',
    `dyzt`        varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL COMMENT '打印状态（0 未打印 1 已打印）',
    `xhf_mc`      varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `xhf_nsrsbh`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '购货方纳税人识别号',
    `create_time` datetime(0)                                                   NOT NULL COMMENT '创建时间',
    `update_time` datetime(0)                                                   NOT NULL COMMENT '修改时间',
    `zflx`        varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL COMMENT '作废类型(0:空白发票作废；1:正数发票作废；2:负数发票作废；)',
    INDEX `idx_paper_invalid_xhfnsrsbh` (`xhf_nsrsbh`) USING BTREE,
    INDEX `idx_paper_invalid_fpdmfhm` (`fpdm`, `fphm`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '纸质发票作废表'
  ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for push_info
-- ----------------------------
DROP TABLE IF EXISTS `push_info`;
CREATE TABLE `push_info`
(
    `id`             varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '推送表ID',
    `nsrsbh`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '销方税号',
    `interface_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL     DEFAULT NULL COMMENT '接口类型（1:发票推送；2:作废状态推送;3:扫码开票;4:删除发票推送）',
    `version_ident`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL COMMENT '版本标识，区分新旧版本(v1,v2,v3)',
    `encrypt_code`   varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '0' COMMENT '加密标识(0:base64加密;1:3des加密)',
    `zip_code`       varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '0' COMMENT '压缩标识(0:不压缩;1:压缩)',
    `push_url`       varchar(400) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '推送企业的url',
    `status`         varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL DEFAULT '0' COMMENT '0 有效 1 无效',
    `byzd1`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL,
    `byzd2`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL     DEFAULT NULL,
    `create_time`    datetime(0)                                                   NOT NULL,
    `modify_time`    datetime(0)                                                   NOT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_push_nsrsbh` (`nsrsbh`, `interface_type`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '发票推送表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for quick_response_code_info
-- ----------------------------
DROP TABLE IF EXISTS `quick_response_code_info`;
CREATE TABLE `quick_response_code_info`
(
    `id`                             varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '二维码表主键',
    `quick_response_code_type`       varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '二维码类型(0:静态码;1:动态码)',
    `xhf_mc`                         varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '销货方名称',
    `xhf_nsrsbh`                     varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '销货方识别号',
    `xhf_dz`                         varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '销货方地址',
    `xhf_dh`                         varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '销货方电话',
    `xhf_yh`                         varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '销货方银行',
    `xhf_zh`                         varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '销货方账号',
    `ywlx`                           varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '业务类型(区分企业业务线),可以企业自定义',
    `ywlx_id`                        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '公众号union_id',
    `sld_mc`                         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '受理点名称',
    `sld`                            varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '受理点',
    `fjh`                            varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '分机号',
    `kpr`                            varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '开票人',
    `skr`                            varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '收款人',
    `fhr`                            varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '复核人',
    `ewmzt`                          varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '二维码状态 0 未使用  1 已使用',
    `tqm`                            varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '提取码,二维码唯一编码',
    `quick_response_code_url`        varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '二维码短链接',
    `quick_response_code_valid_time` datetime(0)                                                   NULL DEFAULT NULL COMMENT '二维码失效时间',
    `create_time`                    datetime(0)                                                   NOT NULL COMMENT '创建时间',
    `update_time`                    datetime(0)                                                   NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_quick_response_tqm` (`tqm`) USING BTREE COMMENT '提取码唯一索引',
    INDEX `idx_quick_response_nsrsbh` (`xhf_nsrsbh`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for quick_response_code_item_info
-- ----------------------------
DROP TABLE IF EXISTS `quick_response_code_item_info`;
CREATE TABLE `quick_response_code_item_info`
(
    `id`                          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '二维码明细表主键',
    `quick_response_code_info_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '二维码主表id',
    `sphxh`                       varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL COMMENT '商品行序号',
    `xmmc`                        varchar(90) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '项目名称',
    `xmdw`                        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目单位',
    `ggxh`                        varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '规格型号',
    `xmsl`                        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '项目数量',
    `hsbz`                        varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '含税标志 0 不含税 1含税',
    `xmdj`                        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '项目单价',
    `fphxz`                       varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '发票行性质 0正常商品行  1折扣行  2被折扣行',
    `spbm`                        varchar(19) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '商品编码',
    `zxbm`                        varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '自行编码',
    `yhzcbs`                      varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL COMMENT '优惠政策标识',
    `lslbs`                       varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NULL DEFAULT NULL COMMENT '零税率标识',
    `zzstsgl`                     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '增值税特殊管理',
    `kce`                         varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '扣除额',
    `xmje`                        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '项目金额',
    `sl`                          varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '税率',
    `se`                          varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '税额',
    `wcje`                        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '尾差金额',
    `byzd1`                       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '备用字段1',
    `byzd2`                       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '备用字段2',
    `byzd3`                       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '备用字段3',
    `byzd4`                       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '备用字段4',
    `byzd5`                       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '备用字段5',
    `create_time`                 datetime(0)                                                   NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_quick_response_item_infoid` (`quick_response_code_info_id`) USING BTREE COMMENT '主表id'
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci
  ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for rule_split
-- ----------------------------
DROP TABLE IF EXISTS `rule_split`;
CREATE TABLE `rule_split`
(
    `id`              varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '超限额拆分ID',
    `taxpayer_code`   varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '纳税人识别号',
    `user_id`         varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '当前登录人id',
    `rule_split_type` varchar(5) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '0 : 保金额 ;  1 : 保数量  ; 2 : 保单价',
    `create_time`     datetime(0)                                            NULL DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime(0)                                            NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '超限额拆分表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for saler_warning
-- ----------------------------
DROP TABLE IF EXISTS `saler_warning`;
CREATE TABLE `saler_warning`
(
    `id`           varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '预警id',
    `tax_code`     varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '税号',
    `waring_email` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '预警邮箱（多个邮箱以逗号分隔）',
    `warning_flag` varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL DEFAULT NULL COMMENT '预警开关（0,关闭；1，开启）',
    `create_by`    varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '创建人',
    `create_time`  datetime(0)                                             NULL DEFAULT NULL COMMENT '创建时间',
    `update_time`  datetime(0)                                             NULL DEFAULT NULL COMMENT '更新时间',
    `attr1`        varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL,
    `attr2`        varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '发票预警表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for special_invoice_reversal
-- ----------------------------
DROP TABLE IF EXISTS `special_invoice_reversal`;
CREATE TABLE `special_invoice_reversal`
(
    `id`                        varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci        NOT NULL COMMENT '专票红字申请单ID',
    `type`                      varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci         NOT NULL COMMENT '申请单类型0:正常;1:成品油-销售数量变更2:成品油-销售金额变更;3成品油-其他',
    `code`                      varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '编号',
    `reason`                    varchar(15) CHARACTER SET utf8 COLLATE utf8_general_ci        NOT NULL COMMENT '申请原因: 1100000000:购方申请 已抵扣;1010000000:购方申请  未抵扣;0000000100 因发票有误购买方拒收的因开票有误等原因尚未交付的',
    `invoice_code`              varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL DEFAULT NULL COMMENT '原蓝票发票代码',
    `invoice_no`                varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci         NULL DEFAULT NULL COMMENT '原蓝票发票号码',
    `invoice_type`              varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci        NOT NULL COMMENT '发票种类',
    `invoice_category`          varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci         NOT NULL COMMENT '发票类别 0:专票;2:普票;41:卷票;51:电票',
    `invoice_date`              varchar(19) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL DEFAULT NULL COMMENT '发票日期',
    `seller_name`               varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL COMMENT '销方名称',
    `seller_taxpayer_code`      varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci        NOT NULL COMMENT '销方纳税人识别号 ',
    `buyer_name`                varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL COMMENT '购方名称',
    `buyer_taxpayer_code`       varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci        NOT NULL COMMENT '购方纳税人识别号',
    `total_amount`              varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL DEFAULT NULL COMMENT '合计金额',
    `tax_rate`                  varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL DEFAULT NULL COMMENT '税率',
    `tax_amount`                varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL DEFAULT NULL COMMENT '税额',
    `total_tax_amount`          varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL DEFAULT NULL COMMENT '含税总金额',
    `submit_code`               varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL DEFAULT NULL COMMENT '上报编号',
    `submit_status`             varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci        NOT NULL COMMENT '上报状态 TZD0000:审核通过;TZD0500:未上传;TZD0061:重复上传;TZD0071:待查证;TZD0072:已核销，待查证;TZD0073:已核销,查证未通过,待处理;TZD0074:已核销;TZD0075:核销后激活;TZD0076:已核销,查证未通过,处理中;TZD0077:已核销,查证未通过,已处理;TZD0078:核销未通过，待处理;TZD0079:核销未通过，处理中;TZD0080:核销未通过，已处理;TZD0082:已撤销;TZD0083:已作废',
    `access_point_id`           varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL DEFAULT NULL COMMENT '受理点ID',
    `extension_num`             varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL DEFAULT NULL COMMENT '分机号',
    `taxpayer_code`             varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci        NOT NULL COMMENT '申请纳税人识别号',
    `status`                    varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci         NOT NULL COMMENT '状态 0 未开票 1 开票中 2 开票成功 3 开票异常',
    `creator_id`                varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci        NOT NULL COMMENT '创建人ID',
    `creator_name`              varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL COMMENT '创建人姓名',
    `create_time`               datetime(0)                                                   NOT NULL COMMENT '创建时间',
    `editor_id`                 varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci        NOT NULL COMMENT '编辑人ID',
    `editor_name`               varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci       NOT NULL COMMENT '编辑人姓名',
    `edit_time`                 datetime(0)                                                   NOT NULL COMMENT '编辑时间',
    `buyer_type`                varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci        NOT NULL COMMENT '购方类型',
    `access_point_name`         varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci       NULL DEFAULT NULL COMMENT '受理点名称',
    `invoice_access_point_id`   varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL DEFAULT NULL COMMENT '开票受理点ID',
    `invoice_access_point_name` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci       NULL DEFAULT NULL COMMENT '开票受理点名称',
    `invoice_extension_num`     varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL DEFAULT NULL COMMENT '开票分机号',
    `drawer_name`               varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL DEFAULT NULL COMMENT '开票人',
    `submit_status_desc`        varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci       NULL DEFAULT NULL COMMENT '转义标识',
    `data_status`               varchar(5) CHARACTER SET utf8 COLLATE utf8_general_ci         NULL DEFAULT '0' COMMENT ' 0：有效；1：无效',
    `reviewer_name`             varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL DEFAULT NULL COMMENT '复核人',
    `payee_name`                varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL DEFAULT NULL COMMENT '收款人',
    `xhf_dz`                    varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '销货方地址',
    `xhf_dh`                    varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '销货方电话',
    `xhf_yh`                    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '销货方银行',
    `xhf_zh`                    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '销货方账号',
    `ghf_dz`                    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '购货方地址',
    `ghf_dh`                    varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '购货方电话',
    `ghf_yh`                    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '购货方银行',
    `ghf_zh`                    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '购货方账号',
    `chyy`                      varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci       NULL DEFAULT NULL COMMENT '冲红原因',
    `red_invoice_code`          varchar(12) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL DEFAULT NULL COMMENT '当前红票发票号码',
    `red_invoice_no`            varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci         NULL DEFAULT NULL COMMENT '当前红票发票号码',
    `agent_name`                varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL DEFAULT NULL COMMENT '经办人',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_special_code` (`code`) USING BTREE,
    INDEX `idx_special_submitcode` (`submit_status`) USING BTREE,
    INDEX `idx_special_nsrsbh` (`taxpayer_code`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '专票红字申请单主表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for special_invoice_reversal_item
-- ----------------------------
DROP TABLE IF EXISTS `special_invoice_reversal_item`;
CREATE TABLE `special_invoice_reversal_item`
(
    `id`                          varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '专票红字申请单明细ID',
    `special_invoice_reversal_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '红色申请单ID',
    `code`                        varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '编码',
    `name`                        varchar(90) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '名称',
    `spec`                        varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '规格',
    `unit`                        varchar(22) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '单位',
    `quantity`                    varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '数量',
    `unit_price`                  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '单价',
    `amount`                      varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '金额',
    `tax_rate`                    varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '税率',
    `tax_amount`                  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '税额',
    `tax_flag`                    varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL COMMENT '含税标志 0:不含税;1:含税',
    `seq_num`                     varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL COMMENT '序号',
    `creator_id`                  varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '创建人ID',
    `creator_name`                varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建人姓名',
    `create_time`                 datetime(0)                                             NOT NULL COMMENT '创建时间',
    `editor_id`                   varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '编辑人ID',
    `editor_name`                 varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '编辑人姓名',
    `edit_time`                   datetime(0)                                             NULL DEFAULT NULL COMMENT '编辑时间',
    `is_special`                  varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL COMMENT '是否享受优惠政策 0:否;1:是',
    `special_type`                varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '优惠政策类型',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `INDEX_SIR_ID` (`special_invoice_reversal_id`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '专票红字申请单明细表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_dictionary
-- ----------------------------
DROP TABLE IF EXISTS `sys_dictionary`;
CREATE TABLE `sys_dictionary`
(
    `id`        varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL,
    `name`      varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '字典名称',
    `type`      varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '字典类型',
    `code`      varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '字典码',
    `value`     varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '字典值',
    `order_num` int(11)                                                  NULL DEFAULT 0 COMMENT '排序',
    `remark`    varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '备注',
    `del_flag`  tinyint(4)                                               NULL DEFAULT 0 COMMENT '删除标记  -1：已删除  0：正常',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `idx_dictionary_type` (`type`, `code`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '数据字典表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_nsr_queue
-- ----------------------------
DROP TABLE IF EXISTS `sys_nsr_queue`;
CREATE TABLE `sys_nsr_queue`
(
    `id`              varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '队列信息表主键',
    `nsrsbh`          varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '税号',
    `queue_prefix`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NULL DEFAULT NULL COMMENT '队列前缀',
    `queue_name`      varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '队列名称',
    `status`          varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '队列状态(0:有效;1:无效)',
    `listener_size`   varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '监听数量,必须大于0,并且为整数',
    `listener_status` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   NOT NULL COMMENT '监听状态(0:有效;1:无效)',
    `create_time`     datetime(0)                                                   NULL DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime(0)                                                   NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_nsr_queue_nsrsbh` (`nsrsbh`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT = '消息队列表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for t_sksb
-- ----------------------------
DROP TABLE IF EXISTS `t_sksb`;
CREATE TABLE `t_sksb`
(
    `id`             varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '税控设备ID',
    `xhf_nsrsbh`     varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '销货方识别号',
    `xhf_mc`         varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '销货方名称',
    `group_id`       varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL DEFAULT NULL COMMENT '所属企业ID',
    `group_name`     varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci       NULL DEFAULT NULL COMMENT '所属企业名称',
    `sksb_code`      varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL DEFAULT NULL COMMENT '税控设备ID  000:未配置 001:C48 002:A9 004:税控盘托管 005:百望服务器 006:本地税控盘 007:本地金税盘 008:百望服务器active-x',
    `sksb_name`      varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci       NULL DEFAULT NULL COMMENT '税控设备名称',
    `sksb_type`      varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci       NULL DEFAULT NULL COMMENT '税控设备型号',
    `link_time`      datetime(0)                                                   NOT NULL COMMENT '关联时间',
    `bz`             varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
    `create_user_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL DEFAULT NULL COMMENT '创建人ID',
    `update_user_id` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci        NULL DEFAULT NULL COMMENT '修改人id',
    `create_time`    datetime(0)                                                   NOT NULL COMMENT '创建时间',
    `update_time`    datetime(0)                                                   NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_sksb_nsrsbh` (`xhf_nsrsbh`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '税控设备表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tax_class_code
-- ----------------------------
DROP TABLE IF EXISTS `tax_class_code`;
CREATE TABLE `tax_class_code`
(
    `id`            varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci   NOT NULL DEFAULT '' COMMENT '税收分类编码ID',
    `spbm`          varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL     DEFAULT NULL COMMENT '商品编码',
    `spmc`          varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL COMMENT '商品名称',
    `spjc`          varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL     DEFAULT NULL COMMENT '商品简称',
    `sm`            varchar(3000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '说明',
    `zzssl`         varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL     DEFAULT NULL COMMENT '增值税税率',
    `gjz`           varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '关键字',
    `hzx`           varchar(5) CHARACTER SET utf8 COLLATE utf8_general_ci    NULL     DEFAULT NULL COMMENT '汇总项 Y 是 N 不是',
    `kyzt`          varchar(5) CHARACTER SET utf8 COLLATE utf8_general_ci    NULL     DEFAULT NULL COMMENT '可用状态 Y 可用 N不可用',
    `zzstsgl`       varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '增值税特殊管理',
    `zzszcyj`       varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '增值税政策依据',
    `zzstsnrdm`     varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '增值税特殊内容代码',
    `xfsgl`         varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '消费税管理',
    `xfszcyj`       varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '消费税政策依据',
    `xfstsnrdm`     varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '消费税特殊内容代码',
    `tjjbm`         varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '统计局编码',
    `hgjcksppm`     text CHARACTER SET utf8 COLLATE utf8_general_ci          NULL COMMENT '海关进出口商品名称',
    `pid`           bigint(30)                                               NULL     DEFAULT NULL COMMENT 'pid',
    `yhzcmc`        varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL     DEFAULT NULL COMMENT '优惠政策名称',
    `sl`            varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL     DEFAULT NULL COMMENT '税率',
    `create_time`   datetime(0)                                              NULL     DEFAULT NULL COMMENT '创建时间',
    `bbh`           varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci   NULL     DEFAULT NULL COMMENT '编码表版本号',
    `cpy`           varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci    NULL     DEFAULT NULL COMMENT '是否成品油(Y:成品油;N:非成品油)',
    `enabling_time` datetime(0)                                              NULL     DEFAULT NULL COMMENT '税编启用时间',
    `update_time`   datetime(0)                                              NULL     DEFAULT NULL COMMENT '税编更新时间',
    `mslx`          varchar(1) CHARACTER SET utf8 COLLATE utf8_general_ci    NULL     DEFAULT NULL COMMENT '免税类型（0：正常税率；1-出口免税率或其他免税优惠政策；2-不征增值税；3-普通零税率）',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_tax_code_spbm` (`spbm`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '税收分类编码表'
  ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tax_controller_info
-- ----------------------------
DROP TABLE IF EXISTS `tax_controller_info`;
CREATE TABLE `tax_controller_info`
(
    `id`     varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '税盘ID',
    `spmc`   varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '税盘名称',
    `nsrsbh` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '纳税人识别号',
    `nsrmc`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '纳税人名称',
    `fjh`    varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '分机号',
    `jqbh`   varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '机器编号',
    `fpzlDm` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '发票种类代码',
    `qysj`   varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '启用时间',
    `jspzt`  varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '金税盘状态',
    `ssrq`   varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '锁死日期',
    `scbsrq` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '上次报税日期',
    `csqsrq` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '上次报税日期',
    `sfdbsq` varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '是否到锁死期',
    `dzkpxe` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '单张开票限额',
    `lxsx`   varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '离线时限',
    `lxsyje` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '离线剩余金额',
    `jspsz`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '金税盘时钟',
    `bszl`   varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '报税资料',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_controller_nsrsbh` (`nsrsbh`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '税盘信息表'
  ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
