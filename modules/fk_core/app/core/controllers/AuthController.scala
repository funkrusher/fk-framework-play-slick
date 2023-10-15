package core.controllers

import core.managers.AuthManager
import foundation.util.QueryParamModel
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
