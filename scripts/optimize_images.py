import hashlib
import os
import subprocess


def find_images(path):
    return [os.path.join(root, file) for root, dirs, files in os.walk(path) for file in files if file.endswith('.png')]


def read_hashes():
    hashes = {}
    if not os.path.exists('scripts/images_cache.txt'):
        return hashes

    with open('scripts/images_cache.txt', 'r', encoding='utf-8') as f:
        lines = f.read().strip().split('\n')
        for line in lines:
            parts = line.split('|')
            hashes.update({parts[0]: parts[1]})

    return hashes


def check_hashes(hashes, files):
    filtered_files = []
    for file in files:
        file_hash = hash_from_file(file)
        if file not in hashes or file_hash != hashes[file]:
            filtered_files.append(file)
        else:
            print(f'Skipping {file}')

    return filtered_files


def update_hashes(hashes, files):
    for file in files:
        hashes.update({file: hash_from_file(file)})

    return hashes


def write_new_hashes_file(hashes):
    s = ''
    for file in hashes:
        s += file
        s += '|'
        s += hashes[file]
        s += '\n'

    with open('scripts/images_cache.txt', 'w', encoding='utf-8') as f:
        f.write(s)


def hash_from_file(file):
    with open(file, 'rb') as f:
        return hashlib.sha512(f.read()).hexdigest()


def main():
    hashes = read_hashes()
    files = find_images('docs')
    files = check_hashes(hashes, files)
    for file in files:
        print(f'Optimize {file}')
        subprocess.run(['optipng', '-o9', file, '-quiet'])
    hashes = update_hashes(hashes, files)
    write_new_hashes_file(hashes)


if __name__ == "__main__":
    main()
