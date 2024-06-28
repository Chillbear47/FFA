package me.hi.ffa;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SavekitProtection implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Material blockType = event.getBlock().getType();
        if (blockType == Material.TNT || blockType == Material.WEB) {
            Location loc = event.getBlock().getLocation();

            double x1 = -4.0, y1 = 203.0, z1 = -6.0;
            double x2 = 6.0, y2 = 193.0, z2 = 9.0;

            // Calculate the min and max coordinates
            double minX = Math.min(x1, x2);
            double minY = Math.min(y1, y2);
            double minZ = Math.min(z1, z2);
            double maxX = Math.max(x1, x2);
            double maxY = Math.max(y1, y2);
            double maxZ = Math.max(z1, z2);

            if (loc.getX() >= minX && loc.getX() <= maxX &&
                    loc.getY() >= minY && loc.getY() <= maxY &&
                    loc.getZ() >= minZ && loc.getZ() <= maxZ) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            Material itemType = event.getItem() != null ? event.getItem().getType() : null;
            if (itemType == Material.FLINT_AND_STEEL) {
                Location loc;
                if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    loc = event.getClickedBlock().getLocation();
                } else {
                    loc = event.getPlayer().getLocation();
                }

                double x1 = -4.0, y1 = 203.0, z1 = -6.0;
                double x2 = 6.0, y2 = 193.0, z2 = 9.0;

                // Calculate the min and max coordinates
                double minX = Math.min(x1, x2);
                double minY = Math.min(y1, y2);
                double minZ = Math.min(z1, z2);
                double maxX = Math.max(x1, x2);
                double maxY = Math.max(y1, y2);
                double maxZ = Math.max(z1, z2);

                if (loc.getX() >= minX && loc.getX() <= maxX &&
                        loc.getY() >= minY && loc.getY() <= maxY &&
                        loc.getZ() >= minZ && loc.getZ() <= maxZ) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Location loc = damaged.getLocation();

            double x1 = -4.0, y1 = 203.0, z1 = -6.0;
            double x2 = 6.0, y2 = 193.0, z2 = 9.0;

            // Calculate the min and max coordinates
            double minX = Math.min(x1, x2);
            double minY = Math.min(y1, y2);
            double minZ = Math.min(z1, z2);
            double maxX = Math.max(x1, x2);
            double maxY = Math.max(y1, y2);
            double maxZ = Math.max(z1, z2);

            if (loc.getX() >= minX && loc.getX() <= maxX &&
                    loc.getY() >= minY && loc.getY() <= maxY &&
                    loc.getZ() >= minZ && loc.getZ() <= maxZ) {
                event.setCancelled(true);

            }
        }
    }


    @EventHandler
    public void onPlayerRodDamage(EntityDamageByEntityEvent event) {
        // Coordinates defining the area
        double x1 = -4.0, y1 = 203.0, z1 = -6.0;
        double x2 = 6.0, y2 = 193.0, z2 = 9.0;

        // Calculate the min and max coordinates
        double minX = Math.min(x1, x2);
        double minY = Math.min(y1, y2);
        double minZ = Math.min(z1, z2);
        double maxX = Math.max(x1, x2);
        double maxY = Math.max(y1, y2);
        double maxZ = Math.max(z1, z2);

        // Check if the damager is a fishing rod hook
        if (event.getDamager() instanceof org.bukkit.entity.FishHook) {
            Location loc = event.getEntity().getLocation();

            // Check if the location is within the specified area
            if (loc.getX() >= minX && loc.getX() <= maxX &&
                    loc.getY() >= minY && loc.getY() <= maxY &&
                    loc.getZ() >= minZ && loc.getZ() <= maxZ) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onAnimalDamage(EntityDamageByEntityEvent event) {
        // Check if the damaged entity is a cow or any friendly animal
        if (event.getEntity() instanceof Cow || event.getEntity() instanceof Animals) {
            Location loc = event.getEntity().getLocation();

            double x1 = -4.0, y1 = 203.0, z1 = -6.0;
            double x2 = 6.0, y2 = 193.0, z2 = 9.0;

            // Calculate the min and max coordinates
            double minX = Math.min(x1, x2);
            double minY = Math.min(y1, y2);
            double minZ = Math.min(z1, z2);
            double maxX = Math.max(x1, x2);
            double maxY = Math.max(y1, y2);
            double maxZ = Math.max(z1, z2);

            // Check if the location is within the specified area
            if (loc.getX() >= minX && loc.getX() <= maxX &&
                    loc.getY() >= minY && loc.getY() <= maxY &&
                    loc.getZ() >= minZ && loc.getZ() <= maxZ) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onAnimalRodDamage(EntityDamageByEntityEvent event) {
        // Coordinates defining the area
        double x1 = -4.0, y1 = 203.0, z1 = -6.0;
        double x2 = 6.0, y2 = 193.0, z2 = 9.0;

        // Calculate the min and max coordinates
        double minX = Math.min(x1, x2);
        double minY = Math.min(y1, y2);
        double minZ = Math.min(z1, z2);
        double maxX = Math.max(x1, x2);
        double maxY = Math.max(y1, y2);
        double maxZ = Math.max(z1, z2);

        // Check if the damager is a fishing rod hook
        if (event.getDamager() instanceof org.bukkit.entity.FishHook) {
            // Check if the damaged entity is a cow or any friendly animal
            if (event.getEntity() instanceof Cow || event.getEntity() instanceof Animals) {
                Location loc = event.getEntity().getLocation();

                // Check if the location is within the specified area
                if (loc.getX() >= minX && loc.getX() <= maxX &&
                        loc.getY() >= minY && loc.getY() <= maxY &&
                        loc.getZ() >= minZ && loc.getZ() <= maxZ) {
                    event.setCancelled(true);
                }
            }
        }
    }

}

//pos 1: -4 203, -6
//pos 2: 6, 193, 9