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

package net.impactdev.impactor.api.ui.containers.layouts;

import com.google.common.collect.ImmutableMap;
import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.ui.containers.icons.Icon;
import net.impactdev.impactor.api.builders.Builder;
import org.spongepowered.math.vector.Vector2i;

import java.util.Optional;
import java.util.function.Consumer;

public interface Layout {

	ImmutableMap<Integer, Icon> elements();

	Optional<Icon> icon(int slot);

	Vector2i dimensions();

	static LayoutBuilder builder() {
		return Impactor.instance().builders().provide(LayoutBuilder.class);
	}

	interface LayoutBuilder extends Builder<Layout> {

		/**
		 * Determines the amount of rows that will make up this chest UI. This is bounded by a lower limit
		 * of 1 and an upper limit of 6. Any values outside this range will provoke an {@link IllegalArgumentException}.
		 *
		 * If not used in the layout generation, the layout will default to a size of 6, resulting in a 54 slot
		 * inventory.
		 *
		 * @param rows The amount of rows of the inventory, between 1-6
		 * @return The updated builder
		 * @throws IllegalArgumentException If the given row count is outside the acceptable bounds
		 */
		LayoutBuilder size(int rows);

		/**
		 * Sets a singular slot in the layout to the icon given. Whether an icon will be successfully set
		 * is determined on the dimensions of the UI being interacted with. Any values outside this range
		 * will provoke an {@link IllegalArgumentException}.
		 *
		 * @param icon The icon to place in the slot
		 * @param slot The slot to insert the icon at
		 * @return The layout builder following the attempted update
		 * @throws IllegalArgumentException If the given slot is not within the bounds of the container
		 */
		LayoutBuilder slot(Icon icon, int slot);

		/**
		 * Sets the following icon to the set of slots given.
		 *
		 * Any slot values outside this range will provoke an {@link IllegalArgumentException}.
		 *
		 * @param icon
		 * @param slots
		 * @return
		 * @throws IllegalArgumentException If a given slot is not within the bounds of the container
		 */
		default LayoutBuilder slots(Icon icon, int... slots) {
			for(int slot : slots) {
				this.slot(icon, slot);
			}

			return this;
		}

		/**
		 * Attempts to fill any remaining slots not currently set with the following icon.
		 *
		 * @param icon The icon to fill the inventory with, if any slots are left available
		 * @return The layout builder following the attempted update
		 */
		LayoutBuilder fill(Icon icon);

		/**
		 * Draws the given icon across the border of the layout. In other words, this will cover the outermost
		 * slots of the chest UI. If you wish for an offset border style, consider
		 * {@link #rectangle(Icon, Vector2i, Vector2i, boolean)}.
		 *
		 * @param icon The icon to draw for the border
		 * @return The updated builder
		 */
		LayoutBuilder border(Icon icon);

		/**
		 * Draws the icon across the specified row for all slots in that row. This must agree with the
		 * dimensions of the layout given by size. Values outside the row boundary will trigger an
		 * {@link IllegalArgumentException}.
		 *
		 * @param icon The icon to draw across the row
		 * @param row The row to draw the icon on
		 * @return The updated builder
		 * @throws IllegalArgumentException If the given row is outside the acceptable bounds
		 */
		LayoutBuilder row(Icon icon, int row);

		/**
		 * Draws the icon across the specified rows for all slots in that row. This must agree with the
		 * dimensions of the layout given by size. Values outside the row boundary will trigger an
		 * {@link IllegalArgumentException}.
		 *
		 * @param icon The icon to draw across each row
		 * @param rows The rows to draw the icon on
		 * @return The updated builder
		 * @throws IllegalArgumentException If the given row is outside the acceptable bounds
		 */
		default LayoutBuilder rows(Icon icon, int... rows) {
			for(int row : rows) {
				this.row(icon, row);
			}

			return this;
		}

		/**
		 * Draws the icon across the specified column for all slots in that column. Values outside the column
		 * boundary will trigger an {@link IllegalArgumentException}. The acceptable column count for a chest
		 * is from 1-9.
		 *
		 * @param icon The icon to draw across the column
		 * @param column The column to draw the icon on
		 * @return The updated builder
		 * @throws IllegalArgumentException If the given column is outside the acceptable bounds
		 */
		LayoutBuilder column(Icon icon, int column);

		/**
		 * Draws the icon across the specified columns for all slots in that column. Values outside the column
		 * boundary will trigger an {@link IllegalArgumentException}. The acceptable column count for a chest
		 * is from 1-9.
		 *
		 * @param icon The icon to draw across the column
		 * @param columns The columns to draw the icon on
		 * @return The updated builder
		 * @throws IllegalArgumentException If a given column is outside the acceptable bounds
		 */
		default LayoutBuilder columns(Icon icon, int... columns) {
			for(int column : columns) {
				this.column(icon, column);
			}

			return this;
		}

		/**
		 * Draws an icon at the center of the container. In the case of a container with an even number of rows, this
		 * will draw in the two middle coordinates. Otherwise, a single coordinate will be the selected draw point.
		 *
		 * @param icon The icon to draw at the center of the container
		 * @return The updated builder
		 */
		LayoutBuilder center(Icon icon);

		/**
		 * Draws a square around the center point, with a radius of 2. This means the square is composed of the
		 * center point slot, as well as all neighboring slots in all directions. Additionally, if specified
		 * to be hollow, only the center piece will not be drawn with the rest of the square.
		 *
		 * @param icon The icon to draw for the square
		 * @param center The center point of the square
		 * @param hollow If the center point should be omitted
		 * @return The updated builder
		 */
		default LayoutBuilder square(Icon icon, int center, boolean hollow) {
			return this.square(icon, center, 2, hollow);
		}

		/**
		 * Draws a square around the center point. Additionally, if specified to be hollow, all
		 * pieces inside the border of the square will be omitted from being drawn.
		 *
		 * <p>To help understand radius values, a radius of 1 is equal to just painting
		 * the center point, whereas larger values begin forming an actual square.
		 *
		 * @param icon The icon to draw for the square
		 * @param center The center point of the square
		 * @param hollow If the center point should be omitted
		 * @return The updated builder
		 */
		LayoutBuilder square(Icon icon, int center, int radius, boolean hollow);

		/**
		 * Draws a rectangle in the UI using the following coordinates. Additionally, this shape
		 * can also be drawn with a hollow center, leaving just the border of the shape.
		 *
		 * @param icon The icon to draw for each position of the rectangle
		 * @param size The size of the rectangle, where x = columns and y = rows
		 * @param offset The offsets of the position of the rectangle in the layout
		 * @param hollow Indicates if the drawn shape should be hollow
		 * @return The updated builder
		 */
		LayoutBuilder rectangle(Icon icon, Vector2i size, Vector2i offset, boolean hollow);

		/**
		 * Allows for customized drawing patterns to be directly applied to the layout builder. For instance,
		 * you can directly use the builder to have it draw a triangle into the layout versus individual
		 * slot calls. The concept of this method is simply to allow for extension beyond the provided shape
		 * drawing providers.
		 *
		 * The builder is provided such that you can make direct calls to components of the builder itself,
		 * so individual slots could be set with {@link #slot(Icon, int)}, or you can even mess around with
		 * the given shapes for your custom method.
		 *
		 * @param consumer The consumer to enact against the builder, which can draw new slots to the layout
		 * @return The updated builder
		 */
		LayoutBuilder custom(Consumer<LayoutBuilder> consumer);

		LayoutBuilder from(Layout layout);

	}
}
