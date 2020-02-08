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
    trace(())
    if(isPersistentId) get(s"datasets/:persistentId/${version.map(v => s"versions/$v/").getOrElse("")}?persistentId=$id")
    else get(s"datasets/$id/${version.map(v => s"versions/$v/").getOrElse("")}")
  }

  def delete(): Try[String] = {
    trace(())
    if(isPersistentId) deletePath(s"datasets/:persistentId/?persistentId=$id")
    else deletePath(s"datasets/$id")
  }

  def listVersions(): Try[String] = {
    trace(())
    if(isPersistentId) get(s"datasets/:persistentId/versions?persistentId=$id")
    else get(s"datasets/$id/versions")
  }

  def exportMetadataTo(format: String): Try[String] = {
    trace(format)
    if(isPersistentId) get(s"datasets/export?persistentId=$id&exporter=$format", formatResponseAsJson = false)
    else Failure(CommandFailedException(501, "Export to metadata is only supported using persistent identifiers. Use the -p option", null))
  }

  def listFiles(version: Option[String] = None): Try[String] = {
    trace(version)
    if(isPersistentId) get(s"datasets/:persistentId/${version.map(v => s"versions/$v/").getOrElse("")}files?persistentId=$id")
    else get(s"datasets/$id/${version.map(v => s"versions/$v/").getOrElse("")}/files")
  }

  def listMetadataBlocks(version: Option[String] = None, name: Option[String]): Try[String] = {
    trace((version))
    if(isPersistentId) get(s"datasets/:persistentId/${version.map(v => s"versions/$v/").getOrElse("")}metadata/${ name.getOrElse( "" )}?persistentId=$id")
    else get(s"datasets/$id/${version.map(v => s"versions/$v/").getOrElse("")}/metadata/${ name.getOrElse( "" )}")
  }

  def updateMetadata(json: File, version: Option[String] = None): Try[String] = {
    trace(json, version)
    val path = if(isPersistentId) s"datasets/:persistentId/${version.map(v => s"versions/$v/").getOrElse("")}?persistentId=$id"
              else s"datasets/$id/${version.map(v => s"versions/$v/").getOrElse("")}/"
    tryReadFileToString(json).flatMap(put(path))
  }

  def editMetadata(json: File, replace: Boolean = false): Try[String] = {
    trace(json, replace)
    val path = if(isPersistentId) s"datasets/:persistentId/editMetadata/?persistentId=$id${ if(replace) "&replace=$replace" else "" }"
               else s"datasets/$id/editMetadata/${ if(replace) "?replace=$replace" else "" }"
    tryReadFileToString(json).flatMap(put(path))
  }

  def deleteMetadata(json: File): Try[String] = {
    trace(json)
    val path = if(isPersistentId) s"datasets/:persistentId/deleteMetadata/?persistentId=$id"
               else s"datasets/$id/deleteMetadata"
    tryReadFileToString(json).flatMap(put(path))
  }

  def publish(updateType: String): Try[String] = {
    trace(updateType)
    val path = if(isPersistentId) s"datasets/:persistentId/actions/:publish/?persistentId=$id&type=$updateType"
               else s"datasets/$id/actions/:publish?type=$updateType"
    postJson(path)(200, 202)(null)
  }

  def deleteDraft(): Try[String] = {
    trace(())
    val path = if(isPersistentId) s"datasets/:persistentId/versions/:draft/?persistentId=$id"
              else s"datasets/$id/versions/:draft/"
    deletePath(path)
  }

  def setCitationDateField(fieldName: String): Try[String] = {
    trace(())
    val path = if(isPersistentId) s"datasets/:persistentId/citationdate?persistentId=$id"
               else s"datasets/$id/citationdate"
    put(path)(s"$fieldName")
  }

  def revertCitationDateField(): Try[String] = {
    trace(())
    val path = if(isPersistentId) s"datasets/:persistentId/citationdate?persistentId=$id"
               else s"datasets/$id/citationdate"
    deletePath(path)
  }

  def listRoleAssignments(): Try[String] = {
    trace(())
    val path = if(isPersistentId) s"datasets/:persistentId/assignments?persistentId=$id"
               else s"datasets/$id/assignments"
    get(path)
  }

  def createPrivateUrl(): Try[String] = {
    trace(())
    val path = if(isPersistentId) s"datasets/:persistentId/privateUrl?persistentId=$id"
               else s"datasets/$id/privateUrl"
    postJson(path)(201)(null)
  }

  def getPrivateUrl: Try[String] = {
    trace(())
    val path = if(isPersistentId) s"datasets/:persistentId/privateUrl?persistentId=$id"
               else s"datasets/$id/privateUrl"
    get(path)
  }

  def deletePrivateUrl(): Try[String]= {
    trace(())
    val path = if(isPersistentId) s"datasets/:persistentId/privateUrl?persistentId=$id"
               else s"datasets/$id/privateUrl"
    deletePath(path)
  }

  def addFile(dataFile: File, jsonMetadata: Option[File], jsonString: Option[String]): Try[String] = {
    trace(dataFile, jsonMetadata, jsonString)
    val path = if(isPersistentId) s"datasets/:persistentId/add?persistentId=$id"
               else s"datasets/$id/add"
    jsonMetadata.map {
      f => tryReadFileToString(f).flatMap {
        s => postFile(path, dataFile, Some(s))(200, formatResponseAsJson = true)
      }
    }.getOrElse {
      postFile(path, dataFile, jsonString)(200, formatResponseAsJson = true)
    }
  }

}
