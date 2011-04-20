package org.nds.dbdroid.remoting.controller;

import net.sf.dozer.util.mapping.DozerBeanMapper;

import org.apache.log4j.Logger;
import org.nds.dbdroid.remoting.service.AndroidServiceManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class DBDroidRemotingController implements InitializingBean {

    private static final Logger logger = Logger.getLogger(DBDroidRemotingController.class);

    @Autowired
    AndroidServiceManager serviceManager;

    @Autowired
    private DozerBeanMapper dozerBeanMapper;

    public void afterPropertiesSet() throws Exception {
        if (serviceManager == null) {
            logger.warn("AndroidServiceManager must be initialized in the Spring configuration if the Android Services must be registered automatically -> Android Service Manager initialized with just the NetBeans Lookup.");
            serviceManager = new AndroidServiceManager();
        }
        if (dozerBeanMapper == null) {
            logger.warn("DozerBeanMapper must be initialized in the Spring configuration -> DozerBeanMapper initialized with the default DozerBeanMapper constructor.");
            dozerBeanMapper = new DozerBeanMapper();
        }
    }

    /*@RequestMapping(value = { "/dbdroid-remoting/", "/dbdroid-remoting/{ws}/{method}" }, method = RequestMethod.POST)
    public void dbdroidRemoting(@PathVariable("ws") String ws, @PathVariable("method") String method, HttpServletRequest request, HttpServletResponse response) {
        ServletOutputStream out = null;
        try {
            String query = IOUtils.toString(request.getInputStream());
            String s = "query retrieved [" + ws + "." + method + "]:\n" + query;

            Object arguments = XStreamHelper.fromXML(query, null);
            Object[] args = arguments instanceof Object[] ? (Object[]) arguments : new Object[] { arguments };

            Class<?> wsClass = Class.forName(ws);

            Object service = serviceManager.getService(wsClass);
            //WebApplicationContext waContext = WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext());
            //Object service = waContext.getBean(wsClass);
            Object o = MethodUtils.invokeMethod(service, method, args);

            String xml = XStreamHelper.toXML(o, null);
            
            // Object obj = dozerBeanMapper.map(o, o.getClass());
            // String xml = XStreamHelper.toXML(obj, null);
            // Object argts = XStreamHelper.fromXML(xml, null);
            // Object[] argnts = argts instanceof Object[] ? (Object[]) argts : new Object[] { argts };
            // ((Contact) argnts[0]).setFirstname("New firstname");
            // dozerBeanMapper.map(argnts[0], o);
            // SessionFactory.getClassMetaData(o.getClass()).getIdentifierPropertyName();

            byte[] bytes = xml.getBytes();
            response.setContentType("text/xml");
            response.setContentLength(bytes.length);
            out = response.getOutputStream();
            out.write(bytes, 0, bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            try {
                out.flush();
                out.close();
            } catch (Exception e) {
            }
        }
    }*/

}
