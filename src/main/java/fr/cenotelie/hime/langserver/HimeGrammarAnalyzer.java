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
import org.xowl.infra.lsp.engine.DocumentAnalyzerHime;
import org.xowl.infra.lsp.engine.DocumentSymbols;
import org.xowl.infra.lsp.engine.SymbolFactory;
import org.xowl.infra.lsp.structures.Diagnostic;
import org.xowl.infra.utils.IOUtils;

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
        return symbols;
    }
}
