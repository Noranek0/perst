package org.garret.perst.fulltext;

import java.io.*;
import java.util.*;
import org.garret.perst.*;

public class FullTextSearchHelper extends Persistent {
    public float nearnessWeight = 10.0f;
    public int wordSwapPenalty = 10;
    public int maxWordLength = 100;

    public String AND = "AND";
    public String OR = "OR";
    public String NOT = "NOT";

    public String[] stopWords = new String[] { "a", "the", "at", "on", "of", "to", "an" };

    public String[] getNormalForms(String word, String language) {
        return new String[] { word };
    }

    public boolean isWordChar(char ch) {
        return Character.isLetter(ch) || Character.isDigit(ch);
    }

    public Occurrence[] parseText(Reader reader) throws IOException {
        int pos = 0;
        ArrayList list = new ArrayList();
        int ch = reader.read();

        while (ch > 0) {
            if (isWordChar((char) ch)) {
                StringBuffer buf = new StringBuffer();
                int wordPos = pos;
                do {
                    pos += 1;
                    buf.append((char) ch);
                    ch = reader.read();
                } while (ch > 0 && isWordChar((char) ch));
                String word = buf.toString().toLowerCase();
                if (word.length() <= maxWordLength && !isStopWord(word)) {
                    list.add(new Occurrence(word, wordPos, 0));
                }
            } else {
                pos += 1;
                ch = reader.read();
            }
        }
        return (Occurrence[]) list.toArray(new Occurrence[list.size()]);
    }

    protected transient HashSet stopList;

    protected void fillStopList() {
        stopList = new HashSet();
        for (int i = 0; i < stopWords.length; i++) {
            stopList.add(stopWords[i]);
        }
    }

    public void onLoad() {
        fillStopList();
    }

    public boolean isStopWord(String word) {
        return stopList.contains(word);
    }

    /*
     * Full text search helper constructor
     */
    public FullTextSearchHelper(Storage storage) {
        super(storage);
        fillStopList();
    }

    protected FullTextSearchHelper() {
    }

    protected class QueryScanner {
        String query;
        int pos;
        boolean inQuotes;
        boolean unget;
        String word;
        int wordPos;
        int token;
        String language;

        QueryScanner(String query, String language) {
            this.query = query;
            this.language = language;
        }

        static final int TKN_EOQ = 0;
        static final int TKN_WORD = 1;
        static final int TKN_AND = 2;
        static final int TKN_OR = 3;
        static final int TKN_NOT = 4;
        static final int TKN_LPAR = 5;
        static final int TKN_RPAR = 6;

        int scan() {
            if (unget) {
                unget = false;
                return token;
            }
            int len = query.length();
            int p = pos;
            String q = query;
            while (p < len) {
                char ch = q.charAt(p);
                if (ch == '"') {
                    inQuotes = !inQuotes;
                    p += 1;
                } else if (ch == '(') {
                    pos = p + 1;
                    return token = TKN_LPAR;
                } else if (ch == ')') {
                    pos = p + 1;
                    return token = TKN_RPAR;
                } else if (isWordChar(ch)) {
                    wordPos = p;
                    while (++p < len && isWordChar(q.charAt(p)))
                        ;
                    String word = q.substring(wordPos, p);
                    pos = p;
                    if (word.equals(AND)) {
                        return token = TKN_AND;
                    } else if (word.equals(OR)) {
                        return token = TKN_OR;
                    } else if (word.equals(NOT)) {
                        return token = TKN_NOT;
                    } else {
                        word = word.toLowerCase();
                        if (!isStopWord(word)) {
                            if (!inQuotes) {
                                // just get the first normal form and ignore all other alternatives
                                word = getNormalForms(word, language)[0];
                            }
                            this.word = word;
                            return token = TKN_WORD;
                        }
                    }
                } else {
                    p += 1;
                }
            }
            pos = p;
            return token = TKN_EOQ;
        }
    }

    protected FullTextQuery disjunction(QueryScanner scanner) {
        FullTextQuery left = conjunction(scanner);
        if (scanner.token == QueryScanner.TKN_OR) {
            FullTextQuery right = disjunction(scanner);
            if (left != null && right != null) {
                return new FullTextQueryBinaryOp(FullTextQuery.OR, left, right);
            } else if (right != null) {
                return right;
            }
        }
        return left;
    }

    protected FullTextQuery conjunction(QueryScanner scanner) {
        FullTextQuery left = term(scanner);
        if (scanner.token == QueryScanner.TKN_WORD || scanner.token == QueryScanner.TKN_AND) {
            if (scanner.token == QueryScanner.TKN_WORD) {
                scanner.unget = true;
            }
            int cop = scanner.inQuotes ? FullTextQuery.NEAR : FullTextQuery.AND;
            FullTextQuery right = disjunction(scanner);
            if (left != null && right != null) {
                return new FullTextQueryBinaryOp(cop, left, right);
            } else if (right != null) {
                return right;
            }
        }
        return left;
    }

    protected FullTextQuery term(QueryScanner scanner) {
        FullTextQuery q = null;
        switch (scanner.scan()) {
            case QueryScanner.TKN_NOT:
                q = term(scanner);
                return (q != null) ? new FullTextQueryUnaryOp(FullTextQuery.NOT, q) : null;
            case QueryScanner.TKN_LPAR:
                q = disjunction(scanner);
                break;
            case QueryScanner.TKN_WORD:
                q = new FullTextQueryMatchOp(scanner.inQuotes ? FullTextQuery.STRICT_MATCH : FullTextQuery.MATCH,
                        scanner.word, scanner.wordPos);
                break;
            case QueryScanner.TKN_EOQ:
                return null;
        }
        scanner.scan();
        return q;
    }

    public FullTextQuery parseQuery(String query, String language) {
        return disjunction(new QueryScanner(query, language));
    }

    static final float[] OCCURRENCE_KIND_WEIGHTS = new float[0];

    public float[] getOccurrenceKindWeights() {
        return OCCURRENCE_KIND_WEIGHTS;
    }

    public float getNearnessWeight() {
        return 10.0f;
    }

    public int getWordSwapPenalty() {
        return 10;
    }
}
