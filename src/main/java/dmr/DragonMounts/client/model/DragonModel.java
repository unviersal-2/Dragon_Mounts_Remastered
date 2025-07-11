package dmr.DragonMounts.client.model;

import dmr.DragonMounts.DMR;
import dmr.DragonMounts.server.entity.TameableDragonEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.loading.math.MathParser;
import software.bernie.geckolib.model.GeoModel;

public class DragonModel extends GeoModel<TameableDragonEntity> {

    @Override
    public ResourceLocation getModelResource(TameableDragonEntity object) {
        if (object != null && object.getBreed() != null && object.getBreed().getDragonModelLocation() != null) {
            return object.getBreed().getDragonModelLocation();
        }

        return DMR.id("geo/dragon.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(TameableDragonEntity object) {
        var breed = object.getBreed();
        var breedResourceLocation = breed.getResourceLocation();
        if (object.hasVariant()) {
            return object.getVariant().skinTexture();
        }
        return DMR.id("textures/entity/dragon/" + breedResourceLocation.getPath() + "/body.png");
    }

    @Override
    public ResourceLocation getAnimationResource(TameableDragonEntity animatable) {
        if (animatable != null
                && animatable.getBreed() != null
                && animatable.getBreed().getDragonAnimationLocation() != null) {
            return animatable.getBreed().getDragonAnimationLocation();
        }

        return DMR.id("animations/dragon.animation.json");
    }

    @Override
    public void applyMolangQueries(AnimationState<TameableDragonEntity> animationState, double animTime) {
        super.applyMolangQueries(animationState, animTime);
        var dragon = animationState.getAnimatable();
        MathParser.setVariable("query.head_pitch", () -> dragon.getXRot() * 1);
        MathParser.setVariable("query.head_yaw", () -> (dragon.yBodyRot - dragon.yHeadRot) * -1);

        var viewVector = dragon.getDeltaMovement().multiply(0, 0.25, 0);

        if (viewVector != null) {
            var pitch = viewVector.y;
            MathParser.setVariable("query.pitch", () -> Mth.clamp(pitch, -1, 1));
        }
    }
}
