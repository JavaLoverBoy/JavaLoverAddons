package com.rosegold.rosegoldexpansions.commands;

import com.rosegold.rosegoldexpansions.Main;
import com.rosegold.rosegoldexpansions.features.Pathfinding;
import com.rosegold.rosegoldexpansions.utils.VecUtils;
import com.rosegold.rosegoldexpansions.utils.pathfinding.Pathfinder;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class Goto extends CommandBase {

    @Override
    public String getName() {
        return "goto";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + getName();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length != 3) return;
        String x = args[0];
        String y = args[1];
        String z = args[2];
        Pathfinding.init();
        new Thread(() -> Pathfinder.setup(new BlockPos(VecUtils.floorVec(Main.mc.player.getPositionVector())), new BlockPos(Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(z)), 0.0)).start();
    }
}
