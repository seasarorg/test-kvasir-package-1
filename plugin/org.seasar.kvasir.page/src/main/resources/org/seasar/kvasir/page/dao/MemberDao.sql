#
# FOR INITIALIZATION
#
initialize=createMember,createMemberIndex1,\
    insertDummyMember,insertAdministratorMember,insertDummyAllMember
initialize.createMember= \
    CREATE TABLE member ( \
        groupid             INTEGER NOT NULL, \
        userid              INTEGER NOT NULL, \
        PRIMARY KEY         (groupid, userid) \
    )
initialize.createMemberIndex1= \
    CREATE INDEX member_userid_idx ON member (userid)
initialize.insertDummyMember= \
    INSERT INTO member VALUES (0, 0)
initialize.insertAdministratorMember= \
    INSERT INTO member VALUES (4, 2)
initialize.insertDummyAllMember= \
    INSERT INTO member VALUES (5, 0)

#
# FOR ACCESSING
#
addUserByGroupId.exists=\
    SELECT COUNT(*) FROM member WHERE groupid=? AND userid=?

addUserByGroupId.insert=\
    INSERT INTO member (groupid,userid) VALUES (?,?)
