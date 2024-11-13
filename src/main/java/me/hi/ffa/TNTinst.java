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

                // Define the first set of coordinates boundaries
                double x1 = 24.0, y1 = 42.0, z1 = -12.0;
                double x2 = 45.0, y2 = 37.0, z2 = 13.0;

                // Calculate the min and max coordinates for the first set
                double minX1 = Math.min(x1, x2);
                double minY1 = Math.min(y1, y2);
                double minZ1 = Math.min(z1, z2);
                double maxX1 = Math.max(x1, x2);
                double maxY1 = Math.max(y1, y2);
                double maxZ1 = Math.max(z1, z2);

                // Define the second set of coordinates boundaries
                double x3 = -4.0, y3 = 203.0, z3 = -6.0;
                double x4 = 6.0, y4 = 193.0, z4 = 9.0;

                // Calculate the min and max coordinates for the second set
                double minX2 = Math.min(x3, x4);
                double minY2 = Math.min(y3, y4);
                double minZ2 = Math.min(z3, z4);
                double maxX2 = Math.max(x3, x4);
                double maxY2 = Math.max(y3, y4);
                double maxZ2 = Math.max(z3, z4);

                // Check if the block location is within either of the defined coordinates
                boolean withinFirstSet = loc.getX() >= minX1 && loc.getX() <= maxX1 &&
                        loc.getY() >= minY1 && loc.getY() <= maxY1 &&
                        loc.getZ() >= minZ1 && loc.getZ() <= maxZ1;

                boolean withinSecondSet = loc.getX() >= minX2 && loc.getX() <= maxX2 &&
                        loc.getY() >= minY2 && loc.getY() <= maxY2 &&
                        loc.getZ() >= minZ2 && loc.getZ() <= maxZ2;

                if (withinFirstSet || withinSecondSet) {
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


/*
public void createNewsScoreboard(Player player, Integer kill) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("Player", "yummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "⚔ FFA ⚔");
        this.kills.put(player, kill);
        objective.getScore(ChatColor.WHITE + "    ").setScore(11);
        objective.getScore(ChatColor.RED + "" + ChatColor.BOLD + "⚕ Kills ⚕                 ").setScore(10);
        objective.getScore(ChatColor.YELLOW + ((Integer)this.kills.get(player)).toString()).setScore(9);
        objective.getScore(ChatColor.WHITE + "     ").setScore(8);
        objective.getScore(ChatColor.AQUA + " ※ Kill Streak ※").setScore(7);
        objective.getScore(ChatColor.RED + ((Integer)this.kill_streak.get(player)).toString()).setScore(6);
        objective.getScore(ChatColor.WHITE + "   ").setScore(5);
        objective.getScore(ChatColor.GOLD + "Points                ").setScore(4);
        objective.getScore(ChatColor.LIGHT_PURPLE + "0").setScore(3);
        objective.getScore(ChatColor.WHITE + "  ").setScore(2);
        objective.getScore(ChatColor.YELLOW + "178.33.213.67").setScore(1);
        player.setScoreboard(scoreboard);
        Objective obh = scoreboard.registerNewObjective("health", Criterias.HEALTH);
        obh.setDisplayName(ChatColor.RED + "❤");
        obh.setDisplaySlot(DisplaySlot.BELOW_NAME);
        player.setScoreboard(scoreboard);
    }

 */