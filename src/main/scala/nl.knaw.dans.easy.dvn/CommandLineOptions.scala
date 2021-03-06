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

import java.io.{ File => jFile }

import org.rogach.scallop.{ ScallopConf, ScallopOption, Subcommand }

class CommandLineOptions(args: Array[String], configuration: Configuration) extends ScallopConf(args) {
  appendDefaultToDescription = true
  editBuilder(_.setHelpWidth(110))
  printedName = "easy-dvn"
  version(configuration.version)
  private val SUBCOMMAND_FOOTER = "------------------\n\n"
  val description: String = s"""Command-line tool exploring the Dataverse API"""
  val synopsis: String =
    s"""
       |  $printedName dataverse <alias> <sub-command>
       |  $printedName dataset [-p] <id> <sub-command>
       |  $printedName file [-p] <id> <sub-command>
     """.stripMargin

  version(s"$printedName v${ configuration.version }")
  banner(
    s"""
       |  $description
       |
       |Usage:
       |
       |$synopsis
       |
       |Options:
       |""".stripMargin)

  /*
   * Dataverse commands
   */
  val dataverse = new Subcommand("dataverse") {
    shortSubcommandsHelp(true)
    descr("Operations on a dataverse")
    val alias: ScallopOption[String] = trailArg("dataverse-alias",
      descr = "The dataverse alias")
    val create = new Subcommand("create") {
      descr("Create a dataverse")
      val json: ScallopOption[jFile] = opt(name = "json",
        descr = "JSON file containing the properties of the new dataverse",
        required = true)
      validateFileExists(json)
    }
    addSubcommand(create)

    val view = new Subcommand("view") {
      descr("View metadata about a dataverse")
    }
    addSubcommand(view)

    val delete = new Subcommand("delete") {
      descr("Delete a dataverse")
    }
    addSubcommand(delete)

    val show = new Subcommand("show") {
      descr("Show the contents of a dataverse")
    }
    addSubcommand(show)

    val listRoles = new Subcommand("list-roles") {
      descr("List roles in a dataverse")
    }
    addSubcommand(listRoles)

    val createRole = new Subcommand("create-role") {
      descr("Create a new role in a dataverse")
      val json: ScallopOption[jFile] = opt(name = "json",
        descr = "JSON file containing the definition of the new role",
        required = true)
    }
    addSubcommand(createRole)

    val listFacets = new Subcommand("list-facets") {
      descr("List facets in a dataverse")
    }
    addSubcommand(listFacets)

    val setFacets = new Subcommand("set-facets") {
      descr("Set the facets fore a dataverse")
      val facets: ScallopOption[List[String]] = trailArg(name = "facets",
        descr = "Facets to set",
        required = true)
    }
    addSubcommand(setFacets)

    val listRoleAssignments = new Subcommand("list-role-assignments") {
      descr("List role assignments in a dataverse")
    }
    addSubcommand(listRoleAssignments)

    val setDefaultRole = new Subcommand("set-default-role") {
      descr("Set the default role for a user creating a dataset")
      val role: ScallopOption[String] = opt("role",
        descr = "Role to set ('none' for no role)")
    }
    addSubcommand(setDefaultRole)

    val assignRole = new Subcommand("assign-role") {
      descr("Assign a new role, based on the POSTed JSON")
      val json: ScallopOption[jFile] = opt(name = "json",
        descr = "JSON file containing role assignment detail",
        required = true)
      validateFileExists(json)
    }
    addSubcommand(assignRole)

    val unassignRole = new Subcommand("unassign-role") {
      descr("Delete ar role assignment")
      val roleId: ScallopOption[String] = trailArg(name = "assignment-id",
        descr = "ID of the role assignment to delete",
        required = true)
    }
    addSubcommand(unassignRole)

    val listMetadataBlocks = new Subcommand("list-metadata-blocks") {
      descr("Get the metadata blocks defined on the passed dataverse")
    }
    addSubcommand(listMetadataBlocks)

    val setMetadataBlocks = new Subcommand("set-metadata-blocks") {
      descr("Set the metadata blocks of the dataverse")
      val metadataBlockIds: ScallopOption[List[String]] = trailArg(name = "metadata-block-id",
        descr = "Identifiers of metadata blocks to set",
        required = true)
    }
    addSubcommand(setMetadataBlocks)

    val isMetadataBlocksRoot = new Subcommand("is-metadata-blocks-root") {
      descr("Return whether this dataverse inherits metadata blocks from its parent")
    }
    addSubcommand(isMetadataBlocksRoot)

    val setMetadataBlocksRoot = new Subcommand("set-metadata-blocks-root") {
      descr("Set whether this dataverse inherits metadata blocks from its parent")
      val setRoot: ScallopOption[String] = trailArg("set-root",
        descr = "If true this is a root (does not inherit) otherwise non-root (inherits)",
        required = false,
        default = Some("true"))
      validate(setRoot) { s =>
        if (List("true", "false").contains(s)) Right(())
        else Left("Must be one of true or false")
      }
    }
    addSubcommand(setMetadataBlocksRoot)

    val createDataset = new Subcommand("create-dataset") {
      descr("Create a new dataset in this dataverse")
      val json: ScallopOption[jFile] = opt(name = "json",
        descr = "JSON file containing the dataset metadata",
        required = true)
      validateFileExists(json)
    }
    addSubcommand(createDataset)

    val importDataset = new Subcommand("import-dataset") {
      descr("Import a dataset with existing PID into this dataverse")
      val pid: ScallopOption[String] = opt(name = "pid",
        descr = "The PID of the dataset",
        required = true)
      val importFile: ScallopOption[jFile] = opt(name = "file",
        descr = "File containing the dataset metadata",
        required = true)
      val format: ScallopOption[String] = opt(name = "format",
        descr = "Format of 'file', either json or ddi",
        required = true)
      val keepOnDraft: ScallopOption[Boolean] = opt(name = "keep-on-draft",
        descr = "Do not release the dataset")
      validateFileExists(importFile)
      validate(format) { f =>
        if (List("json", "ddi").contains(f)) Right(())
        else Left("format must be one of 'json', 'ddi'")
      }
    }
    addSubcommand(importDataset)

    val publish = new Subcommand("publish") {
      descr("Publish the dataverse")
    }
    addSubcommand(publish)
    footer(SUBCOMMAND_FOOTER)
  }
  addSubcommand(dataverse)

