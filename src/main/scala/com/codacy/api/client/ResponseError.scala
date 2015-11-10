package com.codacy.api.client

import play.api.libs.json._

case class ResponseError(error: String)

object ResponseError {
  implicit val formatter = Json.format[ResponseError]
}
