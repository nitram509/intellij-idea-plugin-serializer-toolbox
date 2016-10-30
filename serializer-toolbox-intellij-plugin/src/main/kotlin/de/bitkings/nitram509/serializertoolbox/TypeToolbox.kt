package de.bitkings.nitram509.serializertoolbox

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiType
import com.intellij.psi.search.GlobalSearchScope

class TypeToolbox {

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

}

