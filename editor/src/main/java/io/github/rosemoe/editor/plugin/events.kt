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

import android.view.KeyEvent
import android.view.MotionEvent
import io.github.rosemoe.editor.widget.CodeEditor

interface Event {
    val editor: CodeEditor
}

class KeyEvent(val keyEvent: KeyEvent, override val editor: CodeEditor) : Event

class TouchEvent(val motionEvent: MotionEvent, override val editor: CodeEditor) : Event

class LanguageSearchEvent(val requirement: LanguageRequirement, override val editor: CodeEditor) : Event {

    class LanguageRequirement(val fileSuffix: String?, val languageName: String?, val languageFamily: String?) {

        fun getMetRequirementCount(description: LanguageDescription): Int {
            var count = 0
            if (languageName != null && description.languageName.equals(languageName, true)) {
                count++
            }
            if (languageFamily != null && description.languageFamily.equals(languageFamily, true)) {
                count++
            }
            if (fileSuffix != null) {
                description.supportedFileSuffix.forEach {
                    if (it.equals(fileSuffix, true)) {
                        count++
                        return@forEach
                    }
                }
            }
            return count
        }

    }

}