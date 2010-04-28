#
# FOR INITIALIZATION
#
initialize=createContentsIdSequence,createTable
initialize.createContentsIdSequence= \
    CREATE SEQUENCE _SEQ_contents_id
initialize.createTable= \
    CREATE TABLE contents ( \
        id                  INTEGER DEFAULT NEXTVAL('_SEQ_contents_id') NOT NULL PRIMARY KEY, \
        pageid              INTEGER NOT NULL, \
        variant             VARCHAR(255) NOT NULL, \
        revisionnumber      INTEGER NOT NULL, \
        createdate          TIMESTAMP NOT NULL, \
        modifydate          TIMESTAMP NOT NULL, \
        latest              BOOLEAN NOT NULL, \
        version             INTEGER DEFAULT 1 NOT NULL, \
        mediatype           VARCHAR(255) NOT NULL, \
        encoding            VARCHAR(255) NOT NULL, \
        UNIQUE              (pageid, variant, revisionnumber) \
    )
