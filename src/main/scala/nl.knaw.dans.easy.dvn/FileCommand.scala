package nl.knaw.dans.easy.dvn

import java.io.PrintStream
import java.net.URI

import better.files.File
import nl.knaw.dans.lib.logging.DebugEnhancedLogging

import scala.util.Try

class FileCommand (id: String, isPersistentId: Boolean, configuration: Configuration)(implicit resultOutput: PrintStream) extends HttpSupport with DebugEnhancedLogging {
  protected val connectionTimeout: Int = configuration.connectionTimeout
  protected val readTimeout: Int = configuration.readTimeout
  protected val baseUrl: URI = configuration.baseUrl
  protected val apiToken: String = configuration.apiToken
  protected val apiVersion: String = configuration.apiVersion

  def restrict(doRestict: Boolean): Try[String] = {
    trace(doRestict)
    val path = if(isPersistentId) s"files/:persistentId/restrict?persistentId=$id"
               else s"files/$id/restrict"
    put(path)(doRestict.toString)
  }

  def replace(replacementData: File, replacementJsonMetadata: Option[File], jsonString: Option[String]): Try[String] = {
    trace(replacementData, replacementJsonMetadata, jsonString)
    val path = if(isPersistentId) s"files/:persistentId/replace?persistentId=$id"
               else s"files/$id/replace"
    replacementJsonMetadata.map {
      f => tryReadFileToString(f).flatMap {
        s => postFile(path, replacementData, Some(s))(200, formatResponseAsJson = true)
      }
    }.getOrElse {
      postFile(path, replacementData, jsonString)(200, formatResponseAsJson = true)
    }
  }

  def uningest(): Try[String] = {
    val path = if(isPersistentId) s"files/:persistentId/uningest?persistentId=$id"
               else s"files/$id/uningest"
    postJson(path)(200)()
  }

  def reingest(): Try[String] = {
    val path = if(isPersistentId) s"files/:persistentId/reingest?persistentId=$id"
               else s"files/$id/reingest"
    postJson(path)(200)()
  }

}
