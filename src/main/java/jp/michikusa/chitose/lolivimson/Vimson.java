/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 kamichidu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jp.michikusa.chitose.lolivimson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import jp.michikusa.chitose.lolivimson.core.VimsonGenerator;
import jp.michikusa.chitose.lolivimson.core.VimsonParser;

import static jp.michikusa.chitose.lolivimson.util.Preconditions.checkNotNull;

/**
 * Wrapper utility for {@link VimsonGenerator} and {@link VimsonParser}.
 * This class provides some encode/decode method in a static way.
 *
 * <pre>
 * {@code
 * final Map<String, Object> m= new HashMap<String, Object>();
 *
 * m.put("hoge", "fuga");
 * m.put("piyo", 3.14);
 *
 * // => "{'hoge':'fuga','piyo',3.14,}"
 * System.out.println(Vimson.encode(m));
 * }
 * </pre>
 *
 * @author kamichidu
 * @since 2013-12-21
 */
public class Vimson
{
    /**
     * Encodes a {@link Map} to vimson string.
     * @param value The value will be encoded.
     * @return VIMSON string.
     */
    public static CharSequence encode(Map<? extends CharSequence, ? extends Object> value)
    {
        return encode((Object)value);
    }

    /**
     * Encodes a {@link List} to vimson string.
     * @param value The value will be encoded.
     * @return VIMSON string.
     */
    public static CharSequence encode(List<? extends Object> value)
    {
        return encode((Object)value);
    }

    /**
     * Decodes a VIMSON expr to Java's instance.
     * @param type The type you expect.
     * @param expr The VIMSON string.
     * @return An instance which exactly is a type (1-st argument).
     * @throws
     */
    public static <T> T decode(Class<T> type, CharSequence expr)
    {
        checkNotNull(type);
        checkNotNull(expr);

        try
        {
            final ByteArrayInputStream in= new ByteArrayInputStream(expr.toString().getBytes());
            final VimsonParser parser= new VimsonParser(in);

            final Object value= parser.parse();

            if(type.isAssignableFrom(value.getClass()))
            {
                return type.cast(value);
            }
            else
            {
                throw new IllegalArgumentException();
            }
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static CharSequence encode(Object value)
    {
        checkNotNull(value);

        try
        {
            final ByteArrayOutputStream out= new ByteArrayOutputStream();
            final VimsonGenerator g= new VimsonGenerator(out);

            write(g, value);

            return out.toString();
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void write(VimsonGenerator g, Object value)
        throws IOException
    {
        if(value instanceof CharSequence)
        {
            write(g, (CharSequence)value);
        }
        else if(value instanceof Number)
        {
            write(g, (Number)value);
        }
        else if(value instanceof Map)
        {
            write(g, (Map<?, ?>)value);
        }
        else if(value instanceof List)
        {
            write(g, (List<?>)value);
        }
        else if(value instanceof Boolean)
        {
            write(g, (Boolean)value);
        }
        else if(value instanceof Character)
        {
            write(g, String.valueOf((Character)value));
        }
        else
        {
            throw new UnsupportedTypeException(value != null ? value.getClass() : null);
        }
    }

    private static void write(VimsonGenerator g, Map<?, ? extends Object> value)
        throws IOException
    {
        g.writeStartDictionary();
        for(final Object key : value.keySet())
        {
            if(!(key instanceof CharSequence))
            {
                throw new TypeMismatchException(CharSequence.class, key != null ? key.getClass() : null);
            }

            g.writeFieldName((CharSequence)key);

            write(g, value.get(key));
        }
        g.writeEndDictionary();
    }

    private static void write(VimsonGenerator g, List<?> value)
        throws IOException
    {
        g.writeStartList();
        for(final Object elm : value)
        {
            write(g, elm);
        }
        g.writeEndList();
    }

    private static void write(VimsonGenerator g, CharSequence value)
        throws IOException
    {
        g.writeString(value);
    }

    private static void write(VimsonGenerator g, Number value)
        throws IOException
    {
        if(value instanceof Integer)
        {
            g.writeNumber((Integer)value);
        }
        else if(value instanceof Long)
        {
            g.writeNumber((Long)value);
        }
        else if(value instanceof Double)
        {
            g.writeFloat((Double)value);
        }
        else if(value instanceof Float)
        {
            g.writeFloat((Float)value);
        }
        else if(value instanceof Byte)
        {
            g.writeNumber((Byte)value);
        }
        else if(value instanceof Short)
        {
            g.writeNumber((Short)value);
        }
        else if(value instanceof BigDecimal)
        {
            g.writeFloat(((BigDecimal)value).doubleValue());
        }
        else if(value instanceof BigInteger)
        {
            g.writeNumber(((BigInteger)value).longValue());
        }
        else
        {
            throw new UnsupportedTypeException(value != null ? value.getClass() : null);
        }
    }

    private static void write(VimsonGenerator g, Boolean value)
        throws IOException
    {
        g.writeBoolean(value);
    }
}
