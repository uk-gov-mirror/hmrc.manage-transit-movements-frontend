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
import javax.inject.Inject
import models.Arrival
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import viewModels.{ViewArrivalMovements, ViewMovement}

import scala.concurrent.{ExecutionContext, Future}

class ViewArrivalsController @Inject()(val renderer: Renderer,
                                       identify: IdentifierAction,
                                       cc: MessagesControllerComponents,
                                       val config: FrontendAppConfig,
                                       arrivalMovementConnector: ArrivalMovementConnector)(implicit ec: ExecutionContext, appConfig: FrontendAppConfig)
    extends FrontendController(cc)
    with I18nSupport
    with TechnicalDifficultiesPage {

  def onPageLoad: Action[AnyContent] = identify.async {
    implicit request =>
      arrivalMovementConnector.getArrivals().flatMap {
        case Some(allArrivals) =>
          val viewMovements: Seq[ViewMovement] = allArrivals.arrivals.map((arrival: Arrival) => ViewMovement(arrival))
          val formatToJson: JsObject           = Json.toJsObject(ViewArrivalMovements.apply(viewMovements))

          renderer
            .render("viewArrivals.njk", formatToJson)
            .map(Ok(_))

        case _ => renderTechnicalDifficultiesPage
      }
  }
}
