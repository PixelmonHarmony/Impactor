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

package net.impactdev.impactor.game.test.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.impactdev.impactor.api.commands.executors.CommandResult;
import net.impactdev.impactor.api.commands.executors.CommandExecutors;
import net.minecraft.commands.CommandSourceStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static net.minecraft.commands.Commands.literal;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommandTest {

    private static CommandDispatcher<CommandSourceStack> dispatcher;

    @BeforeAll
    public static void initialize() {
        dispatcher = new CommandDispatcher<>();
    }

    @Test
    public void exceptionTest() {
        dispatcher.register(this.failing());
        assertThrows(RuntimeException.class, () -> {
            dispatcher.execute("failing", null);
        });

        assertEquals(1, assertDoesNotThrow(() -> dispatcher.execute("failing working", null)));
        assertThrows(RuntimeException.class, () -> dispatcher.execute("failing working failAgain", null));
    }

    @Test
    public void statusCodes() {
        dispatcher.register(this.statuses());
        assertEquals('a', assertDoesNotThrow(() -> dispatcher.execute("statuses A", null)));
        assertEquals('b', assertDoesNotThrow(() -> dispatcher.execute("statuses B", null)));
    }

    private LiteralArgumentBuilder<CommandSourceStack> failing() {
        return literal("failing")
                .executes(CommandExecutors.allowAll(context -> CommandResult.exceptional(new RuntimeException("I fail purposefully"))))
                .then(literal("working").executes(CommandExecutors.allowAll(context -> CommandResult.successful()))
                        .then(literal("failAgain").executes(CommandExecutors.allowAll(context -> CommandResult.exceptional(new RuntimeException("I also fail purposefully!")))))
                );
    }

    private LiteralArgumentBuilder<CommandSourceStack> statuses() {
        return literal("statuses")
                .then(literal("A").executes(CommandExecutors.allowAll(context -> CommandResult.builder().result('a').build())))
                .then(literal("B").executes(context -> 'b'));
    }
}
