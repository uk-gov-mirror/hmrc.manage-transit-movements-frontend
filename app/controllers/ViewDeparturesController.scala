/*
 * Copyright 2020 HM Revenue & Customs
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
import connectors.{ArrivalMovementConnector, DeparturesMovementConnector, DeparturesMovementConnectorTemp}
import controllers.actions._
import javax.inject.Inject
import models.Departure
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import viewModels.{ViewDeparture, ViewDepartureMovements}

import scala.concurrent.ExecutionContext

class ViewDeparturesController @Inject()(
  override val messagesApi: MessagesApi,
  identify: IdentifierAction,
  val controllerComponents: MessagesControllerComponents,
  connector: DeparturesMovementConnectorTemp, //TODO: Switch this once we're calling stubs/backend
  renderer: Renderer
)(implicit ec: ExecutionContext, frontendAppConfig: FrontendAppConfig)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] = identify.async {
    implicit request =>
      connector.get().flatMap {
        allDepartures =>
          val viewDepartures: Seq[ViewDeparture] = allDepartures.departures.map((departure: Departure) => ViewDeparture(departure))
          val formatToJson: JsObject             = Json.toJsObject(ViewDepartureMovements.apply(viewDepartures))

          renderer.render("viewDepartures.njk", formatToJson).map(Ok(_))
      }
  }
}