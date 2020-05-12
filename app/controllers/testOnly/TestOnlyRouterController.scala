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

package controllers.testOnly

import connectors.testOnly.TestOnlyRouterConnector
import javax.inject.Inject
import play.api.mvc.{Action, ControllerComponents, DefaultActionBuilder}
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext
import scala.xml.NodeSeq

class TestOnlyRouterController @Inject()(
  cc: ControllerComponents,
  connector: TestOnlyRouterConnector,
  action: DefaultActionBuilder
)(implicit val ec: ExecutionContext)
    extends BackendController(cc) {

  def fromCoreMessage: Action[NodeSeq] = action.async(parse.xml) {
    implicit request =>
      connector
        .submitInboundMessage(request.body, request.headers)
        .map(response => Status(response.status))
  }

  def toCoreMessage: Action[NodeSeq] = action.async(parse.xml) {
    implicit request =>
      connector
        .submitOutboundMessage(request.body, request.headers)
        .map(response => Status(response.status))
  }
}