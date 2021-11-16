package ui.menu;

import ui.menu.operations.toInvoke;

public class ActionMenu extends MenuItem {
    public ActionMenu(String i_Title, toInvoke i_TheAction) {
        super(i_Title);
        m_Action = i_TheAction;
    }
}
