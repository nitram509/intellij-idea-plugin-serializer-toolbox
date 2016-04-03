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
    mainPanel = LabeledComponent.create(decorator.createPanel(), title)
    init()
  }

  override fun createCenterPanel(): JComponent? {
    return mainPanel
  }

  val selectedFields: List<PsiField>
    get() {
      val iMin = fieldList.selectionModel.minSelectionIndex
      val iMax = fieldList.selectionModel.maxSelectionIndex
      if (iMin < 0 || iMax < 0) {
        return emptyList()
      }

      val selectedFields = ArrayList<PsiField>()
      for (i in iMin..iMax) {
        if (fieldList.selectionModel.isSelectedIndex(i)) {
          selectedFields.add(fieldList.model.getElementAt(i) as PsiField)
        }
      }
      return selectedFields
    }

}