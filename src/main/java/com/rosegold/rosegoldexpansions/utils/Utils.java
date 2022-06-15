package com.rosegold.rosegoldexpansions.utils;

import com.rosegold.rosegoldexpansions.Main;
import net.minecraft.util.text.TextComponentString;

public class Utils {
    public static void sendMessage(Object object) {
        String st = object.toString();
        Main.mc.player.sendMessage(new TextComponentString(st));
    }
}
