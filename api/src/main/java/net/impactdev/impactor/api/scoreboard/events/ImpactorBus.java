/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2021 NickImpact
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

package net.impactdev.impactor.api.scoreboard.events;

import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.event.ImpactorEvent;
import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.frames.types.ListeningFrame;
import net.impactdev.impactor.api.scoreboard.lines.ScoreboardLine;

import java.util.Optional;
import java.util.function.BiFunction;

public class ImpactorBus implements Bus<ImpactorEvent> {

    private static ImpactorBus instance;

    public static ImpactorBus getOrCreate() {
        return Optional.ofNullable(instance).orElse(instance = new ImpactorBus());
    }

    @Override
    public String getID() {
        return "Impactor";
    }

    @Override
    public <E extends ImpactorEvent> BiFunction<Updatable, ListeningFrame.EventHandler<E>, RegisteredEvent> getRegisterHandler(TypeToken<E> type) {
        return (line, handler) -> new RegisteredEvent(Impactor.getInstance().getEventBus().subscribe(type, event -> handler.process(line, event)));
    }

    @Override
    public void shutdown(RegisteredEvent registration) {
        //Impactor.getInstance().getEventBus()
    }

}
