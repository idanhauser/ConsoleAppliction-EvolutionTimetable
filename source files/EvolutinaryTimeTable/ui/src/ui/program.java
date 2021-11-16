package ui;

import com.sun.istack.internal.NotNull;
import evolution.engine.EngineActions;
import ui.menu.ActionMenu;
import ui.menu.MainMenu;
import ui.menu.operations.ExitMenu;

public class program {

    public static void main(String[] args) {
        MainMenu main = setMenus();
        main.Show();
    }

    private static @NotNull
    MainMenu setMenus() {
        EngineActions operation = new EngineActionsImp();
        MainMenu main = new MainMenu(" TimeTable Engine");
        main.addSubMenu(new ActionMenu(" Read TimeTable from file", operation::ReadFromFile));
        main.addSubMenu(new ActionMenu(" Show system and algorithm settings", operation::ShowDataInformation));
        main.addSubMenu(new ActionMenu(" Execute evolution algorithm", operation::ExecuteEvolution));
        main.addSubMenu(new ActionMenu(" Show the best solution", operation::ShowBestSolution));
        main.addSubMenu(new ActionMenu(" Show algorithm process", operation::showProcess));
        main.addSubMenu(new ActionMenu(" Load file to system", operation::LoadFileToSystem));
        main.addSubMenu(new ActionMenu(" Saving system to file", operation::SaveSystemToFile));
        main.addSubMenu(new ActionMenu(" Exit", new ExitMenu()));

        return main;
    }
}
