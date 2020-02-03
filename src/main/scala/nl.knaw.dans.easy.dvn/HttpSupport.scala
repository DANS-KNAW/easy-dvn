/**
 * Copyright (C) 2020 DANS - Data Archiving and Networked Services (info@dans.knaw.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.knaw.dans.easy.dvn

import java.net.URI

import com.google.gson.{ GsonBuilder, JsonParser }
import nl.knaw.dans.lib.logging.DebugEnhancedLogging
import scalaj.http.{ Http, HttpResponse }

import scala.util.{ Failure, Success, Try }

trait HttpSupport extends DebugEnhancedLogging{
  protected val connectionTimeout: Int
  protected val readTimeout: Int
  protected val baseUrl: URI
  protected val apiToken: String
  private val gson = new GsonBuilder().setPrettyPrinting().create()

  protected def http(method: String, uri: URI, body: String = null, headers: Map[String, String] = Map.empty[String, String]): Try[HttpResponse[Array[Byte]]] = Try {
    {
      if (body == null) Http(uri.toASCIIString)
      else Http(uri.toASCIIString).postData(body)
      }.method(method)
      .headers(headers)
      .timeout(connTimeoutMs = connectionTimeout, readTimeoutMs = readTimeout)
      .asBytes
  }

  protected def handleResponse(response: HttpResponse[Array[Byte]], expectedStatus: Int): Try[Array[Byte]] = {
    trace(expectedStatus)
    if (response.code != expectedStatus) Failure(CommandFailedException(response.code, response.statusLine, new String(response.body)))
    else Success(response.body)
  }

  protected def prettyPrintJson(json: String): Try[String] = Try {
    trace()
    val parsedJson = JsonParser.parseString(json)
    gson.toJson(parsedJson)
  }

  def uri(s: String): Try[URI] = Try {
    baseUrl resolve s
  }
}
