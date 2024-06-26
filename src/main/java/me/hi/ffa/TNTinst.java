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
    public void onTNTPlace(BlockPlaceEvent b) {
        if (Objects.equals(b.getPlayer().getWorld().getName(), "FFA_Sg2")) {
            Block block = b.getBlockPlaced();
            if (block.getType() == Material.TNT) {
                Location loc = block.getLocation();

                // Define the coordinates boundaries
                double x1 = 24.0, y1 = 42.0, z1 = -12.0;
                double x2 = 45.0, y2 = 37.0, z2 = 13.0;

                // Calculate the min and max coordinates
                double minX = Math.min(x1, x2);
                double minY = Math.min(y1, y2);
                double minZ = Math.min(z1, z2);
                double maxX = Math.max(x1, x2);
                double maxY = Math.max(y1, y2);
                double maxZ = Math.max(z1, z2);

                // Check if the block location is within the defined coordinates
                if (loc.getX() >= minX && loc.getX() <= maxX &&
                        loc.getY() >= minY && loc.getY() <= maxY &&
                        loc.getZ() >= minZ && loc.getZ() <= maxZ) {
                    // Cancel the event to prevent TNT placement
                    b.setCancelled(true);
                } else {
                    // Allow TNT to be placed but modify its properties
                    block.setType(Material.AIR);
                    Location new_pos = new Location(block.getWorld(), loc.getX() + 0.5, loc.getY() + 0.5, loc.getZ() + 0.5);
                    TNTPrimed tnt = (TNTPrimed) block.getWorld().spawn(new_pos, TNTPrimed.class);
                    tnt.setFuseTicks(45);
                    tnt.setIsIncendiary(true);
                }
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
