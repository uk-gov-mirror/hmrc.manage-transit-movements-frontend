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

import akka.util.ByteString
import config.FrontendAppConfig
import javax.inject.Inject
import play.api.mvc.RawBuffer
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import play.api.libs.json.DefaultWrites

import scala.concurrent.{ExecutionContext, Future}

class TestOnlyCustomsReferenceDataConnector @Inject()(val http: HttpClient, config: FrontendAppConfig)(implicit ec: ExecutionContext) {

  def referenceDataList(body: RawBuffer)(implicit hc: HeaderCarrier): Future[HttpResponse] = {

    val serviceUrl = s"${config.customsReferenceDataUrl}/reference-data-lists"

    implicit val rawReads: HttpReads[HttpResponse] = HttpReads.readRaw

    http.POST(serviceUrl, body.asBytes().getOrElse(ByteString.empty))
  }

//  def customsOfficeList(body: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
//
//    val serviceUrl = s"${config.customsReferenceDataUrl}/customs-office-lists/customs-office-lists"
//
//    http.POSTString[HttpResponse](serviceUrl, body)(rds = HttpReads.readRaw, hc, ec)
//  }

}
