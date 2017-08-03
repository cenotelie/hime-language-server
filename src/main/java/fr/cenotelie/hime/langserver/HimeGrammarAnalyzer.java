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

import fr.cenotelie.hime.redist.ASTNode;
import fr.cenotelie.hime.redist.ParseResult;
import fr.cenotelie.hime.redist.Text;
import org.xowl.infra.lsp.engine.*;
import org.xowl.infra.lsp.structures.Diagnostic;
import org.xowl.infra.lsp.structures.DiagnosticSeverity;
import org.xowl.infra.lsp.structures.SymbolKind;
import org.xowl.infra.utils.IOUtils;
import org.xowl.infra.utils.TextUtils;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;

/**
 * The analyzer for a Hime grammar
 *
 * @author Laurent Wouters
 */
public class HimeGrammarAnalyzer extends DocumentAnalyzerHime {
    /**
     * Initializes this analyzer
     */
    public HimeGrammarAnalyzer() {
        super(HimeGrammarAnalyzer.class.getCanonicalName(), "Hime", "hime");
    }

    @Override
    protected ParseResult parse(Reader reader) {
        try {
            String content = IOUtils.read(reader);
            HimeGrammarLexer lexer = new HimeGrammarLexer(content);
            HimeGrammarParser parser = new HimeGrammarParser(lexer);
            parser.setModeRecoverErrors(false);
            return parser.parse();
        } catch (IOException ex) {
            return null;
        }
    }

    @Override
    protected DocumentSymbols findSymbols(String resourceUri, ASTNode root, Text input, SymbolFactory factory, Collection<Diagnostic> diagnostics) {
        DocumentSymbols symbols = new DocumentSymbols();
        HimeAnalysisContext context = new HimeAnalysisContext(resourceUri, input, factory, symbols, diagnostics);
        for (ASTNode child : root.getChildren())
            inspectGrammar(context, child);
        return symbols;
    }

    /**
     * Inspects a grammar node
     *
     * @param context The current context
     * @param node    The AST node
     */
    private void inspectGrammar(HimeAnalysisContext context, ASTNode node) {
        String name = node.getChildren().get(0).getValue();
        Symbol symbolGrammar = context.factory.resolve(name);
        symbolGrammar.setKind(SymbolKind.CLASS);
        context.symbols.addDefinition(new DocumentSymbolReference(
                symbolGrammar,
                getRangeFor(context.input, node.getChildren().get(0))
        ));
        for (ASTNode nodeParent : node.getChildren().get(1).getChildren()) {
            String parent = nodeParent.getValue();
            Symbol symbolParent = context.factory.resolve(parent);
            symbolParent.setKind(SymbolKind.CLASS);
            context.symbols.addReference(new DocumentSymbolReference(
                    symbolParent,
                    getRangeFor(context.input, nodeParent)
            ));
        }
        if (node.getChildren().size() == 5) {
            inspectTerminals(context, symbolGrammar, node.getChildren().get(3));
            inspectVariables(context, symbolGrammar, node.getChildren().get(4));
        } else {
            inspectVariables(context, symbolGrammar, node.getChildren().get(3));
        }
        inspectOptions(context, symbolGrammar, node.getChildren().get(2));
    }

    /**
     * Inspects a terminals node
     *
     * @param context The current context
     * @param grammar The current grammar
     * @param node    The AST node
     */
    private void inspectTerminals(HimeAnalysisContext context, Symbol grammar, ASTNode node) {
        for (ASTNode child : node.getChildren())
            inspectTerminal(context, grammar, child);
    }

    /**
     * Inspects a terminal node
     *
     * @param context The current context
     * @param grammar The current grammar
     * @param node    The AST node
     */
    private void inspectTerminal(HimeAnalysisContext context, Symbol grammar, ASTNode node) {

    }

    /**
     * Inspects a variables node
     *
     * @param context The current context
     * @param grammar The current grammar
     * @param node    The AST node
     */
    private void inspectVariables(HimeAnalysisContext context, Symbol grammar, ASTNode node) {

    }

    /**
     * Inspects an options node
     *
     * @param context The current context
     * @param grammar The current grammar
     * @param node    The AST node
     */
    private void inspectOptions(HimeAnalysisContext context, Symbol grammar, ASTNode node) {
        for (ASTNode couple : node.getChildren()) {
            String optionName = couple.getChildren().get(0).getValue();
            String optionValue = TextUtils.unescape(couple.getChildren().get(1).getValue());
            optionValue = optionValue.substring(1, optionValue.length() - 1);
            if ("Axiom".equals(optionName)) {
                if (!context.variables.contains(optionValue)) {
                    context.diagnostics.add(new Diagnostic(
                            getRangeFor(context.input, node.getChildren().get(1)),
                            DiagnosticSeverity.ERROR,
                            "hime.1",
                            name,
                            "Axiom '" + optionValue + "' is not a defined variable."
                    ));
                } else {
                    Symbol symbol = context.factory.resolve(name + "." + optionValue);
                    symbol.setKind(SymbolKind.PROPERTY);
                    symbol.setParent(grammar);
                    context.symbols.addReference(new DocumentSymbolReference(
                            symbol,
                            getRangeFor(context.input, node.getChildren().get(1))
                    ));
                }
            } else if ("Separator".equals(optionName)) {
                if (!context.terminals.contains(optionValue)) {
                    context.diagnostics.add(new Diagnostic(
                            getRangeFor(context.input, node.getChildren().get(1)),
                            DiagnosticSeverity.ERROR,
                            "hime.2",
                            name,
                            "Separator terminal '" + optionValue + "' is not a defined terminal."
                    ));
                } else {
                    Symbol symbol = context.factory.resolve(name + "." + optionValue);
                    symbol.setKind(SymbolKind.FIELD);
                    symbol.setParent(grammar);
                    context.symbols.addReference(new DocumentSymbolReference(
                            symbol,
                            getRangeFor(context.input, node.getChildren().get(1))
                    ));
                }
            }
        }
    }
}
