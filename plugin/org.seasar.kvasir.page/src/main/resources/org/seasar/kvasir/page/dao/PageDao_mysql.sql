getPageList.first.length=\
    SELECT ?? FROM ?? ?? LIMIT :length

getPageList.offset.all=\
    SELECT ?? FROM ?? ?? LIMIT :offset,-1

getPageList.offset.length=\
    SELECT ?? FROM ?? ?? LIMIT :offset,:length

moveTo.updateDescendants=\
    UPDATE page SET parentpathname=CONCAT(?,COALESCE(SUBSTRING(parentpathname FROM ?),'')),version=version+1 WHERE heimid=? AND (parentpathname=? OR parentpathname LIKE ? ESCAPE '|')

#
# FOR INITIALIZATION
#
initialize.createPageIdSequence=
initialize.createPage= \
    CREATE TABLE page ( \
        id                  INTEGER NOT NULL AUTO_INCREMENT, \
        type                VARCHAR(255) NOT NULL, \
        heimid              INTEGER NOT NULL, \
        lordid              INTEGER NOT NULL, \
        parentpathname   VARCHAR(200) NOT NULL, \
        name             VARCHAR(100) NOT NULL, \
        ordernumber         INTEGER NOT NULL, \
        createdate          DATETIME NOT NULL, \
        modifydate          DATETIME NOT NULL, \
        revealdate          DATETIME NOT NULL, \
        concealdate         DATETIME NOT NULL, \
        owneruserid         INTEGER NOT NULL, \
        node                TINYINT NOT NULL, \
        asfile              TINYINT NOT NULL, \
        listing             TINYINT NOT NULL, \
        version             INTEGER DEFAULT 1 NOT NULL, \
        PRIMARY KEY         (id), \
        UNIQUE              (heimid, parentpathname, name) \
    )
initialize.setPageIdSequence=
