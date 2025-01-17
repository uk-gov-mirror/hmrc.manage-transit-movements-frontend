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

package controllers.testOnly

import config.FrontendAppConfig
import connectors.DeparturesMovementConnector
import controllers.actions.IdentifierAction
import models.DepartureId
import play.api.Logger.logger
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import renderer.Renderer
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AccompanyingDocumentPDFController @Inject()(
  identify: IdentifierAction,
  cc: MessagesControllerComponents,
  departuresMovementConnector: DeparturesMovementConnector)(implicit ec: ExecutionContext, appConfig: FrontendAppConfig, renderer: Renderer)
    extends FrontendController(cc)
    with I18nSupport {

  def getPDF(departureId: DepartureId): Action[AnyContent] = identify.async {
    implicit request =>
      departuresMovementConnector.getPDF(departureId).flatMap {
        result =>
          result.status match {
            case OK =>
              Future.successful(Ok(result.bodyAsBytes.toArray))
            case _ =>
              logger.error(s"failed to download TAD pdf received status code ${result.status}")
              val json = Json.obj("nctsEnquiries" -> appConfig.nctsEnquiriesUrl)
              renderer.render("technicalDifficulties.njk", json).map(InternalServerError(_))
          }
      }
  }
}
