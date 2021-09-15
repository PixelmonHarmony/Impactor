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

package net.impactdev.impactor.sponge.scoreboard.lines.types;

import com.google.common.base.Preconditions;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.scoreboard.lines.types.ConstantLine;
import net.impactdev.impactor.api.services.text.MessageService;
import net.impactdev.impactor.sponge.scoreboard.lines.AbstractSpongeSBLine;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.objective.Objective;

public class SpongeConstantLine extends AbstractSpongeSBLine implements ConstantLine {

    private final Component text;

    protected SpongeConstantLine(SpongeConstantLineBuilder builder) {
        super(builder.score);
        this.text = builder.text;
    }

    public void setup(Scoreboard scoreboard, Objective objective, ServerPlayer viewer) {
        objective.findOrCreateScore(this.text).setScore(this.score);
    }

    @Override
    public Component getText() {
        return this.text;
    }

    @Override
    public int getScore() {
        return this.score;
    }

    public static SpongeConstantLineBuilder builder() {
        return new SpongeConstantLineBuilder();
    }

    public static class SpongeConstantLineBuilder implements ConstantLineBuilder {

        private Component text;
        private int score;

        @Override
        public ConstantLineBuilder text(String raw, PlaceholderSources sources) {
            MessageService<Component> service = Impactor.getInstance().getRegistry().get(MessageService.class);
            this.text = service.parse(raw, sources);
            return this;
        }

        @Override
        public ConstantLineBuilder text(Component text) {
            this.text = text;
            return this;
        }

        @Override
        public ConstantLineBuilder score(int score) {
            this.score = score;
            return this;
        }

        @Override
        public ConstantLineBuilder from(ConstantLine input) {
            Preconditions.checkArgument(input instanceof SpongeConstantLine);
            this.text = input.getText();
            this.score = input.getScore();

            return this;
        }

        @Override
        public ConstantLine build() {
            return new SpongeConstantLine(this);
        }

    }

}
