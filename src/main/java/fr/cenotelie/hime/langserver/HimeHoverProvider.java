/*******************************************************************************
 * Copyright (c) 2017 Association Cénotélie (cenotelie.fr)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General
 * Public License along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package fr.cenotelie.hime.langserver;

import fr.cenotelie.commons.lsp.engine.Document;
import fr.cenotelie.commons.lsp.engine.DocumentHoverProvider;
import fr.cenotelie.commons.lsp.engine.Symbol;
import fr.cenotelie.commons.lsp.engine.SymbolRegistry;
import fr.cenotelie.commons.lsp.structures.*;

import java.util.Objects;

/**
 * The hover provider for Hime grammars
 *
 * @author Laurent Wouters
 */
public class HimeHoverProvider implements DocumentHoverProvider {
    /**
     * The symbol registry
     */
    private final SymbolRegistry symbols;

    /**
     * Initializes this provider
     *
     * @param symbols The symbol registry
     */
    public HimeHoverProvider(SymbolRegistry symbols) {
        this.symbols = symbols;
    }

    @Override
    public int getPriorityFor(Document document) {
        if (Objects.equals(document.getLanguageId(), HimeWorkspace.LANGUAGE))
            return PRIORITY_HIGH;
        return PRIORITY_NONE;
    }

    @Override
    public Hover getHoverData(Document document, Position position) {
        Symbol symbol = symbols.getSymbolAt(document.getUri(), position);
        if (symbol == null)
            return null;
        Range range = symbol.getRangeAt(document.getUri(), position);
        switch (symbol.getKind()) {
            case HimeWorkspace.SYMBOL_GRAMMAR:
                return new Hover(new MarkupContent(MarkupKind.MARKDOWN, "Grammar `" + symbol.getName() + "`"), range);
            case HimeWorkspace.SYMBOL_CONTEXT:
                return new Hover(new MarkupContent(MarkupKind.MARKDOWN, "Lexical context `" + symbol.getName() + "`"), range);
            case HimeWorkspace.SYMBOL_TERMINAL:
                return new Hover(new MarkupContent(MarkupKind.MARKDOWN, "Terminal symbol `" + symbol.getName() + "`"), range);
            case HimeWorkspace.SYMBOL_VARIABLE:
                return new Hover(new MarkupContent(MarkupKind.MARKDOWN, "Variable symbol `" + symbol.getName() + "`"), range);
            case HimeWorkspace.SYMBOL_VIRTUAL:
                return new Hover(new MarkupContent(MarkupKind.MARKDOWN, "Virtual symbol `" + symbol.getName() + "`"), range);
            case HimeWorkspace.SYMBOL_ACTION:
                return new Hover(new MarkupContent(MarkupKind.MARKDOWN, "Grammar action `" + symbol.getName() + "`"), range);
            case HimeWorkspace.SYMBOL_PARAM:
                return new Hover(new MarkupContent(MarkupKind.MARKDOWN, "Variable parameter `" + symbol.getName() + "`"), range);
            default:
                return null;
        }
    }
}
