package nl.knaw.dans.easy.dvn

import java.net.URI

import nl.knaw.dans.lib.logging.DebugEnhancedLogging

import scala.util.Try

class Dataset(id: String, configuration: Configuration) extends HttpSupport with DebugEnhancedLogging {
  protected val connectionTimeout: Int = configuration.connectionTimeout
  protected val readTimeout: Int = configuration.readTimeout
  protected val baseUrl: URI = configuration.baseUrl
  protected val apiToken: String = configuration.apiToken
  protected val apiVersion: String = configuration.apiVersion

  def view(id: String, isPersistentId: Boolean): Try[String] = {
    if(isPersistentId) get(s"datasets/:persistentId/?persistentId=$id")
    else get(s"datasets/$id")
  }
}
