package dev.albercl.mixin.polypokemon;

import com.cobblemon.mod.common.pokemon.Species;
import com.neowalker.polypokemon.disguise.DisguiseManager;
import dev.albercl.conquestmod.common.permissions.PolymorphPermissions;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DisguiseManager.class)
public abstract class PolymorphDisguiseManagerMixin {
    @Inject(method = "applyDisguise", at = @At("HEAD"), cancellable = true, remap = false)
    private static void conquestMod$requireDisguisePermission(ServerPlayer player, Species species, String additionalProperties, CallbackInfo ci) {
        if (!PolymorphPermissions.canUseDisguise(player)) {
            player.displayClientMessage(PolymorphPermissions.denyMessage(), false);
            ci.cancel();
        }
    }
}