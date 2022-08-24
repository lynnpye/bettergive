package com.pyehouse.mcmods.bettergive.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.ItemArgument;
import net.minecraft.command.arguments.ItemInput;
import net.minecraft.command.impl.GiveCommand;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
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

    public static final String RESOURCE_LOCATION_CHARACTERS_DISALLOWED = "[^a-z0-9\\/._-]";

    @SubscribeEvent
    public static void onRegisterCommand(RegisterCommandsEvent event) {
        final CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();

        dispatcher.register(makeBetterGiveCommand(CMD_bgive));
        dispatcher.register(makeBetterGiveCommand(CMD_bettergive));
    }

    private static LiteralArgumentBuilder<CommandSource> makeBetterGiveCommand(String commandString) {
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

    private static SuggestionProvider<CommandSource> createSuggester() {
        return (context, builder) -> {
            String stringRemainder = builder.getRemaining().toLowerCase(Locale.ROOT);
            String[] bitParts = stringRemainder.split(RESOURCE_LOCATION_CHARACTERS_DISALLOWED, 0);
            for (ResourceLocation resourceLocation : ForgeRegistries.ITEMS.getKeys()) {
                String resourceString = resourceLocation.toString();
                boolean addSuggestion = true;
                for (String part : bitParts) {
                    if (!resourceString.contains(part)) {
                        // skip this resourceLocation
                        addSuggestion = false;
                        break;
                    }
                }
                if (addSuggestion) {
                    builder.suggest(resourceString);
                }
            }

            return builder.buildFuture();
        };
    }

    private static int betterGiveItem(CommandContext<CommandSource> context, int count) throws CommandSyntaxException {
        final ItemInput itemInput = ItemArgument.getItem(context, ARG_item);
        final Collection<ServerPlayerEntity> serverPlayerEntities = EntityArgument.getPlayers(context, ARG_players);
        final CommandSource source = context.getSource();

        int result = 0;
        try {
            Class<GiveCommand> giveCommandClass = GiveCommand.class;
            // SRG name: func_198497_a(Lnet/minecraft/command/CommandSource;Lnet/minecraft/command/arguments/ItemInput;Ljava/util/Collection;I)I
            Method giveItemMethod = ObfuscationReflectionHelper.findMethod(giveCommandClass, "func_198497_a", CommandSource.class, ItemInput.class, Collection.class, int.class);
            result = (int) giveItemMethod.invoke(null, source, itemInput, serverPlayerEntities, count);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return result;
    }
}
