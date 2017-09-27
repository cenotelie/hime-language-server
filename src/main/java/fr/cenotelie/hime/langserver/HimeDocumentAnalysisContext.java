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

import fr.cenotelie.hime.redist.Text;
import org.xowl.infra.lsp.engine.SymbolFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a context for an analysis
 *
 * @author Laurent Wouters
 */
public class HimeDocumentAnalysisContext {
    /**
     * The text input that was parsed
     */
    public final Text input;
    /**
     * The factory for symbols
     */
    public final SymbolFactory factory;
    /**
     * The current analysis to fill
     */
    public final HimeDocumentAnalysis analysis;
    /**
     * The imported grammars
     */
    public final Collection<String> imported;
    /**
     * The known lexical contexts
     */
    public final Collection<String> lexicalContexts;
    /**
     * The known terminals
     */
    public final Collection<String> terminals;
    /**
     * The known variables
     */
    public final Collection<String> variables;

    /**
     * Initializes this context
     *
     * @param input    The text input that was parsed
     * @param factory  The factory for symbols
     * @param analysis The current analysis to fill
     */
    public HimeDocumentAnalysisContext(Text input, SymbolFactory factory, HimeDocumentAnalysis analysis) {
        this.input = input;
        this.factory = factory;
        this.analysis = analysis;
        this.imported = new ArrayList<>();
        this.lexicalContexts = new ArrayList<>();
        this.terminals = new ArrayList<>();
        this.variables = new ArrayList<>();
    }
}
