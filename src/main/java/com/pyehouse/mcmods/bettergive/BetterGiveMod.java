package com.pyehouse.mcmods.bettergive;

import com.pyehouse.mcmods.bettergive.common.CommonSetup;
import net.minecraftforge.fml.common.Mod;

@Mod(BetterGiveMod.MODID)
public class BetterGiveMod {
    public static final String MODID = "bettergive";

    public BetterGiveMod() {
        CommonSetup.setup();
    }
}
