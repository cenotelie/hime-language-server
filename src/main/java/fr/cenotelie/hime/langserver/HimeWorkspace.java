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

import org.xowl.infra.lsp.engine.*;
import org.xowl.infra.lsp.structures.ServerCapabilities;
import org.xowl.infra.lsp.structures.SymbolKind;

import java.io.File;

/**
 * The workspace for the Hime language server
 *
 * @author Laurent Wouters
 */
public class HimeWorkspace extends Workspace {
    /**
     * The language identifier for Hime grammars
     */
    public static final String LANGUAGE = "hime";
    /**
     * The type of symbol for a grammar
     */
    public static final int SYMBOL_GRAMMAR = SymbolKind.CLASS;
    /**
     * The type of symbol for a lexical context
     */
    public static final int SYMBOL_CONTEXT = SymbolKind.CONSTANT;
    /**
     * The type of symbol for a terminal
     */
    public static final int SYMBOL_TERMINAL = SymbolKind.FIELD;
    /**
     * The type of symbol for a variable
     */
    public static final int SYMBOL_VARIABLE = SymbolKind.METHOD;
    /**
     * The type of symbol for a virtual
     */
    public static final int SYMBOL_VIRTUAL = SymbolKind.PROPERTY;
    /**
     * The type of symbol for an action
     */
    public static final int SYMBOL_ACTION = SymbolKind.FUNCTION;
    /**
     * The type of symbol for a template rule parameter
     */
    public static final int SYMBOL_PARAM = SymbolKind.VARIABLE;

    /**
     * The analyzer for Hime grammars
     */
    private final HimeGrammarAnalyzer analyzer;
    /**
     * The hover provider for Hime grammars
     */
    private final HimeHoverProvider hoverProvider;
    /**
     * The code lens provider for Hime grammars
     */
    private final HimeLensProvider lensProvider;

    /**
     * Initializes this workspace
     */
    public HimeWorkspace() {
        super();
        this.analyzer = new HimeGrammarAnalyzer();
        this.hoverProvider = new HimeHoverProvider(this.symbolRegistry);
        this.lensProvider = new HimeLensProvider(this.symbolRegistry);
    }

    @Override
    protected boolean isWorkspaceIncluded(File file) {
        String name = file.getName();
        return (name.endsWith(".gram"));
    }

    @Override
    protected String getLanguageFor(File file) {
        String name = file.getName();
        if (name.endsWith(".gram"))
            return LANGUAGE;
        return "text";
    }

    @Override
    protected void listServerCapabilities(ServerCapabilities capabilities) {
        capabilities.addCapability("referencesProvider");
        capabilities.addCapability("documentSymbolProvider");
        capabilities.addCapability("workspaceSymbolProvider");
        capabilities.addCapability("definitionProvider");
        capabilities.addCapability("documentHighlightProvider");
        capabilities.addCapability("hoverProvider");
        capabilities.addOption("codeLensProvider.resolveProvider", false);
    }

    @Override
    protected DocumentAnalyzer getServiceAnalyzer(Document document) {
        return analyzer;
    }

    @Override
    protected DocumentHoverProvider getServiceHoverProvider(Document document) {
        return hoverProvider;
    }

    protected DocumentLensProvider getServiceLensProvider(Document document) {
        return lensProvider;
    }
}
