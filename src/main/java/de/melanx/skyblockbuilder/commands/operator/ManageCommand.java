package de.melanx.skyblockbuilder.commands.operator;

import com.google.common.collect.ImmutableSet;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.melanx.skyblockbuilder.commands.Suggestions;
import de.melanx.skyblockbuilder.config.LibXConfigHandler;
import de.melanx.skyblockbuilder.data.SkyblockSavedData;
import de.melanx.skyblockbuilder.data.Team;
import de.melanx.skyblockbuilder.data.TemplateData;
import de.melanx.skyblockbuilder.events.SkyblockHooks;
import de.melanx.skyblockbuilder.util.NameGenerator;
import de.melanx.skyblockbuilder.util.TemplateLoader;
import de.melanx.skyblockbuilder.util.WorldUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class ManageCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("manage").requires(source -> source.hasPermissionLevel(2))
                // refreshes the island shape
                .then(Commands.literal("islandShape")
                        .requires(source -> source.hasPermissionLevel(3))
                        .then(Commands.argument("template", StringArgumentType.word()).suggests(Suggestions.TEMPLATES)
                                .executes(context -> refreshIsland(context.getSource(), StringArgumentType.getString(context, "template")))))

                .then(Commands.literal("teams")
                        // Removes all empty teams in the world
                        .then(Commands.literal("clear")
                                .executes(context -> deleteEmptyTeams(context.getSource()))
                                .then(Commands.argument("team", StringArgumentType.word()).suggests(Suggestions.ALL_TEAMS)
                                        .executes(context -> clearTeam(context.getSource(), StringArgumentType.getString(context, "team")))))

                        // Creates a team
                        .then(Commands.literal("create")
                                .executes(context -> createTeam(context.getSource(), false))
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .executes(context -> createTeam(context.getSource(), StringArgumentType.getString(context, "name"), false))))

                        // Creates a team and the player executing the command joins
                        .then(Commands.literal("createAndJoin")
                                .executes(context -> createTeam(context.getSource(), true))
                                .then(Commands.argument("name", StringArgumentType.word())
                                        .executes(context -> createTeam(context.getSource(), StringArgumentType.getString(context, "name"), true))))

                        // Deletes the team with the given name
                        .then(Commands.literal("delete")
                                .then(Commands.argument("team", StringArgumentType.word()).suggests(Suggestions.ALL_TEAMS)
                                        .executes(context -> deleteTeam(context.getSource(), StringArgumentType.getString(context, "team"))))))

                // Adds player(s) to a given team
                .then(Commands.literal("addPlayer")
                        .then(Commands.argument("players", EntityArgument.players())
                                .then(Commands.argument("team", StringArgumentType.word()).suggests(Suggestions.ALL_TEAMS)
                                        .executes(context -> addToTeam(context.getSource(), StringArgumentType.getString(context, "team"), EntityArgument.getPlayers(context, "players"))))))
                // Kicks player from its current team
                .then(Commands.literal("kickPlayer")
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(context -> removeFromTeam(context.getSource(), EntityArgument.getPlayer(context, "player")))));
    }

    private static int refreshIsland(CommandSource source, String name) {
        TemplateLoader.setTemplate(TemplateLoader.getTemplates().get(name));
        TemplateData.get(source.getWorld()).refreshTemplate();
        source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.success.reset_island", name), true);

        return 1;
    }

    private static int deleteEmptyTeams(CommandSource source) throws CommandSyntaxException {
        WorldUtil.checkSkyblock(source);
        ServerWorld world = source.getWorld();
        SkyblockSavedData data = SkyblockSavedData.get(world);

        int i = 0;
        Iterator<Team> itr = data.getTeams().iterator();
        while (itr.hasNext()) {
            Team team = itr.next();
            if (!SkyblockHooks.onManageDeleteTeam(source, team)) {
                if (team.isEmpty()) {
                    itr.remove();
                    i++;
                }
            }
        }
        data.markDirty();

        source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.success.delete_multiple_teams", i).mergeStyle(TextFormatting.GREEN), true);
        return 1;
    }

    private static int clearTeam(CommandSource source, String teamName) throws CommandSyntaxException {
        WorldUtil.checkSkyblock(source);
        ServerWorld world = source.getWorld();
        SkyblockSavedData data = SkyblockSavedData.get(world);

        Team team = data.getTeam(teamName);
        if (team == null) {
            source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.error.team_not_exist").mergeStyle(TextFormatting.RED), false);
            return 0;
        }

        if (SkyblockHooks.onManageClearTeam(source, team)) {
            source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.denied.clear_team").mergeStyle(TextFormatting.RED), false);
            return 0;
        }

        int i = team.getPlayers().size();
        data.removeAllPlayersFromTeam(team);
        source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.success.remove_all_players_from_team", i).mergeStyle(TextFormatting.RED), true);
        return 1;
    }

    private static int createTeam(CommandSource source, boolean join) throws CommandSyntaxException {
        WorldUtil.checkSkyblock(source);
        String team;
        Random rand = new Random();
        do {
            team = NameGenerator.randomName(rand);
        } while (SkyblockSavedData.get(source.getWorld()).teamExists(team));
        return createTeam(source, team, join);
    }

    private static int createTeam(CommandSource source, String name, boolean join) throws CommandSyntaxException {
        WorldUtil.checkSkyblock(source);
        ServerWorld world = source.getWorld();
        SkyblockSavedData data = SkyblockSavedData.get(world);

        Pair<Boolean, String> result = SkyblockHooks.onManageCreateTeam(source, name, join);
        if (result.getLeft()) {
            source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.denied.create_team").mergeStyle(TextFormatting.RED), false);
            return 0;
        }

        Team team = data.createTeam(result.getRight());
        if (team == null) {
            source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.error.team_already_exist", result.getRight()).mergeStyle(TextFormatting.RED), false);
            return 0;
        }

        if (join) {
            try {
                ServerPlayerEntity player = source.asPlayer();
                if (data.getTeamFromPlayer(player) != null) {
                    source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.error.user_has_team").mergeStyle(TextFormatting.RED), false);
                    return 0;
                }

                data.addPlayerToTeam(team, player);
                WorldUtil.teleportToIsland(player, team);
            } catch (CommandSyntaxException e) {
                source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.error.user_no_player").mergeStyle(TextFormatting.RED), false);
                return 1;
            }
        }

        source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.success.create_team", result.getRight()).mergeStyle(TextFormatting.GREEN), true);
        return 1;
    }

    private static int deleteTeam(CommandSource source, String team) throws CommandSyntaxException {
        WorldUtil.checkSkyblock(source);
        ServerWorld world = source.getWorld();
        SkyblockSavedData data = SkyblockSavedData.get(world);

        if (!data.teamExists(team)) {
            source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.error.team_not_exist").mergeStyle(TextFormatting.RED), false);
            return 0;
        }

        if (SkyblockHooks.onManageDeleteTeam(source, data.getTeam(team))) {
            source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.denied.delete_team"), false);
            return 0;
        }

        //noinspection ConstantConditions
        Set<UUID> players = new HashSet<>(data.getTeam(team).getPlayers());
        if (!data.deleteTeam(team)) {
            source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.error.delete_team", team).mergeStyle(TextFormatting.RED), false);
            return 0;
        }

        PlayerList playerList = source.getServer().getPlayerList();
        Team spawn = data.getSpawn();
        players.forEach(id -> {
            ServerPlayerEntity player = playerList.getPlayerByUUID(id);
            if (player != null) {
                if (LibXConfigHandler.Inventory.dropItems) {
                    player.inventory.dropAllItems();
                }
                WorldUtil.teleportToIsland(player, spawn);
            }
        });

        source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.success.delete_one_team", team).mergeStyle(TextFormatting.GREEN), true);
        return 1;
    }

    private static int addToTeam(CommandSource source, String teamName, Collection<ServerPlayerEntity> players) throws CommandSyntaxException {
        WorldUtil.checkSkyblock(source);
        ServerWorld world = source.getWorld();
        SkyblockSavedData data = SkyblockSavedData.get(world);

        if (!data.teamExists(teamName)) {
            source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.error.team_not_exist").mergeStyle(TextFormatting.RED), false);
            return 0;
        }

        Team island = data.getTeam(teamName);
        Pair<Boolean, Set<ServerPlayerEntity>> result = SkyblockHooks.onManageAddToTeam(source, island, players);
        if (result.getLeft()) {
            source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.denied.add_players_to_team"), false);
            return 0;
        }

        ServerPlayerEntity added = null;
        int i = 0;
        for (ServerPlayerEntity addedPlayer : result.getRight()) {
            if (!data.hasPlayerTeam(addedPlayer)) {
                data.addPlayerToTeam(teamName, addedPlayer);
                WorldUtil.teleportToIsland(addedPlayer, island);
                if (i == 0) added = addedPlayer;
                i += 1;
            }
        }

        if (i == 0) {
            source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.error.no_player_added").mergeStyle(TextFormatting.RED), false);
            return 0;
        }

        if (i == 1)
            source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.success.add_one_player", added.getDisplayName().getString(), teamName).mergeStyle(TextFormatting.GREEN), true);
        else
            source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.success.add_multiple_players", i, teamName).mergeStyle(TextFormatting.GREEN), true);
        return 1;
    }

    private static int removeFromTeam(CommandSource source, ServerPlayerEntity player) throws CommandSyntaxException {
        WorldUtil.checkSkyblock(source);
        ServerWorld world = source.getWorld();
        SkyblockSavedData data = SkyblockSavedData.get(world);

        Team team = data.getTeamFromPlayer(player);

        if (team == null) {
            source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.error.player_has_no_team").mergeStyle(TextFormatting.RED), false);
            return 0;
        }

        Pair<Boolean, Set<ServerPlayerEntity>> result = SkyblockHooks.onManageAddToTeam(source, team, ImmutableSet.of(player));
        if (result.getLeft()) {
            source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.denied.add_players_to_team"), false);
            return 0;
        }

        String teamName = team.getName();
        Team spawn = data.getSpawn();
        int i = 0;
        for (ServerPlayerEntity target : result.getRight()) {
            // Even if the event adds players to the list
            // we only remove the ones that really are in the team.
            if (team.hasPlayer(target)) {
                data.removePlayerFromTeam(target);
                if (LibXConfigHandler.Inventory.dropItems) {
                    target.inventory.dropAllItems();
                }
                WorldUtil.teleportToIsland(target, spawn);
                i += 1;
            }
        }
        source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.success.remove_multiple_players", i, teamName).mergeStyle(TextFormatting.GREEN), true);
        return 1;
    }
}
