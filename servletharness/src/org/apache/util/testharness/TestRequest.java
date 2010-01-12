/* ============================================================================
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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUtils;

/**
 * Provides a means to programmatically build a request to a
 * servlet, for testing purposes.
 *
 * @author Rob Crawford <crawford@iac.net>
 * @version $Revision: 1.1 $ $Date: 2004/05/29 02:09:52 $
 *
 */
public class TestRequest implements HttpServletRequest {

    public static final int GET = 1;
    public static final int POST = 2;
    public static final int PUT = 3;
    public static final int DELETE = 4;

    public static final String FORM_CONTENT_TYPE =
        "application/x-www-form-urlencoded";

    private int _method = TestRequest.GET;
    private Vector _cookies = null;
    private URL _request_url = null;

    private String _protocol = "HTTP/1.1";
    private String _remote_addr = "127.0.0.1";
    private String _remote_host = "localhost";
    private String _remote_user = null;
    private String _auth_type = null;
    private String _request_uri = null;
    private String _server_name = null;
    private String _servlet_path = null;
    private String _path_info = null;
    private String _path_translated = null;
    private String _query_string = null;
    private String _content_type = "-1";
    private String _context_path = "";

    private Hashtable _parameters = null;
    private Hashtable _attributes = null;
    private Hashtable _headers = null;
    private byte[] _content = null;

    private HttpSession _session = null;
    
    private String charEncoding = "UTF-8";

    public TestRequest() {
        _cookies = new Vector();
        _parameters = new Hashtable();
        _attributes = new Hashtable();
        _headers = new Hashtable();
    }

    public TestRequest(int method) {
        this();
        setMethod(method);
    }

    public TestRequest(int method, String request) {
        this(method);
        String request_url = null;
        if (method == TestRequest.GET) {
            StringTokenizer toker = new StringTokenizer(request, "?");
            request_url = toker.nextToken();
            if (toker.hasMoreTokens()) {
                parseParameters(toker.nextToken());
            }
        } else if (method == TestRequest.POST) {
            request_url = request;
        }
        try {
            _request_url = new URL(request_url);
            _request_uri = _request_url.getPath();

            String filePath = _request_url.getFile();
            try {
                _context_path = filePath.substring(0, filePath.indexOf("/", 1));
                _servlet_path = filePath.substring(_context_path.length());
            } catch (StringIndexOutOfBoundsException e) {
                _context_path = "";
                _servlet_path = filePath;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not parse URL: " + request);
        }
    }

    private final void parseParameters(String query_str) {
        Hashtable query_parms = HttpUtils.parseQueryString(query_str);
        if (query_parms != null) {
            Enumeration keys = query_parms.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                String[] args = (String[]) query_parms.get(key);
                for (int i = 0; i < args.length; i++) {
                    addParameter(key, args[i]);
                }
            }
        }
    }

    public void setMethod(int method) {
        if ((method != TestRequest.GET)
            && (method != TestRequest.POST)
            && (method != TestRequest.PUT)
            && (method != TestRequest.DELETE)) {
            throw new IllegalArgumentException(
                "Must be one of " + "TestRequest.GET, POST, PUT, or DELETE.");
        }
        _method = method;
    }

    public void setMethod(String method) {
        if (method.equals("GET")) {
            setMethod(TestRequest.GET);
        } else if (method.equals("POST")) {
            setMethod(TestRequest.POST);
        } else if (method.equals("PUT")) {
            setMethod(TestRequest.PUT);
        } else if (method.equals("DELETE")) {
            setMethod(TestRequest.DELETE);
        } else {
            throw new IllegalArgumentException(
                "Must be one of GET, " + "POST, PUT, or DELETE.");
        }
    }

    public void setRequestURI(String uri) {
        _request_uri = uri;
    }

    public void setRequestURL(URL request_url) {
        _request_url = request_url;
    }

    public void setProtocol(String protocol) {
        _protocol = protocol;
    }

    public void setRemoteHost(String host) {
        _remote_host = host;
    }

    public void setRemoteAddress(String addr) {
        _remote_addr = addr;
    }

    public void setContent(byte[] content) {
        _content = content;
    }

