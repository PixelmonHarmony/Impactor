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


import com.google.common.collect.Sets;
import net.impactdev.impactor.api.dependencies.Dependency;
import net.impactdev.impactor.api.dependencies.DependencyManager;
import net.impactdev.impactor.api.dependencies.ProvidedDependencies;
import net.impactdev.impactor.api.dependencies.classloader.IsolatedClassLoader;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handles class runtime relocation of packages in downloaded dependencies.
 *
 * Imported from LuckPerms
 */
public class RelocationHandler {

	public static final Set<Dependency> DEPENDENCIES = Sets.newHashSet(
			ProvidedDependencies.ASM,
			ProvidedDependencies.ASM_COMMONS,
			ProvidedDependencies.JAR_RELOCATOR
	);

	private static final String JAR_RELOCATOR_CLASS = "me.lucko.jarrelocator.JarRelocator";
	private static final String JAR_RELOCATOR_RUN_METHOD = "run";

	private final Constructor<?> jarRelocatorConstructor;
	private final Method jarRelocatorRunMethod;

	public RelocationHandler(DependencyManager dependencyManager) {
		try {
			// download the required dependencies for remapping
			dependencyManager.loadDependencies(DEPENDENCIES);
			// get a classloader containing the required dependencies as sources
			IsolatedClassLoader classLoader = dependencyManager.obtainClassLoaderWith(DEPENDENCIES);

			// load the relocator class
			Class<?> jarRelocatorClass = classLoader.loadClass(JAR_RELOCATOR_CLASS);

			// prepare the the reflected constructor & method instances
			this.jarRelocatorConstructor = jarRelocatorClass.getDeclaredConstructor(File.class, File.class, Map.class);
			this.jarRelocatorConstructor.setAccessible(true);

			this.jarRelocatorRunMethod = jarRelocatorClass.getDeclaredMethod(JAR_RELOCATOR_RUN_METHOD);
			this.jarRelocatorRunMethod.setAccessible(true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void remap(Path input, Path output, List<Relocation> relocations) throws Exception {
		Map<String, String> mappings = new HashMap<>();
		for (Relocation relocation : relocations) {
			mappings.put(relocation.getPattern(), relocation.getRelocatedPattern());
		}

		// create and invoke a new relocator
		Object relocator = this.jarRelocatorConstructor.newInstance(input.toFile(), output.toFile(), mappings);
		this.jarRelocatorRunMethod.invoke(relocator);
	}
}