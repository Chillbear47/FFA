package me.hi.ffa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

import java.util.*;
import java.io.File;

public final class FFA extends JavaPlugin implements Listener, CommandExecutor {

    private static final String WORLD_NAME = "FFA_Sg2";
    public final File FFA_FILE = new File(getDataFolder(), "FFA_info.json");

    private final HashMap<Player, Integer> kills = new HashMap<>();
    private final HashMap<Player, Integer> breakperm = new HashMap<>();
    private final HashMap<Player, ItemStack[]> invs = new HashMap<>();
    private final HashMap<Player, Integer> kill_streak = new HashMap<>();
    private final HashMap<Player, Integer> points = new HashMap<>();
    private final HashMap<Player, String> kit = new HashMap<>();
    private final HashMap<Player, Integer> deaths = new HashMap<>();
    private final HashMap<Player, Double> multiplier = new HashMap<>();

    @Override
    public void onEnable() {
        try {
            FileHandler fileHandler = new FileHandler("error.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            File dataFolder = getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs(); // Lager mappen hvis den ikke finnes
                logger.info("Plugin-mappen ble opprettet: " + dataFolder.getPath());
            }

            if (FFA_FILE.createNewFile()) {
                logger.info("FFA_info.json filen ble opprettet.");
                // Initialiser filen med grunnstrukturen
                initFFA_info();
            } else {
                logger.info("FFA_info.json filen finnes allerede.");
            }

            this.getServer().getPluginManager().registerEvents(this, this);
            this.getServer().getPluginManager().registerEvents(new savekit(), this);
            this.getServer().getPluginManager().registerEvents(new TNTinst(), this);
            this.getServer().getPluginManager().registerEvents(new Eventhandlers(), this);
            this.getServer().getPluginManager().registerEvents(new SpawnProtection(), this);
            this.getCommand("break").setExecutor(this);
            this.getCommand("savekit").setExecutor(new savekit());
        } catch(Exception e) {
                logger.severe("En feil oppstod under registrering av events eller kommandoer: " + e.getMessage());
                e.printStackTrace(); // Skriver stacktrace til konsollen for debugging
        }

        try {
            this.getServer().getPluginManager().registerEvents(new kits(this), this);
            this.getCommand("kits").setExecutor(new kits(this));
            this.getCommand("editkits").setExecutor(new kits(this));
        } catch(Exception e) {
            logger.severe("En feil oppstod under registrering av events eller kommandoer (kits.java): " + e.getMessage());
            e.printStackTrace(); // Skriver stacktrace til konsollen for debugging
        }


        for (Player p : Bukkit.getWorld(WORLD_NAME).getPlayers()) {
            loadPlayerStats(p);
            invs.put(p, getInventoryContents(p));
        }
        System.out.println("FFA plugin enabled.");
        Player p = Bukkit.getPlayer("UlrikWF");
        p.sendMessage("hei");
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        System.out.println("A player has teleported");
        Player player = event.getPlayer();
        if (event.getTo().getWorld().getName().equals(WORLD_NAME) &&
                !event.getFrom().getWorld().getName().equals(WORLD_NAME)) {

            this.invs.put(player, player.getInventory().getContents());

            player.sendMessage("Welcome to the FFA");
            resetPlayer(player);
            Result result = this.getPlayerData(player, false);
            int kill = result.getKills();
            int killstreak = result.getKillstreak();
            String kitsunlocked = result.getKit();
            //this.createNewsScoreboard(player, kill);

            player.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
            player.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            player.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
            player.getInventory().setHelmet(new ItemStack(Material.IRON_HELMET));
            this.breakperm.put(player, 0);
            this.kills.put(player, kill);
            this.kill_streak.put(player, killstreak);

            loadPlayerStats(player);
            player.sendTitle(ChatColor.YELLOW + "Welcome to the FFA", ChatColor.RED + "Change kit with /savekit");
        }
    }

    private Result getPlayerData(Player player, boolean update) {
        JsonNode root = JsonUtils.loadJson(FFA_FILE);
        if (root == null) return new Result(0, "default", 0, 0, 0, 1.0);

        JsonNode playerNode = root.path("Players").path(player.getName());
        if (!playerNode.isMissingNode()) {
            Result playerData = new Result(
                    playerNode.path("kills").asInt(0),
                    playerNode.path("kitsunlocked").asText("default"),
                    playerNode.path("killstreak").asInt(0),
                    playerNode.path("points").asInt(0),
                    playerNode.path("deaths").asInt(0),
                    playerNode.path("multiplier").asDouble(1.0)
            );

            if (update) {
                updatePlayerStats(player, root);
            }
            return playerData;
        } else {
            return initializePlayerStats(player, root);
        }
    }

