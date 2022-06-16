package com.rosegold.rosegoldexpansions.features;

import com.rosegold.rosegoldexpansions.Main;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class AutoWalk {
    public static HashMap<String, ArrayList<Point>> profiles = new HashMap<>();
    public static ArrayList<Point> currProfile = new ArrayList<>();

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if(Main.mc.world == null) return;

    }
}
