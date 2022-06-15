package com.rosegold.rosegoldexpansions.utils;

import com.rosegold.rosegoldexpansions.Main;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;

import javax.vecmath.Vector3f;
import java.util.ArrayList;

public class RotationUtils {
    public static Rotation startRot;
    public static Rotation neededChange;
    public static Rotation endRot;
    public static long startTime;
    public static long endTime;
    public static boolean done = true;
    private static float[][] BLOCK_SIDES = new float[][] { { 0.5f, 0.01f, 0.5f }, { 0.5f, 0.99f, 0.5f }, { 0.01f, 0.5f, 0.5f }, { 0.99f, 0.5f, 0.5f }, { 0.5f, 0.5f, 0.01f }, { 0.5f, 0.5f, 0.99f } };


    public static Rotation getRotation(Vec3d vec) {
        Vec3d eyes = Main.mc.player.getPositionEyes(1.0f);
        return getRotation(eyes, vec);
    }

    public static Rotation getRotation(Vec3d from, Vec3d to) {
        double diffX = to.x - from.x;
        double diffY = to.y - from.y;
        double diffZ = to.z - from.z;
        return new Rotation(MathHelper.wrapDegrees((float)(Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0)), (float)(-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ)))));
    }

    public static Rotation getRotation(BlockPos bp) {
        Vec3d vec = new Vec3d(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5);
        return getRotation(vec);
    }

    public static void setup(Rotation rot, Long aimTime) {
        done = false;
        startRot = new Rotation(Main.mc.player.rotationYaw, Main.mc.player.rotationPitch);
        neededChange = getNeededChange(startRot, rot);
        endRot = new Rotation(startRot.getYaw() + neededChange.getYaw(), startRot.getPitch() + neededChange.getPitch());
        startTime = System.currentTimeMillis();
        endTime = System.currentTimeMillis() + aimTime;
    }

    public static void reset() {
        done = true;
        startRot = null;
        neededChange = null;
        endRot = null;
        startTime = 0L;
        endTime = 0L;
    }

    public static void update() {
        if (System.currentTimeMillis() <= endTime) {
            Main.mc.player.rotationYaw = interpolate(startRot.getYaw(), endRot.getYaw());
            Main.mc.player.rotationPitch = interpolate(startRot.getPitch(), endRot.getPitch());
        }
        else if (!done) {
            Main.mc.player.rotationYaw = endRot.getYaw();
            Main.mc.player.rotationPitch = endRot.getPitch();
            reset();
        }
    }

    public static void snapAngles(Rotation rot) {
        Main.mc.player.rotationYaw = rot.getYaw();
        Main.mc.player.rotationPitch = rot.getPitch();
    }

    private static float interpolate(float start, float end) {
        float spentMillis = (float)(System.currentTimeMillis() - startTime);
        float relativeProgress = spentMillis / (endTime - startTime);
        return (end - start) * easeOutCubic(relativeProgress) + start;
    }

    public static float easeOutCubic(double number) {
        return (float)(1.0 - Math.pow(1.0 - number, 3.0));
    }

    public static Rotation getNeededChange(Rotation startRot, Rotation endRot) {
        float yawChng = MathHelper.wrapDegrees(endRot.getYaw()) - MathHelper.wrapDegrees(startRot.getYaw());
        if (yawChng <= -180.0f) {
            yawChng += 360.0f;
        }
        else if (yawChng > 180.0f) {
            yawChng -= 360.0f;
        }
        return new Rotation(yawChng, endRot.getPitch() - startRot.getPitch());
    }

    public static double fovFromEntity(Entity en) {
        return ((Main.mc.player.rotationYaw - fovToEntity(en)) % 360.0 + 540.0) % 360.0 - 180.0;
    }

    public static float fovToEntity(Entity ent) {
        double x = ent.posX - Main.mc.player.posX;
        double z = ent.posZ - Main.mc.player.posZ;
        double yaw = Math.atan2(x, z) * 57.2957795;
        return (float)(yaw * -1.0);
    }

    public static float fovToBlock(BlockPos blockPos) {
        double x = blockPos.getX() - Main.mc.player.posX;
        double z = blockPos.getZ() - Main.mc.player.posZ;
        double yaw = Math.atan2(x, z) * 57.2957795;
        return (float)(yaw * -1.0);
    }

    public static float fovToVec(Vec3d vec3d) {
        double x = vec3d.x - Main.mc.player.posX;
        double z = vec3d.z - Main.mc.player.posZ;
        double yaw = Math.atan2(x, z) * 57.2957795;
        return (float)(yaw * -1.0);
    }

    public static Rotation getNeededChange(Rotation endRot) {
        Rotation startRot = new Rotation(Main.mc.player.rotationYaw, Main.mc.player.rotationPitch);
        return getNeededChange(startRot, endRot);
    }

    public static ArrayList<Vec3d> getBlockSides(BlockPos bp) {
        ArrayList<Vec3d> ret = new ArrayList<>();
        for (float[] side : BLOCK_SIDES) {
            ret.add(new Vec3d(bp).addVector(side[0], side[1], side[2]));
        }
        return ret;
    }

    public static boolean lookingAt(BlockPos blockPos, float range) {
        float stepSize = 0.15f;
        Vec3d position = new Vec3d(Main.mc.player.posX, Main.mc.player.posY + Main.mc.player.getEyeHeight(), Main.mc.player.posZ);
        Vec3d look = Main.mc.player.getLook(0.0f);
        Vector3f step = new Vector3f((float)look.x, (float)look.y, (float)look.z);
        step.scale(stepSize / step.length());
        for (int i = 0; i < Math.floor(range / stepSize) - 2.0; ++i) {
            BlockPos blockAtPos = new BlockPos(position.x, position.y, position.z);
            if (blockAtPos.equals(blockPos)) {
                return true;
            }
            position = position.add(new Vec3d(step.x, step.y, step.z));
        }
        return false;
    }

    public static Vec3d getVectorForRotation(float pitch, float yaw) {
        float f2 = -MathHelper.cos(-pitch * 0.017453292f);
        return new Vec3d(MathHelper.sin(-yaw * 0.017453292f - 3.1415927f) * f2, MathHelper.sin(-pitch * 0.017453292f), MathHelper.cos(-yaw * 0.017453292f - 3.1415927f) * f2);
    }

    public static Vec3d getLook(Vec3d vec) {
        double diffX = vec.x - Main.mc.player.posX;
        double diffY = vec.y - (Main.mc.player.posY + Main.mc.player.getEyeHeight());
        double diffZ = vec.z - Main.mc.player.posZ;
        double dist = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
        return getVectorForRotation((float)(-(MathHelper.atan2(diffY, dist) * 180.0 / 3.141592653589793)), (float)(MathHelper.atan2(diffZ, diffX) * 180.0 / 3.141592653589793 - 90.0));
    }

    public static EnumFacing calculateEnumfacing(Vec3d pos) {
        int x = MathHelper.floor(pos.x);
        int y = MathHelper.floor(pos.y);
        int z = MathHelper.floor(pos.z);
        RayTraceResult position = calculateIntercept(new AxisAlignedBB(x, y, z, x + 1, y + 1, z + 1), pos, 50.0f);
        return (position != null) ? position.sideHit : null;
    }

    public static RayTraceResult calculateIntercept(AxisAlignedBB aabb, Vec3d block, float range) {
        Vec3d vec3 = Main.mc.player.getPositionEyes(1.0f);
        Vec3d vec4 = getLook(block);
        return aabb.calculateIntercept(vec3, vec3.addVector(vec4.x * range, vec4.y * range, vec4.z * range));
    }

    public static ArrayList<Vec3d> getPointsOnBlock(BlockPos bp) {
        ArrayList<Vec3d> ret = new ArrayList<>();
        for (float[] side : BLOCK_SIDES) {
            for (int i = 0; i < 20; ++i) {
                float x = side[0];
                float y = side[1];
                float z = side[2];
                if (x == 0.5) {
                    x = RandomUtils.randBetween(0.1f, 0.9f);
                }
                if (y == 0.5) {
                    y = RandomUtils.randBetween(0.1f, 0.9f);
                }
                if (z == 0.5) {
                    z = RandomUtils.randBetween(0.1f, 0.9f);
                }
                ret.add(new Vec3d(bp).addVector((double)x, (double)y, (double)z));
            }
        }
        return ret;
    }
}
