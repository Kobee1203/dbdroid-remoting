package org.nds.dbdroid.remoting;

import java.lang.reflect.Field;
import java.util.Map;

public class EntityAliases {

    private final Class<?> entity;
    private final String tableName;
    private final Map<String, Field> fieldFromColumnName;
    private final Map<Field, String> columnNameFromField;

    public EntityAliases(Class<?> entity, String tableName, Map<String, Field> fieldFromColumnName, Map<Field, String> columnNameFromField) {
        this.entity = entity;
        this.tableName = tableName;
        this.fieldFromColumnName = fieldFromColumnName;
        this.columnNameFromField = columnNameFromField;
    }

    public Class<?> getEntity() {
        return entity;
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumnNameFromField(Field field) {
        return columnNameFromField.get(field);
    }

    public Field getFieldFromColumnName(String columnName) {
        return fieldFromColumnName.get(columnName);
    }
}
