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
import scala.util.{ Failure, Try }
import scala.language.postfixOps

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
      app dataverse (dv.alias()) create (File(action.json().getAbsolutePath))
    case (dv @ commandLine.dataverse) :: (commandLine.dataverse.view) :: Nil =>
      app dataverse (dv.alias()) view()
    case (dv @ commandLine.dataverse) :: (commandLine.dataverse.delete) :: Nil =>
      app dataverse (dv.alias()) delete()
    case (dv @ commandLine.dataverse) :: (commandLine.dataverse.show) :: Nil =>
      app dataverse (dv.alias()) show()
    case (dv @ commandLine.dataverse) :: (commandLine.dataverse.listRoles) :: Nil =>
      app dataverse (dv.alias()) listRoles()
    case (dv @ commandLine.dataverse) :: (commandLine.dataverse.listFacets) :: Nil =>
      app dataverse (dv.alias()) listFacets()
    case (dv @ commandLine.dataverse) :: (action @ commandLine.dataverse.setFacets) :: Nil =>
      app dataverse (dv.alias()) setFacets (action.facets())
    case (dv @ commandLine.dataverse) :: (action @ commandLine.dataverse.createRole) :: Nil =>
      app dataverse (dv.alias()) createRole (File(action.json().getAbsolutePath))
    case (dv @ commandLine.dataverse) :: (commandLine.dataverse.listRoleAssignments) :: Nil =>
      app dataverse (dv.alias()) listRoleAssignments()
    case (dv @ commandLine.dataverse) :: (action @ commandLine.dataverse.setDefaultRole) :: Nil =>
      app dataverse (dv.alias()) setDefaultRole (action.role())
    case (dv @ commandLine.dataverse) :: (action @ commandLine.dataverse.assignRole) :: Nil =>
      app dataverse (dv.alias()) assignRole (File(action.json().getAbsolutePath))
    case (dv @ commandLine.dataverse) :: (action @ commandLine.dataverse.unassignRole) :: Nil =>
      app dataverse (dv.alias()) unassignRole (action.roleId())
    case (dv @ commandLine.dataverse) :: (commandLine.dataverse.listMetadataBlocks) :: Nil =>
      app dataverse (dv.alias()) listMetadataBocks()
    case (dv @ commandLine.dataverse) :: (action @ commandLine.dataverse.setMetadataBlocks) :: Nil =>
      app dataverse (dv.alias()) setMetadataBlocks (action.metadataBlockIds())
    case (dv @ commandLine.dataverse) :: (action @ commandLine.dataverse.isMetadataBlocksRoot) :: Nil =>
      app dataverse (dv.alias()) isMetadataBlocksRoot
    case (dv @ commandLine.dataverse) :: (action @ commandLine.dataverse.setMetadataBlocksRoot) :: Nil =>
      app dataverse (dv.alias()) setMetadataBlocksRoot (action.setRoot().toBoolean)
    case (dv @ commandLine.dataverse) :: (commandLine.dataverse.publish) :: Nil =>
      app dataverse (dv.alias()) publish()
    case (dv @ commandLine.dataverse) :: (action @ commandLine.dataverse.createDataset) :: Nil =>
      app dataverse (dv.alias()) createDataset (File(action.json().getAbsolutePath))
    case (dv @ commandLine.dataverse) :: (action @ commandLine.dataverse.importDataset) :: Nil =>
      app dataverse (dv.alias()) importDataset(File(action.importFile().getAbsolutePath), action.format() == "ddi", action.pid(), action.keepOnDraft())

    /*
     * Dataset commands
     */
    case (ds @ commandLine.dataset) :: (action @ commandLine.dataset.view) :: Nil =>
      val optVersion = if (action.latestPublished()) Some(":latest-published")
                       else if (action.latest()) Some(":latest")
                            else if (action.draft()) Some(":draft")
                                 else action.version.toOption.orElse(None)
      app dataset(ds.id(), ds.persistentId()) view (optVersion)
    case (ds @ commandLine.dataset) :: (commandLine.dataset.delete) :: Nil =>
      app dataset(ds.id(), ds.persistentId()) delete()
    case (ds @ commandLine.dataset) :: (commandLine.dataset.listVersions) :: Nil =>
      app dataset(ds.id(), ds.persistentId()) listVersions()
    case (ds @ commandLine.dataset) :: (action @ commandLine.dataset.exportMetadataTo) :: Nil =>
      app dataset(ds.id(), ds.persistentId()) exportMetadataTo (action.format())
    case (ds @ commandLine.dataset) :: (action @ commandLine.dataset.listFiles) :: Nil =>
      val optVersion = if (action.latestPublished()) Some(":latest-published")
                       else if (action.latest()) Some(":latest")
                            else if (action.draft()) Some(":draft")
                                 else action.version.toOption.orElse(None)
      app dataset(ds.id(), ds.persistentId()) listFiles (optVersion)
    case (ds @ commandLine.dataset) :: (action @ commandLine.dataset.listMetadataBlocks) :: Nil =>
      val optVersion = if (action.latestPublished()) Some(":latest-published")
                       else if (action.latest()) Some(":latest")
                            else if (action.draft()) Some(":draft")
                                 else action.version.toOption.orElse(None)
      app dataset(ds.id(), ds.persistentId()) listMetadataBlocks(optVersion, action.blockName.toOption)
    case (ds @ commandLine.dataset) :: (action @ commandLine.dataset.updateMetadata) :: Nil =>
      val optVersion = if (action.latestPublished()) Some(":latest-published")
                       else if (action.latest()) Some(":latest")
                            else if (action.draft()) Some(":draft")
                                 else action.version.toOption.orElse(None)
      app dataset(ds.id(), ds.persistentId()) updateMetadata(File(action.json().getAbsolutePath), optVersion)
    case (ds @ commandLine.dataset) :: (action @ commandLine.dataset.editMetadata) :: Nil =>
      app dataset(ds.id(), ds.persistentId()) editMetadata(File(action.json().getAbsolutePath), action.replace())
    case (ds @ commandLine.dataset) :: (action @ commandLine.dataset.deleteMetadata) :: Nil =>
      app dataset(ds.id(), ds.persistentId()) deleteMetadata (File(action.json().getAbsolutePath))
    case (ds @ commandLine.dataset) :: (action @ commandLine.dataset.publish) :: Nil =>
      app dataset(ds.id(), ds.persistentId()) publish (action.publishType())
    case (ds @ commandLine.dataset) :: (commandLine.dataset.deleteDraft) :: Nil =>
      app dataset(ds.id(), ds.persistentId()) deleteDraft()
    case (ds @ commandLine.dataset) :: (action @ commandLine.dataset.setCitationDateField) :: Nil =>
      app dataset(ds.id(), ds.persistentId()) setCitationDateField (action.fieldId())
    case (ds @ commandLine.dataset) :: (commandLine.dataset.revertCitationDateField) :: Nil =>
      app dataset(ds.id(), ds.persistentId()) revertCitationDateField()
    case (ds @ commandLine.dataset) :: (commandLine.dataset.listRoleAssignments) :: Nil =>
      app dataset(ds.id(), ds.persistentId()) listRoleAssignments()
    case (ds @ commandLine.dataset) :: (commandLine.dataset.createPrivateUrl) :: Nil =>
      app dataset(ds.id(), ds.persistentId()) createPrivateUrl()
    case (ds @ commandLine.dataset) :: (commandLine.dataset.getPrivateUrl) :: Nil =>
      app dataset(ds.id(), ds.persistentId()) getPrivateUrl
    case (ds @ commandLine.dataset) :: commandLine.dataset.deletePrivateUrl :: Nil =>
      app dataset(ds.id(), ds.persistentId()) deletePrivateUrl()
    case (ds @ commandLine.dataset) :: (action @ commandLine.dataset.addFile) :: Nil =>
      val categories = action.categories().map(c => s""""$c"""").mkString("[", ",", "]")
      val description = action.description()
      val restrict = action.restrict()

      val json = s"""{"description":"$description","categories":$categories, "restrict":"$restrict"}"""

      app dataset(ds.id(), ds.persistentId()) addFile(File(action.dataFile().getAbsolutePath), action.jsonMetadata.toOption.map(f => File(f.getAbsolutePath)).orElse(None), Some(json))
    case (ds @ commandLine.dataset) :: commandLine.dataset.submitForReview :: Nil =>
      app dataset(ds.id(), ds.persistentId()) submitForReview()
    case (ds @ commandLine.dataset) :: (action @ commandLine.dataset.returnToAuthor) :: Nil =>
      app dataset(ds.id(), ds.persistentId()) returnToAuthor (action.reason())
    case (ds @ commandLine.dataset) :: (action @ commandLine.dataset.link) :: Nil =>
      app dataset(ds.id(), ds.persistentId()) link (action.dataverseAlias())
    case (ds @ commandLine.dataset) :: (action @ commandLine.dataset.getLocks) :: Nil =>
      app dataset(ds.id(), ds.persistentId()) getLocks (action.lockType.toOption)

    /*
     * File commands
     */
    case (f @ commandLine.file) :: (commandLine.file.restrict) :: Nil =>
      app file(f.id(), f.persistentId()) restrict (true)
    case (f @ commandLine.file) :: (commandLine.file.unrestrict) :: Nil =>
      app file(f.id(), f.persistentId()) restrict (false)
    case (f @ commandLine.file) :: (action @ commandLine.file.replace) :: Nil =>
      val categories = action.categories().map(c => s""""$c"""").mkString("[", ",", "]")
      val description = action.description()
      val restrict = action.restrict()

      val json = s"""{"description":"$description","categories":$categories, "restrict":"$restrict"}"""

      app file(f.id(), f.persistentId()) replace(File(action.dataFile().getAbsolutePath), action.jsonMetadata.toOption.map(f => File(f.getAbsolutePath)).orElse(None), Some(json))
    case (f @ commandLine.file) :: (commandLine.file.uningest) :: Nil =>
      app file(f.id(), f.persistentId()) uningest()
    case (f @ commandLine.file) :: (commandLine.file.reingest) :: Nil =>
      app file(f.id(), f.persistentId()) reingest()
    case (f @ commandLine.file) :: (action @ commandLine.file.getProvenance) :: Nil =>
      app file(f.id(), f.persistentId()) getProvenance (action.inJsonFormat())

    /**
     * Hack fix to silence the compiler about incomplete matching
     */
    case _ => Failure(new RuntimeException(s"Unkown command: $commandLine"))
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

