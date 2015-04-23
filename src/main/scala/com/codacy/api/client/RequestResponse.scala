package com.codacy.api.client

case class RequestResponse[T](value: Option[T], message: String = "", hasError: Boolean = false)
