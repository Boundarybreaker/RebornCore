/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package reborncore.corelib.gui.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotValidated extends Slot {

    public SlotValidated(IInventory inv, int id, int x, int y) {
        super(inv, id, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack itemStack) {
        return inventory.isItemValidForSlot(this.getSlotIndex(), itemStack);
    }
}
