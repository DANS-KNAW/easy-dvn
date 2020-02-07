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

import java.io.PrintStream

import better.files.File
import nl.knaw.dans.lib.error._
import nl.knaw.dans.lib.logging.DebugEnhancedLogging

import scala.language.reflectiveCalls
import scala.util.Try

object Command extends App with DebugEnhancedLogging {
  type FeedBackMessage = String
  implicit val resultOutput: PrintStream = Console.out

  val configuration = Configuration(File(System.getProperty("app.home")))
  logger.info(s"Read configuration: $configuration")
  val commandLine: CommandLineOptions = new CommandLineOptions(args, configuration) {
    verify()
  }
  val app = new EasyDvnApp(configuration)

  val result: Try[FeedBackMessage] = commandLine.subcommands match {
    /*
     * Dataverse commands
     */
    case (dv @ commandLine.dataverse) :: (action @ commandLine.dataverse.create) :: Nil =>
      app dataverse (dv.id()) create (File(action.json().getAbsolutePath))
    case (dv @ commandLine.dataverse) :: (commandLine.dataverse.view) :: Nil =>
      app dataverse (dv.id()) view()
    case (dv @ commandLine.dataverse) :: (commandLine.dataverse.delete) :: Nil =>
      app dataverse (dv.id()) delete()
    case (dv @ commandLine.dataverse) :: (commandLine.dataverse.show) :: Nil =>
      app dataverse (dv.id()) show()
    case (dv @ commandLine.dataverse) :: (commandLine.dataverse.listRoles) :: Nil =>
      app dataverse (dv.id()) listRoles()
    case (dv @ commandLine.dataverse) :: (commandLine.dataverse.listFacets) :: Nil =>
      app dataverse (dv.id()) listFacets()
    case (dv @ commandLine.dataverse) :: (action @ commandLine.dataverse.setFacets) :: Nil =>
      app dataverse (dv.id()) setFacets (action.facets())
    case (dv @ commandLine.dataverse) :: (action @ commandLine.dataverse.createRole) :: Nil =>
      app dataverse (dv.id()) createRole (File(action.json().getAbsolutePath))
    case (dv @ commandLine.dataverse) :: (commandLine.dataverse.listRoleAssignments) :: Nil =>
      app dataverse (dv.id()) listRoleAssignments()
    case (dv @ commandLine.dataverse) :: (action @ commandLine.dataverse.setDefaultRole) :: Nil =>
      app dataverse (dv.id()) setDefaultRole (action.role())
    case (dv @ commandLine.dataverse) :: (action @ commandLine.dataverse.assignRole) :: Nil =>
      app dataverse (dv.id()) assignRole (File(action.json().getAbsolutePath))
    case (dv @ commandLine.dataverse) :: (action @ commandLine.dataverse.unassignRole) :: Nil =>
      app dataverse (dv.id()) unassignRole (action.roleId())
    case (dv @ commandLine.dataverse) :: (commandLine.dataverse.listMetadataBlocks) :: Nil =>
      app dataverse (dv.id()) listMetadataBocks()
    case (dv @ commandLine.dataverse) :: (action @ commandLine.dataverse.setMetadataBlocks) :: Nil =>
      app dataverse (dv.id()) setMetadataBlocks (action.metadataBlockIds())
    case (dv @ commandLine.dataverse) :: (action @ commandLine.dataverse.isMetadataBlocksRoot) :: Nil =>
      app dataverse (dv.id()) isMetadataBlocksRoot
    case (dv @ commandLine.dataverse) :: (action @ commandLine.dataverse.setMetadataBlocksRoot) :: Nil =>
      app dataverse (dv.id()) setMetadataBlocksRoot (action.setRoot().toBoolean)
    case (dv @ commandLine.dataverse) :: (commandLine.dataverse.publish) :: Nil =>
      app dataverse (dv.id()) publish()
    case (dv @ commandLine.dataverse) :: (action @ commandLine.dataverse.createDataset) :: Nil =>
      app dataverse (dv.id()) createDataset (File(action.json().getAbsolutePath))
    case (dv @ commandLine.dataverse) :: (action @ commandLine.dataverse.importDataset) :: Nil =>
      app dataverse (dv.id()) importDataset(File(action.importFile().getAbsolutePath), action.format() == "ddi", action.pid(), action.keepOnDraft())

    /*
     * Dataset commands
     */
    case (ds @ commandLine.dataset) :: (action @ commandLine.dataset.view) :: Nil =>
      val optVersion = if (action.latestPublished()) Some(":latest-published")
                    else if (action.latest()) Some(":latest")
                    else if (action.draft()) Some(":draft")
                    else action.version.toOption.orElse(None)
      app dataset (ds.id(), ds.persistentId()) view(optVersion)
    case (ds @ commandLine.dataset) :: (commandLine.dataset.delete) :: Nil =>
      app dataset (ds.id(), ds.persistentId()) delete()
    case (ds @ commandLine.dataset) :: (commandLine.dataset.listVersions) :: Nil =>
      app dataset (ds.id(), ds.persistentId()) listVersions()
    case (ds @ commandLine.dataset) :: (action @ commandLine.dataset.exportMetadataTo) :: Nil =>
      app dataset (ds.id(), ds.persistentId()) exportMetadataTo(action.format())
    case (ds @ commandLine.dataset) :: (action @ commandLine.dataset.listFiles) :: Nil =>
      val optVersion = if (action.latestPublished()) Some(":latest-published")
                       else if (action.latest()) Some(":latest")
                       else if (action.draft()) Some(":draft")
                       else action.version.toOption.orElse(None)
      app dataset (ds.id(), ds.persistentId()) listFiles(optVersion)
    case (ds @ commandLine.dataset) :: (action @ commandLine.dataset.listMetadataBlocks) :: Nil =>
      val optVersion = if (action.latestPublished()) Some(":latest-published")
                       else if (action.latest()) Some(":latest")
                       else if (action.draft()) Some(":draft")
                       else action.version.toOption.orElse(None)
      app dataset (ds.id(), ds.persistentId()) listMetadataBlocks(optVersion, action.blockName.toOption)
    case (ds @ commandLine.dataset) :: (action @ commandLine.dataset.updateMetadata) :: Nil =>
      val optVersion = if (action.latestPublished()) Some(":latest-published")
                       else if (action.latest()) Some(":latest")
                       else if (action.draft()) Some(":draft")
                       else action.version.toOption.orElse(None)
      app dataset (ds.id(), ds.persistentId()) updateMetadata(File(action.json().getAbsolutePath), optVersion)
    case (ds @ commandLine.dataset) :: (action @ commandLine.dataset.editMetadata) :: Nil =>
      app dataset (ds.id(), ds.persistentId()) editMetadata(File(action.json().getAbsolutePath), action.replace())
    case (ds @ commandLine.dataset) :: (action @ commandLine.dataset.deleteMetadata) :: Nil =>
      app dataset (ds.id(), ds.persistentId()) deleteMetadata(File(action.json().getAbsolutePath))
    case (ds @ commandLine.dataset) :: (action @ commandLine.dataset.publish) :: Nil =>
      app dataset (ds.id(), ds.persistentId()) publish(action.publishType())


      /*
       * File commands
       */

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

