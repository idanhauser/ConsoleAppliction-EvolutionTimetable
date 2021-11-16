package time.table.problem.configurations.rules;

import evolution.engine.Individual;
import time.table.problem.Quintet;
import time.table.problem.QuintetFactory;
import time.table.problem.objects.Requirement;
import time.table.problem.objects.StudyClass;
import time.table.problem.objects.Subject;
import time.table.problem.objects.Teacher;

import java.util.*;
import java.util.stream.Stream;

public enum RuleIdEnum
{
    TeacherIsHuman {
        public <T> int Eval(Individual<T> individual) {
            int res = individual.getGeneListLength();
            int maxDays =((QuintetFactory)individual.getIndividualFactory()).getTotalDays();
            int maxHours =((QuintetFactory)individual.getIndividualFactory()).getTotalHours();
            List<Teacher>[][] TeacherArr =  new ArrayList[maxHours][maxDays];
            List<Quintet> list = (List<Quintet>) individual.getListOfGenes();

            for (Quintet quintet : list)
            {
                List<Teacher> curTeacher = TeacherArr[quintet.getTotalHours() - 1][quintet.getDay() - 1];

                if(curTeacher == null)
                {
                    TeacherArr[quintet.getTotalHours() - 1][quintet.getDay() - 1] = new ArrayList<>();
                    TeacherArr[quintet.getTotalHours() - 1][quintet.getDay() - 1].add(quintet.getTeacher());
                }
                else
                {
                    if(curTeacher.contains(quintet.getTeacher()) == true)
                    {
                        res--;
                    }
                    else
                    {
                        curTeacher.add(quintet.getTeacher());
                    }
                }
            }
            return (int)(((float)res/(float)individual.getGeneListLength())*100f);
        }
    },

    Singularity {
        public <T> int Eval(Individual<T> individual)
        {
            int res = individual.getGeneListLength();
            int maxDays =((QuintetFactory)individual.getIndividualFactory()).getTotalDays();
            int maxHours =((QuintetFactory)individual.getIndividualFactory()).getTotalHours();
            List<StudyClass>[][] ClassArr =  new ArrayList[maxHours][maxDays];
            List<Quintet> list = (List<Quintet>) individual.getListOfGenes();

            for (Quintet quintet : list)
            {
                List<StudyClass> curClass = ClassArr[quintet.getTotalHours() - 1][quintet.getDay() - 1];

                if(curClass == null)
                {
                    ClassArr[quintet.getTotalHours() - 1][quintet.getDay() - 1] = new ArrayList<>();
                    ClassArr[quintet.getTotalHours() - 1][quintet.getDay() - 1].add(quintet.getStudyClass());
                }
                else
                {
                    if(curClass.contains(quintet.getStudyClass()) == true)
                    {
                        res--;
                    }
                    else
                    {
                        curClass.add(quintet.getStudyClass());
                    }
                }
            }
            return (int)(((float)res/(float)individual.getGeneListLength())*100f);
        }
    },

    Knowledgeable {
        public <T> int Eval(Individual<T> individual)
        {
            int res = individual.getGeneListLength();
            List<Quintet> list = (List<Quintet>) individual.getListOfGenes();

            for (Quintet quintet : list) {

                if (!quintet.getTeacher().isTeachingSubject(quintet.getSubject())) {
                    res--;
                }
            }

            return (int)(((float)res/(float)individual.getGeneListLength())*100f);
        }
   },
    Satisfactory {
        public <T> int Eval(Individual<T> i_Individual) {
            int res = i_Individual.getGeneListLength();
            int maxStudyClasses = ((QuintetFactory) i_Individual.getIndividualFactory()).getTotalNumberOfClasses();
            Map<Integer, StudyClass> studyClassesById = ((QuintetFactory) i_Individual.getIndividualFactory()).getStudyClasses();
            List<Quintet> quintets = (List<Quintet>) i_Individual.getListOfGenes();

            for (StudyClass sClass : studyClassesById.values()) {
                List<Requirement> sClassReqs = sClass.getRequirements();
                Map<Subject, Integer> checkingReqs = new HashMap<>(sClassReqs.size());
                for (Requirement classReq : sClassReqs) {
                    checkingReqs.put(classReq.getSubject(), classReq.getHours());
                }

                Stream<Quintet> sClassQuintets = quintets.stream().filter(t -> t.getStudyClass().equals(sClass));
                sClassQuintets.filter(quintet -> checkingReqs.containsKey(quintet.getSubject())).forEach(quintet -> {
                    checkingReqs.replace(quintet.getSubject(), checkingReqs.get(quintet.getSubject()) - 1);
                });

                for (int hoursSubjectBeingTaught : checkingReqs.values()) {
                    if (hoursSubjectBeingTaught != 0) {
                        res--;
                    }
                }
            }
            return (int) (((float) res / (float) i_Individual.getGeneListLength()) * 100f);
        }
    };

    public abstract <T> int Eval(Individual<T> individual);
    private final static int m_MaxFitness = 100;
    public static int getMaxEval(){    return m_MaxFitness;    }
}
