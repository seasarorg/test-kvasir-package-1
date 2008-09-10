#
# FOR INITIALIZATION
#
initialize=createCast,createCastIndex1,createCastIndex2, \
    insertAdministratorCast,insertAnonymousCast, \
    insertAllCast,insertOwnerCast
initialize.createCast= \
    CREATE TABLE casto ( \
        roleid              INTEGER NOT NULL, \
        groupid             INTEGER NOT NULL, \
        userid              INTEGER NOT NULL, \
        PRIMARY KEY         (roleid, groupid, userid) \
    )
initialize.createCastIndex1= \
    CREATE INDEX casto_groupid_idx ON casto (groupid)
initialize.createCastIndex2= \
    CREATE INDEX casto_userid_idx ON casto (userid)
initialize.insertAdministratorCast= \
    INSERT INTO casto VALUES (6, 4, 0)
initialize.insertAnonymousCast= \
    INSERT INTO casto VALUES (7, 0, 3)
initialize.insertAllCast= \
    INSERT INTO casto VALUES (8, 5, 0)
initialize.insertOwnerCast= \
    INSERT INTO casto VALUES (9, 0, 0)

#
# FOR ACCESSING
#
isUserInRole=\
    SELECT COUNT(*) FROM casto,member \
    WHERE casto.roleid=? AND casto.groupid=member.groupid \
    AND (casto.userid=? OR member.userid=? OR member.groupid=5)

giveRoleToUser.exists=\
    SELECT COUNT(*) FROM casto WHERE roleid=? AND userid=?

giveRoleToUser.insert=\
    INSERT INTO casto (roleid,groupid,userid) VALUES (?,0,?)

giveRoleToGroup.exists=\
    SELECT COUNT(*) FROM casto WHERE roleid=? AND groupid=?

giveRoleToGroup.insert=\
    INSERT INTO casto (roleid,groupid,userid) VALUES (?,?,0)
