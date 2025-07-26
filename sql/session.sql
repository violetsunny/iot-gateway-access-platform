CREATE TABLE `device_session` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键(自增处理)',
  `server_id` varchar(64) NOT NULL COMMENT '服务节点',
  `transport` varchar(32) NOT NULL DEFAULT '' COMMENT '传输协议',
  `session_id` varchar(64) NOT NULL DEFAULT '' COMMENT '会话id',
  `device_id` varchar(64) NOT NULL DEFAULT '' COMMENT '设备id',
  `last_ping_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次心跳时间',
  `connect_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '连接时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态：0有效 1无效',
  `remark` varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_server_id` (`server_id`) USING BTREE,
  KEY `idx_device_id` (`device_id`) USING BTREE,
  KEY `idx_session_id` (`session_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin ROW_FORMAT=COMPACT  COMMENT='会话管理';