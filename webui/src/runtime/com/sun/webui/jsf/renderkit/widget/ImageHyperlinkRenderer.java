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

package com.sun.webui.jsf.renderkit.widget;
import com.sun.faces.annotation.Renderer;

import java.util.Iterator;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.component.UIParameter;

import com.sun.webui.jsf.component.ImageComponent;
import com.sun.webui.jsf.component.ImageHyperlink;
import com.sun.webui.jsf.component.Link;

import com.sun.webui.jsf.theme.ThemeTemplates;
import com.sun.webui.theme.Theme;
import com.sun.webui.jsf.theme.ThemeJavascript;

import com.sun.webui.jsf.util.ConversionUtilities;
import com.sun.webui.jsf.util.WidgetUtilities;
import com.sun.webui.jsf.util.JavaScriptUtilities;
import com.sun.webui.jsf.util.ThemeUtilities;
/**
 * This class renders an instance of the ImageHyperlink component.
 * This renderer specializes on the functionality offered by the 
 * Hyperlink renderer. The ImageHyperlink renderer defines an "enabledImage"
 * JSON property which contains the image that is to be represented for the
 * image hyperlink. If a "disabledImage" facet is specified, then that image
 * is output as a part of the "disabledImage" JSON property.
 * Text and other children of the imageHyperlink are put into the "contents"
 * JSON array. The imagePosition JSON attribute sepcifies where the enabled/
 * disabled images of the imageHyperlink should be placed with respect to the
 * text and the image present in the imageHyperlink. This corresponds to the
 * "textPosition" attribute found for the imageHyperlink component.
 */
@Renderer(@Renderer.Renders(
    rendererType="com.sun.webui.jsf.widget.ImageHyperlink", 
    componentFamily="com.sun.webui.jsf.ImageHyperlink"))
public class ImageHyperlinkRenderer extends HyperlinkRenderer{

    // Used in positioning of the text.
   private static final String LABEL_LEFT="left"; //NOI8N
   // Disabled image facet
   private static final String DISABLED_IMAGE="disabledImage";//NOI18N
    /**
     * Get the Dojo modules required to instantiate the widget.
     * If the ajaxify attribute of the component is set to true, then the 
     * module necessary for the ajax feature of the imageHyperlink component is also
     * added as a part of the returned JSONArray object.
     * @param context FacesContext for the current request.
     * @param component UIComponent to be rendered.
     */
    protected JSONArray getModules(FacesContext context, UIComponent component)
            throws JSONException {
        JSONArray json = new JSONArray();
        String url = (String)component.getAttributes().get("url");  
        boolean ajaxify = ((Boolean)
            component.getAttributes().get("ajaxify")).booleanValue();        

        json.put(JavaScriptUtilities.getModuleName("widget.imageHyperlink"));   
        if (ajaxify == true) {
            json.put(JavaScriptUtilities.getModuleName(
                "widget.jsfx.imageHyperlink"));
        }            
        return json;
    }      
    
    /**
     * Set the html template to be used by the widget renderer. Override 
     * this method if different templates are to be used.
     * @param context FacesContext for the current request.
     * @param component UIComponent to be rendered.
     * @param json The JSON object
     */
    protected void setHTMLTemplate (FacesContext context, UIComponent component,
            JSONObject json) throws JSONException, IOException {
        Theme theme = ThemeUtilities.getTheme(context);        
        String templatePath = (String) component.getAttributes().get("templatePath");
        json.put("templatePath",
            (templatePath != null && templatePath.length() > 0)
                ? templatePath 
                : theme.getPathToTemplate(ThemeTemplates.IMAGEHYPERLINK));        
    }
    
    /**
     * Get the type of widget represented by this component.
     * This method returns "imageHyperlink" as the widget type.
     * @param context FacesContext for the current request.
     * @param component UIComponent to be rendered.
     */
    protected String getWidgetType(FacesContext context, UIComponent component) {
            return JavaScriptUtilities.getNamespace("imageHyperlink");            
    }
    
    /**
     * The imageHyperlink renderer adds the text attribute as a part of the contents
     * JSON array. The image that is provided as a part of the component's imageUrl
     * attribute or the icon attribute is included in the "enabledImage" JSON array.
     * This image will be shown when the imageHyperlink is in the enabled state.
     * If a "disabledImage" facet exists, then that facet will be included as a 
     * part of the "disabledImage" JSON array. This image will be shown when the
     * imageHyperlink is in the disabled state.
     *
     * @param json The json object
     * @param component The ImageHyperlink component
     * @param context The FacesContext
     */
    protected void setContents(FacesContext context, UIComponent component, 
            JSONObject json)throws JSONException, IOException{
	if (!(component instanceof ImageHyperlink)) {
	    throw new IllegalArgumentException(
                "ImageHyperlinkRenderer can only render ImageHyperlink components.");
        }      
        ImageHyperlink ilink = (ImageHyperlink) component;    
        Object text = ilink.getText();
        // Store any children that are added as children to the imageHyperlink
        JSONArray children = new JSONArray();
        json.put("contents", children);
        String label = (text == null) ? null : 
              ConversionUtilities.convertValueToString(component, text);
        String textPosition = ilink.getTextPosition();          
        if (textPosition.equals(LABEL_LEFT)) {
            json.put("imagePosition", "right");
        } else {
            json.put("imagePosition", "left");  
        }
        ImageComponent ic = ilink.getImageFacet(); 
                            
        if (label != null) {
            WidgetUtilities.addProperties(children, label);
            WidgetUtilities.addProperties(children, "&nbsp;");            
        }
          
        if (ic != null) {
            WidgetUtilities.addProperties(json, "enabledImage",            
                WidgetUtilities.renderComponent(context, ic));
        }
          
        UIComponent disabledImage = component.getFacets().get(DISABLED_IMAGE);
        if (disabledImage != null) {
            WidgetUtilities.addProperties(json, "disabledImage",            
                WidgetUtilities.renderComponent(context, disabledImage));
        }

        UIComponent child;    
        Iterator it = component.getChildren().iterator();        
        while (it.hasNext()) {
            child = (UIComponent)it.next();
            if (!(child instanceof UIParameter)) {
                WidgetUtilities.addProperties(children,
                    WidgetUtilities.renderComponent(context, child));                            
            }        
        }         
    }   
}
