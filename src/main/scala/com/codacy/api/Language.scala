package com.codacy.api

import com.codacy.api.util.EnumUtils
import play.api.libs.json.Format

object Language extends Enumeration {
  val
  CSS,
  Java,
  JavaScript,
  PHP,
  Python,
  Scala,
  Ruby
  = Value

  implicit val languageFmt: Format[Language.Value] = EnumUtils.enumFormat(Language)

}
