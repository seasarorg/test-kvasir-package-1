Event.observe(window, 'load', prepareForConfiguringPluginSettings, false);

function prepareForConfiguringPluginSettings() {
    Event.observe($('configured-plugin-id'), 'change', doChangeConfiguredPluginId, false);
    prepareForInstallSetting();
    prepareForUpdateSetting();
    prepareForRepositorySetting();
}

function doChangeConfiguredPluginId() {
    var select = $('configured-plugin-id');
    var idx = select.selectedIndex;
    if (idx <= 0) {
        return;
    }
    displayConfigurationForm(select.options[idx].value, false);
}

function displayConfigurationForm(pluginId, reset) {
    $('configuration-form').innerHTML = "Loading ...";

    var elements = $A(document.getElementsByClassName('expand'));
    elements.each(function (element) {
        if (element.clickHandler) {
            Event.stopObserving(element, 'click', element.clickHandler);
        }
    });

    var elements = $A(document.getElementsByClassName('delete'));
    elements.each(function (element) {
        if (element.clickHandler) {
            Event.stopObserving(element, 'click', element.clickHandler);
        }
    });

    var elements = $A(document.getElementsByClassName('add'));
    elements.each(function (element) {
        if (element.clickHandler) {
            Event.stopObserving(element, 'click', element.clickHandler);
        }
    });

    var updateButton = $('update');
    if (updateButton && updateButton.clickHandler) {
        Event.stopObserving(updateButton, 'click', updateButton.clickHandler);
    }

    var resetButton = $('reset');
    if (resetButton && resetButton.clickHandler) {
        Event.stopObserving(resetButton, 'click', resetButton.clickHandler);
    }

    var params = 'pluginId=' + encodeURIComponent(pluginId);
    if (reset) {
        params += '&reset=true';
    }
    var myAjax = new Ajax.Updater(
        'configuration-form',
        '<span tal:replace="page:%/plugin.configurationForm.do"></span>',
        {
            method: (reset ? 'post' : 'get'),
            parameters: params,
            onComplete: onCompleteUpdatingConfigurationForm
        }
    );
}

function onCompleteUpdatingConfigurationForm(transport, param) {
    var elements = $A(document.getElementsByClassName('expand'));
    elements.each(function (element) {
        element.clickHandler = expandTextBox.bindAsEventListener(element);
        Event.observe(element, 'click', element.clickHandler);
    });

    elements = $A(document.getElementsByClassName('delete'));
    elements.each(function (element) {
        element.clickHandler = deleteProperty.bindAsEventListener(element);
        Event.observe(element, 'click', element.clickHandler);
    });

    elements = $A(document.getElementsByClassName('add'));
    elements.each(function (element) {
        element.clickHandler = addProperty.bindAsEventListener(element);
        Event.observe(element, 'click', element.clickHandler);
    });

    var updateButton = $('update');
    updateButton.clickHandler = submitConfigurationForm.bindAsEventListener(updateButton);
    Event.observe(updateButton, 'click', updateButton.clickHandler);

    var resetButton = $('reset');
    resetButton.clickHandler = resetConfiguration.bindAsEventListener(resetButton);
    Event.observe(resetButton, 'click', resetButton.clickHandler);
}

function resetConfiguration(event) {
    if (!confirm('<span tal:replace="my/plugin/%app.note.plugin.confirmReset"></span>')) {
        return;
    }
    displayConfigurationForm(this.form.pluginId.value, true);
    window.scrollTo(0, 0);
}

function submitConfigurationForm(event) {
    var form = this.form;

    window.scrollTo(0, 0);
    new Ajax.Updater(
        'notes',
        '<span tal:replace="page:%/plugin.updateConfiguration.do"></span>',
        {
            method: 'post',
            postBody: Form.serialize(form)
        });
}

