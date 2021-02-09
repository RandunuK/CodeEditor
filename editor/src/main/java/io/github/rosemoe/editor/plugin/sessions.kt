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

import android.util.SparseArray
import androidx.core.util.set
import io.github.rosemoe.editor.widget.CodeEditor
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.coroutines.CoroutineContext


/**
 * A [SessionManager] manages [Session] objects for [Plugin] to implement
 * services for multiple [CodeEditor] instances.
 * Implementations of [Plugin] are able to save data for different editors inside a [Session], which
 * will be stored in this manager.
 */
class SessionManager<T : Session>(val plugin: Plugin<T>, val sessionCreator: () -> T){

    /** Container for [Session] objects */
    private val sessions = SparseArray<T>()

    /** Lock for [sessions] due to thread-safe */
    private val lock = ReentrantReadWriteLock()

    /**
     * Get a existing [Session] or start a new [Session] for the given [sessionId]
     * @param sessionId The hash code of target [CodeEditor]
     */
    fun getOrOpenSession(sessionId: Int, coroutineContext: CoroutineContext) : T {
        var session = lock.read {
            sessions[sessionId]
        }
        if (session == null) {
            lock.write {
                val tmpSession = sessions[sessionId]
                if (tmpSession != null) {
                    session = tmpSession
                    return@write
                }
                session = sessionCreator()
                session.coroutineContext = coroutineContext + CoroutineName(plugin.toString())
                sessions[sessionId] = session
            }
        }
        return session
    }

    /**
     * When a [CodeEditor] is not going to be used  any more, it is called to release
     * [Session] to cut down memory usage.
     * After calling this, given [Session] will be deleted and its [Session.release] is called.
     *
     * **This behavior is controlled by editors. Users are not expected to call this by yourself**
     * @param sessionId The hash code of target [CodeEditor]
     */
    fun releaseSession(sessionId: Int) {
        var session: T?
        lock.write {
            session = sessions[sessionId]
            sessions.remove(sessionId)
        }
        session?.release()
    }

}

/**
 * A [Session] holds data for a specified [CodeEditor] instance. [Plugin] uses this [Session] to
 * save data for different editor instances.
 * Also work as a [CoroutineScope]
 *
 * Implementations of [Plugin] should use a custom [Session] to specify its data structure.
 * If you have no data for each editor, you can alternatively use [NoDataSession]
 */
abstract class Session : CoroutineScope {

    override lateinit var coroutineContext: CoroutineContext

    /**
     * Called by [SessionManager] to notify this [Session] that this instance is not going to be
     * used any more.
     *
     * **Implementations of [Session] should release all of its resources due to free memory, especially
     * native resources.**
     */
    abstract fun release()

}

/**
 * Convenient class for those plugins with no data used for services.
 */
class NoDataSession : Session() {

    override fun release() {

    }

}