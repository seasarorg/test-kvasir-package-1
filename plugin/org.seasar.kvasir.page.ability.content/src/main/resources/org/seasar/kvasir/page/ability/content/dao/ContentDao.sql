#
# FOR INITIALIZATION
#
initialize=createTable,createIndex1
initialize.createTable= \
    CREATE TABLE content ( \
        pageid              INTEGER NOT NULL, \
        modifydate          TIMESTAMP NOT NULL \
    )
initialize.createIndex1= \
    CREATE INDEX content_pageid_idx ON content (pageid)
    