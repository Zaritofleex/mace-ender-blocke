package fr.zarito.maceblocker;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class MaceEnderBlocker extends JavaPlugin implements Listener {

    private static final Component MESSAGE = LegacyComponentSerializer.legacyAmpersand()
            .deserialize("&6[LimitedMaces]&r &cTu ne peux pas mettre la mace dans l'ender chest.");

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    private boolean isMace(ItemStack item) {
        return item != null && item.getType() == Material.MACE;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Inventory top = e.getView().getTopInventory();
        if (top.getType() != InventoryType.ENDER_CHEST) return;

        HumanEntity player = e.getWhoClicked();
        if (player.hasPermission("maceblocker.bypass")) return;

        boolean clickedTop = e.getClickedInventory() == top;
        boolean block = false;

        if (clickedTop) {
            if (isMace(e.getCursor())) block = true;
            if (e.getClick() == ClickType.NUMBER_KEY
                    && isMace(player.getInventory().getItem(e.getHotbarButton()))) block = true;
            if (e.getClick() == ClickType.SWAP_OFFHAND
                    && isMace(player.getInventory().getItemInOffHand())) block = true;
        } else if (e.getClickedInventory() != null && e.isShiftClick() && isMace(e.getCurrentItem())) {
            block = true;
        }

        if (block) {
            e.setCancelled(true);
            player.sendMessage(MESSAGE);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        Inventory top = e.getView().getTopInventory();
        if (top.getType() != InventoryType.ENDER_CHEST) return;
        if (e.getWhoClicked().hasPermission("maceblocker.bypass")) return;
        if (!isMace(e.getOldCursor())) return;

        int topSize = top.getSize();
        for (int slot : e.getRawSlots()) {
            if (slot < topSize) {
                e.setCancelled(true);
                e.getWhoClicked().sendMessage(MESSAGE);
                return;
            }
        }
    }
}
