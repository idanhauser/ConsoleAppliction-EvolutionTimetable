package ui.menu.operations;

import ui.menu.MenuItem;

public class ShowMenu implements toInvoke {
    private MenuItem m_MenuToShow;

    public ShowMenu() {
        m_MenuToShow = null;
    }

    public void invoke() throws Exception {
        if (m_MenuToShow == null) {
            throw new Exception("menu item isn't initialize");
        }

        m_MenuToShow.Show();
    }

    public void setMenuToShow(MenuItem m_MenuToShow) {
        this.m_MenuToShow = m_MenuToShow;
    }

}
