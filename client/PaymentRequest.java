package sk.wuestenrot.paybysquare.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.*;
import java.util.List;

/**
 * PayBySquare platobná požiadavka.
 * Obsahuje všetky údaje potrebné pre generovanie PayBySquare QR kódu.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentRequest {

    // Základné platobné údaje
    @NotNull(message = "Amount is mandatory")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    private Double amount;

    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be a valid ISO 4217 code")
    private String currency = "EUR";

    @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z0-9]+$", message = "Invalid IBAN format")
    @Size(max = 34, message = "IBAN must not exceed 34 characters")
    private String iban;

    @Pattern(regexp = "^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$", message = "Invalid SWIFT/BIC format")
    @Size(max = 11, message = "SWIFT must not exceed 11 characters")
    private String swift;

    // Identifikácia
    @Size(max = 10, message = "Invoice ID must not exceed 10 characters")
    private String invoiceId;

    // Dátumy (format YYYY-MM-DD)
    private String date;
    private String paymentDueDate;

    // Platobné symboly
    @Pattern(regexp = "^[0-9]*$", message = "Variable symbol must contain only digits")
    @Size(max = 10, message = "Variable symbol must not exceed 10 characters")
    private String variableSymbol;

    @Pattern(regexp = "^[0-9]*$", message = "Constant symbol must contain only digits")
    @Size(max = 4, message = "Constant symbol must not exceed 4 characters")
    private String constantSymbol;

    @Pattern(regexp = "^[0-9]*$", message = "Specific symbol must contain only digits")
    @Size(max = 10, message = "Specific symbol must not exceed 10 characters")
    private String specificSymbol;

    @Size(max = 35, message = "Originators reference must not exceed 35 characters")
    private String originatorsReferenceInformation;

    @Size(max = 140, message = "Note must not exceed 140 characters")
    private String note;

    // Údaje o príjemcovi
    @Size(max = 70, message = "Beneficiary name must not exceed 70 characters")
    private String beneficiaryName;

    @Size(max = 70, message = "Beneficiary address 1 must not exceed 70 characters")
    private String beneficiaryAddress1;

    @Size(max = 70, message = "Beneficiary address 2 must not exceed 70 characters")
    private String beneficiaryAddress2;

    // Typy platby
    private List<String> paymentOptions;

    // Viacero bankových účtov
    @Size(max = 6, message = "Maximum 6 bank accounts allowed")
    private List<BankAccount> bankAccounts;

    // Trvalý príkaz
    private StandingOrder standingOrder;

    // Inkaso
    private DirectDebit directDebit;

    // QR nastavenia
    private Boolean withFrame = true;

    @Min(value = 100, message = "QR size must be at least 100")
    @Max(value = 1000, message = "QR size must not exceed 1000")
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class BankAccount {
        @NotNull(message = "IBAN is mandatory")
        @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z0-9]+$", message = "Invalid IBAN format")
        @Size(max = 34, message = "IBAN must not exceed 34 characters")
        private String iban;

        @Pattern(regexp = "^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$", message = "Invalid SWIFT/BIC format")
        @Size(max = 11, message = "SWIFT must not exceed 11 characters")
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class StandingOrder {
        @Min(value = 1, message = "Day must be at least 1")
        @Max(value = 31, message = "Day must not exceed 31")
        private Integer day;

        private List<@Min(1) @Max(12) Integer> month;

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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class DirectDebit {
        private String scheme = "other"; // sepa, other

        private String type; // oneoff, one-off, recurrent

        @Pattern(regexp = "^[0-9]*$", message = "Variable symbol must contain only digits")
        @Size(max = 10, message = "Variable symbol must not exceed 10 characters")
        private String variableSymbol;

        @Pattern(regexp = "^[0-9]*$", message = "Specific symbol must contain only digits")
        @Size(max = 10, message = "Specific symbol must not exceed 10 characters")
        private String specificSymbol;

        @Size(max = 35, message = "Originators reference must not exceed 35 characters")
        private String originatorsReferenceInformation;

        @Size(max = 35, message = "Mandate ID must not exceed 35 characters")
        private String mandateId;

        @Size(max = 35, message = "Creditor ID must not exceed 35 characters")
        private String creditorId;

        @Size(max = 35, message = "Contract ID must not exceed 35 characters")
        private String contractId;

        @DecimalMin(value = "0.01", message = "Max amount must be at least 0.01")
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
