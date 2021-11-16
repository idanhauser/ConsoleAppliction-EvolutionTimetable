package time.table.problem.configurations;

import evolution.engine.Individual;
import time.table.problem.jaxb.schema.generated.ETTSelection;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Selection implements Serializable
{
    private static final long serialVersionUID = 100L;
    private  float m_topPercent;
    private type Etype;

    public Selection(ETTSelection ettSelection)
    {
        int indexEquals = ettSelection.getConfiguration().indexOf("=");
        String number = ettSelection.getConfiguration().substring(++indexEquals);
        int percent = Integer.parseInt(number);
        m_topPercent = (float)(percent / 100f);
        Etype = type.valueOf(ettSelection.getType());
    }

    public List<Individual> doSelection(List<Individual> i_individuals)
    {
        return Etype.Select(i_individuals, m_topPercent);
    }

    public enum type implements Serializable
    {
        Truncation
                {
                    @Override
                    public List<Individual> Select(List<Individual> i_individuals, float i_topPercent)
                    {
                        int newSize = (int)(i_individuals.size() * i_topPercent);
                        Comparator<Individual> comp = (ind1,ind2)-> ind1.compareFittness(ind2);
                        List<Individual> res = i_individuals.stream().sorted(comp).limit(newSize).collect(Collectors.toList());
                        return res;
                    }
                };
        // ,RouletteWheel
        public abstract List<Individual> Select(List<Individual> i_individuals, float i_topPercent);
    }

    public float getTopPercent() {
        return m_topPercent;
    }

    public type getType() {
        return Etype;
    }


}
