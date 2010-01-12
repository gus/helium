/**
 * @author justin
 * @version "$Id: TestRequestDispatcher.java,v 1.1 2004/05/29 02:09:52 jaknowlden Exp $"
 */
package org.apache.util.testharness;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class TestRequestDispatcher implements RequestDispatcher {

  private Servlet servlet;

  public TestRequestDispatcher(Servlet servlet) {
    this.servlet = servlet;
  }

  public void forward(ServletRequest request, ServletResponse response)
  throws ServletException, IOException {
    servlet.service(request, response);
  }

  public void include(ServletRequest arg0, ServletResponse arg1)
  throws ServletException, IOException {
    throw new UnsupportedOperationException("Not Implemented");
  }

}
