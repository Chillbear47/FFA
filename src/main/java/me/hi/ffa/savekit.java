//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.hi.ffa;

import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class savekit implements CommandExecutor, Listener {
    public savekit() {
    }

    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (sender instanceof Player) {
            Player player = (Player)sender;
            if (Objects.equals(player.getWorld().getName(), "FFA_Sg2")) {
                Location loc = new Location(player.getWorld(), 0.0, 200.0, 0.0);
                player.teleport(loc);
                player.getInventory().clear();
                ItemStack flintAndSteel = new ItemStack(Material.FLINT_AND_STEEL);
                flintAndSteel.setDurability((short)64);
                player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.STONE_SWORD), new ItemStack(Material.FISHING_ROD), flintAndSteel, new ItemStack(Material.WEB), new ItemStack(Material.TNT)});
            }
        }

        return true;
    }
}
