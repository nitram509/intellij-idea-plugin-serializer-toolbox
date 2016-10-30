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

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiJavaFile
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase
import org.junit.Test

class JsonJacksonStreamingGeneratorTest : LightCodeInsightFixtureTestCase() {

  private var generator: JsonJacksonStreamingGenerator = JsonJacksonStreamingGenerator()

  @Test
  fun test_generate_BASIC_TYPES_without_any_exceptions() {
    runGeneratorTest("src/test/java/org/example/testcases/BasicTypes.java", "org.example.testcases.BasicTypesSerializer")
  }

  @Test
  fun test_generate_BASIC_TYPE_ARRAYS_without_any_exceptions() {
    runGeneratorTest("src/test/java/org/example/testcases/BasicTypeArrays.java", "org.example.testcases.BasicTypeArraysSerializer")
  }

  private fun runGeneratorTest(sourceFile: String, fqClassName: String) {
    val sampleFile = myFixture.copyFileToProject(sourceFile)
    val psiClass = (this.psiManager.findFile(sampleFile) as PsiJavaFile).classes[0]

    generator.generate(psiClass, findAllFields(psiClass))

    // assert no exception
    println(myFixture.findClass(fqClassName).text)
  }

  private fun findAllFields(psiClass: PsiClass): List<PsiField> {
    val fields: MutableList<PsiField> = mutableListOf()
    for (field in psiClass.fields) {
      fields.add(field)
    }
    return fields
  }

}