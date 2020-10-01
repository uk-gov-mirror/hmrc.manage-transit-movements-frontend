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

import java.io.File

import connectors.testOnly.TestOnlyCustomsReferenceDataConnector
import javax.inject.Inject
import play.api.mvc.{Action, ControllerComponents, Request}
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext

class TestOnlyCustomsReferenceDataController @Inject()(
  cc: ControllerComponents,
  connector: TestOnlyCustomsReferenceDataConnector
)(implicit val ec: ExecutionContext)
    extends BackendController(cc) {

  def post(): Action[File] =
    Action(parse.file(to = new File("/tmp/test.gz"))).async {
      request: Request[File] =>
        {
          connector.post(request.body).map {
            result =>
              result.status match {
                case Accepted => Accepted
                case _        => BadRequest(s"Failed: ${result.status} - ${result.body}")
              }
          }
        }
    }
}