/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2022 NickImpact
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package net.impactdev.impactor.api.platform.players;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.adventure.LocalizedAudience;
import net.kyori.adventure.text.Component;

import java.util.UUID;

public interface PlatformSource extends LocalizedAudience {

    static PlatformSource console() {
        return Impactor.instance().factories().provide(Factory.class).console();
    }

    /**
     * Indicates the UUID of the source this platform instance belongs to. This field will always
     * be available, despite whether it correctly maps to a player or not.
     *
     * @return The UUID of the source
     */
    UUID uuid();

    /**
     * Represents the name of the source. This is meant to target the source's specific name, rather
     * than their own display name.
     *
     * @return A component representing the actual name of a source
     */
    Component name();

    interface Factory {

        /**
         * Creates a new {@link PlatformSource} that represents the game console. This source can be used
         * to send messages directly to the console or additionally for other means.
         *
         * @return A platform source representing the game console
         */
        PlatformSource console();

    }

}
