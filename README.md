easy-dvn
========

Command-line tool exploring the Dataverse API

SYNOPSIS
--------

      easy-dvn dataverse <alias> <sub-command>
      easy-dvn dataset [-p] <id> <sub-command>
      easy-dvn file [-p] <id> <sub-command>


DESCRIPTION
-----------
Command-line tool for exploring the Dataverse NativeAPI. 


ARGUMENTS
---------

    Options:

         -h, --help      Show help message
         -v, --version   Show version of this program
       
       Subcommand: dataverse - Operations on a dataverse
         -h, --help   Show help message
       
        trailing arguments:
         dataverse-alias (required)   The dataverse alias
       
       Subcommands:
         create                     Create a dataverse
         view                       View metadata about a dataverse
         delete                     Delete a dataverse
         show                       Show the contents of a dataverse
         list-roles                 List roles in a dataverse
         create-role                Create a new role in a dataverse
         list-facets                List facets in a dataverse
         set-facets                 Set the facets fore a dataverse
         list-role-assignments      List role assignments in a dataverse
         set-default-role           Set the default role for a user creating a dataset
         assign-role                Assign a new role, based on the POSTed JSON
         unassign-role              Delete ar role assignment
         list-metadata-blocks       Get the metadata blocks defined on the passed dataverse
         set-metadata-blocks        Set the metadata blocks of the dataverse
         is-metadata-blocks-root    Return whether this dataverse inherits metadata blocks from its parent
         set-metadata-blocks-root   Set whether this dataverse inherits metadata blocks from its parent
         create-dataset             Create a new dataset in this dataverse
         import-dataset             Import a dataset with existing PID into this dataverse
         publish                    Publish the dataverse
       ------------------
       
       
       Subcommand: dataset
         -p, --persistent-id   Use the persistent instead or internal ID
         -h, --help            Show help message
       
        trailing arguments:
         dataset-id (required)   Dataset (persistent) ID
       
       Subcommands:
         view                         View metadata about a dataverse
         delete                       Delete the dataset
         list-versions                List the versions of a dataset
         export-metadata-to           Export the metadata of the current published version of a dataset in various formats
         list-files                   List all the file metadata, for the given dataset and version
         list-metadata-blocks         List all the metadata blocks and their content, for the given dataset and version
         update-metadata              Update the metadata for a dataset.
         edit-metadata                Add metadata to dataset fields that are blank or accept multiple values with the following
         delete-metadata              Delete metadata from a dataset
         publish                      Publish a dataset.
         delete-draft                 Deletes the draft version
         set-citation-date-field      Sets the date field to use in the citation
         revert-citation-date-field   Reverts the date field to use in the citation to the default
         list-role-assignments        List all the role assignments at the given dataset
         create-private-url           Create a Private URL (must be able to manage dataset permissions)
         get-private-url              Get the Private URL if exists (must be able to manage dataset permissions)
         delete-private-url           Deletes the Private URL if exists (must be able to manage dataset permissions)
         add-file                     Adds a data file
         submit-for-review            Submit dataset for review
         return-to-author             Return a dataset that is in review to the author
         link                         Creates a link to this dataset in another dataverse
         get-locks                    Display information about current locks on this dataset
       ------------------
       
       
       Subcommand: file
         -p, --persistent-id   Use the persistent instead or internal ID
         -h, --help            Show help message
       
        trailing arguments:
         file-id (required)   File (persistent) ID
       
       Subcommands:
         restrict         Set the file to restricted
         unrestrict       Set the file to unrestricted
         replace          Replace a data file
         uningest         Uningest a tabular file
         reingest         Reingest a tabular file
         get-provenance   Returns the provenance for this file
       ------------------
       
EXAMPLES
--------

        easy-dvn dataverse foo view # View a description of the dataverse with alias 'foo'
        easy-dvn dataverse create        
       

INSTALLATION AND CONFIGURATION
------------------------------
1. Build the project
2. Unarchive the .tar.gz file from the `target` folder to a location of your choice. This results in an installation directory.
3. Add the `<installation directory>/bin` your path.
4. Log on to Dataverse and copy your API token
5. Configure the correct Dataverse URL and API in the file `<installation directory>/cfg/application.properties`.
6. Test your installation by opening a terminal prompt and:
   
        easy-dvn dataverse root view
   This should result in a JSON object describing the root dataverse, something like:
   
        {
          "status": "OK",
          "data": {
            "id": 1,
            "alias": "root",
            "name": "Root",
            "dataverseContacts": [
              {
                "displayOrder": 0,
                "contactEmail": "root@mailinator.com"
              }
            ],
            "permissionRoot": true,
            "description": "The root dataverse.",
            "dataverseType": "UNCATEGORIZED",
            "creationDate": "2020-05-18T15:09:52Z"
          }
        }OK: Retrieved URL: http://localhost:8080/api/v1/dataverses/root
   If you get instead the following message, your API token may be incorrect or belong to a user with insufficient privileges:
   
        ERROR: Command could not be executed. Server returned: HTTP/1.1 401 Unauthorized
  

BUILDING FROM SOURCE
--------------------
Prerequisites:

* Java 8 or higher
* Maven 3.3.3 or higher
* RPM

Steps:
    
    git clone https://github.com/DANS-KNAW/easy-dvn.git
    cd easy-dvn 
    mvn clean install

By default only a `tar.gz` package will be built, as this program is mainly intended for use on a Mac. If you want to build an RPM 
package you have to activate the RPM profile explicitly:

    mvn -Pprm install
