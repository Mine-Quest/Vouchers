package com.badbones69.vouchers.api.events;

import com.badbones69.vouchers.api.objects.VoucherCode;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RedeemVoucherCodeEvent extends Event implements Cancellable {
    
    private final Player player;
    private final VoucherCode voucherCode;
    private Boolean cancelled;
    private final HandlerList handlers = new HandlerList();
    
    /**
     *
     * @param player The player using the voucherCode.
     * @param voucherCode The voucherCode being used.
     */
    public RedeemVoucherCodeEvent(Player player, VoucherCode voucherCode) {
        this.player = player;
        this.voucherCode = voucherCode;
        this.cancelled = false;
    }
    
    /**
     * @return The player redeeming the voucherCode.
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * @return Voucher object used in the event.
     */
    public VoucherCode getVoucherCode() {
        return voucherCode;
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
        
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
}