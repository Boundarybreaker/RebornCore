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

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.network.Packet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.IWorld;
import reborncore.RebornCore;
import reborncore.client.gui.GuiUtil;
import reborncore.client.gui.builder.GuiBase;
import reborncore.common.network.NetworkManager;
import reborncore.common.network.ServerBoundPackets;
import reborncore.common.blockentity.SlotConfiguration;
import reborncore.common.blockentity.MachineBaseBlockEntity;
import reborncore.common.util.Color;
import reborncore.common.util.MachineFacing;

public class SlotConfigPopupElement extends ElementBase {
	int id;
	public boolean filter = false;

	ConfigSlotElement slotElement;

	boolean allowInput = true;

	public SlotConfigPopupElement(int slotId, int x, int y, ConfigSlotElement slotElement, boolean allowInput) {
		super(x, y, Sprite.SLOT_CONFIG_POPUP);
		this.id = slotId;
		this.slotElement = slotElement;
		this.allowInput = allowInput;
	}

	@Override
	public void draw(GuiBase<?> gui) {
		drawDefaultBackground(gui, adjustX(gui, getX() - 8), adjustY(gui, getY() - 7), 84, 105 + (filter ? 15 : 0));
		super.draw(gui);

		MachineBaseBlockEntity machine = ((MachineBaseBlockEntity) gui.be);
		IWorld world = machine.getWorld();
		BlockPos pos = machine.getPos();
		BlockState state = world.getBlockState(pos);
		BlockState actualState = state.getBlock().getDefaultState();
		BlockRenderManager dispatcher = MinecraftClient.getInstance().getBlockRenderManager();
		BakedModel model = dispatcher.getModels().getModel(state.getBlock().getDefaultState());
		MinecraftClient.getInstance().getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		drawState(gui, world, model, actualState, pos, dispatcher, 4, 23); //left
		drawState(gui, world, model, actualState, pos, dispatcher, 23, -12, -90F, 1F, 0F, 0F); //top
		drawState(gui, world, model, actualState, pos, dispatcher, 23, 23, -90F, 0F, 1F, 0F); //centre
		drawState(gui, world, model, actualState, pos, dispatcher, 23, 42, 90F, 1F, 0F, 0F); //bottom
		drawState(gui, world, model, actualState, pos, dispatcher, 26, 23, 180F, 0F, 1F, 0F); //right
		drawState(gui, world, model, actualState, pos, dispatcher, 26, 42, 90F, 0F, 1F, 0F); //back

		drawSlotSateColor(gui.getMachine(), MachineFacing.UP.getFacing(machine), id, 22, -1, gui);
		drawSlotSateColor(gui.getMachine(), MachineFacing.FRONT.getFacing(machine), id, 22, 18, gui);
		drawSlotSateColor(gui.getMachine(), MachineFacing.DOWN.getFacing(machine), id, 22, 37, gui);
		drawSlotSateColor(gui.getMachine(), MachineFacing.RIGHT.getFacing(machine), id, 41, 18, gui);
		drawSlotSateColor(gui.getMachine(), MachineFacing.BACK.getFacing(machine), id, 41, 37, gui);
		drawSlotSateColor(gui.getMachine(), MachineFacing.LEFT.getFacing(machine), id, 3, 18, gui);
	}

	@Override
	public boolean onRelease(MachineBaseBlockEntity provider, GuiBase<?> gui, double mouseX, double mouseY) {
		if (isInBox(23, 4, 16, 16, mouseX, mouseY, gui)) {
			cyleSlotConfig(MachineFacing.UP.getFacing(provider), gui);
		} else if (isInBox(23, 23, 16, 16, mouseX, mouseY, gui)) {
			cyleSlotConfig(MachineFacing.FRONT.getFacing(provider), gui);
		} else if (isInBox(42, 23, 16, 16, mouseX, mouseY, gui)) {
			cyleSlotConfig(MachineFacing.RIGHT.getFacing(provider), gui);
		} else if (isInBox(4, 23, 16, 16, mouseX, mouseY, gui)) {
			cyleSlotConfig(MachineFacing.LEFT.getFacing(provider), gui);
		} else if (isInBox(23, 42, 16, 16, mouseX, mouseY, gui)) {
			cyleSlotConfig(MachineFacing.DOWN.getFacing(provider), gui);
		} else if (isInBox(42, 42, 16, 16, mouseX, mouseY, gui)) {
			cyleSlotConfig(MachineFacing.BACK.getFacing(provider), gui);
		} else {
			return false;
		}
		return true;
	}

	public void cyleSlotConfig(Direction side, GuiBase<?> guiBase) {
		SlotConfiguration.SlotConfig currentSlot = guiBase.getMachine().getSlotConfiguration().getSlotDetails(id).getSideDetail(side);

		//Bit of a mess, in the future have a way to remove config options from this list
		SlotConfiguration.ExtractConfig nextConfig = currentSlot.getSlotIO().getIoConfig().getNext();
		if(!allowInput && nextConfig == SlotConfiguration.ExtractConfig.INPUT){
			nextConfig = SlotConfiguration.ExtractConfig.OUTPUT;
		}

		SlotConfiguration.SlotIO slotIO = new SlotConfiguration.SlotIO(nextConfig);
		SlotConfiguration.SlotConfig newConfig = new SlotConfiguration.SlotConfig(side, slotIO, id);
		Packet<?> packetSlotSave = ServerBoundPackets.createPacketSlotSave(guiBase.be.getPos(), newConfig);
		NetworkManager.sendToServer(packetSlotSave);
	}

