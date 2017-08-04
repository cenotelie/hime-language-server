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
import java.util.ArrayList;
import java.util.Collection;

/**
 * The analyzer for a Hime grammar
 *
 * @author Laurent Wouters
 */
public class HimeGrammarAnalyzer extends DocumentAnalyzerHime {
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
        symbolGrammar.setKind(SYMBOL_GRAMMAR);
        context.symbols.addDefinition(new DocumentSymbolReference(
                symbolGrammar,
                getRangeFor(context.input, node.getChildren().get(0))
        ));
        for (ASTNode nodeParent : node.getChildren().get(1).getChildren()) {
            String parent = nodeParent.getValue();
            Symbol symbolParent = context.factory.resolve(parent);
            symbolParent.setKind(SYMBOL_GRAMMAR);
            context.imported.add(parent);
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
        for (ASTNode child : node.getChildren()) {
            if (child.getSymbol().getID() == HimeGrammarParser.ID.terminal_context) {
                String name = node.getChildren().get(0).getValue();
                Symbol contextSymbol = context.factory.resolve(grammar.getIdentifier() + "." + name);
                contextSymbol.setKind(SYMBOL_CONTEXT);
                contextSymbol.setParent(grammar);
                context.symbols.addDefinition(new DocumentSymbolReference(
                        contextSymbol,
                        getRangeFor(context.input, node.getChildren().get(0))
                ));
                context.lexicalContexts.add(name);
                for (int i = 1; i != node.getChildren().size(); i++)
                    inspectTerminal(context, grammar, node.getChildren().get(i));
            } else {
                inspectTerminal(context, grammar, child);
            }
        }
    }

    /**
     * Inspects a terminal node
     *
     * @param context The current context
     * @param grammar The current grammar
     * @param node    The AST node
     */
    private void inspectTerminal(HimeAnalysisContext context, Symbol grammar, ASTNode node) {
        String name = node.getChildren().get(0).getValue();
        inspectTerminalDefinition(context, grammar, name, node.getChildren().get(1));
        Symbol terminal = context.factory.resolve(grammar.getIdentifier() + "." + name);
        terminal.setKind(SYMBOL_TERMINAL);
        terminal.setParent(grammar);
        context.symbols.addDefinition(new DocumentSymbolReference(
                terminal,
                getRangeFor(context.input, node.getChildren().get(0))
        ));
        context.terminals.add(name);
    }

    /**
     * Inspects a terminal definition node
     *
     * @param context  The current context
     * @param grammar  The current grammar
     * @param terminal The current terminal
     * @param node     The AST node
     */
    private void inspectTerminalDefinition(HimeAnalysisContext context, Symbol grammar, String terminal, ASTNode node) {
        if (node.getSymbol().getID() == HimeGrammarLexer.ID.NAME) {
            String name = node.getValue();
            if (terminal.equals(name)) {
                // self-reference
                context.diagnostics.add(new Diagnostic(
                        getRangeFor(context.input, node),
                        DiagnosticSeverity.ERROR,
                        "hime.3",
                        this.name,
                        "Terminal '" + terminal + "' is self-referential in its definition."
                ));
                return;
            }
            if (context.terminals.contains(name)) {
                // reference to an existing terminal
                Symbol referenced = context.factory.resolve(grammar.getIdentifier() + "." + name);
                context.symbols.addReference(new DocumentSymbolReference(
                        referenced,
                        getRangeFor(context.input, node)
                ));
                return;
            }
            // look for imports
            for (String imported : context.imported) {
                Symbol candidate = context.factory.lookup(imported + "." + name);
                if (candidate != null && candidate.getKind() == SYMBOL_TERMINAL) {
                    // found it
                    context.symbols.addReference(new DocumentSymbolReference(
                            candidate,
                            getRangeFor(context.input, node)
                    ));
                    return;
                }
            }
            // not found
            context.diagnostics.add(new Diagnostic(
                    getRangeFor(context.input, node),
                    DiagnosticSeverity.WARNING,
                    "hime.4",
                    this.name,
                    "Missing definition for referenced terminal '" + name + "'."
            ));
        } else {
            for (ASTNode child : node.getChildren()) {
                inspectTerminalDefinition(context, grammar, terminal, child);
            }
        }
    }

    /**
     * Inspects a variables node
     *
     * @param context The current context
     * @param grammar The current grammar
     * @param node    The AST node
     */
    private void inspectVariables(HimeAnalysisContext context, Symbol grammar, ASTNode node) {
        for (ASTNode child : node.getChildren())
            inspectVariable(context, grammar, child);
    }

