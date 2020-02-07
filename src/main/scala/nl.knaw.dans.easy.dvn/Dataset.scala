package nl.knaw.dans.easy.dvn

import java.io.PrintStream
import java.net.URI

import better.files.File
import nl.knaw.dans.lib.logging.DebugEnhancedLogging

import scala.util.{ Failure, Try }

class Dataset(id: String, isPersistentId: Boolean, configuration: Configuration)(implicit resultOutput: PrintStream) extends HttpSupport with DebugEnhancedLogging {
  protected val connectionTimeout: Int = configuration.connectionTimeout
  protected val readTimeout: Int = configuration.readTimeout
  protected val baseUrl: URI = configuration.baseUrl
  protected val apiToken: String = configuration.apiToken
  protected val apiVersion: String = configuration.apiVersion

  def view(version: Option[String] = None): Try[String] = {
    if(isPersistentId) get(s"datasets/:persistentId/${version.map(v => s"versions/$v/").getOrElse("")}?persistentId=$id")
    else get(s"datasets/$id/${version.map(v => s"versions/$v/").getOrElse("")}")
  }

  def delete(): Try[String] = {
    if(isPersistentId) deletePath(s"datasets/:persistentId/?persistentId=$id")
    else deletePath(s"datasets/$id")
  }

  def listVersions(): Try[String] = {
    if(isPersistentId) get(s"datasets/:persistentId/versions?persistentId=$id")
    else get(s"datasets/$id/versions")
  }

  def exportMetadataTo(format: String): Try[String] = {
    if(isPersistentId) get(s"datasets/export?persistentId=$id&exporter=$format", formatResponseAsJson = false)
    else Failure(CommandFailedException(501, "Export to metadata is only supported using persistent identifiers. Use the -p option", null))
  }

  def listFiles(version: Option[String] = None): Try[String] = {
    if(isPersistentId) get(s"datasets/:persistentId/${version.map(v => s"versions/$v/").getOrElse("")}files?persistentId=$id")
    else get(s"datasets/$id/${version.map(v => s"versions/$v/").getOrElse("")}/files")
  }

  def listMetadataBlocks(version: Option[String] = None, name: Option[String]): Try[String] = {
    if(isPersistentId) get(s"datasets/:persistentId/${version.map(v => s"versions/$v/").getOrElse("")}metadata/${ name.getOrElse( "" )}?persistentId=$id")
    else get(s"datasets/$id/${version.map(v => s"versions/$v/").getOrElse("")}/metadata/${ name.getOrElse( "" )}")
  }

  def updateMetadata(json: File, version: Option[String] = None): Try[String] = {
    val path = if(isPersistentId) s"datasets/:persistentId/${version.map(v => s"versions/$v/").getOrElse("")}?persistentId=$id"
              else s"datasets/$id/${version.map(v => s"versions/$v/").getOrElse("")}/"
    tryReadFileToString(json).flatMap(put(path))
  }

  def editMetadata(json: File, replace: Boolean = false): Try[String] = {
    val path = if(isPersistentId) s"datasets/:persistentId/editMetadata/?persistentId=$id${ if(replace) "&replace=$replace" else "" }"
               else s"datasets/$id/editMetadata/${ if(replace) "?replace=$replace" else "" }"
    tryReadFileToString(json).flatMap(put(path))
  }

  def deleteMetadata(json: File): Try[String] = {
    val path = if(isPersistentId) s"datasets/:persistentId/deleteMetadata/?persistentId=$id"
               else s"datasets/$id/deleteMetadata"
    tryReadFileToString(json).flatMap(put(path))
  }

  def publish(updateType: String): Try[String] = {
    val path = if(isPersistentId) s"datasets/:persistentId/actions/:publish/?persistentId=$id&type=$updateType"
               else s"datasets/$id/actions/:publish?type=$updateType"
    postJson(path)(200, 202)(null)
  }

  def deleteDraft(): Try[String] = {
    val path = if(isPersistentId) s"datasets/:persistentId/versions/:draft/?persistentId=$id"
              else s"datasets/$id/versions/:draft/"
    deletePath(path)
  }

  def setCitationDateField(fieldName: String): Try[String] = {
    val path = if(isPersistentId) s"datasets/:persistentId/citationdate?persistentId=$id"
               else s"datasets/$id/citationdate"
    put(path)(s"$fieldName")
  }

}
