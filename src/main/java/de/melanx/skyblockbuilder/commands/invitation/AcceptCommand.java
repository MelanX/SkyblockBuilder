package de.melanx.skyblockbuilder.commands.invitation;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.melanx.skyblockbuilder.commands.Suggestions;
import de.melanx.skyblockbuilder.config.LibXConfigHandler;
import de.melanx.skyblockbuilder.data.SkyblockSavedData;
import de.melanx.skyblockbuilder.data.Team;
import de.melanx.skyblockbuilder.events.SkyblockHooks;
import de.melanx.skyblockbuilder.util.WorldUtil;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class AcceptCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        // Accepts an invitation
        return Commands.literal("accept")
                .then(Commands.argument("team", StringArgumentType.word()).suggests(Suggestions.INVITE_TEAMS)
                        .executes(context -> acceptTeam(context.getSource(), StringArgumentType.getString(context, "team"))));
    }

    private static int acceptTeam(CommandSource source, String teamName) throws CommandSyntaxException {
        WorldUtil.checkSkyblock(source);
        ServerWorld world = source.getWorld();
        SkyblockSavedData data = SkyblockSavedData.get(world);
        ServerPlayerEntity player = source.asPlayer();
        Team team = data.getTeam(teamName);

        if (team == null) {
            source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.error.team_not_exist").mergeStyle(TextFormatting.RED), false);
            return 0;
        }

        if (data.hasPlayerTeam(player)) {
            source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.error.user_has_team").mergeStyle(TextFormatting.RED), false);
            return 0;
        }

        if (!data.hasInvites(player)) {
            source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.error.no_invitations").mergeStyle(TextFormatting.RED), false);
            return 0;
        }

        switch (SkyblockHooks.onAccept(player, team)) {
            case DENY:
                source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.denied.accept_invitations").mergeStyle(TextFormatting.RED), false);
                return 0;
            case DEFAULT:
                if (!LibXConfigHandler.Utility.selfManage && !source.hasPermissionLevel(2)) {
                    source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.disabled.accept_invitations").mergeStyle(TextFormatting.RED), false);
                    return 0;
                }
                break;
            case ALLOW:
                break;
        }

        if (!data.acceptInvite(team, player)) {
            source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.error.accept_invitations").mergeStyle(TextFormatting.RED), false);
            return 0;
        }

        source.sendFeedback(new TranslationTextComponent("skyblockbuilder.command.success.joined_team", team.getName()).mergeStyle(TextFormatting.GOLD), true);
        return 1;
    }

}
