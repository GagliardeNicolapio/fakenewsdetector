package Controller;

import Controller.http.Controller;
import Model.Components.Alert;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.trees.J48;
import weka.core.*;
import weka.core.converters.ConverterUtils;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stopwords.Rainbow;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToWordVector;

@WebServlet(name = "TrainingServlet", value = "/trainingModel", loadOnStartup = 0)
public class TrainingServlet extends Controller {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            final int K_FOLDS = 10;
            long totalTime, startTime, endTime;
            startTime = System.nanoTime();
            //carico il dataset
            ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\dataset\\FakeAndTrueRandomWithCovidTest.arff");
            Instances instances = dataSource.getDataSet();

            // Utilizzo attuale dell'heap
            long heapSize = Runtime.getRuntime().totalMemory() / (1024 * 1024);

            // Get maximum size of heap in bytes. The heap cannot grow beyond this size.// Any attempt will result in an OutOfMemoryException.
            long heapMaxSize = Runtime.getRuntime().maxMemory() / (1024 * 1024);

            // Get amount of free memory within the heap in bytes. This size will increase // after garbage collection and decrease as new objects are created.
            long heapFreeSize = Runtime.getRuntime().freeMemory() / (1024 * 1024);

            System.out.println("Utilizzo heap attuale: "+heapSize+" MB"+
                    "\nMax Heap: "+heapMaxSize+" MB"+
                    "\nFree heap space: "+heapFreeSize+" MB");

            String preStat;
            //DATA CLEANING
            System.out.println("Numero instance data cleaning: "+instances.numInstances());
            System.out.println("Numero campi vuoti: "+instances.attributeStats(1).missingCount);
            preStat = "Numero instanze data cleaning: "+instances.numInstances()+
                    "\nNumero campi vuoti: "+instances.attributeStats(1).missingCount;
            instances.removeIf(Instance::hasMissingValue);
            System.out.println("Numero campi vuoti dopo remove: "+instances.attributeStats(1).missingCount);
            preStat = preStat + "\nNumero campi vuoti dopo remove: "+instances.attributeStats(1).missingCount;
            //FEATURE SELECTION
            //sono state gia tolte le colonne subject e date
            /*Remove remove = new Remove();
            remove.setOptions(new String[]{"-R","3"});
            remove.setInputFormat(instances);
            instances = Filter.useFilter(instances,remove);*/

            //STRING TO WORD VECTOR
            System.out.println("Inizio TF-IDF con StringToWordVector");
            preStat = preStat + "\nStart TF-IDF with StringToWordVector";
            StringToWordVector stringToWordVector = new StringToWordVector();
            stringToWordVector.setIDFTransform(true);
            stringToWordVector.setTFTransform(true);
            stringToWordVector.setAttributeIndices("first-last"); //tutti gli indici
            stringToWordVector.setStemmer(new SnowballStemmer());
            stringToWordVector.setStopwordsHandler(new Rainbow());
            WordTokenizer wordTokenizer = new WordTokenizer();
            wordTokenizer.setDelimiters(".,;:'\"()?!/ -_><&#");
            stringToWordVector.setTokenizer(wordTokenizer);
            stringToWordVector.setInputFormat(instances);
            stringToWordVector.setWordsToKeep(51000);
            instances = Filter.useFilter(instances,stringToWordVector);
            System.out.println("Fine TF-IDF con StringToWordVector");
            preStat = preStat + "\nFine TF-IDF con StringToWordVector";

            //setto il num di colonna della var target
            instances.setClassIndex(0);

            heapSize = Runtime.getRuntime().totalMemory() / (1024 * 1024);
            heapMaxSize = Runtime.getRuntime().maxMemory() / (1024 * 1024);
            heapFreeSize = Runtime.getRuntime().freeMemory() / (1024 * 1024);
            System.out.println("Utilizzo heap attuale preSelection: "+heapSize+" MB"+
                    "\nMax Heap preSelection: "+heapMaxSize+" MB"+
                    "\nFree heap space preSelection: "+heapFreeSize+" MB");

