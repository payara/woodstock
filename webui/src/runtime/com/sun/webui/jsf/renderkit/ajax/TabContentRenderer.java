/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License).  You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * https://woodstock.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at https://woodstock.dev.java.net/public/CDDLv1.0.html.
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * you own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Copyright 2007 Sun Microsystems, Inc. All rights reserved.
 */
package com.sun.webui.jsf.renderkit.ajax;

import com.sun.faces.annotation.Renderer;
import com.sun.webui.jsf.util.ComponentUtilities;

import java.io.IOException;


import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class responds to asynchronous requests made to Tab components. 
 * These requests could be to:
 * a) Refresh the tab in which case the tab goes thru its JSF lifecycle and
 *    and re renders itself.
 * b) Perform a certain action on the tab or show a different view of the tab
 *    based on the action selected from the tab's menu.
 * c) Load the contents of the tab when the tab is selected by the user on
 *    the client side. In this case the tab simply gets rendered.
 */
@Renderer(@Renderer.Renders(
    rendererType="com.sun.webui.jsf.ajax.TabContent",
    componentFamily="com.sun.webui.jsf.TabContent"))
public class TabContentRenderer
        extends com.sun.webui.jsf.renderkit.widget.TabContentRenderer {
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Renderer methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Decode the Tab component
     *
     * @param context The FacesContext associated with this request
     * @param component The Tab component to decode
     */
    public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null) {
            throw new NullPointerException();
        } 
    }

    /**
     * Render the beginning of the specified UIComponent to the output stream or
     * writer associated with the response we are creating.
     *
     * @param context FacesContext for the current request.
     * @param component UIComponent to be rendered.
     *
     * @exception IOException if an input/output error occurs.
     * @exception NullPointerException if context or component is null.
     */
    public void encodeBegin(FacesContext context, UIComponent component)
            throws IOException {
        // Do nothing...
    }
    
    /**
     * This method asynchronously renders the children of the tab component
     * in one of the following casesL
     * a) Refresh the tab in which case the tab goes thru its JSF lifecycle and
     *    and re renders itself.
     * b) Perform a certain action on the tab or show a different view of the tab
     *    based on the action selected from the tab's menu.
     * c) Load the contents of the tab when the tab is selected by the user on
     *    the client side. In this case the tab simply gets rendered.
     *
     * @param context FacesContext for the current request.
     * @param component UIComponent to be rendered.
     *
     * @exception IOException if an input/output error occurs.
     * @exception NullPointerException if context or component is null.
     */
    public void encodeChildren(FacesContext context, UIComponent component)
            throws IOException {
        if (context == null || component == null) {
            throw new NullPointerException();
        }

        // Output component properties if Ajax request and is refresh event.
        // For now handle loadContent and tabAction by refreshing re rendering
        // the tab. Events and/or Actionlisteners will be generated that should
        // prompt the application developer to supply the appropriate data.
        
        if (ComponentUtilities.isAjaxRequest(context, component, "refresh")) {
            super.encodeChildren(context, component);
            
        } else if (ComponentUtilities.isAjaxRequest(context, component, "loadContent")) {
            super.encodeChildren(context, component);
            
        } else if (ComponentUtilities.isAjaxRequest(context, component, "tabAction")) {
            super.encodeChildren(context, component);
        }
    }

    /**
     * Render the ending of the specified UIComponent to the output stream or
     * writer associated with the response we are creating.
     *
     * @param context FacesContext for the current request.
     * @param component UIComponent to be rendered.
     *
     * @exception IOException if an input/output error occurs.
     * @exception NullPointerException if context or component is null.
     */
    public void encodeEnd(FacesContext context, UIComponent component)
            throws IOException {
        // Do nothing...
    }
}
