package de.bitkings.nitram509.serializertoolbox

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.*
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import org.apache.commons.lang.WordUtils
import java.util.*

class JsonJacksonStreamingGeneratorAction() : AnAction("Json Serializer ...") {

  override fun actionPerformed(e: AnActionEvent) {
    val psiClass = getPsiClassFromContext(e)
    val dlg = SelectFieldsDialog(psiClass!!)
    dlg.show()
    if (dlg.isOK) {
      generate(psiClass, dlg.selectedFields)
    }
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

  protected fun setNewMethod(psiClass: PsiClass, newMethodBody: String, methodName: String) {
    val elementFactory = JavaPsiFacade.getElementFactory(psiClass.project)
    val newEqualsMethod = elementFactory.createMethodFromText(newMethodBody, psiClass)
    val method = addOrReplaceMethod(psiClass, newEqualsMethod, methodName)
    JavaCodeStyleManager.getInstance(psiClass.project).shortenClassReferences(method)
  }

  protected fun addOrReplaceMethod(psiClass: PsiClass, newEqualsMethod: PsiMethod, methodName: String): PsiElement {
    val existingEqualsMethod = findMethod(psiClass, methodName)
    return if (existingEqualsMethod != null) existingEqualsMethod.replace(newEqualsMethod) else psiClass.add(newEqualsMethod)
  }

  protected fun findMethod(psiClass: PsiClass, methodName: String): PsiMethod? {
    val allMethods = psiClass.allMethods
    for (method in allMethods) {
      if (psiClass.name == method.containingClass!!.name && methodName == method.name) {
        return method
      }
    }
    return null
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

  private fun generateSerializeByFile(psiClass: PsiClass) {
    importClassByName(psiClass, "java.io.File")
    importClassByName(psiClass, "java.io.IOException")
    importClassByName(psiClass, "com.fasterxml.jackson.core.JsonFactory")
    importClassByName(psiClass, "com.fasterxml.jackson.core.JsonGenerator")
    importClassByName(psiClass, "com.fasterxml.jackson.core.JsonEncoding")

    val builder = StringBuilder()
    builder.append("public void serialize(File targetFile) throws IOException {\n")
    builder.append("  JsonFactory jsonFactory = new JsonFactory();")
    builder.append("  JsonGenerator jg = jsonFactory.createGenerator(targetFile, JsonEncoding.UTF8);")
    builder.append("  writeObject(jg, this);")
    builder.append("  jg.close();")
    builder.append("}")
    setNewMethod(psiClass, builder.toString(), "serializeFile")
  }

  private fun generateSerializeByOutputStream(psiClass: PsiClass) {
    importClassByName(psiClass, "java.io.OutputStream")
    importClassByName(psiClass, "java.io.IOException")
    importClassByName(psiClass, "com.fasterxml.jackson.core.JsonFactory")
    importClassByName(psiClass, "com.fasterxml.jackson.core.JsonGenerator")
    importClassByName(psiClass, "com.fasterxml.jackson.core.JsonEncoding")

    val builder = StringBuilder()
    builder.append("public void serialize(OutputStream outputStream) throws IOException {\n")
    builder.append("  JsonFactory jsonFactory = new JsonFactory();")
    builder.append("  JsonGenerator jg = jsonFactory.createGenerator(outputStream, JsonEncoding.UTF8);")
    builder.append("  writeObject(jg, this);")
    builder.append("  jg.close();")
    builder.append("}")
    setNewMethod(psiClass, builder.toString(), "serializeOutputStream")
  }

  private fun generateWriteObject(psiClass: PsiClass, fields: List<PsiField>) {
    val builder = StringBuilder()
    builder.append("private void writeObject(JsonGenerator jg, Forecast forecast) throws IOException { \n")
    builder.append("  jg.writeStartObject();")

    val remaining = writeFields(psiClass, fields, builder)
    writeObjects(psiClass, remaining, builder)

    builder.append("  jg.writeEndObject();")
    builder.append("}")
    setNewMethod(psiClass, builder.toString(), "writeObject")
  }

  private fun writeFields(psiClass: PsiClass, fields: List<PsiField>, builder: StringBuilder): List<PsiField> {
    val remaining = ArrayList<PsiField>()
    val project = psiClass.project
    val scope = GlobalSearchScope.allScope(project)

    return remaining
  }

  private fun writeObjects(psiClass: PsiClass, fields: List<PsiField>, builder: StringBuilder) {
    val project = psiClass.project
    val scope = GlobalSearchScope.allScope(project)

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
