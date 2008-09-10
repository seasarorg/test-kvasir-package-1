package org.seasar.kvasir.util;

public class Window
{
    private String string_;

    private int idx_;

    private char[] chars_;


    public Window(String string, int width)
    {
        string_ = string;
        chars_ = new char[width];
        shift(width);
    }


    public String toString()
    {
        return new String(chars_);
    }


    public char charAt(int idx)
    {
        return chars_[idx];
    }


    public Window shift()
    {
        for (int i = 0; i < chars_.length - 1; i++) {
            chars_[i] = chars_[i + 1];
        }
        chars_[chars_.length - 1] = (idx_ < string_.length() ? string_
            .charAt(idx_) : '\0');
        idx_++;
        return this;
    }


    public Window shift(int count)
    {
        for (int i = 0; i < count; i++) {
            shift();
        }
        return this;
    }


    public boolean canShift()
    {
        return (chars_[0] != '\0');
    }
}
