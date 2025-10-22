package sk.wuestenrot.paybysquare.client;

import java.util.List;

/**
 * PayBySquare platobná požiadavka.
 * Obsahuje všetky údaje potrebné pre generovanie PayBySquare QR kódu.
 */
public class PaymentRequest {

    // Základné platobné údaje
    private Double amount;
    private String currency = "EUR";
    private String iban;
    private String swift;

    // Identifikácia
    private String invoiceId;

    // Dátumy (format YYYY-MM-DD)
    private String date;
    private String paymentDueDate;

    // Platobné symboly
    private String variableSymbol;
    private String constantSymbol;
    private String specificSymbol;
    private String originatorsReferenceInformation;
    private String note;

    // Údaje o príjemcovi
    private String beneficiaryName;
    private String beneficiaryAddress1;
    private String beneficiaryAddress2;

    // Typy platby
    private List<String> paymentOptions;

    // Viacero bankových účtov
    private List<BankAccount> bankAccounts;

    // Trvalý príkaz
    private StandingOrder standingOrder;

    // Inkaso
    private DirectDebit directDebit;

    // QR nastavenia
    private Boolean withFrame = true;
    private Integer qrSize = 300;

    // Konstruktory
    public PaymentRequest() {
    }

    public PaymentRequest(Double amount, String iban, String beneficiaryName) {
        this.amount = amount;
        this.iban = iban;
        this.beneficiaryName = beneficiaryName;
    }

    // Gettery a Settery
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public String getSwift() {
        return swift;
    }

