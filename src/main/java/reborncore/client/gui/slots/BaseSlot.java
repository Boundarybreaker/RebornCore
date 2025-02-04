/*
 * Copyright (c) 2018 modmuss50 and Gigabit101
 *
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package reborncore.client.gui.slots;

import net.minecraft.container.Slot;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import reborncore.mixin.extensions.SlotExtensions;

import java.util.function.Predicate;

/**
 * Created by modmuss50 on 11/04/2016.
 */
public class BaseSlot extends Slot {

	private Predicate<ItemStack> filter = (stack) -> true;

	public BaseSlot(Inventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}

	public BaseSlot(Inventory inventoryIn, int index, int xPosition, int yPosition, Predicate<ItemStack> filter) {
		super(inventoryIn, index, xPosition, yPosition);
		this.filter = filter;
	}

	public boolean canWorldBlockRemove() {
		return true;
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return filter.test(stack);
	}

	public boolean canWorldBlockInsert(){
		return true;
	}

	public int getSlotID(){
		return ((SlotExtensions)this).getInvSlot();
	}
}
