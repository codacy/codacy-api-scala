package com.codacy.api.util

import play.api.libs.json.{JsError, JsPath, Json, JsonValidationError}

object JsonOps {
  def handleDerivationFailure(error: Seq[(JsPath, Seq[JsonValidationError])]): String = {
    Json.stringify(JsError.toJson(error.toList))
  }
}
