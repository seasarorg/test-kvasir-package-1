package org.seasar.kvasir.util.html;


/**
 * HTMLをトークンに分割するためのトークナイザクラスです。
 * <p>Kvasir/Soraの疑似タグである
 * &lt;uri&gt;タグはHTMLタグとは見なさないようになっています。</p>
 */
public class HTMLTokenizer
{
    /** ボディを表します。 */
    public static final int     BODY = 0;

    /** タグを表します。 */
    public static final int     TAG = 1;
    
    /** コメントを表します。 */
    public static final int     COMMENT = 2;

    /** 疑似タグを表わします。 */
    public static final int     PSEUDO_TAG = 3;


    /** HTML文字列です。 */
    private String      str_;

    /** 処理を行なっているHTML文字列上の位置です。 */
    private int         ptr_;


    /*
     * constructors
     */

    /**
     * このクラスのオブジェクトを生成します。
     *
     * @param str HTML文字列。
     */
    public HTMLTokenizer(String str)
    {
        str_ = str;
        ptr_ = 0;
    }


    /*
     * public scope methods
     */

    /**
     * トークンがまだあるかどうかを返します。
     *
     * @return * トークンがまだあるかどうか。
     */
    public boolean hasMoreTokens()
    {
        return (ptr_ < str_.length());
    }


    /**
     * 次のトークンを返します。
     *
     * @return 次のトークン。
     */
    public Token nextToken()
    {
        int stat = 0;
        int pos = ptr_;
        while (ptr_ < str_.length()) {
            char ch = str_.charAt(ptr_);
            if (stat == 0) {
                if (ch == '<') {
                    if (pos < ptr_) {
                        return new Token(BODY, str_.substring(pos, ptr_));
                    } else {
                        stat = 1;
                    }
                }
            } else if (stat == 1) {
                if (ch == '!' && ptr_ + 2 < str_.length()
                && str_.charAt(ptr_ + 1) == '-'
                && str_.charAt(ptr_ + 2) == '-') {
                    stat = 2;
                } else if (ch == 'u' && ptr_ + 3 < str_.length()
                && str_.charAt(ptr_ + 1) == 'r'
                && str_.charAt(ptr_ + 2) == 'i'
                && str_.charAt(ptr_ + 3) == ' ') {
                    ptr_ += 3;
                    stat = 4;
                } else {
                    stat = 3;
                }
            } else if (stat == 2) {
                // コメント。
                if (ch == '-' && ptr_ + 2 < str_.length()
                && str_.charAt(ptr_ + 1) == '-'
                && str_.charAt(ptr_ + 2) == '>') {
                    ptr_ += 3;
                    return new Token(COMMENT, str_.substring(pos, ptr_));
                }
            } else if (stat == 3) {
                // タグ。
                if (ch == '<') {
                    // タグ中の疑似タグはタグに埋め込んだ状態にしておく。
                    int gt = str_.indexOf('>', ptr_ + 1);
                    if (gt >= 0) {
                        ptr_ = gt;
                    }
                } else if (ch == '>') {
                    ptr_++;
                    return new Token(TAG, str_.substring(pos, ptr_));
                }
            } else if (stat == 4) {
                // <uri>疑似タグ。
                if (ch == '>') {
                    ptr_++;
                    return new Token(PSEUDO_TAG, str_.substring(pos, ptr_));
                }
            } else {
                throw new RuntimeException("Unknown status: " + stat);
            }
            ptr_++;
        }

        return new Token(BODY, str_.substring(pos));
    }


    /*
     * inner classes
     */

    public static class Token
    {
        private String  str_;
        private int     type_;
        private String  tagName_;

        public Token(int type, String str)
        {
            type_ = type;
            str_ = str;
            if (type_ == TAG) {
                int idx = str_.indexOf(" ", 1);
                if (idx < 0) {
                    idx = str_.indexOf("/>", 1);
                    if (idx < 0) {
                        idx = str_.indexOf(">", 1);
                    }
                }
                if (idx >= 1) {
                    tagName_ = str_.substring(1, idx);
                }
            }
        }


        public String toString()
        {
            return str_;
        }


        public int getType()
        {
            return type_;
        }


        public String getTagName()
        {
            return tagName_;
        }
    }
}
