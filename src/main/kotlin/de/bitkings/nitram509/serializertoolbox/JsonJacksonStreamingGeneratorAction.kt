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
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.*
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil

class JsonJacksonStreamingGeneratorAction() : AnAction("Json Serializer ...") {

  override fun actionPerformed(e: AnActionEvent) {
    val psiClass = getPsiClassFromContext(e)
    val dlg = SelectFieldsDialog(psiClass!!)
    dlg.show()
    if (dlg.isOK) {
      generate(psiClass, dlg.selectedFields)
    }
  }

  override fun update(e: AnActionEvent) {
    val psiClass = getPsiClassFromContext(e)
    e.presentation.isEnabled = psiClass != null
    e.presentation.icon = IconConstants.HZ_ACTION
  }

  fun generate(psiClass: PsiClass, fields: List<PsiField>) {
    object : WriteCommandAction.Simple<Unit>(psiClass.project, psiClass.containingFile) {
      override fun run() {
        generateSerializeByFile(psiClass)
        generateSerializeByOutputStream(psiClass)
        generateWriteObject(psiClass, fields)
      }
    }.execute()
  }

  private fun setNewMethod(psiClass: PsiClass, newMethodBody: String, methodName: String) {
    val elementFactory = JavaPsiFacade.getElementFactory(psiClass.project)
    val newEqualsMethod = elementFactory.createMethodFromText(newMethodBody, psiClass)
    val method = addOrReplaceMethod(psiClass, newEqualsMethod, methodName)
    JavaCodeStyleManager.getInstance(psiClass.project).shortenClassReferences(method)
  }

  private fun addOrReplaceMethod(psiClass: PsiClass, newMethod: PsiMethod, methodName: String): PsiElement {
    val existingMethod = findMethod(psiClass, methodName)
    return if (existingMethod != null) existingMethod.replace(newMethod) else psiClass.add(newMethod)
  }

  private fun findMethod(psiClass: PsiClass, methodName: String): PsiMethod? {
    val allMethods = psiClass.allMethods
    for (method in allMethods) {
      if (psiClass.name == method.containingClass!!.name && methodName == method.name) {
        return method
      }
    }
    return null
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

  private fun generateSerializeByFile(psiClass: PsiClass) {
    importClassByName(psiClass, "java.io.File")
    importClassByName(psiClass, "java.io.IOException")
    importClassByName(psiClass, "com.fasterxml.jackson.core.JsonFactory")
    importClassByName(psiClass, "com.fasterxml.jackson.core.JsonGenerator")
    importClassByName(psiClass, "com.fasterxml.jackson.core.JsonEncoding")

    val sb = StringBuilder()
    sb.append("public void serialize(File targetFile) throws IOException {\n")
    sb.append("  JsonFactory jsonFactory = new JsonFactory();\n")
    sb.append("  JsonGenerator jg = jsonFactory.createGenerator(targetFile, JsonEncoding.UTF8);\n")
    sb.append("  writeObject(jg, this);\n")
    sb.append("  jg.close();\n")
    sb.append("}\n")
    setNewMethod(psiClass, sb.toString(), "serializeFile")
  }

  private fun generateSerializeByOutputStream(psiClass: PsiClass) {
    importClassByName(psiClass, "java.io.OutputStream")
    importClassByName(psiClass, "java.io.IOException")
    importClassByName(psiClass, "com.fasterxml.jackson.core.JsonFactory")
    importClassByName(psiClass, "com.fasterxml.jackson.core.JsonGenerator")
    importClassByName(psiClass, "com.fasterxml.jackson.core.JsonEncoding")

    val sb = StringBuilder()
    sb.append("public void serialize(OutputStream outputStream) throws IOException {\n")
    sb.append("  JsonFactory jsonFactory = new JsonFactory();\n")
    sb.append("  JsonGenerator jg = jsonFactory.createGenerator(outputStream, JsonEncoding.UTF8);\n")
    sb.append("  writeObject(jg, this);\n")
    sb.append("  jg.close();\n")
    sb.append("}\n")
    setNewMethod(psiClass, sb.toString(), "serializeOutputStream")
  }

  private fun generateWriteObject(psiClass: PsiClass, fields: List<PsiField>) {
    val sb = StringBuilder()
    sb.append("private void writeObject(JsonGenerator jg, Forecast forecast) throws IOException {\n")
    sb.append("  jg.writeStartObject();\n")

    appendWriteFields(sb, fields)
    sb.append("        // done.\n")
    sb.append("  jg.writeEndObject();\n")
    sb.append("}")
    setNewMethod(psiClass, sb.toString(), "writeObject")
  }

  private fun appendWriteFields(sb: StringBuilder, fields: List<PsiField>) {
    for (field in fields) {
      sb.append("        // write field ").append(field.name).append("...\n")
      sb.append("jg.writeFieldName(\"").append(field.name).append("\");\n")
      val deepType = field.type.deepComponentType
      if (deepType in listOf(PsiType.BYTE, PsiType.SHORT, PsiType.INT, PsiType.LONG, PsiType.FLOAT, PsiType.DOUBLE)) {
        // also BigInteger + BigDecimal
        sb.append("jg.writeNumber(forecast.").append(field.name).append(");\n");
      } else if (PsiType.BOOLEAN.equals(deepType) ) {
        sb.append("jg.writeBoolean(forecast.").append(field.name).append(");\n");
      } else {
        sb.append("jg.writeObject(forecast.").append(field.name).append(");\n");
      }
    }
  }

  private fun importClassByName(psiClass: PsiClass, className: String) {
    val project = psiClass.project
    val scope = GlobalSearchScope.allScope(project)
    val elementFactory = JavaPsiFacade.getElementFactory(psiClass.project)
    val typeByName = PsiType.getTypeByName(className, project, scope)
    val importStatement = elementFactory.createImportStatement(typeByName.resolve() as PsiClass)
    val importList = (psiClass.containingFile as PsiJavaFile).importList
    if (null == importList?.findSingleClassImportStatement(className)) {
      importList?.add(importStatement)
    }
  }

}
