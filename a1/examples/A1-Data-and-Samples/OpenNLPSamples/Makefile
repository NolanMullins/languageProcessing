JAVAC=javac
CLASSPATH=opennlp-tools-1.9.1.jar:.

all: SentenceDetectionME.class POSTaggingMEDemo.class TokenizerMEProbs.class

%.class: %.java
	$(JAVAC) -cp $(CLASSPATH) $^

clean:
	rm -f *.class *~
