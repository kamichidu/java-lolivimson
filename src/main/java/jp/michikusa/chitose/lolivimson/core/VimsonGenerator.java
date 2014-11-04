package jp.michikusa.chitose.lolivimson.core;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.Deque;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class VimsonGenerator
    implements Closeable, Flushable
{
    public VimsonGenerator(OutputStream out)
    {
        this(out, Charset.defaultCharset());
    }

    public VimsonGenerator(OutputStream out, Charset charset)
    {
        this.out= checkNotNull(out);
        this.charset= (charset != null) ? charset : Charset.defaultCharset();
    }

    public void writeString(CharSequence value)
        throws IOException
    {
        this.write("'");
        this.write(value);
        this.write("'");
        if(this.shouldWriteComma())
        {
            this.write(",");
        }
    }

    public void writeBoolean(boolean value)
        throws IOException
    {
        this.write(value ? "1" : "0");
        if(this.shouldWriteComma())
        {
            this.write(",");
        }
    }

    public void writeFloat(float value)
        throws IOException
    {
        this.write("" + value);
        if(this.shouldWriteComma())
        {
            this.write(",");
        }
    }

    public void writeFloat(double value)
        throws IOException
    {
        this.write("" + value);
        if(this.shouldWriteComma())
        {
            this.write(",");
        }
    }

    public void writeNumber(byte value)
        throws IOException
    {
        this.write("" + value);
        if(this.shouldWriteComma())
        {
            this.write(",");
        }
    }

    public void writeNumber(short value)
        throws IOException
    {
        this.write("" + value);
        if(this.shouldWriteComma())
        {
            this.write(",");
        }
    }

    public void writeNumber(int value)
        throws IOException
    {
        this.write("" + value);
        if(this.shouldWriteComma())
        {
            this.write(",");
        }
    }

    public void writeNumber(long value)
        throws IOException
    {
        this.write("" + value);
        if(this.shouldWriteComma())
        {
            this.write(",");
        }
    }

    public void writeObject(Object value)
        throws IOException
    {
        final ObjectCodec codec= this.codec;

        if(codec == null)
        {
            throw new IllegalStateException();
        }

        codec.writeValue(this, value);
    }

    public void writeObjectField(CharSequence fieldName, Object value)
        throws IOException
    {
        this.writeFieldName(fieldName);
        this.writeObject(value);
    }

    public void writeStringField(CharSequence fieldName, CharSequence value)
        throws IOException
    {
        this.writeFieldName(fieldName);
        this.writeString(value);
    }

    public void writeFloatField(CharSequence fieldName, float value)
        throws IOException
    {
        this.writeFieldName(fieldName);
        this.writeFloat(value);
    }

    public void writeFloatField(CharSequence fieldName, double value)
        throws IOException
    {
        this.writeFieldName(fieldName);
        this.writeFloat(value);
    }

    public void writeNumberField(CharSequence fieldName, byte value)
        throws IOException
    {
        this.writeFieldName(fieldName);
        this.writeNumber(value);
    }

    public void writeNumberField(CharSequence fieldName, short value)
        throws IOException
    {
        this.writeFieldName(fieldName);
        this.writeNumber(value);
    }

    public void writeNumberField(CharSequence fieldName, int value)
        throws IOException
    {
        this.writeFieldName(fieldName);
        this.writeNumber(value);
    }

    public void writeNumberField(CharSequence fieldName, long value)
        throws IOException
    {
        this.writeFieldName(fieldName);
        this.writeNumber(value);
    }

    public void writeBooleanField(CharSequence fieldName, boolean value)
        throws IOException
    {
        this.writeFieldName(fieldName);
        this.writeBoolean(value);
    }

    public void writeFieldName(CharSequence fieldName)
        throws IOException
    {
        this.write("'" + fieldName + "'");
        this.write(":");
    }

    public void writeDictionaryFieldStart(CharSequence fieldName)
        throws IOException
    {
        this.writeFieldName(fieldName);
        this.writeStartDictionary();
    }

    public void writeStartDictionary()
        throws IOException
    {
        this.write("{");
        this.context.push(Context.DICTIONARY);
    }

    public void writeEndDictionary()
        throws IOException
    {
        this.write("}");

        checkState(this.context.pop() == Context.DICTIONARY);

        if(this.shouldWriteComma())
        {
            this.write(",");
        }
    }

    public void writeListFieldStart(CharSequence fieldName)
        throws IOException
    {
        this.writeDictionaryFieldStart(fieldName);
        this.writeStartList();
    }

    public void writeStartList()
        throws IOException
    {
        this.write("[");
        this.context.push(Context.LIST);
    }

    public void writeEndList()
        throws IOException
    {
        this.write("]");

        checkState(this.context.pop() == Context.LIST);

        if(this.shouldWriteComma())
        {
            this.write(",");
        }
    }

    public void setObjectCodec(ObjectCodec codec)
    {
        this.codec= codec;
    }

    public ObjectCodec getObjectCodec()
    {
        return this.codec;
    }

    @Override
    public void flush()
        throws IOException
    {
        this.out.flush();
    }

    @Override
    public void close()
        throws IOException
    {
        this.out.close();
    }

    private static enum Context
    {
        LIST,
        DICTIONARY,
        ;
    }

    private void write(CharSequence value)
        throws IOException
    {
        this.out.write(value.toString().getBytes(this.charset));
    }

    private boolean shouldWriteComma()
    {
        if(this.context.isEmpty())
        {
            return false;
        }
        return this.context.peekLast().equals(Context.LIST) || this.context.peekLast().equals(Context.DICTIONARY);
    }

    private final OutputStream out;

    private final Charset charset;

    private final Deque<Context> context = new ArrayDeque<Context>();

    private ObjectCodec codec;
}
