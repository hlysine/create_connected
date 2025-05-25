package com.hlysine.create_connected.content.redstonelinkwildcard;

import com.google.common.collect.Sets;
import com.hlysine.create_connected.CreateConnected;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.levelWrappers.WorldHelper;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.*;

@EventBusSubscriber(modid = CreateConnected.MODID)
public class LinkWildcardNetworkHandler {
    static final Map<LevelAccessor, Map<Couple<Frequency>, Set<Couple<Frequency>>>> wildcard_connections =
            new IdentityHashMap<>();

    @SubscribeEvent
    public static void onLoadWorld(LevelEvent.Load event) {
        wildcard_connections.put(event.getLevel(), new HashMap<>());
        CreateConnected.LOGGER.debug("Prepared Redstone Network Wildcards for {}", WorldHelper.getDimensionID(event.getLevel()));
    }

    @SubscribeEvent
    public static void onUnloadWorld(LevelEvent.Unload event) {
        wildcard_connections.remove(event.getLevel());
        CreateConnected.LOGGER.debug("Removed Redstone Network Wildcards for {}", WorldHelper.getDimensionID(event.getLevel()));
    }

    public static Map<Couple<Frequency>, Set<Couple<Frequency>>> wildcardsIn(LevelAccessor world) {
        if (!wildcard_connections.containsKey(world)) {
            CreateConnected.LOGGER.warn("Tried to Access unprepared network wildcards of {}", WorldHelper.getDimensionID(world));
            return new HashMap<>();
        }
        return wildcard_connections.get(world);
    }

    public static Set<IRedstoneLinkable> getNetworkOf(RedstoneLinkNetworkHandler handler, LevelAccessor world, IRedstoneLinkable actor) {
        Map<Couple<Frequency>, Set<IRedstoneLinkable>> networksInWorld = handler.networksIn(world);
        Couple<Frequency> key = actor.getNetworkKey();
        if (!networksInWorld.containsKey(key))
            networksInWorld.put(key, new LinkedHashSet<>());
        Set<IRedstoneLinkable> set = networksInWorld.get(key);
        set.removeIf(other -> !other.isAlive());
        Map<Couple<Frequency>, Set<Couple<Frequency>>> wildcards = wildcardsIn(world);
        if (wildcards.containsKey(key)) {
            Set<Couple<Frequency>> connections = wildcards.get(key);
            for (Couple<Frequency> connection : connections) {
                Set<IRedstoneLinkable> set2 = networksInWorld.getOrDefault(connection, Collections.emptySet());
                set2.removeIf(other -> !other.isAlive());
                set = concatSetView(set, set2);
            }
        }
        return set;
    }

    private static Set<IRedstoneLinkable> concatSetView(Set<IRedstoneLinkable> set1, Set<IRedstoneLinkable> set2) {
        if (set1.isEmpty()) {
            return set2;
        } else if (set2.isEmpty()) {
            return set1;
        } else {
            return Sets.union(set1, set2);
        }
    }

    public static void addToNetwork(RedstoneLinkNetworkHandler handler, LevelAccessor world, Couple<Frequency> key) {
        Map<Couple<Frequency>, Set<Couple<Frequency>>> wildcards = wildcardsIn(world);
        if (!wildcards.containsKey(key)) {
            HashSet<Couple<Frequency>> connections = new LinkedHashSet<>();
            Map<Couple<Frequency>, Set<IRedstoneLinkable>> networks = handler.networksIn(world);
            for (Couple<Frequency> otherKey : networks.keySet()) {
                if (!otherKey.equals(key) && test(key, otherKey)) {
                    connections.add(otherKey);
                }
            }
            wildcards.put(key, connections);
        }
        for (Map.Entry<Couple<Frequency>, Set<Couple<Frequency>>> entry : wildcards.entrySet()) {
            if (!entry.getKey().equals(key) && test(entry.getKey(), key)) {
                entry.getValue().add(key);
            }
        }
    }

    public static void removeFromNetwork(RedstoneLinkNetworkHandler handler, LevelAccessor world, Couple<Frequency> key) {
        Map<Couple<Frequency>, Set<IRedstoneLinkable>> networks = handler.networksIn(world);
        if (networks.containsKey(key) && !networks.get(key).isEmpty())
            return;
        Map<Couple<Frequency>, Set<Couple<Frequency>>> wildcards = wildcardsIn(world);
        wildcards.remove(key);
        for (Iterator<Map.Entry<Couple<Frequency>, Set<Couple<Frequency>>>> iterator = wildcards.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<Couple<Frequency>, Set<Couple<Frequency>>> entry = iterator.next();
            entry.getValue().remove(key);
            if (entry.getValue().isEmpty()) {
                iterator.remove();
            }
        }
    }

    private static boolean test(Couple<Frequency> transmitter, Couple<Frequency> receiver) {
        return wildcardTransmit(transmitter.getFirst(), receiver.getFirst()) && wildcardTransmit(transmitter.getSecond(), receiver.getSecond()) ||
                wildcardReceive(transmitter.getFirst(), receiver.getFirst()) && wildcardReceive(transmitter.getSecond(), receiver.getSecond());
    }

    private static boolean wildcardTransmit(Frequency transmitter, Frequency receiver) {
        if (transmitter.getStack().getItem() instanceof ILinkWildcard wildcard) {
            return wildcard.test(receiver);
        } else {
            return transmitter.equals(receiver);
        }
    }

    private static boolean wildcardReceive(Frequency transmitter, Frequency receiver) {
        if (receiver.getStack().getItem() instanceof ILinkWildcard wildcard) {
            return wildcard.test(transmitter);
        } else {
            return transmitter.equals(receiver);
        }
    }
}
