JAVAC=javac
CLASSPATH=opennlp-tools-1.9.1.jar:.

all: token

%.class: %.java
	$(JAVAC) -cp $(CLASSPATH) $^

processDoc: token runToken

split:
	java -cp opennlp-tools-1.9.1.jar:. SentenceDetectionME sample.txt > sample.splitted

token: gen-jflex Token.class Lexer.class Scanner.class

runToken:
	java Scanner test.txt > test.tok

#Tokenization
Lexer.java: token.flex
	$(JFLEX) token.flex
Token.class:
	javac Token.java
gen-jflex:
	jflex token.flex

clean:
	rm -f *.class *~ Lexer.java Scanner
