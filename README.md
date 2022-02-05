# FakeNewsDetector

# Introduzione
FakeNewsDetector è un classificatore di fake news, utilizzando il machine 
learning è in grado di analizzare il testo di una news e indicare la veridicità
della notizia.

L’analisi di un testo non è un processo banale a causa di caratteristiche linguistiche come il sarcasmo e le metafore. Lo scopo di questo progetto è quello di creare un sistema che utilizzando i dati di notizie passate, possa prevedere la possibilità che una notizia sia falsa o meno, a questo proposito il sistema sarà in grado di analizzare solo notizie in lingua inglese, essendo la lingua che presenta il maggior numero di dati a disposizione per poter addestrare il modello di Machine Learning.

La notizia sarà analizzata da due diversi classificatori, il primo basato sull'algoritmo Naive Bayes, mentre il secondo sui Decision Tree (J48). In questo modo si potranno confrontare le relative predizioni.

# Contenuto
Nella cartella [assets](https://github.com/ilgrafico/fakenewsdetector/tree/master/assets) è possibile visionare alcuni mockup dell'interfaccia web, mentre nella cartella [src/main/java](https://github.com/ilgrafico/fakenewsdetector/tree/master/src/main/java) troviamo il codice delle Java Servlet. 
La cartella [webapp](https://github.com/ilgrafico/fakenewsdetector/tree/master/src/main/webapp), oltre a contenere i file CSS, JavaScript e le immagini utilizzate, contiene anche la cartella [WEB-INF](https://github.com/ilgrafico/fakenewsdetector/tree/master/src/main/webapp/WEB-INF) contenente le [librerie](https://github.com/ilgrafico/fakenewsdetector/tree/master/src/main/webapp/WEB-INF/lib) tra cui le Weka API, Jsoup, JSTL ed infine la libreria Snowball che permette lo stemming delle parole. Infine sempre nella cartella [WEB-INF](https://github.com/ilgrafico/fakenewsdetector/tree/master/src/main/webapp/WEB-INF) troviamo le [JSP](https://github.com/ilgrafico/fakenewsdetector/tree/master/src/main/webapp/WEB-INF/views) che compongono l'interfaccia web.

# Autori
 - Carmine Leo - [ilgrafico](https://github.com/ilgrafico)
 - Nicolapio Gagliarde - [GagliardeNicolapio](https://github.com/GagliardeNicolapio)
 
 # Costruito con
  - Java - Linguaggio di programmazione back-end
  - Weka - Framework per l'implementazione di modelli di machine learning
  - Jsoup - Framework per l'analisi di pagine web 

# Dataset
 I dataset sono disponibili a questo [link](https://drive.google.com/drive/folders/1AhW3YYmInQGEek945jBpjh3Fsq5YHl47?usp=sharing)
