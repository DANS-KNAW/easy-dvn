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

INSTALLATION AND CONFIGURATION
------------------------------
Currently this project is build only as an RPM package for RHEL7/CentOS7 and later. The RPM will install the binaries to
`/opt/dans.knaw.nl/easy-dvn`, the configuration files to `/etc/opt/dans.knaw.nl/easy-dvn`.

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

If the `rpm` executable is found at `/usr/local/bin/rpm`, the build profile that includes the RPM 
packaging will be activated. If `rpm` is available, but at a different path, then activate it by using
Maven's `-P` switch: `mvn -Pprm install`.
