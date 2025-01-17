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

import java.time.LocalDateTime

import base.SpecBase
import config.FrontendAppConfig
import connectors.ArrivalMovementConnector
import generators.Generators
import matchers.JsonMatchers
import models.{Arrival, ArrivalId, Arrivals}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatest.BeforeAndAfter
import org.scalatestplus.mockito.MockitoSugar
import play.api.Application
import play.api.inject.bind
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.viewmodels.NunjucksSupport
import viewModels.{ViewArrivalMovements, ViewMovement}

import scala.concurrent.Future

class ViewArrivalsControllerSpec extends SpecBase with MockitoSugar with JsonMatchers with Generators with NunjucksSupport with BeforeAndAfter {

  private val mockArrivalMovementConnector = mock[ArrivalMovementConnector]

  val localDateTime: LocalDateTime = LocalDateTime.now()

  private val application: Application =
    applicationBuilder(userAnswers = Some(emptyUserAnswers))
      .overrides(
        bind[ArrivalMovementConnector].toInstance(mockArrivalMovementConnector)
      )
      .build()

  private val mockArrivalResponse: Arrivals = {
    Arrivals(
      Seq(
        Arrival(
          ArrivalId(1),
          localDateTime,
          localDateTime,
          "Submitted",
          "test mrn"
        )
      )
    )
  }

  implicit val appConfig: FrontendAppConfig = frontendAppConfig

  private val mockViewMovement = ViewMovement(
    localDateTime.toLocalDate,
    localDateTime.toLocalTime,
    "test mrn",
    "Submitted",
    Nil
  )

  private val expectedJson: JsValue =
    Json.toJsObject(
      ViewArrivalMovements(Seq(mockViewMovement))
    ) ++ Json.obj(
      "declareArrivalNotificationUrl" -> appConfig.declareArrivalNotificationStartUrl,
      "homePageUrl"                   -> routes.IndexController.onPageLoad().url
    )

  "ViewArrivalNotifications Controller" - {
    "return OK and the correct view for a GET" in {

      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockArrivalMovementConnector.getArrivals()(any()))
        .thenReturn(Future.successful(Some(mockArrivalResponse)))

      val request = FakeRequest(
        GET,
        routes.ViewArrivalsController.onPageLoad().url
      )

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value
      status(result) mustEqual OK

      verify(mockRenderer, times(1))
        .render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val jsonCaptorWithoutConfig: JsObject = jsonCaptor.getValue - configKey

      templateCaptor.getValue mustEqual "viewArrivals.njk"
      jsonCaptorWithoutConfig mustBe expectedJson

      application.stop()
    }

    "render technical difficulty" in {

      val config = app.injector.instanceOf[FrontendAppConfig]
      when(mockRenderer.render(any(), any())(any()))
        .thenReturn(Future.successful(Html("")))

      when(mockArrivalMovementConnector.getArrivals()(any()))
        .thenReturn(Future.successful(None))

      val request = FakeRequest(
        GET,
        routes.ViewArrivalsController.onPageLoad().url
      )

      val templateCaptor = ArgumentCaptor.forClass(classOf[String])
      val jsonCaptor     = ArgumentCaptor.forClass(classOf[JsObject])

      val result = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

      verify(mockRenderer, times(1)).render(templateCaptor.capture(), jsonCaptor.capture())(any())

      val expectedJson = Json.obj {
        "contactUrl" -> config.nctsEnquiriesUrl
      }

      templateCaptor.getValue mustEqual "technicalDifficulties.njk"
      jsonCaptor.getValue must containJson(expectedJson)

      application.stop()
    }
  }

  override def beforeEach: Unit = {
    reset(mockArrivalMovementConnector)
    super.beforeEach
  }
}
