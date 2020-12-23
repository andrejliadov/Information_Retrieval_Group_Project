# Group Project

## Members

* Andrej Liadov
* Matthew Ng
* Philippa Gilsenan
* Rui Xu


## Instructions

### To build and run the project

`run.sh`

The commands run the parsing, indexing, query generation and searching. There wil be trec_eval commands added when a QRels file is released.

The results and the index are in the target directory. If there is an index pre-built, the programme will skip document parsing and index creation.


### Re-index and run project

Delete the index directory to force index creation. To do this run the following script:

`./forceRun.sh`

This will also run the programme.
