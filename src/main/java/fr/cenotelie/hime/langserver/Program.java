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

import org.xowl.infra.lsp.runners.LspRunner;
import org.xowl.infra.lsp.runners.LspRunnerStdStreams;
import org.xowl.infra.lsp.server.LspServer;
import org.xowl.infra.lsp.server.LspServerHandlerBase;

/**
 * The main program for this language server
 *
 * @author Laurent Wouters
 */
public class Program {
    /**
     * The main entry point
     *
     * @param args The arguments
     */
    public static void main(String[] args) {
        LspServer server = new LspServer(new LspServerHandlerBase(new HimeWorkspace()));
        LspRunner runner = new LspRunnerStdStreams(server);
        runner.run();
    }
}
