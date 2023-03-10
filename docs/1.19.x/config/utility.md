# Utility
## Create own team
This allows players to [create their own team](../user/user.md#create-an-own-island).

## Self managing
Allows the player to use several commands. This includes these commands:

- [Leaving the team](../packdev/admin.md#leaving-a-team)
- [Accepting/declining join requests](../user/join-team.md#accepting-requests)
- [Accepting/declining invitations](../user/invitations.md#accepting-invitations)
- [Sending join requests](../user/join-team.md#sending-a-join-request)
- [Invite other players to join](../user/invitations.md#inviting-users)
- [Modifying spawns](#modify-spawns)

## Spawns
### Modify spawns
This allows players to [modify spawns](../user/user.md#modify-spawns). Needs [self-managing](#self-managing) to be
enabled.

### Range
The distance from the island center where the users should be able to add spawns.

## Teleports
### Visits
Allows the player to [visit other islands](../user/visiting.md).

### Home
Allows the player to [teleport back home](../user/user.md#teleporting-back-to-home-island). Has a cooldown for each 
player.

### Spawn
Allows the player to [teleport to the spawn island](../user/user.md#teleporting-to-spawn-island). Has a cooldown for 
each player.

### Cross Dimension Teleportation
Allows the player to teleport (see above) from another dimension, for example the main world is overworld, players will
be able to also teleport from the nether to the spawn, their island, or even visit others. Setting this to false forces
them to be in the overworld (if that's the main dimension).

### Dimension teleportation permission
A list of dimensions where you can't use teleportation commands. If you set `allow_list` to true, these commands are
allowed in these dimensions only.

This is a resource list. Read more about it [here 🔗](https://moddingx.org/libx/org/moddingx/libx/util/data/ResourceList.html#use_resource_lists_in_configs).