            //stampo il numero di parole trovate
            System.out.println("Numero di parole identificate: "+instances.numAttributes());
            preStat = preStat + "\nNumero di parole identificate: "+instances.numAttributes();
           /* long selectionStartTime = System.nanoTime();
            //ATTRIBUTE SELECTION
            System.out.println("Inizio selection");
            preStat = preStat + "\nInizio selection";
            AttributeSelection attributeSelection = new AttributeSelection();
            CfsSubsetEval eval = new CfsSubsetEval();
            BestFirst bestFirst = new BestFirst();
            attributeSelection.setSearch(bestFirst);
            attributeSelection.setEvaluator(eval);
            attributeSelection.SelectAttributes(instances);
            long selectionEndTime = System.nanoTime();
            long selectionTotalTime = selectionEndTime - selectionStartTime;
            System.out.println("Fine selection");
            preStat = preStat + "\nFine selection";
            System.out.println("Attribute selection running time: "+selectionTotalTime/1000000+" ms");
            preStat = preStat + "\nAttribute selection running time: "+selectionTotalTime/1000000+" ms";
            int[] indices = attributeSelection.selectedAttributes();
            System.out.println("Indici da conservare: "+Utils.arrayToString(indices));
            System.out.println("Numero indici(parole da utilizzare): "+indices.length);
            preStat = preStat + "\nIndici da conservare: "+Utils.arrayToString(indices)+
                                "\nNumero indici(parole da utilizzare): "+indices.length;
            writeIndices(indices); //salvo gli indici in un file

            heapSize = Runtime.getRuntime().totalMemory() / (1024 * 1024);
            heapMaxSize = Runtime.getRuntime().maxMemory() / (1024 * 1024);
            heapFreeSize = Runtime.getRuntime().freeMemory() / (1024 * 1024);
            System.out.println("Utilizzo heap attuale postSelection: "+heapSize+" MB"+
                    "\nMax Heap postSelection: "+heapMaxSize+" MB"+
                    "\nFree heap space postSelection: "+heapFreeSize+" MB");*/

            int[] indices = {1,1120,2312,2600,4385,5113,5118,5445,6215,7052,7538,8060,8874,9070,9310,11712,11716,12668,
                    14756,14951,16386,16457,16679,17163,17957,19363,19960,21768,22077,23468,24705,25229,27010,27750,28287,
                    28335,28443,28635,28840,29266,30176,30210,30426,30467,30540,30582,32001,34260,35644,36137,37578,37915,
                    38086,38474,38847,38849,38850,39525,39532,39872,39921,40553,40588,40673,41800,42709,43403,43531,44306,
                    44617,45246,45412,45417,45419,45526,46438,46775,46792,47027,47033,47205,47256,47268,47668,47682,47902,
                    47938,48212,48583,48934,49430,49565,49688,49737,50212,50536,50894,51158,51436,52003,52111,52630,53223,
                    53305,53322,53645,53647,54383,54464,54713,54744,54829,54866,54984,55360,56166,56641,56678,56865,57054,
                    57175,57194,57392,58021,58203,58432,58437,58756,58816,58824,58855,58930,0};
            //ATTRIBUTE REMOVE
            Remove removeFilter = new Remove();
            removeFilter.setAttributeIndicesArray(indices);
            removeFilter.setInvertSelection(true);
            removeFilter.setInputFormat(instances);
            Instances newData = Filter.useFilter(instances, removeFilter);

            System.out.println("Numero di attributi(parole) rimossi: "+(instances.numAttributes()-newData.numAttributes()));

            System.out.println("Numero Classi: "+instances.numClasses());
            System.out.println("Indice Classe: "+instances.classIndex());
            System.out.println("Stats Class attribute: "+instances.attributeStats(instances.classIndex()));

            preStat = preStat + "\nNumero Classi: "+instances.numClasses()+
                    "\nIndice Classe: "+instances.classIndex() +
                    "\nStats Class attribute: "+instances.attributeStats(instances.classIndex());

