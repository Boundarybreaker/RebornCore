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

// Date: 27/11/2016 17:33:21
// Template version 1.1
// Java generated by Techne
// Keep in mind that you still need to fill in some blanks
// - ZeuX

package reborncore.client;

import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;

public class ModelSantaHat extends EntityModel {
	Cuboid hatband1;
	Cuboid hatband2;
	Cuboid hatband3;
	Cuboid hatband4;
	Cuboid hatbase1;
	Cuboid hatband5;
	Cuboid hatband6;
	Cuboid hatbase2;
	Cuboid hatextension1;
	Cuboid hatextension2;
	Cuboid hatextension3;
	Cuboid hatextension4;
	Cuboid hatball1;
	Cuboid hatball2;
	Cuboid hatball3;
	Cuboid hatball4;
	Cuboid hatball5;
	Cuboid hatball6;

	public ModelSantaHat() {
		textureWidth = 64;
		textureHeight = 64;

		hatband1 = new Cuboid(this, 0, 32);
		hatband1.addBox(-4F, -8F, -5F, 8, 1, 1);
		hatband1.setRotationPoint(0F, 0F, 0F);
		hatband1.setTextureSize(64, 64);
		hatband1.mirror = true;
		setRotation(hatband1, 0F, 0F, 0F);
		hatband2 = new Cuboid(this, 0, 32);
		hatband2.addBox(-4F, -8F, 4F, 8, 1, 1);
		hatband2.setRotationPoint(0F, 0F, 0F);
		hatband2.setTextureSize(64, 64);
		hatband2.mirror = true;
		setRotation(hatband2, 0F, 0F, 0F);
		hatband3 = new Cuboid(this, 0, 34);
		hatband3.addBox(-5F, -8F, -4F, 1, 1, 8);
		hatband3.setRotationPoint(0F, 0F, 0F);
		hatband3.setTextureSize(64, 64);
		hatband3.mirror = true;
		setRotation(hatband3, 0F, 0F, 0F);
		hatband4 = new Cuboid(this, 0, 34);
		hatband4.addBox(4F, -8F, -4F, 1, 1, 8);
		hatband4.setRotationPoint(0F, 0F, 0F);
		hatband4.setTextureSize(64, 64);
		hatband4.mirror = true;
		setRotation(hatband4, 0F, 0F, 0F);
		hatbase1 = new Cuboid(this, 0, 43);
		hatbase1.addBox(-4F, -9F, -4F, 8, 1, 8);
		hatbase1.setRotationPoint(0F, 0F, 0F);
		hatbase1.setTextureSize(64, 64);
		hatbase1.mirror = true;
		setRotation(hatbase1, 0F, 0F, 0F);
		hatband5 = new Cuboid(this, 18, 41);
		hatband5.addBox(0F, -7F, -5F, 4, 1, 1);
		hatband5.setRotationPoint(0F, 0F, 0F);
		hatband5.setTextureSize(64, 64);
		hatband5.mirror = true;
		setRotation(hatband5, 0F, 0F, 0F);
		hatband6 = new Cuboid(this, 18, 41);
		hatband6.addBox(-4F, -7F, 0F, 4, 1, 1);
		hatband6.setRotationPoint(0F, 0F, 4F);
		hatband6.setTextureSize(64, 64);
		hatband6.mirror = true;
		setRotation(hatband6, 0F, 0F, 0F);
		hatbase2 = new Cuboid(this, 18, 34);
		hatbase2.addBox(-3F, -10F, -3F, 6, 1, 6);
		hatbase2.setRotationPoint(0F, 0F, 0F);
		hatbase2.setTextureSize(64, 64);
		hatbase2.mirror = true;
		setRotation(hatbase2, 0F, 0.1115358F, 0F);
		hatextension1 = new Cuboid(this, 0, 52);
		hatextension1.addBox(-3F, -11F, -2F, 4, 2, 4);
		hatextension1.setRotationPoint(0F, 0F, 0F);
		hatextension1.setTextureSize(64, 64);
		hatextension1.mirror = true;
		setRotation(hatextension1, 0F, -0.0371786F, 0.0743572F);
		hatextension2 = new Cuboid(this, 16, 52);
		hatextension2.addBox(-2.4F, -12F, -1.5F, 3, 2, 3);
		hatextension2.setRotationPoint(0F, 0F, 0F);
		hatextension2.setTextureSize(64, 64);
		hatextension2.mirror = true;
		setRotation(hatextension2, 0F, 0.0743572F, 0.0743572F);
		hatextension3 = new Cuboid(this, 28, 52);
		hatextension3.addBox(-3.5F, -13F, -1F, 2, 2, 2);
		hatextension3.setRotationPoint(0F, 0F, 0F);
		hatextension3.setTextureSize(64, 64);
		hatextension3.mirror = true;
		setRotation(hatextension3, 0F, 0F, 0.2230717F);
		hatextension4 = new Cuboid(this, 0, 58);
		hatextension4.addBox(-13F, -6.6F, -1F, 2, 3, 2);
		hatextension4.setRotationPoint(0F, 0F, 0F);
		hatextension4.setTextureSize(64, 64);
		hatextension4.mirror = true;
		setRotation(hatextension4, 0F, 0F, 1.264073F);
		hatball1 = new Cuboid(this, 8, 58);
		hatball1.addBox(2F, -14.4F, -1.001F, 2, 2, 2);
		hatball1.setRotationPoint(0F, 0F, 0F);
		hatball1.setTextureSize(64, 64);
		hatball1.mirror = true;
		setRotation(hatball1, 0F, 0F, 0F);
		hatball2 = new Cuboid(this, 16, 57);
		hatball2.addBox(2.5F, -14.8F, -0.5F, 1, 1, 1);
		hatball2.setRotationPoint(0F, 0F, 0F);
		hatball2.setTextureSize(64, 64);
		hatball2.mirror = true;
		setRotation(hatball2, 0F, 0F, 0F);
		hatball3 = new Cuboid(this, 16, 57);
		hatball3.addBox(2.5F, -13F, -0.5F, 1, 1, 1);
		hatball3.setRotationPoint(0F, 0F, 0F);
		hatball3.setTextureSize(64, 64);
		hatball3.mirror = true;
		setRotation(hatball3, 0F, 0F, 0F);
		hatball4 = new Cuboid(this, 16, 57);
		hatball4.addBox(3.4F, -14F, -0.5F, 1, 1, 1);
		hatball4.setRotationPoint(0F, 0F, 0F);
		hatball4.setTextureSize(64, 64);
		hatball4.mirror = true;
		setRotation(hatball4, 0F, 0F, 0F);
		hatball5 = new Cuboid(this, 16, 57);
		hatball5.addBox(2.5F, -14F, 0.4F, 1, 1, 1);
		hatball5.setRotationPoint(0F, 0F, 0F);
		hatball5.setTextureSize(64, 64);
		hatball5.mirror = true;
		setRotation(hatball5, 0F, 0F, 0F);
		hatball6 = new Cuboid(this, 16, 57);
		hatball6.addBox(2.5F, -14F, -1.4F, 1, 1, 1);
		hatball6.setRotationPoint(0F, 0F, 0F);
		hatball6.setTextureSize(64, 64);
		hatball6.mirror = true;
		setRotation(hatball6, 0F, 0F, 0F);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		//setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		hatband1.render(f5);
		hatband2.render(f5);
		hatband3.render(f5);
		hatband4.render(f5);
		hatbase1.render(f5);
		hatband5.render(f5);
		hatband6.render(f5);
		hatbase2.render(f5);
		hatextension1.render(f5);
		hatextension2.render(f5);
		hatextension3.render(f5);
		hatextension4.render(f5);
		hatball1.render(f5);
		hatball2.render(f5);
		hatball3.render(f5);
		hatball4.render(f5);
		hatball5.render(f5);
		hatball6.render(f5);
	}

	public void render(float f5) {
		hatband1.render(f5);
		hatband2.render(f5);
		hatband3.render(f5);
		hatband4.render(f5);
		hatbase1.render(f5);
		hatband5.render(f5);
		hatband6.render(f5);
		hatbase2.render(f5);
		hatextension1.render(f5);
		hatextension2.render(f5);
		hatextension3.render(f5);
		hatextension4.render(f5);
		hatball1.render(f5);
		hatball2.render(f5);
		hatball3.render(f5);
		hatball4.render(f5);
		hatball5.render(f5);
		hatball6.render(f5);
	}

	private void setRotation(Cuboid model, float x, float y, float z) {
		model.pitch = x;
		model.yaw = y;
		model.roll = z;
	}

}
