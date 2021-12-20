package diplomska.naloga.vselokalno.DataObjects;

import java.util.ArrayList;
import java.util.Date;

/**
 * String id_kmetije;
 * String ime_kmetije;
 * String id_kupca;
 * String ime_priimek_kupca;
 * Naročilo je objekt, ki ima osnovne podatke o narocilu.
 * DATE FORMAT: "E dd-MM-yyyy HH:mm"
 * String datum_prevzema
 * String datum_narocila
 * String naslov_prevzema
 * int opravljeno {0 == v procesu (white); 1 == warning (yellow /npr → če se bliža dan prevzema in ni sprejet s strani kmetije/);
 *                 2 == uspešno (green); 3 == neuspešno (red)
 *                 (blue border == pickup today?)}
 * ArrayList<Article> ordered_articles;
 * Narocili sta povezani (za kmetijo={Kmetije/ID_kmetije/Naročila/ID_kmetije#ID_kupca_#datum_narocila} za kupca={Uporabniki/ID_uporabnika/Naročila/ID_kupca#ID_kmetije#datum_narocila}
 */

public class Order {
    public static final String DATE_FORMAT = "E dd-MM-yyyy HH:mm";
    String id_order;
    String id_kmetije;
    String ime_kmetije;
    String id_kupca;
    String ime_priimek_kupca;
    Date datum_prevzema;
    Date datum_narocila;
//    String datum_prevzema;
//    String datum_narocila;
    int opravljeno;
    ArrayList<Article> ordered_articles;

    public Order() {
        this.id_order = "";
        this.id_kmetije = "";
        this.ime_kmetije = "";
        this.id_kupca = "";
        this.ime_priimek_kupca = "";
        this.datum_narocila = null;
        this.datum_prevzema = null;
//        this.datum_prevzema = "";
//        this.datum_narocila = "";
        this.opravljeno = -1;
        this.ordered_articles = new ArrayList<>();
    }

    public String getId_order() {
        return id_order;
    }

    public void setId_order(String id_order) {
        this.id_order = id_order;
    }

    public ArrayList<Article> getOrdered_articles() {
        return ordered_articles;
    }

    public void addOrdered_articles(Article article) {
        this.ordered_articles.add(article);
    }

    public void removeOrdered_articles(Article article) {
        this.ordered_articles.remove(article);
    }

    public void removeOrdered_articles(int index) {
        this.ordered_articles.remove(index);
    }

    public void editOrdered_articles(int index, Article newArticle) {
        this.ordered_articles.set(index, newArticle);
    }

    public void setOrdered_articles(ArrayList<Article> ordered_articles) {
        this.ordered_articles = ordered_articles;
    }

    public String getId_kmetije() {
        return id_kmetije;
    }

    public void setId_kmetije(String id_kmetije) {
        this.id_kmetije = id_kmetije;
    }

    public String getIme_kmetije() {
        return ime_kmetije;
    }

    public void setIme_kmetije(String ime_kmetije) {
        this.ime_kmetije = ime_kmetije;
    }

    public String getId_kupca() {
        return id_kupca;
    }

    public void setId_kupca(String id_kupca) {
        this.id_kupca = id_kupca;
    }

    public String getIme_priimek_kupca() {
        return ime_priimek_kupca;
    }

    public void setIme_priimek_kupca(String ime_priimek_kupca) {
        this.ime_priimek_kupca = ime_priimek_kupca;
    }

    public Date getDatum_prevzema() {
        return datum_prevzema;
    }

    public void setDatum_prevzema(Date datum_prevzema) {
        this.datum_prevzema = datum_prevzema;
    }

    public Date getDatum_narocila() {
        return datum_narocila;
    }

    public void setDatum_narocila(Date datum_narocila) {
        this.datum_narocila = datum_narocila;
    }
//    public String getDatum_prevzema() {
//        return datum_prevzema;
//    }
//
//    public void setDatum_prevzema(String datum_prevzema) {
//        this.datum_prevzema = datum_prevzema;
//    }
//
//    public String getDatum_narocila() {
//        return datum_narocila;
//    }
//
//    public void setDatum_narocila(String datum_narocila) {
//        this.datum_narocila = datum_narocila;
//    }

    public int getOpravljeno() {
        return opravljeno;
    }

    public void setOpravljeno(int opravljeno) {
        this.opravljeno = opravljeno;
    }

}
