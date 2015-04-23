package com.codacy.api.client

import play.api.libs.json.{Reads, _}

case class RequestSuccess(message: String)

object RequestSuccess {
  implicit val reader: Reads[RequestSuccess] =
    (__ \ "success").read[String].map(RequestSuccess.apply)
}
