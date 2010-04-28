getObjectById=\
    SELECT * FROM page WHERE id=?

getObjectListByIds=\
    SELECT * FROM page WHERE id IN (??)

getObjectByParentPathnameAndName=\
    SELECT * FROM page WHERE heimid=? AND parentpathname=? AND name=?

getPageList.first.all=\
    SELECT ?? FROM ?? ??

getPageList.first.length=\
    SELECT ?? FROM ?? ?? LIMIT :length

getPageList.offset.all=\
    SELECT ?? FROM ?? ?? OFFSET :offset

getPageList.offset.length=\
    SELECT ?? FROM ?? ?? LIMIT :length OFFSET :offset

# 'permission.roleid=8' means "all" role,
# 'casto.groupid=5' means "all" group,
# 'permission.roleid=9' means "owner" role
PageConditionParser.privilegeCondition=\
    page.id=permission.pageid AND permission.type=:permission_type \
    AND permission.level>=:permission_level \
    AND permission.roleid=casto.roleid \
    AND casto.groupid=member.groupid \
    AND (permission.roleid=8 \
    OR casto.groupid=5 \
    OR permission.roleid=9 AND page.owneruserid=:user_id \
    OR casto.userid=:user_id OR member.userid=:user_id)

childNameExists=\
    SELECT COUNT(*) FROM page WHERE heimid=? AND parentpathname=? AND name=?

insert.getMaxOrderNumber=\
    SELECT COALESCE(MAX(ordernumber), 0) FROM page WHERE heimid=? AND parentpathname=?

updateById.where=\
    id=?

deleteById=\
    DELETE FROM page WHERE id=?

changeLordId=\
    UPDATE page SET lordid=?, version=version+1 \
        WHERE heimid=? AND parentpathname=? AND name=? OR lordid=? AND \
            (parentpathname=? OR parentpathname LIKE ? ESCAPE '|')

moveTo.lock=\
    SELECT id FROM page WHERE id IN (?, ?) OR heimid=? AND (parentpathname=? OR parentpathname LIKE ? ESCAPE '|') FOR UPDATE

moveTo.updateTarget=\
    UPDATE page SET parentpathname=?,name=?,version=version+1 WHERE heimid=? AND parentpathname=? AND name=?

moveTo.updateDescendants=\
    UPDATE page SET parentpathname=? || COALESCE(SUBSTRING(parentpathname FROM ?),''),version=version+1 WHERE heimid=? AND (parentpathname=? OR parentpathname LIKE ? ESCAPE '|')

runWithLocking=\
    SELECT id FROM page WHERE id IN (??) FOR UPDATE

releasePages=\
    UPDATE page SET owneruserid=2, version=version+1 \
        WHERE owneruserid=?

#
# FOR INITIALIZATION
#
initialize=createPageIdSequence,createPage, \
    createPageIndex1,createPageIndex2,createPageIndex3, \
    createPageIndex4,createPageIndex5, \
    insertRootPage, \
    insertAdministratorUser,insertAnonymousUser, \
    insertAdministratorGroup,insertAllGroup, \
    insertAdministratorRole,insertAnonymousRole, \
    insertAllRole,insertOwnerRole, \
    insertAlfheimRootPage, \
    insertUsersPage,insertGroupsPage,insertRolesPage, \
    insertTemplatesPage,insertSystemPage,insertPluginsPage, \
    insertDummyPage,setPageIdSequence,deleteDummyPage
initialize.specific.pre=
initialize.specific.post=
initialize.createPageIdSequence= \
    CREATE SEQUENCE _SEQ_page_id
initialize.createPage= \
    CREATE TABLE page ( \
        id                  INTEGER DEFAULT NEXTVAL('_SEQ_page_id') NOT NULL PRIMARY KEY, \
        type                VARCHAR(255) NOT NULL, \
        heimid              INTEGER NOT NULL, \
        lordid              INTEGER NOT NULL, \
        parentpathname   VARCHAR(255) NOT NULL, \
        name             VARCHAR(255) NOT NULL, \
        ordernumber         INTEGER NOT NULL, \
        createdate          TIMESTAMP NOT NULL, \
        modifydate          TIMESTAMP NOT NULL, \
        revealdate          TIMESTAMP NOT NULL, \
        concealdate         TIMESTAMP NOT NULL, \
        owneruserid         INTEGER NOT NULL, \
        node                BOOLEAN NOT NULL, \
        asfile              BOOLEAN NOT NULL, \
        listing             BOOLEAN NOT NULL, \
        version             INTEGER DEFAULT 1 NOT NULL, \
        UNIQUE              (heimid, parentpathname, name) \
    )
initialize.createPageIndex1= \
    CREATE INDEX page_type_idx ON page (type)
initialize.createPageIndex2= \
    CREATE INDEX page_lordid_idx ON page (lordid)
initialize.createPageIndex3= \
    CREATE INDEX page_name_idx ON page (name)
initialize.createPageIndex4= \
    CREATE INDEX page_parentpathname_idx ON page (heimid, parentpathname)
initialize.createPageIndex5= \
    CREATE INDEX page_version_idx ON page (version)
