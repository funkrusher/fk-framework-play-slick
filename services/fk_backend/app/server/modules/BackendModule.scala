/**
 * File     : ApplicationModule.scala
 * License  :
 *   The MIT License (MIT)
 *
 *   Original   - Copyright (c) 2014 Jöerg Viola, Marco Sinigaglia
 *   Derivative - Copyright (c) 2016 - 2020 play2-scala-pdf Contributors
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 */
package server.modules

import _root_.play.api.Environment
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.hhandoko.play.pdf.PdfGenerator

class BackendModule extends AbstractModule {

  /** Module configuration + binding */
  override def configure(): Unit = {}

  /**
   * Provides PDF generator implementation.
   *
   * @param env The current Play app Environment context.
   * @return PDF generator implementation.
   */
  @Provides
  def providePdfGenerator(env: Environment): PdfGenerator = {
    val pdfGen = new PdfGenerator(env)
    pdfGen.loadLocalFonts(Seq("fonts/opensans-regular.ttf"))
    pdfGen
  }

}