    public void setSwift(String swift) {
        this.swift = swift;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPaymentDueDate() {
        return paymentDueDate;
    }

    public void setPaymentDueDate(String paymentDueDate) {
        this.paymentDueDate = paymentDueDate;
    }

    public String getVariableSymbol() {
        return variableSymbol;
    }

    public void setVariableSymbol(String variableSymbol) {
        this.variableSymbol = variableSymbol;
    }

    public String getConstantSymbol() {
        return constantSymbol;
    }

    public void setConstantSymbol(String constantSymbol) {
        this.constantSymbol = constantSymbol;
    }

    public String getSpecificSymbol() {
        return specificSymbol;
    }

    public void setSpecificSymbol(String specificSymbol) {
        this.specificSymbol = specificSymbol;
    }

    public String getOriginatorsReferenceInformation() {
        return originatorsReferenceInformation;
    }

    public void setOriginatorsReferenceInformation(String originatorsReferenceInformation) {
        this.originatorsReferenceInformation = originatorsReferenceInformation;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        this.beneficiaryName = beneficiaryName;
    }

    public String getBeneficiaryAddress1() {
        return beneficiaryAddress1;
    }

    public void setBeneficiaryAddress1(String beneficiaryAddress1) {
        this.beneficiaryAddress1 = beneficiaryAddress1;
    }

    public String getBeneficiaryAddress2() {
        return beneficiaryAddress2;
    }

    public void setBeneficiaryAddress2(String beneficiaryAddress2) {
        this.beneficiaryAddress2 = beneficiaryAddress2;
    }

    public List<String> getPaymentOptions() {
        return paymentOptions;
    }

    public void setPaymentOptions(List<String> paymentOptions) {
        this.paymentOptions = paymentOptions;
    }

    public List<BankAccount> getBankAccounts() {
        return bankAccounts;
    }

    public void setBankAccounts(List<BankAccount> bankAccounts) {
        this.bankAccounts = bankAccounts;
    }

    public StandingOrder getStandingOrder() {
        return standingOrder;
    }

    public void setStandingOrder(StandingOrder standingOrder) {
        this.standingOrder = standingOrder;
    }

    public DirectDebit getDirectDebit() {
        return directDebit;
    }

    public void setDirectDebit(DirectDebit directDebit) {
        this.directDebit = directDebit;
    }

    public Boolean getWithFrame() {
        return withFrame;
    }

    public void setWithFrame(Boolean withFrame) {
        this.withFrame = withFrame;
    }

    public Integer getQrSize() {
        return qrSize;
    }

    public void setQrSize(Integer qrSize) {
        this.qrSize = qrSize;
    }

    @Override
    public String toString() {
        return "PaymentRequest{" +
                "amount=" + amount +
                ", currency='" + currency + '\'' +
                ", iban='" + iban + '\'' +
                ", beneficiaryName='" + beneficiaryName + '\'' +
                ", variableSymbol='" + variableSymbol + '\'' +
                '}';
    }

    /**
     * Bankový účet príjemcu.
     */
    public static class BankAccount {
        private String iban;
        private String swift;

        public BankAccount() {
        }

        public BankAccount(String iban) {
            this.iban = iban;
        }

        public BankAccount(String iban, String swift) {
            this.iban = iban;
            this.swift = swift;
        }

        public String getIban() {
            return iban;
        }

        public void setIban(String iban) {
            this.iban = iban;
        }

        public String getSwift() {
            return swift;
        }

        public void setSwift(String swift) {
            this.swift = swift;
        }

        @Override
        public String toString() {
            return "BankAccount{" +
                    "iban='" + iban + '\'' +
                    ", swift='" + swift + '\'' +
                    '}';
        }
    }

    /**
     * Nastavenia pre trvalý príkaz.
     */
    public static class StandingOrder {
        private Integer day;
        private List<Integer> month;
        private String periodicity; // d, w, b, m, B, q, s, a
        private String lastDate; // format YYYY-MM-DD

        public StandingOrder() {
        }

        public Integer getDay() {
            return day;
        }

        public void setDay(Integer day) {
            this.day = day;
        }

        public List<Integer> getMonth() {
            return month;
        }

        public void setMonth(List<Integer> month) {
            this.month = month;
        }

        public String getPeriodicity() {
            return periodicity;
        }

        public void setPeriodicity(String periodicity) {
            this.periodicity = periodicity;
        }

        public String getLastDate() {
            return lastDate;
        }

        public void setLastDate(String lastDate) {
            this.lastDate = lastDate;
        }

        @Override
        public String toString() {
            return "StandingOrder{" +
                    "day=" + day +
                    ", month=" + month +
                    ", periodicity='" + periodicity + '\'' +
                    ", lastDate='" + lastDate + '\'' +
                    '}';
        }
    }

    /**
     * Nastavenia pre inkaso (SEPA direct debit).
     */
    public static class DirectDebit {
        private String scheme = "other"; // sepa, other
        private String type; // oneoff, one-off, recurrent
        private String variableSymbol;
        private String specificSymbol;
        private String originatorsReferenceInformation;
        private String mandateId;
        private String creditorId;
        private String contractId;
        private Double maxAmount;
        private String validTillDate; // format YYYY-MM-DD

        public DirectDebit() {
        }

        public String getScheme() {
            return scheme;
        }

        public void setScheme(String scheme) {
            this.scheme = scheme;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getVariableSymbol() {
            return variableSymbol;
        }

        public void setVariableSymbol(String variableSymbol) {
            this.variableSymbol = variableSymbol;
        }

        public String getSpecificSymbol() {
            return specificSymbol;
        }

        public void setSpecificSymbol(String specificSymbol) {
            this.specificSymbol = specificSymbol;
        }

        public String getOriginatorsReferenceInformation() {
            return originatorsReferenceInformation;
        }

        public void setOriginatorsReferenceInformation(String originatorsReferenceInformation) {
            this.originatorsReferenceInformation = originatorsReferenceInformation;
        }

        public String getMandateId() {
            return mandateId;
        }

        public void setMandateId(String mandateId) {
            this.mandateId = mandateId;
        }

        public String getCreditorId() {
            return creditorId;
        }

        public void setCreditorId(String creditorId) {
            this.creditorId = creditorId;
        }

        public String getContractId() {
            return contractId;
        }

        public void setContractId(String contractId) {
            this.contractId = contractId;
        }

        public Double getMaxAmount() {
            return maxAmount;
        }

        public void setMaxAmount(Double maxAmount) {
            this.maxAmount = maxAmount;
        }

        public String getValidTillDate() {
            return validTillDate;
        }

        public void setValidTillDate(String validTillDate) {
            this.validTillDate = validTillDate;
        }

        @Override
        public String toString() {
            return "DirectDebit{" +
                    "scheme='" + scheme + '\'' +
                    ", type='" + type + '\'' +
                    ", mandateId='" + mandateId + '\'' +
                    ", creditorId='" + creditorId + '\'' +
                    ", maxAmount=" + maxAmount +
                    ", validTillDate='" + validTillDate + '\'' +
                    '}';
        }
    }
}
