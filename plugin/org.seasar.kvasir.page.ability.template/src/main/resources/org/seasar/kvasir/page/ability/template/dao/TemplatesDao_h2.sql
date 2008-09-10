#
# FOR INITIALIZATION
#
initialize.createTable= \
    CREATE TABLE templates ( \
        pageid              INTEGER NOT NULL, \
        variant             VARCHAR_IGNORECASE(255) NOT NULL, \
        modifydate          TIMESTAMP NOT NULL, \
        body                LONGVARCHAR, \
        UNIQUE              (pageid, variant) \
    )
