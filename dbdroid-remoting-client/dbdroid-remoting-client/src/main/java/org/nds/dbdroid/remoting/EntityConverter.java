package org.nds.dbdroid.remoting;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.repository.datatype.TypeConversionException;
import org.alfresco.service.cmr.repository.datatype.TypeConverter;
import org.apache.commons.lang.reflect.ConstructorUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.nds.dbdroid.reflect.utils.ReflectUtils;

public final class EntityConverter {

    private EntityConverter() {
    }

    public static <F, T> T convert(final F source, final Class<T> dest) {
        TypeConverter.Converter<F, T> converter = new TypeConverter.Converter<F, T>() {
            public T convert(F source) {
                T destInstance;
                try {
                    destInstance = ConstructorUtils.invokeConstructor(dest, null);
                } catch (Exception e) {
                    throw new TypeConversionException("Cannot instance the destination object: " + e.getMessage(), e);
                }

                Field[] fields = ReflectUtils.getPropertyFields(source.getClass());
                if (fields != null) {
                    for (Field field : fields) {
                        try {
                            Object value = FieldUtils.readField(field, source, true);
                            Field destField = FieldUtils.getField(dest, field.getName(), true);
                            if (destField != null) {
                                Object convertedValue = null;
                                try {
                                    convertedValue = DefaultTypeConverter.INSTANCE.convert(destField.getType(), value);
                                } catch (TypeConversionException e) {
                                    convertedValue = value;
                                }
                                FieldUtils.writeField(destField, destInstance, convertedValue);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }

                return destInstance;
            }
        };

        return converter.convert(source);
    }

    public static <F, T> Collection<T> convert(final Collection<F> source, final Class<T> dest) {
        TypeConverter.Converter<Collection<F>, Collection<T>> converter = new TypeConverter.Converter<Collection<F>, Collection<T>>() {
            public Collection<T> convert(Collection<F> source) {
                if (source == null) {
                    return null;
                }

                Collection<T> converted = new ArrayList<T>(source.size());
                for (F value : source) {
                    converted.add(EntityConverter.convert(value, dest));
                }

                return converted;
            }
        };

        return converter.convert(source);
    }
}
