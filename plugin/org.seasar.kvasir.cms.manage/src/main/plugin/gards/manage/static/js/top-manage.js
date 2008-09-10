Event.observe(window, 'load', prepare, false);

function prepare() {
    Event.observe($('selectionForGoing'), 'change', goToSelectedPage, false);
    Event.observe($('goToSelectedPage'), 'click', goToSelectedPage, false);

    Event.observe($('selectionForChangingSite'), 'change', changeSite, false);
    Event.observe($('changeSite'), 'click', changeSite, false);
}

function goToSelectedPage(e) {
    var form = Event.element(e).form;
    var option = form.selection.options[form.selection.selectedIndex];
    var href = option.value;
    if (Element.hasClassName(option, 'unframe')) {
        window.parent.location.href = href;
    } else {
        window.open(href, 'manageMain', '');
    }
}

function changeSite(e) {
    Event.element(e).form.submit();
}
