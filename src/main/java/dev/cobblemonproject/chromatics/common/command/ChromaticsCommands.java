package dev.cobblemonproject.chromatics.common.command;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.cobblemonproject.chromatics.common.chromatic.ChromaticLifecycle;
import dev.cobblemonproject.chromatics.common.chromatic.ChromaticProfile;
import dev.cobblemonproject.chromatics.common.chromatic.ChromaticProfileStorage;
import dev.cobblemonproject.chromatics.common.network.ChromaticNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;

public final class ChromaticsCommands {
    private static final String PROPERTIES = "pokemon";

    private ChromaticsCommands() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(CommandManager.literal("chromatics")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("spawn")
                    .then(CommandManager.argument(PROPERTIES, PokemonPropertiesArgumentType.Companion.properties())
                        .executes(ChromaticsCommands::spawn)))
                .then(CommandManager.literal("give")
                    .then(CommandManager.argument("player", EntityArgumentType.player())
                        .then(CommandManager.argument(PROPERTIES, PokemonPropertiesArgumentType.Companion.properties())
                            .executes(ChromaticsCommands::give))))
                .then(CommandManager.literal("set")
                    .then(CommandManager.argument("targets", EntityArgumentType.entities())
                        .then(CommandManager.argument("chromatic", BoolArgumentType.bool())
                            .executes(ChromaticsCommands::set))))
                .then(CommandManager.literal("inspect")
                    .then(CommandManager.argument("target", EntityArgumentType.entity())
                        .executes(ChromaticsCommands::inspect)))
            )
        );
    }

    private static int spawn(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        PokemonProperties properties = properties(context);
        if (properties.getSpecies() == null) {
            source.sendError(Text.literal("A Pokemon species is required."));
            return 0;
        }
        PokemonEntity entity = properties.createEntity(source.getWorld());
        ChromaticLifecycle.ForceResult result = ChromaticLifecycle.force(entity.getPokemon(), true);
        if (result != ChromaticLifecycle.ForceResult.SUCCESS) {
            return failure(source, result);
        }

        Vec3d position = source.getPosition();
        entity.refreshPositionAndAngles(position.x, position.y, position.z, entity.getYaw(), entity.getPitch());
        BlockPos blockPos = BlockPos.ofFloored(position);
        entity.initialize(source.getWorld(), source.getWorld().getLocalDifficulty(blockPos), SpawnReason.COMMAND, null);
        if (!source.getWorld().spawnEntity(entity)) {
            source.sendError(Text.literal("Unable to spawn the chromatic Pokemon."));
            return 0;
        }
        source.sendFeedback(
            () -> Text.literal("Spawned chromatic " + entity.getPokemon().getSpecies().getName() + "."),
            true
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int give(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        PokemonProperties properties = properties(context);
        if (properties.getSpecies() == null) {
            source.sendError(Text.literal("A Pokemon species is required."));
            return 0;
        }
        Pokemon pokemon = properties.create(player);
        ChromaticLifecycle.ForceResult result = ChromaticLifecycle.force(pokemon, true);
        if (result != ChromaticLifecycle.ForceResult.SUCCESS) {
            return failure(source, result);
        }
        if (!Cobblemon.INSTANCE.getStorage().getParty(player).add(pokemon)) {
            source.sendError(Text.literal("Unable to add the chromatic Pokemon to the target player's storage."));
            return 0;
        }
        source.sendFeedback(
            () -> Text.literal("Gave chromatic " + pokemon.getSpecies().getName() + " to " + player.getName().getString() + "."),
            true
        );
        return Command.SINGLE_SUCCESS;
    }

    private static int set(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        boolean chromatic = BoolArgumentType.getBool(context, "chromatic");
        Collection<? extends Entity> targets = EntityArgumentType.getEntities(context, "targets");
        int changed = 0;
        int rejected = 0;
        for (Entity target : targets) {
            if (!(target instanceof PokemonEntity pokemonEntity)) {
                rejected++;
                continue;
            }
            ChromaticLifecycle.ForceResult result = ChromaticLifecycle.force(pokemonEntity.getPokemon(), chromatic);
            if (result == ChromaticLifecycle.ForceResult.SUCCESS) {
                ChromaticNetworking.sync(pokemonEntity);
                changed++;
            } else {
                rejected++;
            }
        }
        int changedCount = changed;
        int rejectedCount = rejected;
        source.sendFeedback(
            () -> Text.literal("Updated " + changedCount + " Pokemon; rejected " + rejectedCount + "."),
            true
        );
        return changed;
    }

    private static int inspect(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        Entity target = EntityArgumentType.getEntity(context, "target");
        if (!(target instanceof PokemonEntity pokemonEntity)) {
            source.sendError(Text.literal("The selected entity is not a Pokemon."));
            return 0;
        }
        Pokemon pokemon = pokemonEntity.getPokemon();
        ChromaticProfileStorage.ReadResult stored = ChromaticProfileStorage.read(pokemon);
        if (stored.status() != ChromaticProfileStorage.ReadStatus.VALID) {
            source.sendError(Text.literal("Chromatic profile status: " + stored.status() + "."));
            return 0;
        }
        ChromaticProfile profile = stored.value();
        source.sendFeedback(
            () -> Text.literal(
                "Pokemon=" + pokemon.getSpecies().getName()
                    + ", shiny=" + pokemon.getShiny()
                    + ", chromatic=" + profile.chromatic()
                    + ", variant=" + profile.variant()
            ),
            false
        );
        return Command.SINGLE_SUCCESS;
    }

    private static PokemonProperties properties(CommandContext<ServerCommandSource> context) {
        return PokemonPropertiesArgumentType.Companion.getPokemonProperties(context, PROPERTIES);
    }

    private static int failure(ServerCommandSource source, ChromaticLifecycle.ForceResult result) {
        String message = switch (result) {
            case REGIONAL_FORM -> "Regional forms cannot be chromatic.";
            case INVALID_PROFILE -> "The Pokemon has an invalid chromatic profile and was not overwritten.";
            case CLIENT_SIDE -> "This command can only run on the server.";
            case SUCCESS -> "";
        };
        source.sendError(Text.literal(message));
        return 0;
    }
}
