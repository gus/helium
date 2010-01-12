/*
 * ============================================================================
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

import java.io.*;
import java.util.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 *        TestResponse
 *
 *        Provides a means to programmatically test a response from a
 * servlet, for testing purposes.
 *
 * @author Rob Crawford <crawford@iac.net>
 * @version $Revision: 1.2 $ $Date: 2004/11/02 04:06:40 $
 *
 */
public class TestResponse implements HttpServletResponse {
    private String _contentType;
    private int _contentLength;
    private Vector _cookies;
    private Hashtable _headers;
    private int _statusCode;
    private String _errorMessage;
    private TestOutputStream _outStream;
    private PrintWriter _outWriter;
    private Locale _locale;

    public TestResponse() {
        _cookies = new Vector();
        _headers = new Hashtable();
        _locale = Locale.getDefault();
    }

    /**
     * A simple implementation of a ServletOutputStream that lets
     * the user get the returned content back.
     */
    private class TestOutputStream extends ServletOutputStream {
        ByteArrayOutputStream _content;
        boolean _committed;

        TestOutputStream() {
            _content = new ByteArrayOutputStream(32768);
            _committed = false;
        }

        public void write(int b) throws IOException {
            _content.write(b);
        }

        public byte[] getContent() {
            try {
                flush();
            } catch (IOException e) {
                throw new RuntimeException("Couldn't flush HTTP Response Stream");
            }
            return _content.toByteArray();
        }

        public void flush() throws IOException {
            _content.flush();
            _committed = true;
        }

        public void reset() {
            _content.reset();
        }

        public boolean isCommitted() {
            return _committed;
        }
    }

    public Vector getCookies() {
        return (Vector) _cookies.clone();
    }

    public Cookie getCookie(String name) {
        Cookie search_item = null;
        Enumeration cook = _cookies.elements();
        while (cook.hasMoreElements()) {
            Cookie cookie = (Cookie) cook.nextElement();
            if (cookie.getName().equals(name)) {
                search_item = cookie;
            }
        }
        return search_item;
    }

    public String getContentType() {
        return _contentType;
    }

    public int getContentLength() {
        return _contentLength;
    }

    public byte[] getContent() {
        return _outStream.getContent();
    }

    public String getContentAsString() {
        return new String(getContent());
    }

    public String getHeader(String name) {
        return (String) _headers.get(name);
    }

    public String getError() {
        return _errorMessage;
    }

    public int getStatus() {
        return _statusCode;
    }

    public void setContentLength(int len) {
        _contentLength = len;
    }

    public void setContentType(String type) {
        _contentType = type;
    }

    public ServletOutputStream getOutputStream() throws IOException {
      if (_outStream == null) {
          _outStream = new TestOutputStream();
      }
      return _outStream;
  }

    public PrintWriter getWriter() throws IOException {
      if (_outWriter == null) {
          _outWriter =
              new PrintWriter(new OutputStreamWriter(getOutputStream(), "ISO-8859-1"));
      }
      return _outWriter;
  }

  public void setWriter(PrintWriter writer) throws IOException {
    _outWriter = writer;
  }

    public String getCharacterEncoding() {
        throw new RuntimeException();
    }

    public void addCookie(Cookie cookie) {
        _cookies.addElement(cookie);
    }

    public boolean containsHeader(String name) {
        return _headers.containsKey(name);
    }

    public void setStatus(int sc, String sm) {
        throw new RuntimeException("setStatus");
    }

    public void setStatus(int sc) {
        _statusCode = sc;
    }

    /**
     * FIXME Should allow multiple headers with a particular
     * name. Currently this does not work. The current
     * implementation uses a hashtable for headers; it should
     * instead be a Vector of (name, value) pairs, so that names
     * need not be unique.
     */
    public void addHeader(String name, String value) {
        setHeader(name, value);
    }

    public void setHeader(String name, String value) {
        _headers.put(name, value);
    }

    /**
     * FIXME Should allow multiple headers with a particular
     * name. Currently this does not work. The current
     * implementation uses a hashtable for headers; it should
     * instead be a Vector of (name, value) pairs, so that names
     * need not be unique.
     */
    public void addIntHeader(String name, int value) {
        addIntHeader(name, value);
    }

    public void setIntHeader(String name, int value) {
        _headers.put(name, new Integer(value));
    }

    /**
     * FIXME Should allow multiple headers with a particular
     * name. Currently this does not work. The current
     * implementation uses a hashtable for headers; it should
     * instead be a Vector of (name, value) pairs, so that names
     * need not be unique.
     */
    public void addDateHeader(String name, long date) {
        setDateHeader(name, date);
    }

    public void setDateHeader(String name, long date) {
        _headers.put(name, new Long(date));
    }

    public void sendError(int sc, String msg) throws IOException {
        _statusCode = sc;
        _errorMessage = msg;
    }

    public void sendError(int sc) throws IOException {
        sendError(sc, "");
    }

    public void sendRedirect(String location) throws IOException {
        setHeader("Location", location);
        setStatus(SC_MOVED_TEMPORARILY);
    }

    public String encodeURL(String url) {
        throw new RuntimeException();
    }

    public String encodeUrl(String url) {
        return encodeURL(url);
    }

    public String encodeRedirectURL(String url) {
        throw new RuntimeException();
    }

    public String encodeRedirectUrl(String url) {
        return encodeRedirectURL(url);
    }

    public int getBufferSize() {
        return 0;
    }

    public void setBufferSize(int size) throws IllegalStateException {
        throw new RuntimeException();
    }

    public void flushBuffer() throws IOException {
        getWriter().flush();
    }

    public boolean isCommitted() {
        return _outStream.isCommitted();
    }

    public void reset() throws IllegalStateException {
        if (isCommitted()) {
            throw new IllegalStateException("Response has been committed");
        }
        _outStream.reset();
        _statusCode = 0;
        _errorMessage = null;
        _cookies = new Vector();
        _headers = new Hashtable();
    }

    public void setLocale(Locale newLocale) {
        _locale = newLocale;
    }

    public Locale getLocale() {
        return _locale;
    }

    public void resetBuffer() { 
        throw new RuntimeException("Not Implemented");
    }

}