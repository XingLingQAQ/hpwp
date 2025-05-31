/*
 * Project hpwp, 2021-07-13T19:01-0400
 *
 * Copyright 2021 Justin Heflin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.heretere.hpwp.util;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.hanging.HangingEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.vehicle.VehicleEvent;
import org.bukkit.event.weather.WeatherEvent;
import org.bukkit.event.world.WorldEvent;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Method;
import java.util.Optional;

@UtilityClass
public final class WorldUtil {

    private static Method inventoryView_getPlayerMethod;
    private static Method humanEntity_getWorldMethod;
    private static boolean reflectionInitialized = false;

    static {
        try {
            Class<?> inventoryViewClass = Class.forName("org.bukkit.inventory.InventoryView");
            inventoryView_getPlayerMethod = inventoryViewClass.getMethod("getPlayer");

            Class<?> humanEntityClass = Class.forName("org.bukkit.entity.HumanEntity");
            humanEntity_getWorldMethod = humanEntityClass.getMethod("getWorld");

            reflectionInitialized = true;
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {}
    }

    public static Optional<World> getWorldFromEvent(final Event event) {
        World world = null;
        if (event instanceof BlockEvent) {
            final BlockEvent blockEvent = (BlockEvent) event;
            world = blockEvent.getBlock().getWorld();
        } else if (event instanceof PlayerEvent) {
            final PlayerEvent playerEvent = (PlayerEvent) event;
            world = playerEvent.getPlayer().getWorld();
        } else if (event instanceof InventoryEvent) {
            final InventoryEvent inventoryEvent = (InventoryEvent) event;
            if (reflectionInitialized) {
                try {
                    Object inventoryViewObject = inventoryEvent.getView();
                    Object playerObject = inventoryView_getPlayerMethod.invoke(inventoryViewObject);

                    if (playerObject != null) {
                        world = (World) humanEntity_getWorldMethod.invoke(playerObject);
                    }
                } catch (Exception ignored) {
                }
            }
        } else if (event instanceof EntityEvent) {
            final EntityEvent entityEvent = (EntityEvent) event;
            world = entityEvent.getEntity().getWorld();
        } else if (event instanceof HangingEvent) {
            final HangingEvent hangingEvent = (HangingEvent) event;
            world = hangingEvent.getEntity().getWorld();
        } else if (event instanceof VehicleEvent) {
            final VehicleEvent vehicleEvent = (VehicleEvent) event;
            world = vehicleEvent.getVehicle().getWorld();
        } else if (event instanceof WeatherEvent) {
            final WeatherEvent weatherEvent = (WeatherEvent) event;
            world = weatherEvent.getWorld();
        } else if (event instanceof WorldEvent) {
            final WorldEvent worldEvent = (WorldEvent) event;
            world = worldEvent.getWorld();
        }

        return Optional.ofNullable(world);
    }
}