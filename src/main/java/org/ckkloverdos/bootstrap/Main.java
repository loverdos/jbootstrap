/*
 * Copyright 2004-2007 Christos KK Loverdos.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ckkloverdos.bootstrap;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Bootstraps an application by dynamically adjusting the CLASSPATH (via the context class loader).
 * @author Christos KK Loverdos
 * @version 1.0
 * @since 2004
 */
public class Main
{
    private static boolean haveRun;
    private static URLClassLoader ldr;
    private static boolean DEBUG;
    private static File myself = myself();

    public static URLClassLoader getClassLoader()
    {
        return ldr;
    }

    private static File getCanonicalFile(File f)
    {
        try
        {
            return f.getCanonicalFile();
        }
        catch(IOException e)
        {
            return f.getAbsoluteFile();
        }
    }
    
    private static void setURLClassPath(List list)
    {
        Main.ldr = new URLClassLoader((URL[]) list.toArray(new URL[0]));
        Thread.currentThread().setContextClassLoader(ldr);
    }
    
    private static void usage()
    {
        System.out.println("Usage: " + Main.class.getName() + " [-debug] [-lib DIR]* class args");
        System.exit(1);
    }

    private static void debug(String s)
    {
        if(DEBUG)
        {
            System.out.println(Main.class.getName() + ": " + s);
        }
    }

    private static boolean isJar(File f)
    {
        if(f.isFile())
        {
            String name = f.getName().toLowerCase();
            return name.endsWith(".jar") || name.endsWith(".zip");
        }
        return false;
    }

    private static boolean isLib(File f)
    {
        return isJar(f) || f.isDirectory();
    }

    private static boolean isMyself(File f)
    {
        return myself.equals(getCanonicalFile(f));
    }

    private static void addLibURL(File f, List urlList, boolean top) throws MalformedURLException
    {
        if(f.isFile())
        {
            if(isMyself(f))
            {
                debug("Ignoring myself: " + f);
            }
            else
            {
                debug("Adding " + f);
                urlList.add(f.toURI().toURL());
            }
        }
        else if(f.isDirectory())
        {
            File[] files = f.listFiles();
            boolean haveSubDirs = false;
            boolean haveNonLibs = false;
            for(int i = 0; i < files.length; i++)
            {
                File file = files[i];
                if(isLib(file))
                {
                    addLibURL(file, urlList, false);
                }
                else
                {
                    haveNonLibs = true;
                }
                if(file.isDirectory())
                {
                    haveSubDirs = true;
                }
            }
            if(top && (haveSubDirs || haveNonLibs))
            {
                debug("Adding " + f);
                urlList.add(f.toURI().toURL());
            }
        }
    }

    private static File myself()
    {
        String path;
        URL url = Main.class./*getClassLoader().*/getResource("");
        String surl = url.toString();
        if(surl.startsWith("jar:"))
        {
            surl = surl.substring("jar:".length(), surl.indexOf('!'));
        }

        if(surl.startsWith("file:"))
        {
            surl = surl.substring("file:".length() + 1);
        }

        while(surl.startsWith("/"))
        {
            surl = surl.substring(1);
        }
        path = surl;
        return getCanonicalFile(new File(path));
    }
    
    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, MalformedURLException, IllegalAccessException, NoSuchMethodException
    {
        if(haveRun)
        {
            String errMsg = Main.class + " cannot be run twice";
            System.err.println(errMsg);
            throw new RuntimeException(errMsg);
        }
        else
        {
            haveRun = true;
        }

        if(0 == args.length)
        {
            usage();
        }

        List urlList = new ArrayList();
        List libList = new ArrayList();
        Class classClass;
        String className;

        int i = 0;
        for(; i < args.length; i++)
        {
            String arg = args[i];
            if("-debug".equals(arg))
            {
                DEBUG = true;
            }
            else if("-lib".equals(arg))
            {
                if(i < args.length - 1)
                {
                    String lib = args[++i];
                    libList.add(lib);
                }
                else
                {
                    usage();
                }
            }
            else
            {
                break;
            }
        }

        if(i == args.length)
        {
            usage();
        }
        else
        {
            debug("I am " + myself);
            className = args[i++];
            int length = args.length - i;
            String[] tmp = new String[length];
            System.arraycopy(args, i, tmp, 0, length);
            args = tmp;

            Iterator ilibs = libList.iterator();
            while(ilibs.hasNext())
            {
                String lib = (String) ilibs.next();
                File flib = new File(lib);
                if(isLib(flib))
                {
                    addLibURL(flib, urlList, true);
                }
            }

            setURLClassPath(urlList);
            classClass = ldr.loadClass(className);

            try
            {
                Method main = classClass.getMethod("main", new Class[]{String[].class});
                if(Modifier.isStatic(main.getModifiers()))
                {
                    main.invoke(null, new Object[]{args});
                }
            }
            catch(InvocationTargetException e)
            {
                e.getCause().printStackTrace(System.err);
            }
        }
    }
}