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

    public kits() {
        // Constructor to initialize default kits or configurations if needed

    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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

        player.sendMessage("Usage: /kits or /editkits <add/remove> <kitname>");
        return true;
    }

    public void openKitSelectionGUI(Player player) {
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
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
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
    }

    private void addKit(Player player, String kitName) {
        JsonNode root = JsonUtils.loadJson(FFA_FILE);
        assert root != null;
        ObjectNode kitsNode = (ObjectNode) root.path("Kits");
        ObjectNode kitNode = objectMapper.createObjectNode();

        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            ItemStack item = player.getInventory().getContents()[i];
            if (item != null) {
                kitNode.put(String.valueOf(i), item.getType().name() + ":" + item.getAmount());
            }
        }

        kitsNode.set(kitName, kitNode);
        JsonUtils.saveJson(FFA_FILE, root);
    }

    private void removeKit(String kitName) {
        JsonNode root = JsonUtils.loadJson(FFA_FILE);
        assert root != null;
        ((ObjectNode) root.path("Kits")).remove(kitName);
        JsonUtils.saveJson(FFA_FILE, root);
    }

    private void equipKit(Player player, String kitName) {
        try {
            JsonNode root = JsonUtils.loadJson(FFA_FILE);
            assert root != null;
            JsonNode kitNode = root.path("Kits").path(kitName);
            player.getInventory().clear();

            kitNode.fields().forEachRemaining(entry -> {
                String slot = entry.getKey();
                String[] itemInfo = entry.getValue().asText().split(":");
                Material material = Material.valueOf(itemInfo[0]);
                int amount = Integer.parseInt(itemInfo[1]);
                player.getInventory().setItem(Integer.parseInt(slot), new ItemStack(material, amount));
            });

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            player.sendMessage("Failed to equip kit: " + kitName);
        }
    }

    public List<String> getUnlockedKits(Player player) {
        List<String> unlockedKits = new ArrayList<>();
        JsonNode root = JsonUtils.loadJson(FFA_FILE);
        if (root == null) return unlockedKits;

        JsonNode playerNode = root.path("Players").path(player.getName());
        if (playerNode.has("unlockedKits")) {
            for (JsonNode kitNode : playerNode.get("unlockedKits")) {
                unlockedKits.add(kitNode.asText());
            }
        }
        return unlockedKits;
    }

    public boolean kitIsUnlocked(Player player, String kitName) {
        return getUnlockedKits(player).contains(kitName);
    }

    public void addUnlockedKit(Player player, String kitName) {
        JsonNode root = JsonUtils.loadJson(FFA_FILE);
        assert root != null;
        ObjectNode playerNode = (ObjectNode) root.path("Players").path(player.getName());

        if (!playerNode.has("unlockedKits")) {
            playerNode.putArray("unlockedKits");
        }

        ArrayNode unlockedKits = (ArrayNode) playerNode.get("unlockedKits");
        unlockedKits.add(kitName);

        JsonUtils.saveJson(FFA_FILE, root);
    }

    public List<String> getAllKits() {
        List<String> kits = new ArrayList<>();
        JsonNode root = JsonUtils.loadJson(FFA_FILE);
        assert root != null;
        root.path("Kits").fieldNames().forEachRemaining(kits::add);
        return kits;
    }
}
