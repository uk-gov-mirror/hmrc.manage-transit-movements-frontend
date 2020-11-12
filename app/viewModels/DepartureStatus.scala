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

package viewModels

import models.{Departure, ViewMovementAction}

case class DepartureStatus(status: String, actions: Seq[ViewMovementAction])

object DepartureStatus {

  type DepartureStatusViewModel = PartialFunction[Departure, DepartureStatus]

  def apply(departure: Departure): DepartureStatus = {
    val partialFunctions: PartialFunction[Departure, DepartureStatus] =
      Seq(
        mrnAllocated,
        departureSubmitted,
        positiveAcknowledgement,
        releasedForTransit,
        transitDeclarationRejected,
        departureDeclarationReceived,
        guaranteeValidationFail,
        transitDeclarationSent,
        writeOffNotification,
        invalidStatus
      ).reduce(_ orElse _)
    partialFunctions.apply(departure)
  }
  
  private def downloadTADAction(departure: Departure) = ViewMovementAction("", "departure.downloadTAD")

  private def mrnAllocated: PartialFunction[Departure, DepartureStatus] = {
    case departure if departure.status == "MrnAllocated" =>
      DepartureStatus("departure.status.mrnAllocated", Nil)
  }

  private def departureSubmitted: DepartureStatusViewModel = {
    case departure if departure.status == "DepartureSubmitted" =>
      DepartureStatus("departure.status.submitted", actions = Nil)
  }

  private def positiveAcknowledgement: DepartureStatusViewModel = {
    case departure if departure.status == "PositiveAcknowledgement" =>
      DepartureStatus("departure.status.positiveAcknowledgement", actions = Nil)
  }

  private def releasedForTransit: DepartureStatusViewModel = {
    case departure if departure.status == "ReleasedForTransit" =>
      DepartureStatus("departure.status.releasedForTransit", actions = Seq(downloadTADAction(departure)))
  }

  private def transitDeclarationRejected: DepartureStatusViewModel = {
    case departure if departure.status == "TransitDeclarationRejected" =>
      DepartureStatus("departure.status.transitDeclarationRejected", actions = Nil)
  }

  private def departureDeclarationReceived: DepartureStatusViewModel = {
    case departure if departure.status == "DepartureDeclarationReceived" =>
      DepartureStatus("departure.status.departureDeclarationReceived", actions = Nil)
  }

  private def guaranteeValidationFail: DepartureStatusViewModel = {
    case departure if departure.status == "GuaranteeValidationFail" =>
      DepartureStatus("departure.status.guaranteeValidationFail", actions = Nil)
  }

  private def transitDeclarationSent: DepartureStatusViewModel = {
    case departure if departure.status == "TransitDeclarationSent" =>
      DepartureStatus("departure.status.transitDeclarationSent", actions = Nil)
  }

  private def writeOffNotification: DepartureStatusViewModel = {
    case departure if departure.status == "WriteOffNotification" =>
      DepartureStatus("departure.status.writeOffNotification", actions = Nil)
  }

  private def invalidStatus: DepartureStatusViewModel = {
    case departure => DepartureStatus(departure.status, actions = Nil)
  }
}
