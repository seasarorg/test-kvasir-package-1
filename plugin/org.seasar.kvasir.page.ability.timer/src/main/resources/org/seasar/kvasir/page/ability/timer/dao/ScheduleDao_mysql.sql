#
# FOR INITIALIZATION
#
initialize.createScheduleIdSequence=
initialize.createSchedule= \
    CREATE TABLE schedule ( \
        id                  INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY, \
        pageid              INTEGER NOT NULL, \
        status              INTEGER NOT NULL DEFAULT 0, \
        scheduleddate       TIMESTAMP NOT NULL, \
        component           VARCHAR(255) NOT NULL, \
        begindate           TIMESTAMP, \
        finishdate          TIMESTAMP, \
        errorinformation    TEXT \
    )
