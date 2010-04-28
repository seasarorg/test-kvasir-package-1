getPageList.first.length=\
    SELECT ?? FROM ?? ?? LIMIT :length

# FIXME 「offset番目から全部」という意味だが、正しく動かない。
getPageList.offset.all=\
    SELECT LIMIT :offset 0 ?? FROM ?? ??

getPageList.offset.length=\
    SELECT ?? FROM ?? ?? LIMIT :offset, :length

#
# FOR INITIALIZATION
#
initialize.createPageIdSequence=
initialize.createPage= \
    CREATE TABLE page ( \
        id                  INTEGER NOT NULL IDENTITY, \
        type                VARCHAR_IGNORECASE(255) NOT NULL, \
        heimid              INTEGER NOT NULL, \
        lordid              INTEGER NOT NULL, \
        parentpathname   VARCHAR_IGNORECASE(255) NOT NULL, \
        name             VARCHAR_IGNORECASE(255) NOT NULL, \
        ordernumber         INTEGER NOT NULL, \
        createdate          TIMESTAMP NOT NULL, \
        modifydate          TIMESTAMP NOT NULL, \
        revealdate          TIMESTAMP NOT NULL, \
        concealdate         TIMESTAMP NOT NULL, \
        owneruserid         INTEGER NOT NULL, \
        node                BOOLEAN NOT NULL, \
        asfile              BOOLEAN NOT NULL, \
        listing             BOOLEAN NOT NULL, \
        version             INTEGER DEFAULT 1 NOT NULL, \
        UNIQUE              (heimid, parentpathname, name) \
    )
initialize.setPageIdSequence=
