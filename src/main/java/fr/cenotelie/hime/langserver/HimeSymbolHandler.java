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

import org.xowl.infra.lsp.engine.Document;
import org.xowl.infra.lsp.engine.DocumentSymbolHandler;
import org.xowl.infra.lsp.engine.Symbol;
import org.xowl.infra.lsp.engine.SymbolRegistry;
import org.xowl.infra.lsp.structures.Range;
import org.xowl.infra.lsp.structures.TextEdit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * The symbol handler for grammar symbols
 *
 * @author Laurent Wouters
 */
public class HimeSymbolHandler implements DocumentSymbolHandler {
    @Override
    public int getPriorityFor(Document document) {
        if (Objects.equals(document.getLanguageId(), HimeWorkspace.LANGUAGE))
            return PRIORITY_HIGH;
        return PRIORITY_NONE;
    }

    @Override
    public boolean isLegalName(Document document, SymbolRegistry symbols, Symbol symbol, String newName) {
        switch (symbol.getKind()) {
            case HimeWorkspace.SYMBOL_TERMINAL:
            case HimeWorkspace.SYMBOL_VARIABLE:
            case HimeWorkspace.SYMBOL_VIRTUAL:
            case HimeWorkspace.SYMBOL_ACTION:
            case HimeWorkspace.SYMBOL_PARAM:
            case HimeWorkspace.SYMBOL_GRAMMAR:
            case HimeWorkspace.SYMBOL_CONTEXT:
                return newName.matches("[_a-zA-Z][_a-zA-Z0-9]*");
        }
        return false;
    }

    @Override
    public TextEdit[] rename(Document document, Symbol symbol, String newName) {
        Collection<TextEdit> result = new ArrayList<>();
        Collection<Range> ranges = symbol.getDefinitionsIn(document.getUri());
        if (ranges != null) {
            for (Range range : ranges) {
                result.add(new TextEdit(range, newName));
            }
        }
        ranges = symbol.getReferencesIn(document.getUri());
        if (ranges != null) {
            for (Range range : ranges) {
                result.add(new TextEdit(range, newName));
            }
        }
        return result.toArray(new TextEdit[result.size()]);
    }
}