function expandTextBox(event) {
    var field = findFirstChild(this.parentNode);
    var newField;
    if (this.value == '+') {
        newField = document.createElement('textarea');
        newField.rows = 10;
        newField.style.verticalAlign = 'top';
        newField.innerHTML = field.value.escapeHTML();
        this.value = '-';
    } else {
        newField = document.createElement('input');
        Element.addClassName(newField, 'text');
        newField.type = 'text';
        newField.value = field.innerHTML.unescapeHTML();
        this.value = '+';
    }
    newField.id = field.id;
    newField.name = field.name;
    field.parentNode.insertBefore(newField, field);
    field.parentNode.removeChild(field);
}

function deleteProperty(event) {
    var li = this.parentNode.parentNode;
    if (!confirm('<span tal:replace="my/plugin/%app.note.plugin.confirmDelete"></span>')) {
        return;
    }
    Element.remove(li);
}

function findFirstChild(element) {
    var children = element.childNodes;
    var length = children.length;
    for (var i = 0; i < length; i++) {
        var item = children.item(i);
        if (item.nodeType == 1) {
            return item;
        }
    }
    return null;
}

function addProperty(event) {
    var li = this.parentNode.parentNode;
    var ul = li.parentNode;
    var indexElement = findFirstChild(li);
    var currentName = indexElement.name + "[" + indexElement.value + "]";
    var nextIndex = parseInt(indexElement.value) + 1;
    var nextName = indexElement.name + "[" + nextIndex + "]";
    var newLi = document.createElement('li');
    newLi.innerHTML = replaceString(li.innerHTML, '="' + currentName, '="' + nextName);

    var nextLi = li.nextSibling;
    if (nextLi) {
        ul.insertBefore(newLi, nextLi);
    } else {
        ul.appendChild(newLi);
    }

    removeElement(this);

    addButtons = $A(document.getElementsByClassName('add', newLi));
    addButtons.each(function (addButton) {
        addButton.clickHandler = addProperty.bindAsEventListener(addButton);
        Event.observe(addButton, 'click', addButton.clickHandler);
    });

    var deleteButtons = $A(document.getElementsByClassName('delete', newLi));
    deleteButtons.each(function (deleteButton) {
        deleteButton.clickHandler = deleteProperty.bindAsEventListener(deleteButton);
        Event.observe(deleteButton, 'click', deleteButton.clickHandler);
    });

    findFirstChild(newLi).value = nextIndex;
    Element.addClassName(newLi, 'prototype');
    Element.removeClassName(li, 'prototype');
}

function removeElement(element) {
    if (element.clickHandler) {
        Event.stopObserving(element, 'click', element.clickHandler);
    }
    Element.remove(element);
}

function replaceString(string, from, to) {
    var pre = 0;
    var idx;
    var result = "";
    while ((idx = string.indexOf(from, pre)) >= 0) {
        result += string.substring(pre, idx);
        result += to;
        pre = idx + from.length;
    }
    result += string.substring(pre);
    return result;
}

// install

function prepareForInstallSetting() {
	showSearchDialog();
}

function showSearchDialog() {
    $('pluginInstallForm').innerHTML = "Loading ...";
    var myAjax = new Ajax.Updater(
        'pluginInstallForm',
        '<span tal:replace="page:%/plugin-install.do"></span>',
        {
            method: 'get',
            onComplete: attachEventSearchPlugin
        }
    );
}

function attachEventSearchPlugin() {
    Event.observe($('searchPluginForInstall'), 'click', searchPluginForInstall, false);
}

function searchPluginForInstall() {
	var searchPluginId = $('searchPluginId').value;
	var excludeSnapshotForInstall = $('excludeSnapshotForInstall').checked;
    $('pluginInstallForm').innerHTML = "Loading ...";
    var params = 'pluginId=' + encodeURIComponent(searchPluginId);
    params += '&excludeSnapshot=' + excludeSnapshotForInstall;
    var myAjax = new Ajax.Updater(
        'pluginInstallForm',
        '<span tal:replace="page:%/plugin-install.searchPluginForInstall.do"></span>',
        {
            method: 'post',
            parameters: params,
            onComplete: onCompleteSearchPluginForInstall
        }
    );
}

