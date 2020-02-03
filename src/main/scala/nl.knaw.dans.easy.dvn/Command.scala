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

import better.files.File
import nl.knaw.dans.lib.error._
import nl.knaw.dans.lib.logging.DebugEnhancedLogging

import scala.language.reflectiveCalls
import scala.util.Try

object Command extends App with DebugEnhancedLogging {
  type FeedBackMessage = String

  val configuration = Configuration(File(System.getProperty("app.home")))
  logger.info(s"Read configuration: $configuration")
  val commandLine: CommandLineOptions = new CommandLineOptions(args, configuration) {
    verify()
  }
  val app = new EasyDvnApp(configuration)

  val result: Try[FeedBackMessage] = commandLine.subcommands match {
    case commandLine.dataverse :: (create @ commandLine.dataverse.create) :: Nil =>
      app dataverse() create(create.parent(), File(create.json().getAbsolutePath))
    case commandLine.dataverse :: (view @ commandLine.dataverse.view) :: Nil =>
      app dataverse() view (view.id())
    case commandLine.dataverse :: (delete @ commandLine.dataverse.delete) :: Nil =>
      app dataverse() delete (delete.id())
    case commandLine.dataverse :: (show @ commandLine.dataverse.show) :: Nil =>
      app dataverse() show (show.id())
    case commandLine.dataverse :: (listRoles @ commandLine.dataverse.listRoles) :: Nil =>
      app dataverse() listRoles (listRoles.id())
    case commandLine.dataverse :: (listFacets @ commandLine.dataverse.listFacets) :: Nil =>
      app dataverse() listFacets (listFacets.id())
    case commandLine.dataverse :: (setFacets @ commandLine.dataverse.setFacets) :: Nil =>
      app dataverse() setFacets(setFacets.id(), setFacets.facets())
    case commandLine.dataverse :: (createRole @ commandLine.dataverse.createRole) :: Nil =>
      app dataverse() createRole(createRole.id(), File(createRole.json().getAbsolutePath))
    case commandLine.dataverse :: (listAssignments @ commandLine.dataverse.listAssignments) :: Nil =>
      app dataverse() listRoleAssignments(listAssignments.id())
    case commandLine.dataverse :: (setDefaultRole @ commandLine.dataverse.setDefaultRole) :: Nil =>
      app dataverse() setDefaultRole(setDefaultRole.id(), setDefaultRole.role())


  }

  result.doIfSuccess(msg => Console.err.println(s"OK: $msg"))
    .doIfFailure {
      case cfe: CommandFailedException =>
        Console.err.println(s"ERROR: ${ cfe.getMessage }")
        logger.error(s"Status line: ${ cfe.msg }. Body: ${ cfe.body }")
        System.exit(1)
      case t =>
        Console.err.println(s"ERROR: ${ t.getClass.getSimpleName }: ${ t.getMessage }")
        logger.error("A fatal exception occurred", t)
        System.exit(1)
    }
}

