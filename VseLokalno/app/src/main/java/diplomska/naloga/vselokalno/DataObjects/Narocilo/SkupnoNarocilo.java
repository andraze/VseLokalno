package diplomska.naloga.vselokalno.DataObjects.Narocilo;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

/**
 * Skupno narocilo je objekt, ki ima osnovne podatke o narocilu.
 * Timestamp datum_dostave
 * Timestamp datum_narocila
 * String naslov_dostave
 * int opravljeno {0 == v procesu; 1 == uspešno; 2 == neuspešno}
 * Map<String, Object> narocilo_cene ... npr: {"Mleko": 3.5}
 * Map<String, Object> narocilo_enote ... npr: {"Mleko": liter}
 * Map<String, Object> narocilo_kolicine .. npr: {"Mleko": 2}
 */
public class SkupnoNarocilo {

    Timestamp datum_dostave;
    Timestamp datum_narocila;
    String naslov_dostave;
    int opravljeno;
    Map<String, String> narocilo_cene;
    Map<String, String> narocilo_enote;
    Map<String, String> narocilo_kolicine;

    public SkupnoNarocilo() {
        naslov_dostave = "";
        datum_dostave = null;
        datum_narocila = null;
        opravljeno = -1;
        this.narocilo_cene = new HashMap<>();
        this.narocilo_enote = new HashMap<>();
        this.narocilo_kolicine = new HashMap<>();
    }

    public void addNarocilo_cene(String articleName, String articlePrice) {
        this.narocilo_cene.put(articleName, articlePrice);
    }

    public void addNarocilo_enote(String articleName, String articleUnit) {
        this.narocilo_enote.put(articleName, articleUnit);
    }

    public void addNarocilo_kolicine(String articleName, String articleAmount) {
        this.narocilo_kolicine.put(articleName, articleAmount);
    }

    public Map<String, String> getNarocilo_cene() {
        return narocilo_cene;
    }

    public void setNarocilo_cene(Map<String, String> narocilo_cene) {
        this.narocilo_cene = narocilo_cene;
    }

    public Map<String, String> getNarocilo_enote() {
        return narocilo_enote;
    }

    public void setNarocilo_enote(Map<String, String> narocilo_enote) {
        this.narocilo_enote = narocilo_enote;
    }

    public Map<String, String> getNarocilo_kolicine() {
        return narocilo_kolicine;
    }

    public void setNarocilo_kolicine(Map<String, String> narocilo_kolicine) {
        this.narocilo_kolicine = narocilo_kolicine;
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
