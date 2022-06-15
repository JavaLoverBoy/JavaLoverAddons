package com.rosegold.rosegoldexpansions.utils;

import com.rosegold.rosegoldexpansions.Main;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public class RayTraceUtils {

    @Nullable
    public static RayTraceResult rayTraceBlocks(Vec3d playerEyesPos, Vec3d playerTargetEyes, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        if (!Double.isNaN(playerEyesPos.x) && !Double.isNaN(playerEyesPos.y) && !Double.isNaN(playerEyesPos.z)) {
            if (!Double.isNaN(playerTargetEyes.x) && !Double.isNaN(playerTargetEyes.y) && !Double.isNaN(playerTargetEyes.z)) {
                int xTarget = MathHelper.floor(playerTargetEyes.x);
                int yTarget = MathHelper.floor(playerTargetEyes.y);
                int zTarget = MathHelper.floor(playerTargetEyes.z);
                int x1 = MathHelper.floor(playerEyesPos.x);
                int y1 = MathHelper.floor(playerEyesPos.y);
                int z1 = MathHelper.floor(playerEyesPos.z);

                RayTraceResult raytraceresult2 = null;
                int k1 = 200;

                while (k1-- >= 0) {
                    if (Double.isNaN(playerEyesPos.x) || Double.isNaN(playerEyesPos.y) || Double.isNaN(playerEyesPos.z)) {
                        return null;
                    }

                    boolean flag2 = true;
                    boolean flag = true;
                    boolean flag1 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if (xTarget > x1) {
                        d0 = (double) x1 + 1.0D;
                    } else if (xTarget < x1) {
                        d0 = (double) x1 + 0.0D;
                    } else {
                        flag2 = false;
                    }

                    if (yTarget > y1) {
                        d1 = (double) y1 + 1.0D;
                    } else if (yTarget < y1) {
                        d1 = (double) y1 + 0.0D;
                    } else {
                        flag = false;
                    }

                    if (zTarget > z1) {
                        d2 = (double) z1 + 1.0D;
                    } else if (zTarget < z1) {
                        d2 = (double) z1 + 0.0D;
                    } else {
                        flag1 = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = playerTargetEyes.x - playerEyesPos.x;
                    double d7 = playerTargetEyes.y - playerEyesPos.y;
                    double d8 = playerTargetEyes.z - playerEyesPos.z;

                    if (flag2) {
                        d3 = (d0 - playerEyesPos.x) / d6;
                    }

                    if (flag) {
                        d4 = (d1 - playerEyesPos.y) / d7;
                    }

                    if (flag1) {
                        d5 = (d2 - playerEyesPos.z) / d8;
                    }

                    if (d3 == -0.0D) {
                        d3 = -1.0E-4D;
                    }

                    if (d4 == -0.0D) {
                        d4 = -1.0E-4D;
                    }

                    if (d5 == -0.0D) {
                        d5 = -1.0E-4D;
                    }

                    EnumFacing enumfacing;

                    if (d3 < d4 && d3 < d5) {
                        enumfacing = xTarget > x1 ? EnumFacing.WEST : EnumFacing.EAST;
                        playerEyesPos = new Vec3d(d0, playerEyesPos.y + d7 * d3, playerEyesPos.z + d8 * d3);
                    } else if (d4 < d5) {
                        enumfacing = yTarget > y1 ? EnumFacing.DOWN : EnumFacing.UP;
                        playerEyesPos = new Vec3d(playerEyesPos.x + d6 * d4, d1, playerEyesPos.z + d8 * d4);
                    } else {
                        enumfacing = zTarget > z1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        playerEyesPos = new Vec3d(playerEyesPos.x + d6 * d5, playerEyesPos.y + d7 * d5, d2);
                    }

                    x1 = MathHelper.floor(playerEyesPos.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    y1 = MathHelper.floor(playerEyesPos.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
                    z1 = MathHelper.floor(playerEyesPos.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                    BlockPos blockpos = new BlockPos(x1, y1, z1);
                    IBlockState iblockstate1 = Main.mc.world.getBlockState(blockpos);
                    Block block1 = iblockstate1.getBlock();

                    if (!ignoreBlockWithoutBoundingBox || iblockstate1.getMaterial() == Material.PORTAL /*|| iblockstate1.getCollisionBoundingBox(Main.mc.world, blockpos) != Block.NULL_AABB*/) {
                        if (block1.canCollideCheck(iblockstate1, stopOnLiquid)) {
                            //if (Main.mc.world.getBlockState(blockpos.add(0, 1, 0)).getMaterial() == Material.AIR &&
                            //        Main.mc.world.getBlockState(blockpos.add(0, 2, 0)).getMaterial() == Material.AIR) {
                            return iblockstate1.collisionRayTrace(Main.mc.world, blockpos, playerEyesPos, playerTargetEyes);
                            //}
                        } else {
                            raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, playerEyesPos, enumfacing, new BlockPos(xTarget, yTarget, zTarget));
                        }
                    }
                }

                return returnLastUncollidableBlock ? raytraceresult2 : null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
