package org.seasar.kvasir.util.pathlock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import junit.framework.TestCase;


public class PathLockTest extends TestCase
{
    public void testNewInstance()
    {
        PathLock lock1 = PathLock.newInstance();
        PathLock lock2 = PathLock.newInstance();
        assertNotNull(lock1);
        assertNotNull(lock2);
        assertNotSame(lock1, lock2);
    }


    public void testSharedLock()
    {
        PathLock lock = PathLock.newInstance();
        Thread anotherThread = new Thread();

        assertTrue(lock(lock, "/a/b/cde", false, false));
        assertTrue((lock(lock, "/a/b/cde", false, false)));
        assertTrue(lock(lock, "/a/b/cde", false, true));
        assertTrue((lock(lock, "/a/b/cde", false, true)));
        assertTrue((lock(lock, "/a/b/cde/fg", false, false)));
        assertTrue((lock(lock, "/a/b", false, false)));
        assertEquals(4, getLockCount(lock, "/a/b/cde"));
        unlock(lock, new String[]{ "/a/b/cde" });
        assertEquals(3, getLockCount(lock, "/a/b/cde"));
        unlock(lock, new String[]{ "/a/b/cde" });
        assertEquals(2, getLockCount(lock, "/a/b/cde"));
        unlock(lock, new String[]{ "/a/b/cde" });
        assertEquals(1, getLockCount(lock, "/a/b/cde"));
        unlock(lock, new String[]{ "/a/b/cde" });
        assertEquals(0, getLockCount(lock, "/a/b/cde"));

        lock = PathLock.newInstance();
        lockAsAnotherThread(lock, anotherThread, "/a/b/cde", false, false);
        assertTrue(lock(lock, "/a/b/c", false, false));
        assertTrue(lock(lock, "/a/b/cd", false, false));
        assertTrue((lock(lock, "/a/b/cde", false, false)));
        assertTrue((lock(lock, "/a/b/cde/fg", false, false)));
        assertTrue((lock(lock, "/a/b", false, false)));
        assertTrue((lock(lock, "/a", false, false)));
        assertTrue((lock(lock, "", false, false)));
        unlock(lock, new String[]{
            "/a/b/c", "/a/b/cd", "/a/b/cde", "/a/b/cde/fg", "/a/b", "/a", "" });
        unlockAsAnotherThread(lock, anotherThread,
            new String[]{ "/a/b/cde" });
        assertTrue(lock(lock, "", false, false));

        lock = PathLock.newInstance();
        lockAsAnotherThread(lock, anotherThread, "/a/b/cde", false, true);
        assertTrue(lock(lock, "/a/b/c", false, false));
        assertTrue(lock(lock, "/a/b/cd", false, false));
        assertTrue((lock(lock, "/a/b/cde", false, false)));
        assertTrue((lock(lock, "/a/b/cde/fg", false, false)));
        assertTrue((lock(lock, "/a/b", false, false)));
        assertTrue((lock(lock, "/a", false, false)));
        assertTrue((lock(lock, "", false, false)));
        unlock(lock, new String[]{
            "/a/b/c", "/a/b/cd", "/a/b/cde", "/a/b/cde/fg", "/a/b", "/a", "" });
        unlockAsAnotherThread(lock, anotherThread,
            new String[]{ "/a/b/cde" });
        assertTrue(lock(lock, "", false, false));

        lock = PathLock.newInstance();
        lockAsAnotherThread(lock, anotherThread, "/a/b/cde", true, false);
        assertTrue(lock(lock, "/a/b/c", false, false));
        assertTrue(lock(lock, "/a/b/cd", false, false));
        assertFalse((lock(lock, "/a/b/cde", false, false)));
        assertTrue((lock(lock, "/a/b/cde/fg", false, false)));
        assertTrue((lock(lock, "/a/b", false, false)));
        assertTrue((lock(lock, "/a", false, false)));
        assertTrue((lock(lock, "", false, false)));

        lock = PathLock.newInstance();
        lockAsAnotherThread(lock, anotherThread, "/a/b/cde", true, true);
        assertTrue(lock(lock, "/a/b/c", false, false));
        assertTrue(lock(lock, "/a/b/cd", false, false));
        assertFalse((lock(lock, "/a/b/cde", false, false)));
        assertFalse((lock(lock, "/a/b/cde/fg", false, false)));
        assertTrue((lock(lock, "/a/b", false, false)));
        assertTrue((lock(lock, "/a", false, false)));
        assertTrue((lock(lock, "", false, false)));

        // withDescendants

        lock = PathLock.newInstance();
        assertTrue(lock(lock, "/a/b/cde", false, true));
        assertTrue((lock(lock, "/a/b/cde", false, true)));
        assertTrue((lock(lock, "/a/b/cde", false, false)));
        assertTrue((lock(lock, "/a/b/cde", false, false)));
        assertTrue((lock(lock, "/a/b/cde/fg", false, true)));
        assertTrue((lock(lock, "/a/b", false, true)));
        assertEquals(4, getLockCount(lock, "/a/b/cde"));
        unlock(lock, new String[]{ "/a/b/cde" });
        assertEquals(3, getLockCount(lock, "/a/b/cde"));
        unlock(lock, new String[]{ "/a/b/cde" });
        assertEquals(2, getLockCount(lock, "/a/b/cde"));
        unlock(lock, new String[]{ "/a/b/cde" });
        assertEquals(1, getLockCount(lock, "/a/b/cde"));
        unlock(lock, new String[]{ "/a/b/cde" });
        assertEquals(0, getLockCount(lock, "/a/b/cde"));

        lock = PathLock.newInstance();
        lockAsAnotherThread(lock, anotherThread, "/a/b/cde", false, true);
        assertTrue(lock(lock, "/a/b/c", false, true));
        assertTrue(lock(lock, "/a/b/cd", false, true));
        assertTrue((lock(lock, "/a/b/cde", false, true)));
        assertTrue((lock(lock, "/a/b/cde/fg", false, true)));
        assertTrue((lock(lock, "/a/b", false, true)));
        assertTrue((lock(lock, "/a", false, true)));
        assertTrue((lock(lock, "", false, true)));
        unlock(lock, new String[]{
            "/a/b/c", "/a/b/cd", "/a/b/cde", "/a/b/cde/fg", "/a/b", "/a", "" });
        unlockAsAnotherThread(lock, anotherThread,
            new String[]{ "/a/b/cde" });
        assertTrue(lock(lock, "", false, true));

        lock = PathLock.newInstance();
        lockAsAnotherThread(lock, anotherThread, "/a/b/cde", true, true);
        assertTrue(lock(lock, "/a/b/c", false, true));
        assertTrue(lock(lock, "/a/b/cd", false, true));
        assertFalse((lock(lock, "/a/b/cde", false, true)));
        assertFalse((lock(lock, "/a/b/cde/fg", false, true)));
        assertFalse((lock(lock, "/a/b", false, true)));
        assertFalse((lock(lock, "/a", false, true)));
        assertFalse((lock(lock, "", false, true)));
    }


