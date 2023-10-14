package auth.controllers

import auth.managers.AuthManager
import core.util.QueryParamModel
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.AbstractController
import play.api.mvc.ControllerComponents

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthController @Inject() (cc: ControllerComponents, authManager: AuthManager)
    extends AbstractController(cc)
    with I18nSupport {}
