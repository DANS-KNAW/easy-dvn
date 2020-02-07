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
  private val SUBCOMMAND_SEPARATOR = "---\n"
  val description: String = s"""Command-line tool for working with the Dataverse API"""
  val synopsis: String =
    s"""
       |  $printedName dataverse {
       |                  create --parent <id> --json <json-definition-file>
       |                | view <id>
       |                | delete [-f, --force]
       |             }
       |  $printedName dataset {
       |
       |             }
       |  $printedName file {
       |
       |             }""".stripMargin

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
    descr("Operations on a dataverse")
    val id: ScallopOption[String] = trailArg("dataverse",
      descr = "The dataverse alias")
    val create = new Subcommand("create") {
      descr("Creates a dataverse")
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
      descr("Assigns a new role, based on the POSTed JSON")
      val json: ScallopOption[jFile] = opt(name = "json",
        descr = "JSON file containing role assignment detail",
        required = true)
      validateFileExists(json)
    }
    addSubcommand(assignRole)

    val unassignRole = new Subcommand("unassign-role") {
      descr("Deletes ar role assignment")
      val roleId: ScallopOption[String] = trailArg(name = "assignment-id",
        descr = "ID of the role assignment to delete",
        required = true)
    }
    addSubcommand(unassignRole)

    val listMetadataBlocks = new Subcommand("list-metadata-blocks") {
      descr("Gets the metadata blocks defined on the passed dataverse")
    }
    addSubcommand(listMetadataBlocks)

    val setMetadataBlocks = new Subcommand("set-metadata-blocks") {
      descr("Sets the metadata blocks of the dataverse")
      val metadataBlockIds: ScallopOption[List[String]] = trailArg(name = "metadata-block-id",
        descr = "Identifiers of metadata blocks to set",
        required = true)
    }
    addSubcommand(setMetadataBlocks)

    val isMetadataBlocksRoot = new Subcommand("is-metadata-blocks-root") {
      descr("Whether this dataverse inherits metadata blocks from its parent")
    }
    addSubcommand(isMetadataBlocksRoot)

    val setMetadataBlocksRoot = new Subcommand("set-metadata-blocks-root") {
      descr("Sets whether this dataverse inherits metadata blocks from its parent")
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
      descr("Creates a new dataset in this dataverse")
      val json: ScallopOption[jFile] = opt(name = "json",
        descr = "JSON file containing the dataset metadata",
        required = true)
      validateFileExists(json)
    }
    addSubcommand(createDataset)

    val importDataset = new Subcommand("import-dataset") {
      descr("Imports a dataset with existing PID into this dataverse")
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
  }
  addSubcommand(dataverse)

  /*
   * Dataset commands
   */
  val dataset = new Subcommand("dataset") {
    val id: ScallopOption[String] = trailArg("dataset",
      descr = "Dataset (persistent) ID")
    val persistentId: ScallopOption[Boolean] = opt("persistent-id",
      descr = "Use the persistent instead of internal ID")

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
      descr("Deletes the dataset")
    }
    addSubcommand(delete)

    val listVersions = new Subcommand("list-versions") {
      descr("Lists the versions of a dataset")
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
      descr("Lists all the file metadata, for the given dataset and version")
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
      descr("Lists all the metadata blocks and their content, for the given dataset and version")
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
      descr("Updates the metadata for a dataset. If a draft of the dataset already exists, the metadata of that draft is overwritten; otherwise, a new draft is created with this metadata.")
      val json: ScallopOption[jFile] = opt("json-metadata",
        descr = "JSON file containing the new metadata",
        required = true
      )
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
    addSubcommand(updateMetadata)

    val editMetadata = new Subcommand("edit-metadata") {
      descr("Add data to dataset fields that are blank or accept multiple values with the following")
      val json: ScallopOption[jFile] = opt("json-metadata",
        descr = "JSON file containing the new metadata",
        required = true
      )
      val replace: ScallopOption[Boolean] = opt("replace",
        descr = "Replace existing value instead of adding")
    }
    addSubcommand(editMetadata)

    val deleteMetadata = new Subcommand("delete-metadata") {
      descr("You may delete some of the metadata of a dataset version by supplying a file with a JSON representation of dataset fields that you would like to delete.")
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


  }
  addSubcommand(dataset)

  footer("")
}
