package nablarch.common.web.handler;

import java.net.URL;

public class CustomClassLoader extends ClassLoader {
    private ClassLoader parent;
    public CustomClassLoader(ClassLoader parent) {
        super(null);
        this.parent = parent;
    }
    @Override
    public URL getResource(String name) {
        return parent.getResource(name);
    }
    
}