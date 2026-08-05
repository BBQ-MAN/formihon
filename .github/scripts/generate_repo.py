import json
import os
import hashlib
import glob
import subprocess
import shutil

def get_source_id(name, lang, version_id):
    key = f"{name}/{lang}/{version_id}".encode('utf-8')
    md5_hash = hashlib.md5(key).digest()[:8]
    source_id = int.from_bytes(md5_hash, byteorder='big') & 0x7fffffffffffffff
    return source_id

def get_apk_sha256(apk_path):
    try:
        android_home = os.environ.get('ANDROID_HOME', '/usr/local/lib/android/sdk')
        build_tools_dir = os.path.join(android_home, 'build-tools')
        versions = sorted(os.listdir(build_tools_dir), reverse=True)
        apksigner_path = os.path.join(build_tools_dir, versions[0], 'apksigner')
        
        result = subprocess.run([apksigner_path, 'verify', '--print-certs', apk_path], capture_output=True, text=True, check=True)
        for line in result.stdout.splitlines():
            if "SHA-256 digest:" in line:
                return line.split("SHA-256 digest:")[1].strip().lower()
    except Exception as e:
        print(f"Failed to get signature: {e}")
    return ""

def main():
    ext_name = "K만화 (KMana)"
    pkg_name = "eu.kanade.tachiyomi.extension.ko.kmana"
    lang = "ko"
    version_code = 1
    version_name = "1.0"
    base_url = "https://kmana10.net"
    
    apk_files = glob.glob("build/outputs/apk/release/*.apk")
    if not apk_files:
        print("No APK found in build/outputs/apk/release/")
        exit(1)
    
    apk_path = apk_files[0]
    apk_filename = os.path.basename(apk_path)
    
    signing_key = get_apk_sha256(apk_path)
    
    repo_data = {
        "name": "KMana Extension Repository",
        "badgeLabel": "KMana",
        "signingKey": signing_key,
        "extensionList": {
            "extensions": [
                {
                    "name": ext_name,
                    "packageName": pkg_name,
                    "resources": {
                        "apkUrl": f"https://raw.githubusercontent.com/BBQ-MAN/formihon/gh-pages/{apk_filename}",
                    },
                    "extensionLib": "6e0c96cea8",
                    "versionCode": version_code,
                    "versionName": version_name,
                    "contentWarning": "SAFE",
                    "sources": [
                        {
                            "id": get_source_id(ext_name, lang, 1),
                            "name": ext_name,
                            "language": lang,
                            "homeUrl": base_url
                        }
                    ]
                }
            ]
        }
    }
    
    os.makedirs("repo", exist_ok=True)
    with open("repo/index.min.json", "w", encoding="utf-8") as f:
        json.dump(repo_data, f, ensure_ascii=False, separators=(',', ':'))
    with open("repo/index.json", "w", encoding="utf-8") as f:
        json.dump(repo_data, f, ensure_ascii=False, indent=2)
        
    shutil.copy2(apk_path, f"repo/{apk_filename}")
    print(f"Successfully generated repo in 'repo' directory with {apk_filename}")

if __name__ == "__main__":
    main()
