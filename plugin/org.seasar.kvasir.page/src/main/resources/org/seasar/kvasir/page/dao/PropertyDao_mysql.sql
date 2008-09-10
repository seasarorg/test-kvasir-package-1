#
# FOR INITIALIZATION
#
initialize.createTable= \
    CREATE TABLE property ( \
        pageid              INTEGER NOT NULL, \
        name                VARCHAR(255) NOT NULL, \
        value               MEDIUMTEXT, \
        UNIQUE              (pageid, name) \
    )
