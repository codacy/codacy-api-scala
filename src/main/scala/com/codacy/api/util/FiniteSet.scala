package com.codacy.api.util

sealed abstract class FiniteSet[E](val values: Set[E]) {
  def byName(name: String): Option[E] = values.find(_.toString == name)
}

sealed abstract class FiniteSetName[E](val value: String)

trait EnumValueAwareness {
  self: Enumeration with Product =>

  implicit object finiteSet extends FiniteSet[Value](self.values)

  implicit object finiteSetName extends FiniteSetName[Value](self.productPrefix)

}