    private Result initializePlayerStats(Player player, JsonNode root) {
        ObjectNode playersNode = (ObjectNode) root.path("Players");
        ObjectNode playerNode = playersNode.putObject(player.getName());
        playerNode.put("kills", 0);
        playerNode.put("kitsunlocked", "default");
        playerNode.put("killstreak", 0);
        playerNode.put("points", 0);
        playerNode.put("deaths", 0);
        playerNode.put("multiplier", 1.0);
        JsonUtils.saveJson(FFA_FILE, root);

        return new Result(0, "default", 0, 0, 0, 1.0);
    }

    private void loadPlayerStats(Player player) {
        Result data = getPlayerData(player, false);
        kills.put(player, data.getKills());
        kill_streak.put(player, data.getKillstreak());
        points.put(player, data.getPoints());
        kit.put(player, data.getKit());
        deaths.put(player, data.getDeaths());
        multiplier.put(player, data.getMultiplier());

        updateScoreboard(player);
    }

    private void updatePlayerStats(Player player, JsonNode root) {
        ObjectNode playersNode = (ObjectNode) root.path("Players");
        ObjectNode playerNode = playersNode.putObject(player.getName());
        playerNode.put("kills", kills.get(player));
        playerNode.put("kit", kit.get(player));
        playerNode.put("killstreak", kill_streak.get(player));
        playerNode.put("points", points.get(player));
        playerNode.put("deaths", deaths.get(player));
        playerNode.put("multiplier", multiplier.get(player));

        JsonUtils.saveJson(FFA_FILE, root);
    }

    private ItemStack[] getInventoryContents(Player player) {
        List<ItemStack> items = new ArrayList<>(); // Use List to store items dynamically
        JsonNode root = JsonUtils.loadJson(FFA_FILE);

        if (root == null) return new ItemStack[0]; // Return an empty array if root is null

        JsonNode playerNode = root.path("Players").path(player.getName()).path("kit");
        String[] kitItems = playerNode.asText().split(",");

        for (String itemData : kitItems) {
            String[] parts = itemData.split(":");
            if (parts.length == 2) { // Check if there are two parts: material and amount
                try {
                    Material material = Material.valueOf(parts[0].toUpperCase()); // Parse material
                    int amount = Integer.parseInt(parts[1]); // Parse amount
                    items.add(new ItemStack(material, amount)); // Add item to the list
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("Invalid material or amount in kit data for player " + player.getName() + ": " + itemData);
                }
            }
        }

        return items.toArray(new ItemStack[0]); // Convert list to array and return
    }


