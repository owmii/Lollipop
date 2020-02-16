package owmii.lib.client.util;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class Render {
    public static final int MAX_LIGHT = 15728880;

    public static void quad(Matrix4f matrix4f, IVertexBuilder buffer, TextureAtlasSprite sprite, float width, float height) {
        quad(matrix4f, buffer, sprite, width, height, MAX_LIGHT, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void quad(Matrix4f matrix4f, IVertexBuilder buffer, TextureAtlasSprite sprite, float width, float height, int light) {
        quad(matrix4f, buffer, sprite, width, height, light, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void quad(Matrix4f matrix4f, IVertexBuilder buffer, TextureAtlasSprite sprite, float width, float height, float r, float g, float b) {
        quad(matrix4f, buffer, sprite, width, height, MAX_LIGHT, r, g, b, 1.0F);
    }

    public static void quad(Matrix4f matrix4f, IVertexBuilder buffer, TextureAtlasSprite sprite, float width, float height, float r, float g, float b, float a) {
        quad(matrix4f, buffer, sprite, width, height, MAX_LIGHT, r, g, b, a);
    }

    public static void quad(Matrix4f matrix4f, IVertexBuilder buffer, TextureAtlasSprite sprite, float width, float height, int light, float r, float g, float b, float a) {
        buffer.pos(matrix4f, 0, 0, height).color(r, g, b, a).tex(sprite.getMinU(), sprite.getMaxV()).lightmap(light).endVertex();
        buffer.pos(matrix4f, width, 0, height).color(r, g, b, a).tex(sprite.getMaxU(), sprite.getMaxV()).lightmap(light).endVertex();
        buffer.pos(matrix4f, width, 0, 0).color(r, g, b, a).tex(sprite.getMaxU(), sprite.getMinV()).lightmap(light).endVertex();
        buffer.pos(matrix4f, 0, 0, 0).color(r, g, b, a).tex(sprite.getMinU(), sprite.getMinV()).lightmap(light).endVertex();
    }

    public static void quad(Matrix4f matrix4f, IVertexBuilder buffer, float width, float height) {
        quad(matrix4f, buffer, width, height, MAX_LIGHT, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void quad(Matrix4f matrix4f, IVertexBuilder buffer, float width, float height, int light) {
        quad(matrix4f, buffer, width, height, light, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void quad(Matrix4f matrix4f, IVertexBuilder buffer, float width, float height, float r, float g, float b) {
        quad(matrix4f, buffer, width, height, MAX_LIGHT, r, g, b, 1.0F);
    }

    public static void quad(Matrix4f matrix4f, IVertexBuilder buffer, float width, float height, float r, float g, float b, float a) {
        quad(matrix4f, buffer, width, height, MAX_LIGHT, r, g, b, a);
    }

    public static void quad(Matrix4f matrix4f, IVertexBuilder buffer, float width, float height, int light, float r, float g, float b, float a) {
        buffer.pos(matrix4f, 0, 0, height).color(r, g, b, a).tex(0.0F, 1.0F).lightmap(light).endVertex();
        buffer.pos(matrix4f, width, 0, height).color(r, g, b, a).tex(1.0F, 1.0F).lightmap(light).endVertex();
        buffer.pos(matrix4f, width, 0, 0).color(r, g, b, a).tex(1.0F, 0.0F).lightmap(light).endVertex();
        buffer.pos(matrix4f, 0, 0, 0).color(r, g, b, a).tex(0.0F, 0.0F).lightmap(light).endVertex();
    }
}