    public void testExclusiveLock()
    {
        PathLock lock = PathLock.newInstance();
        Thread anotherThread = new Thread();

        assertTrue(lock(lock, "/a/b/cde", true, true));
        assertTrue((lock(lock, "/a/b/cde", true, true)));
        assertTrue((lock(lock, "/a/b/cde/fg", true, true)));
        assertTrue((lock(lock, "/a/b", true, true)));
        assertEquals(2, getLockCount(lock, "/a/b/cde"));
        unlock(lock, new String[]{ "/a/b/cde" });
        assertEquals(1, getLockCount(lock, "/a/b/cde"));
        unlock(lock, new String[]{ "/a/b/cde" });
        assertEquals(0, getLockCount(lock, "/a/b/cde"));

        lock = PathLock.newInstance();
        lockAsAnotherThread(lock, anotherThread, "/a/b/cde", false, false);
        assertTrue(lock(lock, "/a/b/c", true, true));
        assertTrue(lock(lock, "/a/b/cd", true, true));
        assertFalse((lock(lock, "/a/b/cde", true, true)));
        assertTrue((lock(lock, "/a/b/cde/fg", true, true)));
        assertFalse((lock(lock, "/a/b", true, true)));
        assertFalse((lock(lock, "/a", true, true)));
        assertFalse((lock(lock, "", true, true)));
        unlock(lock, new String[]{ "/a/b/c", "/a/b/cd", "/a/b/cde/fg" });
        unlockAsAnotherThread(lock, anotherThread,
            new String[]{ "/a/b/cde" });
        assertTrue(lock(lock, "", true, true));

        lock = PathLock.newInstance();
        lockAsAnotherThread(lock, anotherThread, "/a/b/cde", false, true);
        assertTrue(lock(lock, "/a/b/c", true, true));
        assertTrue(lock(lock, "/a/b/cd", true, true));
        assertFalse((lock(lock, "/a/b/cde", true, true)));
        assertFalse((lock(lock, "/a/b/cde/fg", true, true)));
        assertFalse((lock(lock, "/a/b", true, true)));
        assertFalse((lock(lock, "/a", true, true)));
        assertFalse((lock(lock, "", true, true)));
        unlock(lock, new String[]{ "/a/b/c", "/a/b/cd" });
        unlockAsAnotherThread(lock, anotherThread,
            new String[]{ "/a/b/cde" });
        assertTrue(lock(lock, "", true, true));

        lock = PathLock.newInstance();
        lockAsAnotherThread(lock, anotherThread, "/a/b/cde", true, false);
        assertTrue(lock(lock, "/a/b/c", true, true));
        assertTrue(lock(lock, "/a/b/cd", true, true));
        assertFalse((lock(lock, "/a/b/cde", true, true)));
        assertTrue((lock(lock, "/a/b/cde/fg", true, true)));
        assertFalse((lock(lock, "/a/b", true, true)));
        assertFalse((lock(lock, "/a", true, true)));
        assertFalse((lock(lock, "", true, true)));

        lock = PathLock.newInstance();
        lockAsAnotherThread(lock, anotherThread, "/a/b/cde", true, true);
        assertTrue(lock(lock, "/a/b/c", true, true));
        assertTrue(lock(lock, "/a/b/cd", true, true));
        assertFalse((lock(lock, "/a/b/cde", true, true)));
        assertFalse((lock(lock, "/a/b/cde/fg", true, true)));
        assertFalse((lock(lock, "/a/b", true, true)));
        assertFalse((lock(lock, "/a", true, true)));
        assertFalse((lock(lock, "", true, true)));
    }


