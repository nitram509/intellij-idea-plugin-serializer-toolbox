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
import java.lang.Character.isLowerCase
import java.lang.Character.toLowerCase

class JsonJacksonDeserializerGenerator {

  private val typeToolbox = TypeToolbox()

  fun generate(thingClass: PsiClass, fields: List<PsiField>) {
    object : WriteCommandAction.Simple<Unit>(thingClass.project, thingClass.containingFile) {
      override fun run() {
        val serializerName = thingClass.name + "DeSerializer"
        val serializerClass = JavaPsiFacade.getElementFactory(thingClass.project).createClass(serializerName)
        addJacksonFactoryField(serializerClass)
        generateDeserializeByFile(serializerClass, thingClass)
        generateDeserializeByOutputStream(serializerClass, thingClass)
        generateReadObject(serializerClass, thingClass, fields)

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
    val serializerName = thingClass.name + "DeSerializer"
    // TODO: handle case where file already exists
    val serializerFile = parentDirectory.createFile("$serializerName.java") as PsiJavaFile
    return serializerFile
  }

  private fun generateDeserializeByFile(serializerClass: PsiClass, thingClass: PsiClass) {
    typeToolbox.importClassByName(serializerClass, "java.io.File")
    typeToolbox.importClassByName(serializerClass, "java.io.IOException")
    typeToolbox.importClassByName(serializerClass, "java.io.FileInputStream")
    typeToolbox.importClassByName(serializerClass, "com.fasterxml.jackson.core.JsonFactory")
    typeToolbox.importClassByName(serializerClass, "com.fasterxml.jackson.core.JsonToken")
    typeToolbox.importClassByName(serializerClass, "com.fasterxml.jackson.core.JsonParser")
    val sb = StringBuilder()
    sb.append("public ${thingClass.name} deserialize(File sourceFile) throws IOException {\n")
    sb.append("    return deserialize(new FileInputStream(sourceFile));\n")
    sb.append("}\n")
    setNewMethod(serializerClass, sb.toString(), "serializeFile")
  }

  private fun generateDeserializeByOutputStream(serializerClass: PsiClass, thingClass: PsiClass) {
    typeToolbox.importClassByName(serializerClass, "java.io.InputStream")
    typeToolbox.importClassByName(serializerClass, "java.io.IOException")
    typeToolbox.importClassByName(serializerClass, "com.fasterxml.jackson.core.JsonFactory")
    typeToolbox.importClassByName(serializerClass, "com.fasterxml.jackson.core.JsonToken")
    typeToolbox.importClassByName(serializerClass, "com.fasterxml.jackson.core.JsonParser")
    val sb = StringBuilder()
    sb.append("public ${thingClass.name} deserialize(InputStream inputStream) throws IOException {\n")
    sb.append("  try (JsonParser jp = jsonFactory.createParser(inputStream)) {\n")
    sb.append("    return readObject(jp);\n")
    sb.append("  }\n")
    sb.append("}\n")
    setNewMethod(serializerClass, sb.toString(), "serializeOutputStream")
  }

  private fun generateReadObject(serializerClass: PsiClass, thingClass: PsiClass, fields: List<PsiField>) {
    val sb = StringBuilder()
    val instanceName = createInstanceNameDeclaration(thingClass)
    sb.append("private ${thingClass.name} readObject(JsonParser jp) throws IOException {\n")
    sb.append("  BasicTypes $instanceName = new BasicTypes();\n")
    sb.append("  for (JsonToken jsonToken; (jsonToken = jp.nextToken()) != null && (jsonToken != JsonToken.END_OBJECT); ) {\n")
    sb.append("    if (JsonToken.FIELD_NAME != jsonToken) continue;\n")
    sb.append("    final String fieldName = jp.getCurrentName();\n")
    sb.append("    switch (fieldName) {\n")
    appendReadFields(serializerClass, sb, fields, instanceName)
    sb.append("       default: // decide what to do;\n")
    sb.append("    }\n")
    sb.append("  }\n")
    sb.append("  return $instanceName;\n")
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

  private fun appendReadFields(serializerClass: PsiClass, sb: StringBuilder, fields: List<PsiField>, instanceName: String) {
    for (field in fields) {
      val deepType = field.type.deepComponentType
      sb.append("    case \"${field.name}\":\n")
      sb.append("      jsonToken = jp.nextToken(); // read value\n")
      if (field.type.arrayDimensions == 1) {
        // TODO ...
      } else if (PsiType.BYTE == deepType) {
        sb.append("      $instanceName.${field.name} = jp.getByteValue();\n")
      } else if (PsiType.SHORT == deepType) {
        sb.append("      $instanceName.${field.name} = jp.getShortValue();\n")
      } else if (PsiType.INT == deepType) {
        sb.append("      $instanceName.${field.name} = jp.getIntValue();\n")
      } else if (PsiType.DOUBLE == deepType) {
        sb.append("      $instanceName.${field.name} = jp.getDoubleValue();\n")
      } else if (PsiType.FLOAT == deepType) {
        sb.append("      $instanceName.${field.name} = jp.getFloatValue();\n")
      } else if (PsiType.BOOLEAN == deepType) {
        sb.append("      $instanceName.${field.name} = jp.getBooleanValue();\n")
      } else if (typeToolbox.isJavaStringType(deepType)) {
        sb.append("      $instanceName.${field.name} = jp.getText();\n")
      } else {
        // TODO ...
      }
      sb.append("      break;\n")
    }
  }
}
