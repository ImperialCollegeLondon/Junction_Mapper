package uk.ac.imperial.cisbio.cell_metric.utilities;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.filechooser.FileFilter;


public class ExtensionFileFilter extends FileFilter
{
    protected String    desc;
    protected boolean   allowDirs;
    protected Hashtable extensions;
    protected boolean   allowAll=false;	// bugfix #72566

    {
        extensions = new Hashtable();
    }

    public ExtensionFileFilter(boolean f)
    {
        allowDirs = f;
    }

    public void addExtension(String ext, boolean f)
    {
        if(f)
        {
            ext = ext.toLowerCase();
        }

        if(!(extensions.containsKey(ext)))
        {
            extensions.put(ext, new Boolean(f));
        
            if(ext.equals("*") || ext.equals("*.*"))
            {
                allowAll = true;
            }
        }
    }

    public boolean accept(File file)
    {
        int    index;
        String ext;
        String name;
        
        // is it a directory?
        if(file.isDirectory())
        {            
            return (allowDirs);
        }
        
        // handle "*" & "*.*"
        if(allowAll)
        {
            return (true);
        }
        
        name  = file.getName();
        index = name.lastIndexOf('.');

        // is there even an extension?
        if(index == -1)
        {
            return (false);
        }

        ext = name.substring(index + 1);

        // is it a match right off?
        if(extensions.containsKey(ext))
        {
            return (true);
        }

        for(Enumeration e = extensions.keys(); e.hasMoreElements();)
        {
            String filterExt;

            filterExt = (String)e.nextElement();

            if(((Boolean)extensions.get(filterExt)).equals(Boolean.FALSE))
            {
                if(filterExt.equals(".*"))
                {
                    return (true);
                }
                
                if(filterExt.equalsIgnoreCase(ext))
                {
                    return (true);
                }
            }
        }
        
        return (false);
    }

    public void setDescription(String str)
    {
        desc = str;
    }

    public String getDescription()
    {
        return (desc);
    }
}