initialize.insertRootPage= \
    INSERT INTO page VALUES (\
        1, 'directory', 0, \
        1, '_', '', \
        1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, \
        CURRENT_TIMESTAMP, '3000-01-01 00:00:00', \
        2, 1, 0, 0, 1 \
    )
initialize.insertAdministratorUser= \
    INSERT INTO page VALUES (\
        2, 'user', 0, \
        1, '/users', 'administrator', \
        1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, \
        CURRENT_TIMESTAMP, '3000-01-01 00:00:00', \
        2, 0, 0, 0, 1 \
    )
initialize.insertAnonymousUser= \
    INSERT INTO page VALUES (\
        3, 'user', 0, \
        1, '/users', 'anonymous', \
        2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, \
        CURRENT_TIMESTAMP, '3000-01-01 00:00:00', \
        2, 0, 0, 0, 1 \
    )
initialize.insertAdministratorGroup= \
    INSERT INTO page VALUES (\
        4, 'group', 0, \
        1, '/groups', 'administrator', \
        1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, \
        CURRENT_TIMESTAMP, '3000-01-01 00:00:00', \
        2, 0, 0, 0, 1 \
    )
initialize.insertAllGroup= \
    INSERT INTO page VALUES (\
        5, 'group', 0, \
        1, '/groups', 'all', \
        2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, \
        CURRENT_TIMESTAMP, '3000-01-01 00:00:00', \
        2, 0, 0, 0, 1 \
    )
initialize.insertAdministratorRole= \
    INSERT INTO page VALUES (\
        6, 'role', 0, \
        1, '/roles', 'administrator', \
        1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, \
        CURRENT_TIMESTAMP, '3000-01-01 00:00:00', \
        2, 0, 0, 0, 1 \
    )
initialize.insertAnonymousRole= \
    INSERT INTO page VALUES (\
        7, 'role', 0, \
        1, '/roles', 'anonymous', \
        2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, \
        CURRENT_TIMESTAMP, '3000-01-01 00:00:00', \
        2, 0, 0, 0, 1 \
    )
initialize.insertAllRole= \
    INSERT INTO page VALUES (\
        8, 'role', 0, \
        1, '/roles', 'all', \
        3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, \
        CURRENT_TIMESTAMP, '3000-01-01 00:00:00', \
        2, 0, 0, 0, 1 \
    )
initialize.insertOwnerRole= \
    INSERT INTO page VALUES (\
        9, 'role', 0, \
        1, '/roles', 'owner', \
        4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, \
        CURRENT_TIMESTAMP, '3000-01-01 00:00:00', \
        2, 0, 0, 0, 1 \
    )
initialize.insertAlfheimRootPage= \
    INSERT INTO page VALUES (\
        10, 'directory', 1, \
        10, '_', '', \
        2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, \
        CURRENT_TIMESTAMP, '3000-01-01 00:00:00', \
        2, 1, 0, 0, 1 \
    )
initialize.insertUsersPage= \
    INSERT INTO page VALUES (\
        101, 'directory', 0, \
        1, '', 'users', \
        1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, \
        CURRENT_TIMESTAMP, '3000-01-01 00:00:00', \
        2, 1, 0, 0, 1 \
    )
initialize.insertGroupsPage= \
    INSERT INTO page VALUES (\
        102, 'directory', 0, \
        1, '', 'groups', \
        2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, \
        CURRENT_TIMESTAMP, '3000-01-01 00:00:00', \
        2, 1, 0, 0, 1 \
    )
initialize.insertRolesPage= \
    INSERT INTO page VALUES (\
        103, 'directory', 0, \
        1, '', 'roles', \
        3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, \
        CURRENT_TIMESTAMP, '3000-01-01 00:00:00', \
        2, 1, 0, 0, 1 \
    )
initialize.insertTemplatesPage= \
    INSERT INTO page VALUES (\
        104, 'directory', 0, \
        1, '', 'templates', \
        4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, \
        CURRENT_TIMESTAMP, '3000-01-01 00:00:00', \
        2, 1, 0, 0, 1 \
    )
initialize.insertSystemPage= \
    INSERT INTO page VALUES (\
        105, 'directory', 0, \
        1, '', 'system', \
        5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, \
        CURRENT_TIMESTAMP, '3000-01-01 00:00:00', \
        2, 1, 0, 0, 1 \
    )
initialize.insertPluginsPage= \
    INSERT INTO page VALUES (\
        106, 'directory', 0, \
        1, '', 'plugins', \
        6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, \
        CURRENT_TIMESTAMP, '3000-01-01 00:00:00', \
        2, 1, 0, 0, 1 \
    )
initialize.insertDummyPage= \
    INSERT INTO page VALUES (\
        1000, '', 1, \
        1, '_', 'dummy', \
        0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, \
        CURRENT_TIMESTAMP, '3000-01-01 00:00:00', \
        0, 0, 0, 0, 1 \
    )
initialize.setPageIdSequence= \
    SELECT SETVAL('_SEQ_page_id', 1000)
initialize.deleteDummyPage= \
    DELETE FROM page WHERE id=1000
