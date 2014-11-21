package jp.michikusa.chitose.lolivimson;

@SuppressWarnings("serial")
public class TypeMismatchException
    extends VimsonException
{
    public TypeMismatchException(Class<?> expected, Class<?> actual)
    {
        super(String.format("Expected `%s' value, but got a `%s' value.", getCanonicalName(expected), getCanonicalName(actual)));
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
