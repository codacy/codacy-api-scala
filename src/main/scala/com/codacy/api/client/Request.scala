package com.codacy.api.client

class Request[T] private (val endpoint: String, val classType: Class[T], val queryParameters: Map[String, String])
object Request {

  def apply[T](endpoint: String, classType: Class[T], queryParameters: Map[String, String] = Map.empty): Request[T] = {
    val sanitizedEndpoint = endpoint.replaceAll(" ", "%20")
    new Request[T](sanitizedEndpoint, classType, queryParameters)
  }
}
