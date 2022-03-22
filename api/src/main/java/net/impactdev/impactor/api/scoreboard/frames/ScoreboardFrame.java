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

package net.impactdev.impactor.api.scoreboard.frames;


import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.scoreboard.components.ScoreboardComponent;
import net.impactdev.impactor.api.scoreboard.components.Updatable;
import net.impactdev.impactor.api.scoreboard.frames.types.ListeningFrame;
import net.impactdev.impactor.api.scoreboard.frames.types.ConstantFrame;
import net.impactdev.impactor.api.scoreboard.frames.types.RefreshingFrame;
import net.kyori.adventure.text.Component;

public interface ScoreboardFrame extends ScoreboardComponent<ScoreboardFrame> {

    Component getText();

    boolean shouldUpdateOnTick();

    static ConstantFrame.ConstantFrameBuilder constant() {
        return Impactor.getInstance().getRegistry().createBuilder(ConstantFrame.ConstantFrameBuilder.class);
    }

    static RefreshingFrame.RefreshingFrameBuilder refreshing() {
        return Impactor.getInstance().getRegistry().createBuilder(RefreshingFrame.RefreshingFrameBuilder.class);
    }

    static <L> ListeningFrame.ListeningFrameBuilder<L> listening(Class<L> type) {
        return Impactor.getInstance().getRegistry().createBuilder(ListeningFrame.ListeningFrameBuilder.class).type(type);
    }

    interface UpdatableFrame extends ScoreboardFrame {

        void initialize(Updatable parent);

        void shutdown();

    }

}
