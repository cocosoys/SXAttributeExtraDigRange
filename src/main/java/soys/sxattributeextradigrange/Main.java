package soys.sxattributeextradigrange;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import soys.sxattributeextradigrange.attribute.DigRange;

public final class Main extends JavaPlugin {
    public static Main plugin;
    public static FileConfiguration config;

    public static boolean testMod=false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin=this;
        (new DigRange()).registerAttribute();
        saveDefaultConfig();
        config=getConfig();
        DigRange.onEnableEvent();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length==1){
            if(args[0].equalsIgnoreCase("reload")) {
                reloadConfig();
                config=getConfig();
                sendPluginText("成功加载配置文件");
            }
            if(args[0].equalsIgnoreCase("testmod")){
                testMod=!testMod;
                sendPluginText("测试模式信息输出至后台,测试模式修改为 :" +testMod);
            }
        }
        return false;
    }

    public static boolean isConfigBlock(Block block){
        if(block==null || block.getType()== Material.AIR){
            return false;
        }
        int id=block.getType().getId();
        byte damage=block.getData();
        sendTestModPluginText("damage: "+damage);
        String idDamage= String.valueOf(id);
        if(damage!=0){
            idDamage=idDamage+":"+damage;
        }
        sendTestModPluginText("idDamage: "+idDamage);
        if(!config.getStringList("whiteblock.pickaxe").contains(idDamage)){
            sendTestModPluginText("不存在该匹配: "+idDamage);
            return false;
        }
        return true;
    }

    public static boolean isConfigItem(ItemStack itemStack){
        int id=itemStack.getType().getId();
        if(!config.getIntegerList("tool.pickaxe").contains(id)){
            return false;
        }
        return true;
    }

    public static void sendTestModPluginText(String message){
        if(testMod) {
            Bukkit.getLogger().info("[" + plugin.getName() + "][TESTMOD] -> " + message);
        }
    }

    public static void sendPluginText(String message){
        Bukkit.getLogger().info("["+plugin.getName()+"]"+message);
    }

}
