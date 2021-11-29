package diplomska.naloga.vselokalno.DataObjects.Narocilo;

import java.util.HashMap;
import java.util.Map;

/**
 * ZaKmetijo je objekt, ki ima natančnejše podatke o naročilu, ki jih lahko vidi lastnik kmetije. V firestoru so shranjena v collectionu Kmetije/{$id_kmetije}/Narocila.
 * Podeduje vse metode in spremenljivke od razreda SkupnoNarocilo.
 * String ime_narocnika
 * String id_narocnika
 */
public class ZaKmetijo extends SkupnoNarocilo {

    String ime_narocnika;
    String id_narocnika;

    public String getId_narocnika() {
        return id_narocnika;
    }

    public void setId_narocnika(String id_narocnika) {
        this.id_narocnika = id_narocnika;
    }

    public ZaKmetijo() {
        this.ime_narocnika = "";
        this.id_narocnika = "";
    }

    public String getIme_narocnika() {
        return ime_narocnika;
    }

    public void setIme_narocnika(String ime_narocnika) {
        this.ime_narocnika = ime_narocnika;
    }

    public String getPovezavo(String idKmetije) {
        return idKmetije + "#" + id_narocnika + "#" + this.getDatum_narocila();
    }
}
