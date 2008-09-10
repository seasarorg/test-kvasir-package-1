Event.observe(window, 'load', prepare, false);

isSelected = false;

function prepare() {
    var variantSelector = $('variantSelector');
    if (variantSelector) {
        Event.observe(variantSelector, 'change', onChangeVariantSelector, false);
    }

    var elements = $A(document.getElementsByClassName('colorSample'));
    elements.each(function (element) {
        Event.observe(element, 'click', openPalette, false);
        Event.observe(element, 'mouseover', function () {
            this.setStyle({cursor: 'pointer'});
        }, false);
        Event.observe(element, 'mouseout', function () {
            this.setStyle({cursor: 'auto'});
        }, false);
    });

    elements = $A(document.getElementsByClassName('colorInput'));
    elements.each(function (element) {
        element.originalValue = element.value;
        Event.observe(element, 'keyup', changeBgcolor, false);
    });

    elements = $A(document.getElementsByClassName('palette'));
    elements.each(function (element) {
        Element.hide(element);
    });

    elements = $A(document.getElementsByClassName('colorResetter'));
    elements.each(function (element) {
        Event.observe(element, 'click', resetColor, false);
    });

    elements = $A(document.getElementsByClassName('valueInput'));
    elements.each(function (element) {
        element.style.width = '90%';
    });

    elements = $A(document.getElementsByClassName('valueResizer'));
    elements.each(function (element) {
        element.originalValue = element.value;
        Event.observe(element, 'click', resizeValueInput, false);
    });
}

function resizeValueInput(e) {
    var name = this.id.substring('resizer:'.length);
    var input = $('input:' + name);
    var newInput;
    if (this.value == '+') {
        newInput = document.createElement('textarea');
        newInput.rows = 10;
        newInput.setStyle({
            width: '90%',
            verticalAlign: 'top'
        });
        newInput.innerHTML = input.value.escapeHTML();
        this.value = '-';
    } else {
        newInput = document.createElement('input');
        newInput.type = 'text';
        newInput.setStyle({
            width: '90%'
        });
        newInput.value = input.innerHTML.unescapeHTML();
        this.value = '+';
    }
    newInput.id = input.id;
    newInput.name = input.name;
    input.parentNode.insertBefore(newInput, input);
    input.parentNode.removeChild(input);
}

function resetColor(e) {
    var name = this.id.substring('resetter:'.length);
    var input = $('input:' + name);
    var originalValue = input.originalValue;
    input.value = originalValue;
    $('sample:' + name).style.backgroundColor = originalValue;
}

function changeBgcolor(e) {
    var color = this.value;
    if (color.length != 7) {
        return;
    }
    if (color.charAt(0) != '#') {
        return;
    }
    for (var i = 1; i < 7; i++) {
        var ch = color.charAt(i);
        if ("0123456789abcdefABCDEF".indexOf(ch) < 0) {
            return;
        }
    }
    $('sample:' + this.id.substring('input:'.length)).style.backgroundColor
        = this.value;
}

function closePalette(e) {
    var name = this.id.substring('closer:'.length);
    $('palette:' + name).innerHTML = '';
    Event.stopObserving(this, 'click', closePalette);
    Element.hide($('paletteHolder:' + name));
}

function openPalette(e) {
    var name = this.id.substring('sample:'.length);
    var map = [ 'ff', 'cc', '99', '66', '33', '00' ];
    var palette = $('palette:' + name);
    if (palette.innerHTML != '') {
        return;
    }

    var table = document.createElement('table');
    table.valign = 'top';
    table.cellSpacing = '2';
    table.cellPadding = '0';
    table.setStyle({ backgroundColor: '#FFFFFF' });
    palette.appendChild(table);

    for (var r = 0; r < 5; r++) {
        var tr = document.createElement('tr');
        table.appendChild(tr);
        for (var g = 0; g < 5; g++) {
            for (var b = 0; b < 5; b++) {
                var color = '#' + map[r] + map[g] + map[b];
                var td = createPaletteCell(name, color);
                tr.appendChild(td);
            }
        }
    }
    var tr = document.createElement('tr');
    table.appendChild(tr);
    for (var i = 15; i >= 0; i--) {
        var c = Number(i).toString(16);
        var color = '#' + c + c + c + c + c + c;
        var td = createPaletteCell(name, color);
        tr.appendChild(td);
    }

    Event.observe($('closer:' + name), 'click', closePalette, false);
    Element.show($('paletteHolder:' + name));
}

function createPaletteCell(name, color) {
    var td = document.createElement('td');
    td.paletteCellColor = color;
    td.id = 'paletteCell:' + name;
    td.setStyle({
        width: '16px',
        height: '16px',
        backgroundColor: color,
        fontSize: '1px'
    });
    td.innerHTML = '&nbsp;';
    Event.observe(td, 'mouseover', function () {
        this.setStyle({cursor: 'pointer'});
    }, false);
    Event.observe(td, 'mouseout', function () {
        this.setStyle({cursor: 'auto'});
    }, false);
    Event.observe(td, 'click', function () {
        var name = this.id.substring('paletteCell:'.length);
        var color = this.paletteCellColor;
        $('sample:' + name).style.backgroundColor = color;
        $('input:' + name).value = color;
    }, false);

    return td;
}

function onChangeVariantSelector(e) {
    this.form.submit();
}

function toggleSelect(form)
{
    if (!isSelected) {
        for (i = 0; i < form.length; i++) {
            form.elements[i].checked = true;
        }
        isSelected = true;
        form.selectButton.value = "選択を解除";
        return isSelected;
    } else {
        for (i = 0; i < form.length; i++) {
            form.elements[i].checked = false;
        }
        isSelected = false;
        form.selectButton.value = "全て選択";
        return isSelected;
    }
}
