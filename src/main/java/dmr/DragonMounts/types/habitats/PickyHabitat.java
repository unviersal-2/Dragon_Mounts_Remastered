package dmr.DragonMounts.types.habitats;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public record PickyHabitat(List<Habitat> habitats) implements Habitat {
    public static final Codec<PickyHabitat> CODEC = Habitat.CODEC
            .listOf()
            .fieldOf("required_habitats")
            .xmap(PickyHabitat::new, PickyHabitat::habitats)
            .codec();

    @Override
    public int getHabitatPoints(Level level, BlockPos pos) {
        int points = 0;
        for (var habitat : habitats) {
            int i = habitat.getHabitatPoints(level, pos);
            if (i == 0) return 0; // ALL habitat conditions must be met. Otherwise, nope.
            points += i;
        }
        return points;
    }

    @Override
    public String type() {
        return Habitat.PICKY;
    }
}
