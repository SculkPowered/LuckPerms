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

package me.lucko.luckperms.sculk.context;

import com.github.benmanes.caffeine.cache.LoadingCache;
import io.github.sculkpowered.server.entity.player.Player;
import me.lucko.luckperms.common.context.manager.ContextManager;
import me.lucko.luckperms.common.context.manager.QueryOptionsCache;
import me.lucko.luckperms.common.context.manager.QueryOptionsSupplier;
import me.lucko.luckperms.common.util.CaffeineFactory;
import me.lucko.luckperms.sculk.LPSculkPlugin;
import net.luckperms.api.context.ImmutableContextSet;
import net.luckperms.api.query.QueryOptions;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SculkContextManager extends ContextManager<Player, Player> {

    private final LoadingCache<Player, QueryOptionsCache<Player>> subjectCaches = CaffeineFactory.newBuilder()
            .expireAfterAccess(1, TimeUnit.MINUTES)
            .build(key -> new QueryOptionsCache<>(key, this));

    public SculkContextManager(LPSculkPlugin plugin) {
        super(plugin, Player.class, Player.class);
    }

    @Override
    public UUID getUniqueId(Player player) {
        return player.uniqueId();
    }

    @Override
    public QueryOptionsSupplier getCacheFor(Player subject) {
        if (subject == null) {
            throw new NullPointerException("subject");
        }

        return this.subjectCaches.get(subject);
    }

    @Override
    protected void invalidateCache(Player subject) {
        QueryOptionsCache<Player> cache = this.subjectCaches.getIfPresent(subject);
        if (cache != null) {
            cache.invalidate();
        }
    }

    @Override
    public QueryOptions formQueryOptions(Player subject, ImmutableContextSet contextSet) {
        return formQueryOptions(contextSet);
    }
}
