package soys.sxattributeextradigrange.attribute;

import github.saukiya.sxattribute.SXAttribute;
import github.saukiya.sxattribute.data.attribute.AttributeType;
import github.saukiya.sxattribute.data.attribute.SXAttributeData;
import github.saukiya.sxattribute.data.attribute.SubAttribute;
import github.saukiya.sxattribute.data.eventdata.EventData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import soys.sxattributeextradigrange.Main;
import java.util.Collections;
import java.util.List;

public class DigRange extends SubAttribute implements Listener {
    public DigRange() {
        super(Main.plugin, 1, AttributeType.OTHER);
        setPriority(56);
    }

    public static void onEnableEvent(){
        Bukkit.getPluginManager().registerEvents(new DigRange(),Main.plugin);
    }

    @Override
    protected YamlConfiguration defaultConfig(YamlConfiguration config) {
        config.set("DigRange.DiscernName", "范围挖掘");
        config.set("DigRange.CombatPower", 56);
        return config;
    }

    @Override
    public void eventMethod(double[] doubles, EventData eventData) {}

    @Override
    public Object getPlaceholder(double[] values, Player player, String string) {
        return string.equals(getName()) ? values[0] : null;
    }

    @Override
    public List<String> getPlaceholders() {
        return Collections.singletonList(getName());
    }

    @Override
    public void loadAttribute(double[] values, String lore) {
        if (lore.contains(getString("DigRange.DiscernName"))) {
            values[0] = values[0] + getNumber(lore);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST,ignoreCancelled = true)
    public void onBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if(player==null){
            return;
        }
        Main.sendTestModPluginText("玩家存在");

        Block block=event.getBlock();
        if(!Main.isConfigBlock(block)){
            return;
        }
        Main.sendTestModPluginText("方块存在: "+block.getType().getId());

        if(block.hasMetadata("tryDigRange")){
            Main.sendTestModPluginText("方块存在标记,停止连续破坏: "+block.getLocation().toString());
            return;
        }
        Main.sendTestModPluginText("方块无标记: "+block.getLocation().toString());

        ItemStack hand=player.getItemInHand();
        if(hand==null || hand.getType()==Material.AIR){
            return;
        }
        Main.sendTestModPluginText("手持物品存在");

        SXAttributeData sxAttributeData=SXAttribute.getApi().loadItemData(player,hand);
        int range=(int)sxAttributeData.getValues(getName())[0];
        Main.sendTestModPluginText("range: "+range);

        if(range>0){
            breakArea(player,block.getLocation(), range);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(player==null){
            return;
        }
        Main.sendTestModPluginText("interact - 玩家存在");

        Block block=event.getClickedBlock();
        if(!Main.isConfigBlock(block)){
            return;
        }
        Main.sendTestModPluginText("interact - 方块存在: "+block.getType().getId());

        player.setMetadata("PlayerFace",new FixedMetadataValue(Main.plugin,event.getBlockFace()));
    }

    private void breakArea(Player player, Location start, int range) {
        ItemStack itemStack=player.getItemInHand();
        BlockFace blockFace=null;
        List<MetadataValue> metadataValues=player.getMetadata("PlayerFace");
        for (MetadataValue metadataValue : metadataValues) {
            if(metadataValue.getOwningPlugin() instanceof Main){
                blockFace=(BlockFace)metadataValue.value();
            }
        }
        boolean xz=false;
        boolean xy=false;
        boolean zy=false;
        if(blockFace==BlockFace.NORTH || blockFace==BlockFace.SOUTH){
            xy=true;
        }
        if(blockFace==BlockFace.EAST || blockFace==BlockFace.WEST){
            zy=true;
        }
        if(blockFace==BlockFace.UP || blockFace==BlockFace.DOWN){
            xz=true;
        }
        Main.sendTestModPluginText("range xz: "+xz);
        Main.sendTestModPluginText("range xy: "+xy);
        Main.sendTestModPluginText("range zy: "+zy);
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                Block relativeBlock = start.getBlock().getRelative(xz || xy?x:0, xy?z:zy?x:0, xz?z:zy?z:0);
                Main.sendTestModPluginText("range x: "+x);
                Main.sendTestModPluginText("range z: "+z);

                // 检查方块是否可以被挖掘
                if (canBreakBlock(player,itemStack,relativeBlock)) {
                    Main.sendTestModPluginText("检测为可挖掘");
                    relativeBlock.setMetadata("tryDigRange",new FixedMetadataValue(Main.plugin,true));
                    Main.sendTestModPluginText("对方块注入检测标记");
                    BlockBreakEvent event=new BlockBreakEvent(relativeBlock,player);
                    Bukkit.getPluginManager().callEvent(event);
                    event.getBlock().removeMetadata("tryDigRange", Main.plugin);
                    if(!event.isCancelled()){
                        Main.sendTestModPluginText("成功破坏 -> x:"+x+" ,z:"+z);
                        event.getBlock().breakNaturally(itemStack);
                    }

                    /*
                    Main.sendTestModPluginText("开始触发其他破坏事件检测");
                    if(event.isCancelled() || event.getBlock().hasMetadata("tryDigRange-unBreak")) {
                        Main.sendTestModPluginText("无法破坏,删除检测标记 -> x:"+x+" ,z:"+z);
                        event.getBlock().removeMetadata("tryDigRange", Main.plugin);
                        event.getBlock().removeMetadata("tryDigRange-unBreak", Main.plugin);
                    }else {

                    }
                    */
                }
            }
        }
    }

    private boolean canBreakBlock(Player player, ItemStack itemStack, Block block) {
        if(!Main.isConfigItem(itemStack)){
            return false;
        }
        if(!Main.isConfigBlock(block)){
            return false;
        }
        // 这里可以添加方块是否可以被挖掘的逻辑，例如检查方块类型或硬度
        // 这里默认所有方块都可以被挖掘
        return true;
    }

}