	public void updateCheckBox(CheckBoxElement checkBoxElement, String type, GuiBase<?> guiBase) {
		SlotConfiguration.SlotConfigHolder configHolder = guiBase.getMachine().getSlotConfiguration().getSlotDetails(id);
		boolean input = configHolder.autoInput();
		boolean output = configHolder.autoOutput();
		boolean filter = configHolder.filter();
		if (type.equalsIgnoreCase("input")) {
			input = !configHolder.autoInput();
		}
		if (type.equalsIgnoreCase("output")) {
			output = !configHolder.autoOutput();
		}
		if (type.equalsIgnoreCase("filter")) {
			filter = !configHolder.filter();
		}

		Packet<?> packetSlotSave = ServerBoundPackets.createPacketIOSave(guiBase.be.getPos(), id, input, output, filter);
		NetworkManager.sendToServer(packetSlotSave);
	}

	private void drawSlotSateColor(MachineBaseBlockEntity machineBase, Direction side, int slotID, int inx, int iny, GuiBase<?> gui) {
		iny += 4;
		int sx = inx + getX() + gui.getGuiLeft();
		int sy = iny + getY() + gui.getGuiTop();
		SlotConfiguration.SlotConfigHolder slotConfigHolder = machineBase.getSlotConfiguration().getSlotDetails(slotID);
		if (slotConfigHolder == null) {
			RebornCore.LOGGER.debug("Humm, this isnt suppoed to happen");
			return;
		}
		SlotConfiguration.SlotConfig slotConfig = slotConfigHolder.getSideDetail(side);
		Color color;
		switch (slotConfig.getSlotIO().getIoConfig()) {
			case NONE:
				color = new Color(0, 0, 0, 0);
				break;
			case INPUT:
				color = new Color(0, 0, 255, 128);
				break;
			case OUTPUT:
				color = new Color(255, 69, 0, 128);
				break;
			default:
				color = new Color(0, 0, 0, 0);
				break;
		}
		GlStateManager.color3f(255, 255, 255);
		GuiUtil.drawGradientRect(sx, sy, 18, 18, color.getColor(), color.getColor());
		GlStateManager.color3f(255, 255, 255);

	}

	private boolean isInBox(int rectX, int rectY, int rectWidth, int rectHeight, double pointX, double pointY, GuiBase<?> guiBase) {
		rectX += getX();
		rectY += getY();
		return isInRect(guiBase, rectX, rectY, rectWidth, rectHeight, pointX, pointY);
		//return (pointX - guiBase.getGuiLeft()) >= rectX - 1 && (pointX - guiBase.getGuiLeft()) < rectX + rectWidth + 1 && (pointY - guiBase.getGuiTop()) >= rectY - 1 && (pointY - guiBase.getGuiTop()) < rectY + rectHeight + 1;
	}

	public void drawState(GuiBase<?> gui,
	                      IWorld world,
	                      BakedModel model,
	                      BlockState actualState,
	                      BlockPos pos,
	                      BlockRenderManager dispatcher,
	                      int x,
	                      int y,
	                      float rotAngle,
	                      float rotX,
	                      float rotY,
	                      float rotZ) {

		GlStateManager.pushMatrix();
		GlStateManager.enableDepthTest();
		GlStateManager.translatef(8 + gui.getGuiLeft() + this.x + x, 8 + gui.getGuiTop() + this.y + y, 512);
		GlStateManager.scalef(16F, 16F, 16F);
		GlStateManager.translatef(0.5F, 0.5F, 0.5F);
		GlStateManager.scalef(-1, -1, -1);
		if (rotAngle != 0) {
			GlStateManager.rotatef(rotAngle, rotX, rotY, rotZ);
		}
		dispatcher.getModelRenderer().render(model, actualState, 1F, false);
		GlStateManager.disableDepthTest();
		GlStateManager.popMatrix();

/*		GlStateManager.pushMatrix();
		GlStateManager.enableDepth();
		//		GlStateManager.translate(8 + gui.xFactor + this.x + x, 8 + gui.yFactor + this.y + y, 1000);
		GlStateManager.translate(gui.xFactor + this.x + x, gui.yFactor + this.y + y, 512);
		if (rotAngle != 0) {
			GlStateManager.rotate(rotAngle, rotX, rotY, rotZ);
		}
		GlStateManager.scale(16F, 16F, 16F);
		GlStateManager.translate(-0.5F, -0.5F, -0.5F);
		GlStateManager.scale(-1, -1, -1);
		GlStateManager.disableDepth();
		GlStateManager.popMatrix();*/
	}

	public void drawState(GuiBase<?> gui, IWorld world, BakedModel model, BlockState actualState, BlockPos pos, BlockRenderManager dispatcher, int x, int y) {
		drawState(gui, world, model, actualState, pos, dispatcher, x, y, 0, 0, 0, 0);
	}
}