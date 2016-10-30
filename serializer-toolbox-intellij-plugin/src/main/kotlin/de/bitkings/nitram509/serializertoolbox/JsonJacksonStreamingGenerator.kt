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
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.*
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import com.intellij.psi.impl.file.PsiDirectoryFactory
import com.intellij.psi.search.GlobalSearchScope
import java.lang.Character.isLowerCase
import java.lang.Character.toLowerCase

class JsonJacksonStreamingGenerator {

  public fun generate(thingClass: PsiClass, fields: List<PsiField>) {
    object : WriteCommandAction.Simple<Unit>(thingClass.project, thingClass.containingFile) {
      override fun run() {
        val serializerName = thingClass.name + "Serializer"
        val serializerClass = JavaPsiFacade.getElementFactory(thingClass.project).createClass(serializerName)
        addJacksonFactoryField(serializerClass)
        generateSerializeByFile(serializerClass, thingClass)
        generateSerializeByOutputStream(serializerClass, thingClass)
        generateWriteObject(serializerClass, thingClass, fields)

        val serializerFile = createSerializerJavaSourceFile(thingClass)
        serializerFile.packageName = (thingClass.containingFile as PsiJavaFile).packageName
        serializerFile.add(serializerClass)
        FileEditorManager
            .getInstance(thingClass.project)
            .openFile(serializerFile.virtualFile, true)
      }
    }.execute()
  }

  private fun createSerializerJavaSourceFile(thingClass: PsiClass): PsiJavaFile {
    val directoryFactory = PsiDirectoryFactory.getInstance(thingClass.project)
    val parentDirectory = directoryFactory.createDirectory(thingClass.containingFile.virtualFile.parent)
    val serializerName = thingClass.name + "Serializer"
    // TODO: handle case where file already exists
    val serializerFile = parentDirectory.createFile("$serializerName.java") as PsiJavaFile
    return serializerFile
  }

  private fun generateSerializeByFile(serializerClass: PsiClass, thingClass: PsiClass) {
    importClassByName(serializerClass, "java.io.File")
    importClassByName(serializerClass, "java.io.IOException")
    importClassByName(serializerClass, "com.fasterxml.jackson.core.JsonFactory")
    importClassByName(serializerClass, "com.fasterxml.jackson.core.JsonGenerator")
    importClassByName(serializerClass, "com.fasterxml.jackson.core.JsonEncoding")
    val instanceName = createInstanceNameDeclaration(thingClass)
    val sb = StringBuilder()
    sb.append("public void serialize(File targetFile, ${thingClass.name} $instanceName) throws IOException {\n")
    sb.append("  try (JsonGenerator jg = jsonFactory.createGenerator(targetFile, JsonEncoding.UTF8)) {\n")
    sb.append("    writeObject(jg, $instanceName);\n")
    sb.append("  }\n")
    sb.append("}\n")
    setNewMethod(serializerClass, sb.toString(), "serializeFile")
  }

  private fun generateSerializeByOutputStream(serializerClass: PsiClass, thingClass: PsiClass) {
    importClassByName(serializerClass, "java.io.OutputStream")
    importClassByName(serializerClass, "java.io.IOException")
    importClassByName(serializerClass, "com.fasterxml.jackson.core.JsonFactory")
    importClassByName(serializerClass, "com.fasterxml.jackson.core.JsonGenerator")
    importClassByName(serializerClass, "com.fasterxml.jackson.core.JsonEncoding")
    val instanceName = createInstanceNameDeclaration(thingClass)
    val sb = StringBuilder()
    sb.append("public void serialize(OutputStream outputStream, ${thingClass.name} $instanceName) throws IOException {\n")
    sb.append("  try (JsonGenerator jg = jsonFactory.createGenerator(outputStream, JsonEncoding.UTF8)) {\n")
    sb.append("    writeObject(jg, $instanceName);\n")
    sb.append("  }\n")
    sb.append("}\n")
    setNewMethod(serializerClass, sb.toString(), "serializeOutputStream")
  }

  private fun generateWriteObject(serializerClass: PsiClass, thingClass: PsiClass, fields: List<PsiField>) {
    val sb = StringBuilder()
    val instanceName = createInstanceNameDeclaration(thingClass)
    sb.append("private void writeObject(JsonGenerator jg, ${thingClass.name} $instanceName) throws IOException {\n")
    sb.append("  jg.writeStartObject();\n")
    appendWriteFields(serializerClass, sb, fields, instanceName)
    sb.append("        // done.\n")
    sb.append("  jg.writeEndObject();\n")
    sb.append("}")
    setNewMethod(serializerClass, sb.toString(), "writeObject")
  }

  private fun createInstanceNameDeclaration(thingClass: PsiClass): String {
    val clzName = thingClass.name
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

  private fun addJacksonFactoryField(serializerClass: PsiClass) {
    val elementFactory = JavaPsiFacade.getElementFactory(serializerClass.project)
    val newField = elementFactory.createFieldFromText("private final JsonFactory jsonFactory = new JsonFactory();", serializerClass)
    serializerClass.add(newField)
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

  private fun appendWriteFields(serializerClass: PsiClass, sb: StringBuilder, fields: List<PsiField>, instanceName: String) {
    for (field in fields) {
      sb.append("        // write field ${field.name}...\n")
      sb.append("jg.writeFieldName(\"${field.name}\");\n")
      val deepType = field.type.deepComponentType
      if (field.type.arrayDimensions == 1) {
        val methodName = "writeArray_${deepType.presentableText}"
        ensureWriteArrayHelperMethodExists(serializerClass, deepType);
        sb.append("$methodName(jg, $instanceName.${field.name});\n");
      } else if (isWritableNumberType(deepType)) {
        sb.append("jg.writeNumber($instanceName.${field.name});\n");
      } else if (PsiType.BOOLEAN == deepType) {
        sb.append("jg.writeBoolean($instanceName.${field.name});\n");
      } else if (isJavaStringType(deepType)) {
        sb.append("jg.writeString($instanceName.${field.name});\n");
      } else {
        sb.append("jg.writeObject($instanceName.${field.name});\n");
      }
    }
  }

  private fun ensureWriteArrayHelperMethodExists(serializerClass: PsiClass, deepType: PsiType) {
    val methodName = "writeArray_${deepType.presentableText}"
    val exists = serializerClass.methods.any { m -> m.name == methodName }
    if (exists) return
    val sb = StringBuilder()
    sb.append("private void $methodName(JsonGenerator jg, ${deepType.presentableText}[] array) throws IOException {\n")
    sb.append("  jg.writeStartArray();\n")
    sb.append("  for (${deepType.presentableText} val : array) {\n")
    if (isWritableNumberType(deepType)) {
      sb.append("    jg.writeNumber(val);\n");
    } else if (PsiType.BOOLEAN == deepType) {
      sb.append("    jg.writeBoolean(val);\n");
    } else if (isJavaStringType(deepType)) {
      sb.append("    jg.writeString(val);\n");
    } else {
      sb.append("    jg.writeObject(val);\n");
    }
    sb.append("  }\n")
    sb.append("  jg.writeEndArray();\n")
    sb.append("}")
    setNewMethod(serializerClass, sb.toString(), methodName)
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
    // TODO: add import as comment, if class not found
    if (resolvedPsiClass != null) {
      val importStatement = elementFactory.createImportStatement(resolvedPsiClass)
      val importList = (psiClass.containingFile as PsiJavaFile).importList
      if (null == importList?.findSingleClassImportStatement(classNameToImport)) {
        importList?.add(importStatement)
      }
    }
  }

}
