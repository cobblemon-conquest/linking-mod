package dev.albercl.mixin.polypokemon.client;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(com.neowalker.polypokemon.PolymorphClient.class)
public abstract class PolymorphClientMixin {
    @Inject(method = "lambda$static$0", at = @At("HEAD"), cancellable = true, remap = false)
    private static void conquestMod$fixPokemonSuggestions(CommandContext<?> context, SuggestionsBuilder builder, CallbackInfoReturnable<CompletableFuture<?>> cir) {
        Collection<Species> species = PokemonSpecies.getSpecies();

        for (Species entry : species) {
            builder.suggest(entry.getName());
        }

        cir.setReturnValue(builder.buildFuture());
    }

    @Inject(method = "lambda$registerClientEvents$11", at = @At("HEAD"), cancellable = true, remap = false)
    private static void conquestMod$disableJoinThirdPerson(CallbackInfo ci) {
        ci.cancel();
    }
}