package fr.perrier.guard.manager.commands;

import fr.perrier.guard.Guard;
import fr.perrier.guard.commands.annotations.Command;
import fr.perrier.guard.commands.annotations.Param;
import fr.perrier.guard.utils.ChatUtil;
import fr.perrier.guard.utils.Messages;
import fr.perrier.guard.utils.commands.CommandsUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.AbstractMap;

public class LookUp {

    @Command(names = CommandsUtils.LOOKUP, perm = "guard.lookup")
    public static void onLookUP(Player player, @Param(name = "Player")String starget) {
        if(!Guard.getDefaultConfig().getBoolean("commands.lookup.active")) {
            Messages.send(player,Messages.COMMANDDISABLE);
            return;
        }
        Player target = Bukkit.getPlayer(starget);
        if(target == null) {
            Messages.send(player,Messages.PLAYERNOTFOUND);
            return;
        }

        player.sendMessage(ChatUtil.prefix("&cScanning.."));

        AbstractMap.SimpleEntry<Integer, StringBuffer> responseMinecraft = sendRequest("https://api.ashcon.app/mojang/v2/user/" + target.getName());
        if(responseMinecraft.getValue().toString().isEmpty())
            sendRequest("https://api.mojang.com/users/profiles/minecraft/" + target.getName());
        JSONObject jsonMinecraftObject = new JSONObject(responseMinecraft.getValue().toString());

        AbstractMap.SimpleEntry<Integer,StringBuffer> responseIP = sendRequest("http://ip-api.com/json/" + target.getAddress().getAddress().getHostAddress());
        JSONObject jsonIPObject = new JSONObject(responseIP.getValue().toString());


        for(String lore : Messages.COMMANDS_LOOKUP_MESSAGE.getLore()) {
            player.sendMessage(ChatUtil.translate(lore
                    .replace("%target%", target.getName())
                    .replace("%uuid%", target.getUniqueId().toString())
                    .replace("%premium%", (responseMinecraft.getKey() == 200 ? "&a&l✓" : "&c&l✗"))
                    .replace("%date%",(responseMinecraft.getKey() == 200 ? jsonMinecraftObject.getString("created_at") : "&c???"))
                    .replace("%ip%", target.getAddress().getAddress().getHostAddress())
                    .replace("%country%", (!jsonIPObject.getString("status").equalsIgnoreCase("fail") ? jsonIPObject.getString("country") : "&c???"))
                    .replace("%isp%", (!jsonIPObject.getString("status").equalsIgnoreCase("fail") ? jsonIPObject.getString("isp") : "&c???"))
            ));
        }

    }

    private static AbstractMap.SimpleEntry<Integer,StringBuffer> sendRequest(String url) {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return new AbstractMap.SimpleEntry<>(responseCode,response);
            } else {
                return new AbstractMap.SimpleEntry<>(responseCode,new StringBuffer());
            }
        }catch (Exception e1) {
            e1.printStackTrace();
        }
        return new AbstractMap.SimpleEntry<>(-1,new StringBuffer());
    }
}
