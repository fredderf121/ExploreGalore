package fred.exploregalore.mixin;

import fred.exploregalore.lib.EntityPrevPosAccess;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityPrevPosMixin implements EntityPrevPosAccess {
    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getY();

    @Shadow
    public abstract double getZ();

    @Shadow
    public abstract String getEntityName();

    @Shadow
    public World world;
    @Unique
    private double prevXServer;
    @Unique
    private double prevYServer;
    @Unique
    private double prevZServer;

    @Inject(at = @At("TAIL"), method = "<init>")
    private void Entity(CallbackInfo info) {
        this.prevXServer = 0;
        this.prevYServer = 0;
        this.prevZServer = 0;
    }

    @Override
    public void savePrevPos() {
        this.setPrevXServer(this.getX());
        this.setPrevZServer(this.getZ());
        this.setPrevYServer(this.getY());
    }


    public double getPrevXServer() {
        return prevXServer;
    }

    public void setPrevXServer(double prevXServer) {
        this.prevXServer = prevXServer;
    }

    public double getPrevYServer() {
        return prevYServer;
    }

    public void setPrevYServer(double prevYServer) {
        this.prevYServer = prevYServer;
    }

    public double getPrevZServer() {
        return prevZServer;
    }

    public void setPrevZServer(double prevZServer) {
        this.prevZServer = prevZServer;
    }
}