    /**
     * Inspects a variable node
     *
     * @param context The current context
     * @param grammar The current grammar
     * @param node    The AST node
     */
    private void inspectVariable(HimeAnalysisContext context, Symbol grammar, ASTNode node) {
        String name = node.getChildren().get(0).getValue();
        Symbol symbolVariable = context.factory.resolve(grammar.getIdentifier() + "." + name);
        symbolVariable.setKind(SYMBOL_VARIABLE);
        symbolVariable.setParent(grammar);
        context.symbols.addDefinition(new DocumentSymbolReference(
                symbolVariable,
                getRangeFor(context.input, node.getChildren().get(0))
        ));
        context.variables.add(name);

        Collection<String> parameters = new ArrayList<>();
        if (node.getSymbol().getID() == HimeGrammarParser.ID.cf_rule_template) {
            for (ASTNode child : node.getChildren().get(1).getChildren()) {
                String paramName = child.getValue();
                parameters.add(paramName);
                Symbol symbolParameter = context.factory.resolve(symbolVariable.getIdentifier() + "." + paramName);
                symbolParameter.setKind(SYMBOL_PARAM);
                symbolParameter.setParent(symbolVariable);
                context.symbols.addDefinition(new DocumentSymbolReference(
                        symbolParameter,
                        getRangeFor(context.input, child)
                ));
            }
            inspectVariableDefinition(context, grammar, name, parameters, node.getChildren().get(2));
        } else {
            inspectVariableDefinition(context, grammar, name, parameters, node.getChildren().get(1));
        }
    }

    /**
     * Inspects a variable definition node
     *
     * @param context    The current context
     * @param grammar    The current grammar
     * @param variable   The current variable
     * @param parameters The current parameters
     * @param node       The AST node
     */
    private void inspectVariableDefinition(HimeAnalysisContext context, Symbol grammar, String variable, Collection<String> parameters, ASTNode node) {
        if (node.getSymbol().getID() == HimeGrammarParser.ID.rule_def_context) {
            String name = node.getChildren().get(0).getValue();
            if (!context.lexicalContexts.contains(name)) {
                context.diagnostics.add(new Diagnostic(
                        getRangeFor(context.input, node.getChildren().get(0)),
                        DiagnosticSeverity.WARNING,
                        "hime.5",
                        this.name,
                        "Missing definition for referenced lexical context '" + name + "'."
                ));
            }
            inspectVariableDefinition(context, grammar, variable, parameters, node.getChildren().get(1));
        } else if (node.getSymbol().getID() == HimeGrammarParser.ID.rule_sym_action) {
            String name = node.getChildren().get(0).getValue();
            Symbol symbol = context.factory.resolve(grammar.getIdentifier() + "." + name);
            symbol.setKind(SYMBOL_ACTION);
            symbol.setParent(grammar);
            context.symbols.addReference(new DocumentSymbolReference(
                    symbol,
                    getRangeFor(context.input, node.getChildren().get(0))
            ));
        } else if (node.getSymbol().getID() == HimeGrammarParser.ID.rule_sym_virtual) {
            String name = TextUtils.unescape(node.getChildren().get(0).getValue());
            name = name.substring(1, name.length() - 1);
            Symbol symbol = context.factory.resolve(grammar.getIdentifier() + "." + name);
            symbol.setKind(SYMBOL_VIRTUAL);
            symbol.setParent(grammar);
            context.symbols.addReference(new DocumentSymbolReference(
                    symbol,
                    getRangeFor(context.input, node.getChildren().get(0))
            ));
        } else if (node.getSymbol().getID() == HimeGrammarLexer.ID.NAME) {
            String name = node.getValue();
            // is it a parameter?
            if (parameters.contains(name)) {
                Symbol symbol = context.factory.resolve(grammar.getIdentifier() + "." + variable + "." + name);
                context.symbols.addReference(new DocumentSymbolReference(
                        symbol,
                        getRangeFor(context.input, node)
                ));
                return;
            }
            // is it a known terminal?
            if (context.terminals.contains(name)) {
                Symbol symbol = context.factory.resolve(grammar.getIdentifier() + "." + name);
                context.symbols.addReference(new DocumentSymbolReference(
                        symbol,
                        getRangeFor(context.input, node)
                ));
                return;
            }
            // is it a known variable?
            if (context.variables.contains(name)) {
                Symbol symbol = context.factory.resolve(grammar.getIdentifier() + "." + name);
                context.symbols.addReference(new DocumentSymbolReference(
                        symbol,
                        getRangeFor(context.input, node)
                ));
                return;
            }
            // look for imports
            for (String imported : context.imported) {
                Symbol candidate = context.factory.lookup(imported + "." + name);
                if (candidate != null) {
                    // found it
                    context.symbols.addReference(new DocumentSymbolReference(
                            candidate,
                            getRangeFor(context.input, node)
                    ));
                    return;
                }
            }
            // not found
            context.diagnostics.add(new Diagnostic(
                    getRangeFor(context.input, node),
                    DiagnosticSeverity.WARNING,
                    "hime.6",
                    this.name,
                    "Missing definition for referenced symbol '" + name + "'."
            ));
        } else {
            for (ASTNode child : node.getChildren()) {
                inspectVariableDefinition(context, grammar, variable, parameters, child);
            }
        }
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
                            DiagnosticSeverity.WARNING,
                            "hime.1",
                            name,
                            "Axiom '" + optionValue + "' is not a variable defined in this grammar."
                    ));
                } else {
                    Symbol symbol = context.factory.resolve(name + "." + optionValue);
                    symbol.setKind(SYMBOL_VARIABLE);
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
                            DiagnosticSeverity.WARNING,
                            "hime.2",
                            name,
                            "Separator terminal '" + optionValue + "' is not a terminal defined in this grammar."
                    ));
                } else {
                    Symbol symbol = context.factory.resolve(name + "." + optionValue);
                    symbol.setKind(SYMBOL_TERMINAL);
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
