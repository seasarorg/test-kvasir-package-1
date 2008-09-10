#
# FOR INITIALIZATION
#
initialize=createTable
initialize.createTable= \
    CREATE TABLE content ( \
        pageid              INTEGER NOT NULL, \
        modifydate          TIMESTAMP NOT NULL \
    )
