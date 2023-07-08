# Adding compat as mod dev
## Disable team management
You can use the API to disable team management without the config needs to be changed. Additionally, you can disable
that players will be teleported to spawn when joining the world.

For more information about this, look
[here 🔗](https://github.com/MelanX/SkyblockBuilder/blob/1.20.x/src/main/java/de/melanx/skyblockbuilder/api/SkyblockBuilderAPI.java).
You can call this in the main constructor of your mod.

## Custom Forge Events
Skyblock Builder fires several events which you can subscribe like normal Forge events.
These are all events:

- [Create Team](https://github.com/MelanX/SkyblockBuilder/blob/1.20.x/src/main/java/de/melanx/skyblockbuilder/events/SkyblockCreateTeamEvent.java)
- [Invite Player](https://github.com/MelanX/SkyblockBuilder/blob/1.20.x/src/main/java/de/melanx/skyblockbuilder/events/SkyblockInvitationEvent.java#L53)
- [Accept Invitation](https://github.com/MelanX/SkyblockBuilder/blob/1.20.x/src/main/java/de/melanx/skyblockbuilder/events/SkyblockInvitationEvent.java#L74)
- [Decline Invitation](https://github.com/MelanX/SkyblockBuilder/blob/1.20.x/src/main/java/de/melanx/skyblockbuilder/events/SkyblockInvitationEvent.java#L84)
- [Send Join Request](https://github.com/MelanX/SkyblockBuilder/blob/1.20.x/src/main/java/de/melanx/skyblockbuilder/events/SkyblockJoinRequestEvent.java#L51)
- [Accept Join Request](https://github.com/MelanX/SkyblockBuilder/blob/1.20.x/src/main/java/de/melanx/skyblockbuilder/events/SkyblockJoinRequestEvent.java#L61)
- [Deny Join Request](https://github.com/MelanX/SkyblockBuilder/blob/1.20.x/src/main/java/de/melanx/skyblockbuilder/events/SkyblockJoinRequestEvent.java#L81)
- [Toggle Visitation Status](https://github.com/MelanX/SkyblockBuilder/blob/1.20.x/src/main/java/de/melanx/skyblockbuilder/events/SkyblockManageTeamEvent.java#L50)
- [Toggle Join Request Status](https://github.com/MelanX/SkyblockBuilder/blob/1.20.x/src/main/java/de/melanx/skyblockbuilder/events/SkyblockManageTeamEvent.java#L87)
- [Add Spawn](https://github.com/MelanX/SkyblockBuilder/blob/1.20.x/src/main/java/de/melanx/skyblockbuilder/events/SkyblockManageTeamEvent.java#L114)
- [Remove Spawn](https://github.com/MelanX/SkyblockBuilder/blob/1.20.x/src/main/java/de/melanx/skyblockbuilder/events/SkyblockManageTeamEvent.java#L153)
- [Reset Spawns](https://github.com/MelanX/SkyblockBuilder/blob/1.20.x/src/main/java/de/melanx/skyblockbuilder/events/SkyblockManageTeamEvent.java#L183)
- [Rename Team](https://github.com/MelanX/SkyblockBuilder/blob/1.20.x/src/main/java/de/melanx/skyblockbuilder/events/SkyblockManageTeamEvent.java#L193)
- [Leave Team](https://github.com/MelanX/SkyblockBuilder/blob/1.20.x/src/main/java/de/melanx/skyblockbuilder/events/SkyblockManageTeamEvent.java#L222)
- [Create Team](https://github.com/MelanX/SkyblockBuilder/blob/1.20.x/src/main/java/de/melanx/skyblockbuilder/events/SkyblockOpManageEvent.java#L84)
- [Clear Team](https://github.com/MelanX/SkyblockBuilder/blob/1.20.x/src/main/java/de/melanx/skyblockbuilder/events/SkyblockOpManageEvent.java#L64)
- [Delete Team](https://github.com/MelanX/SkyblockBuilder/blob/1.20.x/src/main/java/de/melanx/skyblockbuilder/events/SkyblockOpManageEvent.java#L44)
- [Add to Team](https://github.com/MelanX/SkyblockBuilder/blob/1.20.x/src/main/java/de/melanx/skyblockbuilder/events/SkyblockOpManageEvent.java#L122)
- [Remove from Team](https://github.com/MelanX/SkyblockBuilder/blob/1.20.x/src/main/java/de/melanx/skyblockbuilder/events/SkyblockOpManageEvent.java#L153)
- [Teleport Home](https://github.com/MelanX/SkyblockBuilder/blob/1.20.x/src/main/java/de/melanx/skyblockbuilder/events/SkyblockTeleportHomeEvent.java)
- [Visit Island](https://github.com/MelanX/SkyblockBuilder/blob/1.20.x/src/main/java/de/melanx/skyblockbuilder/events/SkyblockVisitEvent.java)

All events do have proper Javadoc which describe what they do.