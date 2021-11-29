package diplomska.naloga.vselokalno.DataObjects.Narocilo;

import java.util.HashMap;
import java.util.Map;

/**
 * ZaKupca je objekt, ki ima natančnejše podatke o naročilu, ki jih lahko vidi kupec. V firestoru so shranjena v collectionu Uporabniki/{$id_uporabnika}/Narocila.
 * Podeduje vse metode in spremenljivke od razreda SkupnoNarocilo.
 * String ime_kmetije
 * String id_kmetije
 */
public class ZaKupca extends SkupnoNarocilo {

    String ime_kmetije;
    String id_kmetije;

    public String getId_kmetije() {
        return id_kmetije;
    }

    public void setId_kmetije(String id_kmetije) {
        this.id_kmetije = id_kmetije;
    }

    public ZaKupca() {
        this.ime_kmetije = "";
        this.id_kmetije = "";
    }

    public String getIme_kmetije() {
        return ime_kmetije;
    }

    public void setIme_kmetije(String ime_kmetije) {
        this.ime_kmetije = ime_kmetije;
    }

    public String getPovezavo(String idKupca) {
        return idKupca + "#" + id_kmetije + "#" + this.getDatum_narocila();
    }
}
