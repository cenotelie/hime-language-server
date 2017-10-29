# Hime Language Server #

This repository contains the sources for the implementation of a language server for the [Hime](https://bitbucket.org/cenotelie/hime) grammar language.
This language server uses an implementation of the [Language Server Protocol](https://langserver.org/) provided by the [Cénotélie Commons](https://bitbucket.org/cenotelie/commons) project.


## How do I use this software? ##

This software can be used as a library in order to embed the Hime language server into your project.
To do so, just add the following Maven dependency:

```
<dependency>
    <groupId>fr.cenotelie.hime</groupId>
    <artifactId>hime-language-server</artifactId>
    <version>1.0.1</version>
    <scope>compile</scope>
</dependency>
```

You can also use the resulting self-contained [JAR file](https://repo.maven.apache.org/maven2/fr/cenotelie/hime/hime-language-server/1.0.0/hime-language-server-1.0.1-jar-with-dependencies.jar) to execute the language server.
This is useful if you which to embed it into a plugin for an editor.
The communication with the server is simply through the standard input and output streams.


## How to build ##

To build the artifacts in this repository using Maven:

```
$ mvn clean install -Dgpg.skip=true
```


## How can I contribute? ##

The simplest way to contribute is to:

* Fork this repository on [Bitbucket](https://bitbucket.org/cenotelie/hime-language-server).
* Fix [some issue](https://bitbucket.org/cenotelie/hime-language-server/issues?status=new&status=open) or implement a new feature.
* Create a pull request on Bitbucket.

Patches can also be submitted by email, or through the [issue management system](https://bitbucket.org/cenotelie/hime-language-server/issues).

The [isse tracker](https://bitbucket.org/cenotelie/hime-language-server/issues) may contain tickets that are accessible to newcomers. Look for tickets with `[beginner]` in the title. These tickets are good ways to become more familiar with the project and the codebase.


## License ##

This software is licenced under the Lesser General Public License (LGPL) v3.
Refers to the `LICENSE.txt` file at the root of the repository for the full text, or to [the online version](http://www.gnu.org/licenses/lgpl-3.0.html).
