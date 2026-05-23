
import os

controller_dir = '/Users/qihang/Projects/qihang-erp-open/erp-api/src/main/java/cn/qihangerp/erp/controller'

for filename in os.listdir(controller_dir):
    if filename.endswith('.java'):
        filepath = os.path.join(controller_dir, filename)
        with open(filepath, 'r', encoding='utf-8') as f:
            lines = f.readlines()
        
        updated_lines = []
        for line in lines:
            if '@RequestMapping' in line:
                if '/api/erp-api' not in line:
                    # 替换 @RequestMapping("/xxx") 为 @RequestMapping("/api/erp-api/xxx")
                    if '@RequestMapping("/' in line:
                        line = line.replace('@RequestMapping("/', '@RequestMapping("/api/erp-api/')
                    # 替换 @RequestMapping(value = "/xxx") 为 @RequestMapping(value = "/api/erp-api/xxx")
                    elif '@RequestMapping(value = "/' in line:
                        line = line.replace('@RequestMapping(value = "/', '@RequestMapping(value = "/api/erp-api/')
            updated_lines.append(line)
        
        with open(filepath, 'w', encoding='utf-8') as f:
            f.writelines(updated_lines)
        
        print(f'Updated {filename}')

print('\nAll done!')
