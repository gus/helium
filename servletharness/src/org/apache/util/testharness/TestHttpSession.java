package org.apache.util.testharness;
//package com.mypoints.test;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

/**
 * @version $Id: TestHttpSession.java,v 1.1 2004/05/29 02:09:52 jaknowlden Exp $
 */
public class TestHttpSession implements HttpSession {
    private Map attributes;
    private int maxInactiveInterval;

    public TestHttpSession() {
        attributes = new HashMap();
    }

    public long getCreationTime() {
        throw new RuntimeException("getCreationTime");
    }
    public String getId() {
        return toString();
    }
    public long getLastAccessedTime() {
        throw new RuntimeException("getLastAccessedTime");
    }
    public void setMaxInactiveInterval(int interval) {
        maxInactiveInterval = interval;
    }
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }
    public HttpSessionContext getSessionContext() {
        throw new RuntimeException("getSessionContext");
    }
    public Object getAttribute(String name) {
        return attributes.get(name);
    }
    public Object getValue(String name) {
        throw new RuntimeException("getValue");
    }
    public Enumeration getAttributeNames() {
        throw new RuntimeException("getAttributeNames");
    }
    public String[] getValueNames() {
        throw new RuntimeException("getValueNames");
    }
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }
    public void putValue(String name, Object value) {
        throw new RuntimeException("putValue");
    }
    public void removeAttribute(String name) {
        attributes.remove(name);
    }
    public void removeValue(String name) {
        throw new RuntimeException("removeValue");
    }
    public void invalidate() {
        throw new RuntimeException("invalidate");
    }
    public boolean isNew() {
        throw new RuntimeException("isNew");
    }
    public ServletContext getServletContext() {
        throw new RuntimeException("getServletContext");
    }

}
