
#!/usr/bin/env python3
import os
import re

controller_dir = '/Users/qihang/Projects/qihang-erp-open/erp-api/src/main/java/cn/qihangerp/erp/controller'

for filename in os.listdir(controller_dir):
    if filename.endswith('.java'):
        filepath = os.path.join(controller_dir, filename)
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # 替换 @RequestMapping("/xxx") 为 @RequestMapping("/api/erp-api/xxx")
        # 匹配模式: @RequestMapping(value = "/xxx", ...) 或 @RequestMapping("/xxx")
        content, count = re.subn(r'@RequestMapping\s*\(\s*(value\s*=\s*)?"/([^"]+)"', r'@RequestMapping(\1"/api/erp-api/\2"', content)
        
        if count &gt; 0:
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f'Updated {filename}: {count} replacement(s)')
        else:
            print(f'Skipped {filename}: no @RequestMapping found')

print('\nDone!')
