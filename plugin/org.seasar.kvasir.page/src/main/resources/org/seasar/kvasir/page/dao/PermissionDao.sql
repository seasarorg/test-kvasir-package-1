#
# FOR INITIALIZATION
#
initialize=createPermission,createPermissionIndex1,insertDummyPermission,\
    insertRootPagePermission
initialize.createPermission= \
    CREATE TABLE permission ( \
        pageid              INTEGER NOT NULL, \
        roleid              INTEGER NOT NULL, \
        type                INTEGER NOT NULL, \
        level               INTEGER NOT NULL, \
        PRIMARY KEY         (pageid, roleid, type) \
    )
initialize.createPermissionIndex1= \
    CREATE INDEX permission_roleid_idx ON permission (roleid)
initialize.insertDummyPermission= \
    INSERT INTO permission VALUES (0, 8, 0, 0)
initialize.insertRootPagePermission= \
    INSERT INTO permission VALUES (1, 8, 0, 2)

#
# FOR ACCESSING
#
permits=\
    SELECT COALESCE(MAX(permission.level),0) \
        FROM page,permission,casto,member \
        WHERE page.id=permission.pageid \
            AND permission.roleid=casto.roleid \
            AND casto.groupid=member.groupid \
            AND page.id=? \
            AND permission.type=? \
            AND (casto.userid=? \
                OR member.userid=? \
                OR member.groupid=5 \
                OR casto.roleid=9 AND page.owneruserid=?)

getPermissionListByPageId=\
    SELECT roleid,type,level \
         FROM permission \
         WHERE pageid=?

setPermissionsByPageId.clear=\
    DELETE FROM permission WHERE pageid=?

setPermissionsByPageId.insert=\
    INSERT INTO permission (pageid,roleid,type,level) VALUES (?,?,?,?)

clearPermissionsByPageId=\
    DELETE FROM permission WHERE pageid=?

grantPrivilegeByPageId.exists=\
    SELECT COUNT(*) FROM permission WHERE pageid=:pageid AND roleid=:roleid AND type=:type

grantPrivilegeByPageId.insert=\
    INSERT INTO permission (pageid,roleid,type,level) VALUES (:pageid,:roleid,:type,:level)

grantPrivilegeByPageId.update=\
    UPDATE permission SET level=:level WHERE pageid=:pageid AND roleid=:roleid AND type=:type

revokePrivilegeByPageIdAndRoleIdAndPrivType=\
    DELETE FROM permission WHERE pageid=? AND roleid=? AND type=?

revokePrivilegesByPageIdAndRoleId=\
    DELETE FROM permission WHERE pageid=? AND roleid=?

revokePrivilegesByRoleId=\
    DELETE FROM permission WHERE roleid=?

getPrivilegeLevelByPageIdAndRoleIdAndPrivType=\
    SELECT level FROM permission WHERE pageid=? AND roleid=? AND type=?
