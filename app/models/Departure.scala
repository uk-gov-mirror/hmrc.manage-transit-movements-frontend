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

package models

import java.time.LocalDateTime

import play.api.libs.functional.syntax._
import play.api.libs.json.{__, Json, Reads, Writes}

case class Departure(departureId: DepartureId, created: LocalDateTime, localReferenceNumber: LocalReferenceNumber, status: String)

object Departure {
  implicit val reads: Reads[Departure] = (
    (__ \ "departureId").read[DepartureId] and
      (__ \ "created").read[LocalDateTime] and
      (__ \ "referenceNumber").read[LocalReferenceNumber] and
      (__ \ "status").read[String]
  )(Departure.apply _)

  implicit val writes: Writes[Departure] = Json.writes[Departure]
}

case class Departures(departures: Seq[Departure])

object Departures {
  implicit val format: Reads[Departures]  = Json.reads[Departures]
  implicit val writes: Writes[Departures] = Json.writes[Departures]
}
