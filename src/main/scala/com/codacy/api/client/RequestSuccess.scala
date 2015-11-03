package com.codacy.api.client

import play.api.libs.json._

case class RequestSuccess(message: String)

object RequestSuccess {
  implicit val formatter = Json.format[RequestSuccess]
}
