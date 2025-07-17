import re
from pathlib import Path

# 1. 读取 build.gradle.kts，提取 kotlin 版本号
build_gradle = Path("partial-json-parser/build.gradle.kts").read_text(encoding="utf-8")
kotlin_version = re.search(r'kotlin\("multiplatform"\)\s+version\s+"([\d\.]+)"', build_gradle)
kotlin_version = kotlin_version.group(1) if kotlin_version else None

# 2. 提取 version = Config.libVersion
config_version = re.search(r'version\s*=\s*Config\.libVersion', build_gradle)
if config_version:
    # 读取 Config.kt
    config_kt = Path("buildSrc/src/main/kotlin/Config.kt").read_text(encoding="utf-8")
    lib_version = re.search(r'val\s+libVersion\s*=\s*"([^"]+)"', config_kt)
    lib_version = lib_version.group(1) if lib_version else None
else:
    lib_version = None

if kotlin_version is None or lib_version is None:
    raise ValueError("Kotlin 版本号或项目版本号提取失败")

def update_readme_version(readme_path_str: str, kotlin_version: str, lib_version: str):
    # 3. 读取 README.md
    readme_path = Path(readme_path_str)
    readme = readme_path.read_text(encoding="utf-8")

    # 4. 替换 Kotlin 版本徽章
    if kotlin_version:
        readme = re.sub(
            r'(Kotlin-)[\d\.]+(-[A-F0-9]+?\?logo=kotlin)',
            rf'\g<1>{kotlin_version}\g<2>',
            readme
        )

    # 5. 替换 implementation 代码块里的版本号
    if lib_version:
        readme = re.sub(
            r'(implementation\("io\.github\.funnysaltyfish:partial-json-parser:)[^"]+("\))',
            rf'\g<1>{lib_version}\g<2>',
            readme
        )

    # 6. 保存 README.md
    readme_path.write_text(readme, encoding="utf-8")
    print(f"{readme_path_str} 更新完成")

print(f"Kotlin 版本号: {kotlin_version}")
print(f"项目版本号: {lib_version}")

for path in ("README.md", "README_CN.md"):
    update_readme_version(path, kotlin_version, lib_version)
