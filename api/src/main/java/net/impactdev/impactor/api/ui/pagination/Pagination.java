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

package net.impactdev.impactor.api.ui.pagination;

import net.impactdev.impactor.api.Impactor;
import net.impactdev.impactor.api.ui.components.Dimensions;
import net.impactdev.impactor.api.ui.components.UIComponent;
import net.impactdev.impactor.api.ui.icons.Icon;
import net.impactdev.impactor.api.ui.layouts.Layout;
import net.impactdev.impactor.api.ui.pagination.updaters.PageUpdater;
import net.impactdev.impactor.api.ui.pagination.updaters.PageUpdaterType;
import net.impactdev.impactor.api.utilities.Builder;
import net.impactdev.impactor.api.utilities.lists.CircularLinkedList;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.util.TriState;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a UI which features a set of pages of contents loaded dynamically based on the viewer's
 * perspective.
 *
 * By nature of a pagination, each pagination should be considered a unique instance of itself. In other
 * words, it can only be bound to one player at a time.
 */
public interface Pagination {

    Key namespace();

    boolean open();

    boolean close();

    Layout layout();

    void page(int target);

    void set(@Nullable Icon<?> icon, int slot);

    CircularLinkedList<Page<?>> pages();

    default int calculateTargetSlot(int target, Dimensions zone, Dimensions offsets) {
        int x = target % zone.columns() + offsets.columns();
        int y = target / zone.columns() + offsets.rows();

        return x + (9 * y);
    }

    static <P> PaginationBuilder<P> builder(Class<P> typing) {
        return (PaginationBuilder<P>) Impactor.getInstance().getRegistry().createBuilder(PaginationBuilder.class);
    }

    interface PaginationBuilder<P> extends UIComponent<PaginationBuilder<P>>, Builder<Pagination, PaginationBuilder<P>> {

        /**
         * Sets the key referencing the provider of this pagination. This key provides both a namespace
         * and a value to identity the type of pagination, as well as a means of reference to a particular
         * pagination in the event an error occurs during its processing.
         *
         * @param key The key containing the namespace and value information of a provider.
         * @return The updated builder
         */
        @Required
        PaginationBuilder<P> provider(Key key);

        /**
         * Sets the viewer of the pagination. This will control who is capable of controlling and viewing
         * the actual pagination.
         *
         * @param viewer The player that will view this pagination
         * @return The updated builder
         */
        @Required
        PaginationBuilder<P> viewer(P viewer);

        /**
         * Sets the contents of the pagination to the following icons. If the list of icons is more
         * than can be carried in a singular page, the following icons will be associated with further
         * pages until no more pages become necessary.
         *
         * @param icons The icons to associate with the pagination
         * @return The updated builder
         */
        PaginationBuilder<P> contents(List<Icon<?>> icons);

        /**
         * Indicates the section that a page will draw its contents in. If this section overlaps with the layout,
         * the content zone will override the affected slots of the layout. This will draw the content zone in the
         * top left corner of the interface. If you wish to move this section around, consider using
         * {@link #zone(Dimensions, Dimensions)} instead.
         *
         * If the given dimensions cannot fit within the viewable interface, an {@link IllegalArgumentException}
         * will be invoked to identify the issue.
         *
         * @param dimensions The dimensions for the location where icons should be drawn in the pagination view.
         * @return The updated builder
         */
        default PaginationBuilder<P> zone(Dimensions dimensions) {
            return this.zone(dimensions, Dimensions.ZERO);
        }

        /**
         * Indicates the section that a page will draw its contents in. If this section overlaps with the layout,
         * the content zone will override the affected slots of the layout.
         *
         * If the given dimensions cannot fit within the viewable interface, an {@link IllegalArgumentException}
         * will be invoked to identify the issue.
         *
         * @param dimensions The dimensions for the location where icons should be drawn in the pagination view.
         * @param offset An offset that adjusts the placement of the content zone
         * @return The updated builder
         */
        PaginationBuilder<P> zone(Dimensions dimensions, @Nullable Dimensions offset);

        /**
         * Supplies an icon that controls interactions among pages within the pagination. These buttons act
         * as the second layer of the UI, and can only be overridden by the content zone.
         *
         * <p>The only button that will have no actionable aspects is via {@link PageUpdaterType#CURRENT}, which
         * is simply meant to display the current page number.
         *
         * <p>To allow for dynamic parsing of components, these icons are compatible with {@link MiniMessage} parsing
         * mechanics, so you can control the actual language output of each option. Additionally, a placeholder with
         * the following tag &lt;target-page> will be available for all parsable components so your text can be
         * dynamically styled and complete with parsed placeholders.
         *
         * @param updater The updater that will be used for the inventory interaction
         * @return The updated builder
         */
        PaginationBuilder<P> updater(PageUpdater updater);

        /**
         * Indicates whether any {@link PageUpdater PageUpdaters} with typings of {@link PageUpdaterType#PREVIOUS}
         * or {@link PageUpdaterType#NEXT} are allowed to recycle to the appropriate boundary of pages should
         * the current page already be on the boundary. For instance, on page 1, previous could reroute to the
         * actual max page of the pagination rather than do nothing. Additionally, this will allow for possibly
         * even hiding the icons if they would serve no use. So on page 1, with a setting of {@link TriState#FALSE},
         * the updater for {@link PageUpdaterType#PREVIOUS} would just be hidden. If not set, this will default
         * to {@link TriState#NOT_SET}.
         *
         * <br><br>Below you can see the outcomes of each state:
         * <pre>
         +----------------------------------------------+
         | State                | Result               	|
         | ---------------------|----------------------	|
         | {@link TriState#TRUE}    	| Shown and Cycle      	|
         | {@link TriState#NOT_SET} 	| Shown but do nothing 	|
         | {@link TriState#FALSE}   	| Hidden               	|
         +----------------------------------------------+
         * </pre>
         *
         * @param state The state to apply to updaters and how they appear and act within the Pagination
         * @return The updated builder
         *
         */
        PaginationBuilder<P> updaterStyle(TriState state);

    }

}
