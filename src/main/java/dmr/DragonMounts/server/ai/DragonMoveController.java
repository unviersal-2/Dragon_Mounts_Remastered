package dmr.DragonMounts.server.ai;

import dmr.DragonMounts.server.entity.TameableDragonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathType;
import net.neoforged.neoforge.common.NeoForgeMod;

public class DragonMoveController extends MoveControl {

    private final TameableDragonEntity dragon;

    public DragonMoveController(TameableDragonEntity dragon) {
        super(dragon);
        this.dragon = dragon;
        this.speedModifier = 4f;
    }

    @Override
    public void tick() {
        // original movement behavior if the entity isn't flying
        if (this.operation == MoveControl.Operation.MOVE_TO) {
            this.operation = MoveControl.Operation.WAIT;
            double xDif = this.wantedX - this.mob.getX();
            double zDif = this.wantedZ - this.mob.getZ();
            double yDif = this.wantedY - this.mob.getY();

            double sq = xDif * xDif + yDif * yDif + zDif * zDif;
            if (sq < (double) 2.5000003E-7F) {
                this.mob.setYya(0.0F);
                this.mob.setZza(0.0F);
                return;
            }

            boolean isAmphibious = !dragon.canDrownInFluidType(Fluids.WATER.getFluidType());
            boolean isInWater = this.mob.isInWater();

            BlockPos blockpos = this.mob.blockPosition();
            BlockState blockstate = this.mob.level.getBlockState(blockpos);

            var shouldFly = !blockstate.isSolid() && dragon.canFly() && (!blockstate.is(Blocks.WATER) || !isAmphibious);
            dragon.setNoGravity(shouldFly || isInWater);
            dragon.setFlying(shouldFly);

            float f9 = (float) ((Mth.atan2(zDif, xDif) * 180.0F) / (float) Math.PI) - 90.0F;
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f9, 10f));

            float speed;

            if (this.mob.onGround()) {
                speed = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
            } else if (isInWater) {
                speed = (float) (this.speedModifier * this.mob.getAttributeValue(NeoForgeMod.SWIM_SPEED));
            } else {
                speed = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.FLYING_SPEED));
            }

            if (!isInWater) {
                if (mob.getNavigation().getPath() != null) {
                    mob.getNavigation().getPath().getNextNode();
                    var type = mob.getNavigation().getPath().getNextNode().type;
                    if (type == PathType.WATER || type == PathType.WATER_BORDER) {
                        isInWater = true;
                    }
                }
            }

            this.mob.setSpeed(speed);
            double d4 = Math.sqrt(xDif * xDif + zDif * zDif);
            if (isInWater) {
                if (Math.abs(yDif) > 1.0E-5F || Math.abs(d4) > 1.0E-5F) {
                    float f3 = -((float) ((Mth.atan2(yDif, d4) * 180.0F) / (float) Math.PI));
                    f3 = Mth.clamp(Mth.wrapDegrees(f3), (float) (-85), (float) 10);
                    this.mob.setXRot(this.rotlerp(this.mob.getXRot(), f3, 5.0F));
                }

                float f6 = Mth.cos(this.mob.getXRot() * (float) (Math.PI / 180.0));
                float f4 = Mth.sin(this.mob.getXRot() * (float) (Math.PI / 180.0));
                this.mob.zza = f6 * speed;
                this.mob.yya = -f4 * speed * 5;
            } else {
                if (Math.abs(yDif) > 1.0E-5F || Math.abs(d4) > 1.0E-5F) {
                    this.mob.setYya(yDif > 0.0 ? speed : -speed);
                }
            }
        } else if (this.operation == Operation.WAIT) {
            this.mob.setYya(0.0F);
            this.mob.setZza(0.0F);
            this.mob.setXxa(0.0F);
        } else {
            super.tick();
        }
    }
}
