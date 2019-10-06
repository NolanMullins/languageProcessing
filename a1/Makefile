JAVAC=javac
CLASSPATH=opennlp-tools-1.9.1.jar:.

all: SentenceDetectionME.class POSTaggingMEDemo.class 
#POSTaggingMEDemo.class TokenizerMEProbs.class

%.class: %.java
	$(JAVAC) -cp $(CLASSPATH) $^

split:
	java -cp opennlp-tools-1.9.1.jar:. SentenceDetectionME sample.txt > sample.splitted

tag:
	java -cp opennlp-tools-1.9.1.jar:. POSTaggingMEDemo sample.tokenized > sample.tagged

#Tokenization
Lexer.java: token.flex
	$(JFLEX) token.flex
Token.class:
	javac Token.java
gen-jflex:
	jflex token.flex
 
token: gen-jflex Token.class Lexer.class Scanner.class
	java Scanner < sample.splitted
    
#java -cp ../opennlp-tools-1.9.1.jar:. Token sample.splitted > sample.stemmed

clean:
	rm -f *.class *~ Lexer.java
