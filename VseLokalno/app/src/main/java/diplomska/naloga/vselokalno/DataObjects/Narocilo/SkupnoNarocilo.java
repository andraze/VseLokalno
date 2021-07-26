package diplomska.naloga.vselokalno.DataObjects.Narocilo;

import com.google.firebase.Timestamp;

/**
 * Skupno narocilo je objekt, ki ima osnovne podatke o narocilu.
 * Timestamp datum_dostave
 * Timestamp datum_narocila
 * String naslov_dostave
 * int opravljeno {0 == v procesu; 1 == uspešno; 2 == neuspešno}
 */
public class SkupnoNarocilo {

    Timestamp datum_dostave;
    Timestamp datum_narocila;
    String naslov_dostave;
    int opravljeno;

    public SkupnoNarocilo() {
        naslov_dostave = "";
        datum_dostave = null;
        datum_narocila = null;
        opravljeno = -1;
    }

    public Timestamp getDatum_dostave() {
        return datum_dostave;
    }

    public void setDatum_dostave(Timestamp datum_dostave) {
        this.datum_dostave = datum_dostave;
    }

    public Timestamp getDatum_narocila() {
        return datum_narocila;
    }

    public void setDatum_narocila(Timestamp datum_narocila) {
        this.datum_narocila = datum_narocila;
    }

    public String getNaslov_dostave() {
        return naslov_dostave;
    }

    public void setNaslov_dostave(String naslov_dostave) {
        this.naslov_dostave = naslov_dostave;
    }

    public int getOpravljeno() {
        return opravljeno;
    }

    public void setOpravljeno(int opravljeno) {
        this.opravljeno = opravljeno;
    }

}
