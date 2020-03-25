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
import connectors.DestinationConnector
import controllers.actions.IdentifierAction
import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import renderer.Renderer
import services.ViewMovementConversionService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import viewModels.{ViewArrivalMovements, ViewMovement}

import scala.concurrent.{ExecutionContext, Future}

class ViewArrivalNotificationsController @Inject()(
  renderer: Renderer,
  identify: IdentifierAction,
  val controllerComponents: MessagesControllerComponents,
  destinationConnector: DestinationConnector,
  customOfficeLookupService: ViewMovementConversionService
)(implicit ec: ExecutionContext, appConfig: FrontendAppConfig)
    extends FrontendBaseController
    with I18nSupport {

  def onPageLoad: Action[AnyContent] = identify.async {
    implicit request =>
      destinationConnector.getMovements().flatMap {
        movements =>
          val viewMovements: Seq[ViewMovement] = movements.map(customOfficeLookupService.convertToViewMovements)
          val formatToJson: JsObject           = Json.toJsObject(ViewArrivalMovements.apply(viewMovements))

          renderer
            .render("viewArrivalNotifications.njk", formatToJson)
            .map(Ok(_))
      }
  }
}
