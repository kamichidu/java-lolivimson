package jp.michikusa.chitose.lolivimson;

@SuppressWarnings("serial")
public class UnsupportedTypeException
    extends VimsonException
{
    public UnsupportedTypeException(Class<?> type)
    {
        super(String.format("`%s' is not supported.", getCanonicalName(type)));
    }

    private static CharSequence getCanonicalName(Class<?> type)
    {
        if(type != null)
        {
            return type.getCanonicalName();
        }
        else
        {
            return "" + null;
        }
    }
}
