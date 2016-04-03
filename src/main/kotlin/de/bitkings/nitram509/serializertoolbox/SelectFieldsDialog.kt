package de.bitkings.nitram509.serializertoolbox

import com.intellij.ide.util.DefaultPsiElementCellRenderer
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.LabeledComponent
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.ui.CollectionListModel
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.components.JBList
import java.util.*
import javax.swing.JComponent
import javax.swing.JPanel

class SelectFieldsDialog(psiClass: PsiClass) : DialogWrapper(psiClass.project) {
  private val mainPanel: LabeledComponent<JPanel>
  private val fieldList: JBList

  init {
    title = "Select fields you want to be serialized"
    val myFields = CollectionListModel(*psiClass.allFields)
    fieldList = JBList(myFields)
    fieldList.cellRenderer = DefaultPsiElementCellRenderer()
    val decorator = ToolbarDecorator.createDecorator(fieldList)
    decorator.disableAddAction()
    val panel = decorator.createPanel()
    mainPanel = LabeledComponent.create(panel, title)
    init()
  }

  override fun createCenterPanel(): JComponent? {
    return mainPanel
  }

  val selectedFields: List<PsiField>
    get() {
      val selectionModle = fieldList.selectionModel
      val iMin = selectionModle.minSelectionIndex
      val iMax = selectionModle.maxSelectionIndex

      if (iMin < 0 || iMax < 0) {
        return emptyList()
      }

      val selectedItems = ArrayList<PsiField>()
      for (i in iMin..iMax) {
        if (selectionModle.isSelectedIndex(i)) {
          selectedItems.add(fieldList.model.getElementAt(i) as PsiField)
        }
      }
      return selectedItems
    }

}