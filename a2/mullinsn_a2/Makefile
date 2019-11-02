JAVAC=javac
CLASSPATH=opennlp-tools-1.9.1.jar:.
FILE=data/test

all: split token preprocessor offline

%.class: %.java
	$(JAVAC) -cp $(CLASSPATH) $^

processDoc: runSplit runToken runPre runOffline

preprocessor: Preprocessor.class
split: SentenceDetectionME.class
token: gen-jflex Token.class Lexer.class Scanner.class
offline: Offline.class


#Run with a sample file
runSplit: 
	java -cp opennlp-tools-1.9.1.jar:. SentenceDetectionME $(FILE).txt > $(FILE).split
runToken:
	java Scanner $(FILE).txt > $(FILE).tok
runPre:
	java -cp opennlp-tools-1.9.1.jar:. Preprocessor $(FILE).tok > $(FILE).pre
runOffline:
	java Offline $(FILE).pre

#Tokenization
Lexer.java: token.flex
	$(JFLEX) token.flex
Token.class:
	javac Token.java
gen-jflex:
	jflex token.flex

clean:
	rm -f *.class *~ *.tok *.split *.pre Lexer.java Scanner SentenceDetectionME Preprocessor \ 
	rm data/*.tok data/*.split data/*.pre docids.txt postings.txt dictionary.txt
