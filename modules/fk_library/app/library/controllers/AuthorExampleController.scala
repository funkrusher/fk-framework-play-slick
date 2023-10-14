package library.controllers

import core.util.QueryParamModel
import library.dtos.AuthorDTO
import library.dtos.AuthorPaginateDTO
import library.managers.AuthorManager
import library.managers.AuthorExampleManager
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.AbstractController
import play.api.mvc.ControllerComponents

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AuthorExampleController @Inject() (cc: ControllerComponents, authorTestManager: AuthorExampleManager)
    extends AbstractController(cc)
    with I18nSupport {

  /**
   * Test a Transaction with an error and rollback
   *
   * @return test result
   */
  def testTransactionWithRollback =
    Action.async { implicit request =>
      authorTestManager.testTransactionWithErrorAndRollback()
      Future.successful(Ok("ok"))
    }

}
