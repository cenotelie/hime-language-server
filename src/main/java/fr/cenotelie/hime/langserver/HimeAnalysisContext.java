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
import org.xowl.infra.lsp.engine.DocumentSymbols;
import org.xowl.infra.lsp.engine.SymbolFactory;
import org.xowl.infra.lsp.structures.Diagnostic;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a context for an analysis
 *
 * @author Laurent Wouters
 */
public class HimeAnalysisContext {
    /**
     * The text input that was parsed
     */
    public final Text input;
    /**
     * The factory for symbols
     */
    public final SymbolFactory factory;
    /**
     * The symbols for the current document
     */
    public final DocumentSymbols symbols;
    /**
     * The buffer for diagnostics
     */
    public final Collection<Diagnostic> diagnostics;
    /**
     * The URI of the resource currently being loaded
     */
    public final String resource;
    /**
     * The base URI for relative URIs
     */
    public String baseURI;
    /**
     * Map of the current namespaces
     */
    public final Map<String, String> namespaces;

    /**
     * Initializes this context
     *
     * @param resourceUri The URI of the resource
     * @param input       The text input that was parsed
     * @param factory     The factory for symbols
     * @param symbols     The symbols for the current document
     * @param diagnostics The buffer for diagnostics
     */
    public HimeAnalysisContext(String resourceUri, Text input, SymbolFactory factory, DocumentSymbols symbols, Collection<Diagnostic> diagnostics) {
        this.input = input;
        this.factory = factory;
        this.symbols = symbols;
        this.diagnostics = diagnostics;
        this.resource = resourceUri;
        this.baseURI = resourceUri;
        this.namespaces = new HashMap<>();
    }
}
