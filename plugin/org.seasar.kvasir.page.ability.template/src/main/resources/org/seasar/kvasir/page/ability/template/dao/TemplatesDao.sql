#
# FOR INITIALIZATION
#
initialize=createTable,createIndex
initialize.createTable= \
    CREATE TABLE templates ( \
        pageid              INTEGER NOT NULL, \
        variant             VARCHAR(255) NOT NULL, \
        modifydate          TIMESTAMP NOT NULL, \
        body                TEXT, \
        UNIQUE              (pageid, variant) \
    )
initialize.createIndex= \
    CREATE INDEX templates_pageid_variant_idx ON templates (pageid, variant)
