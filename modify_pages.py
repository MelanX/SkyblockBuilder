SCRIPT = """
<script src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.7.1/jszip.min.js"></script>
<script>
    let isFileLoaded = false;
    let dimensions, inventory, spawn, structures, permissions, world;
    
    function handleFile(file) {
        const reader = new FileReader();
    
        reader.onload = function(e) {
            const contents = e.target.result;
            const cleanedContents = removeComments(contents);
            const parsedContents = JSON.parse(cleanedContents);
    
            dimensions = parsedContents.Dimensions;
            inventory = parsedContents.Inventory;
            spawn = parsedContents.Spawn;
            structures = parsedContents.Structures;
            permissions = parsedContents.Utility;
            world = parsedContents.World;
    
            isFileLoaded = true;
            updateButtonState();
        };
    
        reader.readAsText(file);
    }
    
    function removeComments(contents) {
        const lines = contents.split('\\n');
        const cleanedLines = [];
    
        for (let i = 0; i < lines.length; i++) {
            const line = lines[i].trim();
            if (!line.startsWith('//')) {
                cleanedLines.push(line);
            }
        }
    
        return cleanedLines.join('\\n');
    }
    
    function createAndDownloadFiles() {
        const zip = new JSZip();
    
        zip.file("dimensions.json5", JSON.stringify(dimensions));
        zip.file("inventory.json5", JSON.stringify(inventory));
        zip.file("spawn.json5", JSON.stringify(spawn));
        zip.file("structures.json5", JSON.stringify(structures));
        zip.file("permissions.json5", JSON.stringify(permissions));
        zip.file("world.json5", JSON.stringify(world));
    
        zip.generateAsync({ type: "blob" }).then(function(content) {
            const link = document.createElement('a');
            link.href = URL.createObjectURL(content);
            link.download = "data.zip";
            link.click();
        });
    }
    
    function handleDrop(event) {
        event.preventDefault();
        const file = event.dataTransfer.files[0];
        handleFile(file);
    }
    
    function handleDragOver(event) {
        event.preventDefault();
    }
    
    function updateButtonState() {
        const button = document.getElementById("download-button");
        button.disabled = !isFileLoaded;
    }
</script>
<style>
    #drop-area {
        width: 300px;
        height: 150px;
        border: 2px dashed #ccc;
        border-radius: 5px;
        text-align: center;
        padding: 25px;
        font-size: 18px;
    }

    #download-button {
        width: 165px;
        font-size: 18px;
        background-color: #4CAF50;
        color: white;
        padding: 10px 24px;
        border-radius: 6px;
        margin: 0px 67.5px;
        cursor: pointer;
    }

    #download-button:disabled {
        opacity: 0.6;
        cursor: not-allowed;
    }
</style>
"""


def main():
    with open('site/1.20.x/1.19.x_to_1.20.x/index.html', 'r', encoding='utf-8') as f:
        file = f.read()

    lines = file.splitlines()
    newlines = []
    done = False
    for line in lines:
        newlines.append(line)
        if not done and "<head>" in line:
            newlines.append(SCRIPT)
            done = True

    with open('site/1.20.x/1.19.x_to_1.20.x/index.html', 'w', encoding='utf-8') as f:
        for line in newlines:
            f.write(line + '\n')


if __name__ == "__main__":
    main()
