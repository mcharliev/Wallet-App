package ru.zenclass.ylab.model.dto;

import java.util.List;

public class TransactionHistoryDTO {

    private String message;
    private List<TransactionDTO> transactionDTOList;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<TransactionDTO> getTransactionDTOList() {
        return transactionDTOList;
    }

    public void setTransactionDTOList(List<TransactionDTO> transactionDTOList) {
        this.transactionDTOList = transactionDTOList;
    }
}