    private void updateScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("Player", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "⚔ FFA ⚔");
        objective.getScore(ChatColor.WHITE + "    ").setScore(11);
        objective.getScore(ChatColor.RED + "" + ChatColor.BOLD + "⚕ Kills ⚕                 ").setScore(10);
        objective.getScore(ChatColor.YELLOW + kills.get(player).toString()).setScore(9);
        objective.getScore(ChatColor.WHITE + "     ").setScore(8);
        objective.getScore(ChatColor.AQUA + " ※ Kill Streak ※").setScore(7);
        objective.getScore(ChatColor.RED + kill_streak.get(player).toString()).setScore(6);
        objective.getScore(ChatColor.WHITE + "   ").setScore(5);
        objective.getScore(ChatColor.GOLD + "Points                ").setScore(4);
        objective.getScore(ChatColor.LIGHT_PURPLE + points.get(player).toString()).setScore(3);
        objective.getScore(ChatColor.WHITE + "  ").setScore(2);
        objective.getScore(ChatColor.YELLOW + "92.222.153.3").setScore(1);
        player.setScoreboard(scoreboard);
        Objective obh = scoreboard.registerNewObjective("health", Criterias.HEALTH);
        obh.setDisplayName(ChatColor.RED + "❤");
        obh.setDisplaySlot(DisplaySlot.BELOW_NAME);
        player.setScoreboard(scoreboard);
    }


    private void resetPlayer(Player player) {
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        player.setBedSpawnLocation(Bukkit.getWorld(WORLD_NAME).getSpawnLocation(), true);
        player.setFoodLevel(20);
        player.setSaturation(1.0E9F);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        if (!victim.getWorld().getName().equals(WORLD_NAME)) return;

        resetPlayer(victim);
        Player killer = victim.getKiller();
        if (killer != null) {
            kills.put(killer, kills.get(killer) + 1);
            kill_streak.put(killer, kill_streak.get(killer) + 1);
            kill_streak.put(victim, 0);

            alterPointsOnDeath(victim, killer);
            updateScoreboard(killer);
            updateScoreboard(victim);

            if (kill_streak.get(killer) % 5 == 0) {
                Bukkit.broadcastMessage(ChatColor.RED + killer.getName() + " has a killstreak of " + kill_streak.get(killer));
            }
        }
    }

    public void alterPointsOnDeath(Player victim, Player killer) {
        int victimPoints = this.points.getOrDefault(victim, 0);
        int victimStreak = this.kill_streak.getOrDefault(victim, 0);
        double victimMultiplier = 0.05;

        int killerPoints = this.points.getOrDefault(killer, 0);
        int killerKills = this.kills.getOrDefault(killer, 0);
        int killerDeaths = this.deaths.getOrDefault(killer, 0);
        int killerStreak = this.kill_streak.getOrDefault(killer, 0);
        int basegain = 100;

        // Beregn killstreak bonus
        int killstreakBonus = calculateKillstreakBonus(killerStreak, basegain);

        // Beregn K/D ratio multiplikator
        double kdMultiplier = kdRatioMultiplier(killerDeaths == 0 ? killerKills : (double) killerKills / killerDeaths);

        // King of the Hill multiplier, basert på posisjon (for enkelhet, antar vi 1. plass her)
        double kingOfTheHill = kingOfTheHillMultiplier(1); // Implementer posisjonsberegning hvis nødvendig

        // Beregn streak breaker bonus for å stoppe victim sin streak
        int streakBreakerBonus = calculateStreakBreakerBonus(victimStreak, basegain);

        // Revenge bonus hvis killer nylig ble drept av victim
        //int revengeBonus = victim.getRecentKiller() == killer ? calculateRevengeBonus() : 0;

        // Total poengsum, juster multiplikatorer
        //int pointsGained = (int) ((basegain + killstreakBonus + streakBreakerBonus + revengeBonus) * kdMultiplier * kingOfTheHill);
        int pointsGained = (int) ((basegain + killstreakBonus + streakBreakerBonus) * kdMultiplier * kingOfTheHill);
        pointsGained = Math.max(basegain, pointsGained); // Minimum poengsum er basegain

        // Oppdater poeng for killer og victim
        this.points.put(killer, killerPoints + pointsGained);
        this.points.put(victim, (int) (victimPoints - (victimPoints * victimMultiplier)));
    }

    private int calculateKillstreakBonus(int killerStreak, int basegain) {
        // Killstreak bonus using exponential growth with base 1.2 to control the increase rate
        return (int) (basegain * Math.pow(1.2, killerStreak));
    }

    private double kdRatioMultiplier(double kdRatio) {
        // Adjusted K/D multipliers with smaller increments for more balance
        if (kdRatio >= 4.0) return 1.4;
        if (kdRatio >= 3.0) return 1.3;
        if (kdRatio >= 2.0) return 1.2;
        if (kdRatio >= 1.0) return 1.1;
        return 1.0;
    }

    private double kingOfTheHillMultiplier(int position) {
        // Apply a diminishing bonus multiplier based on leaderboard position
        return 1.0 + (0.1 / position);
    }

    private int calculateStreakBreakerBonus(int victimStreak, int basegain) {
        return (int) (basegain * Math.log(victimStreak + 1) * 1.5); // Logarithmic increase
    }

    private int calculateRevengeBonus() {
        return 300; // Constant value for revenge bonus
    }

    @EventHandler
    public void refillFood(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player && event.getEntity().getWorld().getName().equals(WORLD_NAME)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!player.getWorld().getName().equals(WORLD_NAME)) return;

        if (breakperm.getOrDefault(player, 0) == 0) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getType() == Material.WEB &&
                event.getPlayer().getWorld().getName().equals(WORLD_NAME)) {

            Bukkit.getScheduler().runTaskLater(this, () -> event.getBlockPlaced().setType(Material.AIR), 200L);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;
        if (!player.getWorld().getName().equals(WORLD_NAME)) return true;

        if (label.equalsIgnoreCase("break") && args.length > 0) {
            breakperm.put(player, args[0].equalsIgnoreCase("true") ? 1 : 0);
        }
        return true;
    }

}