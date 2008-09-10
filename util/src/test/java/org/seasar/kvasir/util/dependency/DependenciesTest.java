package org.seasar.kvasir.util.dependency;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;


/**
 * @author YOKOTA Takehiko
 */
public class DependenciesTest extends TestCase
{
    public void test1()
    {
        List list = new ArrayList();
        list.add(new MockDependant("E", false, new Requirement[]{
            new MockRequirement("B"), new MockRequirement("D")
        }));
        list.add(new MockDependant("D", false, new Requirement[]{
            new MockRequirement("C")
        }));
        list.add(new MockDependant("C", false, new Requirement[]{
            new MockRequirement("A")
        }));
        list.add(new MockDependant("B", false, new Requirement[]{
            new MockRequirement("A")
        }));
        list.add(new MockDependant("A", false, new Requirement[0]));
        Dependencies deps = null;
        try {
            deps = new Dependencies(list);
        } catch (LoopDetectedException ex) {
            fail();
        }
        Dependency[] ds = deps.getDependencies();
        assertEquals(5, ds.length);
        int idx = 0;
        assertEquals("A", ds[idx++].getId());
        assertEquals("C", ds[idx++].getId());
        assertEquals("D", ds[idx++].getId());
        assertEquals("B", ds[idx++].getId());
        assertEquals("E", ds[idx++].getId());
    }


    public void test2()
    {
        List list = new ArrayList();
        list.add(new MockDependant("A", false, new Requirement[]{
            new MockRequirement("B")
        }));
        list.add(new MockDependant("B", false, new Requirement[]{
            new MockRequirement("C")
        }));
        list.add(new MockDependant("C", false, new Requirement[]{
            new MockRequirement("A")
        }));
        try {
            new Dependencies(list);
            fail();
        } catch (LoopDetectedException ex) {
            assertEquals("A", ex.getRequirement().getId());
            assertEquals("C", ex.getDependant().getId());
        }
    }


    public void test3()
        throws Exception
    {
        List list = new ArrayList();
        list.add(new MockDependant("F", false, new Requirement[0]));
        list.add(new MockDependant("T", false, new Requirement[0]));
        list.add(new MockDependant("D", false, new Requirement[]{
            new MockRequirement("T")
        }));
        list.add(new MockDependant("P", false, new Requirement[]{
            new MockRequirement("F"), new MockRequirement("T"),
            new MockRequirement("D")
        }));
        Dependencies deps = new Dependencies(list);
        Dependency[] ds = deps.getDependencies();
        int idx = 0;
        assertEquals("F", ds[idx++].getId());
        assertEquals("T", ds[idx++].getId());
        assertEquals("D", ds[idx++].getId());
        assertEquals("P", ds[idx++].getId());
    }
}
