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

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.*
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import com.intellij.psi.search.GlobalSearchScope
import java.lang.Character.isLowerCase
import java.lang.Character.toLowerCase

class JsonJacksonStreamingGenerator {

  public fun generate(psiClass: PsiClass, fields: List<PsiField>) {
    object : WriteCommandAction.Simple<Unit>(psiClass.project, psiClass.containingFile) {
      override fun run() {
        addJacksonFactoryField(psiClass)
        generateSerializeByFile(psiClass)
        generateSerializeByOutputStream(psiClass)
        generateWriteObject(psiClass, fields)
      }
    }.execute()
  }

  private fun generateSerializeByFile(psiClass: PsiClass) {
    importClassByName(psiClass, "java.io.File")
    importClassByName(psiClass, "java.io.IOException")
    importClassByName(psiClass, "com.fasterxml.jackson.core.JsonFactory")
    importClassByName(psiClass, "com.fasterxml.jackson.core.JsonGenerator")
    importClassByName(psiClass, "com.fasterxml.jackson.core.JsonEncoding")

    val sb = StringBuilder()
    sb.append("public void serialize(File targetFile) throws IOException {\n")
    sb.append("  try (JsonGenerator jg = jsonFactory.createGenerator(targetFile, JsonEncoding.UTF8)) {\n")
    sb.append("    writeObject(jg, this);\n")
    sb.append("  }\n")
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
    sb.append("  try (JsonGenerator jg = jsonFactory.createGenerator(outputStream, JsonEncoding.UTF8)) {\n")
    sb.append("    writeObject(jg, this);\n")
    sb.append("  }\n")
    sb.append("}\n")
    setNewMethod(psiClass, sb.toString(), "serializeOutputStream")
  }

  private fun generateWriteObject(psiClass: PsiClass, fields: List<PsiField>) {
    val sb = StringBuilder()
    val instanceName = createInstanceNameDeclaration(psiClass)
    sb.append("private void writeObject(JsonGenerator jg, ${psiClass.name} " + instanceName + ") throws IOException {\n")
    sb.append("  jg.writeStartObject();\n")
    appendWriteFields(sb, fields, instanceName)
    sb.append("        // done.\n")
    sb.append("  jg.writeEndObject();\n")
    sb.append("}")
    setNewMethod(psiClass, sb.toString(), "writeObject")
  }

  private fun createInstanceNameDeclaration(psiClass: PsiClass): String {
    val clzName = psiClass.name
    if (clzName != null) {
      val firstLetter = clzName.toCharArray()[0]
      if (isLowerCase(firstLetter)) {
        return "$$clzName"
      }
      return toLowerCase(firstLetter) + clzName.substring(1)
    }
    throw UnsupportedOperationException("Not supported yet.")
  }

  private fun setNewMethod(psiClass: PsiClass, newMethodBody: String, methodName: String) {
    val elementFactory = JavaPsiFacade.getElementFactory(psiClass.project)
    val newEqualsMethod = elementFactory.createMethodFromText(newMethodBody, psiClass)
    val method = addOrReplaceMethod(psiClass, newEqualsMethod, methodName)
    JavaCodeStyleManager.getInstance(psiClass.project).shortenClassReferences(method)
  }

  private fun addJacksonFactoryField(psiClass: PsiClass) {
    val elementFactory = JavaPsiFacade.getElementFactory(psiClass.project)
    val newField = elementFactory.createFieldFromText("private final JsonFactory jsonFactory = new JsonFactory();", psiClass)
    psiClass.add(newField)
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

  private fun appendWriteFields(sb: StringBuilder, fields: List<PsiField>, instanceName: String) {
    for (field in fields) {
      sb.append("        // write field ").append(field.name).append("...\n")
      sb.append("jg.writeFieldName(\"").append(field.name).append("\");\n")
      val deepType = field.type.deepComponentType
      val isCompatibleArrayBasisType = deepType in listOf(PsiType.INT, PsiType.LONG, PsiType.DOUBLE)
      if (field.type.arrayDimensions == 1 && isCompatibleArrayBasisType) {
        sb.append("jg.writeArray($instanceName.ints, 0 ,$instanceName." + field.name + ".length);");
      } else if (isWritableNumberType(deepType)) {
        sb.append("jg.writeNumber($instanceName.").append(field.name).append(");\n");
      } else if (PsiType.BOOLEAN == deepType) {
        sb.append("jg.writeBoolean($instanceName.").append(field.name).append(");\n");
      } else if (isJavaStringType(deepType)) {
        sb.append("jg.writeString($instanceName.").append(field.name).append(");\n");
      } else {
        sb.append("jg.writeObject($instanceName.").append(field.name).append(");\n");
      }
    }
  }

  private fun isWritableNumberType(deepType: PsiType): Boolean {
    val isBasisType = deepType in listOf(PsiType.BYTE, PsiType.SHORT, PsiType.INT, PsiType.LONG, PsiType.FLOAT, PsiType.DOUBLE)
    return isBasisType || deepType.equalsToText("java.math.BigDecimal") || deepType.equalsToText("java.math.BigInteger")
  }

  private fun isJavaStringType(deepType: PsiType): Boolean {
    return deepType.equalsToText("java.lang.String")
  }

  private fun importClassByName(psiClass: PsiClass, classNameToImport: String) {
    val project = psiClass.project
    val scope = GlobalSearchScope.allScope(project)
    val elementFactory = JavaPsiFacade.getElementFactory(psiClass.project)
    val typeByName = PsiType.getTypeByName(classNameToImport, project, scope)
    val resolvedPsiClass = typeByName.resolve()
    if (resolvedPsiClass != null) {
      val importStatement = elementFactory.createImportStatement(resolvedPsiClass)
      val importList = (psiClass.containingFile as PsiJavaFile).importList
      if (null == importList?.findSingleClassImportStatement(classNameToImport)) {
        importList?.add(importStatement)
      }
    }
  }

}
