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
    val create = new Subcommand("create") {
      descr("Create a new dataverse")
      val parent: ScallopOption[String] = opt(name = "parent",
        descr = "ID of parent dataverse",
        required = true)
      val json: ScallopOption[jFile] = opt(name = "json",
        descr = "JSON file containing the properties of the new dataverse",
        required = true)
      validateFileExists(json)
      descr("Creates a dataverse")
    }
    addSubcommand(create)

    val view = new Subcommand("view") {
      descr("View metadata about a dataverse")
      val id: ScallopOption[String] = trailArg("id",
        descr = "ID of the dataverse to view",
        required = true)
    }
    addSubcommand(view)

    val delete = new Subcommand("delete") {
      descr("Delete a dataverse")
      val id: ScallopOption[String] = trailArg("id",
        descr = "ID of the dataverse to delete",
        required = true)
    }
    addSubcommand(delete)

    val show = new Subcommand("show") {
      descr("Show the contents of a dataverse")
      val id: ScallopOption[String] = trailArg("id",
        descr = "ID of the dataverse to show the contents of",
        required = true)
    }
    addSubcommand(show)

    val listRoles = new Subcommand("list-roles") {
      descr("List roles in a dataverse")
      val id: ScallopOption[String] = trailArg("id",
        descr = "ID of the dataverse to show the roles of",
        required = true)
    }
    addSubcommand(listRoles)

    val createRole = new Subcommand("create-role") {
      descr("Create a new role in a dataverse")
      val id: ScallopOption[String] = trailArg("id",
        descr = "ID of the dataverse to create a new role in",
        required = true)
      val json: ScallopOption[jFile] = opt(name = "json",
        descr = "JSON file containing the definition of the new role",
        required = true)
    }
    addSubcommand(createRole)


    val listFacets = new Subcommand("list-facets") {
      descr("List facets in a dataverse")
      val id: ScallopOption[String] = trailArg("id",
        descr = "ID of the dataverse to show the facets of",
        required = true)
    }
    addSubcommand(listFacets)

    val setFacets = new Subcommand("set-facets") {
      descr("Set the facets fore a dataverse")
      val id: ScallopOption[String] = trailArg("id",
        descr = "ID of the dataverse to set the facets for",
        required = true)
      val facets: ScallopOption[List[String]] = opt(name = "facets",
        descr = "Facets to set",
        required = true)
    }
    addSubcommand(setFacets)

    val listAssignments = new Subcommand("list-assignments") {
      descr("List role assignments in a dataverse")
      val id: ScallopOption[String] = trailArg("id",
        descr = "ID of the dataverse to list the role assignments of",
        required = true)
    }
    addSubcommand(listAssignments)

    val setDefaultRole = new Subcommand("set-default-role") {
      descr("Set the default role for a user creating a dataset")
      val id: ScallopOption[String] = trailArg("id",
        descr = "ID of the dataverse to set the default role on",
        required = true)
      val role: ScallopOption[String] = opt("role",
        descr = "Role to set ('none' for no role)")
    }
    addSubcommand(setDefaultRole)



  }
  addSubcommand(dataverse)

  footer("")
}