            long trainStartTime = System.nanoTime();
            //ADDESTRAMENTO NAIVE BAYES
            NaiveBayes naiveBayes = new NaiveBayes();
            /*Evaluation evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(naiveBayes, instances, K_FOLDS, new Random(new Date().getTime()));
            String naiveStats = "Naive Bayes Summary: "+evaluation.toSummaryString() +
                            "\nNaive Bayes: "+evaluation.toClassDetailsString() +
                            "\nNaive Bayes: "+evaluation.toMatrixString();
            writeStats(naiveStats,"NaiveStats-"+new Date().getTime());
            System.out.println("Naive Bayes Summary: "+evaluation.toSummaryString());
            System.out.println("Naive Bayes: "+evaluation.toClassDetailsString());
            System.out.println("Naive Bayes: "+evaluation.toMatrixString());
            writePredictions(evaluation.predictions(),"predictionsNaiveBayes"); //la stampa delle predizioni funziona*/
            naiveBayes.buildClassifier(newData);
            System.out.println("Fine build Classifier");

            //Ho commentato il J48 perchè impiegava troppo tempo, volevo solo testare NaiveMultinominal, alla fine J48 è uguale
            //ADDESTRAMENTO J48
            //J48 decisionTree = new J48();
            //Evaluation evaluationTree = new Evaluation(newData);
            /*evaluationTree.crossValidateModel(decisionTree, newData, K_FOLDS, new Random(new Date().getTime()));
            String j48Stats = "Decision Tree Summary: "+evaluationTree.toSummaryString() +
                    "\nDecision Tree: "+evaluationTree.toClassDetailsString() +
                    "\nDecision Tree: "+evaluationTree.toMatrixString();
            writeStats(j48Stats,"j48Stats-"+new Date().getTime());
            System.out.println("Decision Tree Summary: "+ evaluationTree.toSummaryString());
            System.out.println("Decision Tree: "+ evaluationTree.toClassDetailsString());
            System.out.println("Decision Tree: "+ evaluationTree.toMatrixString());*/
            //decisionTree.buildClassifier(newData);
            long trainEndTime = System.nanoTime();
            long trainTotalTime = trainEndTime - trainStartTime;
            System.out.println("Train time: "+trainTotalTime/1000000+" ms");
            preStat = preStat + "\nTrain time: "+trainTotalTime/1000000+" ms";

            //writePredictions(evaluationTree.predictions(),"predictionsJ48");

            //SALVATAGGIO MODELLI
            SerializationHelper.write("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\naiveBayes.model", naiveBayes);
            //SerializationHelper.write("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\j48.model", decisionTree);
            getServletContext().setAttribute("naiveModel",SerializationHelper.read("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\naiveBayes.model"));
            //getServletContext().setAttribute("dTreeModel",SerializationHelper.read("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\j48.model"));
            endTime = System.nanoTime();
            totalTime = endTime - startTime;
            System.out.println("Total time: "+totalTime/1000000+" ms");
            preStat = preStat + "\nTotal time: "+totalTime/1000000+" ms";

            writeStats(preStat,"preStatFile-"+new Date().getTime());

            Alert alert = new Alert("Modello addestrato con successo");
            request.setAttribute("alert",alert);
            request.getRequestDispatcher(view("site/index")).forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void writeIndices(int[] indices) throws IOException {
        FileWriter fileWriter = new FileWriter("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\indiciBestFirst.txt");
        for(int i:indices)
            fileWriter.append(i+"\n");

        fileWriter.append("\n"+"totale: "+indices.length);
        fileWriter.flush();
        fileWriter.close();
    }

    public void writeStats(String stats, String filename) throws IOException {
        FileWriter fileWriter = new FileWriter("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\"+filename+".txt");
        fileWriter.write(stats);
        fileWriter.flush();
        fileWriter.close();
    }

    public void writePredictions(ArrayList<Prediction> predictions, String fileName) throws IOException {
        FileWriter fileWriter = new FileWriter("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\"+fileName+".txt");
        int i=0;
        for(Prediction prediction : predictions){
            fileWriter.append("\nNum: "+i+" Actual: "+ prediction.actual()+" Predicted: "+prediction.predicted()+" error: "+(prediction.actual()==prediction.predicted() ? "" : "+"));
            fileWriter.flush();
            i++;
        }
        fileWriter.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}