package jp.michikusa.chitose.lolivimson.util;

public class Preconditions
{
    public static <T> T checkNotNull(T ref)
    {
        if(ref == null)
        {
            throw new NullPointerException();
        }
        return ref;
    }

    public static void checkState(boolean cond)
    {
        if(!cond)
        {
            throw new IllegalStateException();
        }
    }

    private Preconditions()
    {
        throw new AssertionError();
    }
}