    public void testProcessWithExclusiveLock()
    {
        PathLock lock = PathLock.newInstance();
        assertEquals("OK!", lock.processWithExclusiveLock(
            "/a/b/c", true, new ProcessWithLock() {
                public Object process()
                {
                    return "OK!";
                }
            }));
    }


    /*
     * private scope methods
     */

    private boolean lock(PathLock lock, String path, boolean exclusive,
        boolean withDescendants)
    {
        Method method;
        try {
            method = lock.getClass().getDeclaredMethod(
                "lockForTest",
                new Class[]{ String.class, boolean.class, boolean.class });
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
        method.setAccessible(true);
        try {
            return ((Boolean)method.invoke(lock, new Object[]{
                path, Boolean.valueOf(exclusive),
                Boolean.valueOf(withDescendants) })).booleanValue();
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }


    private void lockAsAnotherThread(PathLock lock, Thread thread,
        String path, boolean exclusive, boolean withDescendants)
    {
        Method method;
        try {
            method = lock.getClass().getDeclaredMethod("lockForTest",
                new Class[]{ Thread.class, String.class,
                boolean.class, boolean.class });
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
        method.setAccessible(true);
        try {
            method.invoke(lock, new Object[]{ thread, path,
                Boolean.valueOf(exclusive),
                Boolean.valueOf(withDescendants) });
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }


    private void unlock(PathLock lock, String[] paths)
    {
        Method method;
        try {
            method = lock.getClass().getDeclaredMethod(
                "unlock", new Class[]{ String[].class });
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
        method.setAccessible(true);
        try {
            method.invoke(lock, new Object[]{ paths });
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }


    private void unlockAsAnotherThread(PathLock lock, Thread thread,
        String[] paths)
    {
        Method method;
        try {
            method = lock.getClass().getDeclaredMethod(
                "unlockForTest", new Class[]{ Thread.class, String[].class });
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
        method.setAccessible(true);
        try {
            method.invoke(lock, new Object[]{ thread, paths });
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }


    private int getLockCount(PathLock lock, String path)
    {
        Method method;
        try {
            method = lock.getClass().getDeclaredMethod(
                "getLockCountForTest", new Class[]{ String.class });
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
        method.setAccessible(true);
        try {
            return ((Integer)method.invoke(
                lock, new Object[]{ path })).intValue();
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

}
