var Tabified = Class.create();
Tabified.prototype = {
    initialize: function (element, options) {
        this.element = element;
        this.options = Object.extend({
        }, options || {});

        var tabs = document.getElementsByClassName('tabs', element)[0];
        if (!tabs) {
            alert('ul element which belongs to "tabs" class must exist');
            return;
        }

        var firstTab;
        $A(tabs.getElementsByTagName('li')).each(function (tab) {
            if (!firstTab) {
                firstTab = tab;
            }
            if (!this.currentTab && tab.hasClassName('current')) {
                this.currentTab = tab;
            }
            Event.observe(tab, 'click',
                this.tabClickListener.bindAsEventListener(this), false);
            Event.observe(tab, 'mouseover', function () {
                this.setStyle({cursor: 'pointer'});
            }.bindAsEventListener(tab), false);
            Event.observe(tab, 'mouseout', function () {
                this.setStyle({cursor: 'auto'});
            }.bindAsEventListener(tab), false);
                
        }.bind(this));
        if (!firstTab) {
            alert('li element must exist');
            return;
        }
        if (!this.currentTab) {
            this.currentTab = firstTab;
        }
        this.enableTab(this.currentTab);
    },
    tabClickListener: function (e) {
        this.enableTab(Event.element(e));
    },
    enableTab: function (tab) {
        this.currentTab.removeClassName('current');
        tab.addClassName('current');

        $(this.currentTab.id + '_content').removeClassName('current');
        $(tab.id + '_content').addClassName('current');

        this.currentTab = tab;
    }
}
