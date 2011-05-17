package org.nds.dbdroid.remoting;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;

import org.nds.dbdroid.helper.EntityHelper;
import org.nds.dbdroid.query.Query;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public final class XStreamHelper {

    private XStreamHelper() {
    }

    private static XStream initXStream(EntityAliases entityAliases) {
        XStream xstream = new XStream(new DomDriver());

        if (entityAliases != null) {
            xstream.processAnnotations(entityAliases.getEntity());
            xstream.alias(entityAliases.getTableName(), entityAliases.getEntity());
            Field[] fields = EntityHelper.getFields(entityAliases.getEntity());
            for (Field field : fields) {
                xstream.aliasField(entityAliases.getColumnNameFromField(field), entityAliases.getEntity(), field.getName());
            }
        }

        return xstream;
    }

    public static String toXML(Object entity, EntityAliases entityAliases) {
        String xml = null;
        if (entity != null) {
            XStream xstream = initXStream(entityAliases);
            xml = xstream.toXML(entity);
        }
        return xml;
    }

    public static void toXML(Object entity, EntityAliases entityAliases, OutputStream os) {
        if (entity != null) {
            XStream xstream = initXStream(entityAliases);
            xstream.toXML(entity, os);
        }
    }

    public static void toXML(Object entity, EntityAliases entityAliases, Writer w) {
        if (entity != null) {
            XStream xstream = initXStream(entityAliases);
            xstream.toXML(entity, w);
        }
    }

    public static String toXML(Query query, EntityAliases entityAliases) {
        String xml = null;
        if (query != null) {
            XStream xstream = initXStream(entityAliases);
            xml = xstream.toXML(query);
        }
        return xml;
    }

    public static Object fromXML(String xml, EntityAliases entityAliases) {
        Object entity = null;
        if (xml != null && xml.length() > 0) {
            XStream xstream = initXStream(entityAliases);
            entity = xstream.fromXML(xml);
        }
        return entity;
    }

    public static Object fromXML(InputStream xml, EntityAliases entityAliases) {
        Object entity = null;
        if (xml != null) {
            XStream xstream = initXStream(entityAliases);
            entity = xstream.fromXML(xml);
        }
        return entity;
    }

    public static Object fromXML(Reader xml, EntityAliases entityAliases) {
        Object entity = null;
        if (xml != null) {
            XStream xstream = initXStream(entityAliases);
            entity = xstream.fromXML(xml);
        }
        return entity;
    }

}
