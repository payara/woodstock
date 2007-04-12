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

import com.sun.webui.jsf.component.Button;
import com.sun.webui.jsf.component.Icon;
import com.sun.webui.jsf.component.ImageComponent;
import com.sun.webui.jsf.component.StaticText;
import com.sun.webui.jsf.util.WidgetUtilities;
import com.sun.webui.theme.Theme;
import com.sun.webui.jsf.theme.ThemeStyles;
import com.sun.webui.jsf.theme.ThemeTemplates;
import com.sun.webui.jsf.util.ConversionUtilities;
import com.sun.webui.jsf.util.JavaScriptUtilities;
import com.sun.webui.jsf.util.RenderingUtilities;
import com.sun.webui.jsf.util.ThemeUtilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class renders Table components.
 */
@Renderer(@Renderer.Renders(
    rendererType="com.sun.webui.jsf.widget.Button", 
    componentFamily="com.sun.webui.jsf.Button"))
public class ButtonRenderer extends RendererBase {
    /**
     * The set of pass-through attributes to be rendered.
     */
    private static final String attributes[] = {
        "accessKey",
        "alt", // not supported by button
        "align",
        "dir",
        "lang",
        "onBlur",
        "onClick",
        "onDblClick",
        "onFocus",
        "onKeyDown",
        "onKeyPress",
        "onKeyUp",
        "onMouseDown",
        "onMouseOut",
        "onMouseOver",
        "onMouseUp",
        "onMouseMove",
        "style",
        "tabIndex"
    };

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Renderer Methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Determine if this was the component that submitted the form.
     *
     * @param context <code>FacesContext</code> for the current request
     * @param component <code>UIComponent</code> to be decoded
     *
     * @exception NullPointerException if <code>context</code> or
     *  <code>component</code> is <code>null</code>
     */
    public void decode(FacesContext context, UIComponent component) {
        if (context == null || component == null) {
            throw new NullPointerException();
        }
        
        Button button = (Button) component;

        // Do not process disabled or reset components.
        if (button.isReset()) {
            return;
        }

        // Was our command the one that caused this submission?
        String clientId = button.getClientId(context);
        Map map = context.getExternalContext().getRequestParameterMap();
        
        if (map.containsKey(clientId) || (map.containsKey(clientId + ".x") 
                && map.containsKey(clientId + ".y"))) {
            button.queueEvent(new ActionEvent(button));
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // RendererBase methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Get the Dojo modules required to instantiate the widget.
     *
     * @param context FacesContext for the current request.
     * @param component UIComponent to be rendered.
     */
    protected JSONArray getModules(FacesContext context, UIComponent component)
            throws JSONException {
        Button button = (Button) component;

        JSONArray json = new JSONArray();
        json.put(JavaScriptUtilities.getModuleName("widget.button"));

        if (button.isAjaxify()) {
            json.put(JavaScriptUtilities.getModuleName(
                "widget.jsfx.button"));
        }
        return json;
    }

    /** 
     * Helper method to obtain component properties.
     *
     * @param context FacesContext for the current request.
     * @param component UIComponent to be rendered.
     */
    protected JSONObject getProperties(FacesContext context,
            UIComponent component) throws JSONException, IOException {
        Button button = (Button) component;
        String templatePath = button.getHtmlTemplate(); // Get HTML template.

        JSONObject json = new JSONObject();
        json.put("className", button.getStyleClass())
            .put("disabled", button.isDisabled())
            .put("icon", (button.getImageURL() != null 
                || button.getIcon() != null))
            .put("mini", button.isMini())
            .put("name", button.getClientId(context))
            .put("primary", button.isPrimary())
            .put("templatePath", (templatePath != null)
                ? templatePath 
                : getTheme().getPathToTemplate(ThemeTemplates.BUTTON))
            .put("title", button.getToolTip())
            .put("type", button.isReset() ? "reset" : "submit")
            .put("visible", button.isVisible());

        // Append contents property.
        WidgetUtilities.addProperties(json, "contents",
            WidgetUtilities.renderComponent(context, getContents(context, button)));

        // Add core and attribute properties.
        addAttributeProperties(attributes, component, json);
        setCoreProperties(context, component, json);

        return json;
    }

    /**
     * Get the type of widget represented by this component.
     *
     * @return The type of widget represented by this component.
     */
    public String getWidgetType() {
        return JavaScriptUtilities.getNamespace("button");
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Private renderer methods
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * Get the image associated with this component, if any.
     * 
     * Note: The imageURL property takes precedence over the icon property.
     */
    private UIComponent getContents(FacesContext context, Button component) {
        String id = (component.getId() != null)
            ? component.getId().concat(Button.CONTENTS_ID) : null;

        // Get image.
        String imageURL = component.getImageURL();
        if (imageURL != null) {
            ImageComponent image = new ImageComponent();

            // Set properties.
            image.setAlt(component.getAlt());
            image.setBorder(0);
            image.setId(id);
            image.setParent(component);
            image.setUrl(imageURL);
            return image;
        }

        // Get icon.
        String iconKey = component.getIcon();
        if (iconKey != null) {
            Icon icon = ThemeUtilities.getIcon(ThemeUtilities.getTheme(context),
                iconKey);

            // Set properties.
            icon.setId(id);
            icon.setBorder(0);
            icon.setParent(component);
            return icon;
        }

        // Get the textual label of the button.
        String text = ConversionUtilities.convertValueToString(component,
            component.getText());

        // Pad the text per UI guidelines.
        if (text != null && text.trim().length() > 0) {
            if (!component.isNoTextPadding()) { 
                if (text.trim().length() <= 3) {
                    text = "  " + text + "  "; //NOI18N
                } else if (text.trim().length() == 4) {
                    text = " " + text + " "; //NOI18N
                }
            }
        }

        // Set properties.
        StaticText staticText = new StaticText();
        staticText.setEscape(component.isEscape());
        staticText.setId(id);
        staticText.setParent(component);
        staticText.setText(text);
        
        return staticText;
    }

    // Helper method to get Theme objects.
    private Theme getTheme() {
        return ThemeUtilities.getTheme(FacesContext.getCurrentInstance());
    }
}
