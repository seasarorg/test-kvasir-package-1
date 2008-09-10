#
# FOR INITIALIZATION
#
initialize.createContentsIdSequence=
initialize.createTable= \
    CREATE TABLE contents ( \
        id                  INTEGER NOT NULL IDENTITY, \
        pageid              INTEGER NOT NULL, \
        variant             VARCHAR_IGNORECASE(255) NOT NULL, \
        revisionnumber      INTEGER NOT NULL, \
        createdate          TIMESTAMP NOT NULL, \
        modifydate          TIMESTAMP NOT NULL, \
        latest              BOOLEAN NOT NULL, \
        version             INTEGER DEFAULT 1 NOT NULL, \
        mediatype           VARCHAR_IGNORECASE(255) NOT NULL, \
        encoding            VARCHAR_IGNORECASE(255) NOT NULL, \
        UNIQUE              (pageid, variant, revisionnumber) \
    )

#
# FOR ACCESSING
#
