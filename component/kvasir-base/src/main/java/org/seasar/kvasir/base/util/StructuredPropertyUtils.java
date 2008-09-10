package org.seasar.kvasir.base.util;

import java.io.IOException;
import java.io.OutputStream;

import org.seasar.kvasir.util.io.IORuntimeException;
import org.seasar.kvasir.util.io.Resource;
import org.seasar.kvasir.util.io.ResourceNotFoundException;

import net.skirnir.xom.IllegalSyntaxException;
import net.skirnir.xom.ValidationException;


public class StructuredPropertyUtils
{
    protected StructuredPropertyUtils()
    {
    }


    public static <T> T getStructuredProperty(Resource structuredResource,
        Class<T> structureClass)
    {
        if (!structuredResource.exists()) {
            return null;
        }

        try {
            return XOMUtils.toBean(structuredResource.getInputStream(),
                structureClass);
        } catch (ValidationException ex) {
            throw new RuntimeException(
                "Marshaled property is illegal: resource=" + structuredResource
                    + ", structureClass=" + structureClass, ex);
        } catch (IllegalSyntaxException ex) {
            throw new RuntimeException(
                "Marshaled property is illegal: resource=" + structuredResource,
                ex);
        } catch (ResourceNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            throw new IORuntimeException(
                "Can't read property property: resource=" + structuredResource,
                ex);
        }
    }


    public static void setStructuredProperty(Resource structuredResource,
        Object structure)
    {
        if (structure == null) {
            structuredResource.delete();
            return;
        }
        if (!structuredResource.exists()) {
            structuredResource.getParentResource().mkdirs();
        }

        OutputStream os = null;
        try {
            os = structuredResource.getOutputStream();
            XOMUtils.toXML(structure, os);
        } catch (IOException ex) {
            throw new IORuntimeException(
                "Can't write marshaled metaData: resource="
                    + structuredResource, ex);
        } catch (ValidationException ex) {
            throw new RuntimeException(
                "Marshaled property is illegal: resource=" + structuredResource,
                ex);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException ignore) {
                }
            }
        }
    }
}
