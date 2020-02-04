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

import java.io.{File => jFile}
import org.rogach.scallop.{ ScallopConf, ScallopOption, Subcommand, singleArgConverter }

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

  val dataverse = new Subcommand("dataverse") {
    descr("Operations on a dataverse")
    val id: ScallopOption[String] = trailArg("dataverse")
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
      val facets: ScallopOption[List[String]] = opt(name = "facets",
        descr = "Facets to set",
        required = true)
    }
    addSubcommand(setFacets)

    val listAssignments = new Subcommand("list-assignments") {
      descr("List role assignments in a dataverse")
    }
    addSubcommand(listAssignments)

    val setDefaultRole = new Subcommand("set-default-role") {
      descr("Set the default role for a user creating a dataset")
      val role: ScallopOption[String] = opt("role",
        descr = "Role to set ('none' for no role)")
    }
    addSubcommand(setDefaultRole)

    val setRole = new Subcommand("set-role") {
      descr("Assigns a new role, based on the POSTed JSON")
      val json: ScallopOption[jFile] = opt(name = "json",
        descr = "JSON file containing role assignment detail",
        required = true)
      validateFileExists(json)
    }
    addSubcommand(setRole)

    val deleteRole = new Subcommand("delete-role") {
      descr("Deletes are role assignment")
      val roleId: ScallopOption[String] = opt(name = "role-id",
        descr = "ID of the role assignment to delete",
        required = true)
    }
    addSubcommand(deleteRole)

    val listMetadataBlocks = new Subcommand("list-metadata-blocks") {
      descr("Gets the metadata blocks defined on the passed dataverse")
    }
    addSubcommand(listMetadataBlocks)

    val setMetadataBlocks = new Subcommand("set-metadata-blocks") {
      descr("Sets the metadata blocks of the dataverse")
      val metadataBlockIds: ScallopOption[List[String]] = opt(name = "metadata-block-id",
        descr = "Identifiers of metadata blocks to set",
        required = true)
    }
    addSubcommand(setMetadataBlocks)

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

  val dataset = new Subcommand("dataverse") {






    
  }


  footer("")
}