function onCompleteSearchPluginForInstall(transport, param) {
    var doInstallPlugin = $('doInstallPlugin');
	if (doInstallPlugin) {
        Event.observe(doInstallPlugin, 'click', installPlugin, false);
        Event.observe($('cancelInstallPlugin'), 'click', cancelInstallPlugin, false);
	} else {
		attachEventSearchPlugin();
	}
}

function cancelInstallPlugin() {
    Event.stopObserving($('doInstallPlugin'), 'click', installPlugin);
    Event.stopObserving($('cancelInstallPlugin'), 'click', cancelInstallPlugin);
    $('pluginInstallForm').innerHTML = "";
	showSearchDialog();
}

function installPlugin() {
    Event.stopObserving($('doInstallPlugin'), 'click', installPlugin);
    Event.stopObserving($('cancelInstallPlugin'), 'click', cancelInstallPlugin);
    var id = $('pluginIdForInstall').value;
    var version = $('pluginVersionForInstall').value;
	var excludeSnapshot = $('excludeSnapshotForInstall').value;
    $('pluginInstallForm').innerHTML = "Loading ...";
    var params = 'pluginId=' + encodeURIComponent(id);
    params += '&pluginVersion='+ encodeURIComponent(version);
    params += '&excludeSnapshot=' + encodeURIComponent(excludeSnapshot);
    var myAjax = new Ajax.Updater(
        'pluginInstallForm',
        '<span tal:replace="page:%/plugin-install.installPlugin.do"></span>',
        {
            method: 'post',
            parameters: params
        }
    );
}

// repositories

function prepareForRepositorySetting() {
    var openNewRepoPopup = function () {
        var popup = new LITBox('<span tal:replace="page:%/plugin-repository.enterNewRepository.do"></span>',
        {
            type: 'window',
            width: 500,
            height: 300,
            draggable: true,
            resizable: true
        });
        popup.doAddRepository = function() {
            $('newRepositoryNotes').innerHTML = "";
            var params = 'repositoryUrl=' + encodeURIComponent($('repositoryUrl').value);
            params += '&repositoryId='+ encodeURIComponent($('repositoryId').value);
            var myAjax = new Ajax.Request(
                '<span tal:replace="page:%/plugin-repository.addRepository.do"></span>',
                {
                    method: 'post',
                    parameters: params,
                    onComplete: popup.onCompleteAddRepository
                }
            );
        };
        popup.onCompleteAddRepository = function (originalRequest) {
            var txt = originalRequest.responseText;
            // error
            if (0 < txt.length) {
                $('newRepositoryNotes').innerHTML = txt;
                return;
            }
            popup.remove();
            listRepositoties();
        };
        Event.observe($('addRepository'), 'click', popup.doAddRepository, false);
    }
    Event.observe($('openNewRepositoryPopup'), 'click', openNewRepoPopup);
    listRepositoties();
}

function listRepositoties() {
    $('repositories-form').innerHTML = "Loading ...";
    var myAjax = new Ajax.Updater(
        'repositories-form',
        '<span tal:replace="page:%/plugin-repository.listRepositoties.do"></span>',
        {
            method: 'get',
            onComplete: attachEventForRepositories
        }
    );
}

