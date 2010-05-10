#
# FOR INITIALIZATION
#
initialize.createScheduleIdSequence=
initialize.createSchedule= \
    CREATE TABLE schedule ( \
        id                  INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY, \
        pageid              INTEGER NOT NULL, \
        dayofweek           VARCHAR(128) NOT NULL, \
        year                VARCHAR(128) NOT NULL, \
        month               VARCHAR(128) NOT NULL, \
        day                 VARCHAR(128) NOT NULL, \
        hour                VARCHAR(128) NOT NULL, \
        minute              VARCHAR(128) NOT NULL, \
        pluginid            VARCHAR(255) NOT NULL, \
        component           VARCHAR(255) NOT NULL, \
        parameter           VARCHAR(255), \
        enabled             INTEGER NOT NULL DEFAULT 1 \
    )
