/*
<html>
  <head>
    <script type="text/javascript" src="prototype.js"></script>
    <script type="text/javascript" src="dock.js"></script>
    <script type="text/javascript">
Event.observe(window, 'load', function () {
    var docks = $A(document.getElementsByClassName('dock'));
    docks.each(function (dock) {
        new Dock(dock, 'icon', {
        });
    });
});
    </script>
  </head>
  <body>
    <div class="dock">
      <img class="icon" src="app.png" />
      <img class="icon" src="compass.png" />
      <img class="icon" src="Edit.png" />
      <img class="icon" src="iTunes.png" />
      <img class="icon" src="MacIE.png" />
      <img class="icon" src="NSApplicationIcon.png" />
      <img class="icon" src="preview.png" />
      <img class="icon" src="Stickies.png" />
    </div>
  </body>
</html>
*/

var Dock = Class.create();
Dock.prototype = {
    initialize: function (element, iconClassName, options) {
        this.element = element;
        this.options = Object.extend({
            imageScale: 0.4,
            spaceScale: 0.5,
            amplifyingScale: 1.6,
            verticalAlign: 'bottom',
            padding: 4
        }, options || {});

        this.freezed = false;

        this.icons = $A(document.getElementsByClassName(iconClassName, element));
        if (this.icons.length <= 1) {
            alert("Dock must have at least 2 icons.");
            return;
        }

        this.iconOriginalWidth = this.options.iconWidth || this.icons[0].offsetWidth;
        this.iconWidth = this.iconOriginalWidth * this.options.spaceScale;
        this.iconOriginalHeight = this.options.iconHeight || this.icons[0].offsetHeight;
        this.iconHeight = this.iconOriginalHeight * this.options.spaceScale;
        this.iconsWidth = this.iconWidth * (this.icons.length - 1);
        this.iconsAmplifiedWidth = this.iconsWidth * this.options.amplifyingScale;

        if (this.iconOriginalWidth == 0) {
            alert("Icon width is zero. Please specify 'iconWidth' option.");
            return;
        }

        element.setStyle({
            width: (this.iconsAmplifiedWidth + this.iconWidth + this.options.padding * 2) + 'px',
            height: (this.iconOriginalHeight + this.options.padding * 2) + 'px'
        });

        var onMouseover = this.options.onMouseover || Prototype.emptyFunction;
        this.options.onMouseover = function (e) {
            this.mouseoverIcon(e);
            onMouseover(e);
        }.bindAsEventListener(this);

        var onMouseout = this.options.onMouseout || Prototype.emptyFunction;
        this.options.onMouseout = function (e) {
            onMouseout(e);
        }.bindAsEventListener(this);

        for (var i = 0; i < this.icons.length; i++) {
            var icon = this.icons[i];
            icon.originalWidth = this.iconOriginalWidth;
            icon.originalHeight = this.iconOriginalHeight;
            var width = icon.originalWidth * this.options.imageScale;
            var height = icon.originalHeight * this.options.imageScale;
            Event.observe(icon, 'mouseover', this.options.onMouseover, false);
            Event.observe(icon, 'mouseout', this.options.onMouseout, false);
        };

        this.mousemoveListener = this.mousemove.bindAsEventListener(this);
        this.mouseoutListener = this.mouseout.bindAsEventListener(this);

        Event.observe(element, 'mousemove', this.mousemoveListener, false);
        Event.observe(element, 'mouseout', this.mouseoutListener, false);

        this.update();
    },
    mouseoverIcon: function (e) {
        var icon = Event.element(e);
        var zIndex = this.icons.length;
        var delta = 1;
        this.icons.each(function (ic) {
            ic.style.zIndex = zIndex;
            if (ic == icon) {
                delta = -1;
            }
            zIndex += delta;
        });
    },
    mousemove: function (e) {
        if (this.freezed) {
            return;
        }
        var mouseX = Event.pointerX(e);
        var mouseY = Event.pointerY(e);
        this.icons.each(function (icon) {
            var pos = this.calculatePosition(icon.initialCenterX, mouseX);
            this.updatePosition(icon, pos[0], pos[1]);
        }.bind(this));
    },
    mouseout: function (e) {
        if (this.freezed) {
            return;
        }

        // [FIXME] workaround for problem that mouseout sometimes occurs
        // in the element somehow...
        var mouseX = Event.pointerX(e);
        var mouseY = Event.pointerY(e);
        if (Position.within(this.element, mouseX, mouseY)) {
            return;
        }

        this.revertIconsPosition();
        this.icons.each(function (icon) {
            icon.style.zIndex = 0;
        });
    },
    revertIconsPosition: function () {
        this.icons.each(function (icon) {
            this.revertIconPosition(icon);
        }.bind(this));
    },
    revertIconPosition: function (icon) {
        this.updatePosition(icon, icon.initialCenterX, this.options.imageScale);
    },
    updatePosition: function (icon, x, scale) {
        scale = scale || 1.0;
        var width = icon.originalWidth * scale;
        var height = icon.originalHeight * scale;
        var y;
        var verticalAlign = this.options.verticalAlign;
        if (verticalAlign == 'center') {
            y = this.elementCenterY - height / 2;
        } else if (verticalAlign == 'top') {
            y = this.elementCenterY - this.iconOriginalHeight / 2;
        } else {
            y = this.elementCenterY + this.iconOriginalHeight / 2 - height;
        }
        icon.setStyle({
            position: 'absolute',
            left: (x - width / 2) + 'px',
            top: y + 'px',
            width: width + 'px',
            height: height + 'px'
        });
    },
    calculatePosition: function (center, mouse) {
        var pos = (center - mouse) / this.iconWidth;
        var maxPos = this.icons.length / 2.0;
        var offset = (this.iconsAmplifiedWidth - this.iconsWidth) / 2.0;
        var limitOffset = (this.iconsWidth + this.iconWidth) / 2.0;
        var leftX = center - offset;
        var rightX = center + offset;
        var radian = Math.PI / 2.0 * (1 - pos / (this.icons.length - 1));
        var x = mouse + Math.sqrt(Math.pow(this.iconsAmplifiedWidth, 2) / 2.0)
            * Math.cos(radian);
        var scale = (Math.sin(radian) - 1.0) * (1.0 - this.options.imageScale) / (1.0 - Math.sin(Math.PI / 4.0)) + 1.0;
        if (mouse < this.elementCenterX - limitOffset) {
            x = center;
            scale = this.options.imageScale;
        } else if (mouse > this.elementCenterX + limitOffset) {
            x = center;
            scale = this.options.imageScale;
        } else if (pos >= maxPos || x > rightX) {
            x = rightX;
            scale = this.options.imageScale;
        } else if (pos <= -maxPos || x < leftX) {
            x = leftX;
            scale = this.options.imageScale;
        }

        return [x, scale];
    },
    update: function () {
        var pos = Position.cumulativeOffset(this.element);
        this.elementCenterX = pos[0] + this.element.offsetWidth / 2;
        this.elementCenterY = pos[1] + this.element.offsetHeight / 2;
        for (var i = 0; i < this.icons.length; i++) {
            var icon = this.icons[i];
            icon.initialCenterX = this.elementCenterX
                + (i + 1 - (this.icons.length + 1) / 2) * this.iconWidth;
            this.revertIconPosition(icon);
        };
    },
    freeze: function (freezed) {
        this.freezed = freezed;
    },
    destroy: function () {
        this.icons.each(function (icon) {
            Event.stopObserving(icon, 'mouseover', this.mouseoverIconListener);
        });

        Event.stopObserving(this.element, 'mousemove', this.mousemoveListener);
        Event.stopObserving(this.element, 'mouseout', this.mouseoutListener);
    }
}