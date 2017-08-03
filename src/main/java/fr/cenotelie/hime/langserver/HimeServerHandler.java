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

import org.xowl.infra.jsonrpc.JsonRpcRequest;
import org.xowl.infra.jsonrpc.JsonRpcResponse;
import org.xowl.infra.lsp.server.LspServerHandlerBase;

/**
 * Implements the handler for this server
 *
 * @author Laurent Wouters
 */
public class HimeServerHandler extends LspServerHandlerBase {
    /**
     * Initializes this server
     */
    public HimeServerHandler() {
        super(new HimeWorkspace());
    }

    @Override
    protected JsonRpcResponse onWorkspaceExecuteCommand(JsonRpcRequest request) {
        return null;
    }

    @Override
    protected JsonRpcResponse onTextDocumentCompletion(JsonRpcRequest request) {
        return null;
    }

    @Override
    protected JsonRpcResponse onCompletionItemResolve(JsonRpcRequest request) {
        return null;
    }

    @Override
    protected JsonRpcResponse onTextDocumentHover(JsonRpcRequest request) {
        return null;
    }

    @Override
    protected JsonRpcResponse onTextDocumentSignatureHelp(JsonRpcRequest request) {
        return null;
    }

    @Override
    protected JsonRpcResponse onTextDocumentHighlights(JsonRpcRequest request) {
        return null;
    }

    @Override
    protected JsonRpcResponse onTextDocumentFormatting(JsonRpcRequest request) {
        return null;
    }

    @Override
    protected JsonRpcResponse onTextDocumentRangeFormatting(JsonRpcRequest request) {
        return null;
    }

    @Override
    protected JsonRpcResponse onTextDocumentOnTypeFormatting(JsonRpcRequest request) {
        return null;
    }

    @Override
    protected JsonRpcResponse onTextDocumentCodeAction(JsonRpcRequest request) {
        return null;
    }

    @Override
    protected JsonRpcResponse onTextDocumentCodeLenses(JsonRpcRequest request) {
        return null;
    }

    @Override
    protected JsonRpcResponse onCodeLensResolve(JsonRpcRequest request) {
        return null;
    }

    @Override
    protected JsonRpcResponse onTextDocumentLink(JsonRpcRequest request) {
        return null;
    }

    @Override
    protected JsonRpcResponse onDocumentLinkResolve(JsonRpcRequest request) {
        return null;
    }

    @Override
    protected JsonRpcResponse onTextDocumentRename(JsonRpcRequest request) {
        return null;
    }
}