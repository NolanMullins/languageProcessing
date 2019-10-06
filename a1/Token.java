class Token {

    public final static int ERROR = 0;
    public final static int OTHER = 1;
    public final static int NL = 2;
    public final static int NUM = 21;
    public final static int WORD = 22;

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
            return m_value+" ";
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

