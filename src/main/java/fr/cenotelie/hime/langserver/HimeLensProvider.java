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
import org.xowl.infra.lsp.engine.DocumentLensProvider;
import org.xowl.infra.lsp.engine.SymbolRegistry;
import org.xowl.infra.lsp.structures.CodeLens;
import org.xowl.infra.lsp.structures.Command;
import org.xowl.infra.lsp.structures.SymbolInformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * The code lens provider for Hime grammars
 *
 * @author Laurent Wouters
 */
public class HimeLensProvider implements DocumentLensProvider {
    /**
     * The symbol registry
     */
    private final SymbolRegistry symbols;

    /**
     * Initializes this provider
     *
     * @param symbols The symbol registry
     */
    public HimeLensProvider(SymbolRegistry symbols) {
        this.symbols = symbols;
    }

    @Override
    public int getPriorityFor(Document document) {
        if (Objects.equals(document.getLanguageId(), HimeWorkspace.LANGUAGE))
            return PRIORITY_HIGH;
        return PRIORITY_NONE;
    }

    @Override
    public CodeLens[] getLens(Document document) {
        Collection<CodeLens> result = new ArrayList<>();
        for (SymbolInformation info : symbols.getDefinitionsIn(document.getUri())) {
            if (info.getKind() == HimeWorkspace.SYMBOL_GRAMMAR) {
                result.add(new CodeLens(info.getLocation().getRange(), new Command(
                        "Compile grammar " + info.getName(),
                        "compile",
                        new Object[]{
                                document.getUri(),
                                info.getName()
                        })));
            }
        }
        return result.toArray(new CodeLens[result.size()]);
    }

    @Override
    public CodeLens resolve(CodeLens codeLens) {
        return codeLens;
    }
}