    public void setContextPath(String context_path) {
        _context_path = context_path;
    }

    public void setServletPath(String servlet_path) {
        _servlet_path = servlet_path;
    }

    public void setPathInfo(String path_info) {
        _path_info = path_info;
        if (_servlet_path.indexOf(_path_info) > -1)
          _servlet_path = _servlet_path.substring(0, _servlet_path.indexOf(_path_info));
    }

    public void setRemoteUser(String remote_user) {
        _remote_user = remote_user;
        if (_auth_type == null)
            setAuthType("BASIC"); // FIXME
    }

    public void setAuthType(String auth_type) {
        _auth_type = auth_type;
    }

    public void setPathTranslated(String path_translated) {
        _path_translated = path_translated;
    }

    public void setQueryString(String query_string) {
        _query_string = query_string;
    }

    public void setHeader(String name, String value) {
        _headers.put(name, value);
    }

    public void addParameter(String name, String value) {
        Vector vec = (Vector) _parameters.get(name);
        if (vec == null) {
            vec = new Vector();
            _parameters.put(name, vec);
        }
        vec.addElement(value);
    }

    public Locale getLocale() {
        // XXX this should return a locale based on the
        // Accept-Language header, if one is present.
        return Locale.getDefault();
    }

    public Enumeration getLocales() {
        // XXX this should return an enumeration of 
        // locales in preferred order.
        Vector locales = new Vector(1);
        locales.add(getLocale());
        return locales.elements();
    }

    public boolean isSecure() {
        // FIXME this should return a value based on whether
        // HTTPS is being used.
        return false;
    }

    /**
     * This method returns null as allowed by the ServletRequest
     * specification.
     */
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    public void addCookie(Cookie cookie) {
        _cookies.addElement(cookie);
    }

    public void removeCookie(Cookie cookie) {
        _cookies.removeElement(cookie);
    }

    public void clearCookies() {
        _cookies.removeAllElements();
    }

    public int getContentLength() {
        if (_content == null)
            return -1;
        else
            return _content.length;
    }

    public String getContentType() {
        return _content_type;
    }

    public void setContentType(String contentType) {
        _content_type = contentType;
    }

    public void setContentTypeToForm(String content) {
        setContentType(TestRequest.FORM_CONTENT_TYPE);
        parseParameters(content);
        setContent(content.getBytes());
    }

    public String getProtocol() {
        return _protocol;
    }

    public String getScheme() {
        return _request_url.getProtocol();
    }

    public void setServerName(String serverName) {
        _server_name = serverName;
    }

    public String getServerName() {
        return _server_name;
    }

    public int getServerPort() {
        if (_request_url.getPort() == -1)
            return 80;
        return _request_url.getPort();
    }

    public String getRemoteAddr() {
        return _remote_addr;
    }

    public String getRemoteHost() {
        return _remote_host;
    }

    public String getRealPath(String path) {
        throw new RuntimeException("Not Implemented");
    }

    public ServletInputStream getInputStream() throws IOException {
        return new TestInputStream(_content);
    }

    private class TestInputStream extends ServletInputStream {
        InputStream in = null;
        long available = -1;

        TestInputStream(byte[] content) {
            in = new ByteArrayInputStream(content);
            available = content.length;
        }

        public int read() throws IOException {
            if (available > 0) {
                available--;
                return in.read();
            }
            return -1;
        }

        public int read(byte[] b) throws IOException {
            return read(b, 0, b.length);
        }

        public int read(byte[] b, int off, int len) throws IOException {
            if (available > 0) {
                if (len > available) {
                    // shrink len
                    len = (int) available;
                }
                int read = in.read(b, off, len);
                if (read != -1) {
                    available -= read;
                } else {
                    available = -1;
                }
                return read;
            }
            return -1;
        }

        public long skip(long n) throws IOException {
            long skip = in.skip(n);
            available -= skip;
            return skip;
        }

        public void close() throws IOException {
            // do we care?
        }
    }

    /**
     * @deprecated Please use getParameterValues
     */
    public String getParameter(String name) {
        Vector vec = (Vector) _parameters.get(name);
        if (vec == null)
            return null;
        return (String) vec.elementAt(0);
    }

