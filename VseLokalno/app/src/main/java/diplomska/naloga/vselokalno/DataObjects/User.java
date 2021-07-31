package diplomska.naloga.vselokalno.DataObjects;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import diplomska.naloga.vselokalno.DataObjects.Narocilo.SkupnoNarocilo;

import static diplomska.naloga.vselokalno.SignInUp.SignInUpActivity.userData;

/**
 * public String email
 * public String ime_uporabnika
 * public String priimek_uporabnika
 * public boolean lastnik_kmetije ... {true == kmetovalec; false == kupec}
 * public ArrayList<Narocilo> narocila ... vsebuje objekt Narocilo ki je podrazred tega razreda
 * private String password
 */
public class User {

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String email;
    public String ime_uporabnika;
    public String priimek_uporabnika;
    public boolean lastnik_kmetije;
    public ArrayList<Narocilo> narocila;
    private String password;
    boolean use_default_pic;

    public User() {
        this.email = "";
        this.ime_uporabnika = "";
        this.priimek_uporabnika = "";
        this.password = "";
        this.lastnik_kmetije = false;
        this.narocila = new ArrayList<>();
        this.use_default_pic = true;
    }

    public boolean isUse_default_pic() {
        return use_default_pic;
    }

    public void setUse_default_pic(boolean use_default_pic) {
        this.use_default_pic = use_default_pic;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIme_uporabnika() {
        return ime_uporabnika;
    }

    public void setIme_uporabnika(String ime_uporabnika) {
        this.ime_uporabnika = ime_uporabnika;
    }

    public String getPriimek_uporabnika() {
        return priimek_uporabnika;
    }

    public void setPriimek_uporabnika(String priimek_uporabnika) {
        this.priimek_uporabnika = priimek_uporabnika;
    }

    public boolean isLastnik_kmetije() {
        return lastnik_kmetije;
    }

    public void setLastnik_kmetije(boolean lastnik_kmetije) {
        this.lastnik_kmetije = lastnik_kmetije;
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

    /**
     * Narocilo je objekt, ki ima osnovne podatke o naročilu, ki jih lahko vidi kupec. V firestoru so shranjena v dokumentu Uporabniki/{$id_uporabnika}
     * Podeduje vse metode in spremenljivke od razreda SkupnoNarocilo.
     * public String ime_kmetije
     */
    public static class Narocilo extends SkupnoNarocilo {
        public String ime_kmetije;

        public Narocilo() {
            this.ime_kmetije = "";
        }

        public String getIme_kmetije() {
            return ime_kmetije;
        }

        public void setIme_kmetije(String ime_kmetije) {
            this.ime_kmetije = ime_kmetije;
        }
    }
}
