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
package jp.michikusa.chitose.vimson;

import static java.lang.Boolean.TRUE;

import java.util.Collections;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import jp.michikusa.chitose.vimson.util.Function;

/**
 * java object to vimson (Vim's Dict) encoder/decoder.
 * this provides functionally methods by static way.
 *
 * @author kamichidu
 * @since 2013-12-21
 */
public class Vimson
{
    /**
     * encode java.util.Map to vimson.
     *
     * @since 2013-12-21
     * @param map an Map
     * @throws NullPointerException when map is null
     */
    public static CharSequence encode(Map<? extends CharSequence, ? extends Object> map)
    {
        checkNotNull(map);

        final StringBuilder builder= new StringBuilder();

        builder.append('{');
        for(Map.Entry<? extends CharSequence, ? extends Object> entry : map.entrySet())
        {
            builder
                .append("'")
                .append(entry.getKey())
                .append("':")
                .append(encode(entry.getValue()))
                .append(',')
            ;
        }
        builder.append('}');

        return builder;
    }

    public static Map<String, Object> decode(CharSequence vimson)
    {
        // TODO: implements
        throw new UnsupportedOperationException("sorry, umimplemented yet.");
    }

    private static CharSequence encode(Object o)
    {
        if(o == null)
        {
            return Symbol.NULL;
        }

        final Class<?> arg_clazz= o.getClass();
        for(Class<?> key_clazz : encoders.keySet())
        {
            if(key_clazz.isAssignableFrom(arg_clazz))
            {
                return encoders.get(key_clazz).apply(o);
            }
        }

        throw new IllegalArgumentException();
    }

    private static void checkArguments(boolean expr)
    {
        if(!expr)
        {
            throw new IllegalArgumentException();
        }
    }

    private static <T> T checkNotNull(T ref)
    {
        if(ref == null)
        {
            throw new NullPointerException();
        }

        return ref;
    }

    private static void checkState(boolean expr)
    {
        if(!expr)
        {
            throw new IllegalStateException();
        }
    }

    // private block
    private Vimson(){}

    private static interface Symbol
    {
        final CharSequence NULL= "0";
        final CharSequence TRUE= "1";
        final CharSequence FALSE= "0";
    }

    private static final Map<Class<?>, Function<Object, CharSequence>> encoders;
    static
    {
        final Map<Class<?>, Function<Object, CharSequence>> m= new LinkedHashMap<>();

        m.put(Map.class, new Function<Object, CharSequence>(){
            @Override
            public CharSequence apply(Object o)
            {
                checkArguments(o instanceof Map);

                @SuppressWarnings("unchecked")
                final Map<CharSequence, Object> map= (Map<CharSequence, Object>)o;

                return encode(map);
            }
        });
        m.put(Collection.class, new Function<Object, CharSequence>(){
            @Override
            public CharSequence apply(Object o)
            {
                checkArguments(o instanceof Collection);

                final Collection<?> col= (Collection<?>)o;
                final StringBuilder builder= new StringBuilder();

                builder.append('[');
                for(Object elm : col)
                {
                    builder.append(encode(elm)).append(',');
                }
                builder.append(']');

                return builder;
            }
        });
        m.put(Number.class, new Function<Object, CharSequence>(){
            @Override
            public CharSequence apply(Object o)
            {
                // 32bit integer is max size of vim's number
                return o.toString();
            }
        });
        {
            final Function<Object, CharSequence> encoder= new Function<Object, CharSequence>(){
                @Override
                public CharSequence apply(Object o)
                {
                    return "'" + o + "'";
                }
            };
            m.put(CharSequence.class, encoder);
            m.put(Character.class, encoder);
        }
        m.put(Boolean.class, new Function<Object, CharSequence>(){
            @Override
            public CharSequence apply(Object o)
            {
                checkArguments(o instanceof Boolean);

                if(TRUE.equals(o))
                {
                    return Symbol.TRUE;
                }
                else
                {
                    return Symbol.FALSE;
                }
            }
        });
        m.put(Object.class, new Function<Object, CharSequence>(){
            @Override
            public CharSequence apply(Object o)
            {
                throw new IllegalArgumentException("can't encode java.lang.Object");
            }
        });

        encoders= Collections.unmodifiableMap(m);
    }
}

