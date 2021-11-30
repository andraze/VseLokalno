package diplomska.naloga.vselokalno.DataObjects;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import diplomska.naloga.vselokalno.DataObjects.Narocilo.SkupnoNarocilo;
import diplomska.naloga.vselokalno.DataObjects.Narocilo.ZaKmetijo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Kmetija ima isti ID kot uporanik, ki je lastnil kmetije.
 * public String ime_kmetije
 * public String naslov_kmetije
 * public String naslov_dostave
 * public String opis_kmetije
 * public Map<String, ArrayList<Boolean> cas_prevzema ... npr: {"pon": ["10:00-11:00", "11:00-12:00"]}
 * public Map<String, String> koordinate_kmetije ... npr: {"lat": 45.65154123, "lan": 22.85636531}
 * public ArrayList<ZaKmetijo> narocila ... TODO: vsebuje aktivna naročila.
 * public ArrayList<Map<String, String>> artikli
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
    public Map<String, ArrayList<Boolean>> cas_prevzema;
    public Map<String, String> koordinate_kmetije;
    public ArrayList<ZaKmetijo> aktivnaNarocila;
    public ArrayList<Map<String, String>> artikli;

    public Kmetija() {
        this.ime_kmetije = "";
        this.naslov_kmetije = "";
        this.naslov_dostave = "";
        this.opis_kmetije = "";
        this.cas_prevzema = new HashMap<>();
        this.koordinate_kmetije = new HashMap<>();
        this.aktivnaNarocila = new ArrayList<>();
        this.artikli = new ArrayList<>();
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

    public Map<String, ArrayList<Boolean>> getCas_prevzema() {
        return cas_prevzema;
    }

    public void setCas_prevzema(Map<String, ArrayList<Boolean>> cas_prevzema) {
        this.cas_prevzema = cas_prevzema;
    }

    public Map<String, String> getKoordinate_kmetije() {
        return koordinate_kmetije;
    }

    public void setKoordinate_kmetije(Map<String, String> koordinate_kmetije) {
        this.koordinate_kmetije = koordinate_kmetije;
    }

    public ArrayList<ZaKmetijo> getAktivnaNarocila() {
        return aktivnaNarocila;
    }

    public void setAktivnaNarocila(ArrayList<ZaKmetijo> narocila) {
        this.aktivnaNarocila = narocila;
    }

    public void addAktivnaNarocila(ZaKmetijo narocilo) {
        this.aktivnaNarocila.add(narocilo);
    }

    public void removeAktivnaNarocila(ZaKmetijo narocilo) {
        this.aktivnaNarocila.remove(narocilo);
    }

    public ArrayList<Map<String, String>> getArtikli() {
        return artikli;
    }

    public void setArtikli(ArrayList<Map<String, String>> artikli) {
        this.artikli = artikli;
    }

    public void addArtikel(Map<String, String> artikel) {
        this.artikli.add(artikel);
    }

    public void removeArtikel(int position) {
        this.artikli.remove(position);
    }
}
