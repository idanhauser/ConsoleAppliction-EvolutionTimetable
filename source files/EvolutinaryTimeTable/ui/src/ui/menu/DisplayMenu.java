package ui.menu;

import ui.menu.operations.ShowMenu;

public class DisplayMenu extends MenuItem {
    public DisplayMenu(String i_Title) {
        super(i_Title);
        m_Action = new ShowMenu();
    }
}
