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

package reborncore.client.containerBuilder.builder;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import reborncore.client.containerBuilder.IRightClickHandler;
import reborncore.common.tile.TileLegacyMachineBase;
import reborncore.common.util.ItemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

public class BuiltContainer extends Container implements IExtendedContainerListener {

	private final String name;

	private final Predicate<EntityPlayer> canInteract;
	private final List<Range<Integer>> playerSlotRanges;
	private final List<Range<Integer>> tileSlotRanges;

	private final ArrayList<MutableTriple<IntSupplier, IntConsumer, Short>> shortValues;
	private final ArrayList<MutableTriple<IntSupplier, IntConsumer, Integer>> integerValues;
	private final ArrayList<MutableTriple<LongSupplier, LongConsumer, Long>> longValues;
	private final ArrayList<MutableTriple<Supplier, Consumer, Object>> objectValues;
	private List<Consumer<InventoryCrafting>> craftEvents;
	private Integer[] integerParts;

	private final TileLegacyMachineBase tile;

	public BuiltContainer(final String name, final Predicate<EntityPlayer> canInteract,
						  final List<Range<Integer>> playerSlotRange,
						  final List<Range<Integer>> tileSlotRange, TileLegacyMachineBase tile) {
		this.name = name;

		this.canInteract = canInteract;

		this.playerSlotRanges = playerSlotRange;
		this.tileSlotRanges = tileSlotRange;

		this.shortValues = new ArrayList<>();
		this.integerValues = new ArrayList<>();
		this.longValues = new ArrayList<>();
		this.objectValues = new ArrayList<>();

		this.tile = tile;
	}

	public void addShortSync(final List<Pair<IntSupplier, IntConsumer>> syncables) {

		for (final Pair<IntSupplier, IntConsumer> syncable : syncables)
			this.shortValues.add(MutableTriple.of(syncable.getLeft(), syncable.getRight(), (short) 0));
		this.shortValues.trimToSize();
	}

	public void addLongSync(final List<Pair<LongSupplier, LongConsumer>> syncables) {

		for (final Pair<LongSupplier, LongConsumer> syncable : syncables)
			this.longValues.add(MutableTriple.of(syncable.getLeft(), syncable.getRight(), (long) 0));
		this.longValues.trimToSize();
	}

	public void addIntegerSync(final List<Pair<IntSupplier, IntConsumer>> syncables) {

		for (final Pair<IntSupplier, IntConsumer> syncable : syncables)
			this.integerValues.add(MutableTriple.of(syncable.getLeft(), syncable.getRight(), 0));
		this.integerValues.trimToSize();
		this.integerParts = new Integer[this.integerValues.size()];
	}

	public void addObjectSync(final List<Pair<Supplier, Consumer>> syncables) {

		for (final Pair<Supplier, Consumer> syncable : syncables)
			this.objectValues.add(MutableTriple.of(syncable.getLeft(), syncable.getRight(), null));
		this.objectValues.trimToSize();
	}

	public void addCraftEvents(final List<Consumer<InventoryCrafting>> craftEvents) {
		this.craftEvents = craftEvents;
	}

	public void addSlot(final Slot slot) {
		this.addSlotToContainer(slot);
	}

	@Override
	public boolean canInteractWith(final EntityPlayer playerIn) {
		if(this.tile != null) {
			return tile.isUsableByPlayer(playerIn);
		} else {
			return this.canInteract.test(playerIn); // <
		}
	}

	@Override
	public final void onCraftMatrixChanged(final IInventory inv) {
		if (!this.craftEvents.isEmpty())
			this.craftEvents.forEach(consumer -> consumer.accept((InventoryCrafting) inv));
	}

