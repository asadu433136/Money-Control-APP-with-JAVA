package com.example.pluralcode;

public class Transaction {
    private int amount;
    private String category;
    private String date;
    private String description;
    private String payment_type;
    private String time;
    private String type, comment, currency, contact,repeat;

    public Transaction() {
    }

    public Transaction(int amount, String category, String date, String description, String payment_type, String time, String type, String comment, String currency, String contact, String repeat) {
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
        this.payment_type = payment_type;
        this.time = time;
        this.type = type;
        this.comment= comment;
        this.currency=currency;
        this.contact = contact;
        this.repeat=repeat;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment= comment;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }
}
