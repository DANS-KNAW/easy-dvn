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
import java.nio.charset.StandardCharsets

import better.files.File
import nl.knaw.dans.lib.logging.DebugEnhancedLogging
import org.apache.commons.io.{ FileUtils, IOUtils }

import scala.util.{ Failure, Success, Try }

class Dataverse(configuration: Configuration) extends HttpSupport with DebugEnhancedLogging {
  protected val connectionTimeout: Int = configuration.connectionTimeout
  protected val readTimeout: Int = configuration.readTimeout
  protected val baseUrl: URI = configuration.baseUrl
  protected val apiToken: String = configuration.apiToken

  /*
   * Helpers
   */
  private def get(id: String, subPath: String = null): Try[String] = {
    for {
      uri <- uri(s"api/v${ configuration.apiVersion }/dataverses/$id/${ Option(subPath).getOrElse("") }")
      _ = debug(s"Request URL = $uri")
      response <- http("GET", uri, body = null , Map("X-Dataverse-key"-> apiToken))
      body <- handleResponse(response, 200)
      prettyJson <- prettyPrintJson(new String(body))
    } yield prettyJson
  }

  private def postJson(id: String, subPath: String = null)(expectedStatus: Int = 201)(body: String = null): Try[String] = {
    for {
      uri <- uri(s"api/v${ configuration.apiVersion }/dataverses/$id/${ Option(subPath).getOrElse("") }")
      _ = debug(s"Request URL = $uri")
      response <- http("POST", uri, body, Map("Content-Type" -> "application/json", "X-Dataverse-key"-> apiToken))
      _ <- handleResponse(response, expectedStatus)
    } yield s"Successfully POSTed: $body"
  }

  private def put(id: String, subPath: String = null): Try[String] = {
    for {
      uri <- uri(s"api/v${ configuration.apiVersion }/dataverses/$id/${ Option(subPath).getOrElse("") }")
      _ = debug(s"Request URL = $uri")
      response <- http("PUT", uri, null, Map("X-Dataverse-key"-> apiToken))
      _ <- handleResponse(response, 200)
    } yield s"Successfully PUT to $uri"

  }

  private def deleteUri(id: String, subPath: String = null): Try[String] = {
    for {
      uri <- uri(s"api/v${ configuration.apiVersion }/dataverses/$id/${ Option(subPath).getOrElse("") }")
      _ = debug(s"Request URL = $uri")
      response <- http("DELETE", uri, null,  Map("X-Dataverse-key"-> apiToken))
      _ <- handleResponse(response, 200)
    } yield s"Successfully DELETED: $uri"
  }

  private def tryReadFileToString(file: File): Try[String] = Try {
     FileUtils.readFileToString(file.toJava, StandardCharsets.UTF_8)
  }

  /*
   * Operations
   */
  def create(parentId: String, jsonDef: File): Try[String] = {
    trace(jsonDef)
    tryReadFileToString(jsonDef).flatMap(postJson(parentId)(201))
  }

  def view(id: String): Try[String] = {
    trace(id)
    get(id)
  }

  def delete(id: String): Try[String] = {
    trace(id)
    deleteUri(id)
  }

  def show(id: String): Try[String] = {
    trace(id)
    get(id, "contents")
  }

  def listRoles(id: String): Try[String] = {
    trace(id)
    get(id, "roles")
  }

  def createRole(id: String, jsonDef: File): Try[String] = {
    trace(jsonDef)
    tryReadFileToString(jsonDef).flatMap(postJson(id, "roles")(200))
  }

  def listFacets(id: String): Try[String] = {
    trace(id)
    get(id, "facets")
  }

  def setFacets(id: String, facets: Seq[String]): Try[String] = {
    trace(facets)
    postJson(id, "facets")(200)(facets.map(s => s""""$s"""").mkString("[", ",", "]"))
  }

  def listRoleAssignments(id: String): Try[String] = {
    trace(id)
    get(id, "assignments")
  }

  def setDefaultRole(id: String, role: String): Try[String] = {
    trace(id, role)
    put(id, s"defaultContributorRole/$role")
  }
}
