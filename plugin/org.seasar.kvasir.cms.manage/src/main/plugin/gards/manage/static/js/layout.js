Event.observe(window, 'load', prepareForAdminConsole);
Event.observe(window, 'load', prepareForDragAndDrop);
Event.observe(window, 'load', prepareForPops);
Event.observe(window, 'load', prepareForGlobalHandlers);
Event.observe(window, 'load', setListeners);
Event.observe(window, 'load', prepareForNonExistentImages);

function prepareForNonExistentImages() {
    addLinkToNonExistentImages();
}

function addLinkToNonExistentImages(parentElement) {
    var nones = $A(document.getElementsByClassName('pageNotFound', parentElement));
    nones.each(function (none) {
        if (none.tagName == 'IMG') {
            var a = document.createElement('a');
            a.href = none.src;
            a.addClassName('pageNotFound');
            a.innerHTML = '??' + (none.title || none.alt || none.src) + '??';
            none.parentNode.insertBefore(a, none);
        }
    });
}

function getLocation() {
    var loc = location.href;
    var question = loc.indexOf('?');
    if (question < 0) {
        return loc;
    } else {
        return loc.substring(0, question);
    }
}

function prepareForPops() {
    var pops = $A(document.getElementsByClassName('pop'));
    pops.each(function (pop) {
        prepareForPop(pop);
    });
}

function prepareForPop(pop) {
    if (pop.id) {
        new Draggable(pop.id, {
            revert: true,
            onStart: function () {
                // Position.absolutize(this);
                absolutize(this);
                this.addClassName('drag-hovered');
                if (_dock != null) {
                    _dock.freeze(true);
                    Element.hide('currentPopDetail');
                }
            }.bindAsEventListener(pop),
            onEnd: function () {
                this.removeClassName('drag-hovered');
                if (_dock != null) {
                    _dock.freeze(false);
                }
            }.bindAsEventListener(pop),
            endeffect: function(element) {
                Draggable._dragging[element] = false;
            }
        });
        if (!Element.hasClassName(pop, 'immutable')) {
            new InPlacePropertyEditor(pop,
                {
                    loadTextURL: '<span tal:replace="page:%/layout.editProperties.do"></span>'
                });
        }
        Event.observe(pop, 'mouseover', popMouseoverListener.bindAsEventListener(pop));
        Event.observe(pop, 'mouseout', popMouseoutListener.bindAsEventListener(pop));
    }
}

/* work-around function of Position#absolutize() in prototype.js 1.5RC1 */
function absolutize(element) {
  element = $(element);
  if (element.style.position == 'absolute') return;
  Position.prepare();

  var offsets = Position.positionedOffset(element);
  var top     = offsets[1];
  var left    = offsets[0];
  // (prototype.js��) var width   = element.clientWidth;
  // (prototype.js��) var height  = element.clientHeight;
  var width   = element.offsetWidth;
  var height  = element.offsetHeight;

  element._originalLeft   = left - parseFloat(element.style.left  || 0);
  element._originalTop    = top  - parseFloat(element.style.top || 0);
  element._originalWidth  = element.style.width;
  element._originalHeight = element.style.height;

  element.style.position = 'absolute';
  element.style.top    = top + 'px';;
  element.style.left   = left + 'px';;
  element.style.width  = width + 'px';;
  element.style.height = height + 'px';;
}

function popMouseoverListener() {
    Element.setStyle(this, {cursor: 'move'});
}

function popMouseoutListener() {
    Element.setStyle(this, {cursor: 'auto'});
}

function prepareForGlobalHandlers() {
    var myGlobalHandlers = {
        onLoading: function () {
            Element.show('admin-indicator');
        },
        onComplete: function () {
            if (Ajax.activeRequestCount <= 0) {
                setTimeout(function () {
                    Element.hide('admin-indicator');
                }, 100);
            }
        }
    };
    Ajax.Responders.register(myGlobalHandlers);
}

var _dock = null;

function prepareForAdminConsole() {
    var body = document.getElementsByTagName('body')[0];
    var element = document.createElement('div');
    element.id = 'admin-console';
    body.appendChild(element);

    var myAjax = new Ajax.Updater(
        'admin-console',
        '<span tal:replace="page:%/layout.console.do"></span>',
        {
            asynchronous: false,
            method: 'get'
        }
    );

    Element.hide('currentPopDetail');
    updatePopEditMode();

    prepareForButton($('viewOptionalConsole'), function () {
        var litbox = new LITBox('<span tal:replace="page:%/optional-console.do"></span>'
            + '?start=' + encodeURIComponent(getLocation()), {
            type: 'window',
            width: 800,
            height: 450,
            draggable: true,
            resizable: true
        });
        litbox.d4.innerHTML.evalScripts();
        new Tabified($('optionalConsole_tabified'));
        var element_addChildPage_name = $('optionalConsole.addChildPage.name');
        if (element_addChildPage_name) {
            new PageSelector(element_addChildPage_name,
                '<span tal:replace="page:%/layout.loadPageCandidates.do"></span>'
            );
        }
    });

    updateNewPops();

    Droppables.add('popTrash', {
        accept: 'pop',
        onHover: function (pop) {
            deleteDestinationBox();
            enableHighlight($('popTrash'));
        },
        onDrop: function (pop) {
            deleteDestinationBox();
            disableHighlight();
            if (!Element.hasClassName(pop, 'popIcon')) {
                var popTrash = $('popTrash');
                pop.parentNode.removeChild(pop);
                ManageLayout.Pane.save(pop, true);
            }
        }
    });
}

