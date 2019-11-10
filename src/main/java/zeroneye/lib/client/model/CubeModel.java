package zeroneye.lib.client.model;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;

public class CubeModel extends Model {
    private final RendererModel cube;

    public CubeModel(int pixels) {
        this.textureWidth = 64;
        this.textureHeight = 32;
        this.cube = new RendererModel(this, 0, 0);
        float offset = -(pixels / 2.0F);
        this.cube.addBox(offset, offset, offset, pixels, pixels, pixels);
        this.cube.setRotationPoint(0F, 0F, 0F);
        this.cube.setTextureSize(64, 32);
        this.cube.mirror = true;
    }

    public void render() {
        render(0.0625f);
    }

    public void render(float scale) {
        this.cube.render(0.0625f);
    }
}