function attachEventForRepositories() {
    var table = $('repositoriesTable');
    var edits = document.getElementsByClassName('repositoryEdit', table);
    edits.each(function(editLink) {
        var repoId = editLink.id.substring('editRepo_'.length);
        editLink.repoId = repoId;
        var openEditRepoPopup = function () {
            var popup = new LITBox(
            '<span tal:replace="page:%/plugin-repository.enterEditRepository.do"></span>'
                + '?repositoryId=' + encodeURIComponent(repoId),
            {
                type: 'window',
                width: 500,
                height: 300,
                draggable: true,
                resizable: true
            });
            popup.doEditRepository = function () {
                $('editRepositoryNotes').innerHTML = "";
                var params = 'repositoryId=' + encodeURIComponent($('repositoryId').value);
                params += '&repositoryUrl='+ encodeURIComponent($('repositoryUrl').value);
                params += '&repositoryEnabled=' + $('repositoryEnabled').checked;
                params += '&targetRepositoryId='+ encodeURIComponent($('targetRepositoryId').value);
                var myAjax = new Ajax.Request(
                    '<span tal:replace="page:%/plugin-repository.updateRepository.do"></span>',
                    {
                        method: 'post',
                        parameters: params,
                        onComplete: popup.onCompleteUpdateRepository
                    }
                );
            };
            popup.onCompleteUpdateRepository = function (originalRequest) {
                var txt = originalRequest.responseText;
                // error
                if (0 < txt.length) {
                    $('editRepositoryNotes').innerHTML = txt;
                    return;
                }
                popup.remove();
                listRepositoties();
            };
            popup.doDeleteRepository = function () {
                var params = 'targetRepositoryId=' + encodeURIComponent($('targetRepositoryId').value);
                var myAjax = new Ajax.Request(
                    '<span tal:replace="page:%/plugin-repository.deleteRepository.do"></span>',
                    {
                        method: 'post',
                        parameters: params,
                        onComplete: popup.onCompleteDeleteRepository
                    }
                );
            }
            popup.onCompleteDeleteRepository = function (originalRequest) {
                var txt = originalRequest.responseText;
                // error
                if (0 < txt.length) {
                    $('editRepositoryNotes').innerHTML = txt;
                    return;
                }
                popup.remove();
                listRepositoties();
            }
            Event.observe($('updateRepository'), 'click', popup.doEditRepository, false);
            Event.observe($('deleteRepository'), 'click', popup.doDeleteRepository, false);
        }
        Event.observe(editLink, 'click', openEditRepoPopup, false);
    });
}

// update

function prepareForUpdateSetting() {
    listPlugins();
}

function listPlugins() {
    $('pluginUpdateForm').innerHTML = "Loading ...";
    var myAjax = new Ajax.Updater(
        'pluginUpdateForm',
        '<span tal:replace="page:%/plugin-update.listPlugins.do"></span>',
        {
            method: 'get',
            onComplete: onCompleteListPlugins
        }
    );
}

function onCompleteListPlugins() {
    Event.observe($('searchPluginForUpdate'), 'click', searchPluginForUpdate, false);
}

function searchPluginForUpdate() {
    var params = 'excludeSnapshot=' + encodeURIComponent($('excludeSnapshotForUpdate').checked);
    $('pluginUpdateForm').innerHTML = "Loading ...";
    var myAjax = new Ajax.Updater(
        'pluginUpdateForm',
        '<span tal:replace="page:%/plugin-update.searchPluginForUpdate.do"></span>',
        {
            method: 'get',
            parameters: params,
            onComplete: onCompleteCheckUpdate
        }
    );
}

function onCompleteCheckUpdate(transport, param) {
    var updatePlugin = $('updatePlugin');
    if (updatePlugin) {
        Event.observe(updatePlugin, 'click', doUpdatePlugin, false);
    }
    Event.observe($('backToListPlugins'), 'click', listPlugins, false);
}

function doUpdatePlugin() {
    Event.stopObserving($('updatePlugin'), 'click', doUpdatePlugin);
    var params = 'excludeSnapshot=' + $('excludeSnapshotForUpdate').value;
    $('pluginUpdateForm').innerHTML = "Loading ...";
    var myAjax = new Ajax.Updater(
        'pluginUpdateForm',
        '<span tal:replace="page:%/plugin-update.updatePlugin.do"></span>',
        {
            method: 'post',
            parameters: params
        }
    );
}
