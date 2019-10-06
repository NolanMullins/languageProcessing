/* Token.flex
 * jflex spec for tokenization of english sentence structure
 */
   
%%
   
%class Lexer
%type Token
%line
%column
    
%eofval{
  return null;
%eofval};


/* A line terminator is a \r (carriage return), \n (line feed), or
   \r\n. */
LineTerminator = \r|\n|\r\n
   
/* White space is a line terminator, space, tab, or form feed. */
WhiteSpace     = [ \t\f]
   
%%
   
/*
   This section contains regular expressions and actions, i.e. Java
   code, that will be executed when the scanner matches the associated
   regular expression. */

//Labels
"$TEXT"                     {return new Token(Token.LABEL, yytext(), yyline, yycolumn);}
"$TITLE"                    {return new Token(Token.LABEL, yytext(), yyline, yycolumn);}
([$]DOC.*[ |\n])            {return new Token(Token.LABEL, yytext(), yyline, yycolumn);}

{LineTerminator}            {return new Token(Token.NL, yytext(), yyline, yycolumn);}

//Suffix'd words
'(ve|re)                    {return new Token(Token.WORD, yytext(), yyline, yycolumn);}
//Negative numbers
[-][0-9]+[.]*[0-9]*         {return new Token(Token.NEG_NUM, yytext(), yyline, yycolumn);}

//Weird 
//( [-']|[-'] )               { return new Token(Token.OTHER, yytext(), yyline, yycolumn); }
([a-zA-Z]')*([a-zA-z|0-9]+)([-]*[a-zA-z|0-9]+)*('[sS])* { return new Token(Token.WORD, yytext(), yyline, yycolumn); }

{WhiteSpace}+               { /* skip whitespace */ }   
"{"[^\}]*"}"                { /* skip comments */ }

//punctuation
[.!?,;:'\(\)\[\]\"]         { return new Token(Token.PUNCTUATION, yytext(), yyline, yycolumn); }
//New lines
[\n]                        { return new Token(Token.NL, yytext(), yyline, yycolumn); }
.                           { return new Token(Token.OTHER, yytext(), yyline, yycolumn); }
