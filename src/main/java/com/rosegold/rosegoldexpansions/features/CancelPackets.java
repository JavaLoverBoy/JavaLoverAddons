package com.rosegold.rosegoldexpansions.features;

import com.rosegold.rosegoldexpansions.Main;
import com.rosegold.rosegoldexpansions.events.PacketSentEvent;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CancelPackets {
    @SubscribeEvent
    public void onPacketSent(PacketSentEvent event) {
        if(Main.mc.player == null) return;
        if(event.packet instanceof CPacketPlayerTryUseItem) {
            //event.setCanceled(true);
        }
    }
}
