package org.seasar.kvasir.util.pathlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * パスをロックする機構を提供するクラスです。
 * <p>パスをロックして何らかの処理をしたい場合にこのクラスを用います。</p>
 * パスは'/'で区切られた文字列で、
 * 空文字列（ルートパス）または先頭が'/'で始まる必要があります。</p>
 * <p>ロック中に行なう処理は
 * {@link ProcessWithLock}
 * インタフェースを実装したクラスとして表し、
 * {@link ProcessWithLock#process()}
 * メソッドの呼び出しによって実行されるようにしておきます。
 * 例えば<code>/path/to/something</code>をロックし、
 * ロック中に<code>someProcess</code>
 * で表される処理を実行したい場合は以下のようにします。</p>
 * <pre>
 * PathLock lock = PathLock.newInstance();
 * ProcessWithLock someProcess = ...;
 * ...
 * Object result = lock.processWithSharedLock("/path/to/something", false, someProcess);
 * ...
 * </pre>
 * <p>ロックの競合チェックはスレッド毎に行なわれます。
 * 逆に言うと、
 * 同一スレッドであれば競合するような複数のロックを同時に行なうことができます。
 * </p>
 * <p>ロックには共有ロックと排他ロックがあります。
 * 共有ロックは、他のスレッドによって共有ロックされていてもロックが可能です。
 * 逆に、共有ロック中に他のスレッドによって共有ロックされることがあります。
 * 排他ロックは、他のスレッドによってロックされている場合はロックできません。
 * </p>
 * <p>複数のパスを同時にロックすることもできます。
 * 複数のパスを同時にロックしたい場合は
 * {@link #processWithSharedLock(String[], boolean, ProcessWithLock)}
 * などを使用して下さい。</p>
 * <p><code>processWithXXXXLock</code>メソッドの引数
 * <code>withDescendants</code>がfalseである場合、
 * ロックしたパスだけがロックされますので、
 * あるパスがロックされている時に他のスレッドが子孫パスをロックすることができます。
 * <code>withDescendants</code>がtrueである場合、
 * ロックしたパスとその子孫パスがロックされますので、
 * あるパスがロックされている時に他のスレッドは子孫パスをロックすることができません。
 * </p>
 * <p>パスのロックによる排他制御はPathLockインスタンス毎に行なわれます。
 * 通常は唯一のPathLockインスタンスを生成しておき、
 * それを利用するようにすればよいでしょう。</p>
 * 
 * <p><b>同期化：</b>
 * このクラスはスレッドセーフです。</p>
 * 
 * @author YOKOTA Takehiko
 */
public class PathLock
{
    private SortedMap           lockMap_ = new TreeMap();
    private volatile int        exclusiveCount_ = 0;


    /**
     * 新しいPathLockオブジェクトを生成します。
     * 
     * @return 生成したPathLockオブジェクト。
     */
    public static PathLock newInstance()
    {
        return new PathLock();
    }


    private PathLock()
    {
    }


    /**
     * 指定されたパスを共有ロックし、
     * 指定された処理を実行します。
     * <p><code>withDescendants</code>がfalse
     * の場合は指定されたパスだけをロックし子孫パスをロックしません。
     * <code>withDescendants</code>がtrue
     * の場合は指定されたパスとその子孫パスを同時にロックします。</p>
     * 
     * @param pathStr ロックするパス。
     * @param withDescendants 子孫パスを同時にロックするか。
     * @param process ロック中に実行する処理を表すProcessWithLockオブジェクト。
     * @return 処理の実行結果。
     */
    public Object processWithSharedLock(String pathStr,
        boolean withDescendants, ProcessWithLock process)
    {
        return processWithSharedLock(
            new String[]{ pathStr }, withDescendants, process);
    }


    /**
     * 指定されたパスを排他ロックし、
     * 指定された処理を実行します。
     * <p>排他ロックであること以外は
     * {@link #processWithSharedLock(String, boolean, ProcessWithLock)}
     * と同じです。</p>
     * 
     * @param pathStr ロックするパス。
     * @param withDescendants 子孫パスを同時にロックするか。
     * @param process ロック中に実行する処理を表すProcessWithLockオブジェクト。
     * @return 処理の実行結果。
     * @see #processWithSharedLock(String, ProcessWithLock)
     */
    public Object processWithExclusiveLock(String pathStr,
        boolean withDescendants, ProcessWithLock process)
    {
        return processWithExclusiveLock(
            new String[]{ pathStr }, withDescendants, process);
    }


    /**
     * 指定された複数のパスを共有ロックし、
     * 指定された処理を実行します。
     * <p>パスは<code>pathStrs</code>の中での順番によらず、
     * アルファベット順にロックされます。</p>
     * 
     * @param pathStrs ロックするパス。
     * nullを指定してはいけません。
     * @param withDescendants 子孫パスを同時にロックするか。
     * @param process ロック中に実行する処理を表すProcessWithLockオブジェクト。
     * @return 処理の実行結果。
     */
    public Object processWithSharedLock(String[] pathStrs,
        boolean withDescendants, ProcessWithLock process)
    {
        Lock lock = sharedLock(pathStrs, withDescendants);
        try {
            return process.process();
        } finally {
            lock.unlock();
        }
    }


    /**
     * 指定された複数のパスを排他ロックし、
     * 指定された処理を実行します。
     * <p>排他ロックであること以外は
     * {@link #processWithSharedLock(String[], boolean, ProcessWithLock)}
     * と同じです。
     * </p>
     * 
     * @param pathStrs ロックするパス。
     * nullを指定してはいけません。
     * @param withDescendants 子孫パスを同時にロックするか。
     * @param process ロック中に実行する処理を表すProcessWithLockオブジェクト。
     * @return 処理の実行結果。
     * @see #processWithSharedLock(String[], ProcessWithLock)
     */
    public Object processWithExclusiveLock(String[] pathStrs,
        boolean withDescendants, ProcessWithLock process)
    {
        Lock lock = exclusiveLock(pathStrs, withDescendants);
        try {
            return process.process();
        } finally {
            lock.unlock();
        }
    }


    /**
     * 指定されたパスを共有ロックします。
     * <p>ロックしたパスは必ず{@link Lock#unlock()}
     * メソッドでアンロックして下さい。
     * 通常は以下のイディオムを使って下さい：</p>
     * <pre>
     * String path = "/some/path";
     * Lock lock = lock(path, false);
     * try {
     *     ロック中に行ないたい処理;
     * } finally {
     *     lock.unlock();
     * }
     * </pre>
     * 
     * @param pathStr ロックするパス。
     * @param withDescendants 子孫パスを同時にロックするか。
     * @return ロックを表すオブジェクト。ロックを解除する際に使用します。
     */
    public Lock sharedLock(String pathStr, boolean withDescendants)
    {
        return sharedLock(new String[]{ pathStr }, withDescendants);
    }


    /**
     * 指定されたパスを排他ロックします。
     * <p>排他ロックであること以外は
     * {@link #sharedLock(String)}と同じです。</p>
     * 
     * @param pathStr ロックするパス。
     * @param withDescendants 子孫パスを同時にロックするか。
     * @return ロックを表すオブジェクト。ロックを解除する際に使用します。
     * @see #sharedLock(String)
     */
    public Lock exclusiveLock(String pathStr, boolean withDescendants)
    {
        return exclusiveLock(new String[]{ pathStr }, withDescendants);
    }


    /**
     * 指定された複数のパスを共有ロックします。
     * <p>パスは<code>pathStrs</code>の中での順番によらず、
     * アルファベット順にロックされます。</p>
     * <p>ロックしたパスは必ず{@link Lock#unlock()}
     * メソッドでアンロックして下さい。
     * 通常は以下のイディオムを使って下さい：</p>
     * <pre>
     * String[] paths = new String[]{ "/some/path" };
     * Lock lock = lock(paths, false);
     * try {
     *     ロック中に行ないたい処理;
     * } finally {
     *     lock.unlock();
     * }
     * </pre>
     * 
     * @param pathStrs ロックするパス。
     * nullを指定してはいけません。
     * @param withDescendants 子孫パスを同時にロックするか。
     * @return ロックを表すオブジェクト。ロックを解除する際に使用します。
     */
    public Lock sharedLock(String[] pathStrs, boolean withDescendants)
    {
        Path[] paths = new Path[pathStrs.length];
        for (int i = 0; i < pathStrs.length; i++) {
            paths[i] = new Path(pathStrs[i]);
        }
        Arrays.sort(paths);

        return lock(paths, false, withDescendants);
    }


    /**
     * 指定された複数のパスを排他ロックします。
     * <p>排他ロックであること以外は
     * {@link #sharedLock(String[])}と同じです。</p>
     * 
     * @param pathStrs ロックするパス。
     * nullを指定してはいけません。
     * @param withDescendants 子孫パスを同時にロックするか。
     * @return ロックを表すオブジェクト。ロックを解除する際に使用します。
     * @see #sharedLock(String[])
     */
    public Lock exclusiveLock(String[] pathStrs, boolean withDescendants)
    {
        Path[] paths = new Path[pathStrs.length];
        for (int i = 0; i < pathStrs.length; i++) {
            paths[i] = new Path(pathStrs[i]);
        }
        Arrays.sort(paths);

        return lock(paths, true, withDescendants);
    }


    /*
     * package scope methods
     */

    /**
     * 指定された複数のパスをアンロックします。
     * <p>ロックしていないパスを指定した場合、
     * そのパスは無視されます。</p>
     * <p>パスは配列の添え字の大きい順にアンロックされます。
     * <p>このメソッドは通常{@link #sharedLock(String[])}
     * 等のロックメソッドと対で使用されます。</p>
     * 
     * @param pathStrs アンロックするパス。
     * nullを指定してはいけません。
     */
    void unlock(String[] pathStrs)
    {
        synchronized (lockMap_) {
            for (int i = pathStrs.length - 1; i >= 0; i--) {
                Object pathStr = pathStrs[i];
                State state = (State)lockMap_.get(pathStr);
                if (state == null) {
                    continue;
                }
                Pair pair = state.getPair();
                if (pair == null) {
                    // 自分がロックしたパスではないので無視する。
                    continue;
                }
                if (pair.decrement()) {
                    state.removePair();
                    if (state.size() == 0) {
                        lockMap_.remove(pathStr);
                    }
                }
            }
        }
    }


    /*
     * private scope methods
     */

    private Lock lock(Path[] paths, boolean exclusive, boolean withDescendants)
    {
        List list = new ArrayList(paths.length);
        boolean succeed = false;
        try {
            for (int i = 0; i < paths.length; i++) {
                while (!lock(paths[i], exclusive, withDescendants)) {
                    // XXX これでいいのか？Thread.sleep()にすべきか？
                    Thread.yield();
                }
                list.add(paths[i].getPath());
            }
            succeed = true;
            return new Lock(this, (String[])list.toArray(new String[0]));
        } finally {
            if (!succeed) {
                // 全てをロックしないうちに例外が発生した。
                unlock((String[])list.toArray(new String[0]));
            }
        }
    }


    private boolean lock(Path path, boolean exclusive, boolean withDescendants)
    {
        synchronized (lockMap_) {
            String pathStr = path.getPath();
            State state = (State)lockMap_.get(pathStr);
            if (checkIfLocked(state, exclusive)) {
                return false;
            }
            Pair pair = (state != null) ? state.getPair() : null;

            Boolean canLock = null;

            if (!exclusive && exclusiveCount_ == 0) {
                // SHAREDを取りに来た場合は、EXCLUSIVEを持っているスレッドが
                // いなければ必ず取れる。
                canLock = Boolean.TRUE;
            }

            if (canLock == null) {
                if (pair != null) {
                    // 既に自分がロックしている。
                    if (pair.isWithDescendants() || !withDescendants) {
                        if (!pair.isExclusive() && !exclusive
                        || pair.isExclusive()) {
                            // 自分がSHAREDを持っていて再度SHAREDを取りに来た
                            // 場合と自分がEXCLUSIVEを持っている場合は子孫パス
                            // や祖先パスを調べなくともロックできるのは
                            // 明らか。
                            canLock = Boolean.TRUE;
                        }
                    }
                }
            }

            if (canLock == null) {
                if (withDescendants) {
                    // pathの子孫パスがロックされている場合はロックできない。
                    Map descendants = path.getDescendants();
                    for (Iterator itr = descendants.values().iterator();
                    itr.hasNext(); ) {
                        if (checkIfLocked((State)itr.next(), exclusive)) {
                            canLock = Boolean.FALSE;
                        }
                    }
                }
            }

            if (canLock == null) {
                // pathの先祖パスをwithDescendantsでロックされている場合は
                // ロックできない。

                String [] ascendants = path.getAncestors();
                for (int i = 0; i < ascendants.length; i++) {
                    State st = (State)lockMap_.get(ascendants[i]);
                    if (checkIfLocked(st, exclusive)
                    && st.isWithDescendants()) {
                        canLock = Boolean.FALSE;
                    }
                }
            }

            if (Boolean.FALSE.equals(canLock)) {
                return false;
            }

            if (state == null) {
                state = new State();
                lockMap_.put(pathStr, state);
            }
            if (pair == null) {
                pair = new Pair(exclusive, withDescendants);
                state.setPair(pair);
            }
            pair.increment();
            if (!pair.isExclusive() && exclusive) {
                pair.setExclusive(true);
            }
            if (!pair.isWithDescendants() && withDescendants) {
                pair.setWithDescendants(true);
            }
            return true;
        }
    }


    /*
     * ロックされているかどうかを返します。
     * 指定されたパスに関してだけ判定します。
     * 要はロックが絶対取れない場合はtrue、ロックが取れるか分からない場合は
     * falseを返すようになっています。
     */
    private boolean checkIfLocked(State state, boolean exclusive)
    {
        if (state == null) {
            // 誰もロックを持っていない。
            return false;
        }

        Pair pair = state.getPair();
        int size = state.size();
        if (pair != null) {
            // 既に自分がロックしている。
            if (!pair.isExclusive()) {
                if (!exclusive) {
                    // SHAREDロックを持っている上でSHAREDを取りに
                    // 来ている。
                    return false;
                } else {
                    if (size == 1) {
                        // SHAREDであっても自分だけが持っているので
                        // EXCLUSIVEを取れる。
                        return false;
                    } else if (size > 1) {
                        // SHAREDを自分以外が持っているのでEXCLUSIVE
                        // ロックできない。
                        return true;
                    } else {
                        throw new RuntimeException("Logic error");
                    }
                }
            } else {
                // EXCLUSIVEを持っていればどんなロックも取れる。
                return false;
            }
        } else {
            // 自分はロックを持っていないが他の誰かがロックを
            // 持っている（state != nullなので）。
            if (!state.isExclusive() && !exclusive) {
                // SHAREDロックされているならSHAREDロックは取れる。
                return false;
            } else {
                // 既にEXCLUSIVEロックを取られていたり自分が
                // EXCLUSIVEロックを取ろうとしている場合は取れない。
                return true;
            }
        }
    }


    /*
     * このメソッドは単体テスト用のメソッドです。
     */
    private boolean lockForTest(String path, boolean exclusive,
        boolean withDescendants)
    {
        return lock(new Path(path), exclusive, withDescendants);
    }


    /*
     * このメソッドは単体テスト用のメソッドです。
     */
    private void lockForTest(Thread thread, String path, boolean exclusive,
        boolean withDescendants)
    {
        State state = new State();
        Pair pair = new Pair(exclusive, withDescendants);
        pair.increment();
        state.setPair(thread, pair);
        lockMap_.put(path, state);
    }


    /*
     * このメソッドは単体テスト用のメソッドです。
     */
    private void unlockForTest(Thread thread, String[] pathStrs)
    {
        for (int i = pathStrs.length - 1; i >= 0; i--) {
            Object pathStr = pathStrs[i];
            State state = (State)lockMap_.get(pathStr);
            if (state == null) {
                continue;
            }
            Pair pair = state.getPair(thread);
            if (pair == null) {
                // 自分がロックしたパスではないので無視する。
                continue;
            }
            if (pair.decrement()) {
                state.removePair(thread);
                if (state.size() == 0) {
                    lockMap_.remove(pathStr);
                }
            }
        }
    }


    /*
     * このメソッドは単体テスト用のメソッドです。
     */
    private int getLockCountForTest(String path)
    {
        State state = (State)lockMap_.get(path);
        if (state != null) {
            Pair pair = state.getPair();
            if (pair != null) {
                return pair.getCount();
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }


    /*
     * inner classes
     */

    private class Path
        implements Comparable
    {
        private String          path_;
        private String          fromPath_;
        private String          toPath_;
        private String[]        ancestors_;

        public Path(String path)
        {
            // 子孫パスを探すための上下限を生成する。

            if (path.endsWith("/")) {
                path_ = path.substring(0, path.length() - 1);
                fromPath_ = path_;
            } else {
                path_ = path;
                fromPath_ = path + "/";
            }
            toPath_ = path_ + String.valueOf((char)('/' + 1));

            // 祖先パスを生成する。

            List list = new ArrayList();
            int pre = 0;
            int idx;
            while ((idx = path_.indexOf('/', pre)) >= 0) {
                list.add(path_.substring(0, idx));
                pre = idx + 1;
            }
            ancestors_ = (String[])list.toArray(new String[0]);
        }

        public String toString()
        {
            return path_;
        }

        public int compareTo(Object o)
        {
            Path p = (Path)o;
            return path_.compareTo(p.getPath());
        }

        public String getPath()
        {
            return path_;
        }

        public String[] getAncestors()
        {
            return ancestors_;
        }

        public SortedMap getDescendants()
        {
            return lockMap_.subMap(fromPath_, toPath_);
        }
    }


    private static class State
    {
        private Map     pairMap_ = new HashMap();
        private boolean     exclusive_;
        private boolean     withDescendants_;

        public Pair getPair()
        {
            return (Pair)pairMap_.get(Thread.currentThread());
        }

        public void setPair(Pair pair)
        {
            pairMap_.put(Thread.currentThread(), pair);
            exclusive_ = pair.isExclusive();
            withDescendants_ = pair.isWithDescendants();
        }


        public void removePair()
        {
            pairMap_.remove(Thread.currentThread());
        }

        public boolean isExclusive()
        {
            return exclusive_; 
        }

        public boolean isWithDescendants()
        {
            return withDescendants_;
        }

        public int size()
        {
            return pairMap_.size();
        }

        /*
         * このメソッドは単体テスト用のメソッドです。
         */
        private Pair getPair(Thread thread)
        {
            return (Pair)pairMap_.get(thread);
        }

        /*
         * このメソッドは単体テスト用のメソッドです。
         */
        private void setPair(Thread thread, Pair pair)
        {
            pairMap_.put(thread, pair);
            exclusive_ = pair.isExclusive();
            withDescendants_ = pair.isWithDescendants();
        }

        /*
         * このメソッドは単体テスト用のメソッドです。
         */
        private void removePair(Thread thread)
        {
            pairMap_.remove(thread);
        }
    }


    private class Pair
    {
        private boolean     exclusive_;
        private boolean     withDescendants_;
        private int         count_;

        public Pair(boolean exclusive, boolean withDescendants)
        {
            exclusive_ = exclusive;
            withDescendants_ = withDescendants;
            count_ = 0;
        }

        public boolean isExclusive()
        {
            return exclusive_;
        }

        public void setExclusive(boolean exclusive)
        {
            if (count_ > 0) {
                if (!exclusive_ && exclusive) {
                    exclusiveCount_++;
                } else if (exclusive_ && !exclusive) {
                    exclusiveCount_--;
                }
            }
            exclusive_ = exclusive;
        }

        public boolean isWithDescendants()
        {
            return withDescendants_;
        }

        public void setWithDescendants(boolean withDescendants)
        {
            withDescendants_ = withDescendants;
        }

        public int getCount()
        {
            return count_;
        }

        public void increment()
        {
            if (count_ == 0 && exclusive_) {
                exclusiveCount_++;
            }
            count_++;
        }

        public boolean decrement()
        {
            count_--;
            if (count_ == 0) {
                if (exclusive_) {
                    exclusiveCount_--;
                }
                return true;
            } else {
                return false;
            }
        }
    }
}
