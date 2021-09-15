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

package net.impactdev.impactor.sponge.scoreboard;

import io.leangen.geantyref.TypeToken;
import net.impactdev.impactor.api.placeholders.PlaceholderSources;
import net.impactdev.impactor.api.scoreboard.ImpactorScoreboard;
import net.impactdev.impactor.api.scoreboard.events.PlatformBus;
import net.impactdev.impactor.api.scoreboard.frames.ScoreboardFrame;
import net.impactdev.impactor.api.scoreboard.lines.ScoreboardLine;
import net.impactdev.impactor.api.scoreboard.objective.ScoreboardObjective;
import net.impactdev.impactor.sponge.SpongeImpactorPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;

import java.util.StringJoiner;
import java.util.UUID;

public class ScoreboardTest {

    public void create(ServerPlayer player) {
        PlaceholderSources sources = PlaceholderSources.builder().append(ServerPlayer.class, () -> player).build();

        ImpactorScoreboard scoreboard = ImpactorScoreboard.builder()
                .objective(ScoreboardObjective.listening()
                        .frame(ScoreboardFrame.listening(TypeToken.get(ServerSideConnectionEvent.class))
                                .text("&e&lImpactDev &7(&b{{impactor:player_count}}&7)")
                                .bus(PlatformBus.getOrCreate())
                                .handler((updatable, event) -> {
                                    if(event instanceof ServerSideConnectionEvent.Join ||
                                            event instanceof ServerSideConnectionEvent.Disconnect) {
                                        Sponge.server().scheduler().submit(Task.builder()
                                                .plugin(SpongeImpactorPlugin.getInstance().getPluginContainer())
                                                .execute(updatable::update)
                                                .delay(Ticks.of(1))
                                                .build()
                                        );
                                    }
                                })
                                .sources(sources)
                                .build()
                        )
                        .build()
                )
                .line(ScoreboardLine.refreshing()
                        .text("{{impactor:rainbow|" + this.rainbowArguments() + "}}")
                        .score(15)
                        .rate(2)
                        .sources(PlaceholderSources.empty())
                        .build()
                )
                .line(ScoreboardLine.constant().text(Component.text("Hello ").append(player.displayName().get().color(NamedTextColor.YELLOW))).score(14).build())
                .line(ScoreboardLine.listening()
                        .content(ScoreboardFrame.listening(TypeToken.get(MoveEntityEvent.class))
                                .text("&fCoordinates: &b{{impactor:coordinates}}")
                                .bus(PlatformBus.getOrCreate())
                                .handler((updatable, event) -> {
                                    if(event.entity().uniqueId().equals(player.uniqueId())) {
                                        updatable.update();
                                    }
                                })
                                .sources(sources)
                                .build()
                        )
                        .score(13)
                        .build()
                )
                .line(ScoreboardLine.constant().text(Component.empty()).score(12).build())
                .line(ScoreboardLine.refreshing()
                        .text("&fTPS: {{impactor:tps}}")
                        .rate(1)
                        .score(3)
                        .sources(PlaceholderSources.empty())
                        .build()
                )
                .line(ScoreboardLine.refreshing()
                        .text("&fMSpT: {{impactor:mspt}}")
                        .rate(1)
                        .score(2)
                        .sources(PlaceholderSources.empty())
                        .build()
                )
                .line(ScoreboardLine.refreshing()
                        .text("{{impactor:rainbow|" + this.rainbowArguments() + "}}")
                        .score(1)
                        .rate(2)
                        .sources(PlaceholderSources.empty())
                        .build()
                )
                .build();
        scoreboard.applyFor(player.uniqueId());
    }

    private String rainbowArguments() {
        UUID key = UUID.randomUUID();
        String out = StringUtils.repeat('\u25A0', 30);

        StringJoiner arguments = new StringJoiner(";");
        arguments.add("id=" + key);
        arguments.add("value=" + out);
        arguments.add("start=0");

        return arguments.toString();
    }

}
