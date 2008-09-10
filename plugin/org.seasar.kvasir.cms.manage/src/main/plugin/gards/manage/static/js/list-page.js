isSelected = false;


function toggleSelect(form)
{
    if (isSelected == false) {
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
