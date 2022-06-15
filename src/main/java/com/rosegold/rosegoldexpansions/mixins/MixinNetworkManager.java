package com.rosegold.rosegoldexpansions.mixins;

import com.rosegold.rosegoldexpansions.events.PacketReceivedEvent;
import com.rosegold.rosegoldexpansions.events.PacketSentEvent;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {
    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo callbackInfo) {
        if (MinecraftForge.EVENT_BUS.post(new PacketSentEvent(packet))) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("RETURN"), cancellable = true)
    private void onSendPacketPost(Packet<?> packet, CallbackInfo callbackInfo) {
        if (MinecraftForge.EVENT_BUS.post(new PacketSentEvent.Post(packet))) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "channelRead0", at = @At(value = "HEAD"), cancellable = true)
    private void onChannelReadHead(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callbackInfo) {
        if (MinecraftForge.EVENT_BUS.post(new PacketReceivedEvent(packet, context))) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "channelRead0", at = @At(value = "RETURN"), cancellable = true)
    private void onPost(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callbackInfo) {
        if (MinecraftForge.EVENT_BUS.post(new PacketReceivedEvent.Post(packet, context))) {
            callbackInfo.cancel();
        }
    }
}
