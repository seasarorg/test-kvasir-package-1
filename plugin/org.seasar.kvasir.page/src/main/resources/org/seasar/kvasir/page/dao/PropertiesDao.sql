#
# FOR INITIALIZATION
#
initialize=createTable,createIndex
initialize.createTable= \
    CREATE TABLE properties ( \
        pageid              INTEGER NOT NULL, \
        variant             VARCHAR(255) NOT NULL, \
        body                TEXT, \
        UNIQUE              (pageid, variant) \
    )
initialize.createIndex= \
    CREATE INDEX properties_pageid_variant_idx ON properties (pageid, variant)
