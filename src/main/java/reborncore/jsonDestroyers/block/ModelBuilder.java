package reborncore.jsonDestroyers.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.util.EnumFacing;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ModelBuilder {

    public static BakedQuad copyQuad(BakedQuad quad) {
        return new BakedQuad(Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length), quad.getTintIndex(), quad.getFace());
    }


    public static BakedQuad changeTexture(BakedQuad quad, TextureAtlasSprite tex) {
        quad = copyQuad(quad);

        for (int i = 0; i < 4; ++i) {
            int j = 7 * i;

            float x = Float.intBitsToFloat(quad.getVertexData()[j]);
            float y = Float.intBitsToFloat(quad.getVertexData()[j + 1]);
            float z = Float.intBitsToFloat(quad.getVertexData()[j + 2]);
            float u = 0.0F;
            float v = 0.0F;

            if (x < 0 || x > 1) x = (x + 1) % 1;
            if (y < 0 || y > 1) y = (y + 1) % 1;
            if (z < 0 || z > 1) z = (z + 1) % 1;

            switch (quad.getFace().ordinal()) {
                case 0:
                    u = x * 16.0F;
                    v = (1.0F - z) * 16.0F;
                    break;
                case 1:
                    u = x * 16.0F;
                    v = z * 16.0F;
                    break;
                case 2:
                    u = (1.0F - x) * 16.0F;
                    v = (1.0F - y) * 16.0F;
                    break;
                case 3:
                    u = x * 16.0F;
                    v = (1.0F - y) * 16.0F;
                    break;
                case 4:
                    u = z * 16.0F;
                    v = (1.0F - y) * 16.0F;
                    break;
                case 5:
                    u = (1.0F - z) * 16.0F;
                    v = (1.0F - y) * 16.0F;
            }

            quad.getVertexData()[j + 4] = Float.floatToRawIntBits(tex.getInterpolatedU((double) u));
            quad.getVertexData()[j + 4 + 1] = Float.floatToRawIntBits(tex.getInterpolatedV((double) v));
        }

        return quad;
    }

    public static SimpleBakedModel changeIcon(IBakedModel model, TextureAtlasSprite texture) {
        SimpleBakedModel bakedModel = new SimpleBakedModel(new LinkedList(), newBlankFacingLists(), model.isGui3d(), model.isAmbientOcclusion(), texture, model.getItemCameraTransforms());

        for (Object o : model.getGeneralQuads()) {
            bakedModel.getGeneralQuads().add(changeTexture((BakedQuad) o, texture));
        }

        for (EnumFacing facing : EnumFacing.values()) {
            for (Object o : model.getFaceQuads(facing)) {
                bakedModel.getFaceQuads(facing).add(changeTexture((BakedQuad) o, texture));
            }
        }

        return bakedModel;
    }

    public static List newBlankFacingLists() {
        Object[] list = new Object[EnumFacing.values().length];
        for (int i = 0; i < EnumFacing.values().length; ++i) {
            list[i] = Lists.newLinkedList();
        }

        return ImmutableList.copyOf(list);
    }

    public static ModelResourceLocation getModelResourceLocation(IBlockState state) {
        return new ModelResourceLocation(Block.blockRegistry.getNameForObject(state.getBlock()), (new DefaultStateMapper()).getPropertyString(state.getProperties()));
    }
}
