#!/usr/bin/env python3
import os
import re

def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    original_content = content

    # 更新 package 声明
    # cn.qihangerp.open.controller -> cn.qihangerp.erp.controller.open
    # cn.qihangerp.erp.controller -> cn.qihangerp.erp.controller.ai (对于ai-agent的文件)

    # 更新 open-api 的 package
    content = re.sub(
        r'package cn\.qihangerp\.open\.controller;',
        r'package cn.qihangerp.erp.controller.open;',
        content
    )

    # 更新 ai-agent 的 package
    content = re.sub(
        r'package cn\.qihangerp\.erp\.controller;',
        r'package cn.qihangerp.erp.controller.ai;',
        content
    )

    # 更新 RequestMapping - 加上 /api/open-api 前缀
    if '@RequestMapping(' in content and '/api/open-api' not in content and 'package cn.qihangerp.erp.controller.open;' in content:
        content = re.sub(
            r'@RequestMapping\("(.*?)"\)',
            r'@RequestMapping("/api/open-api\1")',
            content
        )

    # 更新 RequestMapping - 加上 /api/ai-agent 前缀
    if '@RequestMapping(' in content and '/api/ai-agent' not in content and 'package cn.qihangerp.erp.controller.ai;' in content:
        content = re.sub(
            r'@RequestMapping\("(.*?)"\)',
            r'@RequestMapping("/api/ai-agent\1")',
            content
        )

    if content != original_content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        return True
    return False

def main():
    base_dir = '/Users/qihang/Projects/qihang-erp-open/erp-api/src/main/java/cn/qihangerp/erp/controller'

    # 处理 open 目录
    open_dir = os.path.join(base_dir, 'open')
    if os.path.exists(open_dir):
        for file in os.listdir(open_dir):
            if file.endswith('.java'):
                filepath = os.path.join(open_dir, file)
                if process_file(filepath):
                    print(f'✓ 已更新(open): {file}')

    # 处理 ai 目录
    ai_dir = os.path.join(base_dir, 'ai')
    if os.path.exists(ai_dir):
        for file in os.listdir(ai_dir):
            if file.endswith('.java'):
                filepath = os.path.join(ai_dir, file)
                if process_file(filepath):
                    print(f'✓ 已更新(ai): {file}')

    print('\n完成！')

if __name__ == '__main__':
    main()
