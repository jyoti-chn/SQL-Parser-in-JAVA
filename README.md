# SQL Parser in JAVA

## _TABLE OF CONTENTS_
  - [ABOUT THE PROJECT](#about-the-project)
  - [INSTALLATION](#installation)
    - [Prequisite](#prequisite)
    - [Clone the Repo](#clone-the-repo)
    - [Compiling the Parser](#compiling-the-parser)
    - [Running Test Cases](#running-test-cases)
    - [Outputs](#outputs)
  


## _ABOUT THE PROJECT_

The SQL parser is written in Java using top-down recursive descent parsing that accepts SQL statements and keeps track of all constraints like tables, primary-secondary keys, joins.. It analyzes SQL queries and produces a parse tree, which is used to evaluate the query. The parser works by breaking down the query into its constituent parts, such as keywords, identifiers, and literals, and then matching these parts against a set of grammar rules.
    
The parser begins with a top-level nonterminal symbol, such as "SELECT", and uses a recursive descent algorithm to generate a parse tree by recursively matching the input against the grammar rules. The algorithm starts by looking for the top-level symbol, and then looks for the next symbol in the input that matches the grammar rule. If a match is found, the algorithm moves on to the next symbol, and so on, until the entire query has been parsed.
Here the parser uses separate methods or functions to handle different syntactic constructs in the input language. In our code, each method corresponds to a specific production rule in the SQL grammar.
> For example, the method `parseSelectStatement()` starts by checking if the input starts with the keyword "SELECT", followed by a select list (columns to be selected) and a "FROM" clause. It then checks if there are optional clauses such as WHERE, GROUP BY, HAVING, JOIN, and ORDER BY, by calling the corresponding parsing methods. If all clauses are parsed correctly, the method returns true, indicating that the input is a valid SELECT statement in SQL.


During the parsing process, if the parser encounters errors, such as syntax errors or semantic errors,it report it to the user.


## _INSTALLATION_

## Prequisite

Java Development Kit (JDK): This is a software development kit that includes the Java Runtime Environment (JRE), which is necessary to run Java applications.

```sh
sudo apt install default-jdk
```
```sh
java -version
```

## Clone the Project

Clone the project from the following github link into your system.

```sh
git clone https://github.com/sweety-sg/SQL-Parser-in-JAVA
```


## Compiling the Parser

Open the directory where you have cloned the repository and run following commands:

Compile the PARSER
```console
javac SQLParserExample.java
```

## Running Test Cases

In the test file, `SQLParser parser = new SQLParser(TC)`; creats an instance of a SQLParser object, which is used for parsing SQL statements. The constructor for this object takes a single argument, which is a string representing the SQL statement to be parsed. Once you have created the SQLParser object, you can use its methods to parse the SQL statement and extract information from it. 

Here `TC` is a string variable test case (SQL query). We have provided ten different test cases (named TC1, TC2...TC10).

Compile the Test Cases file

```
To test a string , create a SQL parser object for the string in the SQLParserTest file -
SQLParser parser = new SQLParser(str);
```

```console
javac SQLParserTest.java
```


Run the file

```console
java SQLParserTest
```

## Output

* If the input file is syntanctically and semantically correct, the parsing is succesful and the output is a message, followed by the details of table, columns, primary and secondary keys. 

```console
No syntax/lexical errors found
```
   

* In case any syntax error is found, the parsing fails and output is

```console
Parsing failed. Please check for sytax errors.
```


