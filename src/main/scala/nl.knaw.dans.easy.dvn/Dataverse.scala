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
import org.apache.commons.io.FileUtils

import scala.util.Try

class Dataverse(dvId: String, configuration: Configuration) extends HttpSupport with DebugEnhancedLogging {
  trace(dvId)
  protected val connectionTimeout: Int = configuration.connectionTimeout
  protected val readTimeout: Int = configuration.readTimeout
  protected val baseUrl: URI = configuration.baseUrl
  protected val apiToken: String = configuration.apiToken


  /*
   * Helpers
   */
  private def get(subPath: String = null): Try[String] = {
    for {
      uri <- uri(s"api/v${ configuration.apiVersion }/dataverses/$dvId/${ Option(subPath).getOrElse("") }")
      _ = debug(s"Request URL = $uri")
      response <- http("GET", uri, body = null, Map("X-Dataverse-key" -> apiToken))
      body <- handleResponse(response, 200)
      prettyJson <- prettyPrintJson(new String(body))
    } yield prettyJson
  }

  private def postJson(subPath: String = null)(expectedStatus: Int = 201)(body: String = null): Try[String] = {
    for {
      uri <- uri(s"api/v${ configuration.apiVersion }/dataverses/$dvId/${ Option(subPath).getOrElse("") }")
      _ = debug(s"Request URL = $uri")
      response <- http("POST", uri, body, Map("Content-Type" -> "application/json", "X-Dataverse-key" -> apiToken))
      _ <- handleResponse(response, expectedStatus)
    } yield s"Successfully POSTed: $body"
  }

  private def put(subPath: String = null)(body: String = null): Try[String] = {
    for {
      uri <- uri(s"api/v${ configuration.apiVersion }/dataverses/$dvId/${ Option(subPath).getOrElse("") }")
      _ = debug(s"Request URL = $uri")
      response <- http("PUT", uri, body, Map("X-Dataverse-key" -> apiToken))
      _ <- handleResponse(response, 200)
    } yield s"Successfully PUT to $uri"
  }

  private def deletePath(subPath: String = null): Try[String] = {
    for {
      uri <- uri(s"api/v${ configuration.apiVersion }/dataverses/$dvId/${ Option(subPath).getOrElse("") }")
      _ = debug(s"Request URL = $uri")
      response <- http("DELETE", uri, null, Map("X-Dataverse-key" -> apiToken))
      _ <- handleResponse(response, 200)
    } yield s"Successfully DELETED: $uri"
  }

  private def tryReadFileToString(file: File): Try[String] = Try {
    FileUtils.readFileToString(file.toJava, StandardCharsets.UTF_8)
  }

  /*
   * Operations
   */
  def create(jsonDef: File): Try[String] = {
    trace(jsonDef)
    tryReadFileToString(jsonDef).flatMap(postJson()(201))
  }

  def view(): Try[String] = {
    trace(())
    get()
  }

  def delete(): Try[String] = {
    trace(())
    deletePath()
  }

  def show(): Try[String] = {
    trace(())
    get("contents")
  }

  def listRoles(): Try[String] = {
    trace(())
    get("roles")
  }

  def createRole(jsonDef: File): Try[String] = {
    trace(jsonDef)
    tryReadFileToString(jsonDef).flatMap(postJson("roles")(200))
  }

  def listFacets(): Try[String] = {
    trace(())
    get("facets")
  }

  def setFacets(facets: Seq[String]): Try[String] = {
    trace(facets)
    postJson("facets")(200)(facets.map(s => s""""$s"""").mkString("[", ",", "]"))
  }

  def listRoleAssignments(): Try[String] = {
    trace(())
    get("assignments")
  }

  // TODO: find out why it doesn't work
  def setDefaultRole(role: String): Try[String] = {
    trace(role)
    put(s"defaultContributorRole/$role")(null)
  }

  def setRole(jsonDef: File): Try[String] = {
    trace(jsonDef)
    tryReadFileToString(jsonDef).flatMap(postJson("assignments")(200))
  }

  def deleteRole(roleId: String): Try[String] = {
    trace(roleId)
    deletePath(s"assignments/$roleId")
  }

  def listMetadataBocks(): Try[String] = {
    trace(())
    get("metadatablocks")
  }

  def setMetadataBlocks(mdBlockIds: Seq[String]): Try[String] = {
    trace(mdBlockIds)
    postJson("metadatablocks")(200)(mdBlockIds.map(s => s""""$s"""").mkString("[", ",", "]"))
  }

  def isMetadataBlocksRoot: Try[String] = {
    trace(())
    get("metadatablocks/isRoot")
  }

  def setMetadataBlocksRoot(isRoot: Boolean): Try[String] = {
    trace(isRoot)
    put("metadatablocks/isRoot")(isRoot.toString.toLowerCase)
  }

  def createDataset(json: File): Try[String] = {
    trace(json)
    tryReadFileToString(json).flatMap(postJson("datasets")(201))
  }

  def importDataset(importFile: File, isDdi: Boolean = false, pid: String, keepOnDraft: Boolean = false): Try[String] = {
    trace(importFile)
    tryReadFileToString(importFile).flatMap(postJson(s"datasets/:import${ if(isDdi) "ddi" else "" }?pid=$pid&release=${!keepOnDraft}")(201))
  }
  def publish(): Try[String] = {
    trace(())
    postJson("actions/:publish")(200)()
  }


}
