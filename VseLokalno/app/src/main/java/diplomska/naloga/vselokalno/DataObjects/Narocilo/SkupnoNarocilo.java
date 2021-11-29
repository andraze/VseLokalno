package diplomska.naloga.vselokalno.DataObjects.Narocilo;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

/**
 * Skupno narocilo je objekt, ki ima osnovne podatke o narocilu.
 * String datum_dostave
 * String datum_narocila
 * String naslov_dostave
 * int opravljeno {0 == v procesu; 1 == uspešno; 2 == neuspešno}
 * Map<String, String> narocilo_cene ... npr: {"Mleko": 3.5}
 * Map<String, String> narocilo_enote ... npr: {"Mleko": liter}
 * Map<String, String> narocilo_kolicine ... npr: {"Mleko": 2}
 * Mao<String, String> narocilo_slike ... pot do slike na firebase storage
 * Map<String, String> narocilo_zaloge
 * Narocili sta povezani (za kmetijo={Kmetije/ID_kmetije/Naročila/ID_kmetije#ID_kupca_#datum_narocila} za kupca={Uporabniki/ID_uporabnika/Naročila/ID_kupca#ID_kmetije#datum_narocila}
 */
public class SkupnoNarocilo {

    String datum_dostave;
    String datum_narocila;
    String naslov_dostave;
    int opravljeno;
    Map<String, String> narocilo_cene;
    Map<String, String> narocilo_enote;
    Map<String, String> narocilo_kolicine;
    Map<String, String> narocilo_slike;
    Map<String, String> narocilo_zaloge;

    public SkupnoNarocilo() {
        naslov_dostave = "";
        datum_dostave = "";
        datum_narocila = "";
        opravljeno = -1;
        this.narocilo_cene = new HashMap<>();
        this.narocilo_enote = new HashMap<>();
        this.narocilo_kolicine = new HashMap<>();
        this.narocilo_slike = new HashMap<>();
        this.narocilo_zaloge = new HashMap<>();
    }

    public Map<String, String> getNarocilo_zaloge() {
        return narocilo_zaloge;
    }

    public void setNarocilo_zaloge(Map<String, String> narocilo_zaloge) {
        this.narocilo_zaloge = narocilo_zaloge;
    }

    public void addNarocilo_zaloge(String articleName, String imagePath) {
        this.narocilo_zaloge.put(articleName, imagePath);
    }

    public void setNarocilo_slike(Map<String, String> narocilo_slike) {
        this.narocilo_slike = narocilo_slike;
    }

    public void addNarocilo_slike(String articleName, String imagePath) {
        this.narocilo_slike.put(articleName, imagePath);
    }

    public void removeNarocilo_slike(String articleName) {
        this.narocilo_slike.remove(articleName);
    }

    public void removeNarocilo_cene(String articleName) {
        this.narocilo_cene.remove(articleName);
    }

    public void removeNarocilo_zaloge(String articleName) {
        this.narocilo_zaloge.remove(articleName);
    }

    public void removeNarocilo_enote(String articleName) {
        this.narocilo_enote.remove(articleName);
    }

    public void removeNarocilo_kolicine(String articleName) {
        this.narocilo_kolicine.remove(articleName);
    }

    public Map<String, String> getNarocilo_slike() {
        return this.narocilo_slike;
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

    public String getDatum_dostave() {
        return datum_dostave;
    }

    public void setDatum_dostave(String datum_dostave) {
        this.datum_dostave = datum_dostave;
    }

    public String getDatum_narocila() {
        return datum_narocila;
    }

    public void setDatum_narocila(String datum_narocila) {
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
