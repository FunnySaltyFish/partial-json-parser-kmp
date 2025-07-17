import re
import sys
from pathlib import Path

def extract_versions():
    """从构建文件中提取 Kotlin 版本号和项目版本号"""
    try:
        # 1. 读取 build.gradle.kts，提取 kotlin 版本号
        build_gradle_path = Path("partial-json-parser/build.gradle.kts")
        if not build_gradle_path.exists():
            raise FileNotFoundError(f"构建文件不存在: {build_gradle_path}")
            
        build_gradle = build_gradle_path.read_text(encoding="utf-8")
        kotlin_version_match = re.search(r'kotlin\("multiplatform"\)\s+version\s+"([\d\.]+)"', build_gradle)
        kotlin_version = kotlin_version_match.group(1) if kotlin_version_match else None
        
        if not kotlin_version:
            raise ValueError("无法从 build.gradle.kts 中提取 Kotlin 版本号")

        # 2. 提取 version = Config.libVersion
        config_version_match = re.search(r'version\s*=\s*Config\.libVersion', build_gradle)
        if not config_version_match:
            raise ValueError("build.gradle.kts 中未找到 Config.libVersion 引用")

        # 读取 Config.kt
        config_kt_path = Path("buildSrc/src/main/kotlin/Config.kt")
        if not config_kt_path.exists():
            raise FileNotFoundError(f"配置文件不存在: {config_kt_path}")
            
        config_kt = config_kt_path.read_text(encoding="utf-8")
        lib_version_match = re.search(r'const\s+val\s+libVersion\s*=\s*"([^"]+)"', config_kt)
        lib_version = lib_version_match.group(1) if lib_version_match else None
        
        if not lib_version:
            raise ValueError("无法从 Config.kt 中提取项目版本号")

        return kotlin_version, lib_version
        
    except Exception as e:
        print(f"错误: 提取版本号失败 - {e}", file=sys.stderr)
        sys.exit(1)

def update_readme_version(readme_path_str: str, kotlin_version: str, lib_version: str):
    """更新 README 文件中的版本号"""
    try:
        readme_path = Path(readme_path_str)
        if not readme_path.exists():
            print(f"警告: README 文件不存在: {readme_path_str}")
            return False
            
        original_content = readme_path.read_text(encoding="utf-8")
        updated_content = original_content

        # 替换 Kotlin 版本徽章
        kotlin_pattern = r'(Kotlin-)[\d\.]+(-[A-F0-9]+?\?logo=kotlin)'
        kotlin_replacement = rf'\g<1>{kotlin_version}\g<2>'
        updated_content = re.sub(kotlin_pattern, kotlin_replacement, updated_content)

        # 替换 implementation 代码块里的版本号
        impl_pattern = r'(implementation\("io\.github\.funnysaltyfish:partial-json-parser:)[^"]+("\))'
        impl_replacement = rf'\g<1>{lib_version}\g<2>'
        updated_content = re.sub(impl_pattern, impl_replacement, updated_content)

        # 检查是否有变化
        if original_content == updated_content:
            print(f"ℹ️  {readme_path_str} 无需更新")
            return False
        else:
            readme_path.write_text(updated_content, encoding="utf-8")
            print(f"✅ {readme_path_str} 更新完成")
            return True
            
    except Exception as e:
        print(f"错误: 更新 {readme_path_str} 失败 - {e}", file=sys.stderr)
        return False

def main():
    """主函数"""
    print("🚀 开始更新 README 版本号...")
    
    # 提取版本号
    kotlin_version, lib_version = extract_versions()
    print(f"📝 Kotlin 版本号: {kotlin_version}")
    print(f"📝 项目版本号: {lib_version}")

    # 更新 README 文件
    updated_files = []
    for readme_path in ("README.md", "README_CN.md"):
        if update_readme_version(readme_path, kotlin_version, lib_version):
            updated_files.append(readme_path)

    if updated_files:
        print(f"✨ 成功更新了 {len(updated_files)} 个文件: {', '.join(updated_files)}")
    else:
        print("ℹ️  所有文件都已是最新版本")

if __name__ == "__main__":
    main()
