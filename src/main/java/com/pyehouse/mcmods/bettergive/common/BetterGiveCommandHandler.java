package com.pyehouse.mcmods.bettergive.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.GiveCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Locale;

public class BetterGiveCommandHandler {

    public static final String CMD_bgive = "bgive";
    public static final String CMD_bettergive = "bettergive";

    public static final String ARG_players = "players";
    public static final String ARG_item = "item";
    public static final String ARG_count = "count";

    @SubscribeEvent
    public static void onRegisterCommand(RegisterCommandsEvent event) {
        final CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(makeBetterGiveCommand(CMD_bgive));
        dispatcher.register(makeBetterGiveCommand(CMD_bettergive));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> makeBetterGiveCommand(String commandString) {
        return Commands
            .literal(commandString)
            .requires((commandSource) -> commandSource.hasPermission(2))
            .then(
                Commands.argument(ARG_players, EntityArgument.players())
                    .then(
                        Commands.argument(ARG_item, ItemArgument.item())
                                    .suggests(createSuggester())
                                    .executes(context -> betterGiveItem(context, 1))
                                .then(
                                        Commands
                                                .argument(ARG_count, IntegerArgumentType.integer(1))
                                                .executes(context -> betterGiveItem(context, IntegerArgumentType.getInteger(context, ARG_count)))
                                )
                    )
            )
            ;
    }

    private static SuggestionProvider<CommandSourceStack> createSuggester() {
        return (context, builder) -> {
            String stringRemainder = builder.getRemaining().toLowerCase(Locale.ROOT);
            for (ResourceLocation resourceLocation : ForgeRegistries.ITEMS.getKeys()) {
                String resourceString = resourceLocation.toString();
                if (resourceString.contains(stringRemainder)) {
                    builder.suggest(resourceString);
                }
            }

            return builder.buildFuture();
        };
    }

    private static int betterGiveItem(CommandContext<CommandSourceStack> context, int count) throws CommandSyntaxException {
        final ItemInput itemInput = ItemArgument.getItem(context, ARG_item);
        final Collection<ServerPlayer> serverPlayerEntities = EntityArgument.getPlayers(context, ARG_players);
        final CommandSourceStack source = context.getSource();

        int result = 0;
        try {
            Class<GiveCommand> giveCommandClass = GiveCommand.class;
            Method giveItemMethod = ObfuscationReflectionHelper.findMethod(giveCommandClass, "giveItem", CommandSourceStack.class, ItemInput.class, Collection.class, int.class);
            result = (int) giveItemMethod.invoke(null, source, itemInput, serverPlayerEntities, count);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return result;
    }
}
