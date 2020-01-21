package org.sysRestaurante.dao;

public class ProductDao {
    private int idProduct;
    private int idCategory;
    private int quantity;
    private long barCode;
    private double sellPrice;
    private double buyPrice;
    private double total;
    private String description;

    public ProductDao() {
        quantity = 1;
        total = 0;
    }

    public void setTotal(double price) {
        this.total = price * quantity;
    }

    public double getTotal() {
        return total;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getBarCode() {
        return barCode;
    }

    public void setBarCode(long barCode) {
        this.barCode = barCode;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void incrementsQuantity() {
        this.quantity += 1;
    }
}