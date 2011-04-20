package org.nds.dbdroid.remoting.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.output.StringBuilderWriter;
import org.nds.dbdroid.remoting.controller.IServiceController;
import org.nds.dbdroid.service.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

public class DBDroidServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Autowired
    IServiceController controller;

    @Override
    public void init(ServletConfig config) throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        invoke(request, response, HttpMethod.POST);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        invoke(request, response, HttpMethod.GET);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        invoke(request, response, HttpMethod.DELETE);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        invoke(request, response, HttpMethod.PUT);
    }

    @Override
    protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        invoke(request, response, HttpMethod.HEAD);
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        invoke(request, response, HttpMethod.OPTIONS);
    }

    @Override
    protected void doTrace(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        invoke(request, response, HttpMethod.TRACE);
    }

    protected void invoke(HttpServletRequest request, HttpServletResponse response, HttpMethod httpMethod) throws ServletException {
        System.out.println("***** BEGIN SERVICE INVOCATION *****");

        /*
        System.out.println("ServletPath: " + request.getServletPath());
        System.out.println("ContextPath: " + request.getContextPath());
        System.out.println("RequestURI: " + request.getRequestURI());
        System.out.println("RequestURL: " + request.getRequestURL());
        System.out.println("PathInfo: " + request.getPathInfo());
        System.out.println("PathTranslated: " + request.getPathTranslated());
        System.out.println("QueryString: " + request.getQueryString());
        System.out.println("ServletContextName: " + request.getSession().getServletContext().getServletContextName());
        System.out.println("RealPath[/]: " + request.getSession().getServletContext().getRealPath("/"));
        */

        System.out.println("# HTTP METHOD: " + httpMethod);

        try {
            String service = null;
            String method = null;
            Object[] arguments = null;

            String pathInfo = request.getPathInfo();
            if (pathInfo.startsWith("/")) {
                pathInfo = pathInfo.substring(1);
            }
            int slashIndex = pathInfo.indexOf('/');
            if (slashIndex != -1) {
                service = pathInfo.substring(0, slashIndex);
                method = pathInfo.substring(slashIndex + 1);
                slashIndex = method.indexOf('/');
                if (slashIndex != -1) { // one argument in the URI
                    String m = method.substring(0, slashIndex);
                    String argument = method.substring(slashIndex + 1);

                    arguments = new Object[] { argument };
                    method = m;
                }
                System.out.println("SERVICE NAME: " + service);
                System.out.println("METHOD: " + method);
                System.out.println("ARGUMENTS: " + Arrays.toString(arguments));

                System.out.println("##### ATTRIBUTES NAMES #####");
                Enumeration<String> attrNames = request.getAttributeNames();
                while (attrNames.hasMoreElements()) {
                    String attrName = attrNames.nextElement();
                    Object value = request.getAttribute(attrName);
                    System.out.println(attrName + ": " + value);
                }
                System.out.println("##### HEADER NAMES #####");
                Enumeration<String> headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    Object value = request.getAttribute(headerName);
                    System.out.println(headerName + ": " + value);
                }
                System.out.println("##### PARAMETER NAMES #####");
                Enumeration<String> paramNames = request.getParameterNames();
                while (paramNames.hasMoreElements()) {
                    String paramName = paramNames.nextElement();
                    Object value = request.getAttribute(paramName);
                    System.out.println(paramName + ": " + value);
                }
                System.out.println("##### PARAMETER MAP #####");
                Map<?, ?> paramMap = request.getParameterMap();
                for (Map.Entry<?, ?> entry : paramMap.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }

                String s1 = toString(request.getInputStream());
                System.out.println("INPUSTREAM: " + s1);
                //String s2 = toString(request.getReader());
                //System.out.println("READER: " + s2);
            }
            //controller.invoke(service, method, arguments, httpMethod);
            System.out.println("***** END SERVICE INVOCATION *****");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String toString(InputStream input) throws IOException {
        StringBuilderWriter sw = new StringBuilderWriter();
        copy(input, sw);
        return sw.toString();
    }

    public static String toString(Reader input) throws IOException {
        StringBuilderWriter sw = new StringBuilderWriter();
        copy(input, sw);
        return sw.toString();
    }

    public static void copy(InputStream input, Writer output) throws IOException {
        InputStreamReader in = new InputStreamReader(input);
        copy(in, output);
    }

    public static int copy(Reader input, Writer output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    public static long copyLarge(Reader input, Writer output) throws IOException {
        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * The default buffer size
     */
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
}
