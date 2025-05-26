package com.hlysine.create_connected.content.redstonelinkwildcard;

import com.hlysine.create_connected.CCItems;
import com.hlysine.create_connected.CreateConnected;
import com.hlysine.create_connected.config.CServer;
import com.hlysine.create_connected.config.FeatureToggle;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.levelWrappers.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.withinRange;

@EventBusSubscriber(modid = CreateConnected.MODID)
public class LinkWildcardNetworkHandler {
    static final Map<LevelAccessor, Map<Couple<Frequency>, Set<Couple<Frequency>>>> wildcard_connections =
            new IdentityHashMap<>();

    @SubscribeEvent
    public static void onLoadWorld(LevelEvent.Load event) {
        wildcard_connections.put(event.getLevel(), new HashMap<>());
        CreateConnected.LOGGER.debug("Link-Wildcard: Prepared Redstone Network Wildcards for {}", WorldHelper.getDimensionID(event.getLevel()));
    }

    @SubscribeEvent
    public static void onUnloadWorld(LevelEvent.Unload event) {
        wildcard_connections.remove(event.getLevel());
        CreateConnected.LOGGER.debug("Link-Wildcard: Removed Redstone Network Wildcards for {}", WorldHelper.getDimensionID(event.getLevel()));
    }

    public static Map<Couple<Frequency>, Set<Couple<Frequency>>> wildcardsIn(LevelAccessor world) {
        if (!wildcard_connections.containsKey(world)) {
            CreateConnected.LOGGER.warn("Link-Wildcard: Tried to Access unprepared network wildcards of {}", WorldHelper.getDimensionID(world));
            return new HashMap<>();
        }
        return wildcard_connections.get(world);
    }

    public static boolean updateNetworkOf(RedstoneLinkNetworkHandler handler, LevelAccessor world, IRedstoneLinkable actor) {
        if (!FeatureToggle.isEnabled(CCItems.REDSTONE_LINK_WILDCARD.getId()))
            return false;

        Couple<Frequency> key = actor.getNetworkKey();
        updateNetworkForReceiver(handler, world, actor, key);
        Map<Couple<Frequency>, Set<Couple<Frequency>>> wildcards = wildcardsIn(world);
        if (wildcards.containsKey(key)) {
            Set<Couple<Frequency>> connections = wildcards.get(key);
            for (Couple<Frequency> connection : connections) {
                updateNetworkForReceiver(handler, world, actor, connection);
            }
        }
        return true;
    }

    private static void updateNetworkForReceiver(RedstoneLinkNetworkHandler handler, LevelAccessor world, IRedstoneLinkable actor, Couple<Frequency> key) {
        Map<Couple<Frequency>, Set<IRedstoneLinkable>> networksInWorld = handler.networksIn(world);
        Map<Couple<Frequency>, Set<Couple<Frequency>>> wildcardsInWorld = wildcardsIn(world);
        Set<IRedstoneLinkable> network = networksInWorld.get(key);
        Set<Couple<Frequency>> wildcards = wildcardsInWorld.get(key);

        handler.globalPowerVersion.incrementAndGet();
        AtomicInteger power = new AtomicInteger(0);

        Consumer<Set<IRedstoneLinkable>> updatePower = (set) -> {
            if (set == null || set.isEmpty())
                return;
            for (Iterator<IRedstoneLinkable> iterator = set.iterator(); iterator.hasNext(); ) {
                IRedstoneLinkable other = iterator.next();
                if (!other.isAlive()) {
                    iterator.remove();
                    continue;
                }

                if (!withinRange(actor, other))
                    continue;

                if (power.get() < 15)
                    power.accumulateAndGet(other.getTransmittedStrength(), Math::max);
            }
        };

        updatePower.accept(network);
        if (wildcards != null)
            for (Couple<Frequency> wildcard : wildcards) {
                Set<IRedstoneLinkable> wildcardNetwork = networksInWorld.get(wildcard);
                updatePower.accept(wildcardNetwork);
            }

        if (actor instanceof LinkBehaviour linkBehaviour) {
            // fix one-to-one loading order problem
            if (linkBehaviour.isListening()) {
                linkBehaviour.newPosition = true;
                linkBehaviour.setReceivedStrength(power.get());
            }
        }

        if (network != null && !network.isEmpty())
            for (IRedstoneLinkable other : network) {
                if (other != actor && other.isListening() && withinRange(actor, other))
                    other.setReceivedStrength(power.get());
            }
        if (wildcards != null && !wildcards.isEmpty())
            for (Couple<Frequency> wildcard : wildcards) {
                Set<IRedstoneLinkable> wildcardNetwork = networksInWorld.get(wildcard);
                if (wildcardNetwork != null && !wildcardNetwork.isEmpty())
                    for (IRedstoneLinkable other : wildcardNetwork) {
                        if (other != actor && other.isListening() && withinRange(actor, other))
                            other.setReceivedStrength(power.get());
                    }
            }
    }

