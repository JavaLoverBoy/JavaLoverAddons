package com.rosegold.rosegoldexpansions.mixins;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase {

    @Inject(method = "isPotionActive", at = @At("RETURN"), cancellable = true)
    public void antiBlind(Potion potionIn, CallbackInfoReturnable<Boolean> cir) {
        if(potionIn == MobEffects.BLINDNESS) {
            cir.setReturnValue(false);
        }
    }
}
