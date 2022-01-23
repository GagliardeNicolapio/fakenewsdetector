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
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.attribute.StringToWordVector;

@WebServlet(name = "TrainingServlet", value = "/trainingModel", loadOnStartup = 0)
public class TrainingServlet extends Controller {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            final int K_FOLDS = 10, TIMES = 10, WORDS_TO_KEEP = 10000;
            final double  PERCENTUALE = 0.7;
            long totalTime, startTime, endTime;
            startTime = System.nanoTime();

            //carico il dataset
            ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\dataset\\FakeAndTrueRandomWithCovidTest.arff");
            Instances instances = dataSource.getDataSet();

            FilteredClassifier naiveClassifier = new FilteredClassifier();
            FilteredClassifier j48Classifier = new FilteredClassifier();

            //DATA CLEANING, da fare sempre
            removeRows(instances);

            //STRING TO WORD VECTOR, da fare sempre
            stringToWordVector(instances,j48Classifier,naiveClassifier,WORDS_TO_KEEP);

            //Validazione J48 SPLIT DATASET
            evaluationJ48SplitDataSet(instances, j48Classifier, PERCENTUALE);

            //Validazione naive bayes one times k fold cross validarion stratified
            //nTimesKFoldCrossValidationStratified(instances,naiveClassifier,TIMES,K_FOLDS);

            //Validazione naive bayes cross validation normale
            //evaluationNaiveBayesCrossFold(instances,naiveClassifier,K_FOLDS);

            //Validazione naive bayes split dataset
            evaluationNaiveBayesSplitDT(instances,naiveClassifier,PERCENTUALE);

            //SALVATAGGIO MODELLI
            saveModels(naiveClassifier,j48Classifier);

             endTime = System.nanoTime();
            totalTime = endTime - startTime;
            System.out.println("Total time: "+totalTime/1000000+" ms");


            System.out.println("Salvataggio completato");
            Alert alert = new Alert("Modello addestrato con successo");
            request.setAttribute("alert",alert);
            request.getRequestDispatcher(view("site/index")).forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //METODO SALVATAGGIO MODELLI
    public void saveModels(FilteredClassifier naiveClassifier, FilteredClassifier j48Classifier) throws Exception {
        SerializationHelper.write("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\naiveBayes.model", naiveClassifier);
        SerializationHelper.write("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\j48.model", j48Classifier);
        getServletContext().setAttribute("naiveModel",SerializationHelper.read("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\naiveBayes.model"));
        getServletContext().setAttribute("j48",SerializationHelper.read("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\j48.model"));
    }

    //METODI VALIDAZIONE
    private void evaluationNaiveBayesSplitDT(Instances instances, FilteredClassifier naiveClassifier, double percentuale) throws Exception {
        instances.randomize(new Random(new Date().getTime()));
        int trainSize = (int) Math.round(instances.numInstances() * percentuale);
        int testSize = instances.numInstances() - trainSize;
        Instances train = new Instances(instances, 0, trainSize);
        Instances test = new Instances(instances,trainSize, testSize);

        System.out.println("Building naiveClassifier...");
        naiveClassifier.buildClassifier(train); //il modello conterrà il filtro che applicherà on the flyyyy
        System.out.println("Naive build finish");

        Evaluation evaluation = new Evaluation(test);
        evaluation.evaluateModel(naiveClassifier,test);

        writeStats(evaluation,"Naive Bayes"+"Stats-"+new Date().getTime(),"Naive Bayes");
        printReport(evaluation,"Naive Bayes");
        writePredictions(evaluation.predictions(),"predictionsNaiveBayes"); //la stampa delle predizioni funziona*/

    }

    private void evaluationNaiveBayesCrossFold(Instances instances, FilteredClassifier naiveClassifier, int kFold) throws Exception {
        Evaluation evaluation = new Evaluation(instances);
        evaluation.crossValidateModel(naiveClassifier, instances, kFold, new Random(new Date().getTime()));

        writeStats(evaluation,"NaiveStats-"+new Date().getTime(),"Naive Bayes");
        printReport(evaluation,"Naive Bayes");
        writePredictions(evaluation.predictions(),"predictionsNaiveBayes"); //la stampa delle predizioni funziona*/
    }

    private void evaluationJ48SplitDataSet(Instances instances, FilteredClassifier j48Classifier, double percentuale) throws Exception {
        instances.randomize(new Random(new Date().getTime()));
        int trainSize = (int) Math.round(instances.numInstances() * percentuale);
        int testSize = instances.numInstances() - trainSize;

        Instances train = new Instances(instances, 0, trainSize);
        Instances test = new Instances(instances, trainSize, testSize);

        System.out.println("Building j48Classifier for evaluation...");
        j48Classifier.buildClassifier(train);
        System.out.println("J48 evaluation build terminate");

        Evaluation evaluationTree = new Evaluation(train);
        evaluationTree.evaluateModel(j48Classifier,test);

        writeStats(evaluationTree,"j48Stats-"+new Date().getTime(),"Decision Tree");
        printReport(evaluationTree,"Decision Tree");
        writePredictions(evaluationTree.predictions(),"predictionsJ48");
    }

    private void nTimesKFoldCrossValidationStratified(Instances instances, Classifier classifier, int times, int folds) throws Exception {
        for(int i=0; i<times; i++){
            Random random = new Random(new Date().getTime());
            Instances randInstances = new Instances(instances); //per non modificare l'oggetto passato
            randInstances.randomize(random);
            randInstances.stratify(folds);

            Evaluation evaluation = new Evaluation(randInstances);
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
            printReport(evaluation,"Naive Bayes, Times: "+i);
        }
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

    private void writeStats(Evaluation evaluation, String filename, String algoName) throws Exception {
        String naiveStats = algoName+" Summary: "+evaluation.toSummaryString() +
                "\n"+algoName+": "+evaluation.toClassDetailsString() +
                "\n"+algoName+": "+evaluation.toMatrixString();
        FileWriter fileWriter = new FileWriter("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\"+filename+".txt");
        fileWriter.write(naiveStats);
        fileWriter.flush();
        fileWriter.close();
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}