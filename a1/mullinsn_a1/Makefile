JAVAC=javac
CLASSPATH=opennlp-tools-1.9.1.jar:.

all: SentenceDetectionME.class POSTaggingMEDemo.class Analysis.class gen-jflex Token.class Lexer.class Scanner.class
#POSTaggingMEDemo.class TokenizerMEProbs.class

%.class: %.java
	$(JAVAC) -cp $(CLASSPATH) $^

processDoc: split token tag analysis

split:
	java -cp opennlp-tools-1.9.1.jar:. SentenceDetectionME sample.txt > sample.splitted

token: gen-jflex Token.class Lexer.class Scanner.class
	java Scanner sample.splitted > sample.tokenized

tag:
	java -cp opennlp-tools-1.9.1.jar:. POSTaggingMEDemo sample.tokenized > sample.tagged

analysis:
	java Analysis sample.tagged > sample.analysis

#Tokenization
Lexer.java: token.flex
	$(JFLEX) token.flex
Token.class:
	javac Token.java
gen-jflex:
	jflex token.flex

clean:
	rm -f *.class *~ Lexer.java
