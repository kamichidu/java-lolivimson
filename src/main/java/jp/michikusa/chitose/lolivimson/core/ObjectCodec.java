package jp.michikusa.chitose.lolivimson.core;

import java.io.IOException;

public abstract class ObjectCodec
{
    public abstract void writeValue(VimsonGenerator vgen, Object value)
        throws IOException;
}
