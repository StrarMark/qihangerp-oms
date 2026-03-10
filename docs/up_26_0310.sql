CREATE TABLE `ai_conversation_history` (
                                           `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id，自增',
                                           `user_id` bigint(20) NOT NULL COMMENT '用户id',
                                           `session_id` varchar(36) NOT NULL COMMENT '会话id',
                                           `role` varchar(20) NOT NULL COMMENT '角色：user或assistant',
                                           `content` text NOT NULL COMMENT '消息内容',
                                           `timestamp` bigint(20) NOT NULL COMMENT '消息时间戳',
                                           `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                           PRIMARY KEY (`id`),
                                           KEY `idx_session_id` (`session_id`),
                                           KEY `idx_user_id` (`user_id`),
                                           KEY `idx_timestamp` (`timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI聊天历史表';