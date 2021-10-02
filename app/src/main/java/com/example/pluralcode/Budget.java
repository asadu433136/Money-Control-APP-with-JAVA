package com.example.pluralcode;

public class Budget {
    private String amount;
    private String category;
    private String progress;

    public Budget() {
    }

    public Budget(String amount, String category, String progress) {
        this.amount = amount;
        this.category = category;
        this.progress = progress;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }
}