  /*
   * Dataset commands
   */
  val dataset = new Subcommand("dataset") {
    shortSubcommandsHelp(true)
    val id: ScallopOption[String] = trailArg("dataset-id",
      descr = "Dataset (persistent) ID")
    val persistentId: ScallopOption[Boolean] = opt("persistent-id",
      descr = "Use the persistent instead or internal ID")

    val view = new Subcommand("view") {
      descr("View metadata about a dataverse")
      val latest: ScallopOption[Boolean] = opt("latest",
        descr = "View the latest version")
      val latestPublished: ScallopOption[Boolean] = opt("latest-published", short = 'p',
        descr = "View the latest published version")
      val draft: ScallopOption[Boolean] = opt("draft",
        descr = "View the draft version")
      val version: ScallopOption[String] = opt("version",
        descr = "View the specified version")

      mutuallyExclusive(latest, latestPublished, draft, version)
    }
    addSubcommand(view)

    val delete = new Subcommand("delete") {
      descr("Delete the dataset")
    }
    addSubcommand(delete)

    val listVersions = new Subcommand("list-versions") {
      descr("List the versions of a dataset")
    }
    addSubcommand(listVersions)

    val exportMetadataTo = new Subcommand("export-metadata-to") {
      descr("Export the metadata of the current published version of a dataset in various formats")
      val format: ScallopOption[String] = trailArg("format",
        descr = "The metadata format to export to. One of: ddi, oai_ddi, dcterms, oai_dc, schema.org, dataverse_json",
        required = true)
    }
    addSubcommand(exportMetadataTo)

    val listFiles = new Subcommand("list-files") {
      descr("List all the file metadata, for the given dataset and version")
      val latest: ScallopOption[Boolean] = opt("latest",
        descr = "View the latest version")
      val latestPublished: ScallopOption[Boolean] = opt("latest-published", short = 'p',
        descr = "View the latest published version")
      val draft: ScallopOption[Boolean] = opt("draft",
        descr = "View the draft version")
      val version: ScallopOption[String] = opt("version",
        descr = "View the specified version")
      mutuallyExclusive(latest, latestPublished, draft, version)
      requireAtLeastOne(latest, latestPublished, draft, version)
    }
    addSubcommand(listFiles)

    val listMetadataBlocks = new Subcommand("list-metadata-blocks") {
      descr("List all the metadata blocks and their content, for the given dataset and version")
      val latest: ScallopOption[Boolean] = opt("latest",
        descr = "View the latest version")
      val latestPublished: ScallopOption[Boolean] = opt("latest-published", short = 'p',
        descr = "View the latest published version")
      val draft: ScallopOption[Boolean] = opt("draft",
        descr = "View the draft version")
      val version: ScallopOption[String] = opt("version",
        descr = "View the specified version")
      val blockName: ScallopOption[String] = opt("name",
        descr = "Show only this block")
      mutuallyExclusive(latest, latestPublished, draft, version)
      requireAtLeastOne(latest, latestPublished, draft, version)
    }
    addSubcommand(listMetadataBlocks)

    val updateMetadata = new Subcommand("update-metadata") {
      descr("Update the metadata for a dataset.")
      val json: ScallopOption[jFile] = opt("json-metadata",
        descr = "JSON file containing the new metadata",
        required = true
      )
      val latest: ScallopOption[Boolean] = opt("latest",
        descr = "Update the latest version")
      val latestPublished: ScallopOption[Boolean] = opt("latest-published", short = 'p',
        descr = "Update the latest published version")
      val draft: ScallopOption[Boolean] = opt("draft",
        descr = "Update the draft version")
      val version: ScallopOption[String] = opt("version",
        descr = "Update the specified version")
      mutuallyExclusive(latest, latestPublished, draft, version)
    }
    addSubcommand(updateMetadata)

    val editMetadata = new Subcommand("edit-metadata") {
      descr("Add metadata to dataset fields that are blank or accept multiple values with the following")
      val json: ScallopOption[jFile] = opt("json-metadata",
        descr = "JSON file containing the new metadata",
        required = true
      )
      val replace: ScallopOption[Boolean] = opt("replace",
        descr = "Replace existing value instead of adding")
    }
    addSubcommand(editMetadata)

    val deleteMetadata = new Subcommand("delete-metadata") {
      descr("Delete metadata from a dataset")
      val json: ScallopOption[jFile] = opt("json-metadata",
        descr = "JSON file containing the metadata to delete",
        required = true
      )
    }
    addSubcommand(deleteMetadata)

    val publish = new Subcommand("publish") {
      descr("Publish a dataset.")
      val publishType: ScallopOption[String] = opt("type",
        descr = "How to update existing version: major or minor update",
        required = true
      )
      validate(publishType) { t =>
        if (List("major", "minor").contains(t)) Right(())
        else Left("type must be one of 'major', 'minor'")
      }
    }
    addSubcommand(publish)

    val deleteDraft = new Subcommand("delete-draft") {
      descr("Deletes the draft version")
    }
    addSubcommand(deleteDraft)

    val setCitationDateField = new Subcommand("set-citation-date-field") {
      descr("Sets the date field to use in the citation")
      val fieldId: ScallopOption[String] = trailArg("field-id",
        descr = "ID of the date field to use",
        required = true)
    }
    addSubcommand(setCitationDateField)

    val revertCitationDateField = new Subcommand("revert-citation-date-field") {
      descr("Reverts the date field to use in the citation to the default")
    }
    addSubcommand(revertCitationDateField)

    val listRoleAssignments = new Subcommand("list-role-assignments") {
      descr("List all the role assignments at the given dataset")
    }
    addSubcommand(listRoleAssignments)

    val createPrivateUrl = new Subcommand("create-private-url") {
      descr("Create a Private URL (must be able to manage dataset permissions)")
    }
    addSubcommand(createPrivateUrl)

    val getPrivateUrl = new Subcommand("get-private-url") {
      descr("Get the Private URL if exists (must be able to manage dataset permissions)")
    }
    addSubcommand(getPrivateUrl)

    val deletePrivateUrl = new Subcommand("delete-private-url") {
      descr("Deletes the Private URL if exists (must be able to manage dataset permissions)")
    }
    addSubcommand(deletePrivateUrl)

    val addFile = new Subcommand("add-file") {
      descr("Adds a data file")
      val jsonMetadata: ScallopOption[jFile] = opt("json-metadata",
        descr = "Metadata for the file in JSON format")
      val description: ScallopOption[String] = opt("description", default = Some(""))
      val categories: ScallopOption[List[String]] = opt("categories", default = Some(List()))
      val restrict: ScallopOption[Boolean] = opt("restrict")
      val dataFile: ScallopOption[jFile] = trailArg("file",
        descr = "Data file to add",
        required = true)
      mutuallyExclusive(jsonMetadata, description)
      mutuallyExclusive(jsonMetadata, categories)
      mutuallyExclusive(jsonMetadata, restrict)
      validateFileExists(jsonMetadata)
      validateFileExists(dataFile)
    }
    addSubcommand(addFile)

    val submitForReview = new Subcommand("submit-for-review") {
      descr("Submit dataset for review")
    }
    addSubcommand(submitForReview)

    val returnToAuthor = new Subcommand("return-to-author") {
      descr("Return a dataset that is in review to the author")
      val reason: ScallopOption[String] = opt("reason",
        descr = "Reason why the dataset cannot be published yet",
        required = true)
    }
    addSubcommand(returnToAuthor)

    val link = new Subcommand("link") {
      descr("Creates a link to this dataset in another dataverse")
      val dataverseAlias: ScallopOption[String] = trailArg("dataverse",
        descr = "The alias of the dataverse to create the link in",
        required = true)
    }
    addSubcommand(link)

    val getLocks = new Subcommand("get-locks") {
      descr("Display information about current locks on this dataset")
      val lockType: ScallopOption[String] = opt("lock-type", short = 't')
    }
    addSubcommand(getLocks)
    footer(SUBCOMMAND_FOOTER)
  }
  addSubcommand(dataset)

