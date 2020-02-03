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

import better.files.File
import better.files.File.root
import org.apache.commons.configuration.PropertiesConfiguration

case class Configuration(version: String,
                         apiToken: String,
                         baseUrl: URI,
                         apiVersion: String,
                         connectionTimeout: Int,
                         readTimeout: Int) {
  override def toString: String = {
    s"[version = $version, apiToken = $apiToken, baseUrl = $baseUrl, apiVersion = $apiVersion]"
  }
}

object Configuration {

  def apply(home: File): Configuration = {
    val cfgPath = Seq(
      root / "etc" / "opt" / "dans.knaw.nl" / "easy-dvn",
      home / "cfg")
      .find(_.exists)
      .getOrElse { throw new IllegalStateException("No configuration directory found") }
    val properties = new PropertiesConfiguration() {
      setDelimiterParsingDisabled(true)
      load((cfgPath / "application.properties").toJava)
    }

    new Configuration(
      version = (home / "bin" / "version").contentAsString.stripLineEnd,
      apiToken = properties.getString("dvn.api-token"),
      baseUrl = new URI(properties.getString("dvn.base-url")),
      apiVersion = properties.getString("dvn.api.version"),
      connectionTimeout = properties.getInt("dvn.api.connection-timout-ms"),
      readTimeout = properties.getInt("dvn.api.read-timeout-ms"))
  }
}
