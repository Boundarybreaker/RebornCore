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

package reborncore.client.shields;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.item.ItemDynamicRenderer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureCache;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import reborncore.common.shields.json.ShieldJsonLoader;
import reborncore.common.shields.json.ShieldUser;
import reborncore.common.util.ItemNBTHelper;
import reborncore.mixin.extensions.ItemDynamicRendererExtensions;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mark on 21/03/2016.
 */
public class RebornItemStackRenderer extends ItemDynamicRenderer {

	private BannerBlockEntity banner = new BannerBlockEntity();
	private ShieldEntityModel modelShield = new ShieldEntityModel();

	private HashMap<String, AbstractTexture> customTextureMap = new HashMap<>();
	private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue());

	ItemDynamicRenderer renderer;

	public static void setup(){
		ItemDynamicRendererExtensions.getExtension().extend(RebornItemStackRenderer::new);
	}

	public RebornItemStackRenderer(ItemDynamicRenderer renderer) {
		this.renderer = renderer;
	}

	@Override
	public void render(ItemStack itemStackIn) {
		if (itemStackIn.getItem() == Items.SHIELD) {
			boolean isCustom = !ItemNBTHelper.getBoolean(itemStackIn, "vanilla", false);
			if (isCustom) {
				Identifier location = null;
				String str = ItemNBTHelper.getString(itemStackIn, "type", "vanilla");
				if (ShieldJsonLoader.shieldJsonFile == null || ShieldJsonLoader.shieldJsonFile.userList == null) {
					renderer.render(itemStackIn);
					return;
				}
				for (ShieldUser user : ShieldJsonLoader.shieldJsonFile.userList) {
					if (user.username.equalsIgnoreCase(str)) {
						location = new Identifier("lookup_" + str);
					}
				}
				if (location == null) {
					renderer.render(itemStackIn);
					return;
				}
				ShieldTexture shieldTexture = ShieldTextureStore.getTexture(str);
				if (shieldTexture != null) {
					if (shieldTexture.getState() == DownloadState.DOWNLOADED) {
						if (customTextureMap.containsKey(location.getPath())) {
							MinecraftClient.getInstance().getTextureManager().bindTexture(location);
						} else {
							AbstractTexture texture = shieldTexture.getTexture();
							customTextureMap.put(location.getPath(), texture);
							final Identifier resourceLocation = location;
							THREAD_POOL.submit(() -> MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().getTextureManager().registerTexture(resourceLocation, texture)));
							MinecraftClient.getInstance().getTextureManager().bindTexture(TextureCache.DEFAULT_SHIELD);
						}
					} else {
						MinecraftClient.getInstance().getTextureManager().bindTexture(TextureCache.DEFAULT_SHIELD);
					}
				} else {
					MinecraftClient.getInstance().getTextureManager().bindTexture(TextureCache.DEFAULT_SHIELD);
				}
			} else {
				renderer.render(itemStackIn);
				return;
			}
			GlStateManager.pushMatrix();
			GlStateManager.scalef(1.0F, -1.0F, -1.0F);
			modelShield.renderItem();
			GlStateManager.popMatrix();
			return;
		}
		renderer.render(itemStackIn);

	}
}
