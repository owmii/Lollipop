package owmii.lib.util.dev;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DevUtil {
    public static void genItemModel(@Nullable ResourceLocation location, String folder) {
        if (location != null) {
            String domain = location.getNamespace();
            String name = location.getPath();
            String jsonString = "{\n  \"parent\": \"item/generated\",\n  \"textures\": {\n\t\"layer0\": \"" + domain + ":item/" + name + "\"\n  }\n}";
            Path path = Paths.get(FMLPaths.GAMEDIR.get().toAbsolutePath().toString(), "../../../" + folder + "/src/main/resources/assets/" + domain + "/models/item/" + name + ".json");

            try {
                Files.createDirectories(path.getParent());
                try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
                    bufferedwriter.write(jsonString);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
