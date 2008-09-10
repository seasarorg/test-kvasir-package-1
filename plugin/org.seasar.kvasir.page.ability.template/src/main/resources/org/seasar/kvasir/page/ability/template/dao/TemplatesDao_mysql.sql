#
# FOR INITIALIZATION
#
initialize.createTable= \
    CREATE TABLE templates ( \
        pageid              INTEGER NOT NULL, \
        variant             VARCHAR(255) NOT NULL, \
        modifydate          DATETIME NOT NULL, \
        body                MEDIUMTEXT, \
        UNIQUE              (pageid, variant) \
    )
