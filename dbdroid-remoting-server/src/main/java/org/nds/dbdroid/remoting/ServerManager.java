package org.nds.dbdroid.remoting;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nds.dbdroid.DataBaseManager;
import org.nds.dbdroid.config.ConfigXMLHandler;
import org.nds.dbdroid.dao.IAndroidDAO;
import org.nds.dbdroid.exception.DBDroidException;
import org.nds.dbdroid.query.LogicalOperator;
import org.nds.dbdroid.query.Operator;
import org.nds.dbdroid.query.Query;
import org.nds.dbdroid.query.QueryValueResolver;
import org.nds.dbdroid.type.DataType;

public class ServerManager extends DataBaseManager {

    public ServerManager(InputStream config) {
        super(config);
    }

    @Override
    public void onOpen() throws DBDroidException {
        // NOTHING TO DO
    }

    @Override
    protected ConfigXMLHandler getConfigXMLHandler(DataBaseManager dataBaseManager, ClassLoader classLoader) {
        return super.getConfigXMLHandler(dataBaseManager, classLoader);
    }

    @Override
    public void onClose() throws DBDroidException {
        // NOTHING TO DO
    }

    @Override
    protected void onCheckEntity(Class<?> entityClass) throws DBDroidException {
        // NOTHING TO DO
    }

    @Override
    protected void onCreateTable(String tableName, Field[] fields) throws DBDroidException {
        throw new DBDroidException("onCreateTable");
    }

    @Override
    protected void onUpdateTable(String tableName, Field[] fields) throws DBDroidException {
        throw new DBDroidException("onUpdateTable");
    }

    @Override
    protected void onResetTable(String tableName, Field[] fields) throws DBDroidException {
        throw new DBDroidException("onResetTable");
    }

    @Override
    public void delete(Object entity) {
    }

    @Override
    public <E> List<E> findAll(Class<E> entityClass) {
        return null;
    }

    @Override
    public <E> E findById(Serializable id, Class<E> entityClass) {
        return null;
    }

    @Override
    public <E> E saveOrUpdate(E entity) {
        return null;
    }

    @Override
    public void rawQuery(String query) {
    }

    @Override
    public <E> List<E> queryList(Query query) {
        return null;
    }

    @Override
    public DataType getDataType() {
        return null;
    }

    @Override
    protected QueryValueResolver getQueryValueResolver() {
        return null;
    }

    @Override
    protected String onExpressionString(Operator operator, String value) {
        return null;
    }

    @Override
    protected String onExpressionString(LogicalOperator logicalOperator, String expression) {
        return null;
    }

    public Object runQuery(String dao, String method, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Object o = null;
        /*
        String xml = IOUtils.toString(request.getInputStream());
        String s = "query retrieved [" + dao + "." + method + "]:\n" + xml;

        Object obj = XStreamHelper.fromXML(xml, null);

        if (obj instanceof Query) {

        } else {
            IAndroidDAO<?, ?> androidDAO = getDAOFromString(dao);
            if (androidDAO == null) {
                androidDAO = getDAOFromEntity(obj.getClass());
            }
            try {
                o = MethodUtils.invokeMethod(androidDAO, method, obj);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        */

        return o;
    }

    private IAndroidDAO<?, ?> getDAOFromString(String dao) {
        return null; // daosFromString.get(dao);
    }

}
