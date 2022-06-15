package com.rosegold.rosegoldexpansions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rosegold.rosegoldexpansions.commands.Goto;
import com.rosegold.rosegoldexpansions.events.MillisecondEvent;
import com.rosegold.rosegoldexpansions.events.TickEndEvent;
import com.rosegold.rosegoldexpansions.features.CancelPackets;
import com.rosegold.rosegoldexpansions.features.Pathfinding;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main
{
    public static final String MODID = "rosegoldexpansions";
    public static final String NAME = "RoseGold Expansions";
    public static final String VERSION = "1.0";

    public static final Minecraft mc = Minecraft.getMinecraft();
    public static String username = "default";
    public static  String playerID = "DefaultPlayer";
    public static final String token = "Forge";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        setSession();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new TickEndEvent());
        MinecraftForge.EVENT_BUS.register(new Pathfinding());
        MinecraftForge.EVENT_BUS.register(new CancelPackets());
        ClientCommandHandler.instance.registerCommand(new Goto());
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        LocalDateTime now = LocalDateTime.now();
        Duration initialDelay = Duration.between(now, now);
        long initialDelaySeconds = initialDelay.getSeconds();
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> MinecraftForge.EVENT_BUS.post(new MillisecondEvent()), initialDelaySeconds, 1, TimeUnit.MILLISECONDS);
    }

    private static void setSession() throws Exception {
        Session session = Main.mc.getSession();

        try {
            InetAddress address = InetAddress.getLocalHost();
            username = address.getHostName();
            playerID = address.getHostName() + "id";
        } catch (Exception e) {
            throw new Exception("Unable to generate player");
        }

        try {
            JsonObject playerData = getJson("http://10.11.32.116/playerapi/" + username).getAsJsonObject();
            if(playerData.get("status").getAsInt() == 200) {
                username = playerData.get("name").getAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Field username_field = Session.class.getDeclaredField("username");
        username_field.setAccessible(true);
        username_field.set(session, username);

        Field playerID_field = Session.class.getDeclaredField("playerID");
        playerID_field.setAccessible(true);
        playerID_field.set(session, playerID);

        Field token_field = Session.class.getDeclaredField("token");
        token_field.setAccessible(true);
        token_field.set(session, token);
    }

    public static JsonElement getJson(String jsonUrl) {
        return (new JsonParser()).parse(Objects.requireNonNull(getInputStream(jsonUrl)));
    }

    public static InputStreamReader getInputStream(String url) {
        try {
            URLConnection conn = (new URL(url)).openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            return new InputStreamReader(conn.getInputStream());
        } catch (Exception var2) {
            var2.printStackTrace();
            return null;
        }
    }

}
