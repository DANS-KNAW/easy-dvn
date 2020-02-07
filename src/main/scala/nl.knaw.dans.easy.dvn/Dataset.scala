package nl.knaw.dans.easy.dvn

import java.io.PrintStream
import java.net.URI

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

}
