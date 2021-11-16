package ui;

import data.transfer.objects.DTOEngineInformation;
import data.transfer.objects.GeneticPoolInformation;
import data.transfer.objects.TimeTableInformation;
import evolution.engine.EngineActions;
import evolution.engine.EvolutionEngine;
import evolution.engine.Population;
import javafx.util.Pair;
import org.w3c.dom.ranges.RangeException;
import time.table.problem.LoadData;
import time.table.problem.Quintet;
import time.table.problem.QuintetFactory;
import time.table.problem.VisualTimeTable;
import time.table.problem.configurations.EvolutionEngineInfo;
import time.table.problem.objects.StudyClass;
import time.table.problem.objects.TimeTable;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class EngineActionsImp implements EngineActions {
    private QuintetFactory m_Factory = null;
    private EvolutionEngine<Quintet> m_EvolutionEngine = null;
    private EvolutionEngineInfo m_EngineInfo = null;
    private TimeTable m_TimeTable = null;
    private Thread m_trEngine = null;
    private DTOEngineInformation<Quintet> m_DTOEngine = null;
    private boolean m_dataIsLoaded = false;

    @Override
    public void ReadFromFile() throws Exception {
        boolean isRunning = false;
        String loadAgain = "Y";
        Scanner scanner = new Scanner(System.in);
        if (m_trEngine != null && m_trEngine.isAlive()) {
            System.out.println("You have already loaded a data and you are running the evolution process ," +
                    System.lineSeparator() + "Are you sure you want to load data again? if you do enter 'Y', else enter any other key.");
            isRunning = true;
            loadAgain = scanner.next();
        }
        if ((m_dataIsLoaded) && (!isRunning)) {
            System.out.println("You have already loaded a data into the system," +
                    System.lineSeparator() + "Are you sure you want to load data again? if you do enter 'Y', else enter any other key.");
            loadAgain = scanner.next();
        }

        if (loadAgain.equals("Y")) {
            System.out.println("Please enter file name: ");
            String fileName = scanner.next();

            if (fileName.endsWith(".xml") == false) {
                throw new IOException("Prohibited file extension: file suffix must be in the format *.xml");
            }

            LoadData data = new LoadData(fileName);
            m_TimeTable = new TimeTable(data);
            m_EngineInfo = LoadData.getEngineInfo();
            m_Factory = new QuintetFactory(m_TimeTable);
            m_dataIsLoaded = true;
            m_DTOEngine = new DTOEngineInformation<>(m_EngineInfo);

            System.out.println("~~~~ Data was Loaded successfully! ~~~~");
            System.out.println();
        } else {
            System.out.println("Data was not loaded.");
            System.out.println();
        }
    }

    @Override
    public void ExecuteEvolution() throws Exception {
        Scanner scanner = new Scanner(System.in);
        if (m_dataIsLoaded) {
            if (m_DTOEngine.getBestResult() != null) {
                System.out.println("you have already executed the system, would you like to execute again? y = for yes, else = n ");
                String input = scanner.next();
                if (input.equals("n")) return;
            }

            if (m_trEngine != null) {
                if (m_trEngine.isAlive()) {
                    System.out.println("The engine is already running! are you sure you want to start over? y = for yes, else = n");
                    String input = scanner.next();
                    if (input.equals("n")) return;
                    m_trEngine.interrupt();
                }
            }

            if (m_dataIsLoaded) {
                String stringInput;
                int numberOfGenerationsToExit = 0;
                int numberOfGenerationToShow = 0;
                int fitnessToExit = 0;

                int i_ExitCondition = getExitCondition();
                if (i_ExitCondition == 1) {
                    fitnessToExit = 100;
                    while (numberOfGenerationsToExit < 100) {
                        System.out.println("Please enter the maximum number of generation (greater then 100) :");
                        stringInput = scanner.next();
                        try {
                            numberOfGenerationsToExit = Integer.parseInt(stringInput);
                        } catch (NumberFormatException ex) {
                            throw new Exception("Please enter a number which is bigger than 100.");
                        }
                    }
                } else {
                    numberOfGenerationsToExit = Integer.MAX_VALUE;
                    while (fitnessToExit <= 1 || fitnessToExit > 100) {
                        System.out.println("Please enter a minimum fitness value to stop (1-100) :");
                        fitnessToExit = scanner.nextInt();
                    }
                }

                while (numberOfGenerationToShow < 1) {
                    System.out.println("Please enter number of generation to show (greater then 0) :");
                    stringInput = scanner.next();
                    try {
                        numberOfGenerationToShow = Integer.parseInt(stringInput);
                    } catch (NumberFormatException ex) {
                        throw new Exception("Please enter a number which is bigger than 0.");
                    }
                }

                m_EvolutionEngine = new EvolutionEngine<>(m_Factory, m_EngineInfo, m_TimeTable.getGeneLength(), m_DTOEngine, numberOfGenerationsToExit, numberOfGenerationToShow, fitnessToExit);
                m_trEngine = new Thread(m_EvolutionEngine);
                m_trEngine.start();
                System.out.println("Evolution is running, you can check the process using options 4 or 5.");
            }
        } else {
            throw new Exception("Data is not loaded to the system. Please Load data first(option 1 or 6 in menu).");
        }
    }

    @Override
//Idan: I think this information need to be in engine? becuase think about the next ex.
    //this is the reason i made this DTO, but i will also write here all the  prints even tho, I don't think this is the right way.
    public void ShowDataInformation() throws Exception {
        DTOEngineInformation engingInfoDto = null;
        TimeTableInformation timeTableDto = null;

        if (m_dataIsLoaded) {

            engingInfoDto = new DTOEngineInformation(m_EngineInfo.getInitialPopulation(),
                    m_EngineInfo.getCrossover(), m_EngineInfo.getSelection(), m_EngineInfo.getMutations());
            timeTableDto = new TimeTableInformation(m_TimeTable.getDays(), m_TimeTable.getHours(), m_TimeTable.getSubjects(),
                    m_TimeTable.getStudyClasses(), m_TimeTable.getTeachers(), m_TimeTable.getRules());


            printTimeTableInformation(timeTableDto);
            System.out.println("==============================");
            printEngineInformation(engingInfoDto);
            System.out.println("==============================");
            System.out.print(System.lineSeparator());
        } else {
            throw new Exception("Data is not loaded to the system. Please Load data first(option 1 in menu).");
        }
    }

    private void printEngineInformation(DTOEngineInformation engingInfoDto) {
        System.out.println("-----------------------------");
        System.out.println("  Engine Information");
        System.out.println("-----------------------------");
        System.out.print(System.lineSeparator());
        System.out.println("Size of population:");
        System.out.println("--------------------");
        System.out.println(engingInfoDto.getInitialPopulation());
        System.out.print(System.lineSeparator());
        System.out.println("Selection:");
        System.out.println("--------------------");
        System.out.println(engingInfoDto.getSelection().getType().toString() + ":" + engingInfoDto.getSelection().getTopPercent() + "%");
        System.out.print(System.lineSeparator());
        System.out.println("CrossOver:");
        System.out.println("--------------------");
        System.out.println(engingInfoDto.getCrossOver().toString());
        System.out.print(System.lineSeparator());
        System.out.println("Mutation:");
        System.out.println("--------------------");
        engingInfoDto.getMutations().forEach(mutation -> {
            System.out.println(mutation.toString() + System.lineSeparator());
        });
    }


    public void printTimeTableInformation(TimeTableInformation i_timeTableDto) {
        System.out.println();
        System.out.println("-----------------------------");
        System.out.println("  Time Table Information");
        System.out.println("-----------------------------");


        System.out.println("Subjects:");
        System.out.print("-----------");

        System.out.print(System.lineSeparator());
        i_timeTableDto.getSubjects().values().forEach(subject ->
                System.out.println(subject.getId() + " : " + subject.getName()));
        System.out.print(System.lineSeparator());
        System.out.println("Teachers:");
        System.out.println("-----------");

        i_timeTableDto.getSubjects().values().forEach(teacher ->
                System.out.println(teacher.getId() + " : " + teacher.getName()));
        System.out.print(System.lineSeparator());
        System.out.println("Classes:");
        System.out.println("-----------");

        i_timeTableDto.getStudyClasses().values().forEach(this::PrintStudyClass);
        System.out.print(System.lineSeparator());
        System.out.println("Rules:");
        System.out.println("-----------");

        i_timeTableDto.getRules().values().forEach(t -> System.out.println(t.toString()));
        System.out.println();

    }

    private void PrintStudyClass(StudyClass studyClass) {
        System.out.println(studyClass.getId() + " : " + studyClass.getName());
        studyClass.getRequirements().forEach(req ->
                System.out.println("Subject " + req.getSubject().toString() + " And " + req.getHours() + " are required."));
    }


    public void showProcess() throws Exception {

        if (m_trEngine == null) {
            System.out.println("please execute the evolution engine before use this function");
            return;
        }
        int len;
        Map<Integer, Pair<Integer, Population>> historyPopulation = null;
        int prevFitness = -1;
        if (m_dataIsLoaded) {
            try {

                GeneticPoolInformation<Quintet> dataToShow = m_EvolutionEngine.requestGeneticsPool();
                historyPopulation = dataToShow.getHistoryOfGenerations();


                System.out.println("§ == Genetics pool == §");
                System.out.println("-------------------------");
                System.out.println("Showing population every " + dataToShow.getGensToShow() + " generations.");

                if (m_trEngine.isAlive()) {
                    if (historyPopulation.size() >= 10) {
                        len = 10;
                    } else {
                        len = historyPopulation.size();
                    }
                } else {
                    len = historyPopulation.values().size();
                }
                System.out.println("showing you " + len + " generations:");
                System.out.println("-------------------------------");
                System.out.println("Generations:");
                for (Pair<Integer, Population> populationPair : historyPopulation.values()) {
                    if (len == 0) {
                        break;
                    }
                    len--;
                    Population population = populationPair.getValue();
                    System.out.println("Generation #" + populationPair.getKey() + ":");
                    System.out.println("----------------------");
                    System.out.println("The Best fitness in this generation is: " + population.getFittestScore().getFitness());
                    if (prevFitness != -1) {
                        System.out.println("Offset from previous saved generation: " + (population.getFittestScore().getFitness() - prevFitness));
                    }

                    prevFitness = population.getFittestScore().getFitness();
                }
            } catch (Exception ex) {
                System.out.println("There was an error in showing the process: "
                        + System.lineSeparator() + ex.getMessage());
            }
        } else {
            throw new Exception("Data is not loaded to the system. Please Load data first(option 1 or 6 in menu).");
        }
        System.out.println();
    }

    @Override
    public void ShowBestSolution() throws Exception {

        if (m_trEngine == null) {
            System.out.println("please execute the evolution engine before use this function");
            return;
        }

        if (m_dataIsLoaded) {

            int input = 0;
            while (input < 1 || input > 3) {
                System.out.println("please choose one of the following modes:");
                System.out.println("1. raw mode");
                System.out.println("2. Teacher mode");
                System.out.println("3. Class mode");
                Scanner scanner = new Scanner(System.in);
                input = scanner.nextInt();
            }

            if (m_trEngine.isAlive()) {
                System.out.println("Engine current generation: " + m_EvolutionEngine.getGenerationCount());
            }
            System.out.println("Best solution found in gen: " + m_DTOEngine.getBestGen());
            System.out.println("The fitness of this solution is: " + m_DTOEngine.getBestFitness() + System.lineSeparator());
            List<Quintet> res = m_DTOEngine.getBestResult();
            VisualTimeTable timeTableToShow = new VisualTimeTable(res, m_TimeTable.getDays(), m_TimeTable.getHours());


            if (input == 1)
                res.forEach(q -> System.out.println(q));

            if (input == 2)
                m_TimeTable.getTeachers().entrySet().forEach(t ->
                {
                    System.out.println("====" + t.getValue().getName());
                    System.out.println(timeTableToShow.showAsTeacher(t.getValue()));
                });

            if (input == 3)
                m_TimeTable.getStudyClasses().entrySet().forEach(s ->
                {
                    System.out.println("====" + s.getValue().getName());
                    System.out.println(timeTableToShow.showAsClass(s.getValue()));

                });

        } else {
            throw new Exception("Data is not loaded to the system. Please Load data first(option 1 or 6 in menu).");
        }
    }

    @Override
    public void SaveSystemToFile() throws Exception {
        Scanner scanner = new Scanner(System.in);
        if (m_trEngine.isAlive()) {
            throw new Exception("Evolution engine is still working, please try after its done.");
        }
        System.out.println("Please enter the path where you want to save your system + name of the file:");
        String pathFile = scanner.next();
        if (m_dataIsLoaded == true) {
            try {
                FileOutputStream fos = new FileOutputStream(pathFile);
                ObjectOutputStream outputStream = new ObjectOutputStream(fos);
                outputStream.writeObject(m_TimeTable);
                outputStream.writeObject(m_EvolutionEngine);
                outputStream.writeObject(m_Factory);
                outputStream.writeObject(m_EngineInfo);
                outputStream.close();
            } catch (IOException ex) {
                throw new IOException(ex.getMessage() + System.lineSeparator() + " Could not save the system to file.");
            }
        } else {
            throw new NotSerializableException("The system is empty, you might load the data first (option 1 or 6 in the menu).");
        }
        System.out.println("System saved to file successfully at " + System.lineSeparator() + pathFile);
    }

    @Override
    public void LoadFileToSystem() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter the path where is the file:");
        String filePath = scanner.next();
        try {
            FileInputStream fis = new FileInputStream(filePath);
            ObjectInputStream inputStream = new ObjectInputStream(fis);
            m_TimeTable = null;
            m_TimeTable = (TimeTable) inputStream.readObject();
            m_EvolutionEngine = null;
            m_EvolutionEngine = (EvolutionEngine<Quintet>) inputStream.readObject();
            m_Factory = null;
            m_Factory = (QuintetFactory) inputStream.readObject();
            m_EngineInfo = null;
            m_EngineInfo = (EvolutionEngineInfo) inputStream.readObject();
            inputStream.close();
            System.out.println("File loaded to system successfully!");
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println(ex.getMessage() + " Could not load the system to file.");
        }
        m_dataIsLoaded = true;
    }


    private int getExitCondition() {
        int input = 0;
        Scanner scanner = new Scanner(System.in);
        String inputStr;
        System.out.println("Please choose one of the following options:");
        System.out.println("1) Stop by generation");
        System.out.println("2) Stop by fitness");

        inputStr = scanner.next();
        try {
            int intInput = Integer.parseInt(inputStr);
            if (intInput < 1 || intInput > 2) {
                throw new RangeException((short) 1, "Please choose one of the conditions: 1 or 2.");
            }
        } catch (NumberFormatException ex) {
            throw new RangeException((short) 1, "You can't enter a word." +
                    System.lineSeparator() + "Please choose one of the conditions: 1 or 2.");
        }
        return Integer.parseInt(inputStr);
    }
}