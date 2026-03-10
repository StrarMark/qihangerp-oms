-- 用户AI角色表
CREATE TABLE `ai_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id，自增',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `role_name` varchar(255) NOT NULL COMMENT '角色名称',
  `description` varchar(500) DEFAULT NULL COMMENT '角色描述',
  `system_prompt` text NOT NULL COMMENT '角色系统提示词',
  `is_default` int(11) NOT NULL DEFAULT '0' COMMENT '是否为默认角色：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_default` (`is_default`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户AI角色表';

-- 添加默认角色示例数据
INSERT INTO `ai_user_role` (`user_id`, `role_name`, `description`, `system_prompt`, `is_default`) VALUES
(1, '默认ERP助手', '默认的ERP智能助手，用于查询订单、商品、库存等信息', '你是一个智能ERP助手，名叫启航助手，专门为电商企业提供服务。\n你的职责是帮助用户查询订单、商品、库存等信息，以及执行其他ERP相关操作。\n你可以使用提供的工具来获取实时数据，工具返回的结果要以用户友好的方式呈现。\n如果用户的请求超出你的能力范围，你应该礼貌地拒绝。\n回答问题时要简洁明了，避免使用过于技术化的术语。', 1);