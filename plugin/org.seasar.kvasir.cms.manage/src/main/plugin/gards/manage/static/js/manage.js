Event.observe(window, 'load', prepareForTabs, false);

function prepareForTabs() {
    $A(document.getElementsByClassName('tabified')).each(function (tabified) {
        new Tabified(tabified);
    });
}

function updateMenu()
{
    window.open("<span tal:replace='structure page:@/menu-manage.do?refresh=true' />", "manageMenu", "");
}
