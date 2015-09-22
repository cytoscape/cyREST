![](http://cl.ly/XohP/logo300.png)

# cyREST: a language-agnostic RESTful API for Cytoscape

[![Build Status](https://travis-ci.org/cytoscape/cyREST.svg?branch=master)](https://travis-ci.org/cytoscape/cyREST)

[![Coverage Status](https://coveralls.io/repos/cytoscape/cyREST/badge.svg)](https://coveralls.io/r/cytoscape/cyREST)

![](http://cl.ly/Xemf/networkx_cytoscape.png)

----

# Citation
__We need your support to make this project sustainable__.  Please cite the following paper when you use cyREST in your projects:

> Ono, Keiichiro, et al. [CyREST: Turbocharging Cytoscape Access for External Tools via a RESTful API](http://f1000research.com/articles/4-478/v1). F1000Research 4 (2015).


----

## Introduction

### Important: Now cyREST is a Core App
From Cytoscape 3.3.0, cyREST is a part of core distribution, as a ___Core App___.  The main differences are the following:

* You don't have to install cyREST manually.  It is installed by default
* API 
* Although it is part of core distribution, you will get updates after 3.3.0 release
* Code repository had been moved to the Cytoscape Consortium GitHub account


### In One Sentence
__An application to control [Cytoscape](http://www.cytoscape.org) from [RStudio](http://www.rstudio.com/), [IPython Notebook](http://ipython.org/notebook.html), [Node.js](http://nodejs.org/) or other programming languages.__

### More Details
This is a Cytoscape App to provide low-level API access to Cytoscape data objects, including networks, data tables, and Visual Styles, for programming languages such as R, Python, JavaScript, and MATLAB via RESTful API.  You can write your own Cytoscape workflows with programming languages of your choice.

__This app is still in beta status and we need your feedback!__

## System Requirements
To use cyREST 0.9.16 and newer, you need the following:

* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
    * Oracle JDK is recommended, but should be compatible with OpenJDK.
    * __Does not work with Java 7 and older!!__
* [Cytoscape 3.2.1+](http://www.cytoscape.org/)

## Documentation
__All documents, including tutorials and full API list, are available from our [Wiki](https://github.com/idekerlab/cyREST/wiki)__.

## Problems or Feature Requests?
The API Version 1 is not finalized yet.  Please send your feature requests to our [mailing list](https://groups.google.com/forum/#!forum/cytoscape-discuss).

Please report the problems to our issue tracker:

* [cyREST Issue Tracker](https://github.com/idekerlab/cyREST/issues)

And of course, pull requests are always welcome!

## License
* Source Code: [The MIT license](http://opensource.org/licenses/MIT)
* Documentation: [CC BY-SA 4.0](http://creativecommons.org/licenses/by-sa/4.0/)

----
&copy; 2014-2015 [Cytoscape Consortium](http://www.cytoscape.org/).  Developed and maintained by [Keiichiro Ono](http://keiono.github.io/), [UCSD Trey Ideker Lab](http://idekerlab.ucsd.edu/Pages/default.aspx).
