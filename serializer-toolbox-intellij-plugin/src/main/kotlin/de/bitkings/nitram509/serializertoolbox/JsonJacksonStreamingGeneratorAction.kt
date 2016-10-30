/*
 * The MIT License (MIT)
 * Copyright (c) 2016 Martin W. Kirst
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package de.bitkings.nitram509.serializertoolbox

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.psi.PsiClass
import com.intellij.psi.util.PsiTreeUtil

class JsonJacksonStreamingGeneratorAction() : AnAction("Json Serializer ...") {

  private val jsonJacksonSerializerGenerator = JsonJacksonSerializerGenerator()
  private val jsonJacksonDeserializerGenerator = JsonJacksonDeserializerGenerator()

  override fun actionPerformed(e: AnActionEvent) {
    val psiClass = getPsiClassFromContext(e)
    val dlg = SelectFieldsDialog(psiClass!!)
    dlg.show()
    if (dlg.isOK) {
      jsonJacksonSerializerGenerator.generate(psiClass, dlg.selectedFields)
      jsonJacksonDeserializerGenerator.generate(psiClass, dlg.selectedFields)
    }
  }

  override fun update(e: AnActionEvent) {
    val psiClass = getPsiClassFromContext(e)
    e.presentation.isEnabled = psiClass != null
    e.presentation.icon = IconConstants.HZ_ACTION
  }

  private fun getPsiClassFromContext(e: AnActionEvent): PsiClass? {
    val psiFile = e.getData(LangDataKeys.PSI_FILE)
    val editor = e.getData(PlatformDataKeys.EDITOR)
    if (psiFile == null || editor == null) {
      return null
    }
    val offset = editor.caretModel.offset
    val elementAt = psiFile.findElementAt(offset)
    return PsiTreeUtil.getParentOfType(elementAt, PsiClass::class.java)
  }

}
