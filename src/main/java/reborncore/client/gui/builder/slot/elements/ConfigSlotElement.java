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

package reborncore.client.gui.builder.slot.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import reborncore.api.recipe.IRecipeCrafterProvider;
import reborncore.client.gui.builder.GuiBase;
import reborncore.client.gui.builder.slot.GuiSlotConfiguration;
import reborncore.common.recipes.RecipeCrafter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigSlotElement extends ElementBase {
	SlotType type;
	IItemHandler inventory;
	int id;
	public List<ElementBase> elements = new ArrayList<>();
	boolean filter = false;

	public ConfigSlotElement(IItemHandler slotInventory, int slotId, SlotType type, int x, int y, GuiBase gui) {
		super(x, y, type.getButtonSprite());
		this.type = type;
		this.inventory = slotInventory;
		this.id = slotId;

		SlotConfigPopupElement popupElement;

		elements.add(popupElement = new SlotConfigPopupElement(this.id, x - 22, y - 22, this));
		elements.add(new ButtonElement(x + 37, y - 25, Sprite.EXIT_BUTTON).addReleaseAction((element, gui1, provider, mouseX, mouseY) -> {
			GuiSlotConfiguration.selectedSlot = -1;
			GuiBase.slotConfigType = GuiBase.SlotConfigType.NONE;
			return true;
		}));

		elements.add(new CheckBoxElement("Auto Input", 0xFFFFFFFF, x - 26, y + 42, "input", slotId, Sprite.LIGHT_CHECK_BOX, gui.getMachine(),
			checkBoxElement -> checkBoxElement.machineBase.slotConfiguration.getSlotDetails(checkBoxElement.slotID).autoInput()).addPressAction((element, gui12, provider, mouseX, mouseY) -> {
			popupElement.updateCheckBox((CheckBoxElement) element, "input", gui12);
			return true;
		}));
		elements.add(new CheckBoxElement("Auto Output", 0xFFFFFFFF, x - 26, y + 57, "output", slotId, Sprite.LIGHT_CHECK_BOX, gui.getMachine(),
			checkBoxElement -> checkBoxElement.machineBase.slotConfiguration.getSlotDetails(checkBoxElement.slotID).autoOutput()).addPressAction((element, gui13, provider, mouseX, mouseY) -> {
			popupElement.updateCheckBox((CheckBoxElement) element, "output", gui13);
			return true;
		}));

		if (gui.getMachine() instanceof IRecipeCrafterProvider) {
			RecipeCrafter recipeCrafter = ((IRecipeCrafterProvider) gui.getMachine()).getRecipeCrafter();
			if (Arrays.stream(recipeCrafter.inputSlots).anyMatch(value -> value == slotId)) {
				elements.add(new CheckBoxElement("Filter Input", 0xFFFFFFFF, x - 26, y + 72, "filter", slotId, Sprite.LIGHT_CHECK_BOX, gui.getMachine(),
					checkBoxElement -> checkBoxElement.machineBase.slotConfiguration.getSlotDetails(checkBoxElement.slotID).filter()).addPressAction((element, gui13, provider, mouseX, mouseY) -> {
					popupElement.updateCheckBox((CheckBoxElement) element, "filter", gui13);
					return true;
				}));
				filter = true;
				popupElement.filter = true;
			}
		}
		setWidth(85);
		setHeight(105 + (filter ? 15 : 0));
	}

	@Override
	public void draw(GuiBase gui) {
		super.draw(gui);
		ItemStack stack = inventory.getStackInSlot(id);
		int xPos = x + 1 + gui.getGuiLeft();
		int yPos = y + 1 + gui.getGuiTop();

		GlStateManager.enableDepthTest();
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderHelper.enableGUIStandardItemLighting();
		ItemRenderer renderItem = Minecraft.getInstance().getItemRenderer();
		renderItem.renderItemAndEffectIntoGUI(gui.mc.player, stack, xPos, yPos);
		renderItem.renderItemOverlayIntoGUI(gui.mc.fontRenderer, stack, xPos, yPos, null);
		GlStateManager.disableDepthTest();
		GlStateManager.disableLighting();
		GlStateManager.popMatrix();
		if (isHovering) {
			drawSprite(gui, type.getButtonHoverOverlay(), x, y);
		}
		elements.forEach(elementBase -> elementBase.draw(gui));
	}

	public SlotType getType() {
		return type;
	}

	public int getId() {
		return id;
	}
}