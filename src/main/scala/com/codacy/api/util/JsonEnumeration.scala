package com.codacy.api.util

import rapture.data.{Extractor, Serializer}
import rapture.json.Json

trait JsonEnumeration extends Enumeration {
  self: Enumeration =>

  implicit def enumSerializer[E <: Enumeration#Value](implicit serializer: Serializer[String, Json]): Serializer[E, Json] = {
    Json.serializer[String].contramap[E](_.toString)
  }

  implicit def EnumExtractor[E <: Enumeration#Value](implicit set: FiniteSet[E], name: FiniteSetName[E], strExt: Extractor[String, Json]): Extractor[E, Json] = {
    strExt.map { raw =>
      set.byName(raw).getOrElse(throw new Exception(s"$raw is not a valid $name"))
    }
  }

}
