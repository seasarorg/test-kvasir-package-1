#
# FOR INITIALIZATION
#
initialize=createTable,createIndex,insertAdministratorUserPassword
initialize.createTable= \
    CREATE TABLE property ( \
        pageid              INTEGER NOT NULL, \
        name                VARCHAR(255) NOT NULL, \
        value               TEXT, \
        UNIQUE              (pageid, name) \
    )
initialize.createIndex= \
    CREATE INDEX property_pageid_name_idx ON property (pageid, name)
initialize.insertAdministratorUserPassword= \
    INSERT INTO property VALUES (\
        2, 'user.password', '*')
