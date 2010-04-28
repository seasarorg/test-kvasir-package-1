#
# FOR INITIALIZATION
#
initialize=createScheduleIdSequence,createSchedule,createSchedulePageidIndex
initialize.createScheduleIdSequence= \
    CREATE SEQUENCE _SEQ_schedule_id
initialize.createSchedule= \
    CREATE TABLE schedule ( \
        id                  INTEGER DEFAULT NEXTVAL('_SEQ_schedule_id') NOT NULL PRIMARY KEY, \
        pageid              INTEGER NOT NULL, \
        status              INTEGER NOT NULL DEFAULT 0, \
        scheduleddate       TIMESTAMP NOT NULL, \
        component           VARCHAR(255) NOT NULL, \
        begindate           TIMESTAMP, \
        finishdate          TIMESTAMP, \
        errorinformation    CLOB \
    )
