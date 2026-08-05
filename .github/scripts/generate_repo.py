import json
import os
import hashlib
import glob

def get_source_id(name, lang, version_id):
    key = f"{name}/{lang}/{version_id}".encode('utf-8')
    md5_hash = hashlib.md5(key).digest()[:8]
    source_id = int.from_bytes(md5_hash, byteorder='big') & 0x7fffffffffffffff
    return str(source_id)

def main():
    # Hardcoded values for KMana
    ext_name = "K만화 (KMana)"
    pkg_name = "eu.kanade.tachiyomi.extension.ko.kmana"
    lang = "ko"
    version_code = 1
    version_name = "1.0"
    base_url = "https://kmana10.net"
    
    # Find the built APK
    apk_files = glob.glob("build/outputs/apk/release/*.apk")
    if not apk_files:
        print("No APK found in build/outputs/apk/release/")
        exit(1)
    
    apk_path = apk_files[0]
    apk_filename = os.path.basename(apk_path)
    
    # Generate index
    repo_data = [
        {
            "name": ext_name,
            "pkg": pkg_name,
            "apk": apk_filename,
            "lang": lang,
            "code": version_code,
            "version": version_name,
            "nsfw": 0,
            "hasReadme": 0,
            "hasChangelog": 0,
            "sources": [
                {
                    "name": ext_name,
                    "id": get_source_id(ext_name, lang, 1),
                    "lang": lang,
                    "baseUrl": base_url,
                    "versionId": 1
                }
            ]
        }
    ]
    
    # Write index.min.json
    os.makedirs("repo", exist_ok=True)
    with open("repo/index.min.json", "w", encoding="utf-8") as f:
        json.dump(repo_data, f, ensure_ascii=False, separators=(',', ':'))
        
    # Copy APK to repo folder
    import shutil
    shutil.copy2(apk_path, f"repo/{apk_filename}")
    
    print(f"Successfully generated repo in 'repo' directory with {apk_filename}")

if __name__ == "__main__":
    main()
