/*******************************************************************************
 *   Copyright 2020-2021 Rosemoe
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 ******************************************************************************/

package io.github.rosemoe.editor.plugin

import io.github.rosemoe.editor.widget.CodeEditor
import kotlinx.coroutines.CoroutineScope

/**
 * Plugin for [CodeEditor].
 *
 * Plugins are expected to subscribe events of editors to implement its functions.
 *
 * However, as plugins are registered statically, one plugin instance is expected to provide
 * services for multiple editors. So [SessionManager]
 * and [Session] are provided to manage conversations across several editors.
 * Plugins are expected to create its data structure used for each editor by implementing a [Session]
 * and manage them by [Plugin.getOrOpenSession].
 *
 * And to provide convenient thread managing, [Session] can work as a [CoroutineScope]
 * due to execute async tasks in editor's threads so that these jobs can be canceled correctly in time.
 *
 * For those plugins with no data for service states, you can use [NoDataSession] instead.
 * @param sessionCreator Creator of custom data structure
 * @see [SessionManager]
 * @see [Session]
 * @see [PluginManager]
 */
abstract class Plugin<S : Session>(sessionCreator: () -> S) {

    private val sessions = SessionManager(this, sessionCreator)

    /**
     * Get a existing [Session] or start a new [Session] for the given [editor]
     * @param editor Target [CodeEditor]
     */
    protected open fun getOrOpenSession(editor: CodeEditor) : S {
        return sessions.getOrOpenSession(editor.hashCode(), editor.coroutineContext)
    }

    /**
     * Call when a [CodeEditor] is not going to be used any more to release its [Session] in this
     * [Plugin]
     */
    internal fun onEditorDestroy(editor: CodeEditor) {
        sessions.releaseSession(editor.hashCode())
    }

    /**
     * Called when the plugins are expected to prepare its data and listeners.
     * At this time, plugins are able to subscribe events and initialize global data.
     */
    abstract fun onLoad()

    /**
     * Get plugin configuration items
     */
    abstract fun getConfig()

}