# Managing teams
## Operators only
Only an operator with permission level 2 or higher can change anything in the `/skyblock manage` category.

## Creating teams
Use the `/skyblock manage teams create <name>` command to generate a team with the given name. If no name is provided, 
a random name will be generated.

Alternatively (good for servers), you can use `/skyblock manage teams createAndJoin` to create the team and join
it. To use it on a server, you should put it in a command block which can be triggered by the user and runs this 
command:

`/execute as @p run skyblock manage teams createAndJoin`

## Deleting teams
Use the `/skyblock manage teams delete <name>` to delete the team with the given name.

**WARNING**! This cannot be undone. The island will still exist, but you can't re-bind a new team to that island. If users 
are in the team, they will be teleported to spawn after dropping all their items.

## Clearing teams
Because teams can be empty, you can "clear" all islands. If you use `/skyblock manage teams clear <name>`, all empty 
teams will be deleted as in [Deleting teams](#deleting-teams).

If you provide a team name, all players from this team will be removed and teleported to spawn island.

## Leaving a team
An operator need to remove players from a team. For that, they need to use `/skyblock manage kickPlayer <player>`. 
The removed player will be teleported back to spawn after dropping all the items in the inventory.

If you're not op and want to leave your team, you can simply type in `/skyblock leave`. You will drop all your items 
and be teleported to spawn.