	@Override
	public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		if (dragType == 1 && slotId > 0 && slotId < 1000) {
			Slot slot = this.inventorySlots.get(slotId);
			if (slot instanceof IRightClickHandler) {
				if (((IRightClickHandler) slot).handleRightClick(slot.getSlotIndex(), player, this)) {
					return ItemStack.EMPTY;
				}
			}
		}
		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (final IContainerListener listener : this.listeners) {

			int i = 0;
			if (!this.shortValues.isEmpty())
				for (final MutableTriple<IntSupplier, IntConsumer, Short> value : this.shortValues) {
					final short supplied = (short) value.getLeft().getAsInt();
					if (supplied != value.getRight()) {

						listener.sendWindowProperty(this, i, supplied);
						value.setRight(supplied);
					}
					i++;
				}

			if (!this.integerValues.isEmpty())
				for (final MutableTriple<IntSupplier, IntConsumer, Integer> value : this.integerValues) {
					final int supplied = value.getLeft().getAsInt();
					if (supplied != value.getRight()) {

						listener.sendWindowProperty(this, i, supplied >> 16);
						listener.sendWindowProperty(this, i + 1, (short) (supplied & 0xFFFF));
						value.setRight(supplied);
					}
					i += 2;
				}

			if (!this.longValues.isEmpty()){
				int longs = 0;
				for (final MutableTriple<LongSupplier, LongConsumer, Long> value : this.longValues) {
					final long supplied = value.getLeft().getAsLong();
					if(supplied != value.getRight()){
						sendLong(listener,this, longs, supplied);
						value.setRight(supplied);
					}
					longs++;
				}
			}

			if (!this.objectValues.isEmpty()){
				int objects = 0;
				for (final MutableTriple<Supplier, Consumer, Object> value : this.objectValues) {
					final Object supplied = value.getLeft();
					if(supplied != value.getRight()){
						sendObject(listener,this, objects, supplied);
						value.setRight(supplied);
					}
					objects++;
				}
			}
		}
	}

	@Override
	public void addListener(final IContainerListener listener) {
		super.addListener(listener);

		int i = 0;
		if (!this.shortValues.isEmpty())
			for (final MutableTriple<IntSupplier, IntConsumer, Short> value : this.shortValues) {
				final short supplied = (short) value.getLeft().getAsInt();

				listener.sendWindowProperty(this, i, supplied);
				value.setRight(supplied);
				i++;
			}

		if (!this.integerValues.isEmpty())
			for (final MutableTriple<IntSupplier, IntConsumer, Integer> value : this.integerValues) {
				final int supplied = value.getLeft().getAsInt();

				listener.sendWindowProperty(this, i, supplied >> 16);
				listener.sendWindowProperty(this, i + 1, (short) (supplied & 0xFFFF));
				value.setRight(supplied);
				i += 2;
			}

		if (!this.longValues.isEmpty()){
			int longs = 0;
			for (final MutableTriple<LongSupplier, LongConsumer, Long> value : this.longValues) {
				final long supplied = value.getLeft().getAsLong();
				sendLong(listener,this, longs, supplied);
				value.setRight(supplied);
				longs++;
			}
		}

		if (!this.objectValues.isEmpty()){
			int objects = 0;
			for (final MutableTriple<Supplier, Consumer, Object> value : this.objectValues) {
				final Object supplied = value.getLeft();
				sendObject(listener,this, objects, supplied);
				value.setRight(supplied);
				objects++;
			}
		}
	}

	@Override
	public void handleLong(int var, long value) {
		this.longValues.get(var).getMiddle().accept(value);
	}

	@Override
	public void handleObject(int var, Object value) {
		this.objectValues.get(var).getMiddle().accept(value);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(final int id, final int value) {

		if (id < this.shortValues.size()) {
			this.shortValues.get(id).getMiddle().accept((short) value);
			this.shortValues.get(id).setRight((short) value);
		} else if (id - this.shortValues.size() < this.integerValues.size() * 2) {

			if ((id - this.shortValues.size()) % 2 == 0)
				this.integerParts[(id - this.shortValues.size()) / 2] = value;
			else {
				this.integerValues.get((id - this.shortValues.size()) / 2).getMiddle().accept(
					(this.integerParts[(id - this.shortValues.size()) / 2] & 0xFFFF) << 16 | value & 0xFFFF);
			}
		}
	}

	@Override
	public ItemStack transferStackInSlot(final EntityPlayer player, final int index) {

		ItemStack originalStack = ItemStack.EMPTY;

		final Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {

			final ItemStack stackInSlot = slot.getStack();
			originalStack = stackInSlot.copy();

			boolean shifted = false;

			for (final Range<Integer> range : this.playerSlotRanges)
				if (range.contains(index)) {

					if (this.shiftToTile(stackInSlot))
						shifted = true;
					break;
				}

			if (!shifted)
				for (final Range<Integer> range : this.tileSlotRanges)
					if (range.contains(index)) {
						if (this.shiftToPlayer(stackInSlot))
							shifted = true;
						break;
					}

			slot.onSlotChange(stackInSlot, originalStack);
			if (stackInSlot.getCount() <= 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();
			if (stackInSlot.getCount() == originalStack.getCount())
				return ItemStack.EMPTY;
			slot.onTake(player, stackInSlot);
		}
		return originalStack;


	}

	protected boolean shiftItemStack(final ItemStack stackToShift, final int start, final int end) {
		boolean changed = false;
		if (stackToShift.isStackable()) {
			for (int slotIndex = start; stackToShift.getCount() > 0 && slotIndex < end; slotIndex++) {
				final Slot slot = this.inventorySlots.get(slotIndex);
				final ItemStack stackInSlot = slot.getStack();
				if (!stackInSlot.isEmpty() && ItemUtils.isItemEqual(stackInSlot, stackToShift, true, true)
					&& slot.isItemValid(stackToShift)) {
					final int resultingStackSize = stackInSlot.getCount() + stackToShift.getCount();
					final int max = Math.min(stackToShift.getMaxStackSize(), slot.getSlotStackLimit());
					if (resultingStackSize <= max) {
						stackToShift.setCount(0);
						stackInSlot.setCount(resultingStackSize);
						slot.onSlotChanged();
						changed = true;
					} else if (stackInSlot.getCount() < max) {
						stackToShift.shrink(max - stackInSlot.getCount());
						stackInSlot.setCount(max);
						slot.onSlotChanged();
						changed = true;
					}
				}
			}
		}
		if (stackToShift.getCount() > 0) {
			for (int slotIndex = start; stackToShift.getCount() > 0 && slotIndex < end; slotIndex++) {
				final Slot slot = this.inventorySlots.get(slotIndex);
				ItemStack stackInSlot = slot.getStack();
				if (stackInSlot.isEmpty() && slot.isItemValid(stackToShift)) {
					final int max = Math.min(stackToShift.getMaxStackSize(), slot.getSlotStackLimit());
					stackInSlot = stackToShift.copy();
					stackInSlot.setCount(Math.min(stackToShift.getCount(), max));
					stackToShift.shrink(stackInSlot.getCount());
					slot.putStack(stackInSlot);
					slot.onSlotChanged();
					changed = true;
				}
			}
		}
		return changed;
	}

	private boolean shiftToTile(final ItemStack stackToShift) {
		if(!tile.getInventoryForTile().isPresent()){
			return false;
		}
		for (final Range<Integer> range : this.tileSlotRanges)
			if (this.shiftItemStack(stackToShift, range.getMinimum(), range.getMaximum() + 1))
				return true;
		return false;
	}

	private boolean shiftToPlayer(final ItemStack stackToShift) {
		for (final Range<Integer> range : this.playerSlotRanges)
			if (this.shiftItemStack(stackToShift, range.getMinimum(), range.getMaximum() + 1))
				return true;
		return false;
	}

	public String getName() {
		return this.name;
	}
}
