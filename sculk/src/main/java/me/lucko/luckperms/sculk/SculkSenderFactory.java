/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.luckperms.sculk;

import io.github.sculkpowered.server.command.CommandSource;
import io.github.sculkpowered.server.entity.player.Player;
import me.lucko.luckperms.common.locale.TranslationManager;
import me.lucko.luckperms.common.sender.Sender;
import me.lucko.luckperms.common.sender.SenderFactory;
import me.lucko.luckperms.sculk.service.CompatibilityUtil;
import net.kyori.adventure.permission.PermissionChecker;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import net.luckperms.api.util.Tristate;

import java.util.Locale;
import java.util.UUID;

public class SculkSenderFactory extends SenderFactory<LPSculkPlugin, CommandSource> {
    public SculkSenderFactory(LPSculkPlugin plugin) {
        super(plugin);
    }

    @Override
    protected String getName(CommandSource source) {
        if (source instanceof Player) {
            return ((Player) source).name();
        }
        return Sender.CONSOLE_NAME;
    }

    @Override
    protected UUID getUniqueId(CommandSource source) {
        if (source instanceof Player) {
            return ((Player) source).uniqueId();
        }
        return Sender.CONSOLE_UUID;
    }

    @Override
    protected void sendMessage(CommandSource source, Component message) {
        Locale locale = null;
        if (source instanceof Player) {
            locale = ((Player) source).settings().locale();
        }
        Component rendered = TranslationManager.render(message, locale);
        source.sendMessage(rendered);
    }

    @Override
    protected Tristate getPermissionValue(CommandSource source, String node) {
        return CompatibilityUtil.convertTristate(source.get(PermissionChecker.POINTER)
                .orElse(PermissionChecker.always(TriState.NOT_SET)).value(node));
    }

    @Override
    protected boolean hasPermission(CommandSource source, String node) {
        return source.hasPermission(node);
    }

    @Override
    protected void performCommand(CommandSource source, String command) {
        getPlugin().getBootstrap().server().commandHandler().execute(source, command);
    }

    @Override
    protected boolean isConsole(CommandSource sender) {
        return sender.equals(getPlugin().getBootstrap().server().consoleCommandSource());
    }
}
