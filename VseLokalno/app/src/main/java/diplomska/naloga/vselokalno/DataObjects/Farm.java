package diplomska.naloga.vselokalno.DataObjects;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Farm ima isti ID kot uporanik, ki je lastnil kmetije.
 * public String ime_kmetije
 * public String naslov_kmetije
 * public Map<String, ArrayList<Boolean> cas_prevzema ... npr: {"pon": ["10:00-11:00", "11:00-12:00"]}
 * public Map<String, String> koordinate_kmetije ... npr: {"lat": 45.65154123, "lan": 22.85636531}
 * public ArrayList<Map<String, String>> artikli
 */
public class Farm {

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String ime_kmetije;
    public String naslov_kmetije;
    public Map<String, ArrayList<Boolean>> cas_prevzema;
    public Map<String, String> koordinate_kmetije;
    boolean use_default_pic;

    public Farm() {
        this.ime_kmetije = "";
        this.naslov_kmetije = "";
        this.cas_prevzema = new HashMap<>();
        this.koordinate_kmetije = new HashMap<>();
        this.use_default_pic = true;
    }

    public boolean isUse_default_pic() {
        return use_default_pic;
    }

    public void setUse_default_pic(boolean use_default_pic) {
        this.use_default_pic = use_default_pic;
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
}
