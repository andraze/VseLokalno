package diplomska.naloga.vselokalno.DataObjects;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import diplomska.naloga.vselokalno.DataObjects.Narocilo.SkupnoNarocilo;
import diplomska.naloga.vselokalno.DataObjects.Narocilo.ZaKmetijo;
import diplomska.naloga.vselokalno.DataObjects.Narocilo.ZaKupca;

import static diplomska.naloga.vselokalno.SignInUp.SignInUpActivity.userData;

/**
 * public String email
 * public String ime_uporabnika
 * public String priimek_uporabnika
 * public boolean lastnik_kmetije ... {true == kmetovalec; false == kupec}
 * public ArrayList<ZaKupca> aktivnaNaročila ... TODO: Vsebuje vsa aktivna naročila
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
    public ArrayList<ZaKupca> aktivnaNarocila;
    private String password;
    boolean use_default_pic;

    public User() {
        this.email = "";
        this.ime_uporabnika = "";
        this.priimek_uporabnika = "";
        this.password = "";
        this.lastnik_kmetije = false;
        this.aktivnaNarocila = new ArrayList<>();
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

    public ArrayList<ZaKupca> getAktivnaNarocila() {
        return aktivnaNarocila;
    }

    public void setAktivnaNarocila(ArrayList<ZaKupca> narocila) {
        this.aktivnaNarocila = narocila;
    }

    public void addAktivnaNarocila(ZaKupca narocilo) {
        this.aktivnaNarocila.add(narocilo);
    }

    public void removeAktivnaNarocila(ZaKupca narocilo) {
        this.aktivnaNarocila.remove(narocilo);
    }
}
