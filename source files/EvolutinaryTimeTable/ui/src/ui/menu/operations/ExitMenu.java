package ui.menu.operations;

import ui.menu.MenuItem;

public class ExitMenu implements toInvoke {
    private MenuItem m_item;

    public void invoke() throws NullPointerException {
        if (m_item == null) {
            throw new NullPointerException("menu item isn't initialize");
        }

        m_item.Exit();
    }

    public void setItem(MenuItem m_item) {
        this.m_item = m_item;
    }
}
