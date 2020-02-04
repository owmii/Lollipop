package owmii.lib.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class CubeModel extends Model {
    private final ModelRenderer cube;

    public CubeModel(int pixels) {
        super(RenderType::entitySolid);
        this.textureWidth = pixels * 4;
        this.textureHeight = pixels * 2;
        this.cube = new ModelRenderer(this, 0, 0);
        float offset = -(pixels / 2.0F);
        this.cube.addBox(offset, offset, offset, pixels, pixels, pixels);
        this.cube.setRotationPoint(0F, 0F, 0F);
        this.cube.setTextureSize(this.textureWidth, this.textureHeight);
        this.cube.mirror = true;
    }

    public void render() {
        render(0.0625f);
    }

    public void render(float scale) {
        // this.cube.render(scale); TODO
    }

    @Override
    public void render(MatrixStack p_225598_1_, IVertexBuilder p_225598_2_, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_) {

    }
}
