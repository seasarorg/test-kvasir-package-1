package org.seasar.kvasir.cms.kdiary.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.seasar.kvasir.page.ability.content.ContentHandler;
import org.seasar.kvasir.util.el.VariableResolver;
import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.IOUtils;


public class TdiaryContentHandler
    implements ContentHandler
{
    public static final String ENCODING_DEFAULT = "UTF-8";

    private static final String SP = System.getProperty("line.separator");


    public Object compile(InputStream in, String encoding, String type)
    {
        return toHTML(in, encoding, type, null);
    }


    public String toHTML(Object compiled, VariableResolver resolver)
    {
        if (!(compiled instanceof String)) {
            return null;
        } else {
            return (String)compiled;
        }
    }


    public String toHTML(InputStream in, String encoding, String type,
        VariableResolver resolver)
    {
        if (encoding == null) {
            encoding = ENCODING_DEFAULT;
        }

        StringBuilder sb = new StringBuilder();
        boolean inSection = false;
        boolean verb = false;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                in, encoding));

            String line;
            while ((line = reader.readLine()) != null) {
                boolean emptyLine = (line.trim().length() == 0);
                char begin;
                if (line.length() > 0) {
                    begin = line.charAt(0);
                } else {
                    begin = '\0';
                }

                if (!inSection) {
                    if (!emptyLine) {
                        verb = false;
                        if (begin == '<'
                            && (line.length() < 2 || line.charAt(1) != '<')) {
                            verb = true;
                        } else {
                            sb.append("<div class=\"section\">");
                            sb.append(SP);
                            if (begin == '<') {
                                line = line.substring(1);
                            }
                        }

                        inSection = true;

                        if (begin == ' ' || begin == '　') {
                            sb.append("<p>");
                            sb.append(line);
                            sb.append("</p>");
                            sb.append(SP);
                        } else {
                            if (verb) {
                                sb.append(line);
                                sb.append(SP);
                            } else {
                                sb.append("<h3>");
                                sb.append(line);
                                sb.append("</h3>");
                                sb.append(SP);
                            }
                        }
                    } else {
                        ;
                    }
                } else {
                    if (!emptyLine) {
                        if (verb) {
                            sb.append(line);
                            sb.append(SP);
                        } else {
                            sb.append("<p>");
                            sb.append(line);
                            sb.append("</p>");
                            sb.append(SP);
                        }
                    } else {
                        if (!verb) {
                            sb.append("</div>");
                            sb.append(SP);
                        }
                        inSection = false;
                    }
                }
            }

            if (inSection) {
                if (!verb) {
                    sb.append("</div>");
                    sb.append(SP);
                }
            }
        } catch (UnsupportedEncodingException ex) {
            throw new IORuntimeException(ex);
        } catch (IOException ignore) {
        } finally {
            IOUtils.closeQuietly(in);
        }

        return sb.toString();
    }
}
