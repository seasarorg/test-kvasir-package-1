#
# FOR INITIALIZATION
#
initialize=createTable
initialize.createTable= \
    CREATE TABLE template ( \
        pageid              INTEGER NOT NULL PRIMARY KEY, \
        type                VARCHAR(255) NOT NULL, \
        responsecontenttype VARCHAR(255) NOT NULL \
    )
