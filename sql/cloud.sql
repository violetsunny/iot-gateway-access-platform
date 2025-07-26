-- 请在此输入SQL，以分号结尾，仅支持DML和DDL语句，查询语句请使用SQL查询功能。
CREATE TABLE `cloud_docking_auth_resp` (
                                           `id` varchar(32) COLLATE utf8mb4_bin NOT NULL,
                                           `host_id` varchar(64) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '宿主ID',
                                           `access_key` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL,
                                           `access_ref` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL,
                                           `params_type` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL,
                                           `access_prefix` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL,
                                           `expire_time` int(11) DEFAULT NULL,
                                           `expire_type` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL,
                                           `expire_key` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL,
                                           PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE `cloud_docking_metadata` (
                                          `id` varchar(32) COLLATE utf8mb4_bin NOT NULL,
                                          `host_id` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '宿主ID',
                                          `code` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL,
                                          `name` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                          `path_value` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                          `data_type` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
                                          `is_list` int(11) DEFAULT NULL,
                                          PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE `cloud_docking_params` (
                                        `id` varchar(32) NOT NULL,
                                        `host_id` varchar(64) NOT NULL COMMENT '宿主ID',
                                        `type` varchar(32) DEFAULT NULL COMMENT '参数分类 认证参数Auth,拉去数据参数PullData',
                                        `param_key` varchar(255) NOT NULL COMMENT '请求参数名',
                                        `param_value` varchar(255) NOT NULL COMMENT '请求参数值',
                                        `param_type` varchar(64) NOT NULL COMMENT '请求参数类型，header,form,body,path',
                                        `prod_id` varchar(32) DEFAULT NULL COMMENT '所属产品ID，PullData需要',
                                        `group` varchar(32) DEFAULT '' COMMENT '分组',
                                        PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_bin;

-------------20231012----------------
ALTER TABLE `cloud_docking_params`
    CHANGE COLUMN `group` `req_group`  varchar(32) NULL DEFAULT '' COMMENT '请求分组' AFTER `prod_id`,
    ADD COLUMN `data_code`  varchar(64) NOT NULL DEFAULT '' AFTER `host_id`;
ALTER TABLE `cloud_docking_metadata`
    ADD COLUMN `data_code`  varchar(64) NOT NULL DEFAULT '' AFTER `host_id`;

ALTER TABLE `cloud_docking_auth_resp`
    ADD INDEX `idx_code` (`host_id`) USING BTREE;
ALTER TABLE `cloud_docking_params`
    ADD INDEX `idx_code` (`host_id`, `data_code`) USING BTREE;
ALTER TABLE `cloud_docking_metadata`
    ADD INDEX `idx_code` (`host_id`, `data_code`) USING BTREE;

CREATE TABLE `cloud_docking_base` (
                                      `id` varchar(32) NOT NULL,
                                      `name` varchar(255) NOT NULL COMMENT '名称',
                                      `code` varchar(64) NOT NULL COMMENT '编码',
                                      `base_url` varchar(255) DEFAULT NULL,
                                      `state` varchar(10) DEFAULT NULL COMMENT '启用状态',
                                      `create_time` datetime DEFAULT NULL,
                                      PRIMARY KEY (`id`),
                                      KEY `idx_code` (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE `cloud_docking_auth` (
                                      `id` varchar(32) NOT NULL,
                                      `host_id` varchar(64) NOT NULL,
                                      `request_url` varchar(255) NOT NULL,
                                      `request_method` varchar(32) NOT NULL,
                                      `request_type` varchar(32) DEFAULT NULL,
                                      `root_path` varchar(64) DEFAULT NULL,
                                      PRIMARY KEY (`id`),
                                      KEY `idx_code` (`host_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE = utf8mb4_bin;

CREATE TABLE `cloud_docking_data` (
                                      `id` varchar(32) NOT NULL,
                                      `host_id` varchar(64) NOT NULL,
                                      `data_code` varchar(64) NOT NULL DEFAULT '' COMMENT '请求code',
                                      `request_url` varchar(255) DEFAULT NULL,
                                      `request_type` varchar(32) NOT NULL DEFAULT '' COMMENT '请求类型',
                                      `request_method` varchar(32) DEFAULT NULL,
                                      `root_path` varchar(64) DEFAULT NULL,
                                      `split` int(11) DEFAULT NULL,
                                      `req_limit` int(11) DEFAULT NULL,
                                      PRIMARY KEY (`id`),
                                      KEY `idx_code` (`host_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE = utf8mb4_bin;

INSERT INTO `cloud_docking_base` (`id`, `name`, `code`, `base_url`, `state`, `create_time`) VALUES ('1001', '水培菜物联接入', 'yunyangaiot', 'http://api.yunyangaiot.com', 'enabled', '2023-10-10 19:58:18');
INSERT INTO `cloud_docking_auth` (`id`, `host_id`, `request_url`, `request_method`, `request_type`, `root_path`) VALUES ('1001', 'yunyangaiot', '/auth/oauth/login', 'POST', 'body', 'data');
INSERT INTO `cloud_docking_auth_resp` (`id`, `host_id`, `access_key`, `access_ref`, `params_type`, `access_prefix`, `expire_type`, `expire_key`) VALUES ('1001', 'yunyangaiot', 'Authorization', 'access_token', 'Header', 'token_type', 'REF', 'expires_in');
INSERT INTO `cloud_docking_data` (`id`, `host_id`, `data_code`, `request_url`, `request_type`, `request_method`, `root_path`, `split`, `req_limit`) VALUES ('1001', 'yunyangaiot', 'latest', '/device/cacheData/latest/${ie}', 'Json', 'GET', 'data', '0', '10');
INSERT INTO `cloud_docking_params` (`id`, `host_id`, `data_code`, `type`, `param_key`, `param_value`, `param_type`, `prod_id`, `req_group`) VALUES ('1001', 'yunyangaiot', '', 'Auth', 'username', '17731613080', 'body', '', '1');
INSERT INTO `cloud_docking_params` (`id`, `host_id`, `data_code`, `type`, `param_key`, `param_value`, `param_type`, `prod_id`, `req_group`) VALUES ('1002', 'yunyangaiot', '', 'Auth', 'password', 'e10adc3949ba59abbe56e057f20f883e', 'body', '', '1');
INSERT INTO `cloud_docking_params` (`id`, `host_id`, `data_code`, `type`, `param_key`, `param_value`, `param_type`, `prod_id`, `req_group`) VALUES ('1003', 'yunyangaiot', 'latest', 'PullData', 'ie', 'YBF2152F001B91008C', 'params', '', '1');
INSERT INTO `cloud_docking_params` (`id`, `host_id`, `data_code`, `type`, `param_key`, `param_value`, `param_type`, `prod_id`, `req_group`) VALUES ('1004', 'yunyangaiot', 'latest', 'PullData', 'ie', 'YBF2152F004B68008F', 'params', '', '2');
INSERT INTO `cloud_docking_params` (`id`, `host_id`, `data_code`, `type`, `param_key`, `param_value`, `param_type`, `prod_id`, `req_group`) VALUES ('1005', 'yunyangaiot', 'latest', 'PullData', 'ie', 'YBF2152F000BD8007F', 'params', '', '3');
INSERT INTO `cloud_docking_params` (`id`, `host_id`, `data_code`, `type`, `param_key`, `param_value`, `param_type`, `prod_id`, `req_group`) VALUES ('1006', 'yunyangaiot', 'latest', 'PullData', 'ie', 'YBF2152F006B19008E', 'params', '', '4');
INSERT INTO `cloud_docking_params` (`id`, `host_id`, `data_code`, `type`, `param_key`, `param_value`, `param_type`, `prod_id`, `req_group`) VALUES ('1007', 'yunyangaiot', 'latest', 'PullData', 'ie', 'YBF2152F0020D00087', 'params', '', '5');
INSERT INTO `cloud_docking_params` (`id`, `host_id`, `data_code`, `type`, `param_key`, `param_value`, `param_type`, `prod_id`, `req_group`) VALUES ('1008', 'yunyangaiot', 'latest', 'PullData', 'ie', 'YBF2152F000CFD0091', 'params', '', '6');
INSERT INTO `cloud_docking_params` (`id`, `host_id`, `data_code`, `type`, `param_key`, `param_value`, `param_type`, `prod_id`, `req_group`) VALUES ('1009', 'yunyangaiot', 'latest', 'PullData', 'ie', 'YBF2152F0086B10084', 'params', '', '7');
INSERT INTO `cloud_docking_params` (`id`, `host_id`, `data_code`, `type`, `param_key`, `param_value`, `param_type`, `prod_id`, `req_group`) VALUES ('1010', 'yunyangaiot', 'latest', 'PullData', 'ie', 'YBF2152F0083860081', 'params', '', '8');
INSERT INTO `cloud_docking_params` (`id`, `host_id`, `data_code`, `type`, `param_key`, `param_value`, `param_type`, `prod_id`, `req_group`) VALUES ('1011', 'yunyangaiot', 'latest', 'PullData', 'ie', 'YBF2152F0026350092', 'params', '', '9');
INSERT INTO `cloud_docking_params` (`id`, `host_id`, `data_code`, `type`, `param_key`, `param_value`, `param_type`, `prod_id`, `req_group`) VALUES ('1012', 'yunyangaiot', 'latest', 'PullData', 'ie', 'YBF2152F0159150093', 'params', '', '10');
INSERT INTO `cloud_docking_params` (`id`, `host_id`, `data_code`, `type`, `param_key`, `param_value`, `param_type`, `prod_id`, `req_group`) VALUES ('1013', 'yunyangaiot', 'latest', 'PullData', 'ie', 'YBF2152F015F2E0090', 'params', '', '11');
INSERT INTO `cloud_docking_params` (`id`, `host_id`, `data_code`, `type`, `param_key`, `param_value`, `param_type`, `prod_id`, `req_group`) VALUES ('1014', 'yunyangaiot', 'latest', 'PullData', 'ie', 'YBF2152F00F0150089', 'params', '', '12');
INSERT INTO `cloud_docking_params` (`id`, `host_id`, `data_code`, `type`, `param_key`, `param_value`, `param_type`, `prod_id`, `req_group`) VALUES ('1015', 'yunyangaiot', 'latest', 'PullData', 'ie', 'YBF2152F00787A0094', 'params', '', '13');
INSERT INTO `cloud_docking_params` (`id`, `host_id`, `data_code`, `type`, `param_key`, `param_value`, `param_type`, `prod_id`, `req_group`) VALUES ('1016', 'yunyangaiot', 'latest', 'PullData', 'ie', 'YBF2152F004E120095', 'params', '', '14');
INSERT INTO `cloud_docking_params` (`id`, `host_id`, `data_code`, `type`, `param_key`, `param_value`, `param_type`, `prod_id`, `req_group`) VALUES ('1017', 'yunyangaiot', 'latest', 'PullData', 'ie', 'YBF2152F001DDB0085', 'params', '', '15');
INSERT INTO `cloud_docking_params` (`id`, `host_id`, `data_code`, `type`, `param_key`, `param_value`, `param_type`, `prod_id`, `req_group`) VALUES ('1018', 'yunyangaiot', 'latest', 'PullData', 'ie', 'YBF2152F007FC50083', 'params', '', '16');
INSERT INTO `cloud_docking_params` (`id`, `host_id`, `data_code`, `type`, `param_key`, `param_value`, `param_type`, `prod_id`, `req_group`) VALUES ('1019', 'yunyangaiot', 'latest', 'PullData', 'ie', 'YBF2152F00E1920088', 'params', '', '17');
INSERT INTO `cloud_docking_params` (`id`, `host_id`, `data_code`, `type`, `param_key`, `param_value`, `param_type`, `prod_id`, `req_group`) VALUES ('1020', 'yunyangaiot', 'latest', 'PullData', 'ie', 'YBF2152F008C5B0082', 'params', '', '18');
INSERT INTO `cloud_docking_metadata` (`id`, `host_id`, `data_code`, `code`, `name`, `path_value`, `data_type`, `is_list`) VALUES ('1001', 'yunyangaiot', 'latest', 'deviceId', '设备编号', '$.ie', 'String','0');
INSERT INTO `cloud_docking_metadata` (`id`, `host_id`, `data_code`, `code`, `name`, `path_value`, `data_type`, `is_list`) VALUES ('1002', 'yunyangaiot', 'latest', 'timestamp', '时间', '$.onlineTimeStr', 'String','0');
INSERT INTO `cloud_docking_metadata` (`id`, `host_id`, `data_code`, `code`, `name`, `path_value`, `data_type`, `is_list`) VALUES ('1003', 'yunyangaiot', 'latest', 'ECwaterQuality', '水质ec', '$.data.wec', 'Float','0');
INSERT INTO `cloud_docking_metadata` (`id`, `host_id`, `data_code`, `code`, `name`, `path_value`, `data_type`, `is_list`) VALUES ('1004', 'yunyangaiot', 'latest', 'H', '空气湿度', '$.data.ah', 'Float','0');
INSERT INTO `cloud_docking_metadata` (`id`, `host_id`, `data_code`, `code`, `name`, `path_value`, `data_type`, `is_list`) VALUES ('1005', 'yunyangaiot', 'latest', 'PHwaterQuality', '水质ph', '$.data.wph', 'Float','0');
INSERT INTO `cloud_docking_metadata` (`id`, `host_id`, `data_code`, `code`, `name`, `path_value`, `data_type`, `is_list`) VALUES ('1006', 'yunyangaiot', 'latest', 'Tair', '空气温度', '$.data.at', 'Float','0');
INSERT INTO `cloud_docking_metadata` (`id`, `host_id`, `data_code`, `code`, `name`, `path_value`, `data_type`, `is_list`) VALUES ('1007', 'yunyangaiot', 'latest', 'Online', '在线状态', '$.online', 'Boolean','0');

