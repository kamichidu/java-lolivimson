package jp.michikusa.chitose.lolivimson.core;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.lang.Character.*;

public class VimsonParser
{
    public VimsonParser(InputStream in)
        throws IOException
    {
        this.in= new InputStreamReader(in, Charset.forName("UTF-8"));
        this.offset= 0;
        this.c= (char) this.in.read();
        this.c2= (char)this.in.read();
    }

    public <T> T parse(T...type)
        throws IOException
    {
        if(this.c == EOF)
        {
            throw new EOFException();
        }

        this.skip();
        final Object value= this.value();

        if(type.getClass().getComponentType().isAssignableFrom(value.getClass()))
        {
            @SuppressWarnings("unchecked")
            final T ret= (T) value;
            return ret;
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    private static boolean isOctDigit(char c)
    {
        return c >= '0' && c <= '7';
    }

    private static boolean isHexDigit(char c)
    {
        return isDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }

    private static boolean isDigit(char c)
    {
        return c >= '0' && c <= '9';
    }

    private CharSequence key()
        throws IOException
    {
        if(this.c == '\'')
        {
            return this.singleQuotedString();
        }
        else if(this.c == '"')
        {
            return this.doubleQuotedString();
        }
        else
        {
            throw new IllegalArgumentException();
        }
    }

    private Object value()
        throws IOException
    {
        if(this.c == '{')
        {
            return this.dictionary();
        }
        else if(this.c == '[')
        {
            return this.list();
        }
        else if(this.c == '\'')
        {
            return this.singleQuotedString();
        }
        else if(this.c == '"')
        {
            return this.doubleQuotedString();
        }
        else
        {
            return this.numberOrFloat();
        }
    }

    private Map<String, Object> dictionary()
        throws IOException
    {
        this.match('{');
        this.consume();

        final Map<String, Object> map= new HashMap<String, Object>();
        while(this.c != EOF)
        {
            if(this.c == '}')
            {
                break;
            }

            this.skip();
            final String key= this.key().toString();

            this.skip();
            this.match(':');
            this.consume();

            this.skip();
            final Object value= this.value();

            map.put(key, value);

            this.skip();
            if(this.c == ',')
            {
                this.consume();
                this.skip();
            }
            if(this.c == '}')
            {
                break;
            }
        }

        this.match('}');
        this.consume();

        return map;
    }

    private List<Object> list()
        throws IOException
    {
        this.match('[');
        this.consume();

        final List<Object> list= new LinkedList<Object>();
        while(this.c != EOF)
        {
            if(this.c == ']')
            {
                break;
            }

            this.skip();
            final Object value= this.value();

            list.add(value);

            this.skip();
            if(this.c == ',')
            {
                this.consume();
                this.skip();
            }
            if(this.c == ']')
            {
                break;
            }
        }

        this.match(']');
        this.consume();

        return list;
    }

    private Number numberOrFloat()
        throws IOException
    {
        final StringBuilder number= new StringBuilder();
        int radix= 10;

        if(this.c == '+' || this.c == '-')
        {
            number.append(this.c);
            this.consume();
        }

        if(this.c == '0')
        {
            number.append('0');
            this.consume();
            if(this.c == 'x' || this.c == 'x')
            {
                radix= 16;
                this.consume();
            }
            else
            {
                radix= 8;
            }
        }

        while(isDigit(this.c) || isHexDigit(this.c) || isOctDigit(this.c))
        {
            number.append(this.c);
            if(radix == 8 && !isOctDigit(this.c))
            {
                radix= 10;
            }
            this.consume();
        }

        if(this.c != '.')
        {
            return Integer.valueOf(number.toString(), radix);
        }
        number.append('.');
        this.consume();

        // float-type
        radix= 10;
        while(isDigit(this.c))
        {
            number.append(this.c);
            this.consume();
        }

        if(this.c == 'e' || this.c == 'E')
        {
            number.append('e');
            this.consume();
            if(this.c == '+' || this.c == '-')
            {
                number.append(this.c);
                this.consume();
            }

            while(isDigit(this.c))
            {
                number.append(this.c);
                this.consume();
            }
        }

        return Double.valueOf(number.toString());
    }

    private CharSequence singleQuotedString()
        throws IOException
    {
        this.match('\'');
        this.consume();

        final StringBuilder buffer= new StringBuilder();
        while(this.c != EOF)
        {
            if(this.c == '\'' && this.c2 == '\'')
            {
                buffer.append('\'');
                this.consume();
                this.consume();
            }
            else if(this.c == '\'')
            {
                break;
            }
            else
            {
                buffer.append(this.c);
                this.consume();
            }
        }

        this.match('\'');
        this.consume();

        return buffer.toString();
    }

    private CharSequence doubleQuotedString()
        throws IOException
    {
        this.match('"');
        this.consume();

        final StringBuilder buffer= new StringBuilder();
        while(this.c != EOF)
        {
            if(this.c == '\\')
            {
                this.consume();
                // \... - 3 octal digits
                // \..  - 2 octal digits
                // \.   - 1 octal digits
                if(isOctDigit(this.c))
                {
                    final StringBuilder octal= new StringBuilder(3);
                    octal.append(this.c);
                    this.consume();
                    while(isOctDigit(this.c))
                    {
                        octal.append(this.c);
                        this.consume();
                    }
                    buffer.append((char)(int)Integer.valueOf(octal.toString(), 8));
                }
                // \x.. - 2 hex digits
                // \x.  - 1 hex digits
                else if(this.c == 'x' || this.c == 'X')
                {
                    final StringBuilder hex= new StringBuilder(2);
                    this.consume();
                    while(isHexDigit(this.c))
                    {
                        hex.append(this.c);
                        this.consume();
                    }
                    buffer.append((char)(int)Integer.valueOf(hex.toString(), 16));
                }
                // \\u.... - 4 hex digits
                else if(this.c == 'u' || this.c == 'U')
                {
                    final StringBuilder hex= new StringBuilder(4);
                    this.consume();
                    while(hex.length() < 4)
                    {
                        hex.append(this.c);
                        this.consume();
                    }
                    buffer.append((char)(int)Integer.valueOf(hex.toString(), 16));
                }
                else if(this.c == 'b')
                {
                    this.consume();
                    buffer.append("\b");
                }
                else if(this.c == 'e')
                {
                    this.consume();
                    buffer.append((char)27);
                }
                else if(this.c == 'f')
                {
                    this.consume();
                    buffer.append("\f");
                }
                else if(this.c == 'n')
                {
                    this.consume();
                    buffer.append("\n");
                }
                else if(this.c == 'r')
                {
                    this.consume();
                    buffer.append("\r");
                }
                else if(this.c == 't')
                {
                    this.consume();
                    buffer.append("\t");
                }
                else if(this.c == '"')
                {
                    this.consume();
                    buffer.append('"');
                }
                else if(this.c == '\\')
                {
                    this.consume();
                    buffer.append('\\');
                }
                else
                {
                    throw new IllegalArgumentException("Cannot recognize \\" + this.c);
                }
            }
            else if(this.c == '"')
            {
                break;
            }
            else
            {
                buffer.append(this.c);
                this.consume();
            }
        }

        this.match('"');
        this.consume();

        return buffer.toString();
    }

    private void skip()
        throws IOException
    {
        while(this.c != EOF)
        {
            switch(this.c)
            {
            case ' ':
            case '\t':
            case '\r':
                // skip
                break;
            case '\n':
                if(this.c2 == '\\')
                {
                    // skip
                    break;
                }
                else
                {
                    return;
                }
            default:
                return;
            }
            this.consume();
        }
    }

    private void consume()
        throws IOException
    {
        if(this.c == EOF)
        {
            throw new EOFException();
        }

        ++this.offset;
        this.c= this.c2;
        this.c2= (char) this.in.read();
    }

    private void match(char expects)
    {
        if(this.c != expects)
        {
            throw new IllegalArgumentException();
        }
    }

    private static final char EOF= (char)-1;

    private final InputStreamReader in;

    private char c;

    private char c2;

    private int offset;
}
