package data.transfer.objects;

import java.util.List;

public interface UIAdapter<T>
{
     void AlgorithmStart();
     void AlgorithmPause();
     void AlgorithmResume();
    void UpdateProgressbar(double Generation, double Fitness, double Time);
    void UpdatePopulationAndFitness(int PopulationInformation, int FitnessInformation);
    void UpdateBestTable(List<T> arrangeByHoursDays);
}
