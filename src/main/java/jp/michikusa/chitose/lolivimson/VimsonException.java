package jp.michikusa.chitose.lolivimson;

@SuppressWarnings("serial")
public class VimsonException
    extends RuntimeException
{
    public VimsonException()
    {
    }

    public VimsonException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public VimsonException(String message)
    {
        super(message);
    }

    public VimsonException(Throwable cause)
    {
        super(cause);
    }
}
