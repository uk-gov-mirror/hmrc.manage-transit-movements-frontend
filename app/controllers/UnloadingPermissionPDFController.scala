/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import config.FrontendAppConfig
import connectors.ArrivalMovementConnector
import controllers.actions.IdentifierAction
import models.ArrivalId
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class UnloadingPermissionPDFController @Inject()(
  identify: IdentifierAction,
  cc: MessagesControllerComponents,
  arrivalMovementConnector: ArrivalMovementConnector,
  val config: FrontendAppConfig,
  val renderer: Renderer
)(implicit ec: ExecutionContext, appConfig: FrontendAppConfig)
    extends FrontendController(cc)
    with I18nSupport
    with TechnicalDifficultiesPage {

  def getPDF(arrivalId: ArrivalId): Action[AnyContent] = identify.async {
    implicit request =>
      hc.authorization
        .map {
          token =>
            arrivalMovementConnector.getPDF(arrivalId, token.value).flatMap {
              result =>
                result.status match {
                  case OK =>
                    Future.successful(Ok(result.bodyAsBytes.toArray))
                  case _ =>
                    renderTechnicalDifficultiesPage
                }
            }
        }
        .getOrElse {
          Future.successful(Redirect(controllers.routes.UnauthorisedController.onPageLoad()))
        }
  }
}