    public String[] getParameterValues(String name) {
        Vector vec = (Vector) _parameters.get(name);
        if (vec == null)
            return null;
        String[] strings = new String[vec.size()];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = (String) vec.elementAt(i);
        }
        return strings;
    }

    public void setParameter(String name, String val) {
        Vector vec = (Vector) _parameters.get(name);
        if (vec == null)
            vec = new Vector(10);
        vec.add(val);
        _parameters.put(name, vec);
    }

    public Enumeration getParameterNames() {
        return _parameters.keys();
    }

    public Object getAttribute(String name) {
        return _attributes.get(name);
    }

    public void setAttribute(String name, Object attribute) {
        _attributes.put(name, attribute);
    }

    public void removeAttribute(String name) {
        _attributes.remove(name);
    }

    public Enumeration getAttributeNames() {
        return _attributes.keys();
    }

    public String getCharacterEncoding() {
        return charEncoding;
    }

    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    public Cookie[] getCookies() {
        if (_cookies.size() == 0) {
            return null;
        }
        Cookie[] cookies = new Cookie[_cookies.size()];
        for (int i = 0; i < cookies.length; i++) {
            cookies[i] = (Cookie) _cookies.elementAt(i);
        }
        return cookies;
    }

    public String getMethod() {
        switch (_method) {
            case TestRequest.POST :
                return "POST";
            case TestRequest.PUT :
                return "PUT";
            case TestRequest.DELETE :
                return "DELETE";
            case TestRequest.GET :
                // fall through
            default :
                return "GET";
        }
    }

    public String getRequestURI() {
        return _request_uri;
    }

    public String getServletPath() {
        return _servlet_path;
    }

    public String getPathInfo() {
        if (_path_info == null)
            return getServletPath();
        else
            return _path_info;
    }

    public String getPathTranslated() {
        return _path_translated;
    }

    public String getQueryString() {
        return _query_string;
    }

    public String getRemoteUser() {
        return _remote_user;
    }

    public String getAuthType() {
        return _auth_type;
    }

    public String getHeader(String name) {
        return (String) _headers.get(name);
    }

    /**
     * FIXME This is meant to return an enumeration of all of the
     * header values specified by the client.  At the moment it
     * assumes there is only one.
     */
    public Enumeration getHeaders(String name) {
        Vector headers = new Vector();
        headers.add(_headers.get(name));
        return headers.elements();
    }

    public int getIntHeader(String name) {
        return Integer.parseInt(getHeader(name));
    }

    public long getDateHeader(String name) {
        try {
            if (getHeader(name) == null)
                return -1;
            Date date = DateFormat.getInstance().parse(getHeader(name));
            return date.getTime();
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Enumeration getHeaderNames() {
        return _headers.keys();
    }

    public HttpSession getSession() {
        return getSession(true);
    }

    public HttpSession getSession(boolean create) {
        if (_session == null && create == true) {
            _session = new TestHttpSession();
        }
        return _session;
    }

    public void setSession(HttpSession session) {
        _session = session;
    }

    public String getRequestedSessionId() {
        throw new RuntimeException("Not Implemented");
    }

    public boolean isRequestedSessionIdValid() {
        throw new RuntimeException("Not Implemented");
    }

    public boolean isRequestedSessionIdFromCookie() {
        throw new RuntimeException("Not Implemented");
    }

    public boolean isRequestedSessionIdFromURL() {
        throw new RuntimeException("Not Implemented");
    }

    public boolean isRequestedSessionIdFromUrl() {
        return isRequestedSessionIdFromURL();
    }

    public String getContextPath() {
        return _context_path;
    }

    public boolean isUserInRole(String role) {
        throw new RuntimeException("Not Implemented");
    }

    public java.security.Principal getUserPrincipal() {
        throw new RuntimeException("Not Implemented");
    }

    public StringBuffer getRequestURL() {
        return(new StringBuffer(_request_url.toString()));
    }

    public Map getParameterMap() {
      return _parameters;
    }

    public void setCharacterEncoding(String character) {
        charEncoding = character;
    }

}
