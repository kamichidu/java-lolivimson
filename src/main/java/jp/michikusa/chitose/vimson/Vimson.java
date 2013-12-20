package jp.michikusa.chitose.vimson;

import static java.lang.Boolean.TRUE;

import java.util.Collections;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import jp.michikusa.chitose.vimson.util.Function;

public class Vimson
{
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

