package dev.albercl.conquestmod.common.permissions;

import java.lang.reflect.Method;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class PolymorphPermissions {
    public static final String DISGUISE_PERMISSION_NODE = "polymorph.command.disguise";

    private static final int DEFAULT_PERMISSION_LEVEL = 2;
    private static final Method FABRIC_PERMISSIONS_CHECK = resolveFabricPermissionsCheck();

    private PolymorphPermissions() {
    }

    public static boolean canUseDisguise(ServerPlayer player) {
        Method checkMethod = FABRIC_PERMISSIONS_CHECK;

        if (checkMethod != null) {
            try {
                Object result = checkMethod.invoke(null, player, DISGUISE_PERMISSION_NODE, DEFAULT_PERMISSION_LEVEL);
                if (result instanceof Boolean allowed) {
                    return allowed;
                }
            } catch (ReflectiveOperationException ignored) {
                // Fall through to the vanilla operator fallback.
            }
        }

        // Try LuckPerms API reflectively if present
        try {
            Class<?> providerClass = Class.forName("net.luckperms.api.LuckPermsProvider");
            Method getMethod = providerClass.getMethod("get");
            Object luckPerms = getMethod.invoke(null);

            if (luckPerms != null) {
                Method getUserManager = luckPerms.getClass().getMethod("getUserManager");
                Object userManager = getUserManager.invoke(luckPerms);

                if (userManager != null) {
                    Method getUser = null;
                    try {
                        getUser = userManager.getClass().getMethod("getUser", java.util.UUID.class);
                    } catch (NoSuchMethodException ex) {
                        // some API versions may expose an async method returning CompletableFuture
                        try {
                            getUser = userManager.getClass().getMethod("getUser", java.util.UUID.class);
                        } catch (NoSuchMethodException ignored) {
                            getUser = null;
                        }
                    }

                    if (getUser != null) {
                        Object possibleUser = getUser.invoke(userManager, player.getUUID());

                        // If the API returned a CompletableFuture, block to get the user
                        if (possibleUser instanceof java.util.concurrent.CompletableFuture<?> future) {
                            try {
                                possibleUser = future.get();
                            } catch (Exception ignored) {
                                possibleUser = null;
                            }
                        }

                        if (possibleUser != null) {
                            try {
                                Method getCached = possibleUser.getClass().getMethod("getCachedData");
                                Object cached = getCached.invoke(possibleUser);

                                if (cached != null) {
                                    // Try getPermissionData() no-arg
                                    Method getPermissionData = null;
                                    try {
                                        getPermissionData = cached.getClass().getMethod("getPermissionData");
                                    } catch (NoSuchMethodException ex) {
                                        // ignore
                                    }

                                    if (getPermissionData != null) {
                                        Object permData = getPermissionData.invoke(cached);
                                        if (permData != null) {
                                            // Try checkPermission(String) -> result object with asBoolean()
                                            try {
                                                Method checkPermission = permData.getClass().getMethod("checkPermission", String.class);
                                                Object result = checkPermission.invoke(permData, DISGUISE_PERMISSION_NODE);
                                                if (result != null) {
                                                    // some versions return a Tristate-like object with asBoolean()
                                                    try {
                                                        Method asBoolean = result.getClass().getMethod("asBoolean");
                                                        Object bool = asBoolean.invoke(result);
                                                        if (bool instanceof Boolean b) return b;
                                                    } catch (NoSuchMethodException ex) {
                                                        if (result instanceof Boolean b) return b;
                                                    }
                                                }
                                            } catch (NoSuchMethodException ex) {
                                                // try direct hasPermission on cached/user
                                            }
                                        }
                                    }
                                }
                            } catch (ReflectiveOperationException ignored) {
                                // fall through to fallback
                            }
                        }
                    }
                }
            }
        } catch (ReflectiveOperationException ignored) {
            // LuckPerms not present or unexpected API shape
        }

        return player.hasPermissions(DEFAULT_PERMISSION_LEVEL);
    }

    public static Component denyMessage() {
        return Component.literal("You do not have permission to use /disguise.");
    }

    private static Method resolveFabricPermissionsCheck() {
        try {
            Class<?> permissionsClass = Class.forName("me.lucko.fabric.api.permissions.v0.Permissions");
            return permissionsClass.getMethod("check", ServerPlayer.class, String.class, int.class);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }
}