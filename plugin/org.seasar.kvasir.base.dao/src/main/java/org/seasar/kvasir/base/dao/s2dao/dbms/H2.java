/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.kvasir.base.dao.s2dao.dbms;

import org.seasar.dao.dbms.Standard;


/**
 * @author YOKOTA Takehiko
 *
 */
public class H2 extends Standard
{
    /**
     * @see org.seasar.dao.Dbms#getSuffix()
     */
    public String getSuffix()
    {
        return "_h2";
    }


    public String getIdentitySelectString()
    {
        return "CALL IDENTITY()";
    }


    public String getSequenceNextValString(String sequenceName)
    {
        return "CALL NEXTVAL('" + sequenceName + "')";
    }
}
