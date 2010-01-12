/* 
 * =======================================================================
 * Copyright (c) 1997-1999 The Java Apache Project.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. Every modification must be notified to the Java Apache Project
 *    and redistribution of the modified code without prior notification
 *    is NOT permitted in any form.
 *
 * 4. All advertising materials mentioning features or use of this
 *    software must display the following acknowledgment:
 *    "This product includes software developed by the Java Apache Project
 *    (http://java.apache.org/)."
 *
 * 5. The names "JServ", "JServ Servlet Engine" and "Java Apache Project"
 *    must not be used to endorse or promote products derived from this 
 *    software without prior written permission.
 *
 * 6. Redistributions of any form whatsoever must retain the following
 *    acknowledgment:
 *    "This product includes software developed by the Java Apache Project
 *    (http://java.apache.org/)."
 *
 * THIS SOFTWARE IS PROVIDED BY THE JAVA APACHE PROJECT "AS IS" AND ANY
 * EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE JAVA APACHE PROJECT OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Java Apache Group. For more information 
 * on the Java Apache Project and the JServ Servlet Engine project, 
 * please see <http://java.apache.org/>.
 *
 */
package org.apache.util.testharness;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * A servlet testing harness. This class is used to load one or
 * more servlets outside of a servlet runner environment, make
 * requests of the servlet(s), and get the servlets' responses to
 * the requests.  
 */
public class ServletHarness implements ServletContext {
    private Map _servlets;
    private Vector _log;
    private Hashtable _attribs;
    private String _defaultServlet;
    private Map resources;


    /**
     * Constructor for the testing harness.  This constructor
     * creates and initializes a default servlet which will be
     * used to handle <CODE>service</CODE> calls that don't
     * specify which servlet to use.
     *
     * @param servletClass a class literal for the default servlet.
     * @param props Properties to be made available via the ServletConfig.
     * @param attribs Attributes to be made available via the ServletContext.
     */
    public ServletHarness(Class servletClass, Properties props, Hashtable attribs)
        throws
            IllegalAccessException,
            ClassNotFoundException,
            InstantiationException,
            ServletException {
        _servlets = new HashMap();
        _attribs = attribs;
        _log = new Vector();
        _defaultServlet = servletClass.getName();
        resources = new HashMap(16);

        addServlet(servletClass, props, _defaultServlet);
    }

    /**
     * Constructor for the testing harness.
     *
     * @param servletClass a class literal for the servlet under test
     * @param props Properties to be made available via the ServletConfig.
     */
    public ServletHarness(Class servletClass, Properties props)
        throws
            IllegalAccessException,
            ClassNotFoundException,
            InstantiationException,
            ServletException {
        this(servletClass, props, new Hashtable());
    }

    /**
     * Add a new servlet (by name) to the servlet harness.
     */
    public final void addServlet(Class servletClass, Properties props, String name)
        throws InstantiationException, ServletException, IllegalAccessException {
        Servlet servlet = (Servlet) servletClass.newInstance();
        servlet.init(new TestServletConfig(name, props, this));
        _servlets.put(name, servlet);
    }

    /**
     * Call a named servlet's <CODE>service</CODE> method.
     */
    public TestResponse service(String servletName, TestRequest req, TestResponse res)
        throws IOException, ServletException {
        Servlet toCall = (Servlet) _servlets.get(servletName);
        toCall.service(req, res);
        res.flushBuffer();
        return res;
    }

    /**
     * Call a named servlet's <CODE>service</CODE> method.
     */
    public TestResponse service(String servletName, TestRequest req)
        throws IOException, ServletException {
        return service(servletName, req, new TestResponse());
    }

    /**
     * Call the default servlet's <CODE>service</CODE> method.
     */
    public TestResponse service(TestRequest req, TestResponse res)
        throws IOException, ServletException {
        return service(_defaultServlet, req, res);
    }

    /**
     * Call the default servlet's <CODE>service</CODE> method.
     */
    public TestResponse service(TestRequest req)
        throws IOException, ServletException {
        return service(_defaultServlet, req);
    }

    public void destroy() {
        Iterator i = _servlets.values().iterator();
        while (i.hasNext()) {
            Servlet serv = (Servlet) i.next();
            serv.destroy();
        }
    }

    public Enumeration getLogEntries() {
        return _log.elements();
    }

    public Vector getLogEntry(int position) {
        return (Vector) _log.elementAt(position);
    }

    public int getLogEntryCount() {
        return _log.size();
    }

    public void clearLog() {
        _log = new Vector();
    }

    /**
     * Always returns null, as is permitted by the API
     * documentation.
     */
    public ServletContext getContext(String uripath) {
        return null;
    }

    public Servlet getServlet() {
        return getServlet(_defaultServlet);
    }

    public Servlet getServlet(String name) {
        return (Servlet) _servlets.get(name);
    }

    public Enumeration getServlets() {
        throw new RuntimeException();
    }

    public Enumeration getServletNames() {
        throw new RuntimeException();
    }

    public void log(String message, Throwable throwable) {
        Vector logEntry = new Vector();
        logEntry.addElement(message);
        logEntry.addElement(throwable);
        _log.addElement(logEntry);
    }

    public void log(String msg) {
        Vector logEntry = new Vector();
        logEntry.addElement(msg);
        _log.addElement(logEntry);
    }

    public void log(Exception exception, String msg) {
        Vector logEntry = new Vector();
        logEntry.addElement(msg);
        logEntry.addElement(exception);
        _log.addElement(logEntry);
    }

    public String getRealPath(String path) {
        throw new RuntimeException("Not Implemented");
    }

    public String getMimeType(String file) {
        throw new RuntimeException("Not Implemented");
    }

    public String getServerInfo() {
        return "ServletTestHarness";
    }

    public Enumeration getAttributeNames() {
        return _attribs.keys();
    }

    public Object getAttribute(String name) {
        return _attribs.get(name);
    }

    public void setAttribute(String name, Object value) {
        _attribs.put(name, value);
    }

    public void removeAttribute(String name) {
        _attribs.remove(name);
    }

    public int getMajorVersion() {
        return 2;
    }

    public int getMinorVersion() {
        return 2;
    }

    public URL getResource(String path) {
      throw new RuntimeException("Not Implemented");
    }

    public void setStreamableResource(String path, String stream) {
      resources.put(path, stream);
    }

    public InputStream getResourceAsStream(String path) {
      Object resource = resources.get(path);
      return resource == null ? null : new ByteArrayInputStream(resource.toString().getBytes());
    }

    /**
     * Returns null as permitted in the API documentation.
     */
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    /**
     * Returns null as permitted in the API documentation.
     */
    public RequestDispatcher getNamedDispatcher(String name) {
        return(new TestRequestDispatcher(getServlet(name)));
    }

    public String getInitParameter(String name) {
        throw new RuntimeException("Not Implemented");
    }

    public Enumeration getInitParameterNames() {
        throw new RuntimeException("Not Implemented");
    }

    public String getServletContextName() {
        throw new RuntimeException("Not Implemented");
    }
    
    public Set getResourcePaths() {
        throw new RuntimeException("Not Implemented");
    }
        
    public Set getResourcePaths(String arg0) {
        throw new RuntimeException("Not Implemented");
    }

}