var _popEditModeInPlace = false;

function updatePopEditMode(popEditModeInPlace) {
    var method;
    var parameters;
    if (popEditModeInPlace == undefined) {
        method = 'get';
        parameters = null;
    } else {
        method = 'post';
        parameters = 'popEditModeInPlace=' + popEditModeInPlace;
    }
    var myAjax = new Ajax.Updater(
        $('popEditModeArea'),
        '<span tal:replace="page:%/layout.popEditMode.do"></span>',
        {
            method: method,
            parameters: parameters,
            onComplete: function () {
                var button = $('popEditMode');
                _popEditInPlace = (button.name == 'inPlace');
                prepareForButton(button, function () {
                    togglePopEditMode();
                });
            }
        });
}

function togglePopEditMode() {
    if (_popEditModeInPlace) {
        _popEditModeInPlace = false;
    } else {
        _popEditModeInPlace = true;
    }
    updatePopEditMode(_popEditModeInPlace);
}

var _newPopsOffset = -1;

function updateNewPops() {
    $('currentPopDetail').innerHTML = "";
    var newPops = $('newPops');
    var myAjax = new Ajax.Updater(
        newPops,
        '<span tal:replace="page:%/layout.newPops.do"></span>',
        {
            method: 'get',
            parameters: 'offset=' + _newPopsOffset + '&start=' + encodeURIComponent(getLocation()),
            onComplete: function () {
                if (_dock != null) {
                    _dock.destroy();
                }
                if (_newPopsOffset < 0) {
                    prepareForButton($('showNewPops'), function () {
                        _newPopsOffset = 0;
                        updateNewPops();
                    });
                    Element.hide('currentPopDetail');
                } else {
                    _dock = new Dock(newPops, 'popIcon', {
                        iconWidth: 200,
                        iconHeight: 100,
                        onMouseover: function (e) {
                            var icon = Event.element(e);
                            var detail = $('currentPopDetail');
                            var innerHTML = icon.nextSibling.innerHTML;
                            if (innerHTML != '') {
                                Element.show('currentPopDetail');
                                detail.innerHTML = innerHTML;
                                var pos = Position.cumulativeOffset($('adminConsole'));
                                Element.setStyle(detail, {
                                    top: (pos[1] - detail.offsetHeight) + 'px'
                                });
                            }
                        },
                        onMouseout: function (e) {
                            $('currentPopDetail').innerHTML = "";
                            Element.hide('currentPopDetail');
                        }
                    });
                    var pops = $A(document.getElementsByClassName('pop', newPops));
                    pops.each(function (pop) {
                        prepareForPop(pop);
                    });
                    prepareForButton($('goBackward'), function () {
                        _newPopsOffset--;
                        updateNewPops();
                    });
                    prepareForButton($('goForward'), function () {
                        _newPopsOffset++;
                        updateNewPops();
                    });
                }
            }
        }
    );
}

function prepareForButton(button, clickListener) {
    if (button) {
        Event.observe(button, 'click', clickListener);
        if (button.nodeName != 'INPUT') {
            Event.observe(button, 'mouseover', function () {
                Element.setStyle(this, {cursor: 'pointer'});
            }.bindAsEventListener(button));
            Event.observe(button, 'mouseout', function () {
                Element.setStyle(this, {cursor: 'auto'});
            }.bindAsEventListener(button));
        }
    }
}

function setListeners() {
/*    var panes = $A(document.getElementsByClassName('pane'));
    panes.each(function(pane) {
        Event.observe(pane, 'click', doSelectNewPop, false);
    });*/
}

var ManageLayout = {};

ManageLayout.Pane = {
    save: function (pop, remove) {
        var insertion;
        if (remove) {
            insertion = ManageLayout.Pane.insertionAfterRemove;
        } else {
            insertion = ManageLayout.Pane.insertionAfterDrop;
        }
        var myAjax = new Ajax.Updater(
            {
                success: pop,
                failure: null
            },
            '<span tal:replace="page:%/layout.update.do"></span>',
            {
                asynchronous: true,
                method: 'post',
                postBody: ManageLayout.Pane.constructUpdatePostBody(pop, remove),
                insertion: insertion
            }
        );
    },
    insertionAfterRemove: function (receiver, response) {
        if (_dock != null) {
            _dock.update();
        }
    },
    insertionAfterDrop: function (receiver, response) {
        if (response && response.length > 0) {
            Element.hide(receiver);
            var div = document.createElement('div');
            div.innerHTML = response;
            var parent = receiver.parentNode;
            var pop = div.firstChild
            parent.insertBefore(pop, receiver);
            prepareForPop(pop);
            parent.removeChild(receiver);
            addLinkToNonExistentImages(pop);
        }

        updateNewPops();
    },
    constructUpdatePostBody: function (pop, remove) {
        var panes = $A(document.getElementsByClassName('pane'));
        var postBody = '';
        var delim = '';
        panes.each(function (pane) {
            if (pane.id) {
                postBody += delim + encodeURIComponent(pane.id)
                    + '=';
                delim = '&';

                var pops = $A(pane.childNodes);
                var body = '';
                var dlm = '';
                pops.each(function (pop) {
                    if (pop.id) {
                        body += dlm + pop.id;
                        dlm = ',';
                    }
                });

                postBody += encodeURIComponent(body);
            }
        });
        if (remove) {
            postBody += delim + 'remove=true';
            delim = '&';
        }
        postBody += delim + 'popId=' + encodeURIComponent(pop.id);
        delim = '&';
        postBody += delim + 'start=' + encodeURIComponent(getLocation());
        delim = '&';

        return postBody;
    }
};

