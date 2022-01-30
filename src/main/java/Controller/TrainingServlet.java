package Controller;

import Controller.http.Controller;
import Model.Components.Alert;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.*;
import weka.core.converters.ConverterUtils;
import weka.core.stemmers.SnowballStemmer;
import weka.core.stopwords.Rainbow;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.Filter;
import weka.filters.supervised.instance.ClassBalancer;
import weka.filters.unsupervised.attribute.StringToWordVector;

@WebServlet(name = "TrainingServlet", value = "/trainingModel")
@MultipartConfig
public class TrainingServlet extends Controller {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(view("site/index.html")).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String wordsToKeep = request.getParameter("wordToKeep");

        String j48Split = request.getParameter("j48Split");
        String naiveSplit = request.getParameter("naiveSplit");
        String naiveCross = request.getParameter("naiveCross");
        String naiveNtimesCV = request.getParameter("naiveNtimesCV");
        String fileArff = request.getParameter("fileArff");

        int K_FOLDS = 2, TIMES = 1, WORDS_TO_KEEP = 100; //default values
        double PERCENTUALE = 0.7;

        if(wordsToKeep != null && wordsToKeep.matches("\\d*")){
            WORDS_TO_KEEP = Integer.parseInt(wordsToKeep);
        }

        try {

            Instant start = Instant.now();

            //carico il dataset
            ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\dataset\\"+fileArff);

            Instances instances = dataSource.getDataSet();
            instances.randomize(new Random(new Date().getTime()));

            FilteredClassifier naiveClassifier = new FilteredClassifier();
            FilteredClassifier j48Classifier = new FilteredClassifier();

            //DATA CLEANING, da fare sempre
            removeRows(instances);

            //STRING TO WORD VECTOR, da fare sempre
            stringToWordVector(instances,j48Classifier,naiveClassifier,WORDS_TO_KEEP);

            //Validazione J48 SPLIT DATASET
            if(j48Split != null){
                String percentage = request.getParameter("percentagej48");
                if(percentage != null && percentage.matches("\\d*")){
                    PERCENTUALE = ((double)Integer.parseInt(percentage)/100);
                }
                String statJ48 = evaluationJ48SplitDataSet(instances, j48Classifier, PERCENTUALE,WORDS_TO_KEEP);
                request.setAttribute("statJ48",statJ48);
            }

            //Validazione naive bayes split dataset
            if(naiveSplit != null) {
                String percentage = request.getParameter("percentageNaive");
                if(percentage != null && percentage.matches("\\d*")){
                    PERCENTUALE = ((double)Integer.parseInt(percentage)/100);
                }
                String statNaiveSplit = evaluationNaiveBayesSplitDT(instances, naiveClassifier, PERCENTUALE, WORDS_TO_KEEP);
                request.setAttribute("statNaiveSplit",statNaiveSplit);
            }

            //Validazione naive bayes cross validation normale
            if(naiveCross != null) {
                String k = request.getParameter("kFoldNaive");
                if(k != null && k.matches("\\d*")){
                    K_FOLDS = Integer.parseInt(k);
                }
                String statNaiveCross = evaluationNaiveBayesCrossFold(instances, naiveClassifier, K_FOLDS, WORDS_TO_KEEP);
                request.setAttribute("statNaiveCross",statNaiveCross);
            }

            //Validazione naive bayes n times k fold cross validation stratified
            if(naiveNtimesCV != null) {
                String n = request.getParameter("nTimesNaive");
                if(n != null && n.matches("\\d*")){
                    TIMES = Integer.parseInt(n);
                }

                String k = request.getParameter("kFoldNaive");
                if(k != null && k.matches("\\d*")){
                    K_FOLDS = Integer.parseInt(k);
                }
                ArrayList<String> statsNaiveNTimes = nTimesKFoldCrossValidationStratified(instances, naiveClassifier, TIMES, K_FOLDS, WORDS_TO_KEEP);
                request.setAttribute("statsNaiveNTimes",statsNaiveNTimes);
            }

            Alert alert = new Alert();

            Instant end = Instant.now();
            Duration interval = Duration.between(start,end);
            int seconds = (int)interval.getSeconds()%60;
            int minutes = ((int)interval.getSeconds()/60)%60;
            int hour = (int)interval.getSeconds()/60/60;
            String runTime = "Tempo di esecuzione: "+hour+":hour "+minutes+":minutes "+seconds+":seconds";
            //SALVATAGGIO MODELLI
            String save = request.getParameter("save");
            alert.addMessage("Addestramento completato");
            if(save != null){
                buildModels(instances,naiveClassifier,j48Classifier);
                saveModels(naiveClassifier,j48Classifier);
                alert.addMessage("Modelli salvati");
            }
            alert.addMessage(runTime);

            request.setAttribute("alert",alert);

            File folder = new File("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\dataset");
            ArrayList<String> fileList = listFilesForFolder(folder);
            request.setAttribute("fileList",fileList);

            request.getRequestDispatcher(view("site/evaluationPage")).forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //METODO SALVATAGGIO MODELLI
    private void buildModels(Instances instances,FilteredClassifier naiveBayes, FilteredClassifier j48) throws Exception {
        naiveBayes.buildClassifier(instances);
        j48.buildClassifier(instances);
    }

    private void saveModels(FilteredClassifier naiveClassifier, FilteredClassifier j48Classifier) throws Exception {
        SerializationHelper.write("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\naiveBayes.model", naiveClassifier);
        SerializationHelper.write("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\j48.model", j48Classifier);
        getServletContext().setAttribute("naiveModel",SerializationHelper.read("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\naiveBayes.model"));
        getServletContext().setAttribute("j48",SerializationHelper.read("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\j48.model"));
    }

    //METODI VALIDAZIONE
    private String evaluationNaiveBayesSplitDT(Instances instances, FilteredClassifier naiveClassifier, double percentuale, int wordFilter) throws Exception {
        int trainSize = (int) Math.round(instances.numInstances() * percentuale);
        int testSize = instances.numInstances() - trainSize;
        Instances train = new Instances(instances, 0, trainSize);
        Instances test = new Instances(instances,trainSize, testSize);

        ClassBalancer filter = new ClassBalancer();
        filter.setInputFormat(instances);
        train = Filter.useFilter(train,filter);

        System.out.println("Building naiveClassifier for Split evaluation...");
        naiveClassifier.buildClassifier(train); //il modello conterrà il filtro che applicherà on the fly
        System.out.println("Naive evaluation build finish");

        Evaluation evaluation = new Evaluation(test);
        evaluation.evaluateModel(naiveClassifier,test);

        String stat = writeStats(evaluation,"NaiveStats-"+wordFilter+"Word-PercentageSplit"+new Date().getTime()/1000,"Naive Bayes");
        printReport(evaluation,"Naive Bayes");
        writePredictions(evaluation.predictions(),"predictionsNaiveBayes");
        return stat;
    }

    private String evaluationNaiveBayesCrossFold(Instances instances, FilteredClassifier naiveClassifier, int kFold, int wordFilter) throws Exception {
        Evaluation evaluation = new Evaluation(instances);
        evaluation.crossValidateModel(naiveClassifier, instances, kFold, new Random(new Date().getTime()));

        String stat = writeStats(evaluation,"NaiveStats-"+wordFilter+"Word-"+kFold+"FoldCrossValidation"+new Date().getTime()/1000,"Naive Bayes");
        printReport(evaluation,"Naive Bayes");
        writePredictions(evaluation.predictions(),"predictionsNaiveBayes");
        return stat;
    }

    private String evaluationJ48SplitDataSet(Instances instances, FilteredClassifier j48Classifier, double percentuale, int wordFilter) throws Exception {
        int trainSize = (int) Math.round(instances.numInstances() * percentuale);
        int testSize = instances.numInstances() - trainSize;

        Instances train = new Instances(instances, 0, trainSize);
        Instances test = new Instances(instances, trainSize, testSize);

        ClassBalancer filter = new ClassBalancer();
        filter.setInputFormat(instances);
        train = Filter.useFilter(train,filter);

        System.out.println("Building j48Classifier for Split evaluation...");
        j48Classifier.buildClassifier(train);
        System.out.println("J48 evaluation build terminate");

        Evaluation evaluationTree = new Evaluation(train);
        evaluationTree.evaluateModel(j48Classifier,test);

        String stat = writeStats(evaluationTree,"j48Stats-"+wordFilter+"Word-PercentageSplit"+new Date().getTime(),"Decision Tree");
        printReport(evaluationTree,"Decision Tree");
        writePredictions(evaluationTree.predictions(),"predictionsJ48");
        return stat;
    }

    private ArrayList<String> nTimesKFoldCrossValidationStratified(Instances instances, Classifier classifier, int times, int folds, int wordFilter) throws Exception {
        Evaluation evaluation = null;
        ArrayList<String> list = new ArrayList<>();
        for(int i=0; i<times; i++){
            Random random = new Random(new Date().getTime());
            Instances randInstances = new Instances(instances); //per non modificare l'oggetto passato
            randInstances.randomize(random);
            randInstances.stratify(folds);

            evaluation = new Evaluation(randInstances);
            for(int j=0; j<folds; j++){
                Instances training = randInstances.trainCV(folds,j,random);
                Instances test = randInstances.testCV(folds,j);

                ClassBalancer filter = new ClassBalancer();
                filter.setInputFormat(randInstances);
                training = Filter.useFilter(training,filter);

                training.setClassIndex(training.numAttributes()-1);
                test.setClassIndex(test.numAttributes()-1);

                classifier.buildClassifier(training);
                evaluation.evaluateModel(classifier,test);
            }
            String stat = writeNTimesStats(evaluation,"Naive"+times+"TimesStats","NaiveStats-"+(i+1)+"Times-"+wordFilter+"Word-"+folds+"FoldCrossValidationStratified"+new Date().getTime()/1000,"Naive Bayes");
            list.add(stat);
            printReport(evaluation,"Naive Bayes, Times: "+i+1);
            writePredictions(evaluation.predictions(),"predictionsNaiveBayes");
        }
        return list;
    }

    //METODI DATA PREPARATION
    private void removeRows(Instances instances){
        System.out.println("Numero istanze: "+instances.numInstances());
        System.out.println("Numero campi vuoti: "+instances.attributeStats(1).missingCount);
        System.out.println("Rimozione missingValue...");
        instances.removeIf(Instance::hasMissingValue);
        System.out.println("Rimozione terminata.");
        System.out.println("Numero campi vuoti dopo remove: "+instances.attributeStats(1).missingCount);
        System.out.println("Numero istanze dopo remove: "+instances.numInstances());
    }

    private void stringToWordVector(Instances instances, FilteredClassifier j48, FilteredClassifier naiveBayes, int wordsToKeep) throws Exception {
        StringToWordVector stringToWordVector = new StringToWordVector();
        stringToWordVector.setIDFTransform(true);
        stringToWordVector.setTFTransform(true);
        int[] attributeToProcess = {0,1};
        stringToWordVector.setAttributeIndicesArray(attributeToProcess); //applichiamo il filtro ai primi due attributi, quindi titolo e testo
        stringToWordVector.setStemmer(new SnowballStemmer());
        stringToWordVector.setStopwordsHandler(new Rainbow());
        WordTokenizer wordTokenizer = new WordTokenizer();
        wordTokenizer.setDelimiters(".,;:'\"()?!/ -_><&#");
        stringToWordVector.setTokenizer(wordTokenizer);
        stringToWordVector.setInputFormat(instances);
        stringToWordVector.setWordsToKeep(wordsToKeep);
        instances.setClassIndex(instances.numAttributes()-1); //perchè prima di applicare il filtro, nella gui e nel file compare come terzo attributo

        j48.setFilter(stringToWordVector);
        j48.setClassifier(new J48());
        naiveBayes.setFilter(stringToWordVector); //si aggiunge il filtro che dovrà usare il classificatore
        naiveBayes.setClassifier(new NaiveBayes()); //si setta il classificatore*/
    }

    //METODI WRITE
    private void writeIndices(int[] indices) throws IOException {
        FileWriter fileWriter = new FileWriter("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\indiciBestFirst.txt");
        for(int i:indices)
            fileWriter.append(i+"\n");

        fileWriter.append("\n"+"totale: "+indices.length);
        fileWriter.flush();
        fileWriter.close();
    }

    private String writeStats(Evaluation evaluation, String filename, String algoName) throws Exception {
        String stats = algoName+" Summary: "+evaluation.toSummaryString() +
                "\n"+algoName+": "+evaluation.toClassDetailsString() +
                "\n"+algoName+": "+evaluation.toMatrixString();
        FileWriter fileWriter = new FileWriter("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\"+filename+".txt");
        fileWriter.write(stats);
        fileWriter.flush();
        fileWriter.close();
        return stats;
    }

    private String writeNTimesStats(Evaluation evaluation, String folderName, String filename, String algoName) throws Exception {
        File theDir = new File("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\"+folderName);
        if (!theDir.exists()){
            theDir.mkdirs();
        }
        String stat = null;
        if(theDir.exists()){
            stat = algoName+" Summary: "+evaluation.toSummaryString() +
                    "\n"+algoName+": "+evaluation.toClassDetailsString() +
                    "\n"+algoName+": "+evaluation.toMatrixString();
            FileWriter fileWriter = new FileWriter("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\"+folderName+"\\"+filename+".txt");
            fileWriter.write(stat);
            fileWriter.flush();
            fileWriter.close();
        }
        return stat;
    }

    private void writePredictions(ArrayList<Prediction> predictions, String fileName) throws IOException {
        FileWriter fileWriter = new FileWriter("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\"+fileName+".txt");
        int i=0;
        for(Prediction prediction : predictions){
            fileWriter.append("\nNum: "+i+" Actual: "+ prediction.actual()+" Predicted: "+prediction.predicted()+" error: "+(prediction.actual()==prediction.predicted() ? "" : "+"));
            fileWriter.flush();
            i++;
        }
        fileWriter.close();
    }

    private void printReport(Evaluation evaluation, String algoName) throws Exception {
        System.out.println(algoName+" Summary: "+evaluation.toSummaryString());
        System.out.println(algoName+": "+evaluation.toClassDetailsString());
        System.out.println(algoName+": "+evaluation.toMatrixString());
    }
}