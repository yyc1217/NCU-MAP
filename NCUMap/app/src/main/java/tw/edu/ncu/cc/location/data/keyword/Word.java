package tw.edu.ncu.cc.location.data.keyword;

@SuppressWarnings( "unused" )
public class Word {

    private String word;
    private WordType type;

    public Word() { }

    public Word( String word, WordType type ) {
        this.word = word;
        this.type = type;
    }

    public String getWord() {
        return word;
    }

    public void setWord( String word ) {
        this.word = word;
    }

    public WordType getType() {
        return type;
    }

    public void setType( WordType type ) {
        this.type = type;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        Word word1 = ( Word ) o;

        if ( type != word1.type ) return false;
        if ( !word.equals( word1.word ) ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = word.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

}
