package org.seasar.kvasir.cms.java.impl;

import java.io.Reader;

import org.codehaus.janino.ClassBodyEvaluator;
import org.codehaus.janino.Scanner;
import org.codehaus.janino.SimpleCompiler;
import org.seasar.kvasir.base.EmptySettings;
import org.seasar.kvasir.base.plugin.AbstractPlugin;
import org.seasar.kvasir.cms.java.CompileException;
import org.seasar.kvasir.cms.java.JavaPlugin;
import org.seasar.kvasir.util.io.IOUtils;


public class JavaPluginImpl extends AbstractPlugin<EmptySettings>
    implements JavaPlugin

{
    @Override
    protected boolean doStart()
    {
        return true;
    }


    @Override
    protected void doStop()
    {
    }


    @SuppressWarnings("unchecked")
    public <S> Class<S> compileClassBody(Reader classBody, Class<S> superClass,
        ClassLoader classLoader)
        throws CompileException
    {
        try {
            return new ClassBodyEvaluator(new Scanner(null, classBody),
                superClass, new Class[0], classLoader).getClazz();
        } catch (Throwable t) {
            throw new CompileException(t);
        } finally {
            IOUtils.closeQuietly(classBody);
        }
    }


    public ClassLoader compile(Reader javaSource, ClassLoader classLoader)
        throws CompileException
    {
        try {
            return new SimpleCompiler(new Scanner(null, javaSource),
                classLoader).getClassLoader();
        } catch (Throwable t) {
            throw new CompileException(t);
        } finally {
            IOUtils.closeQuietly(javaSource);
        }
    }
}
