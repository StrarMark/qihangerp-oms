#!/usr/bin/env python3
import os
import re

def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    original_content = content

    # 更新 package 声明
    # cn.qihangerp.oms.dou.controller -> cn.qihangerp.erp.controller.oms.dou
    # cn.qihangerp.oms.kwai.controller -> cn.qihangerp.erp.controller.oms.kwai
    # cn.qihangerp.oms.wei.controller -> cn.qihangerp.erp.controller.oms.wei
    # cn.qihangerp.oms.jd.controller -> cn.qihangerp.erp.controller.oms.jd
    # cn.qihangerp.oms.pdd.controller -> cn.qihangerp.erp.controller.oms.pdd
    # cn.qihangerp.oms.tao.controller -> cn.qihangerp.erp.controller.oms.tao
    # cn.qihangerp.oms.controller -> cn.qihangerp.erp.controller.oms

    content = re.sub(
        r'package cn\.qihangerp\.oms\.(\w+)\.controller;',
        r'package cn.qihangerp.erp.controller.oms.\1;',
        content
    )

    content = re.sub(
        r'package cn\.qihangerp\.oms\.controller;',
        r'package cn.qihangerp.erp.controller.oms;',
        content
    )

    # 更新 RequestMapping - 加上 /api/oms-api 前缀
    # 但要避免重复添加
    if '@RequestMapping(' in content and '/api/oms-api' not in content:
        content = re.sub(
            r'@RequestMapping\("(.*?)"\)',
            r'@RequestMapping("/api/oms-api\1")',
            content
        )

    if content != original_content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        return True
    return False

def main():
    oms_dir = '/Users/qihang/Projects/qihang-erp-open/erp-api/src/main/java/cn/qihangerp/erp/controller/oms'

    modified_count = 0
    for root, dirs, files in os.walk(oms_dir):
        for file in files:
            if file.endswith('.java'):
                filepath = os.path.join(root, file)
                if process_file(filepath):
                    modified_count += 1
                    print(f'✓ 已更新: {os.path.relpath(filepath, oms_dir)}')

    print(f'\n完成！共更新 {modified_count} 个文件')

if __name__ == '__main__':
    main()
