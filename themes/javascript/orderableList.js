/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */


dojo.provide("webui.@THEME@.orderableList");

/** 
 * Define webui.@THEME@.orderableList name space. 
 */ 
webui.@THEME@.orderableList = {
    /**
     * This function is used to initialize HTML element properties with the
     * following Object literals.
     *
     * <ul>
     *  <li>id</li>
     *  <li>moveMessage</li>
     * </ul>
     *
     * Note: This is considered a private API, do not use.
     *
     * @param props Key-Value pairs of properties.
     */
    init: function(props) {
        if (props == null || props.id == null) {
            return false;
        }
        var domNode = document.getElementById(props.id);
        if (domNode == null) {
            return false;
        }

        // Set given properties on domNode.
        Object.extend(domNode, props);

        // Not a facet does not have "extra" editable list id.

        // child elements
        domNode.list = document.getElementById(props.id + "_list");

        // Not a facet does not have "extra" editable list id.

        // The select element from which selections are made 
        domNode.list = document.getElementById(props.id + "_list");

        // The options of the select element from which selections are made 
        domNode.options = domNode.list.options;

        // Bug 6338492 -
        //     ALL: If a component supports facets or children is must be a
        //      NamingContainer
        // Since OrderableList has become a NamingContainer the id's for
        // the facet children are prefixed with the OrderableList id
        // in addition to their own id, which also has the 
        // OrderableList id, as has been the convention for facets. This introduces
        // a redundancy in the facet id so the moveUp button now looks like
        //
        // "formid:orderablelistid:orderablelistid:orderablelistid_moveUpButton"
        //
        // It used to be "formid:orderablelistid_moveUpButton"
        // It would be better to encapsulate that knowledge in the
        // OrderableList renderer as does FileChooser which has the
        // same problem but because the select elements are not
        // facets in OrderableList they really do only have id's of the
        // form "formid:orderablelistid_list". Note that 
        // in these examples the "id" parameter is "formid:orderablelistid"
        //
        // Therefore for now, locate the additional prefix here as the
        // "facet" id. Assume that id never ends in ":" and if there is
        // no colon, id is the same as the component id.
        //
        var componentid = props.id;
        var colon_index = componentid.lastIndexOf(':');
        if (colon_index != -1) {
            componentid = props.id.substring(colon_index + 1);
        }
        var facetid = props.id + ":" + componentid;

        domNode.moveUpButton = document.getElementById(facetid + "_moveUpButton");
        domNode.moveDownButton = document.getElementById(facetid + "_moveDownButton");
        domNode.moveTopButton = document.getElementById(facetid + "_moveTopButton");
        domNode.moveBottomButton = document.getElementById(facetid + "_moveBottomButton");

        // Not a facet
        domNode.values = document.getElementById(props.id + "_list_value");

        // The messages
        if(domNode.moveMessage == null) {
            "Select at least one item to remove";
        }

        // attach OrderableList object methods
        domNode.moveUp = webui.@THEME@.orderableList.moveUp;
        domNode.moveDown = webui.@THEME@.orderableList.moveDown;
        domNode.moveTop = webui.@THEME@.orderableList.moveTop;
        domNode.moveBottom = webui.@THEME@.orderableList.moveBottom;
        domNode.updateButtons = webui.@THEME@.orderableList.updateButtons;
        domNode.updateValue = webui.@THEME@.orderableList.updateValue;
        domNode.onChange = webui.@THEME@.orderableList.updateButtons;
    },

    // The original allowed items to be moved on both lists. Surely we
    // only sort items on the selected list? 
    // This does not work on Mozilla
    moveUp: function() {
        var numOptions = this.options.length;
    
        // If there aren't at least two more selected items, then there is
        // nothing to move 
        if(numOptions < 2) {
            return;
        }

        // Start by examining the first item 
        var index = 0;

        // We're not going to move the first item. Instead, we will start
        // on the first selected item that is below an unselected
        // item. We identify the first unselected item on the list, and 
        // then we will start on next item after that
        while(this.options[index].selected) {
            ++index;
            if(index == numOptions) {
                // We've reached the last item - no more items below it so
                // we return
                return;
            }
        }

        // Start on the item below this one 
        ++index;

        for(index;index < numOptions;++index) {
            if(this.options[index].selected == true) {
                var curOption = this.options[index];
                if(this.options.remove == null) {
                    // For Mozilla
                    this.options[index] = null;
                    this.list.add(curOption, this.options[index - 1]);
                } else {
                    // Windows and Opera do
                    this.options.remove(index);
                    this.options.add(curOption, index - 1);
                }
                // This is needed for Opera only
                this.options[index].selected = false;
                this.options[index - 1].selected = true;
            }
        }
        this.updateValue();
        this.updateButtons();
    },

    // The original allowed items to be moved on both lists. Surely we
    // only sort items on the selected list? 
    // This does not work on Mozilla
    moveTop: function() {
        var numOptions = this.options.length;
        // If there aren't at least two items, there is nothing to move  
        if(numOptions < 2) {
            return;
        }

        // Find the first open spot 
        var openSpot = 0;
        while (this.options[openSpot].selected) {
            openSpot++;
        }

        // Find the first selected item below it
        var index = openSpot+1;

        for(index;index < numOptions;++index) {
            if(this.options[index].selected == true) {
                var curOption = this.options[index];
                if(this.options.remove == null) {
                    // For Mozilla
                    this.options[index] = null;
                    this.list.add(curOption, this.options[openSpot]);
                } else {
                    // Windows and Opera do
                    this.options.remove(index);
                    this.options.add(curOption, openSpot);
                }

                // This is needed for Opera only
                this.options[index].selected = false;
                this.options[openSpot].selected = true;
                openSpot++;
            }
        }
        this.updateValue();
        this.updateButtons();
    },

    // The original allowed items to be moved on both lists. Surely we
    // only sort items on the selected list? 
    // This does not work on Mozilla
    moveDown: function() {
        // Get the last item
        var index = this.options.length - 1;
    
        // If this number is less than zero, there was nothing on the list
        // and we return
        if(index < 0) {
            return;
        }

        for (var i = index - 1;i >= 0;i--) {
            if (this.options[i].selected) {          
                var next = i + 1;
                if (this.options[next].selected == false) {
                    tmpText = this.options[i].text;
                    tmpValue = this.options[i].value;
                    this.options[i].text = this.options[next].text;
                    this.options[i].value = this.options[next].value;
                    this.options[i].selected = false;
                    this.options[next].text = tmpText;
                    this.options[next].value = tmpValue;
                    this.options[next].selected = true;
                }
            }
        }

        this.updateValue();
        this.updateButtons();
    },

    moveBottom: function() {
        var numOptions = this.options.length - 1;

        // If there aren't at least two items, there is nothing to move  
        if(numOptions < 1) {
            return;
        }

        // Find the last open spot 
        var openSpot = numOptions;
        while (this.options[openSpot].selected) {
            openSpot--;
        }

        // Find the first selected item above it
        var index = openSpot-1;

        for(index;index > -1;--index) {
            if(this.options[index].selected == true) {
                var curOption = this.options[index];
	        if(this.options.remove == null) {
                    // For Mozilla
                    this.options[index] = null;
                    this.list.add(curOption, this.options[openSpot+1]);
                } else {
                    // Windows and Opera do
                    this.options.remove(index);
                    this.options.add(curOption, openSpot);
                }

                // This is needed for Opera only
                this.options[index].selected = false;
                this.options[openSpot].selected = true;
                openSpot--;
            }
        }
        this.updateValue();
        this.updateButtons();
    },

    updateButtons: function() {
        var numOptions = this.options.length;
        var selectedIndex = this.options.selectedIndex;
        var disabled = true;
        var index;

        // First, check if move down and move to bottom should be
        // enabled. These buttons should be enabled if and only if at
        // least one of the items are selected and there is at least one
        // open spot below a selected item. 
        if(selectedIndex > -1 && selectedIndex < numOptions -1) {
            index = selectedIndex+1;
            while(index < numOptions) {
                if(this.options[index].selected == false) {
                    disabled = false;
                    break;
                }
                index++;
            }
        }

        if(this.moveDownButton != null) {
            if(this.moveDownButton.setDisabled != null) {
                this.moveDownButton.setDisabled(disabled);
            } else {
                this.moveDownButton.disabled = disabled;
            }
        }

        if(this.moveBottomButton != null) {
            if(this.moveBottomButton.setDisabled != null) {
                this.moveBottomButton.setDisabled(disabled);
            } else {
                this.moveBottomButton.disabled = disabled;
            }
        }

        // First, check if move up and move to top should be
        // enabled. These buttons should be enabled if and only if at
        // least one of the items is selected and there is at least one
        // open spot above a selected item. 
        disabled = true;

        if(selectedIndex > -1) {
            index = numOptions - 1;
            while(index > 0) {
                if(this.options[index].selected) {
                    break;
                }
                index--;
            }
            index--;
            while(index > -1) {
                if(this.options[index].selected == false) {
                    disabled = false;
                    break;
                }
                index--;
            }
        }

        if(this.moveUpButton != null) {
            if(this.moveUpButton.setDisabled != null) {
                this.moveUpButton.setDisabled(disabled);
            } else {
                this.moveUpButton.disabled = disabled;
            }
        }

        if(this.moveTopButton != null) {
            if(this.moveTopButton.setDisabled != null) {
                this.moveTopButton.setDisabled(disabled);
            } else {
                this.moveTopButton.disabled = disabled;
            }
        }
    },

    updateValue: function() {
        // Remove the options from the select that holds the actual
        // selected values
        while(this.values.length > 0) {
            this.values.remove(0);
        }

        // Create a new array consisting of the options marked as selected
        // on the official list
        var newOptions = new Array();
        var cntr = 0;
        var newOption;

        while(cntr < this.options.length) {
            newOption = document.createElement("option");
            if(this.options[cntr].text != null) {
                newOption.text = this.options[cntr].text;
            }
            if(this.options[cntr].value != null) {
                newOption.value = this.options[cntr].value;
            }
            newOption.selected = true;
            newOptions[newOptions.length] = newOption;
            ++ cntr;
        }
        cntr = 0;
        if(this.options.remove == null) {
            // For Mozilla
            while(cntr < newOptions.length) {
                this.values.add(newOptions[cntr], null);
                ++cntr;
            }
        } else {
            // Windows and Opera do
            while(cntr < newOptions.length) {
                this.values.add(newOptions[cntr], cntr);
                ++cntr;
            }
        }
        return true;
    }
}

//-->
