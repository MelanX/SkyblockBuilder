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
            isFileLoaded = false;
            updateButtonState();
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
    
    let isTemplatesFileLoaded = false;
    let fileContent = {};

    function handleTemplatesFile(file) {
        const reader = new FileReader();

        reader.onload = function(e) {
            const contents = e.target.result;
            const cleanedContents = removeComments(contents);

            const json = JSON.parse(cleanedContents);
            const spawn = json.spawn;
            const templates = json.templates;
            const spawns = json.spawns;
            const surroundingBlocks = json.surroundingBlocks;

            const newSpawns = {}
            Object.keys(spawns).forEach(function (key) {
                let matchingTemplates = templates.filter(function (template) {
                    return template.spawns === key && template.direction;
                });
                const directions = {}
                matchingTemplates.forEach(function (template) {
                    directions[template.direction] = template;
                });
                const directionsCount = Object.keys(directions).length;
                const allDirections = ['north', 'east', 'south', 'west'];
                newSpawns[key] = {};
                if (directionsCount === 0) {
                    newSpawns[key]['north'] = [];
                    newSpawns[key]['east'] = [];
                    newSpawns[key]['south'] = spawns[key];
                    newSpawns[key]['west'] = [];
                } else if (directionsCount === 1) {
                    const obj = {};
                    allDirections.forEach(function (direction) {
                        if (direction === Object.keys(directions)[0]) {
                            obj[direction] = spawns[key];
                        } else {
                            obj[direction] = [];
                        }
                    });
                    newSpawns[key] = obj;
                } else {
                    Object.keys(directions).forEach(function (direction) {
                        const obj = {};
                        allDirections.forEach(function (dir) {
                            if (dir === direction) {
                                obj[dir] = spawns[key];
                            } else {
                                obj[dir] = [];
                            }
                        });
                        newSpawns[`${key}_${direction}`] = obj;
                        directions[direction]['spawns'] = `${key}_${direction}`;
                    });
                }
            });

            fileContent['spawns'] = newSpawns;

            if (spawn) {
                if (spawn.offset) {
                    const offsetY = spawn.offsetY ? spawn.offsetY : 0;
                    spawn.offset = [spawn.offset[0], offsetY, spawn.offset[1]];
                }
                fileContent['spawn'] = spawn;
            }

            for (let i = 0; i < templates.length; i++) {
                let template = templates[i];
                if (template.offset) {
                    const offsetY = template.offsetY ? template.offsetY : 0;
                    template.offset = [template.offset[0], offsetY, template.offset[1]];
                }
                if (template.direction) {
                    delete template.direction;
                }
            }
            fileContent['templates'] = templates;

            if (surroundingBlocks) {
                fileContent['surroundingBlocks'] = surroundingBlocks;
            }

            isTemplatesFileLoaded = true;
            updateTemplatesButtonState();
        };

        reader.readAsText(file);
    }

    function createAndDownloadTemplatesFile() {
        let blob = new Blob([JSON.stringify(fileContent)]);

        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = "templates.json5";
        link.click();
        isTemplatesFileLoaded = false;
        updateTemplatesButtonState();
    }

    function handleTemplatesDrop(event) {
        event.preventDefault();
        const file = event.dataTransfer.files[0];
        handleTemplatesFile(file);
    }

    function handleDragOverTemplates(event) {
        event.preventDefault();
    }

    function updateTemplatesButtonState() {
        const button = document.getElementById("templates-download-button");
        button.disabled = !isTemplatesFileLoaded;
    }
</script>
<style>
    .drop-area {
        width: 250px;
        height: 150px;
        border: 2px dashed #ccc;
        border-radius: 5px;
        text-align: center;
        padding: 25px;
        font-size: 18px;
        margin: 10px;
    }

    .download-button {
        width: 165px;
        font-size: 18px;
        background-color: #4CAF50;
        color: white;
        padding: 10px 24px;
        border-radius: 6px;
        margin: 0px 50px;
        cursor: pointer;
    }

    .download-button:disabled {
        opacity: 0.6;
        cursor: not-allowed;
    }
    
    .container {
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 20px;
    }
</style>
"""


def main():
    with open('build_site/1.20.x/1.19.x_to_1.20.x/index.html', 'r', encoding='utf-8') as f:
        file = f.read()

    lines = file.splitlines()
    newlines = []
    done = False
    for line in lines:
        newlines.append(line)
        if not done and "<head>" in line:
            newlines.append(SCRIPT)
            done = True

    with open('build_site/1.20.x/1.19.x_to_1.20.x/index.html', 'w', encoding='utf-8') as f:
        for line in newlines:
            f.write(line + '\n')


if __name__ == "__main__":
    main()
