#
# FOR INITIALIZATION
#
initialize.createTable= \
    CREATE TABLE properties ( \
        pageid              INTEGER NOT NULL, \
        variant             VARCHAR(255) NOT NULL, \
        body                MEDIUMTEXT, \
        UNIQUE              (pageid, variant) \
    )
