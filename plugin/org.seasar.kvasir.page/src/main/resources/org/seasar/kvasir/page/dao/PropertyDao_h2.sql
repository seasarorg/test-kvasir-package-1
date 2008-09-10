#
# FOR INITIALIZATION
#
initialize.createTable= \
    CREATE TABLE property ( \
        pageid              INTEGER NOT NULL, \
        name                VARCHAR_IGNORECASE(255) NOT NULL, \
        value               LONGVARCHAR, \
        UNIQUE              (pageid, name) \
    )
