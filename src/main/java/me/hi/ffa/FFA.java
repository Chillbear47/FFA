//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
package me.hi.ffa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

public final class FFA extends JavaPlugin implements Listener, CommandExecutor {
    public HashMap<Player, Integer> kills = new HashMap();
    public HashMap<Player, Integer> breakperm = new HashMap();
    public HashMap<Player, ItemStack[]> invs = new HashMap();
    public HashMap<Player, Integer> kill_streak = new HashMap();
    //public HashMap<Player, Integer> points = new HashMap<>();
    public Scoreboard scoreboard1;

    public void onEnable() {
        System.out.println("The plugin has started");
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(new savekit(), this);
        this.getServer().getPluginManager().registerEvents(new TNTinst(), this);
        this.getServer().getPluginManager().registerEvents(new Eventhandlers(), this);
        this.getServer().getPluginManager().registerEvents(new SpawnProtection(), this);
        this.getServer().getPluginManager().registerEvents(new SavekitProtection(), this);
        this.getCommand("break").setExecutor(this);
        this.getCommand("savekit").setExecutor(new savekit());
        this.breakperm = new HashMap();
        Iterator var1 = Bukkit.getWorld("FFA_Sg2").getPlayers().iterator();

        while(var1.hasNext()) {
            Player p = (Player)var1.next();
            this.kill_streak.put(p, 0);
            int kl = this.getkillsfromfile(p);
            this.kills.put(p, kl);
            this.breakperm.put(p, 0);
            this.createNewsScoreboard(p, kl);
            List<ItemStack> temp_stacks = this.getinvs(p);
            ItemStack[] temp_array = (ItemStack[])temp_stacks.toArray(new ItemStack[0]);
            this.invs.put(p, temp_array);
        }

    }


    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        System.out.println("A player has teleported");
        Player player = event.getPlayer();
        if (event.getTo().getWorld().getName().equals("FFA_Sg2") && !Objects.equals(event.getFrom().getWorld().getName(), "FFA_Sg2")) {
            player.sendMessage("we are in FFA");
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
            Location a = Bukkit.getWorld("FFA_Sg2").getSpawnLocation();
            player.setBedSpawnLocation(a, true);
            player.getInventory().clear();
            List<ItemStack> temp_stacks = this.getinvs(player);
            Iterator var5 = temp_stacks.iterator();

            while(var5.hasNext()) {
                ItemStack i = (ItemStack)var5.next();
                player.getInventory().addItem(new ItemStack[]{i});
            }

            this.invs.put(player, player.getInventory().getContents());
            player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
            player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
            player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
            player.setFoodLevel(20);
            player.setSaturation(1.0E9F);
            int kill = this.getkillsfromfile(player);
            //int pts = this.getpointsfromfile(player);
            this.kill_streak.put(player, 0);
            this.createNewsScoreboard(player, kill);
            this.kills.put(player, kill);
            //this.points.put(player, pts);
            this.breakperm.put(player, 0);
            player.sendTitle(ChatColor.YELLOW + "" + ChatColor.BOLD + "Welcome to the FFA", ChatColor.RED + "You can change your kit with /savekit");
        }

    }

    @EventHandler
    public void death(PlayerDeathEvent event) {
        Player player = event.getEntity().getPlayer();
        if (player.getWorld().getName().equals("FFA_Sg2")) {
            player.getInventory().clear();
            player.getInventory().setContents((ItemStack[])this.invs.get(player));
            player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
            player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
            player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
            player.setFoodLevel(20);
            player.setSaturation(1.0E9F);
            Player killer = player.getKiller();
            this.kills.put(killer, (Integer)this.kills.get(killer) + 1);
            this.kill_streak.put(killer, (Integer)this.kill_streak.get(killer) + 1);
            this.kill_streak.put(player, 0);
            //int victimPoints = this.points.get(player);
            //int killerPoints = this.points.get(killer);
            //int pointsGained = (int) (0.05 * victimPoints);
            //this.points.put(killer, killerPoints + pointsGained);
            //this.points.put(player, victimPoints - pointsGained);
            //this.refreshKillsInFile(killer);
            //this.refreshPointsInFile(killer);
            //this.refreshPointsInFile(player);
            this.createNewsScoreboard(killer, (Integer)this.kills.get(killer));
            this.createNewsScoreboard(player, (Integer)this.kills.get(player));
            this.refreshKillsInFile(killer);
            killer.setHealth(20.0);
            killer.getInventory().setContents((ItemStack[])this.invs.get(killer));
            if ((Integer)this.kill_streak.get(killer) % 5 == 0) {
                Iterator var4 = Bukkit.getWorld("FFA_Sg2").getPlayers().iterator();

                while(var4.hasNext()) {
                    Player p = (Player)var4.next();
                    p.sendMessage(ChatColor.RED + killer.getName() + ChatColor.RESET + " has a killstreak of " + ChatColor.BOLD + " " + ChatColor.RED + this.kill_streak.get(killer));
                }
            }
        }

    }

    @EventHandler
    public void refillfood(FoodLevelChangeEvent fl) {
        if (fl.getEntity() instanceof Player) {
            Player p = (Player)fl.getEntity();
            if (Objects.equals(p.getWorld().getName(), "FFA_Sg2")) {
                fl.setCancelled(true);
            }
        }

    }



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



    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getName().equals("FFA_Sg2")) {
            Block brokenBlock = event.getBlock();
            if (brokenBlock.getType() != Material.WEB) {
                if ((Integer)this.breakperm.get(player) == 0) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
            }
        }

    }

    @EventHandler
    public void onplacecobweb(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if (player.getWorld().getName().equals("FFA_Sg2") && e.getBlockPlaced().getType() == Material.WEB) {
            Bukkit.getScheduler().runTaskLater(this, () -> {
                e.getBlockPlaced().setType(Material.AIR);
            }, 200L);
        }

    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player)sender;
            if (Objects.equals(p.getWorld().getName(), "FFA_Sg2")) {
                if (Objects.equals(args[0], "true")) {
                    this.breakperm.put(p, 1);
                }

                if (Objects.equals(args[0], "false")) {
                    this.breakperm.put(p, 0);
                }
            }
        }

        return true;
    }

    public int getkillsfromfile(Player player) {
        int x = 0;
        String path = this.getServer().getWorldContainer().getAbsolutePath();
        path = path + "/FFA_info.json";
        File file = new File(path);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(new File(path));
            if (jsonNode.path("Players").has(player.getName())) {
                x = jsonNode.path("Players").path(player.getName()).path("kills").asInt();
            } else {
                ((ObjectNode)jsonNode.path("Players")).putObject(player.getName()).put("kills", 0).put("kit", false);
                objectMapper.writeValue(file, jsonNode);
            }
        } catch (IOException var7) {
            var7.printStackTrace();
        }

        return x;
    }

    public List<ItemStack> getinvs(Player player) {
        String s = "false";
        String path = this.getServer().getWorldContainer().getAbsolutePath();
        path = path + "/FFA_info.json";
        new File(path);
        ItemStack flintAndSteel = new ItemStack(Material.FLINT_AND_STEEL);
        flintAndSteel.setDurability((short)64);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(new File(path));
            if (jsonNode.path("Players").has(player.getName())) {
                s = jsonNode.path("Players").path(player.getName()).path("kit").asText();
            }
        } catch (IOException var15) {
            var15.printStackTrace();
        }

        ArrayList temp_inv;
        if (s != "false") {
            temp_inv = new ArrayList();
            String[] Items = s.split(",");
            String[] var8 = Items;
            int var9 = Items.length;

            for(int var10 = 0; var10 < var9; ++var10) {
                String txt = var8[var10];
                String[] info = txt.split(":");
                Material material = Material.AIR;
                if (!Objects.equals(info[0], "null")) {
                    material = Material.valueOf(info[0]);
                }

                int amount = Integer.parseInt(info[1]);
                if (material != Material.FLINT_AND_STEEL) {
                    temp_inv.add(new ItemStack(material, amount));
                } else {
                    temp_inv.add(flintAndSteel);
                }
            }

            return temp_inv;
        } else {
            temp_inv = new ArrayList();
            temp_inv.add(new ItemStack(Material.STONE_SWORD));
            temp_inv.add(new ItemStack(Material.FISHING_ROD));
            temp_inv.add(flintAndSteel);
            temp_inv.add(new ItemStack(Material.WEB));
            temp_inv.add(new ItemStack(Material.TNT));
            return temp_inv;
        }
    }

    public void refreshKillsInFile(Player player) {
        String path = this.getServer().getWorldContainer().getAbsolutePath() + "/FFA_info.json";
        File file = new File(path);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(file);
            ((ObjectNode)jsonNode.path("Players")).putObject(player.getName()).put("kills", (Integer)this.kills.get(player)).put("kit", false);
            objectMapper.writeValue(file, jsonNode);
        } catch (IOException var6) {
            var6.printStackTrace();
        }

    }

    @EventHandler
    public void savekitlistener(PlayerInteractAtEntityEvent e) {
        if (Objects.equals(e.getRightClicked().getName(), "SAVE KIT FFA")) {
            Inventory inv = e.getPlayer().getInventory();
            this.savekitinfile(e.getPlayer(), inv);
            e.getPlayer().sendMessage(ChatColor.GREEN + "kit saved");
            e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
        }

    }

    public void savekitinfile(Player player, Inventory inv) {
        String path = this.getServer().getWorldContainer().getAbsolutePath() + "/FFA_info.json";
        File file = new File(path);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(file);
            int kills = jsonNode.path("Players").path(player.getName()).get("kills").asInt();
            int j = 0;
            StringBuilder s = new StringBuilder();
            ItemStack[] var10 = inv.getContents();
            int var11 = var10.length;

            for(int var12 = 0; var12 < var11; ++var12) {
                ItemStack i = var10[var12];
                if (i != null) {
                    Material type = i.getType();
                    int amount = i.getAmount();
                    s.append(type.toString()).append(":").append(amount).append(",");
                } else {
                    s.append("null").append(":").append("0").append(",");
                }

                ++j;
            }

            ((ObjectNode)jsonNode.path("Players")).putObject(player.getName()).put("kills", kills).put("kit", s.toString());
            objectMapper.writeValue(file, jsonNode);
            this.invs.put(player, inv.getContents());
        } catch (IOException var16) {
            var16.printStackTrace();
        }

    }
