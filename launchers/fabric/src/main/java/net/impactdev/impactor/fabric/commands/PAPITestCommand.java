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

package net.impactdev.impactor.fabric.commands;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import net.impactdev.impactor.api.commands.CommandSource;
import net.impactdev.impactor.fabric.FabricImpactorBootstrap;
import net.impactdev.impactor.minecraft.api.text.AdventureTranslator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.processing.CommandContainer;

@CommandContainer
public final class PAPITestCommand {

    @Command("impactor papi test <placeholder>")
    public void test(CommandSource source, @Argument("placeholder") @Greedy String placeholder) {
        ServerPlayer minecraft = FabricImpactorBootstrap.instance().server()
                .map(server -> server.getPlayerList().getPlayer(source.source().uuid()))
                .orElse(null);

        if(minecraft == null) {
            return;
        }

        Component input = Component.literal(placeholder);
        Component result = Placeholders.parseText(input, PlaceholderContext.of(minecraft));
        source.sendMessage(AdventureTranslator.fromNative(result));
    }

}
