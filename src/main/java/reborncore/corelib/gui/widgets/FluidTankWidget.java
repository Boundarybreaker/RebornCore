/** Copyright (c) 2011-2015, SpaceToad and the BuildCraft Team http://www.mod-buildcraft.com
 * <p/>
 * BuildCraft is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL. Please check the contents
 * of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt */
package reborncore.corelib.gui.widgets;

import java.io.DataInputStream;
import java.io.IOException;

import reborncore.corelib.gui.tooltips.ToolTipLine;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import reborncore.corelib.gui.GuiBuildCraft;
import reborncore.corelib.gui.tooltips.ToolTip;
import reborncore.common.util.Tank;

/** Provides a "view" of a given {@link Tank} for use in GUI's. The tank will be given a tooltip containing the name of
 * the fluid and the amount of fluid current in the tank. The tank can be clicked with a valid
 * {@link IFluidContainerItem} or any container registered with {@link FluidContainerRegistry} to fill or drain the
 * tank. */
public class FluidTankWidget extends Widget {
    public static final byte NET_CLICK = 0;

    public final Tank tank;
    private boolean overlay;
    private int overlayX, overlayY;

    public FluidTankWidget(Tank tank, int x, int y, int w, int h) {
        super(x, y, 0, 0, w, h);
        this.tank = tank;
    }

    /** Adds an overlay for the tank from the specified location in the texture. */
    public FluidTankWidget withOverlay(int x, int y) {
        overlay = true;
        overlayX = x;
        overlayY = y;
        return this;
    }

    /** Returns a copied version of this widget that instead looks at the give tank and displays in a different place.
     * This will copy over the overlay (if one has been specified) from {@link #withOverlay(int, int)} */
    public FluidTankWidget copyMoved(Tank tank, int x, int y) {
        FluidTankWidget copy = new FluidTankWidget(tank, x, y, w, h);
        if (overlay) copy.withOverlay(overlayX, overlayY);
        return copy;
    }

    @Override
    public ToolTip getToolTip() {
        return new ToolTip() {
            @Override
            public void refresh() {
                this.delegate().clear();
                if(!tank.isEmpty() && tank.getFluid() != null) {
                    FluidStack fluid = tank.getFluid();
                    this.delegate().add(new ToolTipLine(fluid.getLocalizedName(), fluid.getFluid().getColor(fluid)));
                } else this.delegate().add(new ToolTipLine("Empty", 0x707070));
                this.delegate().add(new ToolTipLine(tank.getFluidAmount() + " / " + tank.getCapacity(), 0x606060));
            }
        };
    }

    @Override
    public boolean handleMouseClick(int mouseX, int mouseY, int mouseButton) {
        container.sendWidgetDataToServer(this, new byte[] { NET_CLICK });
        return true;
    }

    @Override
    public void handleServerPacketData(DataInputStream data) throws IOException {
        byte b = data.readByte();
        if (b == NET_CLICK) handleTankClick();
    }

    /** Attempts to interact the currently held item (in the gui) with this tank. This will either attempt to fill or
     * drain the tank depending on what type of item is currently held.
     * TODO: Ensure it's working */
    private void handleTankClick() {
        InventoryPlayer inv = container.getPlayer().inventory;
        ItemStack heldStack = inv.getItemStack();
        if (heldStack == null || heldStack.getItem() == null) return;
        Item heldItem = heldStack.getItem();
        if (FluidContainerRegistry.isEmptyContainer(heldStack)) {
            int capacity = FluidContainerRegistry.getContainerCapacity(tank.drain(1, false), heldStack);
            FluidStack potential = tank.drain(capacity, false);
            if (potential == null) return;
            ItemStack filled = FluidContainerRegistry.fillFluidContainer(potential, heldStack);
            if (filled == null) return;
            if (FluidContainerRegistry.getContainerCapacity(filled) != capacity) return;

            tank.drain(capacity, true);
            inv.setItemStack(filled);
            if (inv.player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) inv.player).updateHeldItem();
            }
        } else if (FluidContainerRegistry.isFilledContainer(heldStack)) {
            FluidStack contained = FluidContainerRegistry.getFluidForFilledItem(heldStack);
            if (tank.fill(contained, false) != contained.amount) return;
            ItemStack drained = FluidContainerRegistry.drainFluidContainer(heldStack);
            if (drained == null) return;

            tank.fill(contained, true);
            inv.setItemStack(drained);
            if (inv.player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) inv.player).updateHeldItem();
            }
        } else if (heldItem instanceof IFluidContainerItem) {
            IFluidContainerItem capability = (IFluidContainerItem) heldItem;
            if(!tank.isEmpty() && tank.getFluid() != null) {
                FluidStack fill = capability.drain(heldStack, tank.getCapacity(), true);
                if(fill != null && fill.amount != 0) {
                    tank.fill(fill, true);
                    inv.setItemStack(heldStack);
                } else {
                    int drain = capability.fill(heldStack, tank.getFluid(), true);
                    if (drain != 0) {
                        tank.drain(drain, true);
                        inv.setItemStack(heldStack);
                    }
                }
            } else {
                FluidStack fill = capability.drain(heldStack, tank.getCapacity(), true);
                if(fill != null && fill.amount != 0) {
                    tank.fill(fill, true);
                    inv.setItemStack(heldStack);
                }
            }

            if (inv.player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) inv.player).updateHeldItem();
            }
        } else if(heldStack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN)) {
            IFluidHandler capability = heldStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.DOWN);
            if(!tank.isEmpty() && tank.getFluid() != null) {
                FluidStack fill = capability.drain(tank.getCapacity(), true);
                if(fill != null && fill.amount != 0) {
                    tank.fill(fill, true);
                    inv.setItemStack(heldStack);
                } else {
                    int drain = capability.fill(tank.getFluid(), true);
                    if (drain != 0) {
                        tank.drain(drain, true);
                        inv.setItemStack(heldStack);
                    }
                }
            } else {
                FluidStack fill = capability.drain(tank.getCapacity(), true);
                if(fill != null && fill.amount != 0) {
                    tank.fill(fill, true);
                    inv.setItemStack(heldStack);
                }
            }

            if (inv.player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) inv.player).updateHeldItem();
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(GuiBuildCraft gui, int guiX, int guiY, int mouseX, int mouseY) {
        if (tank == null) {
            return;
        }
        FluidStack fluidStack = tank.getFluid();
        if (fluidStack != null && fluidStack.amount > 0) {
            gui.drawFluid(fluidStack, guiX + x, guiY + y, w, h, tank.getCapacity());
        }

        GuiBuildCraft.bindTexture(gui.texture);

        if (overlay) {
            gui.drawTexturedModalRect(guiX + x, guiY + y, overlayX, overlayY, w, h);
        }
    }
}
