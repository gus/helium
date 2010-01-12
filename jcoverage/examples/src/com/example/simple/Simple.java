/**
 * www.jcoverage.com
 * Copyright (C)2003 jcoverage ltd.
 *
 * This file is part of jcoverage.
 *
 * jcoverage is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * jcoverage is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jcoverage; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 */
package com.example.simple;

import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;


public class Simple {
  static final Logger logger=Logger.getLogger(Simple.class);

  public int square(int x) {
    if(logger.isDebugEnabled()) {
      logger.debug("x: "+x);
    }

    int result=x*x;

    if(logger.isDebugEnabled()) {
      logger.debug("result: "+result);
    }

    return result;
  }

  public int f(int x) {
    if(logger.isDebugEnabled()) {
      logger.debug("x: "+x);
    }

    if(x<0) {
      if(logger.isDebugEnabled()) {
        logger.debug("negative x");
      }

      return square(x);
    } else if((x>=0)&&(x<=5)) {
      if(logger.isDebugEnabled()) {
        logger.debug("0<=x<=5");
      }

      return x+3;
    } else {
      return 2*x;
    }
  }

  public int sum(Collection c) {
    int result=0;

    for(Iterator i=c.iterator();i.hasNext();) {
      int value=((Number)i.next()).intValue();

      if(logger.isDebugEnabled()) {
        logger.debug("value: "+value);
      }

      result+=value;
    }

    if(logger.isDebugEnabled()) {
      logger.debug("result: "+result);
    }

    return result;
  }
}
