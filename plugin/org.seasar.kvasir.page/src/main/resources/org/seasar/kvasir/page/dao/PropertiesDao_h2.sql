#
# FOR INITIALIZATION
#
initialize.createTable= \
    CREATE TABLE properties ( \
        pageid              INTEGER NOT NULL, \
        variant             VARCHAR_IGNORECASE(255) NOT NULL, \
        body                LONGVARCHAR, \
        UNIQUE              (pageid, variant) \
    )