function doCancel() {
    window.location.reload();
}

function findElementByClassName(element, className) {
    while (element.parentNode && (!element.className ||
            !element.className.match(
            new RegExp("(^|\\s)" + className + "(\\s|$)")))) {
        element = element.parentNode;
    }
    return element;
}

function prepareForDragAndDrop() {
    var panes = $A(document.getElementsByClassName('pane'));
    panes.each(function (pane) {
        Droppables.add(pane.id, {
            accept: 'pop',
            onHover: function (pop, targetElement, overlap) {
                enableHighlight(targetElement);
                updateDestinationBox(pop, targetElement, Position.xcomp,
                Position.ycomp);
            },
            onDrop: function (pop, targetElement, event) {
                disableHighlight();
                if (Element.hasClassName(pop, 'popIcon')) {
                    Element.hide(pop);
                    var receiver = document.createElement('div');
                    receiver.id = pop.id + "_";
                    if (_destinationBox && _destinationBox.parentNode == targetElement) {
                        targetElement.insertBefore(receiver, _destinationBox);
                        targetElement.insertBefore(pop, _destinationBox);
                    } else {
                        targetElement.appendChild(receiver);
                        targetElement.appendChild(pop);
                    }
                    targetElement.removeChild(pop);
                    deleteDestinationBox();
                    ManageLayout.Pane.save(receiver, false);
                } else {
                    if (_destinationBox && _destinationBox.parentNode == targetElement) {
                        targetElement.insertBefore(pop, _destinationBox);
                    } else {
                        targetElement.appendChild(pop);
                    }
                    deleteDestinationBox();
                    ManageLayout.Pane.save(pop, false);
                }
            }
        });
    });
}

function findNextElement(element) {
    var e = element;
    do {
        e = e.nextSibling;
        if (e && e.nodeType == 1) {
            return e;
        }
    } while (e);
    return undefined;
}

var _enabledElement;
function enableHighlight(element) {
    disableHighlight();
    element.style.borderStyle = 'dotted';
    element.style.borderWidth = '2px';
    element.style.borderColor = '#cccc66';
    _enabledElement = element;
}

function disableHighlight(element) {
    var elem = element || _enabledElement;
    if (elem) {
        elem.style.borderStyle = 'none';
        _enabledElement = null;
    }
}

var _destinationBox;
function updateDestinationBox(draggedPop, pane, mouseX, mouseY) {
    var pop, pos;
    if (_destinationBox && _destinationBox.parentNode == pane) {
        pop = findMouseoverPop(draggedPop, pane, mouseX, mouseY);
        if (!pop) {
            return;
        }

        if (!isBefore(pop, _destinationBox)) {
            switch (getStackingDirection(_destinationBox)) {
            case 'left':
                mouseX += _destinationBox.offsetWidth;
                break;

            case 'right':
                mouseX -= _destinationBox.offsetWidth;
                break;

            case 'down':
                mouseY += _destinationBox.offsetHeight;
                break;

            default:
                alert("ERROR!");
                return;
            }
        }
    }
    pop = findMouseoverPop(draggedPop, pane, mouseX, mouseY);
    if (!pop) {
        if (_destinationBox) {
            if (_destinationBox.parentNode != pane) {
                _destinationBox.parentNode.removeChild(_destinationBox);
                _destinationBox = undefined;
            }
            return;
        }

        var afterTail;
        var boxHeight;
        var pops = document.getElementsByClassName('pop', pane);
        if (pops.length > 0) {
            var lastPop = pops[pops.length - 1];
            pos = Position.cumulativeOffset(lastPop);
            switch (getStackingDirection(lastPop)) {
            case 'left':
                afterTail = mouseX >= pos[0] + lastPop.offsetWidth;
                break;

            case 'right':
                afterTail = mouseX <= pos[0] - lastPop.offsetWidth;
                break;

            case 'down':
                afterTail = mouseY >= pos[1] + lastPop.offsetHeight;
                boxHeight = draggedPop.offsetHeight;
                break;

            default:
                alert("ERROR!");
                return;
            }
        } else {
            afterTail = true;
            boxHeight = draggedPop.offsetHeight;
        }
        if (afterTail) {
            _destinationBox = createDestinationBox(boxHeight);
            pane.appendChild(_destinationBox);
        }
        return;
    }

    pos = Position.cumulativeOffset(pop);
    var appendToTail = false;
    var nearToTail;
    switch (getStackingDirection(pop)) {
    case 'left':
        nearToTail = mouseX > pos[0] + pop.offsetWidth / 2;
        break;

    case 'right':
        nearToTail = mouseX < pos[0] - pop.offsetWidth / 2;
        break;

    case 'down':
        nearToTail = mouseY > pos[1] + pop.offsetHeight / 2;
        break;

    default:
        alert("ERROR!");
        return;
    }
    if (nearToTail) {
        var nextElement = findNextElement(pop);
        if (!nextElement) {
            appendToTail = true;
        } else if (nextElement == _destinationBox
            && !findNextElement(_destinationBox)) {
            return;
        }
    }

    if (_destinationBox) {
        if (pop == _destinationBox) {
            return;
        }
        var par = _destinationBox.parentNode;
        if (par) {
            if (_destinationBox.nextSibling == pop && !appendToTail) {
                return;
            }
            par.removeChild(_destinationBox);
        }
    }
    _destinationBox = createDestinationBox(draggedPop.offsetHeight);

    if (appendToTail) {
        pane.appendChild(_destinationBox);
    } else {
        pane.insertBefore(_destinationBox, pop);
    }
}

