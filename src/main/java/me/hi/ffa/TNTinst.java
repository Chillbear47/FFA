//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package me.hi.ffa;

import java.util.Objects;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

public class TNTinst implements Listener {
    public TNTinst() {
    }

    @EventHandler
    public void TNT(BlockPlaceEvent b) {
        if (Objects.equals(b.getPlayer().getWorld().getName(), "FFA_Sg2")) {
            Block block = b.getBlockPlaced();
            if (block.getType() == Material.TNT) {
                Location pos = block.getLocation();
                block.setType(Material.AIR);
                Location new_pos = new Location(block.getWorld(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                TNTPrimed tnt = (TNTPrimed)block.getWorld().spawn(new_pos, TNTPrimed.class);
                tnt.setFuseTicks(45);
                tnt.setIsIncendiary(true);
            }
        }

    }

    @EventHandler
    public void RemTNT(EntityExplodeEvent e) {
        if (Objects.equals(e.getEntity().getWorld().getName(), "FFA_Sg2")) {
            e.setCancelled(true);
        }

    }
}
