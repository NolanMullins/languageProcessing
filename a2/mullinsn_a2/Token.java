/* Token 
 * Utility class to store jflex token data
 */
class Token {

    public final static int ERROR = 0;
    public final static int OTHER = 1;
    public final static int NL = 2;
    public final static int LABEL = 3;
    public final static int DELIMITER = 4;
    public final static int NEG_NUM = 5;
    public final static int PUNCTUATION = 6;
    public final static int NUM = 7;
    public final static int WORD = 8;

    public int m_type;
    public String m_value;
    public int m_line;
    public int m_column;
  
    Token (int type, String value, int line, int column) {
        m_type = type;
        m_value = value;
        m_line = line;
        m_column = column;
    }

    public String toString() {
        switch (m_type) {
        case WORD:
        case NUM:
        case NEG_NUM:
        case DELIMITER:
        case PUNCTUATION:
            return m_value+" ";
        case LABEL:
        case NL:
            return m_value;
        case OTHER:
            return m_value +" ";
        case ERROR:
            return "ERROR(" + m_value + ")";
        default:
            return "UNKNOWN(" + m_value + ")";
        }
    }
}

