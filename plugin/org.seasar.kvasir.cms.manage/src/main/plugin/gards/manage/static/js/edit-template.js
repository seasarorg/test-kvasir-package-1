function setCommand(form, command)
{
    form.elements["command"].value = command;
    return true;
}


function deleteTemplate(form)
{
    if (confirm("現在のテンプレートを削除して"
    + "デフォルトのテンプレートを使うように戻します。よろしいですか？")) {
        return setCommand(form, "delete");
    } else {
        return false;
    }
}
