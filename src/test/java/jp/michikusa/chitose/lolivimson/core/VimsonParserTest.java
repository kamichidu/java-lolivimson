package jp.michikusa.chitose.lolivimson.core;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VimsonParserTest
{
    @Test(expected= EOFException.class)
    public void emptyExpr()
        throws Exception
    {
        parse("");
    }

    @Test
    public void singleQuotedString()
        throws Exception
    {
        assertEquals("hoge", parse("'hoge'").toString());
        assertEquals("'hoge'fuga\"", parse("'''hoge''fuga\"'").toString());
        assertEquals("", parse("''").toString());
        assertEquals("\\", parse("'\\'").toString());
    }

    @Test
    public void doubleQuotedString()
        throws Exception
    {
        assertEquals("hoge", parse("\"hoge\"").toString());
        assertEquals("\"hoge\"fuga\"", parse("\"\\\"hoge\\\"fuga\\\"\"").toString());
        assertEquals("", parse("\"\"").toString());
        assertEquals("\\", parse("\"\\\\\"").toString());
        assertEquals("a", parse("\"\\" + Integer.toOctalString('a') + "\"").toString());
    }

    @Test
    public void number()
        throws Exception
    {
        assertEquals(777, parse("777"));

        assertEquals(0777, parse("0777"));
        assertEquals(0777, parse("0777"));
        assertEquals(778, parse("0778"));

        assertEquals(0xff, parse("0xff"));
        assertEquals(0xf, parse("0xf"));

        assertEquals(0.003, parse("0.003"));
        assertEquals(-0.003, parse("-0.003"));
        assertEquals(+0.003, parse("+0.003"));

        assertEquals(0.003e3, parse("0.003e3"));
        assertEquals(-0.003e+3, parse("-0.003e+3"));
        assertEquals(+0.003e-3, parse("+0.003e-3"));
    }

    @Test
    public void dictionary()
        throws Exception
    {
        {
            final Map<String, Object> expects= new HashMap<String, Object>();
            expects.put("Y", "hoge");
            expects.put("V", 30);

            assertEquals(expects, parse("{'Y':'hoge','V':30}"));
            assertEquals(expects, parse("{'Y':'hoge','V':30,}"));
        }
    }

    @Test
    public void list()
        throws Exception
    {
    }

    static Object parse(String expr)
        throws IOException
    {
        final VimsonParser parser= new VimsonParser(new ByteArrayInputStream(expr.getBytes(Charset.forName("UTF-8"))));

        final Object ret= parser.parse();
        return ret;
    }
}
