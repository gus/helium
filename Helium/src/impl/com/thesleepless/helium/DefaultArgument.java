/*
 * Created: Jul 17, 2004
 * File version: $Id: DefaultArgument.java,v 1.1 2005/05/03 23:34:39 jaknowlden Exp $
 * 
 * Helium, Dynamic content replacement
 * Copyright (C) 2004  The Sleepless, Inc.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.thesleepless.helium;

import org.w3c.dom.Node;

/**
 * 
 * @author Justin Knowlden
 * @version "$Revision: 1.1 $"
 */
class DefaultArgument implements Argument {

  private Node argumentNode;

  public DefaultArgument(Node argumentNode) {
    this.argumentNode = argumentNode;
  }

  public String getName() {
    return argumentNode.getNodeName();
  }

  public String getValue() {
    return argumentNode.getNodeValue();
  }

}
