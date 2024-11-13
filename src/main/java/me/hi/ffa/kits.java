package me.hi.ffa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class kits implements CommandExecutor, Listener {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String GUI_TITLE = "Select a Kit";
    private static final int GUI_SIZE = 9;

    private final FFA plugin;

    public kits(FFA plugin) {
        this.plugin = plugin;
        try {
            initJsonKit();
        } catch (Exception e) {
            plugin.getLogger().severe("Error initializing JSON kit: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can use this command.");
                return true;
            }

            Player player = (Player) sender;
            if (label.equalsIgnoreCase("kits")) {
                openKitSelectionGUI(player);
                return true;
            }

            if (label.equalsIgnoreCase("editkits") && args.length == 2) {
                String action = args[0].toLowerCase();
                String kitName = args[1];

                if (action.equals("add")) {
                    addKit(player, kitName);
                    player.sendMessage("Kit " + kitName + " has been added.");
                } else if (action.equals("remove")) {
                    removeKit(kitName);
                    player.sendMessage("Kit " + kitName + " has been removed.");
                } else {
                    player.sendMessage("Invalid action. Use 'add' or 'remove'.");
                }
                return true;
            }

            if (label.equalsIgnoreCase("unlockkit") && args.length == 2) {
                String playerName = args[0];
                String kitName = args[1];
                List<String> allKits = getAllKits();

                if (allKits.contains(kitName)) {
                    unlockKit(playerName, kitName);
                    player.sendMessage("Unlocked kit " + kitName + " for " + playerName);
                } else {
                    player.sendMessage(kitName + " does not exist");
                    String allKitsList = String.join(", ", allKits);

                    player.sendMessage("Available kits: " + allKitsList);
                }
                return true;

            }

            player.sendMessage("Usage: /kits or /editkits <add/remove> <kitname>");
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Error executing command: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void openKitSelectionGUI(Player player) {
        try {
            Inventory gui = Bukkit.createInventory(null, GUI_SIZE, GUI_TITLE);
            List<String> unlockedKits = getUnlockedKits(player);

            for (String kitName : unlockedKits) {
                ItemStack kitItem = new ItemStack(Material.CHEST);
                ItemMeta meta = kitItem.getItemMeta();
                if (meta != null) {
                    meta.setDisplayName(ChatColor.GREEN + kitName);
                    kitItem.setItemMeta(meta);
                }
                gui.addItem(kitItem);
            }

            player.openInventory(gui);
        } catch (Exception e) {
            plugin.getLogger().severe("Error opening kit selection GUI for player " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        try {
            if (!event.getView().getTitle().equals(GUI_TITLE)) return;

            event.setCancelled(true);
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

            Player player = (Player) event.getWhoClicked();
            String kitName = ChatColor.stripColor(Objects.requireNonNull(event.getCurrentItem().getItemMeta()).getDisplayName());

            if (kitIsUnlocked(player, kitName)) {
                equipKit(player, kitName);
                player.closeInventory();
                player.sendMessage("Equipped kit: " + kitName);
            } else {
                player.sendMessage("You haven't unlocked this kit.");
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Error handling inventory click: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addKit(Player player, String kitName) {
        try {
            JsonNode root = JsonUtils.loadJson(plugin.FFA_FILE);
            if (root == null) {
                plugin.getLogger().severe("Failed to load FFA_info.json.");
                return;
            }

            ObjectNode kitsNode = (ObjectNode) root.path("Kits");

            // Create a new node for the kit
            ObjectNode kitNode = objectMapper.createObjectNode();
            ObjectNode mainKitNode = objectMapper.createObjectNode();
            ObjectNode armorNode = objectMapper.createObjectNode();

            // Save main inventory items
            for (int i = 0; i < player.getInventory().getContents().length; i++) {
                ItemStack item = player.getInventory().getContents()[i];
                if (item != null) {
                    mainKitNode.put(String.valueOf(i), item.getType().name() + ":" + item.getAmount());
                }
            }

            // Save armor items
            ItemStack[] armorContents = player.getInventory().getArmorContents();
            for (int i = 0; i < armorContents.length; i++) {
                ItemStack armorItem = armorContents[i];
                if (armorItem != null) {
                    armorNode.put(String.valueOf(i), armorItem.getType().name() + ":" + armorItem.getAmount());
                }
            }

            // Add the main kit and armor nodes to the kit node
            kitNode.set("kit", mainKitNode);
            kitNode.set("armor", armorNode);

            // Add the kit to the kits node
            kitsNode.set(kitName, kitNode);

            // Save the JSON file
            JsonUtils.saveJson(plugin.FFA_FILE, root);
            plugin.getLogger().info("Kit " + kitName + " added for player " + player.getName());

        } catch (Exception e) {
            plugin.getLogger().severe("Error adding kit " + kitName + " for player " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void initJsonKit() {
        try {
            // Define armor and basic kit items
            ItemStack[] ironArmor = new ItemStack[]{
                    new ItemStack(Material.IRON_BOOTS),
                    new ItemStack(Material.IRON_LEGGINGS),
                    new ItemStack(Material.IRON_CHESTPLATE),
                    new ItemStack(Material.IRON_HELMET)
            };

            ItemStack flintAndSteel = new ItemStack(Material.FLINT_AND_STEEL);
            flintAndSteel.setDurability((short) 64);

            ItemStack[] basicKit = new ItemStack[]{
                    new ItemStack(Material.STONE_SWORD),
                    new ItemStack(Material.FISHING_ROD),
                    flintAndSteel,
                    new ItemStack(Material.WEB),
                    new ItemStack(Material.TNT)
            };

            // Load JSON root and handle cases where it's null
            JsonNode root = JsonUtils.loadJson(plugin.FFA_FILE);
            if (root == null || root.isMissingNode()) {
                root = objectMapper.createObjectNode(); // Create root node if missing
            }

            // Access or create the Kits node
            ObjectNode kitsNode;
            if (root.has("Kits")) {
                kitsNode = (ObjectNode) root.path("Kits");
            } else {
                kitsNode = ((ObjectNode) root).putObject("Kits");
            }

            // Create JSON nodes for kit items and armor
            ObjectNode kitNode = objectMapper.createObjectNode();
            ObjectNode armorNode = objectMapper.createObjectNode();

            for (int i = 0; i < basicKit.length; i++) {
                ItemStack item = basicKit[i];
                if (item != null) {
                    kitNode.put(String.valueOf(i), item.getType().name() + ":" + item.getAmount());
                }
            }

            for (int i = 0; i < ironArmor.length; i++) {
                ItemStack item = ironArmor[i];
                if (item != null) {
                    armorNode.put(String.valueOf(i), item.getType().name() + ":" + item.getAmount());
                }
            }

            // Add armor and kit to the default kit node
            ObjectNode defaultKitNode = objectMapper.createObjectNode();
            defaultKitNode.set("armor", armorNode);
            defaultKitNode.set("kit", kitNode);

            // Set the default kit in Kits
            kitsNode.set("default", defaultKitNode);

            // Save the updated JSON back to the file
            JsonUtils.saveJson(plugin.FFA_FILE, root);
        } catch (Exception e) {
            plugin.getLogger().severe("Error initializing JSON kit: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void removeKit(String kitName) {
        try {
            JsonNode root = JsonUtils.loadJson(plugin.FFA_FILE);
            if (root == null) {
                plugin.getLogger().severe("Failed to load FFA_info.json.");
                return;
            }
            ((ObjectNode) root.path("Kits")).remove(kitName);
            JsonUtils.saveJson(plugin.FFA_FILE, root);
        } catch (Exception e) {
            plugin.getLogger().severe("Error removing kit " + kitName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void equipKit(Player player, String kitName) {
        try {
            JsonNode root = JsonUtils.loadJson(plugin.FFA_FILE);
            if (root == null) {
                plugin.getLogger().severe("Failed to load FFA_info.json.");
                return;
            }

            JsonNode kitNode = root.path("Kits").path(kitName);
            if (kitNode.isMissingNode()) {
                player.sendMessage("Kit " + kitName + " does not exist.");
                return;
            }

            player.getInventory().clear();

            // Equip main inventory items
            JsonNode mainKitNode = kitNode.path("kit");
            mainKitNode.fields().forEachRemaining(entry -> {
                String slot = entry.getKey();
                String[] itemInfo = entry.getValue().asText().split(":");
                Material material = Material.valueOf(itemInfo[0]);
                int amount = Integer.parseInt(itemInfo[1]);
                player.getInventory().setItem(Integer.parseInt(slot), new ItemStack(material, amount));
            });

            // Equip armor items
            JsonNode armorNode = kitNode.path("armor");
            armorNode.fields().forEachRemaining(entry -> {
                String slot = entry.getKey();
                String[] itemInfo = entry.getValue().asText().split(":");
                Material material = Material.valueOf(itemInfo[0]);
                int amount = Integer.parseInt(itemInfo[1]);

                ItemStack armorItem = new ItemStack(material, amount);
                switch (Integer.parseInt(slot)) {
                    case 0:
                        player.getInventory().setBoots(armorItem);
                        break;
                    case 1:
                        player.getInventory().setLeggings(armorItem);
                        break;
                    case 2:
                        player.getInventory().setChestplate(armorItem);
                        break;
                    case 3:
                        player.getInventory().setHelmet(armorItem);
                        break;
                    default:
                        plugin.getLogger().warning("Invalid armor slot: " + slot + " in kit " + kitName);
                }
            });

        } catch (IllegalArgumentException e) {
            plugin.getLogger().severe("Invalid item in kit " + kitName + " for player " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
            player.sendMessage("Failed to equip kit: " + kitName);
        } catch (Exception e) {
            plugin.getLogger().severe("Error equipping kit " + kitName + " for player " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }


    public List<String> getUnlockedKits(Player player) {
        List<String> unlockedKits = new ArrayList<>();
        try {
            JsonNode root = JsonUtils.loadJson(plugin.FFA_FILE);
            if (root == null) {
                plugin.getLogger().severe("Failed to load FFA_info.json.");
                return unlockedKits;
            }

            JsonNode playerNode = root.path("Players").path(player.getName());
            if (playerNode.has("kitsunlocked")) {
                for (JsonNode kitNode : playerNode.get("kitsunlocked")) {
                    unlockedKits.add(kitNode.asText());
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Error getting unlocked kits for player " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
        return unlockedKits;
    }

    public boolean kitIsUnlocked(Player player, String kitName) {
        try {
            return getUnlockedKits(player).contains(kitName);
        } catch (Exception e) {
            plugin.getLogger().severe("Error checking if kit " + kitName + " is unlocked for player " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void unlockKit(String playerName, String kitName) {
        try {
            // Load the root JSON node
            JsonNode root = JsonUtils.loadJson(plugin.FFA_FILE);
            if (root == null) {
                plugin.getLogger().severe("Failed to load FFA_info.json.");
                return;
            }

            // Access or create the player node
            ObjectNode playersNode = (ObjectNode) root.path("Players");
            ObjectNode playerNode;
            if (playersNode.has(playerName)) {
                playerNode = (ObjectNode) playersNode.get(playerName);
            } else {
                playerNode = playersNode.putObject(playerName);
                plugin.getLogger().info("Created new player node for " + playerName);
            }

            // Ensure "kitsunlocked" is an array; create if it doesn’t exist
            ArrayNode unlockedKits;
            if (playerNode.has("kitsunlocked")) {
                unlockedKits = (ArrayNode) playerNode.get("kitsunlocked");
            } else {
                unlockedKits = playerNode.putArray("kitsunlocked");
                plugin.getLogger().info("Created new kitsunlocked array for player " + playerName);
            }

            // Check if the kit is already unlocked to prevent duplicates
            boolean alreadyUnlocked = false;
            for (JsonNode kitNode : unlockedKits) {
                if (kitNode.asText().equals(kitName)) {
                    alreadyUnlocked = true;
                    break;
                }
            }

            // Add the kit only if it’s not already unlocked
            if (!alreadyUnlocked) {
                unlockedKits.add(kitName);
                JsonUtils.saveJson(plugin.FFA_FILE, root);
                plugin.getLogger().info("Kit " + kitName + " unlocked for player " + playerName);
            } else {
                plugin.getLogger().info("Kit " + kitName + " is already unlocked for player " + playerName);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Error adding unlocked kit " + kitName + " for player " + playerName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }



    public List<String> getAllKits() {
        List<String> kits = new ArrayList<>();
        try {
            JsonNode root = JsonUtils.loadJson(plugin.FFA_FILE);
            if (root == null) {
                plugin.getLogger().severe("Failed to load FFA_info.json.");
                return kits;
            }
            root.path("Kits").fieldNames().forEachRemaining(kits::add);
        } catch (Exception e) {
            plugin.getLogger().severe("Error getting all kits: " + e.getMessage());
            e.printStackTrace();
        }
        return kits;
    }
}