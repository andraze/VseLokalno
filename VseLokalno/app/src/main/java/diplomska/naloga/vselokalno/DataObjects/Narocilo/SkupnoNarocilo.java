package diplomska.naloga.vselokalno.DataObjects.Narocilo;

import java.util.HashMap;
import java.util.Map;

/**
 * Skupno narocilo je objekt, ki ima osnovne podatke o narocilu.
 * String datum_dostave
 * String datum_narocila
 * String naslov_dostave
 * int opravljeno {0 == v procesu (white); 1 == warning (yellow /npr → če se bliža dan prevzema in ni sprejet s strani kmetije/);
 *                 2 == uspešno (green); 3 == neuspešno (red)
 *                 (blue border == pickup today?)}
 * Map<String, String> narocilo_cene ... npr: {"Mleko_123543": 3.5}
 * Map<String, String> narocilo_enote ... npr: {"Mleko_123543": liter}
 * Map<String, String> narocilo_kolicine ... npr: {"Mleko_123543": 2}
 * Mao<String, String> narocilo_slike ... pot do slike na firebase storage
 * Map<String, String> narocilo_zaloge ... zaloge naročila
 * Map<String, String> narocilo_imena ... imena artiklov naročila
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
    Map<String, String> narocilo_imena;

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
        this.narocilo_imena = new HashMap<>();
    }

    public Map<String, String> getNarocilo_zaloge() {
        return narocilo_zaloge;
    }

    public void setNarocilo_zaloge(Map<String, String> narocilo_zaloge) {
        this.narocilo_zaloge = narocilo_zaloge;
    }

    public void addNarocilo_zaloge(String articleId, String imagePath) {
        this.narocilo_zaloge.put(articleId, imagePath);
    }

    public void setNarocilo_slike(Map<String, String> narocilo_slike) {
        this.narocilo_slike = narocilo_slike;
    }

    public void addNarocilo_slike(String articleId, String imagePath) {
        this.narocilo_slike.put(articleId, imagePath);
    }

    public void removeNarocilo_slike(String articleId) {
        this.narocilo_slike.remove(articleId);
    }

    public void removeNarocilo_cene(String articleId) {
        this.narocilo_cene.remove(articleId);
    }

    public void removeNarocilo_zaloge(String articleId) {
        this.narocilo_zaloge.remove(articleId);
    }

    public void removeNarocilo_enote(String articleId) {
        this.narocilo_enote.remove(articleId);
    }

    public void removeNarocilo_imena(String articleID) {
        this.narocilo_imena.remove(articleID);
    }

    public void removeNarocilo_kolicine(String articleId) {
        this.narocilo_kolicine.remove(articleId);
    }

    public Map<String, String> getNarocilo_slike() {
        return this.narocilo_slike;
    }

    public void addNarocilo_cene(String articleId, String articlePrice) {
        this.narocilo_cene.put(articleId, articlePrice);
    }

    public void addNarocilo_enote(String articleId, String articleUnit) {
        this.narocilo_enote.put(articleId, articleUnit);
    }

    public void addNarocilo_kolicine(String articleId, String articleAmount) {
        this.narocilo_kolicine.put(articleId, articleAmount);
    }

    public Map<String, String> getNarocilo_imena() {
        return this.narocilo_imena;
    }

    public void setNarocilo_imena(Map<String, String> narocilo_ime) {
        this.narocilo_imena = narocilo_ime;
    }

    public void addNarocilo_imena(String articleId, String articleName) {
        this.narocilo_imena.put(articleId, articleName);
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