function createDestinationBox(height) {
    var destinationBox = document.createElement('div');
    var innerBox = document.createElement('div');
    Element.setStyle(innerBox, {visibility: 'hidden'});
    innerBox.appendChild(document.createTextNode('INSERTED HERE'));
    destinationBox.appendChild(innerBox);
    Element.setStyle(destinationBox, {
        borderStyle: 'dotted',
        borderWidth: '2px',
        borderColor: '#000000'
    });
    if (height !== undefined) {
        Element.setStyle(destinationBox, {
            height: height + 'px'
        });
    }
    destinationBox.addClassName('pop');
    destinationBox.addClassName('destinationBox');
    return destinationBox;
}

function getStackingDirection(element) {
    var floatStyle = element.getStyle('float');
    if (floatStyle == 'left' || floatStyle == 'right') {
        return floatStyle;
    }
    var displayStyle = element.getStyle('display');
    if (displayStyle == 'inline') {
        return 'left';
    } else {
        return 'down';
    }
}

function deleteDestinationBox() {
    if (_destinationBox) {
        if (_destinationBox.parentNode) {
            _destinationBox.parentNode.removeChild(_destinationBox);
        }
        _destinationBox = undefined;
    }
}

function findMouseoverPop(draggedPop, pane, mouseX, mouseY) {
    var pops = document.getElementsByClassName('pop', pane);
    for(var i = 0; i < pops.length; i++) {
        if (pops[i] == draggedPop) {
            continue;
        }
        if (Position.within(pops[i], mouseX, mouseY)) {
            return pops[i];
        }
    }
    return undefined;
}

function isBefore(target, position) {
    var e = target;
    do {
        e = e.nextSibling;
        if (e == position) {
            return true;
        }
    } while (e);
    return false;
}

