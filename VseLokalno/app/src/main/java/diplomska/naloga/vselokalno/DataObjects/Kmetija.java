package diplomska.naloga.vselokalno.DataObjects;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import diplomska.naloga.vselokalno.DataObjects.Narocilo.SkupnoNarocilo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * public String ime_kmetije
 * public String naslov_kmetije
 * public String naslov_dostave
 * public String opis_kmetije
 * public String lastnik ... email uporabnika, ki je lastnik kmetije.
 * public Map<String, Object> cas_dostave ... npr: {"pom": ["10:00-11:00", "11:00-12:00"]}
 * public Map<String, Object> koordinate_kmetije ... npr: {"lat": 45.65154123, "lan": 22.85636531}
 * public ArrayList<Narocilo> narocila ... vsebuje objekt Narocilo ki je podrazred tega razreda
 * public Map<String, Object> ponudbe_cene ... npr: {"Mleko": 3.5}
 * public Map<String, Object> ponudbe_enote ... npr: {"Mleko": liter}
 * public Map<String, Object> ponudbe_zaloge .. npr: {"Mleko": 2}
 */
public class Kmetija {

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String ime_kmetije;
    public String naslov_kmetije;
    public String naslov_dostave;
    public String opis_kmetije;
    public String lastnik;
    public Map<String, Object> cas_dostave;
    public Map<String, Object> koordinate_kmetije;
    public ArrayList<Narocilo> narocila;
    public Map<String, Object> ponudbe_cene;
    public Map<String, Object> ponudbe_enote;
    public Map<String, Object> ponudbe_zaloge;

    public Kmetija() {
        this.ime_kmetije = "";
        this.naslov_kmetije = "";
        this.naslov_dostave = "";
        this.opis_kmetije = "";
        this.lastnik = "";
        this.cas_dostave = new HashMap<>();
        this.koordinate_kmetije = new HashMap<>();
        this.narocila = new ArrayList<>();
        this.ponudbe_cene = new HashMap<>();
        this.ponudbe_enote = new HashMap<>();
        this.ponudbe_zaloge = new HashMap<>();
    }

    public Kmetija(String ime_kmetije, String naslov_kmetije, String naslov_dostave, String opis_kmetije, Map<String, Object> cas_dostave, Map<String, Object> koordinate_kmetije, ArrayList<Narocilo> narocila, Map<String, Object> ponudbe_cene, Map<String, Object> ponudbe_enote, Map<String, Object> ponudbe_zaloge) {
        this.ime_kmetije = ime_kmetije;
        this.naslov_kmetije = naslov_kmetije;
        this.naslov_dostave = naslov_dostave;
        this.opis_kmetije = opis_kmetije;
        this.cas_dostave = cas_dostave;
        this.koordinate_kmetije = koordinate_kmetije;
        this.narocila = narocila;
        this.ponudbe_cene = ponudbe_cene;
        this.ponudbe_enote = ponudbe_enote;
        this.ponudbe_zaloge = ponudbe_zaloge;
    }

    public String getIme_kmetije() {
        return ime_kmetije;
    }

    public void setIme_kmetije(String ime_kmetije) {
        this.ime_kmetije = ime_kmetije;
    }

    public String getNaslov_kmetije() {
        return naslov_kmetije;
    }

    public void setNaslov_kmetije(String naslov_kmetije) {
        this.naslov_kmetije = naslov_kmetije;
    }

    public String getNaslov_dostave() {
        return naslov_dostave;
    }

    public void setNaslov_dostave(String naslov_dostave) {
        this.naslov_dostave = naslov_dostave;
    }

    public String getOpis_kmetije() {
        return opis_kmetije;
    }

    public void setOpis_kmetije(String opis_kmetije) {
        this.opis_kmetije = opis_kmetije;
    }

    public Map<String, Object> getCas_dostave() {
        return cas_dostave;
    }

    public void setCas_dostave(Map<String, Object> cas_dostave) {
        this.cas_dostave = cas_dostave;
    }

    public Map<String, Object> getKoordinate_kmetije() {
        return koordinate_kmetije;
    }

    public void setKoordinate_kmetije(Map<String, Object> koordinate_kmetije) {
        this.koordinate_kmetije = koordinate_kmetije;
    }

    public ArrayList<Narocilo> getNarocila() {
        return narocila;
    }

    public void setNarocila(ArrayList<Narocilo> narocila) {
        this.narocila = narocila;
    }

    public void addNarocila(Narocilo narocilo) {
        this.narocila.add(narocilo);
    }

    public Map<String, Object> getPonudbe_cene() {
        return ponudbe_cene;
    }

    public void setPonudbe_cene(Map<String, Object> ponudbe_cene) {
        this.ponudbe_cene = ponudbe_cene;
    }

    public void addPonudbe_cene(String izdelek, Object cena) {
        this.ponudbe_cene.put(izdelek, cena);
    }

    public void addponudbe_enote(String izdelek, Object enota) {
        this.ponudbe_enote.put(izdelek, enota);
    }

    public Map<String, Object> getPonudbe_zaloge() {
        return ponudbe_zaloge;
    }

    public void setPonudbe_zaloge(Map<String, Object> ponudbe_zaloge) {
        this.ponudbe_zaloge = ponudbe_zaloge;
    }

    public void addPonudbe_zaloge(String izdelek, Object zaloga) {
        this.ponudbe_zaloge.put(izdelek, zaloga);
    }

    public Map<String, Object> getPonudbe_enote() {
        return ponudbe_enote;
    }

    public void setPonudbe_enote(Map<String, Object> ponudbe_enote) {
        this.ponudbe_enote = ponudbe_enote;
    }

    public void addPonudbe_enote(String izdelek, Object enota) {
        this.ponudbe_enote.put(izdelek, enota);
    }

    public String getLastnik() {
        return lastnik;
    }

    public void setLastnik(String lastnik) {
        this.lastnik = lastnik;
    }

    /**
     * Narocilo je objekt, ki ima osnovne podatke o naročilu, ki jih lahko vidi lastnik kmetije. V firestoru so shranjena v dokumentu Kmetije/{$id_kmetije}
     * Podeduje vse metode in spremenljivke od razreda SkupnoNarocilo.
     * public String ime_narocnika
     */
    public static class Narocilo extends SkupnoNarocilo {
        public String ime_narocnika;
        public String uid_narocnika;

        public Narocilo() {
            this.ime_narocnika = "";
            this.uid_narocnika = "";
        }

        public String getUid_narocnika() {
            return uid_narocnika;
        }

        public void setUid_narocnika(String uid_narocnika) {
            this.uid_narocnika = uid_narocnika;
        }

        public String getIme_narocnika() {
            return ime_narocnika;
        }

        public void setIme_narocnika(String ime_narocnika) {
            this.ime_narocnika = ime_narocnika;
        }
    }

}
