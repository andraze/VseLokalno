package diplomska.naloga.vselokalno.DataObjects.Narocilo;

import java.util.HashMap;
import java.util.Map;

/**
 * ZaKupca je objekt, ki ima natančnejše podatke o naročilu, ki jih lahko vidi kupec. V firestoru so shranjena v collectionu Uporabniki/{$id_uporabnika}/Narocila.
 * Podeduje vse metode in spremenljivke od razreda SkupnoNarocilo.
 * String ime_kmetije
 * Map<String, Object> narocilo_cene ... npr: {"Mleko": 3.5}
 * Map<String, Object> narocilo_enote ... npr: {"Mleko": liter}
 * Map<String, Object> narocilo_kolicine .. npr: {"Mleko": 2}
 */
public class ZaKupca extends SkupnoNarocilo {

    String ime_kmetije;
    Map<String, Object> narocilo_cene;
    Map<String, Object> narocilo_enote;
    Map<String, Object> narocilo_kolicine;

    public ZaKupca() {
        this.ime_kmetije = "";
        this.narocilo_cene = new HashMap<>();
        this.narocilo_enote = new HashMap<>();
        this.narocilo_kolicine = new HashMap<>();
    }

    public String getIme_kmetije() {
        return ime_kmetije;
    }

    public void setIme_kmetije(String ime_kmetije) {
        this.ime_kmetije = ime_kmetije;
    }

    public Map<String, Object> getNarocilo_cene() {
        return narocilo_cene;
    }

    public void setNarocilo_cene(Map<String, Object> narocilo_cene) {
        this.narocilo_cene = narocilo_cene;
    }

    public Map<String, Object> getNarocilo_enote() {
        return narocilo_enote;
    }

    public void setNarocilo_enote(Map<String, Object> narocilo_enote) {
        this.narocilo_enote = narocilo_enote;
    }

    public Map<String, Object> getNarocilo_kolicine() {
        return narocilo_kolicine;
    }

    public void setNarocilo_kolicine(Map<String, Object> narocilo_kolicine) {
        this.narocilo_kolicine = narocilo_kolicine;
    }

}
