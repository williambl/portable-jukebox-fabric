package com.williambl.portablejukebox.client;

import com.williambl.portablejukebox.client.sound.EntityFollowingSound;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class PortableJukeboxClientMod implements ClientModInitializer {

    private static final Map<UUID, SoundInstance> PLAYING_SOUNDS = new HashMap<>();

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(new Identifier("portablejukebox:play"), ((client, handler, buf, responseSender) -> {
            UUID uuid = buf.readUuid();

            SoundInstance oldSound = PLAYING_SOUNDS.get(uuid);
            if (oldSound != null)
                client.getSoundManager().stop(oldSound);

            EntityFollowingSound sound = new EntityFollowingSound(
                    MinecraftClient.getInstance().world.getPlayerByUuid(uuid),
                    ((MusicDiscItem) Objects.requireNonNull(Registry.ITEM.get(new Identifier(buf.readString())))).getSound()
            );
            client.getSoundManager().play(sound);
            PLAYING_SOUNDS.put(uuid, sound);
        }));

        ClientPlayNetworking.registerGlobalReceiver(new Identifier("portablejukebox:stop"), ((client, handler, buf, responseSender) -> {
            UUID uuid = buf.readUuid();
            SoundInstance oldSound = PLAYING_SOUNDS.get(uuid);
            if (oldSound != null)
                client.getSoundManager().stop(oldSound);
            PLAYING_SOUNDS.remove(uuid);
        }));
    }
}
