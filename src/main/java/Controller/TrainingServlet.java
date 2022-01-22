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
            final int K_FOLDS = 10;
            long totalTime, startTime, endTime;
            startTime = System.nanoTime();
            //carico il dataset
            ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\dataset\\FakeAndTrueRandomWithCovidTest.arff");
            Instances instances = dataSource.getDataSet();
            FilteredClassifier naiveClassifier = new FilteredClassifier();
            FilteredClassifier j48Classifier = new FilteredClassifier();


            //DATA CLEANING
            System.out.println("Numero istanze: "+instances.numInstances());
            System.out.println("Numero campi vuoti: "+instances.attributeStats(1).missingCount);
            System.out.println("Rimozione missingValue...");
            instances.removeIf(Instance::hasMissingValue);
            System.out.println("Rimozione terminata.");
            System.out.println("Numero campi vuoti dopo remove: "+instances.attributeStats(1).missingCount);
            System.out.println("Numero istanze dopo remove: "+instances.numInstances());

            //STRING TO WORD VECTOR
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
            stringToWordVector.setWordsToKeep(10000);
            instances.setClassIndex(instances.numAttributes()-1); //perchè prima di applicare il filtro, nella gui e nel file compare come terzo attributo
            j48Classifier.setFilter(stringToWordVector);
            j48Classifier.setClassifier(new J48());
           /* naiveClassifier.setFilter(stringToWordVector); //si aggiunge il filtro che dovrà usare il classificatore
            naiveClassifier.setClassifier(new NaiveBayes()); //si setta il classificatore*/

            // effettuare la cross validation in questo punto
            //Validazione naive
            /*Evaluation evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(naiveClassifier, instances, K_FOLDS, new Random(new Date().getTime()));
            String naiveStats = "Naive Bayes Summary: "+evaluation.toSummaryString() +
                    "\nNaive Bayes: "+evaluation.toClassDetailsString() +
                    "\nNaive Bayes: "+evaluation.toMatrixString();
            writeStats(naiveStats,"NaiveStats-"+new Date().getTime());
            System.out.println("Naive Bayes Summary: "+evaluation.toSummaryString());
            System.out.println("Naive Bayes: "+evaluation.toClassDetailsString());
            System.out.println("Naive Bayes: "+evaluation.toMatrixString());
            writePredictions(evaluation.predictions(),"predictionsNaiveBayes"); //la stampa delle predizioni funziona*/

            //Validazione J48
            int trainSize = (int) Math.round(instances.numInstances() * 0.7);
            int testSize = instances.numInstances() - trainSize;
            Instances train = new Instances(instances, 0, trainSize);
            Instances test = new Instances(instances, trainSize, testSize);
            System.out.println("Building j48Classifier for evaluation...");
            j48Classifier.buildClassifier(train);
            System.out.println("J48 evaluation build terminate");
            Evaluation evaluationTree = new Evaluation(train);
            evaluationTree.evaluateModel(j48Classifier,test);

            String j48Stats = "Decision Tree Summary: "+evaluationTree.toSummaryString() +
                    "\nDecision Tree: "+evaluationTree.toClassDetailsString() +
                    "\nDecision Tree: "+evaluationTree.toMatrixString();
            writeStats(j48Stats,"j48Stats-"+new Date().getTime());
            System.out.println("Decision Tree Summary: "+ evaluationTree.toSummaryString());
            System.out.println("Decision Tree: "+ evaluationTree.toClassDetailsString());
            System.out.println("Decision Tree: "+ evaluationTree.toMatrixString());
            writePredictions(evaluationTree.predictions(),"predictionsJ48");

            //TenTimesTenCrossValidation naive Bayes
            nTimesKFoldCrossValidation(instances,naiveClassifier,1,K_FOLDS);
            System.out.println("inizio salvataggio modelli");


            /*System.out.println("Building naiveClassifier...");
            naiveClassifier.buildClassifier(instances); //il modello conterrà il filtro che applicherà on the flyyyy
            System.out.println("Naive build finish");*/
            System.out.println("Final Building j48Classifier...");
            j48Classifier.buildClassifier(instances); //rebuild with all dataset
            System.out.println("Final J48 build terminate.");



            //SALVATAGGIO MODELLI
            //SerializationHelper.write("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\naiveBayes.model", naiveClassifier);
            SerializationHelper.write("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\j48.model", j48Classifier);
            //getServletContext().setAttribute("naiveModel",SerializationHelper.read("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\naiveBayes.model"));
            getServletContext().setAttribute("j48",SerializationHelper.read("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\j48.model"));
            endTime = System.nanoTime();
            totalTime = endTime - startTime;
            System.out.println("Total time: "+totalTime/1000000+" ms");
            //preStat = preStat + "\nTotal time: "+totalTime/1000000+" ms";

            //writeStats(preStat,"preStatFile-"+new Date().getTime());

            System.out.println("Salvataggio completato");
            Alert alert = new Alert("Modello addestrato con successo");
            request.setAttribute("alert",alert);
            request.getRequestDispatcher(view("site/index")).forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void nTimesKFoldCrossValidation(Instances instances, Classifier classifier, int times, int folds) throws Exception {
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
        }
    }

    private void writeIndices(int[] indices) throws IOException {
        FileWriter fileWriter = new FileWriter("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\indiciBestFirst.txt");
        for(int i:indices)
            fileWriter.append(i+"\n");

        fileWriter.append("\n"+"totale: "+indices.length);
        fileWriter.flush();
        fileWriter.close();
    }

    private void writeStats(String stats, String filename) throws IOException {
        FileWriter fileWriter = new FileWriter("C:\\Program Files\\Apache Software Foundation\\Tomcat 9.0\\model\\"+filename+".txt");
        fileWriter.write(stats);
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }
}