InPlacePropertyEditor = Class.create();
InPlacePropertyEditor.defaultHighlightColor = "#FFFF99";
InPlacePropertyEditor.prototype = {
  initialize: function(element, options) {
    this.element = $(element);

    this.options = Object.extend({
      okButton: true,
      okText: "      OK      ",
      cancelButton: true,
      cancelText: '<span tal:replace="structure my/plugin/%app.label.cancel">cancel</span>',
      savingText: "Saving...",
      clickToEditText: "Double click to edit",
      rows: 1,
      onComplete: function(transport, element) {
        new Effect.Highlight(element, {startcolor: this.options.highlightcolor});
      },
      onFailure: function(transport) {
        alert("Error communicating with the server: " + transport.responseText.stripTags());
      },
      callback: function(form) {
        return Form.serialize(form);
      },
      handleLineBreaks: true,
      loadingText: 'Loading...',
      savingClassName: 'inplaceeditor-saving',
      loadingClassName: 'inplaceeditor-loading',
      formClassName: 'inplaceeditor-form',
      highlightcolor: Ajax.InPlaceEditor.defaultHighlightColor,
      highlightendcolor: "#FFFFFF",
      externalControl: null,
      submitOnBlur: false,
      ajaxOptions: {},
      evalScripts: false,
      dialogMode: false
    }, options || {});

    if(!this.options.formId && this.element.id) {
      this.options.formId = this.element.id + "-inplaceeditor";
      if ($(this.options.formId)) {
        // there's already a form with that name, don't specify an id
        this.options.formId = null;
      }
    }

    if (this.options.externalControl) {
      this.options.externalControl = $(this.options.externalControl);
    }

    this.originalBackground = Element.getStyle(this.element, 'background-color');
    if (!this.originalBackground) {
      this.originalBackground = "transparent";
    }

    this.originalBorderColor = Element.getStyle(this.element, 'border-color');
    if (!this.originalBorderColor) {
      this.originalBorderColor = "#ffffff";
    }
    this.originalBorderStyle = Element.getStyle(this.element, 'border-style');
    if (!this.originalBorderStyle) {
      this.originalBorderStyle = "none";
    }
    this.originalBorderWidth = Element.getStyle(this.element, 'border-width');
    if (!this.originalBorderWidth) {
      this.originalBorderWidth = "0px";
    }

    this.element.title = this.options.clickToEditText;

    this.onclickListener = this.enterEditMode.bindAsEventListener(this);
    this.mouseoverListener = this.enterHover.bindAsEventListener(this);
    this.mouseoutListener = this.leaveHover.bindAsEventListener(this);
    Event.observe(this.element, 'dblclick', this.onclickListener);
    Event.observe(this.element, 'mouseover', this.mouseoverListener);
    Event.observe(this.element, 'mouseout', this.mouseoutListener);
    if (this.options.externalControl) {
      Event.observe(this.options.externalControl, 'dblclick', this.onclickListener);
      Event.observe(this.options.externalControl, 'mouseover', this.mouseoverListener);
      Event.observe(this.options.externalControl, 'mouseout', this.mouseoutListener);
    }

    this.onkeypressVariantListener = this._onkeypressVariantListener.bindAsEventListener(this);
    this.onDelayedVariantListener = this._onDelayedVariantListener.bind(this);
    this.onclickRemoveVariantListener = this._onclickRemoveVariantListener.bindAsEventListener(this);
  },
  enterEditMode: function(evt) {
    if (this.saving) return;
    if (this.editing) return;
    this.editing = true;
    this.onEnterEditMode();
    if (_popEditModeInPlace) {
      if (this.options.externalControl) {
        Element.hide(this.options.externalControl);
      }
      Element.hide(this.element);
      this.createForm();
      this.element.parentNode.insertBefore(this.form, this.element);
      if (!this.options.loadTextURL) Field.scrollFreeActivate(this.editField);
    } else {
      this.createForm();
      this.dialog = new LITBox('', {
        type: 'alert',
        width: 800,
        height: 600,
        draggable: true,
        resizable: true
      });
      this.dialog.close.onclick = this.onclickCancel.bind(this);
      this.dialog.d.onclick = this.onclickCancel.bind(this);
      this.dialog.d4.appendChild(this.form);
    }
    // stop the event to avoid a page refresh in Safari
    if (evt) {
      Event.stop(evt);
    }
    return false;
  },
  createForm: function() {
    this.form = document.createElement("form");
    this.form.id = this.options.formId;
    Element.addClassName(this.form, this.options.formClassName)
    this.form.onsubmit = this.onSubmit.bind(this);

    this.createEditField();

    if (this.options.textarea) {
      var br = document.createElement("br");
      this.form.appendChild(br);
    }

    var buttons = document.createElement("div");
    buttons.className = 'editor_buttons';
    this.form.appendChild(buttons);

    if (this.options.okButton) {
      okButton = document.createElement("input");
      okButton.type = "submit";
      okButton.value = this.options.okText;
      okButton.className = 'editor_ok_button';
      buttons.appendChild(okButton);
    }

    if (this.options.cancelButton) {
      buttons.appendChild(document.createTextNode(' '));
      cancelButton = document.createElement("input");
      cancelButton.type = "button";
      cancelButton.value = this.options.cancelText;
      cancelButton.className = 'editor_cancel';
      cancelButton.onclick = this.onclickCancel.bind(this);
      buttons.appendChild(cancelButton);
    }
  },
  hasHTMLLineBreaks: function(string) {
    if (!this.options.handleLineBreaks) return false;
    return string.match(/<br/i) || string.match(/<p>/i);
  },
  convertHTMLLineBreaks: function(string) {
    return string.replace(/<br>/gi, "\n").replace(/<br\/>/gi, "\n").replace(/<\/p>/gi, "\n").replace(/<p>/gi, "");
  },
  createEditField: function () {
    var obj = this;
    this.options.textarea = false;
    var div = document.createElement("div");
    div.innerHTML = this.options.loadingText;
    if (_popEditModeInPlace) {
      div.style.backgroundColor = this.options.highlightcolor;
    }
    if (this.options.submitOnBlur) {
      div.onblur = this.onSubmit.bind(this);
    }
    this.editField = div;
    this.loadExternalText();
    this.form.appendChild(this.editField);
  },
  getText: function() {
    return this.element.innerHTML;
  },
  loadExternalText: function() {
    this.editField.disabled = true;
    new Ajax.Request(
      this.options.loadTextURL,
      {
        asynchronous: true,
        parameters: 'popId=' + encodeURIComponent(this.element.id)
            + '&start=' + encodeURIComponent(getLocation()),
        onComplete: this.completeLoadForm.bind(this)
      });
  },
  onLoadedExternalText: function(transport) {
    Element.removeClassName(this.form, this.options.loadingClassName);
    this.editField.disabled = false;
    this.editField.value = transport.responseText.stripTags();
    Field.scrollFreeActivate(this.editField);
  },
  onclickCancel: function() {
    this.onComplete();
    this.leaveEditMode();
    return false;
  },
  onFailure: function(transport) {
    this.options.onFailure(transport);
    if (this.oldInnerHTML) {
      this.element.innerHTML = this.oldInnerHTML;
      this.oldInnerHTML = null;
    }
    return false;
  },
  onSubmit: function() {
    var form = this.form;
    var value = this.editField.value;
    new Ajax.Request(
      '<span tal:replace="page:%/layout.validateProperties.do"></span>',
      {
        asynchronous: true,
        method: 'post',
        postBody: this.options.callback(form, value),
        onComplete: function (transport) {
          if (transport.responseText.length > 0) {
            // ERROR
            this.completeLoadForm(transport);
          } else {
            // SUCCEED
            this.doSubmit();
          }
        }.bind(this)
      });
    // stop the event to avoid a page refresh in Safari
    if (arguments.length > 1) {
      Event.stop(arguments[0]);
    }
    return false;
  },
  doSubmit: function() {
    // onLoading resets these so we need to save them away for the Ajax call
    var form = this.form;
    var value = this.editField.value;

    // do this first, sometimes the ajax call returns before we get a chance to switch on Saving...
    // which means this will actually switch on Saving... *after* we've left edit mode causing Saving...
    // to be displayed indefinitely
    this.onLoading();

    new Ajax.Updater(
      { success: this.element,
        // don't update on failure (this could be an option)
        failure: null },
      '<span tal:replace="page:%/layout.updateProperties.do"></span>',
      Object.extend({
        method: 'post',
        postBody: this.options.callback(form, value),
        onComplete: this.onComplete.bind(this),
        onFailure: this.onFailure.bind(this),
        insertion: this.insertionAfterSubmit.bind(this)
      }, this.options.ajaxOptions));
  },
  insertionAfterSubmit: function (receiver, response) {
    if (response && response.length > 0) {
      Element.hide(receiver);
      var div = document.createElement('div');
      div.innerHTML = response;
      var parent = receiver.parentNode;
      var pop = div.firstChild
      parent.insertBefore(pop, receiver);
      prepareForPop(pop);
      parent.removeChild(receiver);
      addLinkToNonExistentImages(pop);
    }
  },
  onLoading: function() {
    this.saving = true;
    this.removeForm();
    this.leaveHover();
    this.showSaving();
  },
  showSaving: function() {
    this.oldInnerHTML = this.element.innerHTML;
    this.element.innerHTML = this.options.savingText;
    Element.addClassName(this.element, this.options.savingClassName);
    this.element.style.backgroundColor = this.originalBackground;
    Element.show(this.element);
  },
  removeForm: function() {
    if(this.form) {
      if (this.form.parentNode) Element.remove(this.form);
      this.form = null;
    }
  },
  enterHover: function() {
    if (this.saving) return;
    Element.setStyle(this.element, {
      borderColor: '#666666',
      borderStyle: 'dashed',
      borderWidth: '2px'
    });
    if (this.effect) {
      this.effect.cancel();
    }
    Element.addClassName(this.element, this.options.hoverClassName);
  },
  leaveHover: function() {
    Element.removeClassName(this.element, this.options.hoverClassName);
    if (this.saving) return;
    Element.setStyle(this.element, {
      borderColor: this.originalBorderColor,
      borderStyle: this.originalBorderStyle,
      borderWidth: this.originalBorderWidth
    });
  },
  leaveEditMode: function() {
    Element.removeClassName(this.element, this.options.savingClassName);
    this.removeForm();
    this.leaveHover();
    if (_popEditModeInPlace) {
      this.element.style.backgroundColor = this.originalBackground;
      Element.show(this.element);
      if (this.options.externalControl) {
        Element.show(this.options.externalControl);
      }
    } else {
      if (this.dialog != null) {
        this.dialog.remove();
        this.dialog = null;
      }
    }
    this.editing = false;
    this.saving = false;
    this.oldInnerHTML = null;
    this.onLeaveEditMode();
    if (_dock != null) {
        _dock.update();
    }
  },
  onComplete: function(transport) {
    this.leaveEditMode();
    this.options.onComplete.bind(this)(transport, this.element);
  },
  onEnterEditMode: function() {},
  onLeaveEditMode: function() {},
  dispose: function() {
    if (this.oldInnerHTML) {
      this.element.innerHTML = this.oldInnerHTML;
    }
    this.leaveEditMode();
    Event.stopObserving(this.element, 'dblclick', this.onclickListener);
    Event.stopObserving(this.element, 'mouseover', this.mouseoverListener);
    Event.stopObserving(this.element, 'mouseout', this.mouseoutListener);
    if (this.options.externalControl) {
      Event.stopObserving(this.options.externalControl, 'dblclick', this.onclickListener);
      Event.stopObserving(this.options.externalControl, 'mouseover', this.mouseoverListener);
      Event.stopObserving(this.options.externalControl, 'mouseout', this.mouseoutListener);
    }
  },
  _onDelayedVariantListener: function () {
    this.onChangeVariantListener($A(document.getElementsByClassName('variant', this.editField)).shift());
  },
  _onkeypressVariantListener: function (e) {
    if (e.keyCode == Event.KEY_RETURN) {
      Event.stop(e);
      this.onChangeVariantListener(Event.element(e));
    }
  },
  onChangeVariantListener: function (element) {
    var variant = element.value;
    this.editField.disabled = true;
    new Ajax.Request(
      this.options.loadTextURL,
      {
        asynchronous: true,
        parameters: 'popId=' + encodeURIComponent(this.element.id)
            + '&variant=' + encodeURIComponent(variant)
            + '&start=' + encodeURIComponent(getLocation()),
        onComplete: this.completeLoadForm.bind(this)
      });
  },
  _onclickRemoveVariantListener: function (e) {
    if (!confirm('<span tal:replace="my/plugin/%app.note.layout.confirmRemoveVariant"></span>')) {
        return;
    }

    Event.stop(e);

    var element = Event.element(e);
    Event.stopObserving(element, "click", this.onclickRemoveVariantListener);

    var variant = this.variantElement.value;
    this.editField.disabled = true;
    new Ajax.Request(
      this.options.loadTextURL,
      {
        asynchronous: true,
        parameters: 'popId=' + encodeURIComponent(this.element.id)
            + '&variant=' + encodeURIComponent(variant)
            + '&remove=true'
            + '&start=' + encodeURIComponent(getLocation()),
        onComplete: this.completeLoadForm.bind(this)
      });
  },
  completeLoadForm: function (transport) {
    this.editField.innerHTML = transport.responseText;
    this.editField.disabled = false;
    Field.scrollFreeActivate(this.editField);

    if (!_popEditModeInPlace) {
      var tabified = $(this.element.id + '_editProperties_tabified');
      if (tabified) {
        new Tabified(tabified);
      }

      var previewTab = $(this.element.id + '_preview');
      if (previewTab) {
        Event.observe(previewTab, 'click',
          this.previewPop.bindAsEventListener(this));
      }
    }

    if (_dock != null) {
        _dock.update();
    }

    var pageSelectors = $A(document.getElementsByClassName('pageSelector_input', this.editField));
    pageSelectors.each(function (pageSelector) {
      new PageSelector(pageSelector.id,
          '<span tal:replace="page:%/layout.loadPageCandidates.do"></span>'
      );
    });

    var elements = $A(document.getElementsByClassName('expand', this.editField));
    elements.each(function (element) {
      Event.observe(element, 'click', this.expandField.bindAsEventListener(this));
    }.bind(this));

    var variantElement = $A(document.getElementsByClassName('variant', this.editField)).shift();
    if (variantElement) {
      this.variantElement = variantElement;
      Event.observe(variantElement, "keypress", this.onkeypressVariantListener);
      new Form.Element.DelayedObserver(variantElement, 1.0,
          this.onDelayedVariantListener);
      Field.focus(variantElement);
    }

    var removeVariantElement = $A(document.getElementsByClassName('removeVariant', this.editField)).shift();
    if (removeVariantElement) {
      this.removeVariantElement = removeVariantElement;
      Event.observe(removeVariantElement, "click", this.onclickRemoveVariantListener);
    }
  },
  expandField: function (e) {
    var target = Event.element(e);
    var field = $(target.id.substring(0, target.id.length - 7));
    var newField;
    if (target.value == '+') {
      newField = document.createElement('textarea');
      newField.rows = 10;
      newField.style.verticalAlign = 'top';
      newField.innerHTML = field.value.escapeHTML();
      target.value = '-';
    } else {
      newField = document.createElement('input');
      newField.type = 'text';
      newField.value = field.innerHTML.unescapeHTML();
      newField.className = 'line';
      target.value = '+';
    }
    newField.id = field.id;
    newField.name = field.name;
    newField.style.width = field.style.width;
    field.parentNode.insertBefore(newField, field);
    field.parentNode.removeChild(field);
  },
  previewPop: function() {
    $(this.element.id + '_preview_content').innerHTML = 'Loading...';
    new Ajax.Request(
      '<span tal:replace="page:%/layout.validateProperties.do"></span>',
      {
        asynchronous: true,
        method: 'post',
        postBody: this.options.callback(this.form),
        onComplete: function (transport) {
          if (transport.responseText.length > 0) {
            // ERROR
            this.completeLoadForm(transport);
          } else {
            // SUCCEED
            this.doPreviewPop();
          }
        }.bind(this)
      });
  },
  doPreviewPop: function() {
    $(document.getElementsByClassName('error-message', this.form)).each(function(e){
        e.parentNode.removeChild(e);
    });
    $(document.getElementsByClassName('error', this.form)).each(function(e){
        Element.removeClassName(e, 'error');
    });
    new Ajax.Updater(
      this.element.id + '_preview_content',
      '<span tal:replace="page:%/layout.previewPop.do"></span>',
      Object.extend({
        method: 'post',
        postBody: this.options.callback(this.form)
      }, this.options.ajaxOptions));
  }
};


