package ui.menu;

public class MainMenu extends MenuItem {
    public MainMenu(String i_Title) {
        super(i_Title);

    }

    public void Exit() {
        super.Exit();
        System.out.println("~~ GoodBye!");
        pressToContinue();
    }
}