  val file = new Subcommand("file") {
    shortSubcommandsHelp(true)
    val id: ScallopOption[String] = trailArg("file-id",
      descr = "File (persistent) ID")
    val persistentId: ScallopOption[Boolean] = opt("persistent-id",
      descr = "Use the persistent instead or internal ID")

    val restrict = new Subcommand("restrict") {
      descr("Set the file to restricted")
    }
    addSubcommand(restrict)
    val unrestrict = new Subcommand("unrestrict") {
      descr("Set the file to unrestricted")
    }
    addSubcommand(unrestrict)

    val replace = new Subcommand("replace") {
      descr("Replace a data file")
      val jsonMetadata: ScallopOption[jFile] = opt("json-metadata",
        descr = "Metadata for the file in JSON format")
      val description: ScallopOption[String] = opt("description", default = Some(""))
      val categories: ScallopOption[List[String]] = opt("categories", default = Some(List()))
      val restrict: ScallopOption[Boolean] = opt("restrict")
      val dataFile: ScallopOption[jFile] = trailArg("file",
        descr = "Data file to replace",
        required = true)
      mutuallyExclusive(jsonMetadata, description)
      mutuallyExclusive(jsonMetadata, categories)
      mutuallyExclusive(jsonMetadata, restrict)
      validateFileExists(jsonMetadata)
      validateFileExists(dataFile)
    }
    addSubcommand(replace)

    val uningest = new Subcommand("uningest") {
      descr("Uningest a tabular file")
    }
    addSubcommand(uningest)

    val reingest = new Subcommand("reingest") {
      descr("Reingest a tabular file")
    }
    addSubcommand(reingest)

    /*
     * Permission for this must first be turned on:
     * curl -X PUT -d 'true' http://localhost:8080/api/admin/settings/:ProvCollectionEnabled
     */
    val getProvenance = new Subcommand("get-provenance") {
      descr("Returns the provenance for this file")
      val inJsonFormat: ScallopOption[Boolean] = opt("json",
        descr = "Use JSON as output")
    }
    addSubcommand(getProvenance)
    footer(SUBCOMMAND_FOOTER)
  }
  addSubcommand(file)
}
