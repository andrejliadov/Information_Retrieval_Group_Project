Run the following script:

1)./run.sh

The commands should run the parsing, indexing, query generation and searching. There wil be trec_eval commands added when a QRels file is released.

The results and the index are in the target directory. If there is an index pre-built, the programme will skip document parsing and index creation. Delete the index directory to force index creation. To do this run the following script:

1)./forceRun.sh

This will also run the programme.
