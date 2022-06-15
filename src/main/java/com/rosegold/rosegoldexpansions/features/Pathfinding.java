package com.rosegold.rosegoldexpansions.features;

import com.rosegold.rosegoldexpansions.Main;
import com.rosegold.rosegoldexpansions.events.TickEndEvent;
import com.rosegold.rosegoldexpansions.utils.*;
import com.rosegold.rosegoldexpansions.utils.pathfinding.Pathfinder;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

public class Pathfinding {
    private static int stuckTicks = 0;
    private static BlockPos oldPos;
    private static BlockPos curPos;
    private static Vec3d nextPos;

    @SubscribeEvent
    public void onTick(TickEndEvent event) {
        if (Main.mc.player == null || Main.mc.world == null) return;
        if (Pathfinder.hasPath()) {
            if (++stuckTicks >= 20) {
                curPos = Main.mc.player.getPosition();
                if (oldPos != null && Math.sqrt(curPos.distanceSq(oldPos)) <= 0.1) {
                    KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindJump.getKeyCode(), false);
                    KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindRight.getKeyCode(), false);
                    init();
                    Pathfinder.path.clear();
                    new Thread(() -> Pathfinder.setup(new BlockPos(VecUtils.floorVec(Main.mc.player.getPositionVector())), Pathfinder.goal, 0.0)).start();
                    return;
                }
                oldPos = curPos;
                stuckTicks = 0;
            }
            nextPos = goodPoints(Pathfinder.path);
            Pathfinder.path.removeIf(vec3d -> new BlockPos(vec3d).getY() == Main.mc.player.getPosition().getY() && Pathfinder.path.indexOf(vec3d) < Pathfinder.path.indexOf(nextPos));
            Vec3d first = Pathfinder.getCurrent();
            first = first.addVector(0.5, 0.0, 0.5);
            Rotation needed = RotationUtils.getRotation(first);
            needed.setPitch(Main.mc.player.rotationPitch);
            if (VecUtils.getHorizontalDistance(Main.mc.player.getPositionVector(), first) > 0.69) {
                if (RotationUtils.done /*&& needed.getYaw() < 135.0f*/) {
                    RotationUtils.setup(needed, (long) 150);
                }
                if (Pathfinder.hasNext()) {
                    Vec3d next = Pathfinder.getNext().addVector(0.5, 0.0, 0.5);
                    double xDiff = Math.abs(Math.abs(next.x) - Math.abs(first.x));
                    double zDiff = Math.abs(Math.abs(next.z) - Math.abs(first.z));
                    Main.mc.player.setSprinting((xDiff == 1.0 && zDiff == 0.0) || (xDiff == 0.0 && zDiff == 1.0));
                }
                stopMovement();
                KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindForward.getKeyCode(), true);
                if (Math.abs(Main.mc.player.posY - first.y) > 0.5) {
                    KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindJump.getKeyCode(), Main.mc.player.posY < first.y);
                }
                else {
                    KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindJump.getKeyCode(), false);
                }
            }
            else {
                RotationUtils.reset();
                if (!Pathfinder.goNext()) {
                    stopMovement();
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender(RenderWorldLastEvent event) {
        if (Main.mc.player == null || Main.mc.world == null) return;
        if (Pathfinder.path != null && !Pathfinder.path.isEmpty()) {
            for(Vec3d vec3d : Pathfinder.path) {
                RenderUtils.drawBlockESP(new BlockPos(vec3d.subtract(0, 1, 0)), Color.WHITE, event.getPartialTicks());
            }
        }
        if (nextPos != null) {
            RenderUtils.drawBlockESP(new BlockPos(nextPos.subtract(0, 1, 0)), ColorUtils.getChroma(3000.0f, 0), event.getPartialTicks());
        }
        if (Main.mc.currentScreen != null && !(Main.mc.currentScreen instanceof GuiChat)) {
            return;
        }
        if (!RotationUtils.done) {
            RotationUtils.update();
        }
    }

    private static Vec3d goodPoints(ArrayList<Vec3d> path) {
        ArrayList<Vec3d> reversed = new ArrayList<>(path);
        Collections.reverse(reversed);
        for(Vec3d vec3d : reversed.stream().filter(vec3d -> new BlockPos(vec3d).getY() == Main.mc.player.getPosition().getY()).collect(Collectors.toList())) {
            if(isGood(vec3d)) {
                return vec3d;
            }
        }
        return null;
    }

    private static boolean isGood(Vec3d point) {
        if(point == null) return false;
        point = point.add(new Vec3d(0, 2, 0));
        Vec3d topPos = Main.mc.player.getPositionVector().addVector(0, 2, 0);
        Vec3d botPos = Main.mc.player.getPositionVector().addVector(0, 1, 0);
        Vec3d direction = RotationUtils.getLook(point);
        direction = VecUtils.scaleVec(direction, 0.5f);
        for (int i = 0; i < Math.round(point.distanceTo(Main.mc.player.getPositionEyes(1))) * 2; i++) {
            if (Main.mc.world.getBlockState(new BlockPos(topPos)).getBlock() != Blocks.AIR) {
                return false;
            }
            if (Main.mc.world.getBlockState(new BlockPos(botPos)).getBlock() != Blocks.AIR) {
                return false;
            }
            botPos = botPos.add(direction);
            topPos = topPos.add(direction);
        }
        return true;
    }

    public static void init() {
        stuckTicks = 0;
        oldPos = null;
        curPos = null;
    }

    private void stopMovement() {
        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindLeft.getKeyCode(), false);
        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindRight.getKeyCode(), false);
        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindForward.getKeyCode(), false);
        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindBack.getKeyCode(), false);
        KeyBinding.setKeyBindState(Main.mc.gameSettings.keyBindJump.getKeyCode(), false);
    }
}