/*
    public int getpointsfromfile(Player player) {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("FFA_info.json");

        try {
            JsonNode root = mapper.readTree(file);
            return root.path(player.getName()).path("points").asInt();
        } catch (IOException var5) {
            var5.printStackTrace();
            return 400;
        }
    }

    public void refreshPointsInFile(Player player) {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File("FFA_info.json");

        try {
            JsonNode root = mapper.readTree(file);
            ((ObjectNode)root.path(player.getName())).put("points", (Integer)this.points.get(player));
            mapper.writeValue(file, root);
        } catch (IOException var5) {
            var5.printStackTrace();
        }

    }
*/
    public void onDisable() {
    }
}


/*
package me.hi.ffa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public final class FFA extends JavaPlugin implements Listener, CommandExecutor {
    public HashMap<Player, Integer> kills = new HashMap();
    public HashMap<Player, Integer> breakperm = new HashMap();
    public HashMap<Player, ItemStack[]> invs = new HashMap();
    public HashMap<Player, Integer> kill_streak = new HashMap();

    public FFA() {
    }

    public void onEnable() {
        System.out.println("The plugin has started");
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(new savekit(), this);
        this.getServer().getPluginManager().registerEvents(new TNTinst(), this);
        this.getCommand("break").setExecutor(this);
        this.getCommand("savekit").setExecutor(new savekit());
        this.breakperm = new HashMap();
        Iterator var1 = Bukkit.getWorld("FFA_Sg2").getPlayers().iterator();

        while(var1.hasNext()) {
            Player p = (Player)var1.next();
            this.kill_streak.put(p, 0);
            int kl = this.getkillsfromfile(p);
            this.kills.put(p, kl);
            this.breakperm.put(p, 0);
            this.createNewsScoreboard(p, kl);
            List<ItemStack> temp_stacks = this.getinvs(p);
            ItemStack[] temp_array = (ItemStack[])temp_stacks.toArray(new ItemStack[0]);
            this.invs.put(p, temp_array);
        }

    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        System.out.println("A player has teleported");
        Player player = event.getPlayer();
        if (event.getTo().getWorld().getName().equals("FFA_Sg2") && !Objects.equals(event.getFrom().getWorld().getName(), "FFA_Sg2")) {
            player.sendMessage("we are in FFA");
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
            Location a = Bukkit.getWorld("FFA_Sg2").getSpawnLocation();
            player.setBedSpawnLocation(a, true);
            player.getInventory().clear();
            List<ItemStack> temp_stacks = this.getinvs(player);
            Iterator var5 = temp_stacks.iterator();

            while(var5.hasNext()) {
                ItemStack i = (ItemStack)var5.next();
                player.getInventory().addItem(new ItemStack[]{i});
            }

            this.invs.put(player, player.getInventory().getContents());
            player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
            player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
            player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
            player.setFoodLevel(20);
            player.setSaturation(1.0E9F);
            int kill = this.getkillsfromfile(player);
            this.kill_streak.put(player, 0);
            this.createNewsScoreboard(player, kill);
            this.kills.put(player, kill);
            this.breakperm.put(player, 0);
            player.sendTitle(ChatColor.YELLOW + "" + ChatColor.BOLD + "Welcome to the FFA", ChatColor.RED + "You can change your kit with /savekit");
        }

    }

    @EventHandler
    public void death(PlayerDeathEvent event) {
        Player player = event.getEntity().getPlayer();
        if (player.getWorld().getName().equals("FFA_Sg2")) {
            player.getInventory().clear();
            player.getInventory().setContents((ItemStack[])this.invs.get(player));
            player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
            player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
            player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
            player.setFoodLevel(20);
            player.setSaturation(1.0E9F);
            Player killer = player.getKiller();
            this.kills.put(killer, (Integer)this.kills.get(killer) + 1);
            this.kill_streak.put(killer, (Integer)this.kill_streak.get(killer) + 1);
            this.kill_streak.put(player, 0);
            this.createNewsScoreboard(killer, (Integer)this.kills.get(killer));
            this.createNewsScoreboard(player, (Integer)this.kills.get(player));
            this.refreshKillsInFile(killer);
            killer.setHealth(20.0);
            killer.getInventory().setContents((ItemStack[])this.invs.get(killer));
            if ((Integer)this.kill_streak.get(killer) % 5 == 0) {
                Iterator var4 = Bukkit.getWorld("FFA_Sg2").getPlayers().iterator();

                while(var4.hasNext()) {
                    Player p = (Player)var4.next();
                    p.sendMessage(ChatColor.RED + killer.getName() + ChatColor.RESET + " has a killstreak of " + ChatColor.BOLD + " " + ChatColor.RED + this.kill_streak.get(killer));
                }
            }
        }

    }

    @EventHandler
    public void refillfood(FoodLevelChangeEvent fl) {
        if (fl.getEntity() instanceof Player) {
            Player p = (Player)fl.getEntity();
            if (Objects.equals(p.getWorld().getName(), "FFA_Sg2")) {
                fl.setCancelled(true);
            }
        }

    }

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


    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        event.setCancelled(true);
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
    public void Drop(PlayerDropItemEvent event) {event.setCancelled(true);}

    @EventHandler
    public void onChestOpen(InventoryOpenEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof Chest) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Location loc = damaged.getLocation();

            double x1 = 24.0, y1 = 42.0, z1 = -12.0;
            double x2 = 45.0, y2 = 37.0, z2 = 13.0;

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
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().getName().equals("FFA_Sg2")) {
            Block brokenBlock = event.getBlock();
            if (brokenBlock.getType() != Material.WEB) {
                if ((Integer)this.breakperm.get(player) == 0) {
                    event.setCancelled(true);
                }
            } else {
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
            }
        }

    }

    @EventHandler
    public void onplacecobweb(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        if (player.getWorld().getName().equals("FFA_Sg2") && e.getBlockPlaced().getType() == Material.WEB) {
            Bukkit.getScheduler().runTaskLater(this, () -> {
                e.getBlockPlaced().setType(Material.AIR);
            }, 200L);
        }

    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player)sender;
            if (Objects.equals(p.getWorld().getName(), "FFA_Sg2")) {
                if (Objects.equals(args[0], "true")) {
                    this.breakperm.put(p, 1);
                }

                if (Objects.equals(args[0], "false")) {
                    this.breakperm.put(p, 0);
                }
            }
        }

        return true;
    }

    public int getkillsfromfile(Player player) {
        int x = 0;
        String path = this.getServer().getWorldContainer().getAbsolutePath();
        path = path + "/FFA_info.json";
        File file = new File(path);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(new File(path));
            if (jsonNode.path("Players").has(player.getName())) {
                x = jsonNode.path("Players").path(player.getName()).path("kills").asInt();
            } else {
                ((ObjectNode)jsonNode.path("Players")).putObject(player.getName()).put("kills", 0).put("kit", false);
                objectMapper.writeValue(file, jsonNode);
            }
        } catch (IOException var7) {
            var7.printStackTrace();
        }

        return x;
    }

    public List<ItemStack> getinvs(Player player) {
        String s = "false";
        String path = this.getServer().getWorldContainer().getAbsolutePath();
        path = path + "/FFA_info.json";
        new File(path);
        ItemStack flintAndSteel = new ItemStack(Material.FLINT_AND_STEEL);
        flintAndSteel.setDurability((short)64);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(new File(path));
            if (jsonNode.path("Players").has(player.getName())) {
                s = jsonNode.path("Players").path(player.getName()).path("kit").asText();
            }
        } catch (IOException var15) {
            var15.printStackTrace();
        }

        ArrayList temp_inv;
        if (s != "false") {
            temp_inv = new ArrayList();
            String[] Items = s.split(",");
            String[] var8 = Items;
            int var9 = Items.length;

            for(int var10 = 0; var10 < var9; ++var10) {
                String txt = var8[var10];
                String[] info = txt.split(":");
                Material material = Material.AIR;
                if (!Objects.equals(info[0], "null")) {
                    material = Material.valueOf(info[0]);
                }

                int amount = Integer.parseInt(info[1]);
                if (material != Material.FLINT_AND_STEEL) {
                    temp_inv.add(new ItemStack(material, amount));
                } else {
                    temp_inv.add(flintAndSteel);
                }
            }

            return temp_inv;
        } else {
            temp_inv = new ArrayList();
            temp_inv.add(new ItemStack(Material.STONE_SWORD));
            temp_inv.add(new ItemStack(Material.FISHING_ROD));
            temp_inv.add(flintAndSteel);
            temp_inv.add(new ItemStack(Material.WEB));
            temp_inv.add(new ItemStack(Material.TNT));
            return temp_inv;
        }
    }

    public void refreshKillsInFile(Player player) {
        String path = this.getServer().getWorldContainer().getAbsolutePath() + "/FFA_info.json";
        File file = new File(path);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(file);
            ((ObjectNode)jsonNode.path("Players")).putObject(player.getName()).put("kills", (Integer)this.kills.get(player)).put("kit", false);
            objectMapper.writeValue(file, jsonNode);
        } catch (IOException var6) {
            var6.printStackTrace();
        }

    }

    @EventHandler
    public void savekitlistener(PlayerInteractAtEntityEvent e) {
        if (Objects.equals(e.getRightClicked().getName(), "SAVE KIT FFA")) {
            Inventory inv = e.getPlayer().getInventory();
            this.savekitinfile(e.getPlayer(), inv);
            e.getPlayer().sendMessage(ChatColor.GREEN + "kit saved");
            e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
        }

    }

    public void savekitinfile(Player player, Inventory inv) {
        String path = this.getServer().getWorldContainer().getAbsolutePath() + "/FFA_info.json";
        File file = new File(path);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(file);
            int kills = jsonNode.path("Players").path(player.getName()).get("kills").asInt();
            int j = 0;
            StringBuilder s = new StringBuilder();
            ItemStack[] var10 = inv.getContents();
            int var11 = var10.length;

            for(int var12 = 0; var12 < var11; ++var12) {
                ItemStack i = var10[var12];
                if (i != null) {
                    Material type = i.getType();
                    int amount = i.getAmount();
                    s.append(type.toString()).append(":").append(amount).append(",");
                } else {
                    s.append("null").append(":").append("0").append(",");
                }

                ++j;
            }

            ((ObjectNode)jsonNode.path("Players")).putObject(player.getName()).put("kills", kills).put("kit", s.toString());
            objectMapper.writeValue(file, jsonNode);
            this.invs.put(player, inv.getContents());
        } catch (IOException var16) {
            var16.printStackTrace();
        }

    }

    public void onDisable() {
    }
}
*/