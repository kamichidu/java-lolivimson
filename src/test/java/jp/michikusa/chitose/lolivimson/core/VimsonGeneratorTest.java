package jp.michikusa.chitose.lolivimson.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jp.michikusa.chitose.lolivimson.core.VimsonGenerator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VimsonGeneratorTest
{
    @Test
    public void writeString()
        throws IOException
    {
        final ByteArrayOutputStream out= new ByteArrayOutputStream();
        final VimsonGenerator vson= new VimsonGenerator(out);

        vson.writeString("hello");

        assertEquals("'hello'", out.toString());
    }

    @Test
    public void writeRaw()
        throws IOException
    {
        final ByteArrayOutputStream out= new ByteArrayOutputStream();
        final VimsonGenerator vson= new VimsonGenerator(out);

        vson.writeRaw("hello");

        assertEquals("hello", out.toString());
    }

    @Test
    public void writeTrue()
        throws IOException
    {
        final ByteArrayOutputStream out= new ByteArrayOutputStream();
        final VimsonGenerator vson= new VimsonGenerator(out);

        vson.writeBoolean(true);

        assertEquals("1", out.toString());
    }

    @Test
    public void writeFalse()
        throws IOException
    {
        final ByteArrayOutputStream out= new ByteArrayOutputStream();
        final VimsonGenerator vson= new VimsonGenerator(out);

        vson.writeBoolean(false);

        assertEquals("0", out.toString());
    }

    @Test
    public void writeFloat()
        throws IOException
    {
        {
            final ByteArrayOutputStream out= new ByteArrayOutputStream();
            final VimsonGenerator vson= new VimsonGenerator(out);

            vson.writeFloat(0.0f);

            assertEquals("0.0", out.toString());
        }
        {
            final ByteArrayOutputStream out= new ByteArrayOutputStream();
            final VimsonGenerator vson= new VimsonGenerator(out);

            vson.writeFloat(0.0);

            assertEquals("0.0", out.toString());
        }
    }

    @Test
    public void writeNumber()
        throws IOException
    {
        {
            final ByteArrayOutputStream out= new ByteArrayOutputStream();
            final VimsonGenerator vson= new VimsonGenerator(out);

            vson.writeNumber((byte)0);

            assertEquals("0", out.toString());
        }
        {
            final ByteArrayOutputStream out= new ByteArrayOutputStream();
            final VimsonGenerator vson= new VimsonGenerator(out);

            vson.writeNumber((short)0);

            assertEquals("0", out.toString());
        }
        {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final VimsonGenerator vson = new VimsonGenerator(out);

            vson.writeNumber((int)0);

            assertEquals("0", out.toString());
        }
        {
            final ByteArrayOutputStream out= new ByteArrayOutputStream();
            final VimsonGenerator vson= new VimsonGenerator(out);

            vson.writeNumber((long)0);

            assertEquals("0", out.toString());
        }
    }

    @Test
    public void writeDictionary()
        throws IOException
    {
        {
            final ByteArrayOutputStream out= new ByteArrayOutputStream();
            final VimsonGenerator vson= new VimsonGenerator(out);

            vson.writeStartDictionary();
            vson.writeEndDictionary();

            assertEquals("{}", out.toString());
        }
        {
            final ByteArrayOutputStream out= new ByteArrayOutputStream();
            final VimsonGenerator vson= new VimsonGenerator(out);

            vson.writeStartDictionary();
            vson.writeBooleanField("state", true);
            vson.writeEndDictionary();

            assertEquals("{'state':1,}", out.toString());
        }
        {
            final ByteArrayOutputStream out= new ByteArrayOutputStream();
            final VimsonGenerator vson= new VimsonGenerator(out);

            vson.writeStartDictionary();
            vson.writeStringField("bar", "baz");
            vson.writeFloatField("boo", 0.0);
            vson.writeNumberField("foo", 0);
            vson.writeEndDictionary();

            assertEquals("{'bar':'baz','boo':0.0,'foo':0,}", out.toString());
        }
    }

    @Test
    public void writeNestedDictionary()
        throws IOException
    {
        final ByteArrayOutputStream out= new ByteArrayOutputStream();
        final VimsonGenerator vson= new VimsonGenerator(out);

        vson.writeStartDictionary();
        {
            vson.writeFieldName("boo");
            vson.writeBoolean(true);

            vson.writeDictionaryFieldStart("foo");
            {
                vson.writeBooleanField("pii", false);
            }
            vson.writeEndDictionary();
        }
        vson.writeEndDictionary();

        assertEquals("{'boo':1,'foo':{'pii':0,},}", out.toString());
    }

    @Test
    public void writeList()
        throws IOException
    {
        final ByteArrayOutputStream out= new ByteArrayOutputStream();
        final VimsonGenerator vson= new VimsonGenerator(out);

        vson.writeStartList();
        {
            vson.writeString("hoge");
            vson.writeBoolean(true);
            vson.writeFloat(0.0);
            vson.writeNumber(0);
        }
        vson.writeEndList();

        assertEquals("['hoge',1,0.0,0,]", out.toString());
    }

    @Test
    public void writeNestedList()
        throws IOException
    {
        final ByteArrayOutputStream out= new ByteArrayOutputStream();
        final VimsonGenerator vson= new VimsonGenerator(out);

        vson.writeStartList();
        {
            vson.writeString("hoge");
            vson.writeStartList();
            {
                vson.writeBoolean(false);
            }
            vson.writeEndList();
        }
        vson.writeEndList();

        assertEquals("['hoge',[0,],]", out.toString());
    }

    @Test
    public void writeObject()
        throws IOException
    {
        final ByteArrayOutputStream out= new ByteArrayOutputStream();
        final VimsonGenerator vson= new VimsonGenerator(out);
        final ObjectCodec codec= new ObjectCodec(){
            @Override
            public void writeValue(VimsonGenerator vgen, Object value)
                throws IOException
            {
                final String str= (String)value;

                vgen.writeStartDictionary();
                vgen.writeStringField("text", str);
                vgen.writeEndDictionary();
            }
        };

        vson.setObjectCodec(codec);

        vson.writeObject("hello");

        assertEquals("{'text':'hello',}", out.toString());
    }
}
