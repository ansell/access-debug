# Summariser and Mapper for Access files

A mapper for Access databases to create CSV files.

[![Build Status](https://travis-ci.org/ansell/access-debug.svg?branch=master)](https://travis-ci.org/ansell/access-debug) [![Coverage Status](https://coveralls.io/repos/ansell/access-debug/badge.svg?branch=master)](https://coveralls.io/r/ansell/access-debug?branch=master)

# Setup

Install Maven and Git

Download the Git repository.

Set the relevant programs to be executable.

    chmod a+x ./accessmap

# Access Mapper

Access Mapper inherits the functionality of CSV Mapper, so all of the functions and languages available to the CSV Mapper program are available here.

In addition, it adds another supported language, Access. Primary and foreign keys are linked using rows in the mapping column that point from either 1-1 or Many-1 relationships. 1-Many relationships are not supported due to the way each output row is generated based on a single base row from the base table. The base table is chosen based on the table referenced in the first mapping row of the mapping CSV file. Tables are merged based on the Left Outer Join pattern.

## Usage

Run accessmap with --help to get usage details:

    ./accessmap --help

Run accessmap with a sample access file:

    ./accessmap --input src/test/resources/com/github/ansell/csvaccess/test-source.accdb --mapping src/test/resources/com/github/ansell/csvaccess/test-mapping.csv --output target/ --debug true

# Maven

    <dependency>
        <groupId>com.github.ansell.access</groupId>
        <artifactId>access-debug</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>

# Changelog

# 2018-11-09
* Initial port from csvsum
