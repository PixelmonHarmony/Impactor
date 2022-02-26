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

package net.impactdev.impactor.api.dependencies.relocation;

import java.util.Objects;

/**
 * Represents a relocation pattern that should be applied to a package scheme within a jar.
 *
 * Imported from LuckPerms
 */
public final class Relocation {
	public static final String RELOCATION_PREFIX = "net.impactdev.impactor.relocations.";

	public static Relocation of(String pattern, String replacement) {
		return new Relocation(pattern.replace("{}", "."), RELOCATION_PREFIX + replacement);
	}

	private final String pattern;
	private final String relocatedPattern;

	private Relocation(String pattern, String relocatedPattern) {
		this.pattern = pattern;
		this.relocatedPattern = relocatedPattern;
	}

	public String getPattern() {
		return this.pattern;
	}

	public String getRelocatedPattern() {
		return this.relocatedPattern;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Relocation that = (Relocation) o;
		return Objects.equals(this.pattern, that.pattern) &&
				Objects.equals(this.relocatedPattern, that.relocatedPattern);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.pattern, this.relocatedPattern);
	}
}
