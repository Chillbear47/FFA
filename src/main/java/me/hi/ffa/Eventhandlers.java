package me.hi.ffa;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.Objects;

public class Eventhandlers implements Listener {


    @EventHandler
    public void Drop(PlayerDropItemEvent event) {
        if (event.getPlayer().getWorld().getName().equals("FFA_Sg2")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (event.getBlock().getWorld().getName().equals("FFA_Sg2")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        if (event.getBlock().getWorld().getName().equals("FFA_Sg2")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent lp) {
        World world = lp.getWorld();

        if (Objects.equals(lp.getWorld().getName(), "FFA_Sg2")) {
            lp.setCancelled(true);
            world.setStorm(true);
            world.setThundering(true);

            Bukkit.getLogger().info("Rain has been toggled off in the world: " + world.getName());
        }
    }


    @EventHandler
    public void onChestOpen(InventoryOpenEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Chest) {
            Chest chest = (Chest) holder;
            // Check if the chest is in the world named "FFA_Sg2"
            if (chest.getWorld().getName().equals("FFA_Sg2")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onButtonInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null) {
                Material blockType = clickedBlock.getType();
                if (blockType == Material.STONE_BUTTON || blockType == Material.WOOD_BUTTON) {
                    if (clickedBlock.getWorld().getName().equals("FFA_Sg2")) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }


}
