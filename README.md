easy-dvn
===========
[![Build Status](https://travis-ci.org/DANS-KNAW/easy-dvn.png?branch=master)](https://travis-ci.org/DANS-KNAW/easy-dvn)

<!-- Remove this comment and extend the descriptions below -->


SYNOPSIS
--------

    easy-dvn (synopsis of command line parameters)
    easy-dvn (... possibly multiple lines for subcommands)


DESCRIPTION
-----------

Command-line tool for working with the Dataverse API


ARGUMENTS
---------

    Options:

       -h, --help      Show help message
       -v, --version   Show version of this program

    Subcommand: run-service - Starts EASY Dvn as a daemon that services HTTP requests
       -h, --help   Show help message
    ---

EXAMPLES
--------

    easy-dvn -o value

INSTALLATION AND CONFIGURATION
------------------------------
Currently this project is build only as an RPM package for RHEL7/CentOS7 and later. The RPM will install the binaries to
`/opt/dans.knaw.nl/easy-dvn`, the configuration files to `/etc/opt/dans.knaw.nl/easy-dvn`,
and will install the service script for `systemd`. 

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
