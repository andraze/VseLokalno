package diplomska.naloga.vselokalno.DataObjects;

/**
 * String article_id;
 * String article_name;
 * double article_price;
 * String article_unit;
 * double article_buying_amount;
 * double article_storage;
 * String farm_id;
 * String category_id;
 * boolean picture;
 */

public class Article {
    String article_id;
    String article_name;
    double article_price;
    String article_unit;
    double article_storage;
    double article_buying_amount;
    String farm_id;
    String category_id;
    boolean picture;

    public Article() {
        this.article_id = "";
        this.article_name = "";
        this.article_price = 0;
        this.article_unit = "";
        this.article_buying_amount = 0;
        this.article_storage = 0;
        this.farm_id = "";
        this.category_id = "";
        this.picture = false;
    }

    public Article(Article article) {
        this.article_id = article.article_id;
        this.article_name = article.article_name;
        this.article_price = article.article_price;
        this.article_unit = article.article_unit;
        this.article_storage = article.article_storage;
        this.article_buying_amount = article.article_buying_amount;
        this.farm_id = article.farm_id;
        this.category_id = article.category_id;
        this.picture = article.picture;
    }

    public Article makeCopy() {
        return new Article(this);
    }

    public String getFarm_id() {
        return farm_id;
    }

    public void setFarm_id(String farm_id) {
        this.farm_id = farm_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getArticle_id() {
        return article_id;
    }

    public void setArticle_id(String article_id) {
        this.article_id = article_id;
    }

    public String getArticle_name() {
        return article_name;
    }

    public void setArticle_name(String article_name) {
        this.article_name = article_name;
    }

    public double getArticle_price() {
        return article_price;
    }

    public void setArticle_price(double article_price) {
        this.article_price = article_price;
    }

    public String getArticle_unit() {
        return article_unit;
    }

    public void setArticle_unit(String article_unit) {
        this.article_unit = article_unit;
    }

    public double getArticle_buying_amount() {
        return article_buying_amount;
    }

    public void setArticle_buying_amount(double article_buying_amount) {
        this.article_buying_amount = article_buying_amount;
    }

    public double getArticle_storage() {
        return article_storage;
    }

    public void setArticle_storage(double article_storage) {
        this.article_storage = article_storage;
    }

    public boolean isPicture() {
        return picture;
    }

    public void setPicture(boolean picture) {
        this.picture = picture;
    }
}
