function setCommand(form, command)
{
    form.elements["command"].value = command;
    return true;
}

function clearContents(form)
{
    if (confirm("このバリアントのコンテンツを削除します。履歴を含めて全て削除されます。よろしいですか？")) {
        return setCommand(form, "clear");
    } else {
        return false;
    }
}

function clearAllContents(form)
{
    if (confirm("全てのバリアントについてコンテンツを削除します。履歴を含めて全て削除されます。よろしいですか？")) {
        return setCommand(form, "clearAll");
    } else {
        return false;
    }
}
