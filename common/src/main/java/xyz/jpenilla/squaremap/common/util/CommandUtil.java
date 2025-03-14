package xyz.jpenilla.squaremap.common.util;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.RichDescription;
import java.util.Optional;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.minecraft.server.level.ServerLevel;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import xyz.jpenilla.squaremap.common.command.Commander;
import xyz.jpenilla.squaremap.common.command.Commands;
import xyz.jpenilla.squaremap.common.command.PlayerCommander;
import xyz.jpenilla.squaremap.common.command.exception.CommandCompleted;
import xyz.jpenilla.squaremap.common.config.Lang;
import xyz.jpenilla.squaremap.common.data.MapWorldInternal;

@DefaultQualifier(NonNull.class)
public final class CommandUtil {
    private CommandUtil() {
    }

    public static MapWorldInternal resolveWorld(final CommandContext<Commander> context) {
        final Commander sender = context.getSender();
        final @Nullable MapWorldInternal world = context.getOrDefault("world", null);
        if (world != null) {
            return world;
        }
        if (sender instanceof final PlayerCommander player) {
            final ServerLevel level = player.player().getLevel();
            final Optional<MapWorldInternal> mapWorld = context.get(Commands.WORLD_MANAGER).getWorldIfEnabled(level);
            if (mapWorld.isPresent()) {
                return mapWorld.get();
            }
            Lang.send(sender, Lang.MAP_NOT_ENABLED_FOR_WORLD, Placeholder.unparsed("world", level.dimension().location().toString()));
            throw CommandCompleted.withoutMessage();
        } else {
            throw CommandCompleted.withMessage(Components.miniMessage(Lang.CONSOLE_MUST_SPECIFY_WORLD));
        }
    }

    public static RichDescription description(final String miniMessage, TagResolver... placeholders) {
        return RichDescription.of(Components.miniMessage(miniMessage, placeholders));
    }
}
