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

package connectors.testOnly

import config.FrontendAppConfig
import javax.inject.Inject
import play.api.mvc.Headers
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}
import scala.xml.NodeSeq

class TestOnlyRouterConnector @Inject()(
  val http: HttpClient,
  config: FrontendAppConfig
)(implicit ec: ExecutionContext) {

  def sendMessage(
    requestData: NodeSeq,
    headers: Headers
  )(implicit hc: HeaderCarrier): Future[HttpResponse] = {

    val serviceUrl =
      s"${config.routerUrl}/messages"

    // TODO: Determine which headers need to be sent on
    http.POSTString[HttpResponse](
      serviceUrl,
      requestData.toString,
      headers.headers
    )
  }
}
