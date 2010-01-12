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

import java.util.*;

import javax.servlet.*;

public class TestServletConfig implements ServletConfig {
    private String _servletName;
    private Properties _servletProps;
    private ServletContext _context;

    public TestServletConfig(String servletName, Properties props,
                             ServletContext context) {
        _servletName = servletName;
        _servletProps = props;
        _context = context;
    }

    public ServletContext getServletContext() {
        return _context;
    }


    public String getInitParameter(String name) {
        return _servletProps.getProperty(name);
    }

    public Enumeration getInitParameterNames() {
        return _servletProps.propertyNames();
    }

    public String getServletName() {
        return _servletName;
    }
}
