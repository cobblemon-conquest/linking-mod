package dev.albercl.mixin.polypokemon;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.pokemon.Species;
import com.neowalker.polypokemon.Polymorph;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Polymorph.class)
public abstract class PolymorphServerMixin {
    @Redirect(
        method = "lambda$registerC2SPackets$0",
        at = @At(
            value = "INVOKE",
            target = "Lcom/cobblemon/mod/common/api/pokemon/PokemonSpecies;getByName(Ljava/lang/String;)Lcom/cobblemon/mod/common/pokemon/Species;"
        ),
        remap = false
    )
    private static Species conquestMod$useStaticGetByName(PokemonSpecies ignored, String speciesName) {
        return PokemonSpecies.getByName(speciesName);
    }
}