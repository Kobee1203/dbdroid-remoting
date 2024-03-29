package org.nds.dbdroid.remoting.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.cmr.repository.datatype.TypeConversionException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
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
import org.nds.dbdroid.reflect.utils.AnnotationUtils;
import org.nds.dbdroid.remoting.EntityAliases;
import org.nds.dbdroid.remoting.RawQuery;
import org.nds.dbdroid.remoting.XStreamHelper;
import org.nds.dbdroid.service.EndPoint;
import org.nds.dbdroid.service.HttpMethod;
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

    public final Object execute(Object obj, Method method, Object... args) {
        return sendQuery(obj, method, args);
    }

    private void postQuery(String queryString) {
        sendQuery(new RawQuery(queryString), null);
    }

    private <E> List<E> postQuery(Query query) {
        // EntityAliases entityAliases = entityAliasesFromEntity.get(query.getEntityClass());
        return (List<E>) sendQuery(query, null);
    }

    private Object postQuery(Object entity) {
        // EntityAliases entityAliases = entityAliasesFromEntity.get(entity.getClass());
        return sendQuery(entity, null);
    }

    private Object sendQuery(Object obj, Method method, Object[] args) {
        return send(method.getDeclaringClass(), method, args, null);
    }

    private Object sendQuery(Object arguments, EntityAliases entityAliases) {
        Object[] args = arguments == null ? null : arguments instanceof Object[] ? (Object[]) arguments : new Object[] { arguments };

        StackTraceElementExtended callingElement = getCallingElement(getParameterTypes(args));
        Method method = callingElement.getMethod();
        Class<?> clazz = callingElement.getDeclaringClass();
        if (entityAliases != null && entityAliases.getEntity() != null) {
            clazz = getDAOFromEntity(entityAliases.getEntity()).getClass();
        }

        return send(clazz, method, args, null);
    }

    private StackTraceElementExtended getCallingElement(Class<?>[] parameterTypes) {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        StackTraceElementExtended callingElement = null;
        for (StackTraceElement element : elements) {
            String className = element.getClassName();
            try {
                Class<?> clazz = Class.forName(className);
                if (getClass().equals(clazz) || ClientManager.class.equals(clazz) || ClientInvocationHandler.class.equals(clazz)) {
                    callingElement = new StackTraceElementExtended(element, parameterTypes);
                } else if (IAndroidDAO.class.isAssignableFrom(clazz) || IAndroidService.class.isAssignableFrom(clazz)) {
                    if (Proxy.class.isAssignableFrom(clazz)) {
                        element = new StackTraceElement(clazz.getInterfaces()[0].getName(), element.getMethodName(), element.getFileName(), element.getLineNumber());
                    }
                    callingElement = new StackTraceElementExtended(element, parameterTypes);
                } else if (callingElement != null) {
                    break;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return callingElement;
    }

    private Object send(Class<?> clazz, Method method, Object[] args, EntityAliases entityAliases) {
        Object result = null;

        HttpClient httpClient = new DefaultHttpClient();
        try {
            // Create a local instance of cookie store
            CookieStore cookieStore = new BasicCookieStore();

            // Create local HTTP context
            HttpContext localContext = new BasicHttpContext();
            // Bind custom cookie store to the local context
            localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

            EndPoint endPointClazz = AnnotationUtils.getAnnotation(clazz, EndPoint.class);
            EndPoint endPointMethod = AnnotationUtils.getAnnotation(method, EndPoint.class);
            String uri = getUri(clazz.getName(), method.getName(), endPointClazz, endPointMethod);
            HttpUriRequest httpUriRequest = getHttpUriRequest(serverUrl + uri, endPointClazz, endPointMethod, args, entityAliases);
            try {
                HttpResponse response = httpClient.execute(httpUriRequest, localContext);
                // String xml = HttpHelper.getStringResponse(response);
                String xml = response.getEntity() != null ? EntityUtils.toString(response.getEntity(), null) : null;
                log.debug("RESULT: " + xml);
                result = XStreamHelper.fromXML(xml, entityAliases);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            } catch (RuntimeException ex) {
                // In case of an unexpected exception you may want to abort
                // the HTTP request in order to shut down the underlying
                // connection immediately.
                httpUriRequest.abort();
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

    private HttpUriRequest getHttpUriRequest(String url, EndPoint endPointClazz, EndPoint endPointMethod, Object[] args, EntityAliases entityAliases) {

        HttpUriRequest httpUriRequest;

        HttpMethod httpMethod;
        if (endPointMethod != null && endPointMethod.httpMethod().length > 0) {
            httpMethod = endPointMethod.httpMethod()[0];
        } else if (endPointClazz != null && endPointClazz.httpMethod().length > 0) {
            httpMethod = endPointClazz.httpMethod()[0];
        } else {
            httpMethod = HttpMethod.GET;
        }

        try {
            switch (httpMethod) {
                case DELETE:
                    httpUriRequest = new HttpDelete(url + getUrlParams(args));
                    break;
                case GET:
                    httpUriRequest = new HttpGet(url + getUrlParams(args));
                    break;
                case HEAD:
                    httpUriRequest = new HttpHead(url + getUrlParams(args));
                    break;
                case OPTIONS:
                    httpUriRequest = new HttpOptions(url + getUrlParams(args));
                    break;
                case POST:
                    httpUriRequest = new HttpPost(url);
                    ((HttpEntityEnclosingRequestBase) httpUriRequest).setEntity(getHttpEntity(args, entityAliases));
                    break;
                case PUT:
                    httpUriRequest = new HttpPut(url);
                    ((HttpEntityEnclosingRequestBase) httpUriRequest).setEntity(getHttpEntity(args, entityAliases));
                    break;
                case TRACE:
                    httpUriRequest = new HttpTrace(url + getUrlParams(args));
                    break;
                default:
                    httpUriRequest = new HttpGet(url + getUrlParams(args));
                    break;
            }
        } catch (TypeConversionException e) {
            httpUriRequest = new HttpPost(url);
            ((HttpEntityEnclosingRequestBase) httpUriRequest).setEntity(getHttpEntity(args, entityAliases));
        }

        return httpUriRequest;
    }

    private String getUrlParams(Object[] args) throws TypeConversionException {
        String urlParams = "";
        if (args != null) {
            for (Object arg : args) {
                try {
                    String s = DefaultTypeConverter.INSTANCE.convert(String.class, arg);
                    urlParams += "/" + s;
                } catch (TypeConversionException e) {
                    throw e;
                }
            }
        }

        return urlParams;
    }

    private HttpEntity getHttpEntity(final Object[] args, final EntityAliases entityAliases) {
        ContentProducer cp = new ContentProducer() {
            public void writeTo(OutputStream outstream) throws IOException {
                if (args != null) {
                    String xml = XStreamHelper.toXML(args, entityAliases);
                    Writer writer = new OutputStreamWriter(outstream, "UTF-8");
                    writer.write(xml);
                    writer.flush();
                }
            }
        };
        HttpEntity httpEntity = new EntityTemplate(cp);
        return httpEntity;
    }

    private String getUri(String clazzName, String methodName, EndPoint endPointClazz, EndPoint endPointMethod) {
        String endPointClazz_ = clazzName;
        String endPointMethod_ = methodName;

        if (endPointClazz != null && endPointClazz.value() != null && endPointClazz.value().length() > 0) {
            endPointClazz_ = endPointClazz.value();
        }

        if (endPointMethod != null && endPointMethod.value() != null && endPointMethod.value().length() > 0) {
            endPointMethod_ = endPointMethod.value();
        }

        return "/" + endPointClazz_ + "/" + endPointMethod_;
    }

    private Class<?>[] getParameterTypes(Object[] args) {
        if (args == null) {
            return null;
        }

        List<Class<?>> argClasses = new ArrayList<Class<?>>();
        for (Object arg : args) {
            argClasses.add(arg.getClass());
        }

        return argClasses.toArray(new Class<?>[] {});
    }
}
