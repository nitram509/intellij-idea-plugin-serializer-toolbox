package de.bitkings.nitram509.serializertoolbox

import com.intellij.psi.*
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import com.intellij.psi.search.GlobalSearchScope

class PsiToolbox {

  fun isWritableNumberType(deepType: PsiType): Boolean {
    val isBasisType = deepType in listOf(PsiType.BYTE, PsiType.SHORT, PsiType.INT, PsiType.LONG, PsiType.FLOAT, PsiType.DOUBLE)
    return isBasisType || deepType.equalsToText("java.math.BigDecimal") || deepType.equalsToText("java.math.BigInteger")
  }

  fun isJavaStringType(deepType: PsiType): Boolean {
    return deepType.equalsToText("java.lang.String")
  }

  fun importClassByName(psiClass: PsiClass, classNameToImport: String) {
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

  fun createInstanceNameDeclaration(thingClass: PsiClass): String {
    val clzName = thingClass.name
    if (clzName != null) {
      val firstLetter = clzName.toCharArray()[0]
      if (Character.isLowerCase(firstLetter)) {
        return "$$clzName"
      }
      return Character.toLowerCase(firstLetter) + clzName.substring(1)
    }
    throw UnsupportedOperationException("Not supported yet.")
  }

  fun setNewMethod(psiClass: PsiClass, newMethodBody: String, methodName: String) {
    val elementFactory = JavaPsiFacade.getElementFactory(psiClass.project)
    val newEqualsMethod = elementFactory.createMethodFromText(newMethodBody, psiClass)
    val method = addOrReplaceMethod(psiClass, newEqualsMethod, methodName)
    JavaCodeStyleManager.getInstance(psiClass.project).shortenClassReferences(method)
  }

  fun addOrReplaceMethod(psiClass: PsiClass, newMethod: PsiMethod, methodName: String): PsiElement {
    val existingMethod = findMethod(psiClass, methodName)
    return if (existingMethod != null) existingMethod.replace(newMethod) else psiClass.add(newMethod)
  }

  fun findMethod(psiClass: PsiClass, methodName: String): PsiMethod? {
    val allMethods = psiClass.allMethods
    return allMethods.firstOrNull { psiClass.name == it.containingClass!!.name && methodName == it.name }
  }


}