    public static void addToNetwork(RedstoneLinkNetworkHandler handler, LevelAccessor world, IRedstoneLinkable actor) {
        Couple<Frequency> key = actor.getNetworkKey();
        Map<Couple<Frequency>, Set<Couple<Frequency>>> wildcards = wildcardsIn(world);
//        CreateConnected.LOGGER.debug("Link-Wildcard: Adding {}", keyToString(key));
        if (!wildcards.containsKey(key)) {
            HashSet<Couple<Frequency>> connections = new LinkedHashSet<>();
            Map<Couple<Frequency>, Set<IRedstoneLinkable>> networks = handler.networksIn(world);
            for (Couple<Frequency> otherKey : networks.keySet()) {
                if (!otherKey.equals(key) && test(key, otherKey)) {
                    if (connections.add(otherKey)) {
//                        CreateConnected.LOGGER.debug("Link-Wildcard: - Connected to {}", keyToString(otherKey));
                    }
                }
            }
            wildcards.put(key, connections);
        }
        for (Map.Entry<Couple<Frequency>, Set<Couple<Frequency>>> entry : wildcards.entrySet()) {
            if (!entry.getKey().equals(key) && test(entry.getKey(), key)) {
                if (entry.getValue().add(key)) {
//                    CreateConnected.LOGGER.debug("Link-Wildcard: - Reverse connected to {}", keyToString(entry.getKey()));
                }
            }
        }
    }

    public static void removeFromNetwork(RedstoneLinkNetworkHandler handler, LevelAccessor world, IRedstoneLinkable actor) {
        Couple<Frequency> key = actor.getNetworkKey();
        Map<Couple<Frequency>, Set<IRedstoneLinkable>> networks = handler.networksIn(world);
        if (networks.containsKey(key) && !networks.get(key).isEmpty())
            return;
//        CreateConnected.LOGGER.debug("Link-Wildcard: Removing {}", keyToString(key));
        Map<Couple<Frequency>, Set<Couple<Frequency>>> wildcards = wildcardsIn(world);
        wildcards.remove(key);
        for (Map.Entry<Couple<Frequency>, Set<Couple<Frequency>>> entry : wildcards.entrySet()) {
            if (entry.getValue().remove(key)) {
//                CreateConnected.LOGGER.debug("Link-Wildcard: - Removing connection to {}", keyToString(entry.getKey()));
                handler.updateNetworkOf(world, new IRedstoneLinkable() {
                    @Override
                    public int getTransmittedStrength() {
                        return 0;
                    }

                    @Override
                    public void setReceivedStrength(int power) {

                    }

                    @Override
                    public boolean isListening() {
                        return false;
                    }

                    @Override
                    public boolean isAlive() {
                        return true;
                    }

                    @Override
                    public Couple<Frequency> getNetworkKey() {
                        return entry.getKey();
                    }

                    @Override
                    public BlockPos getLocation() {
                        return actor.getLocation();
                    }
                });
            }
        }
        if (actor.isListening())
            actor.setReceivedStrength(0);
    }

    private static String keyToString(Couple<Frequency> key) {
        return String.format("%s + %s",
                BuiltInRegistries.ITEM.getKey(key.getFirst().getStack().getItem()),
                BuiltInRegistries.ITEM.getKey(key.getSecond().getStack().getItem())
        );
    }

    private static boolean test(Couple<Frequency> transmitter, Couple<Frequency> receiver) {
        if (!CServer.AllowDualWildcardLink.get() && transmitter.getFirst().getStack().getItem() instanceof ILinkWildcard && transmitter.getSecond().getStack().getItem() instanceof ILinkWildcard)
            return false;
        if (!CServer.AllowDualWildcardLink.get() && receiver.getFirst().getStack().getItem() instanceof ILinkWildcard && receiver.getSecond().getStack().getItem() instanceof ILinkWildcard)
            return false;
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
