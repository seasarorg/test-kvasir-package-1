package org.seasar.kvasir.util.wiki;



public class BreakEvaluator
    implements BlockEvaluator
{
    public boolean canNest()
    {
        return false;
    }


    public boolean canBeNested()
    {
        return false;
    }


    public boolean shouldEvaluate(Context context, String line)
    {
        return (line.length() == 0);
    }


    public boolean evaluate(Context context, String line, boolean first)
    {
        return true;
    }


    public void terminate(Context context)
    {
    }


    public void beginNestedBlock(Context context, String line)
    {
    }


    public void endNestedBlock(Context context)
    {
    }
}
