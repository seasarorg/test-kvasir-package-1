#
# FOR INITIALIZATION
#
initialize.createContentsIdSequence=
initialize.createTable= \
    CREATE TABLE contents ( \
        id                  INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY, \
        pageid              INTEGER NOT NULL, \
        variant             VARCHAR(255) NOT NULL, \
        revisionnumber      INTEGER NOT NULL, \
        createdate          DATETIME NOT NULL, \
        modifydate          DATETIME NOT NULL, \
        latest              TINYINT NOT NULL, \
        version             INTEGER DEFAULT 1 NOT NULL, \
        mediatype           VARCHAR(255) NOT NULL, \
        encoding            VARCHAR(255) NOT NULL, \
        UNIQUE              (pageid, variant, revisionnumber) \
    )
