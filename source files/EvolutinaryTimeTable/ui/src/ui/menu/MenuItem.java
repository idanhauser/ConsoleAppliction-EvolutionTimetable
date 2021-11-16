package ui.menu;

import ui.menu.operations.ExitMenu;
import org.w3c.dom.ranges.RangeException;
import ui.menu.operations.ShowMenu;
import ui.menu.operations.toInvoke;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuItem {
    protected List<MenuItem> m_SubMenus = new ArrayList<MenuItem>();
    protected toInvoke m_Action;
    private String m_Title;
    private boolean m_ToExit = false;

    public MenuItem(String i_Title) {
        m_Title = i_Title;
    }

    protected static void pressToContinue() {
        System.out.println("Press any key continue.");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    public String getTitle() {
        return m_Title;
    }

    public void setTitle(String m_Title) {
        m_Title = m_Title;
    }

    public void Show() {
        final String tryAgain = "Please Try again.";
        m_ToExit = false;

        while (!m_ToExit) {
            try {
                printTitle();
                printMenu();
                int input = getIntInput();


                m_SubMenus.get(--input).m_Action.invoke();
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    public MenuItem addSubMenu(MenuItem i_SubMenu) {
        initDefaultsActions(i_SubMenu);
        m_SubMenus.add(i_SubMenu);
        return i_SubMenu;
    }

    private void initDefaultsActions(MenuItem i_NewMenuItem) {
        if (i_NewMenuItem.m_Action.getClass() == ShowMenu.class) {
            ((ShowMenu) i_NewMenuItem.m_Action).setMenuToShow(i_NewMenuItem);
        } else if (i_NewMenuItem.m_Action.getClass() == ExitMenu.class) {
            ((ExitMenu) i_NewMenuItem.m_Action).setItem(this);
        }
    }

    private void printTitle() {
        System.out.println(m_Title);
        System.out.println("=====================================");
    }

    private void printMenu() {
        System.out.println("Please choose one of the following options(Enter 1 to " + m_SubMenus.size() + "): ");
        int index = 1;
        for (MenuItem currentItem : m_SubMenus) {
            System.out.println(index + ")" + currentItem.m_Title);
            index++;
        }
    }

    private int getIntInput() throws NumberFormatException, RangeException {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.next();
        try {
            int intInput = Integer.parseInt(input);
            if (intInput < 1 || intInput > m_SubMenus.size()) {
                throw new RangeException((short) 1, "Please enter number in the range of 1 - " + m_SubMenus.size());
            }
        } catch (NumberFormatException ex) {
            throw new RangeException((short) 1, "You can't enter a word." +
                    System.lineSeparator() + "Please enter number in the range of 1 - " + m_SubMenus.size());
        }
        return Integer.parseInt(input);
    }

    public void Exit() // virtual?
    {
        m_ToExit = true;
    }
}
