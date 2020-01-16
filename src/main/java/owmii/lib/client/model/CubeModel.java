package owmii.lib.client.model;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;

public class CubeModel extends Model {
    private final RendererModel cube;

    public CubeModel(int pixels) {
        this.textureWidth = pixels * 4;
        this.textureHeight = pixels * 2;
        this.cube = new RendererModel(this, 0, 0);
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
        this.cube.render(scale);
    }
}
