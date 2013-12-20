package jp.michikusa.chitose.vimson;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.Collections;
import java.util.Collection;

/**
 * Unit test for simple App.
 */
public class VimsonTest
{
    @Test
    public void encodeEmpty()
    {
        assertEquals("{}", Vimson.encode(Collections.<String, Object>emptyMap()).toString());
    }

    @Test
    public void encodeNumber()
    {
        final Map<String, Number> m= new LinkedHashMap<>();

        m.put("kub", (byte)9);
        m.put("kus", (short)9);
        m.put("kui", 9);
        m.put("kukul", 99L);
        m.put("kukukud", 99.9);
        m.put("kukukuf", 99.9f);

        assertEquals("{'kub':9,'kus':9,'kui':9,'kukul':99,'kukukud':99.9,'kukukuf':99.9,}", Vimson.encode(m).toString());
    }

    @Test
    public void encodeString()
    {
        final Map<String, Object> m= new LinkedHashMap<>();

        m.put("a", 'A');
        m.put("b", "B");

        assertEquals("{'a':'A','b':'B',}", Vimson.encode(m).toString());
    }


    @Test
    public void encodeBoolean()
    {
        final Map<String, Object> m= new LinkedHashMap<>();

        m.put("a", true);
        m.put("b", false);

        assertEquals("{'a':1,'b':0,}", Vimson.encode(m).toString());
    }

    @Test
    public void encodeNestedCollection()
    {
        final Map<String, Object> m= new LinkedHashMap<>();

        m.put("a", Collections.emptyList());
        {
            final Set<Collection<?>> s= new LinkedHashSet<>();

            s.add(Collections.emptyList());
            s.add(Collections.emptySet());

            m.put("b", s);
        }

        assertEquals("{'a':[],'b':[[],[],],}", Vimson.encode(m).toString());
    }

    @Test
    public void encodeNestedMap()
    {
        final Map<String, Object> m= new LinkedHashMap<>();

        m.put("a", Collections.emptyMap());
        {
            final Map<String, Object> in= new LinkedHashMap<>();

            in.put("A", Collections.emptyMap());
            in.put("B", "hoge");

            m.put("b", in);
        }

        assertEquals("{'a':{},'b':{'A':{},'B':'hoge',},}", Vimson.encode(m).toString());
    }

    @Test(expected= NullPointerException.class)
    public void encodePassNull()
    {
        Vimson.encode(null);
    }

    @Test
    public void decode()
    {
        // TODO: implements
        // Vimson.decode("");
    }
}

