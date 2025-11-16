package matanku.kakurembo.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import matanku.kakurembo.HideAndSeek;
import matanku.kakurembo.command.game.*;
import matanku.kakurembo.command.server.*;

import java.util.ArrayList;
import java.util.List;

public class ServerCommandManager {
    @Getter
    private static List<ServerCommand> commands = new ArrayList<>();

    public static void init() {
        commands = new ArrayList<>();
        commands.add(new Countdown());
        commands.add(new End());
        commands.add(new RoleMenu());
        commands.add(new Settings());
        commands.add(new SetTracker());
        commands.add(new Mute());
        commands.add(new ReloadLB());
        commands.add(new SetBuilder());
        commands.add(new SetLobby());
        commands.add(new SetRank());
        commands.add(new SetStar());
        commands.add(new Whitelist());
        ServerCommandManager.register();
    }

    @SuppressWarnings("all")
    public static void register() {
        HideAndSeek.getInstance().getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            for (ServerCommand command : commands) {
                var literal = Commands.literal(command.getName())
                        .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("args", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    command.onCommand(ctx.getSource().getSender(), null, command.getName(), StringArgumentType.getString(ctx, "args").split(" "));
                                    return 1;
                                })
                        )
                        .executes(ctx -> {
                            command.onCommand(ctx.getSource().getSender(), null, command.getName(), new String[0]);
                            return 1;
                        })
                        .build();

                event.registrar().register(literal, "Hide and Seek command", List.of(command.getAliases()));
            }
        });
    }

}
