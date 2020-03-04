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

package connectors

import base.SpecBase
import helper.WireMockServerHandler
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import com.github.tomakehurst.wiremock.client.WireMock._
import models.referenceData.CustomsOffice
import org.scalacheck.Gen
import org.scalatest.exceptions.TestFailedException
import play.api.libs.json.JsResult

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.test.Helpers._

import scala.concurrent.Future

class ReferenceDataConnectorSpec extends SpecBase with WireMockServerHandler with ScalaCheckPropertyChecks {

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      conf = "microservice.services.reference-data.port" -> server.port()
    )
    .build()

  private val startUrl                               = "transit-movements-trader-reference-data"
  private val customsOfficeId                        = "123"
  private lazy val connector: ReferenceDataConnector = app.injector.instanceOf[ReferenceDataConnector]

  private val customsOfficeResponseJson: String =
    """
      | {
      |   "id" : "testId1",
      |   "name" : "testName1",
      |   "roles" : ["role1", "role2"],
      |   "phoneNumber" : "testPhoneNumber"
      | }
      |""".stripMargin

  "ReferenceDataConnector" - {

    "getCustomsOffice" - {

      "must return CustomsOffice wrapped in a Some for an Ok" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/customs-office/$customsOfficeId"))
            .willReturn(okJson(customsOfficeResponseJson))
        )

        val expectedResult = CustomsOffice("testId1", "testName1", Seq("role1", "role2"), Some("testPhoneNumber"))

        val result = connector.getCustomsOffice(customsOfficeId).futureValue.value.asOpt.value

        result mustBe expectedResult
      }

      "must return a None for a Not_Found Status" in {
        server.stubFor(
          get(urlEqualTo(s"/$startUrl/customs-office/$customsOfficeId"))
            .willReturn(
              aResponse()
                .withStatus(NOT_FOUND)
            )
        )

        val result = connector.getCustomsOffice(customsOfficeId)

        result.futureValue must not be (defined)

      }

      "must return an Exception when a CustomsOffice cannot be found" in {
        val errorResponses = Gen.chooseNum(400, 599).suchThat(_ != NOT_FOUND)

        forAll(errorResponses) {
          errorResponse =>
            server.stubFor(
              get(urlEqualTo(s"/$startUrl/customs-office/$customsOfficeId"))
                .willReturn(
                  aResponse()
                    .withStatus(errorResponse)
                )
            )

            val result = connector.getCustomsOffice(customsOfficeId)

            result.futureValue must not be (defined)
        }
      }
    }
  }

}