var PageSelector = Class.create();
PageSelector.prototype = {
    initialize: function(element, loadCandidatesURL, options) {
       this.element = $(element);
       this.loadCandidatesURL = loadCandidatesURL;
       this.candidates = $(this.element.id + '_candidates');
       this.preview = $(this.element.id + '_preview');
       this.options = Object.extend({
           pathnameParamName: 'pathname'
       }, options || {});

       if (!this.candidates) {
           alert("select element does not found: id=" + this.element.id + '_candidates');
           return;
       }

       this.prepare();
    },
    prepare: function() {
        this.hideCandidates();
        this.hidePreview();

        if (this.preview) {
            Element.setStyle(this.preview, {
                width: '240px',
                height: '240px'
            });
        }

        new Form.Element.DelayedObserver(this.element, 1.0,
            function() {
                this.changeListener();
            }.bindAsEventListener(this));
        Event.observe(this.element, 'blur', function() {
            this.hideCandidates();
            this.hidePreview();
        }.bindAsEventListener(this));
        Event.observe(this.element, 'keypress',
            this.elementKeypressListener.bindAsEventListener(this));
        Event.observe(this.candidates, 'focus', function() {
            this.showCandidates();
        }.bindAsEventListener(this));
        Event.observe(this.candidates, 'keypress',
            this.candidatesKeypressListener.bindAsEventListener(this));
    },
    elementKeypressListener: function(event) {
        if (event.keyCode == Event.KEY_ESC) {
            Event.stop(event);
            this.hideCandidates();
            this.hidePreview();
            Field.focus(this.element);
        }
    },
    candidatesKeypressListener: function(event) {
        if (event.keyCode == Event.KEY_ESC) {
            Event.stop(event);
            this.hideCandidates();
            this.hidePreview();
            Field.focus(this.element);
        } else if (event.keyCode == Event.KEY_RETURN) {
            Event.stop(event);
            var select = Event.element(event);
            var index = select.selectedIndex;
            if (index >= 0) {
                var pathname = select.options[index].innerHTML.unescapeHTML();
                this.element.value = pathname;
                this.hideCandidates();
                Field.focus(this.element);
            }
        }
    },
    changeListener: function() {
        new Ajax.Request(
           this.loadCandidatesURL, {
              parameters: this.options.pathnameParamName
                  + '=' + encodeURIComponent(this.element.value)
                  + '&start=' + encodeURIComponent(getLocation()),
              onComplete: this.completeLoadCandidates.bind(this)
           });
    },
    completeLoadCandidates: function(transport) {
        if (transport.responseText.length > 0) {
            this.showCandidates(transport.responseText);
        } else {
            this.hideCandidates();
        }
        this.hidePreview();
        $A(document.getElementsByClassName('candidate', this.candidates)).each(function (candidate) {
            Event.observe(candidate, 'mouseover', function(event) {
                var element = Event.element(event);
                this.showPreview(element.innerHTML.unescapeHTML());
            }.bindAsEventListener(this));
            Event.observe(candidate, 'click',
                this.selectCandidate.bindAsEventListener(this));
        }.bind(this));
    },
    selectCandidate: function(event) {
        var element = Event.element(event);
        var pathname = element.innerHTML.unescapeHTML();
        this.element.value = pathname;
        this.hideCandidates();
        Field.focus(this.element);
    },
    showPreview: function(pathname) {
        if (this.preview) {
            if (this.hidePreviewTimerId) {
                clearTimeout(this.hidePreviewTimerId);
                this.hidePreviewTimerId = undefined;
            }
            Element.hide(this.preview);
            if (pathname && pathname.length > 0) {
                var myAjax = new Ajax.Updater(
                    this.preview,
                    '<span tal:replace="page:%/layout.showPagePreview.do"></span>', {
                        asynchronous: false,
                        method: 'get',
                        parameters: this.options.pathnameParamName
                            + '=' + encodeURIComponent(pathname)
                            + '&start=' + encodeURIComponent(getLocation())
                    });
                var imgs = this.preview.getElementsByTagName('img');
                if (imgs.length > 0) {
                    Element.setStyle(imgs[0], {
                        visibility: 'hidden'
                    });
                    Element.show(this.preview);
                    Event.observe(imgs[0], 'load', function(event) {
                        var img = this.preview.getElementsByTagName('img')[0];
                        Event.stop(event);

                        var w = img.offsetWidth;
                        var h = img.offsetHeight;
                        if (w >= h) {
                            if (w > 240) {
                                h = 240.0 * h / w;
                                w = 240;
                            }
                        } else {
                            if (h > 240) {
                                w = 240.0 * w / h;
                                h = 240;
                            }
                        }
                        Element.setStyle(img, {
                            width: w + 'px',
                            height: h + 'px'
                        });
                        Element.setStyle(img, {
                            visibility: 'visible'
                        });
                    }.bindAsEventListener(this));
                }
            }
        }
    },
    hidePreview: function() {
        if (this.preview) {
            if (this.hidePreviewTimerId) {
                clearTimeout(this.hidePreviewTimerId);
            }
            this.hidePreviewTimerId = setTimeout(function() {
                Element.hide(this.preview);
            }.bind(this));
        }
    },
    showCandidates: function(candidates) {
        if (this.hideCandidatesTimerId) {
            clearTimeout(this.hideCandidatesTimerId);
            this.hideCandidatesTimerId = undefined;
        }
        if (candidates) {
            this.candidates.innerHTML = candidates;
        }
        Element.show(this.candidates);
    },
    hideCandidates: function() {
        if (this.hideCandidatesTimerId) {
            clearTimeout(this.hideCandidatesTimerId);
        }
        this.hideCandidatesTimerId = setTimeout(function() {
            Element.hide(this.candidates);
        }.bind(this));
    }
}
