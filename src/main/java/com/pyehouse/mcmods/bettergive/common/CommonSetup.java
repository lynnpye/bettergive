package com.pyehouse.mcmods.bettergive.common;

import net.minecraftforge.common.MinecraftForge;

public class CommonSetup {
    public static void setup() {
        MinecraftForge.EVENT_BUS.addListener(BetterGiveCommandHandler::onRegisterCommand);
    }
}
