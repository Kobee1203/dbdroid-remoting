package org.nds.dbdroid.remoting.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.nds.dbdroid.DataBaseManager;
import org.nds.dbdroid.dao.IAndroidDAO;
import org.nds.dbdroid.exception.DBDroidException;
import org.nds.dbdroid.helper.EntityHelper;
import org.nds.dbdroid.query.LogicalOperator;
import org.nds.dbdroid.query.Operator;
import org.nds.dbdroid.query.Query;
import org.nds.dbdroid.query.QueryValueResolver;
import org.nds.dbdroid.remoting.EntityAliases;
import org.nds.dbdroid.remoting.XStreamHelper;
import org.nds.dbdroid.service.IAndroidService;
import org.nds.dbdroid.type.DataType;
import org.nds.dbdroid.type.DbDroidType;
import org.nds.logging.Logger;
import org.nds.logging.LoggerFactory;

class ClientDataBaseManager extends DataBaseManager {

    private static final Logger log = LoggerFactory.getLogger(ClientDataBaseManager.class);

    private final String serverUrl;

    private final Map<Class<?>, EntityAliases> entityAliasesFromEntity = new HashMap<Class<?>, EntityAliases>();

    private final Map<String, EntityAliases> entityAliasesFromTableName = new HashMap<String, EntityAliases>();

    public ClientDataBaseManager(InputStream config, String serverUrl) throws DBDroidException {
        super(config);
        this.serverUrl = serverUrl;
    }

    @Override
    public void onOpen() throws DBDroidException {
        List<Class<?>> entities = getEntities();
        for (Class<?> entity : entities) {
            String tableName = getTableNameFromEntity(entity);
            Field[] fields = getFieldsFromEntity(entity);
            Map<String, Field> fieldFromColumnName = new HashMap<String, Field>();
            Map<Field, String> columnNameFromField = new HashMap<Field, String>();
            for (Field field : fields) {
                String columnName = EntityHelper.getColumnName(field);
                fieldFromColumnName.put(columnName, field);
                columnNameFromField.put(field, columnName);
            }
            EntityAliases entityAliases = new EntityAliases(entity, tableName, fieldFromColumnName, columnNameFromField);
            entityAliasesFromEntity.put(entity, entityAliases);
            entityAliasesFromTableName.put(tableName, entityAliases);
        }
    }

    @Override
    public void onClose() throws DBDroidException {
    }

    @Override
    protected void onCheckEntity(Class<?> entityClass) throws DBDroidException {
        // TODO
    }

    @Override
    protected void onCreateTable(String tableName, Field[] fields) throws DBDroidException {
        // TODO
    }

    @Override
    protected void onUpdateTable(String tableName, Field[] fields) throws DBDroidException {
        // TODO

    }

    @Override
    protected void onResetTable(String tableName, Field[] fields) throws DBDroidException {
        // TODO

    }

    @Override
    public void delete(Object entity) {
        postQuery(entity);
    }

    @Override
    public <E> List<E> findAll(Class<E> entityClass) {
        return postQuery(createQuery(entityClass));
    }

    @Override
    public <E> E findById(Serializable id, Class<E> entityClass) {
        Field idField = EntityHelper.getIdField(entityClass);
        Query query = createQuery(entityClass);
        query.add(Query.createExpression(idField.getName(), id, DbDroidType.STRING, Operator.EQUAL));
        List<E> list = postQuery(query);
        return (list != null && !list.isEmpty() ? (E) list.get(0) : null);
    }

    @Override
    public <E> E saveOrUpdate(E entity) {
        return (E) postQuery(entity);
    }

    @Override
    public void rawQuery(String query) {
        postQuery(query);
    }

    @Override
    public <E> List<E> queryList(Query query) {
        return postQuery(query);
    }

    @Override
    public DataType getDataType() {
        // TODO
        return null;
    }

    @Override
    protected QueryValueResolver getQueryValueResolver() {
        // TODO
        return null;
    }

    @Override
    protected String onExpressionString(Operator operator, String value) {
        // TODO
        return null;
    }

    @Override
    protected String onExpressionString(LogicalOperator logicalOperator, String expression) {
        // TODO
        return null;
    }

    public final Object execute(String xml) {
        return postQuery(xml, null);
    }

    private void postQuery(String queryString) {
        postQuery("<rawQuery>" + queryString + "<rawQuery>", null);
    }

    private <E> List<E> postQuery(Query query) {
        EntityAliases entityAliases = entityAliasesFromEntity.get(query.getEntityClass());

        String queryString = XStreamHelper.toXML(query, null);

        return (List<E>) postQuery(queryString, entityAliases);
    }

    private Object postQuery(Object entity) {
        EntityAliases entityAliases = entityAliasesFromEntity.get(entity.getClass());

        String query = XStreamHelper.toXML(entity, null);

        return postQuery(query, entityAliases);
    }

    private Object postQuery(final String query, final EntityAliases entityAliases) {
        Object result = null;

        StackTraceElement callingElement = getCallingElement();
        String methodName = callingElement.getMethodName();
        String className = callingElement.getClassName();
        if (entityAliases != null && entityAliases.getEntity() != null) {
            className = getDAOFromEntity(entityAliases.getEntity()).getClass().getName();
        }

        ContentProducer cp = new ContentProducer() {
            public void writeTo(OutputStream outstream) throws IOException {
                Writer writer = new OutputStreamWriter(outstream, "UTF-8");
                writer.write(query);
                writer.flush();
            }
        };
        HttpEntity httpEntity = new EntityTemplate(cp);

        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            // Create a local instance of cookie store
            CookieStore cookieStore = new BasicCookieStore();

            // Create local HTTP context
            HttpContext localContext = new BasicHttpContext();
            // Bind custom cookie store to the local context
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

            HttpPost httppost = new HttpPost(serverUrl + "/" + className + "/" + methodName);
            try {
                httppost.setEntity(httpEntity);

                HttpResponse response = httpClient.execute(httppost, localContext);
                // String xml = HttpHelper.getStringResponse(response);
                String xml = EntityUtils.toString(response.getEntity(), null);
                log.debug(xml);
                result = XStreamHelper.fromXML(xml, entityAliases);

                List<Cookie> cookies = httpClient.getCookieStore().getCookies();
                for (int i = 0; i < cookies.size(); i++) {
                    System.out.println("Local cookie: " + cookies.get(i));
                }

            } catch (IOException e) {
                log.error(e.getMessage(), e);
            } catch (RuntimeException ex) {
                // In case of an unexpected exception you may want to abort
                // the HTTP request in order to shut down the underlying
                // connection immediately.
                httppost.abort();
                throw ex;
            }
        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            httpClient.getConnectionManager().shutdown();
        }

        return result;
    }

    private StackTraceElement getCallingElement() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        StackTraceElement callingElement = null;
        for (StackTraceElement element : elements) {
            String className = element.getClassName();
            try {
                Class<?> clazz = Class.forName(className);
                if (getClass().equals(clazz) || ClientManager.class.equals(clazz) || ClientInvocationHandler.class.equals(clazz)) {
                    callingElement = element;
                } else if (IAndroidDAO.class.isAssignableFrom(clazz) || IAndroidService.class.isAssignableFrom(clazz)) {
                    if (Proxy.class.isAssignableFrom(clazz)) {
                        element = new StackTraceElement(clazz.getInterfaces()[0].getName(), element.getMethodName(), element.getFileName(), element.getLineNumber());
                    }
                    callingElement = element;
                } else if (callingElement != null) {
                    break;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return callingElement;
    }
}
