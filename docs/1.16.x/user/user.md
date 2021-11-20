# End user/commands
## Create an own island
If users are allowed to create islands with their own command, you can use `/skyblock create <name> <players>`. 
This will create a team, and the given players will be added to the new team. If no players are given, the user who 
executes the command will be added to the team. If no name is given, a random name will be generated.

## Rename team island
You can rename a/your team by using `/skyblock team <new name> <team name>`. `<team name>` is optional. Can be used by 
any team member. Users with permission level 2 could also edit other teams' name.

## Modify spawns
If enabled in the config, you can modify spawns from your team. You can add them with `/skyblock team spawns add <pos>`. 
If no position is given, the current position will be used. Remove them with `/skyblock team spawns remove <pos>`. Same 
as before: position is optional. For users with permission level 2: `/skyblock team spawns reset <team>` will reset the 
spawn points to the default ones. To add the spawns, you need to be within the range specified in the config.

## Teleporting back to home island
If home command is enabled in the config, you can teleport back to your teams' island with `/skyblock home`.

## Teleporting to spawn island
If teleporting to spawn is enabled in the config, you can teleport to spawn island with `/skyblock spawn`.
