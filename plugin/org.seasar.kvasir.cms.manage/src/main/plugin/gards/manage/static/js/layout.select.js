Event.observe(window, 'load', setListeners, false);

function setListeners() {
    var elements = $A(document.getElementsByClassName('pop'));
    elements.each(function (element) {
        Event.observe(element, 'mouseover', enableHilight, false);
        Event.observe(element, 'mouseout', disableHilight, false);
    });

    elements = $A(document.getElementsByClassName('submit'));
    elements.each(function (element) {
        Event.observe(element, 'mouseover', function () {
                this.setStyle({cursor: 'pointer' });
            }.bindAsEventListener(element), false);
        Event.observe(element, 'mouseout', function () {
                this.setStyle({cursor: 'auto' });
            }.bindAsEventListener(element), false);
        Event.observe(element, 'click', doAdd, false);
    });
}

function doAdd(e) {
    var popId = Event.element(e).parentNode.id;
    if (popId) {
        $('popId').value = popId;
        Event.findElement(e, 'form').submit();
    }
}


function enableHilight(e) {
    var element = Event.element(e);
    element.oldBackgroundColor = element.style.backgroundColor;
    element.oldBorderStyle = element.style.borderStyle;
    element.oldBorderWidth = element.style.borderWidth;
    element.oldBorderColor = element.style.borderColor;
    element.style.backgroundColor = '#ffffcc';
    element.style.borderStyle = 'solid';
    element.style.borderWidth = '2px';
    element.style.borderColor = '#cccc66';
}

function disableHilight(e) {
    var element = Event.element(e);
    element.style.backgroundColor = element.oldBackgroundColor;
    element.style.borderStyle = element.oldBorderStyle;
    element.style.borderWidth = element.oldBorderWidth;
    element.style.borderColor = element.oldBorderColor;
}
