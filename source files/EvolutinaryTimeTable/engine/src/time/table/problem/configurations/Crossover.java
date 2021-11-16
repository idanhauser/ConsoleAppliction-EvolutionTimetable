package time.table.problem.configurations;

import evolution.engine.Individual;
import time.table.problem.Quintet;
import time.table.problem.jaxb.schema.generated.ETTCrossover;

import java.io.Serializable;
import java.util.*;

public class Crossover implements Serializable {
    private static final long serialVersionUID = 100L;
    int m_cuttingPoint;
    type Etype;

    public Crossover(ETTCrossover ettCrossOver) {
        m_cuttingPoint = ettCrossOver.getCuttingPoints();
        Etype = type.valueOf(ettCrossOver.getName());
    }

    public type getEtype() {
        return Etype;
    }

    public int getCuttingPoint() {
        return m_cuttingPoint;
    }

    public List<Individual> runCrossover(List<Individual> individuals, int initialPopulation) throws Exception {
        int initPopulationSize = initialPopulation;
        List<Individual> offSprings = new ArrayList<>(initialPopulation);
        individuals.forEach(individual -> getEtype().initSort(individual));
        List<Integer> randomizedCuttingPoints = new ArrayList<>(m_cuttingPoint);
        Random rand = new Random();
        int randIdx;
        boolean first = true;
        try {
            while (offSprings.size() < initPopulationSize) {
                int idxParent1 = rand.nextInt(individuals.size());
                int idxParent2 = rand.nextInt(individuals.size());
                first = true;
                int maxLimit = Math.max(individuals.get(idxParent1).getGeneListLength(), individuals.get(idxParent2).getGeneListLength());
                for (int j = 0; j < m_cuttingPoint; j++) {

                    if (first) {
                        randIdx = rand.nextInt(maxLimit);
                    } else {
                        randIdx = rand.nextInt(maxLimit - randomizedCuttingPoints.get(j - 1));
                    }


                    if (randomizedCuttingPoints.size() <= j) {
                        randomizedCuttingPoints.add(randIdx);
                    } else {
                        if (first) {
                            randomizedCuttingPoints.set(j, randIdx);
                        } else {
                            randomizedCuttingPoints.set(j, randIdx + randomizedCuttingPoints.get(j - 1));
                        }

                    }
                    first = false;
                }
                Collections.sort(randomizedCuttingPoints);
                //maybe we can put here a thread?
                offSprings.addAll((List<Individual<Quintet>>) makeCrossover(individuals.get(idxParent1), individuals.get(idxParent2), randomizedCuttingPoints));

            }


        } catch (Exception ex) {
            throw new Exception("There was some problem in runCrossover method: " + ex.getMessage());
        }


        return offSprings;

    }


    private List<Individual<Quintet>> makeCrossover(Individual<Quintet> ind1, Individual<Quintet> ind2, List<Integer> cuttingPoints) throws Exception {
        List<Quintet> genesParent1 = ind1.getListOfGenes();
        List<Quintet> genesParent2 = ind2.getListOfGenes();
        List<Individual<Quintet>> newDescendants = new ArrayList<Individual<Quintet>>(2);
        int maxGenes = Math.max(genesParent2.size(), genesParent1.size());
        try {
            newDescendants.add(0, new Individual<>(ind1.getIndividualFactory(), ind1.getRules(), maxGenes));
            newDescendants.add(1, new Individual<>(ind1.getIndividualFactory(), ind1.getRules(), maxGenes));

            List<Quintet> genesDescendant1 = new ArrayList<>();
            List<Quintet> genesDescendant2 = new ArrayList<>();
            int lastValue = -1;
            boolean first = true;
            int reader1 = 0;
            int reader2 = 0;
            for (int i = 0; i < m_cuttingPoint; i++) {
                if (first) {
                    Procreation(genesParent2, genesParent1, genesDescendant1, genesDescendant2, cuttingPoints.get(i), reader1, reader2);
                } else {
                    Procreation(genesParent1, genesParent2, genesDescendant1, genesDescendant2, cuttingPoints.get(i), reader1, reader2);
                }
                reader1 += cuttingPoints.get(i);
                reader2 += cuttingPoints.get(i);
                first = !first;
            }
            if (cuttingPoints.stream().mapToInt(Integer::intValue).sum() < maxGenes) {
                if (first) {
                    Procreation(genesParent2, genesParent1, genesDescendant1, genesDescendant2, ind1.getGeneListLength() - cuttingPoints.stream().mapToInt(Integer::intValue).sum(), reader1, reader2);
                } else {
                    Procreation(genesParent1, genesParent2, genesDescendant1, genesDescendant2, ind1.getGeneListLength() - cuttingPoints.stream().mapToInt(Integer::intValue).sum(), reader1, reader2);
                }
            }
            newDescendants.get(0).setListOfGenes(genesDescendant1);
            newDescendants.get(1).setListOfGenes(genesDescendant2);
        } catch (Exception ex) {
            throw new Exception("There was some problem in makeCrossover method: " + ex.getMessage());
        }

        return newDescendants;
    }

    //In case one list is longer than the other - > it will put all the genes in the descedant.
    private void Procreation(List<Quintet> genesParent1, List<Quintet> genesParent2, List<Quintet> genesDescendant1, List<Quintet> genesDescendant2, int times, int reader1, int reader2) {
        for (int i = 0; i < times; i++) {
            if (genesParent2.size() > reader2) {
                genesDescendant1.add(genesParent2.get(reader2));
            } else if (genesParent1.size() > reader1) {
                genesDescendant1.add(genesParent1.get(reader1));
            }
            if (genesParent1.size() > reader1) {
                genesDescendant2.add(genesParent1.get(reader1));
            } else if (genesParent2.size() > reader2) {
                genesDescendant2.add(genesParent2.get(reader2));
            }
            reader1++;
            reader2++;
        }
    }

    @Override
    public String toString() {
        return "Name: " + this.Etype + ", Number of cutting points: " + this.m_cuttingPoint;
    }


    public enum type implements Serializable {

        DayTimeOriented {
            public void initSort(Individual<Quintet> solution) {
                List<Quintet> listOfGenes;
                Map<Integer, List<Quintet>> dayHourSorting = new TreeMap<>();
                listOfGenes = solution.getListOfGenes();
                //listOfGenes.stream().sorted();
                Quintet currentQuintet;
                for (int i = 0; i < listOfGenes.size(); i++) {
                    currentQuintet = listOfGenes.get(i);
                    if (dayHourSorting.containsKey(currentQuintet.getDay())) {
                        dayHourSorting.get(currentQuintet.getDay()).add(currentQuintet);
                    } else {
                        List<Quintet> quintetList = new ArrayList<>();
                        quintetList.add(currentQuintet);
                        dayHourSorting.put(currentQuintet.getDay(), quintetList);
                    }
                }

                for (List<Quintet> day : dayHourSorting.values()) {
                    day.sort(new Comparator<Quintet>() {
                        @Override
                        public int compare(Quintet q1, Quintet q2) {
                            return (int) (q1.getHour() - q2.getHour());
                        }
                    });
                }
                //  listOfGenes = new ArrayList(dayHourSorting.values());
                listOfGenes = new ArrayList<Quintet>(solution.getGeneListLength());
                for (List<Quintet> quintetList : dayHourSorting.values()) {
                    listOfGenes.addAll(quintetList);
                }
                solution.setListOfGenes(listOfGenes);


            }

        };
        private static final long serialVersionUID = 100L;
        public abstract void initSort(Individual<Quintet> solution);
    }

}